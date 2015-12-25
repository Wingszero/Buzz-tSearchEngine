rm -rf ../testdir/tfidf/*
rm -rf ../testdir/wiki_tfidf/postings
java -jar jetty-runner.jar --port 8080 servlet.war
