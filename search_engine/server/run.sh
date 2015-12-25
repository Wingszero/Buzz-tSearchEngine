rm -rf ../testdir/tfidf/*
rm -rf ../testdir/wiki_tfidf/postings
java -jar jetty-runner-9.3.6.v20151106.jar --port 8081 servlet.war

