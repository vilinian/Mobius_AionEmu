@ECHO off
TITLE AionEmu - Login Server Panel
:MENU
CLS
ECHO.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                        AionEmu - Login Server Panel                      ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Development                                       3 - Quit        ^|
ECHO   ^|    2 - Production                                                        ^|
ECHO   ^|                                                                          ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.
SET /P OPTION=Type your option and press ENTER: 
IF %OPTION% == 1 (
SET MODE=DEVELOPMENT
SET JAVA_OPTS=-server -Dfile.encoding=UTF-8 -Dorg.slf4j.simpleLogger.log.com.zaxxer.hikari=warn -XX:+UseZGC -Xms128m -Xmx256m -agentlib:jdwp=transport=dt_socket,address=8999,server=y,suspend=n -ea
CALL AionEmu-LoginServer.bat
)
IF %OPTION% == 2 (
SET MODE=PRODUCTION
SET JAVA_OPTS=-server -Dfile.encoding=UTF-8 -Dorg.slf4j.simpleLogger.log.com.zaxxer.hikari=warn -XX:+UseZGC -Xms128m -Xmx256m
CALL AionEmu-LoginServer.bat
)
IF %OPTION% == 3 (
EXIT
)
IF %OPTION% GEQ 4 (
GOTO :MENU
)