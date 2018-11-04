@echo off
set path=%path%;%systemroot%\SysWOW64
start javaw -Xmx512m -cp bin;NullpoMino.jar;lib\log4j.jar;lib\swing-worker.jar;lib\swing-layout.jar -Djava.library.path=lib mu.nu.nullpo.tool.airankstool.AIRanksTool
