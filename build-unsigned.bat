del /s /q output-bin


copy launch.exe output-bin\launch.exe
copy launch.bat output-bin\launch.bat

mkdir output-bin\vsmodelcreator_lib
mkdir output-bin\natives

copy output-jar\vsmodelcreator.jar output-bin\vsmodelcreator.jar
xcopy output-jar\vsmodelcreator_lib output-bin\vsmodelcreator_lib /e
xcopy output-jar\natives output-bin\natives /e


mkdir tmp
"Bat_To_Exe_Converter_(x64).exe" /bat launch-bat2exe.bat /exe tmp\launch.exe /icon assets\appicon.ico /include output-bin\vsmodelcreator.jar /include output-bin\launch.bat /include output-bin\natives  /include output-bin\vsmodelcreator_lib /extractdir 1 /workdir 1 /invisible
move tmp\launch.exe output-release\vsmodelcreator_portable.exe
rmdir tmp




"..\binutil\InnoSetup5\ISCC.exe" innosetup.iss

cd output-bin
..\..\binutil\7Zip\7z a -tzip ..\output-release\vsmodelcreator.zip *
cd ..

php build-rename.php

pause

