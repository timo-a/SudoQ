import fileinput
import sys
import os

#input validation
if not sys.argv[2]:
    print("Error: no position argument given", file = sys.stderr )

print("file is: " + sys.argv[1])
print("position argument is: " + sys.argv[2])
    

position = sys.argv[2]

INCREMENT = 0
OVERRIDE  = 1



if position in ["M","m","p"]:
    print(f"increment ({position})")
    operation = INCREMENT
else:
    print(f"override old version with ({position})")
    operation = OVERRIDE


def increment(versionName, severity):
    (major, minor, patch) = versionName.split('.')
    if severity == 'M':
        major = str(int(major) + 1)
        minor = "0"
        patch = "0"
    elif position == 'm':
        minor = str(int(minor) + 1)
        patch = "0"
    else:
        patch = str(int(patch) + 1)

    versionNameIncremented = '.'.join((major, minor, patch))
    os.environ['NEW_VERSION_NAME'] = versionNameIncremented
    return versionNameIncremented
    

for line in fileinput.input(files=(sys.argv[1]), inplace=True):
    if line.find("android:versionName=") >= 0:
        versionName = line.split('"')[1]

        if operation == INCREMENT:
            newVersionName = increment(versionName, position)
        else:
            os.environ['NEW_VERSION_NAME'] = position
            newVersionName = position

        line = line.replace(versionName, newVersionName)
    print(line, end='')

#save to file so we can use it later
with open("new_version_name.txt", "w") as f:
    f.write(newVersionName)
