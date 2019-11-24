@echo off
cls
SET /P ip="IP: "
e:
cd "E:\Prg\ASDK\platform-tools"
adb disconnect
adb tcpip 5555
adb connect %ip%
c:
cd "C:\Users\pomam\AndroidStudioProjects\Reminder"