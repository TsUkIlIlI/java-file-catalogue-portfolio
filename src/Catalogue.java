import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

final class Catalogue {
    static final int PREVIEW_LIMIT = 64;

    record Entry(String id, String uploader, String title, LocalDate date, Path file) {}

    record Query(String id, String uploader, String titleFragment, LocalDate date) {
        boolean matches(Entry entry) {
            return (isBlank(id) || entry.id().equals(id.strip()))
                    && (isBlank(uploader) || entry.uploader().equalsIgnoreCase(uploader.strip()))
                    && (isBlank(titleFragment) || entry.title().toLowerCase(Locale.ROOT)
                            .contains(titleFragment.strip().toLowerCase(Locale.ROOT)))
                    && (date == null || date.equals(entry.date()));
        }

        private static boolean isBlank(String value) {
            return value == null || value.isBlank();
        }
    }

    private final List<Entry> entries;

    Catalogue(List<Entry> entries) {
        this.entries = List.copyOf(entries);
    }

    List<Entry> search(Query query) {
        return entries.stream()
                .filter(query::matches)
                .sorted(Comparator.comparing(Entry::id))
                .toList();
    }

    static String preview(Entry entry) throws IOException {
        try (var input = Files.newInputStream(entry.file())) {
            return HexFormat.ofDelimiter(" ").formatHex(input.readNBytes(PREVIEW_LIMIT));
        }
    }
}
