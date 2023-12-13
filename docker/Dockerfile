ARG BASE_IMAGE="b2ihealthcare/ubuntu:lts"

FROM ${BASE_IMAGE} AS builder

ARG SNOWOWL_INSTALL_PACKAGE

RUN mkdir /usr/share/snowowl
WORKDIR /usr/share/snowowl

COPY ${SNOWOWL_INSTALL_PACKAGE} ${SNOWOWL_INSTALL_PACKAGE}
RUN tar -zxf ${SNOWOWL_INSTALL_PACKAGE} --strip-components=1 && rm -f ${SNOWOWL_INSTALL_PACKAGE}
RUN chmod --silent 0775 configuration resources serviceability
COPY config/snowowl.yml configuration/
RUN chmod 0660 configuration/snowowl.yml

FROM ${BASE_IMAGE}

RUN groupadd -g 1000 snowowl && \
    useradd --uid 1000 --gid 1000 --home-dir /usr/share/snowowl --create-home --shell /bin/bash snowowl && \
    usermod --append --groups root snowowl && \
    chown -R 0:0 /usr/share/snowowl

WORKDIR /usr/share/snowowl

COPY --from=builder --chown=0:0 /usr/share/snowowl /usr/share/snowowl
RUN ln -s /usr/share/snowowl/configuration /etc/snowowl && \
    ln -s /usr/share/snowowl/resources /var/lib/snowowl && \
    ln -s /usr/share/snowowl/serviceability /var/log/snowowl

COPY bin/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

RUN chmod g=u /etc/passwd && \
    chmod 0555 /usr/local/bin/docker-entrypoint.sh && \
    chmod 0775 /usr/share/snowowl && \
    chown --silent --recursive snowowl bin configuration resources serviceability work 

# Expose necessary ports used by Snow Owl
EXPOSE 2036 8080

LABEL org.label-schema.license="Apache-2.0" \
    org.label-schema.name="Snow Owl OSS" \
    org.label-schema.schema-version="1.0" \
    org.label-schema.url="https://github.com/b2ihealthcare/snow-owl" \
    org.label-schema.usage="https://docs.b2ihealthcare.com/snow-owl" \
    org.label-schema.vcs-url="https://github.com/b2ihealthcare/snow-owl/tree/8.x" \
    org.label-schema.vendor="B2i Healthcare" \
    org.opencontainers.image.licenses="Apache-2.0" \
    org.opencontainers.image.title="Snow Owl OSS" \
    org.opencontainers.image.url="https://github.com/b2ihealthcare/snow-owl" \
    org.opencontainers.image.documentation="https://docs.b2ihealthcare.com/snow-owl" \
    org.opencontainers.image.source="https://github.com/b2ihealthcare/snow-owl/tree/8.x" \
    org.opencontainers.image.vendor="B2i Healthcare"

USER snowowl:root

ENTRYPOINT ["/usr/bin/tini", "--", "/usr/local/bin/docker-entrypoint.sh"]
# Dummy overridable parameter parsed by entrypoint
CMD ["sowrapper"]
