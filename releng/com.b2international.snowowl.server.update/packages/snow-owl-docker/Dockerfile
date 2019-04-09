FROM centos:7
LABEL maintainer="B2i Healthcare <http://b2i.sg/>"

ARG SNOWOWL_VERSION
ARG ES_CLUSTER_URL_ARG

# Set environment ES_URL variable for Snow Owl 
ENV ES_CLUSTER_URL ${ES_CLUSTER_URL_ARG}:$ES_CLUSTER_URL

# Install java 1.8 as a pre requirement
RUN yum -y update -q -e 0 && yum clean all
RUN yum install -y \
       java-1.8.0-openjdk \
       java-1.8.0-openjdk-devel -q -e 0

# Set JAVA_HOME environment variable
ENV JAVA_HOME /etc/alternatives/jre

# Install wget
RUN yum -y install wget -q -e 0 
# Download 7.0.0 Snow Owl rpm package
RUN wget https://github.com/b2ihealthcare/snow-owl/releases/download/v${SNOWOWL_VERSION}/snow-owl-oss-${SNOWOWL_VERSION}.rpm -q

# Install Snow Owl rpm package
RUN rpm --install snow-owl-oss-${SNOWOWL_VERSION}.rpm

# Openshift overrides USER and uses ones with randomly uid>1024 and gid=0
RUN chgrp 0 /usr/share/snowowl/bin/startup.sh && \
    chmod g=u /etc/passwd && \
    chmod 0775 /usr/share/snowowl/bin/startup.sh

# Overwrite config file after installation
COPY /config/snowowl.yml /etc/snowowl/snowowl.yml

# Set environment variable for Snow Owl data folder
ENV SO_PATH_DATA /var/lib/snowowl:$SO_PATH_DATA

# Expose necessary ports used by Snow OWl
EXPOSE 2036 8080

USER snowowl

ENTRYPOINT ["/usr/share/snowowl/bin/startup.sh"]
