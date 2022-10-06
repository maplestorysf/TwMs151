@echo off
@title Dump
set CLASSPATH=.;dist\*
java -Xms8192m -Xmx10920m -Dnet.sf.odinms.wzpath=wz tools.wztosql.DumpQuests
pause