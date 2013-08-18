package se.lnu.cs.doris.main;

import se.lnu.cs.doris.git.GitRepository;
import se.lnu.cs.doris.global.ExceptionHandler;
import se.lnu.cs.doris.global.GlobalStrings;
import se.lnu.cs.doris.global.Help;
import se.lnu.cs.doris.global.Utilities;
import se.lnu.cs.doris.git.GitParameters;

//This package in added to show how to include external
//packages to Doris.
import com.gingerswede.source.metrics.SLOC;

/**
 * 
 * @author Emil Carlsson
 * 
 *         This file is a part of Doris
 * 
 *         Doris is free software: you can redistribute it and/or modify it
 *         under the terms of the GNU General Public License as published by the
 *         Free Software Foundation, either version 3 of the License, or (at
 *         your option) any later version. Doris is distributed in the hope that
 *         it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *         warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *         the GNU General Public License for more details.
 * 
 *         You should have received a copy of the GNU General Public License
 *         along with Doris. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
public class Main {

	/**
	 * Entry point of Doris.
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		String target = null, uri = null,  projectPath = null, projectName = null, cwd = System.getProperty("user.dir");
		String[] metricsFiles = null;
		Boolean metrics = false;
		GitParameters parameters = new GitParameters();
		
		
		// Check if the help flag is used.
		if (Flags.needHelp(args)) {
			Help.printHelp(Flags.getFlagValue(args, GlobalStrings.HELP_SHORT));
		} else { // If no parameters are given the uri and target is asked for.
			if (args.length < 1) {

				System.out.println("Please enter uri to .git file: ");
				uri = Utilities.scanner.nextLine().trim();
				System.out
						.format("Please enter target directory (leave blank for %s):\n",
								cwd);
				target = Utilities.scanner.nextLine().trim();

				target = (target.trim().isEmpty()) ? null : target;
				
				parameters.setUri(uri);
				parameters.setTarget(target);
				
			} else { 
				try {
					parameters = new GitParameters(args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//Example of how to add "hooked" parameters.
				metricsFiles = (metrics) ? Flags.getMetricsFiles(args) : null;
			}

			if (Flags.validateUri(parameters.getUri())) { // Check if the URI is valid.

				GitRepository gr = new GitRepository(
							parameters.getUri(),
							parameters.getTarget(),
							parameters.getBranch(),
							parameters.getStartPoint(),
							parameters.getEndPoint(),
							parameters.getLimit(),
							parameters.getLogStatus()
						);
				try {

					projectPath = gr.getTarget();
					projectName = gr.getProjectName();

					System.out
							.println("Initializing mining of "
									+ projectName
									+ "\n(This may take a while, go grab a cup of coffee)");

					gr.mine();

					gr = null;

					/*
					 * Doris is done with all mining beyond this point.
					 * Beneth here hooks can be added and external programs
					 * can be called for via an API.
					 */

					if (metrics) { //Example of external tool usage.
						System.out.println("Generating metrics, please wait.");

						SLOC sloc = new SLOC(projectPath, metricsFiles,
								projectName);
						sloc.generateCSV();

						System.out
								.println("Metrics generated, cvs file can be found in:\n"
										+ projectPath);
					}

				} catch (Exception e) {
					ExceptionHandler.HandleException(e);
				}
			} else { // Print help information of the URI flag if URI is
						// invalid.
				Help.printUriHelp();
			}
		} // Close static scanner.
		Utilities.scanner.close();
	}
}
