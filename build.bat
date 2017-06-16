del /s /q output-bin
cd launch4j
launch4jc.exe ../launch4j.xml
cd ..
cd output-bin
mkdir natives
cd natives
mkdir windows
xcopy ..\..\natives\windows windows /e
cd ..
cd ..

..\..\binutil\ksignCMD.exe /f C:\Users\tyron\Dropbox\job\comodo_codesigning.pfx /p xLd2oGZ1S7CA+ output-bin/VSModelCreator.exe

"..\binutil\InnoSetup5\ISCC.exe" innosetup.iss

pause