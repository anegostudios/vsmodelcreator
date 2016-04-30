del /s /q output-bin
cd launch4j
launch4jc.exe ../launch4j.xml
cd ..
cd output-bin
mkdir natives
cd natives
xcopy ..\..\natives /e
cd ..
cd ..
pause