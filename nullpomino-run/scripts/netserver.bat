@echo off
set path=%path%;%systemroot%\SysWOW64
start java -cp bin;NullpoMino.jar;lib\log4j.jar mu.nu.nullpo.game.net.NetServer %1
