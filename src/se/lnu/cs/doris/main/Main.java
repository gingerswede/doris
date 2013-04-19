package se.lnu.cs.doris.main;

import java.util.Arrays;

import se.lnu.cs.doris.git.*;
import se.lnu.cs.doris.global.ExceptionHandler;
import se.lnu.cs.doris.global.GlobalStrings;
import se.lnu.cs.doris.global.Help;
import se.lnu.cs.doris.global.Utilities;
import se.lnu.cs.doris.metrics.SLOC;

/**
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
public class Main {
	
	/**
	 * Entry point of Doris.
	 * @param args
	 */
	public static void main(String[] args) {
		String target = null, 
				uri = null, 
				limit = null, 
				endPoint = null, 
				startPoint = null,
				projectPath = null,
				projectName = null,
				cwd = System.getProperty("user.dir");
		String[] metricsFiles = null;
		Boolean noLog = false,
				metrics = false;
		
		//Check if the help flag is used.
		if (Flags.needHelp(args)) {
			Help.printHelp(Flags.getFlagValue(args, GlobalStrings.HELP_SHORT));
		} else { //If no parameters are given the uri and target is asked for.
			if (args.length < 1) {
				
				System.out.println("Please enter uri to .git file: ");
				uri = Utilities.scanner.nextLine().trim();
				System.out.format("Please enter target directory (leave blank for %s):\n", cwd);
				target = Utilities.scanner.nextLine().trim();
				
				target = (target.trim().isEmpty()) ? null : target;
			} else { //Fetch all parameters from the parameter array.
				target = Flags.getFlagValue(args, GlobalStrings.TARGET_SHORT);
				uri = Flags.getFlagValue(args, GlobalStrings.URI_SHORT);
				limit = Flags.getFlagValue(args, GlobalStrings.LIMIT_SHORT);
				endPoint = Flags.getFlagValue(args, GlobalStrings.END_POINT_SHORT);
				startPoint = Flags.getFlagValue(args, GlobalStrings.START_POINT_SHORT);
				noLog = Arrays.asList(args).contains(GlobalStrings.NO_LOG_SHORT);
				metrics = Arrays.asList(args).contains(GlobalStrings.MEASURE_SLOC_SHORT);
				metricsFiles = (metrics) ? Flags.getMetricsFiles(args) : null;
			}
			
			if (Flags.validateUri(uri)) { //Check if the URI is valid.
				if (Flags.validateTarget(target)) { //Check if target is valid.
					GitRepository gr = new GitRepository(uri, target, startPoint, endPoint, limit, noLog);
					try {

						projectPath = gr.getTarget();
						projectName = gr.getProjectName();
						
						System.out.println("Initializing mining of " + projectName + "\n(This may take a while, go grab a cup of coffee)");
						
						gr.mine();
						
						gr = null;
						
						System.out.println("Cleaning up mined directories.");

						Thread.sleep(2000);
						System.gc();
						
						//Clean up the .git dirs for disk storage.
						//TODO:Some .pack files are not released.
						//Tried using a loop to close and delete them while
						//mining. They still refuse to close and Doris get
						//stuck in a bad loop.
						GitRepository.cleanupProject(projectPath);
						
						System.out.println("Clean up done.");
						
						//Flag is bound to be removed within a short time
						//No good code metrics API found to use like this.
						if (metrics) {
							System.out.println("Generating metrics, please wait.");
							
							SLOC sloc = new SLOC(projectPath, metricsFiles, projectName);
							sloc.generatePNG();
							
							System.out.println("Metrics generated, images can be found in:\n" + projectPath);
						}
						
					} catch (Exception e) {
						ExceptionHandler.HandleException(e);
					}
				} else { //Print help information of the target flag if target parameter is invalid.
					Help.printTargetHelp();
				}
			} else { //Print help information of the URI flag if URI is invalid.
				Help.printUriHelp();
			}
		} //Close static scanner.
		Utilities.scanner.close();
	}
}
