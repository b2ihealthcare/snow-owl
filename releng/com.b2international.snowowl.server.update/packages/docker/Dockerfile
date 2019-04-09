FROM centos:7
LABEL maintainer="B2i Healthcare <http://b2i.sg/>"

# Install java 1.8 as a pre requirement
RUN yum -y update -q -e 0 && yum clean all
RUN yum install -y \
       java-1.8.0-openjdk \
       java-1.8.0-openjdk-devel -q -e 0

# Set JAVA_HOME environment variable
ENV JAVA_HOME /etc/alternatives/jre

# Install wget
RUN yum -y install wget -q -e 0 

# Download and install 7.0.0 Snow Owl rpm package
RUN wget https://github.com/b2ihealthcare/snow-owl/releases/download/v7.0.0/snow-owl-oss-7.0.0.rpm -q
RUN rpm --install snow-owl-oss-7.0.0.rpm

# Openshift overrides USER and uses ones with randomly uid>1024 and gid=0
RUN chgrp 0 /usr/share/snowowl/bin/startup.sh && \
    chmod g=u /etc/passwd && \
    chmod 0775 /usr/share/snowowl/bin/startup.sh

# Overwrite config file after installation
COPY snowowl.yml /etc/snowowl/snowowl.yml

# Set environment variable for Snow Owl data folder
ENV SO_PATH_DATA /var/lib/snowowl:$SO_PATH_DATA

# Set environment ES_URL variable for Snow Owl 
ENV ES_CLUSTER_URL http://es:9300:$ES_CLUSTER_URL

# Expose necessary ports used by Snow OWl
EXPOSE 2036 8080

USER snowowl

ENTRYPOINT ["/usr/share/snowowl/bin/startup.sh"]
