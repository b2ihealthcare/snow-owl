FROM centos:7

ARG SNOWOWL_RPM_PACKAGE
ARG BUILD_TIMESTAMP
ARG VERSION
ARG GIT_REVISION

# Install java-11-openjdk as a pre requirement
RUN rpmkeys --import file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7 && \
    yum update --setopt=tsflags=nodocs -y -q -e 0 && \
    yum install --setopt=tsflags=nodocs -y -q -e 0 java-11-openjdk && \
    yum clean all

# Set JAVA_HOME environment variable
ENV JAVA_HOME /etc/alternatives/jre

# Install Snow Owl rpm package
WORKDIR /usr/share
COPY ${SNOWOWL_RPM_PACKAGE} ${SNOWOWL_RPM_PACKAGE}
RUN rpm --install ${SNOWOWL_RPM_PACKAGE} && rm -f ${SNOWOWL_RPM_PACKAGE}
COPY docker/config/snowowl.yml /etc/snowowl/snowowl.yml
RUN chown snowowl:snowowl /etc/snowowl/snowowl.yml

# Openshift overrides USER and uses ones with randomly uid>1024 and gid=0
RUN chgrp 0 /usr/share/snowowl/bin/snowowl.sh && \
    chmod g=u /etc/passwd && \
    chmod 0775 /usr/share/snowowl/bin/snowowl.sh

# Expose necessary ports used by Snow OWl
EXPOSE 2036 8080

USER snowowl

LABEL org.label-schema.build-date="${BUILD_TIMESTAMP}" \
  org.label-schema.vcs-ref="${GIT_REVISION}" \
  org.label-schema.version="${VERSION}" \
  org.label-schema.license="Apache-2.0" \
  org.label-schema.name="Snow Owl OSS" \
  org.label-schema.schema-version="1.0" \
  org.label-schema.url="https://github.com/b2ihealthcare/snow-owl" \
  org.label-schema.usage="https://docs.b2i.sg/snow-owl" \
  org.label-schema.vcs-url="https://github.com/b2ihealthcare/snow-owl" \
  org.label-schema.vendor="B2i Healthcare" \
  org.opencontainers.image.created="${BUILD_TIMESTAMP}" \
  org.opencontainers.image.revision="${GIT_REVISION}" \
  org.opencontainers.image.version="${VERSION}" \
  org.opencontainers.image.licenses="Apache-2.0" \
  org.opencontainers.image.title="Snow Owl OSS" \
  org.opencontainers.image.url="https://github.com/b2ihealthcare/snow-owl" \
  org.opencontainers.image.documentation="https://docs.b2i.sg/snow-owl" \
  org.opencontainers.image.source="https://github.com/b2ihealthcare/snow-owl" \
  org.opencontainers.image.vendor="B2i Healthcare"

ENTRYPOINT ["/usr/share/snowowl/bin/snowowl.sh"]
