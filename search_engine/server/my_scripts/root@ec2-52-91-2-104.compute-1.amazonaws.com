#!/usr/bin/env bash
~/persistent-hdfs/sbin/stop-all.sh
sed -i 's#vol/persistent-hdfs#vol0/persistent-hdfs#g' ~/persistent-hdfs/conf/core-site.xml
~/spark-ec2/copy-dir.sh ~/persistent-hdfs/conf/core-site.xml
~/persistent-hdfs/bin/hadoop namenode -format -y
~/persistent-hdfs/sbin/start-all.sh
