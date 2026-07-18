#!/bin/sh

err=1
until [ $err == 0 ];
do

	java -server -Dfile.encoding=UTF-8 -Dorg.slf4j.simpleLogger.log.com.zaxxer.hikari=warn -XX:+UseZGC -Xms128m -Xmx256m -cp ../libs/*:AionEmu-Login.jar com.aionemu.loginserver.LoginServer
	err=$?
	lspid=$!
	echo ${lspid} > loginserver.pid
	echo "LoginServer started!"
	sleep 10
done
