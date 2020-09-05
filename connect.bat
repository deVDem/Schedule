@echo off
cls
SET /P ip="IP: "
c:
cd "C:\Users\devdem\AppData\Local\Android\Sdk\platform-tools\"
adb disconnect
adb tcpip 5555
adb connect %ip%
c:
cd "C:\Users\devdem\StudioProjects\Schedule"