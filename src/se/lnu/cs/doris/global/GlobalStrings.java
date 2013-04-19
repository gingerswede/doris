package se.lnu.cs.doris.global;

import java.nio.charset.Charset;

/**
 * Representation of global strings for reusability.
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
 *  
 * @author Emil Carlsson
 * 
 */
public class GlobalStrings {
	public static Charset UTF8_CHATSET = Charset.forName("UTF-8");
	//Log strings
	public static String NAME = "name";
	public static String E_MAIL = "e_mail";
	public static String COMMITTER = "committer";
	public static String AUTHOR = "author";
	public static String COMMIT = "commit";
	public static String COMMIT_TIME = "commit_time";
	public static String COMMIT_NUMBER = "commit_number";
	public static String COMMIT_NAME = "commit_name";
	public static String PROJECT = "project";
	public static String PROJECT_NAME = "project_name";
	public static String PARENT = "parent";
	public static String COMMIT_MESSAGE = "commit_message";
	
	//Flag strings
	public static String LIMIT_SHORT = "-l";
	public static String LIMIT_LONG = "--limit";
	public static String START_POINT_SHORT = "-s";
	public static String START_POINT_LONG = "--startpoint";
	public static String END_POINT_SHORT = "-e";
	public static String END_POINT_LONG = "--endpoint";
	public static String NO_LOG_SHORT = "-n";
	public static String NO_LOG_LONG = "--nolog";
	public static String URI_SHORT = "-u";
	public static String URI_LONG = "--uri";
	public static String TARGET_SHORT = "-t";
	public static String TARGET_LONG = "--target";
	public static String HELP_SHORT = "-h";
	public static String HELP_LONG = "--help";
	public static String MEASURE_SLOC_SHORT = "-m";
	public static String MEASURE_SLOC_LONG = "--metric";
}
