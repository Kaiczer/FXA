import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AnnexationGenerator {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java AnnexationGenerator <inputTXT>");
            return;
        }

        String inputTXT = args[0];
        String outputDir = new File(inputTXT).getParent();

        // Clear the content of the output files before starting if they exist
        try {
            clearFile(outputDir + "/01_annexation_trigger_out.txt");
            clearFile(outputDir + "/01_annexation_gui_out.txt");
            clearFile(outputDir + "/01_annexation_effect_out.txt");
            clearFile(outputDir + "/01_annexation_event_out.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(inputTXT))) {
            String line;
            Map<Integer, Integer> continentEventCount = new HashMap<>();
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 6) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                String tagOrRegion = parts[0];
                String stateTrigger = parts[1];
                String landSize = parts[2];
                String commonName = parts[3];
                String landType = parts[4];
                int continent = Integer.parseInt(parts[5]);

                continentEventCount.putIfAbsent(continent, -1);
                int eventNumber = continentEventCount.get(continent) + 1;
                continentEventCount.put(continent, eventNumber);
                int continentEventId = continent * 1000 + eventNumber;

                generateAnnexationFiles(outputDir, tagOrRegion, stateTrigger, landSize, commonName, landType, continentEventId, isFirstLine);
                generateCountryEventFile(outputDir, tagOrRegion, stateTrigger, landSize, commonName, landType, continentEventId);
                isFirstLine = false;
            }

            System.out.println("Annexation files generated successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateAnnexationFiles(String outputDir, String tagOrRegion, String stateTrigger, String landSize, String commonName, String landType, int eventId, boolean isFirstLine) {
        String upperTagOrRegion = tagOrRegion.toUpperCase();

        // Generate the trigger content
        StringBuilder annexationTriggerContent = new StringBuilder();
        annexationTriggerContent.append("# ").append(capitalize(commonName)).append(" - annexation_").append(upperTagOrRegion).append("_CONQ\n")
            .append("can_release_").append(commonName.toLowerCase()).append(" = {\n");
        if (stateTrigger.matches("\\d+")) {
            annexationTriggerContent.append("    state = ").append(stateTrigger).append("\n");
        } else {
            annexationTriggerContent.append("    ").append(stateTrigger).append(" = yes\n");
        }
        annexationTriggerContent.append("    NOT = { is_claimed_by = ROOT }\n")
            .append("    ROOT = {\n");
        annexationTriggerContent.append("        NOT = {\n");
        if ("nation".equals(landType)) {
            annexationTriggerContent.append("            country_exists = ").append(tagOrRegion).append("\n");
        }
        annexationTriggerContent.append("            has_country_flag = ").append(upperTagOrRegion).append("_CONQ\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n");

        // Generate the GUI effect content
        StringBuilder annexationGuiEffectContent = new StringBuilder();
        if (isFirstLine) {
            annexationGuiEffectContent.append("if = {\n");
        } else {
            annexationGuiEffectContent.append("else_if = {\n");
        }
        annexationGuiEffectContent.append("    limit = { can_release_").append(commonName.toLowerCase()).append(" = yes }\n")
            .append("    ROOT = { activate_mission = annexation_").append(upperTagOrRegion).append("_CONQ }\n")
            .append("}\n");

        // Generate the decision content
        StringBuilder annexationDecisionContent = new StringBuilder();
        annexationDecisionContent.append("# ").append(capitalize(commonName)).append("\n")
            .append("annexation_").append(upperTagOrRegion).append("_CONQ = {\n")
            .append("    icon = generic_form_nation\n")
            .append("    selectable_mission = yes\n")
            .append("    days_mission_timeout = @annexation_days_mission_timeout\n")
            .append("    is_good = no\n")
            .append("    available = {\n")
            .append("        can_select_annexation_mission = yes\n")
            .append("    }\n")
            .append("    cancel_trigger = {\n");
        if (stateTrigger.matches("\\d+")) {
            annexationDecisionContent.append("        ").append(stateTrigger).append(" = { is_valid_annexation_state = no }\n");
        } else {
            annexationDecisionContent.append("        ").append(stateTrigger).append(" = no\n");
        }
        annexationDecisionContent.append("    }\n")
            .append("    complete_effect = {\n")
            .append("        log = \"[GetDateText]: [Root.GetName]: Decision complete annexation_").append(upperTagOrRegion).append("_CONQ\"\n")
            .append("        block_annexation_mission_selection = yes\n")
            .append("        country_event = annex.").append(eventId).append("\n")
            .append("    }\n")
            .append("    timeout_effect = {\n")
            .append("        log = \"[GetDateText]: [Root.GetName]: Decision timeout annexation_").append(upperTagOrRegion).append("_CONQ\"\n")
            .append("        block_annexation_mission_selection = yes\n")
            .append("        country_event = annex.").append(eventId).append("\n")
            .append("    }\n")
            .append("    ai_will_do = { factor = 100 }\n")
            .append("}\n\n");

        // Write the content to files
        try {
            appendToFile(outputDir + "/01_annexation_trigger_out.txt", annexationTriggerContent.toString());
            appendToFile(outputDir + "/01_annexation_gui_out.txt", annexationGuiEffectContent.toString());
            appendToFile(outputDir + "/01_annexation_effect_out.txt", annexationDecisionContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateCountryEventFile(String outputDir, String tagOrRegion, String stateTrigger, String landSize, String commonName, String landType, int eventId) {
        String upperTagOrRegion = tagOrRegion.toUpperCase();
        
        StringBuilder countryEventContent = new StringBuilder();
        countryEventContent.append("country_event = { # Fate of ").append(capitalize(commonName)).append("\n")
            .append("    id = annex.").append(eventId).append("\n")
            .append("    title = annexation_").append(upperTagOrRegion).append("_CONQ\n")
            .append("    desc = annexation_").append(upperTagOrRegion).append("_CONQ_desc\n")
            .append("    picture = GFX_report_event_generic_conference2\n\n")
            .append("    is_triggered_only = yes\n\n");

        if ("nation".equals(landType)) {
            countryEventContent.append("    immediate = {\n")
                .append("        hidden_effect = {\n\n")
                .append("        }\n")
                .append("    }\n\n");
        }

        countryEventContent.append("    option = { # Liberate ").append(tagOrRegion).append("\n")
            .append("        log = \"[GetLogInfo]: event annex.").append(eventId).append(" option annex.").append(eventId).append(".a\"\n")
            .append("        name = annex.").append(eventId).append(".a\n")
            .append("        ai_chance = { base = 100 }\n");

        if ("nation".equals(landType)) {
            countryEventContent.append("        set_temp_variable = { tag_to_release = ").append(tagOrRegion).append(" }\n")
                .append("        release_targeted_tag = yes\n");
        }

        countryEventContent.append("        recheck_annexations = yes\n")
            .append("    }\n\n")
            .append("    option = { # Occupy ").append(tagOrRegion).append("\n")
            .append("        log = \"[GetLogInfo]: event annex.").append(eventId).append(" option annex.occupation\"\n")
            .append("        name = annex.occupation\n")
            .append("        ai_chance = { base = 0 }\n")
            .append("        occupy_").append(landSize).append("_country = yes\n")
            .append("        set_country_flag = ").append(upperTagOrRegion).append("_CONQ\n")
            .append("        recheck_annexations = yes\n")
            .append("    }\n")
            .append("}\n\n");

        try {
            appendToFile(outputDir + "/01_annexation_event_out.txt", countryEventContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void appendToFile(String filePath, String content) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(content);
        }
    }

    private static void clearFile(String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // This will clear the file content
        }
    }

    private static String capitalize(String input) {
        String[] words = input.split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase()).append(" ");
            }
        }
        return capitalized.toString().trim();
    }
}
