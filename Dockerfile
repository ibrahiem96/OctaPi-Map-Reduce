from resin/rpi-raspbian

MAINTAINER KiwenLau <kiwenlau@gmail.com>

WORKDIR /root

# make directory
RUN mkdir /opt/hadoop
RUN mkdir /opt/hadoop_tmp

# install openssh-server, openjdk and wget
RUN apt-get update && apt-get install oracle-java8-jdk
RUN java -version

# passwordless ssh
RUN ssh-keygen -t rsa -b 4096 -C imoham3@uic.edu

# replicate SSH keys
RUN ssh-copy-id user@master
RUN ssh-copy-id user@slave-01
RUN ssh-copy-id user@slave-02
RUN ssh-copy-id user@slave-03
RUN ssh-copy-id user@slave-04
RUN ssh-copy-id user@slave-05
RUN ssh-copy-id user@slave-06
RUN ssh-copy-id user@slave-07
RUN ssh-copy-id user@slave-08
RUN ssh-copy-id user@slave-09
RUN ssh-copy-id user@slave-10
RUN ssh-copy-id user@slave-11

# -- HADOOP ENVIRONMENT VARIABLES START -- #
RUN export HADOOP_HOME=/opt/hadoop/hadoop
RUN export PATH=$PATH:$HADOOP_HOME/bin
RUN export PATH=$PATH:$HADOOP_HOME/sbin
RUN export HADOOP_MAPRED_HOME=$HADOOP_HOME
RUN export HADOOP_COMMON_HOME=$HADOOP_HOME
RUN export HADOOP_HDFS_HOME=$HADOOP_HOME
RUN export YARN_HOME=$HADOOP_HOME
RUN export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
RUN export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib"
# -- HADOOP ENVIRONMENT VARIABLES END -- #
RUN export JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre 


# install hadoop 2.7.2
RUN cd /opt/hadoop/
RUN sudo wget http://www-us.apache.org/dist/hadoop/common/hadoop-2.6.4/hadoop-2.6.4.tar.gz
RUN sudo tar xvf hadoop-2.6.4.tar.gz
RUN mv hadoop-2.6.4 hadoop

# set environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64 
ENV HADOOP_HOME=/usr/local/hadoop 
ENV PATH=$PATH:/usr/local/hadoop/bin:/usr/local/hadoop/sbin 

# setup hdfs
RUN sudo mkdir /opt/hadoop_tmp/hdfs
RUN sudo mkdir /opt/hadoop_tmp/hdfs/namenode

# copy configurations to slave nodes
RUN rsync -avxP $HADOOP_HOME pirate@10.0.0.13:$HADOOP_HOME

CMD [ "sh", "-c", "service ssh start; bash"]