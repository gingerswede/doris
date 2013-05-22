package se.lnu.cs.doris.global;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Small utility class created to do tasks that occur often.
 * 
 * @author Emil Carlsson
 * 
 * This file is a part of Doris
 *
 * Doris is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * Doris is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with Doris.  
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
public class Utilities {
	
	/**
	 * Static access to scanner so System.in doesn't get closed.
	 */
	public static Scanner scanner = new Scanner(System.in);
	
	/**
	 * Method to delete entire directories recursively.
	 * @param file
	 * @return True on success
	 */
	public static Boolean deleteDirectory(File file) {

		file.setWritable(true, false);
		file.setExecutable(true, false);
		file.setReadable(true, false);
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				deleteDirectory(f);
			}
		}
		System.gc();
		
		Boolean deleted = file.delete();;
		
		if (!deleted) {
			Path path = file.toPath();
			
			try {
				Files.deleteIfExists(path);
			} catch(Exception e) {
				if (file.isFile()) {
					System.out.format("Deletion failed.\nManual deletion of %s needed.\n", 
						file.getAbsolutePath());
				}
			}
			
		}
		
		return deleted; 
	}
	
	/**
	 * Checks if a string is possible to parse into an integer or not.
	 * @param stringInt String to be parsed.
	 * @return Boolean
	 */
	public static Boolean tryParseInt(String stringInt) {
		try {
			Integer.parseInt(stringInt);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Parses a String into an integer. Returns 0 if not parsable.
	 * @param stringInt String to be parsed.
	 * @return Integer.
	 */
	public static int parseInt(String stringInt) {
		if (tryParseInt(stringInt)) {
			return Integer.parseInt(stringInt);
		}
		
		return 0;
	}

	/**
	 * Check if a file exists. If it does it finds the lowest free number to 
	 * append to make filename unique.
	 * @param target Path to file.
	 * @return String of the lowest number.
	 */
	public static String getNumeralFileAppendix(String target) {
		int i = 1;
		while (true) {
			if (new File(target+"_"+i).exists()) {
				i++;
			} else {
				return Integer.toString(i);
			}
		}
	}
}
