import sys
filename = sys.argv[1]

for line in open(filename):
	if len(line) < 5:
		continue
	sys.stdout.write("\"" + line.strip() + "\",")
