import string

fileName = "enwiki-20151201-abstract11.xml"

print "Formatting" + fileName

inFile = open(fileName, 'r')
outFile = open("wiki", "w")

inPage = False

title = ""
abstract = ""
url = ""

num = 0

for line in inFile:
    if inPage:
        if line.find("</doc>") != -1:
            inPage = False
            outFile.write(url+"::"+title+"::"+abstract + "\t" + title + "\n")
            title = ""
            url = ""
            abstract = ""
            num += 1
            if num % 5000 == 0:
                if num == 5000:
                    break
                print "Finish reading " + num + " pages..."
            continue
        elif line.find("<title>") != -1:
            title = line[line.find("<title>")+18:line.find("</title>")]
        elif line.find("<url>") != -1:
            url = line[line.find("<url>")+5:line.find("</url>")]
        elif line.find("<abstract>") != -1:
            abstract = line[line.find("<abstract>")+10:line.find("</abstract>")]
    else:
        if line.find("<doc>") != -1:
            inPage = True
