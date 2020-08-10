FROM b2ihealthcare/centos:7 AS builder

ARG SNOWOWL_INSTALL_PACKAGE

RUN groupadd -g 1000 snowowl && \
    adduser -u 1000 -g 1000 -d /usr/share/snowowl snowowl

WORKDIR /usr/share/snowowl

COPY ${SNOWOWL_INSTALL_PACKAGE} ${SNOWOWL_INSTALL_PACKAGE}
RUN tar zxf ${SNOWOWL_INSTALL_PACKAGE} --strip-components=1 && rm -f ${SNOWOWL_INSTALL_PACKAGE}
RUN chmod 0775 configuration resources serviceability
COPY config/snowowl.yml configuration/
RUN chmod 0660 configuration/snowowl.yml

FROM b2ihealthcare/centos:7

ARG BUILD_TIMESTAMP
ARG TAG
ARG GIT_REVISION
ARG PRODUCT_NAME="Snow Owl OSS"
ARG REPOSITORY_URL="https://github.com/b2ihealthcare/snow-owl"

RUN groupadd -g 1000 snowowl && \
    adduser -u 1000 -g 1000 -G 0 -d /usr/share/snowowl snowowl && \
    chmod 0775 /usr/share/snowowl && \
    chgrp 0 /usr/share/snowowl

WORKDIR /usr/share/snowowl
COPY --from=builder --chown=1000:0 /usr/share/snowowl /usr/share/snowowl
RUN ln -s /usr/share/snowowl/configuration /etc/snowowl && \
    ln -s /usr/share/snowowl/resources /var/lib/snowowl && \
    ln -s /usr/share/snowowl/serviceability /var/log/snowowl

COPY bin/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

RUN chmod g=u /etc/passwd && \
    chmod 0775 /usr/local/bin/docker-entrypoint.sh

# Expose necessary ports used by Snow Owl
EXPOSE 2036 8080

LABEL org.label-schema.build-date="${BUILD_TIMESTAMP}" \
  org.label-schema.vcs-ref="${GIT_REVISION}" \
  org.label-schema.version="${TAG}" \
  org.label-schema.license="Apache-2.0" \
  org.label-schema.name="${PRODUCT_NAME}" \
  org.label-schema.schema-version="1.0" \
  org.label-schema.url="${REPOSITORY_URL}" \
  org.label-schema.usage="https://docs.b2i.sg/snow-owl" \
  org.label-schema.vcs-url="${REPOSITORY_URL}" \
  org.label-schema.vendor="B2i Healthcare" \
  org.opencontainers.image.created="${BUILD_TIMESTAMP}" \
  org.opencontainers.image.revision="${GIT_REVISION}" \
  org.opencontainers.image.version="${TAG}" \
  org.opencontainers.image.licenses="Apache-2.0" \
  org.opencontainers.image.title="${PRODUCT_NAME}" \
  org.opencontainers.image.url="${REPOSITORY_URL}" \
  org.opencontainers.image.documentation="https://docs.b2i.sg/snow-owl" \
  org.opencontainers.image.source="${REPOSITORY_URL}" \
  org.opencontainers.image.vendor="B2i Healthcare"

USER snowowl:root

ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
# Dummy overridable parameter parsed by entrypoint
CMD ["sowrapper"]
