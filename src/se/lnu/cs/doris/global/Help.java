package se.lnu.cs.doris.global;

/**
 * Class to manage console help messages.
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
public class Help {
	
	//Different help and usage messages.
	private static String m_general = 
			"When using parameters and not specifying target directory,\n" +
			"Doris will automatically create a directory with the same\n" +
			"name as the .git file used for mining. If no parameters\n" +
			"are passed to Doris, Doris will prompt for URI to .git file\n" +
			"and the target to store the results from the mining. All\n" +
			"flags are to be appended after the command to initialize\n" +
			"Doris. When using flags the URI flag must be included as a \n" +
			"minimum.\n\n" +
			"Run Doris on *nix:\n " +
			"\temil@linux-computer ~ $ ./dors.jar\n" +
			"Run Doris on Windows:\n" +
			"\tC:\\> java -jar c:\\path \\to\\doris.jar";
	private static String m_help = 
			"Help\n" +
			"\t-h, --help [flag]\n\n" +
			"Shows help information. If a flag is appended it will show\n" +
			"help information of that particular flag.";
	private static String m_uri = 
			"URI\n" +
			"\t-u, --uri <link to .git-file>\n\n" +
			"Specifies the URI where the .git file can be found. The \n" +
			"protocols that Doris can handle is http(s)://, git:// and\n" +
			"file://.\n" +
			"Example of formatting: git://github.com/GingerSwede/Doris.git.";
	private static String m_target = 
			"Target\n"+
			"\t-t, --target <path to target directory>\n" +
			"Specifies the target where the different commits should be \n" +
			"stored. When omitted Doris will use the current working \n" +
			"directory and set up a folder named after the .git-file used\n" +
			"in the URI.";
	private static String m_noLog =
			"No log\n" +
			"\t-n, --nolog\n" +
			"When this flag is passed the logging option in Doris is turned\n" +
			"off. This is recommended when mining larger repositories that\n" +
			"will generate many commits. All information that is logged by\n" +
			"Doris can manually be obtained through the .git-file copied to\n" +
			"local access. It can be found in the same directory as the\n" +
			"mining results named <repository name>.git.";
	private static String m_startPoint =
			"Start point\n" +
			"\t-s, --startpoint <commit sha-1>\n" +
			"Set a starting point for Doris to start mining the repository \n" +
			"from. Full sha-1 is needed. If the sha-1 value is incorrect the\n" +
			"mining will never be started.";
	private static String m_endPoint = 
			"End point\n" +
			"\t-e, --endpoint <commit sha-1>\n" +
			"Set a commit where Doris should stop mining. Full sha-1 is \n" +
			"needed. If the sha-1 value is incorrect the mining will not \n" +
			"stop. The given sha-1 commit will not be included in the \n" +
			"mining results.";
	private static String m_limit =
			"Limit\n" +
			"\t-l, --limit <max number of commits>\n" +
			"Set a maximum number of commits Doris should mine. Amount is to \n" +
			"be given as an integer (e.g., 6, 10, 600).";
	private static String m_metric =
			"Metric\n" +
			"\t-m, --metric <file ending>\n" +
			"WARNING: This flag will be removed!" +
			"Start a simple code metrics analysis tool that compare the percent\n" +
			"of source lines of code, lines of comments and total lines in the\n" +
			"repository.";
	
	/**
	 * Main method to filter out specific help through tags or
	 * if tag is absent prints complete help.
	 * @param topic
	 */
	public static void printHelp(String topic) {
		System.out.println();
		if (topic != null) {
			//For backward compatibility reasons String was not used in Switch.
			InputFlag flag = InputFlag.valueOf(
					(
						topic.startsWith("-") ? 
							topic.substring(topic.lastIndexOf("-") + 1) :
							topic
					).toLowerCase()
				);
			switch(flag) {
			case h:
			case help:
				printHelpHelp();
				break;
			case t:
			case target:
				printTargetHelp();
				break;
			case u:
			case uri:
				printUriHelp();
				break;
			case n:
			case nolog:
				printNoLogHelp();
				break;
			case s:
			case startpoint:
				printStartPointHelp();
				break;
			case e:
			case endpoint:
				printEndPointHelp();
				break;
			case l:
			case limit:
				printLimitHelp();
				break;
			case m:
			case metric:
				printMetricHelp();
				break;
			default: //If a topic can't be found.
				printFullHelp();
				break;
			}
		} else { //If no topic is stated print all help messages.
			printFullHelp();
		}
	}
	
	/**
	 * Print help information of all flags.
	 */
	public static void printFullHelp() {
		System.out.println(m_general);
		System.out.println();
		printHelpHelp();
		printUriHelp();
		printTargetHelp();
		printNoLogHelp();
		printStartPointHelp();
		printEndPointHelp();
		printLimitHelp();
		printMetricHelp();
	}

	/**
	 * Print help information of the help flag.
	 */
	public static void printHelpHelp() {
		System.out.println(m_help);
		System.out.println();
	}
	
	/**
	 * Print help information of the target flag.
	 */
	public static void printTargetHelp() {
		System.out.println(m_target);
		System.out.println();
	}

	/**
	 * Print help information of the URI flag.
	 */
	public static void printUriHelp() {
		System.out.println(m_uri);
		System.out.println();
	}

	/**
	 * Print help information of the no log flag.
	 */
	public static void printNoLogHelp() {
		System.out.println(m_noLog);
		System.out.println();
	}

	/**
	 * Print help information of the start point flag.
	 */
	public static void printStartPointHelp() {
		System.out.println(m_startPoint);
		System.out.println();
	}

	/**
	 * Print help information of the end point flag.
	 */
	public static void	printEndPointHelp() {
		System.out.println(m_endPoint);
		System.out.println();
	}

	/**
	 * Print help information of the limit flag.
	 */
	public static void printLimitHelp() {
		System.out.println(m_limit);
		System.out.println();
	}
	
	/**
	 * Print help information of the metric flag.
	 */
	public static void printMetricHelp() {
		System.out.println(m_metric);
		System.out.println();
	}
}
