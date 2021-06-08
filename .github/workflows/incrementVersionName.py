import fileinput
import sys

#input validation
#must be (b|n|p) for breaking, non breaking, patch
#any word starting with b/n/p works
if not sys.argv[2]:
    print("Error: no position argument given", file = sys.stderr )

position = sys.argv[2][0]

if position not in "bnp":
    print("Error: position argument is not valid", file = sys.stderr )

for line in fileinput.input(files=(sys.argv[1]), inplace=True):
    if line.find("android:versionName=") >= 0:
        versionName = line.split('"')[1]
        
        (b,n,p) = versionName.split('.')
        if position == 'b':
            b = str(int(b) + 1)
        elif position == 'n':
            n = str(int(n) + 1)
        else:
            p = str(int(p) + 1)

        versionNameIncremented = '.'.join((b,n,p))
        line = line.replace(versionName, versionNameIncremented)
    print(line, end='')
