package se.lnu.cs.doris.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import se.lnu.cs.doris.global.GlobalStrings;

/**
 * Class to handle input flags passed to the program.
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
public class Flags {
	//Regular expressions used to validate git uri and target.
	private static String m_uriRegExp = "^(git|http(s?)|file)://.*\\.git";
	private static String m_targetRegExp = "^([a-zA-Z]:(\\\\|/)|/)";
	
	/**
	 * Extracts the value following a flag.
	 * @param args String array containing flags and values.
	 * @param i Possition of the value.
	 * @return The value.
	 */
	private static String extractValue(String[] args, int i) {
		try {
			return args[i];
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Checks if the -h or --help flag have been used.
	 * @param args String array containing all parameters.
	 * @return True if flag is used.
	 */
	public static Boolean needHelp(String[] args) {
		return (Arrays.asList(args).contains(GlobalStrings.HELP_SHORT) || 
				Arrays.asList(args).contains(GlobalStrings.HELP_LONG)) ? true : false;
	}

	/**
	 * Returns the value of a flag.
	 * @param args String array with all parameters.
	 * @param match What flag to search for.
	 * @return Value paired with the flag.
	 */
	public static String getFlagValue(String[] args, String match) {		
		if (args.length > 1) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].toLowerCase().equals(match)) {
					return extractValue(args, i+1);
				}
			}
		} 		
		return null;
	}
	
	public static String[] getFlags(String[] args) {
		ArrayList<String> flags = new ArrayList<>();
		
		for (String arg : args) {			
			if (arg.trim().startsWith("-")) {
				flags.add(arg);
			}
		}
		
		String[] flagArray = new String[flags.size()];
		
		flagArray = flags.toArray(flagArray);
		
		return flagArray;
	}
	
	/**
	 * Validates a git URI.
	 * @param uri URI string to validate.
	 * @return True if valid URI
	 */
	public static Boolean validateUri(String uri) {
		return Pattern.compile(m_uriRegExp).matcher(uri).matches();
	}
	
	/**
	 * Validates target directory.
	 * Can handle both Windows and UNIX style paths
	 * @param target String of the path.
	 * @return True if valid path.
	 */
	public static Boolean validateTarget(String target) {
		return (target == null || Pattern.compile(m_targetRegExp).matcher(target).matches());
	}

	public static String[] getMetricsFiles(String[] args) {
		String[] files;
		String metricFlag = getFlagValue(args, GlobalStrings.MEASURE_SLOC_SHORT);
		
		if (!metricFlag.startsWith("-")) {
			if (metricFlag.contains(",")) 
				files = metricFlag.split(",");
			else
				files = new String[] { metricFlag };
		} else {
			files = null;
		}
		
		return files;
	}
}