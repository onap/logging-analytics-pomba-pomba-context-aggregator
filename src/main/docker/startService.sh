#!/bin/sh


cd /opt/app
preStartFile="/opt/app/bin/pre_start.sh"
if [ -f "$preStartFile" ]
then
	echo "Executing $preStartFile ... ..."
	$preStartFile
else
	echo "$preStartFile not found."
fi

cd /opt/app
if [ -z "${java_runtime_arguments}" ]; then
	java -Dlogging.config=config/logback.xml -Xms128m -Xmx512m -jar /opt/app/lib/pomba-context-aggregator.jar

else
	java -Dlogging.config=config/logback.xml $java_runtime_arguments -jar /opt/app/lib/pomba-context-aggregator.jar
fi
