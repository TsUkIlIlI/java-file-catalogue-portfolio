import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

final class DownloadSession {
    private final Path managedDirectory;
    private int successfulCopies;

    DownloadSession(Path managedDirectory) throws IOException {
        this.managedDirectory = managedDirectory.toRealPath();
    }

    int successfulCopies() {
        return successfulCopies;
    }

    boolean run(Path source, BufferedReader input, PrintWriter output) throws IOException {
        while (true) {
            output.println("Destination path (blank to cancel):");
            String answer = input.readLine();
            if (answer == null || answer.isBlank()) {
                output.println("Download cancelled.");
                return false;
            }

            try {
                Path destination = Path.of(answer.strip()).toAbsolutePath();
                Path parent = destination.getParent();
                if (parent == null || !Files.isDirectory(parent)) {
                    output.println("Destination parent directory does not exist.");
                    continue;
                }

                // Resolve aliases before comparing the destination with the managed directory.
                Path realParent = parent.toRealPath();
                Path target = realParent.resolve(destination.getFileName()).normalize();
                if (realParent.startsWith(managedDirectory) || target.startsWith(managedDirectory)) {
                    output.println("Choose a destination outside the managed directory.");
                    continue;
                }

                // The copy operation itself rejects an existing target, including after a retry.
                Files.copy(source, target);
                successfulCopies++;
                output.println("Download complete: " + target);
                return true;
            } catch (InvalidPathException error) {
                output.println("Invalid destination path.");
            } catch (FileAlreadyExistsException error) {
                output.println("Destination already exists.");
            }
        }
    }
}
