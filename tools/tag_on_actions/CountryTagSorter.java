import java.io.*;
import java.util.*;

public class CountryTagSorter {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java CountryTagSorter <inputFile> <outputFile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Collections.sort(lines);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            for (String sortedLine : lines) {
                bw.write(sortedLine);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Sorting completed. Check the output file: " + outputFile);
    }
}
