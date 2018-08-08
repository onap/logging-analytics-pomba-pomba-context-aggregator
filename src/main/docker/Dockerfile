FROM anapsix/alpine-java:jre8

ARG MICROSERVICE_HOME=/opt/app/

# Build up the deployment folder structure
RUN mkdir -p $MICROSERVICE_HOME
RUN mkdir -p $MICROSERVICE_HOME/config/
RUN mkdir -p $MICROSERVICE_HOME/lib/
RUN mkdir -p $MICROSERVICE_HOME/bin/

ADD target/pomba-context-aggregator.jar $MICROSERVICE_HOME/lib/
COPY config/ $MICROSERVICE_HOME/config/
ADD target/startService.sh $MICROSERVICE_HOME/bin/

RUN chmod 755 $MICROSERVICE_HOME/config/*
RUN chmod 755 $MICROSERVICE_HOME/lib/*
RUN chmod 755 $MICROSERVICE_HOME/bin/*

CMD ["/opt/app/bin/startServer.sh"]
