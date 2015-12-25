cd src
rm -rf WEB-INF/classes
mkdir WEB-INF/classes

javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/myapp/worker/utils/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/myapp/worker/utils/Robots/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/myapp/worker/db/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/myapp/worker/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/myapp/worker/servlet/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/indexer/ConfigLoader.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/pagerank/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/SparkConn.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/SearchEngineParam.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/indexer/stemmer/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/indexer/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/indexer/db/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/indexer/wiki/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/ranker/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/indexer/cache/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/imagesearch/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/buzzit/*.java
javac  -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/myapp/utils/*.java
javac  -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/myapp/storage/entity/*.java
javac  -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/myapp/storage/accessor/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/myapp/storage/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/myapp/view/*.java
javac -classpath WEB-INF/lib/*:WEB-INF/classes -d WEB-INF/classes com/myapp/servlet/*.java
jar -cvf servlet.war jsp htmls images css WEB-INF *.*

mv servlet.war ../

