cd launch4j
launch4jc.exe ../launch4j.xml
cd ..
cd output
mkdir natives
cd natives
xcopy ..\..\natives /e
cd ..
del *.jar
cd ..