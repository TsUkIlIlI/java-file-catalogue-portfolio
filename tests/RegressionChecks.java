import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

public class RegressionChecks {
    public static void main(String[] args) throws Exception {
        var catalogue = Demo.sampleCatalogue();
        var all = catalogue.search(new Catalogue.Query(" ", null, null, null));
        check("Blank filters return entries in ID order",
                all.stream().map(Catalogue.Entry::id).toList().equals(List.of("F-01", "F-02", "F-03")));

        var combined = catalogue.search(new Catalogue.Query("F-01", "u-01", "NOTES", Demo.SAMPLE_DATE));
        check("All active filters must match", combined.size() == 1 && combined.get(0).id().equals("F-01")
                && catalogue.search(new Catalogue.Query("F-01", "u-01", "NOTES",
                        Demo.SAMPLE_DATE.plusDays(1))).isEmpty());
        check("File ID requires an exact match",
                catalogue.search(new Catalogue.Query("F-0", null, null, null)).isEmpty());

        var hex = HexFormat.ofDelimiter(" ");
        byte[] longSample = Files.readAllBytes(all.get(0).file());
        check("Long previews contain exactly the first 64 bytes",
                longSample.length > 64 && hex.parseHex(Catalogue.preview(all.get(0))).length == 64
                && Arrays.equals(hex.parseHex(Catalogue.preview(all.get(0))), Arrays.copyOf(longSample, 64)));
        check("Short previews contain the whole file",
                Arrays.equals(hex.parseHex(Catalogue.preview(all.get(1))), Files.readAllBytes(all.get(1).file())));

        verifyDownloads(all.get(0).file());
    }

    private static void verifyDownloads(Path source) throws Exception {
        Path root = Files.createTempDirectory("catalogue-regression-");
        Path managed = root.resolve("managed");
        Path existing = root.resolve("existing.txt");
        Path protectedTarget = managed.resolve("protected.txt");
        Path copied = root.resolve("copied.txt");
        try {
            Files.createDirectory(managed);
            Files.writeString(existing, "Original content");
            var session = new DownloadSession(managed);
            var messages = new StringWriter();
            var output = new PrintWriter(messages);

            String invalidAttempts = existing + "\n" + root.resolve("missing/fail.txt")
                    + "\n" + protectedTarget + "\n";
            boolean cancelled = !session.run(source, input(invalidAttempts + "\n"), output);
            String log = messages.toString();
            check("Rejected destinations can be retried, and cancellation has no side effects",
                    cancelled && session.successfulCopies() == 0
                    && log.contains("already exists") && log.contains("parent directory")
                    && log.contains("outside the managed directory")
                    && Files.readString(existing).equals("Original content") && !Files.exists(protectedTarget));

            boolean completed = session.run(source, input(existing + "\n" + copied + "\n"), output);
            check("A valid retry copies exact bytes and increments the count once",
                    completed && session.successfulCopies() == 1 && Files.mismatch(source, copied) == -1
                    && Files.readString(existing).equals("Original content"));

            check("End of input cancels a download and preserves the previous count",
                    !session.run(source, input(""), output) && session.successfulCopies() == 1);
        } finally {
            Files.deleteIfExists(copied);
            Files.deleteIfExists(protectedTarget);
            Files.deleteIfExists(existing);
            Files.deleteIfExists(managed);
            Files.deleteIfExists(root);
        }
    }

    private static BufferedReader input(String text) {
        return new BufferedReader(new StringReader(text));
    }

    private static void check(String description, boolean passed) {
        if (!passed) {
            throw new AssertionError(description);
        }
        System.out.println("PASS  " + description);
    }
}
