#!/usr/bin/env bash
sed -i 's#mnt/spark#vol0/spark#g' ~/spark/conf/spark-env.sh
~/spark-ec2/copy-dir.sh ~/spark/conf/spark-env.sh
