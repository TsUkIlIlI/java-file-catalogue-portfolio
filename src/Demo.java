import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class Demo {
    static final Path EXAMPLES = Path.of("examples");
    static final LocalDate SAMPLE_DATE = LocalDate.of(2030, 2, 10);

    static Catalogue sampleCatalogue() {
        return new Catalogue(List.of(
                new Catalogue.Entry("F-03", "U-02", "Review Notes", SAMPLE_DATE,
                        EXAMPLES.resolve("review-notes.txt")),
                new Catalogue.Entry("F-02", "U-01", "Site Summary", SAMPLE_DATE.plusDays(1),
                        EXAMPLES.resolve("site-summary.txt")),
                new Catalogue.Entry("F-01", "U-01", "Field Notes", SAMPLE_DATE,
                        EXAMPLES.resolve("field-notes.txt"))));
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 1 || (args.length == 1 && !args[0].equals("--download"))) {
            System.err.println("Usage: java -cp out Demo [--download]");
            System.exit(2);
        }

        var query = new Catalogue.Query(null, "U-01", "notes", SAMPLE_DATE);
        var matches = sampleCatalogue().search(query);
        System.out.println("Filters: uploader U-01, title contains notes, date " + SAMPLE_DATE);
        System.out.println("Matches: " + matches.size());
        for (var entry : matches) {
            System.out.printf("%s  %s  %s%n", entry.id(), entry.title(), entry.date());
            System.out.println("Preview (first 64 bytes at most): " + Catalogue.preview(entry));
        }

        if (args.length == 1 && !matches.isEmpty()) {
            var session = new DownloadSession(EXAMPLES);
            var input = new BufferedReader(new InputStreamReader(System.in));
            session.run(matches.get(0).file(), input, new PrintWriter(System.out, true));
            System.out.println("Successful downloads: " + session.successfulCopies());
        }
    }
}
