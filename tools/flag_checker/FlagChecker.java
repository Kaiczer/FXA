import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FlagChecker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the path to the 'history/countries' directory:");
        String countriesDirPath = scanner.nextLine();

        System.out.println("Please enter the path to the 'gfx/flags' directory:");
        String flagsDirPath = scanner.nextLine();

        // Step 3: Keep the first three capital letters of every file in "history/countries" in an array (Country Tag)
        Set<String> countryTags = getCountryTags(countriesDirPath);

        // Step 4: Scan the "gfx/flags" folder and see what tags still don't have flags and which flags have no matching country tags
        Set<String> flagTags = getFlagTags(flagsDirPath);

        // Step 5: Print the report
        printReport(countryTags, flagTags);
    }

    private static Set<String> getCountryTags(String dirPath) {
        Set<String> countryTags = new HashSet<>();
        try {
            Files.walkFileTree(Paths.get(dirPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String fileName = file.getFileName().toString();
                    if (fileName.endsWith(".txt") && fileName.length() >= 3) {
                        String tag = fileName.substring(0, 3).toUpperCase();
                        countryTags.add(tag);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countryTags;
    }

    private static Set<String> getFlagTags(String dirPath) {
        Set<String> flagTags = new HashSet<>();
        try {
            Files.walkFileTree(Paths.get(dirPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String fileName = file.getFileName().toString();
                    if (fileName.endsWith(".tga") && fileName.length() >= 3) {
                        String tag = fileName.substring(0, 3).toUpperCase();
                        flagTags.add(tag);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flagTags;
    }

    private static void printReport(Set<String> countryTags, Set<String> flagTags) {
        Set<String> countriesWithoutFlags = new HashSet<>(countryTags);
        countriesWithoutFlags.removeAll(flagTags);

        Set<String> flagsWithoutCountries = new HashSet<>(flagTags);
        flagsWithoutCountries.removeAll(countryTags);

        try (FileWriter reportWriter = new FileWriter("Flag_Report.txt")) {
            reportWriter.write("=== Countries without Flags ===\n");
            for (String tag : countriesWithoutFlags) {
                reportWriter.write(tag + "\n");
            }

            reportWriter.write("\n=== Flags without Matching Countries ===\n");
            for (String tag : flagsWithoutCountries) {
                reportWriter.write(tag + "\n");
            }

            System.out.println("Report generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
