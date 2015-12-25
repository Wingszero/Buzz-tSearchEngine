import sys
if len(sys.argv) < 3:
  print "usage: <ori_urls> <raw_urls>"
  sys.exit(0)

ori_urls = {}
for line in open(sys.argv[1]):
  ori_urls[line.strip()] = True

sys.stderr.write("ori urls ready\n")

count = 0

for l in open(sys.argv[2]):
  count += 1
  try:
    tokens = l.split("\t")
    if not tokens[0] == tokens[1].strip():
      if tokens[1].strip() in ori_urls:
        sys.stdout.write(l)
  except:
    pass
  if count % 1000000 == 0:
    sys.stderr.write(str(count) + "\n")
