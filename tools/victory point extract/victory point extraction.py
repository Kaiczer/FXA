import os
import re
import codecs

def find_victory_points(states_dir, output_dir, vp_file):
    victory_points = set()
    pattern = re.compile(r"victory_points\s*=\s*\{\s*(\d+)")
    
    for root, _, files in os.walk(states_dir):
        for file in files:
            if file.endswith(".txt"):
                file_path = os.path.join(root, file)
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                        matches = pattern.findall(content)
                        victory_points.update(matches)
                except (OSError, UnicodeDecodeError) as e:
                    print(f"Error reading {file_path}: {e}")
    
    if not victory_points:
        print("No victory points found.")
        return
    
    vp_names = {}
    vp_pattern = re.compile(r'VICTORY_POINTS_(\d+):\s*"([^\"]*)"')
    
    try:
        with codecs.open(vp_file, 'r', encoding='utf-8-sig') as f:  # Handles UTF-8 with BOM
            content = f.read()
            matches = vp_pattern.findall(content)
            for num, name in matches:
                vp_names[num] = name
    except (OSError, UnicodeDecodeError) as e:
        print(f"Error reading {vp_file}: {e}")
    
    output_file = os.path.join(output_dir, "victory_points.txt")
    try:
        with open(output_file, 'w', encoding='utf-8') as f:
            for vp in sorted(victory_points, key=int):
                city_name = vp_names.get(vp, "Placeholder")
                f.write(f"VICTORY_POINTS_{vp}: \"{city_name}\"\n")
        print(f"Victory points extracted and saved to {output_file}")
    except OSError as e:
        print(f"Error writing to {output_file}: {e}")

def main():
    states_dir = input("Enter the path to the states folder: ").strip()
    output_dir = input("Enter the path for the output file: ").strip()
    vp_file = input("Enter the path to the victory points file: ").strip()
    
    if not os.path.isdir(states_dir):
        print("Error: States directory not found!")
        return
    if not os.path.isdir(output_dir):
        try:
            os.makedirs(output_dir)
        except OSError as e:
            print(f"Error creating output directory: {e}")
            return
    if not os.path.isfile(vp_file):
        print("Error: Victory points file not found!")
        return
    
    find_victory_points(states_dir, output_dir, vp_file)

if __name__ == "__main__":
    main()
