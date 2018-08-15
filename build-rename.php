<?php

$date = strtolower(date("dMY"));

rename("output-release/vsmodelcreator.zip", "output-release/vsmodelcreator_{$date}.zip");
rename("output-release/vsmodelcreator_installer.exe", "output-release/vsmodelcreator_installer_{$date}.exe");
rename("output-release/vsmodelcreator_portable.exe", "output-release/vsmodelcreator_portable_{$date}.exe");