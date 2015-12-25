import string

fileName = "enwiki-latest-pages-articles9.xml-p000665001p000925000"

print "Formatting" + fileName

inPage = False

inFile = open(fileName, 'r')
title = ""

num = 0

for line in inFile:
    if inPage:
        if line.find("</page>") != -1:
            inPage = False
            print "finish reading page: " + title
            outFile.close()
            num += 1
            if num >= 1000:
                break
            continue
        elif line.find("<title>") != -1:
            title = line[line.find("<title>")+7:line.find("</title>")]
            title = string.replace(title, '/', '_')
            title = string.replace(title, '#', '_')
            title = string.replace(title, ':', '_')
            title = string.replace(title, ',', '_')
            title = string.replace(title, ' ', '_')
            title = string.replace(title, '?', '')
            print "start reading page: " + title
            outFile = open("large/" + title, 'w')
        outFile.write(line)
    else:
        if line.find("<page>") != -1:
            inPage = True
