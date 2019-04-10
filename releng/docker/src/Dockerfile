FROM centos:7
LABEL maintainer="B2i Healthcare <http://b2i.sg/>"

ARG SERVER_ARCHIVE_NAME

ARG SUID=1042
ARG SGID=1042

VOLUME /tmp

# Install java 1.8 as a pre requirement
RUN yum -y update -q -e 0 && yum clean all
RUN yum install -y \
       java-1.8.0-openjdk \
       java-1.8.0-openjdk-devel -q -e 0

RUN yum -y install unzip -q e 0

# Set JAVA_HOME environment variable
ENV JAVA_HOME /etc/alternatives/jre

RUN mkdir /opt/snowowl

# Create the snowowl user
#RUN groupadd -g $SGID snowowl && \
#    adduser -D -u $SUID -G snowowl snowowl
#
#RUN chown -R snowowl:snowowl /opt/snowowl

WORKDIR /usr/share

COPY ${SERVER_ARCHIVE_NAME} ${SERVER_ARCHIVE_NAME}

RUN mkdir /usr/share/snowowl && mkdir /usr/share/snowowl/bin

COPY install_snowowl.sh /usr/share/snowowl/bin/install_snowowl.sh

RUN chgrp 0 /usr/share/snowowl/bin/install_snowowl.sh && \
    chmod g=u /etc/passwd && \
    chmod 0775 /usr/share/snowowl/bin/install_snowowl.sh

RUN /usr/share/snowowl/bin/install_snowowl.sh /usr/share/${SERVER_ARCHIVE_NAME}

RUN rm -rf /usr/share/${SERVER_ARCHIVE_NAME}

RUN rm -f usr/share/snowowl/bin/install_snowowl.sh

# Overwrite config file after installation
COPY /config/snowowl_config.yml /opt/snowowl/server/snowowl_config.yml

RUN mkdir /var/log/snowowl

RUN chgrp 0 /var/log/snowowl && \
    chmod g=u /etc/passwd && \
    chmod 0775 /var/log/snowowl

RUN ln -sf /opt/snowowl/server/servicability/logs /var/log/snowowl

ENTRYPOINT [/opt/snowowl/server/bin/startup.sh]
