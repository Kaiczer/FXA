def generate_annexation_script(input_file, output_file="01_Annexation_GUI_effects.txt"):
    try:
        with open(input_file, "r", encoding="utf-8") as file:
            regions = sorted(set(line.strip() for line in file if line.strip()))  # Read, clean, remove duplicates, and sort

        if not regions:
            print("Error: No regions found in the input file.")
            return

        with open(output_file, "w", encoding="utf-8") as file:
            file.write("check_possible_annexations_europe = {\n")

            first = True
            for region in regions:
                if first:
                    file.write(f"    if = {{\n")
                    first = False
                else:
                    file.write(f"    else_if = {{\n")

                file.write(f"        limit = {{ can_release_{region} = yes }}\n")
                file.write(f"        ROOT = {{ activate_targeted_decision = {{ decision = annexation_{region} target = PREV }} }}\n")
                file.write("    }\n")

            file.write("}\n")

        print(f"Annexation script generated successfully: {output_file}")

    except FileNotFoundError:
        print(f"Error: The file '{input_file}' was not found.")
    except Exception as e:
        print(f"An error occurred: {e}")

# Example usage
input_file = "regions.txt"  # Ensure this file exists
generate_annexation_script(input_file)
