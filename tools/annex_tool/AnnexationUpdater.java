import java.io.*;
import java.util.*;

public class AnnexationUpdater {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java AnnexationUpdater <modDir> <newGeneratedDir>");
            return;
        }

        String modDir = args[0];
        String newGeneratedDir = args[1];

        try {
            // Read existing "can_release_" lines from the specified directories
            Set<String> existingCanReleaseLines = readCanReleaseLinesFromDirectory(modDir + "/common/scripted_triggers", "Annexation triggers");

            // Read newly generated "can_release_" lines from the generated files
            Set<String> newCanReleaseLines = readCanReleaseLinesFromFile(newGeneratedDir + "/01_annexation_trigger_out.txt");

            // Write the comparison report
            try (FileWriter reportWriter = new FileWriter(newGeneratedDir + "/Annexation_Report.txt")) {
                compareCanReleaseLines(existingCanReleaseLines, newCanReleaseLines, reportWriter);
            }

            System.out.println("Report generated successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> readCanReleaseLinesFromDirectory(String dirPath, String filePrefix) throws IOException {
        Set<String> canReleaseLines = new HashSet<>();
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.getName().startsWith(filePrefix)) {
                    canReleaseLines.addAll(readCanReleaseLinesFromFile(file.getPath()));
                }
            }
        }
        return canReleaseLines;
    }

    private static Set<String> readCanReleaseLinesFromFile(String filePath) throws IOException {
        Set<String> canReleaseLines = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("can_release_")) {
                    canReleaseLines.add(line.trim());
                }
            }
        }
        return canReleaseLines;
    }

    private static void compareCanReleaseLines(Set<String> existingLines, Set<String> newLines, FileWriter reportWriter) throws IOException {
        List<String> bothLines = new ArrayList<>();
        List<String> onlyInModDir = new ArrayList<>();
        List<String> onlyInNewGenerated = new ArrayList<>();

        for (String line : newLines) {
            if (existingLines.contains(line)) {
                bothLines.add(trimCanReleaseLine(line));
            } else {
                onlyInNewGenerated.add(trimCanReleaseLine(line));
            }
        }

        for (String line : existingLines) {
            if (!newLines.contains(line)) {
                onlyInModDir.add(trimCanReleaseLine(line));
            }
        }

        // Sort the lists alphabetically
        Collections.sort(bothLines);
        Collections.sort(onlyInModDir);
        Collections.sort(onlyInNewGenerated);

        reportWriter.write("=== Can Release Lines Comparison ===\n\n");

        reportWriter.write("=== Existed on both files ===\n");
        for (String line : bothLines) {
            reportWriter.write(line + "\n");
        }
        reportWriter.write("\n");

        reportWriter.write("=== Do only exist on mod directory ===\n");
        for (String line : onlyInModDir) {
            reportWriter.write(line + "\n");
        }
        reportWriter.write("\n");

        reportWriter.write("=== Do only exist on new generated file ===\n");
        for (String line : onlyInNewGenerated) {
            reportWriter.write(line + "\n");
        }
        reportWriter.write("\n");
    }

    private static String trimCanReleaseLine(String line) {
        return line.replace(" = {", "").trim();
    }
}
