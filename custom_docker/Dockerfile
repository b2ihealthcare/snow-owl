FROM b2ihealthcare/snow-owl-oss:7.10.0

# change the config path
ENV SO_PATH_CONF="/opt/snowowl/bbl/config"

COPY --chown=snowowl:snowowl snowowl.yml /opt/snowowl/bbl/config/
RUN mv /usr/share/snowowl/configuration/* $SO_PATH_CONF/


RUN rm -rf /usr/share/snowowl/configuration  && \
ln -s $SO_PATH_CONF /usr/share/snowowl/configuration

RUN ln -s $SO_PATH_CONF /etc/snowowl && \
    ln -s /usr/share/snowowl/resources /var/lib/snowowl && \
    ln -s /usr/share/snowowl/serviceability /var/log/snowowl

# define envs
ENV LOG_LEVEL $LOG_LEVEL
ENV ELASTICSEARCH_CLUSTER_URL $ELASTICSEARCH_CLUSTER_URL
ENV ELASTICSEARCH_CONNECT_TIMEOUT $ELASTICSEARCH_CONNECT_TIMEOUT
ENV ELASTICSEARCH_SOCKET_TIMEOUT $ELASTICSEARCH_SOCKET_TIMEOUT

# prepare and copy over the snowowl.yml file
COPY --chown=snowowl:snowowl snowowl.yml /opt/snowowl/bbl/config/

# prepare and copy over the serviceability.xml file
COPY --chown=snowowl:snowowl serviceability.xml /opt/snowowl/bbl/config/
