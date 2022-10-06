@echo off
@title Development v117.2
Color 3E
set CLASSPATH=.;dist\*
java -server -Dnet.sf.odinms.wzpath=wz server.Start
pause