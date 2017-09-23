<?php

$subcounts = array();
$totallines = countLines("src/at/vintagestory", array(""), array("Mat4f.java"));
$subcounts = array_reverse($subcounts);

foreach ($subcounts as $subcount) {
	echo $subcount;
}

echo "\r\nTotal lines: " . number_format($totallines);




function countLines($folder, $excludefoldernames, $excludefilenames, $depth = 0) {
	global $subcounts;
	
	$lines = 0;
	
	$dir = opendir($folder);
	while ($file = readdir($dir)) {
		
		if (preg_match("#\.java$#", $file) && !in_array($file, $excludefilenames)) {
			$lines += count(file($folder . '/' . $file));
		}
		if (is_dir($folder . '/' . $file) && $file != '.' && $file != '..' && !in_array($file, $excludefoldernames)) {
			$linesOfDir  = countLines($folder . '/' . $file, $excludefoldernames, $excludefilenames, $depth + 1);
			if ($depth < 3 && $linesOfDir > 0) {
				$prefix = pad($depth) . $file;
				$subcounts[] = $prefix . pad(15 - strlen($prefix)/2) . number_format($linesOfDir ) . "\r\n";	
			}
			$lines += $linesOfDir ;
		}
	}
	return $lines;
}

function pad($depth) {
	if ($depth <= 0) return ""; 
	return str_repeat("  ", $depth);
}