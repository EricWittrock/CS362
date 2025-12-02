# a python script that concatenates all .java files and saves it as all.txt
import os

txt = ""
for file in os.listdir():
    if file.endswith(".java"):
        with open(file, "r") as f:
            txt += f.read() + "\n\n"
            
with open("all.txt", "w") as f:
    f.write(txt)