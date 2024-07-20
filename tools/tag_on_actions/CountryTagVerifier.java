import java.io.*;
import java.util.regex.*;

public class CountryTagVerifier {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java CountryTagVerifier <inputFile> <outputFile> <continentName>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];
        String continentName = args[2];

        System.out.println("Input File: " + inputFile);
        System.out.println("Output File: " + outputFile);
        System.out.println("Continent Name: " + continentName);

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            Pattern pattern = Pattern.compile("([A-Z]{3}) = \"([^\"]+)\"");

            while ((line = br.readLine()) != null) {
                // Remove comments and unnecessary text
                line = line.split("#")[0].trim();
                System.out.println("Processing line: " + line);

                Matcher matcher = pattern.matcher(line);

                if (matcher.matches()) {
                    String countryTag = matcher.group(1);
                    System.out.println("Found country tag: " + countryTag);

                    String outputLine = "add_to_array = { global." + continentName + "_tags_array = " + countryTag + " }";
                    bw.write(outputLine);
                    bw.newLine();
                    System.out.println("Written to output: " + outputLine);
                } else {
                    System.out.println("Line did not match pattern");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
