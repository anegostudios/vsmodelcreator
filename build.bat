del /s /q output-bin

copy launch.exe output-bin\launch.exe
copy launch.bat output-bin\launch.bat

mkdir output-bin\vsmodelcreator_lib
mkdir output-bin\natives

xcopy output-jar\vsmodelcreator_lib output-bin\vsmodelcreator_lib /e
xcopy output-jar\natives output-bin\natives /e

"..\binutil\InnoSetup5\ISCC.exe" innosetup.iss

cd output-bin
..\..\binutil\7Zip\7z a -tzip ..\output-release\vsmodelcreator.zip *
cd ..

php build-rename.php

pause