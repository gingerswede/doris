package se.lnu.cs.doris.global;

/**
 * Class containing messages that can be used globally.
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
public class GlobalMessages {
	/**
	 * Get confirmation if a target should be overwritten or not.
	 * @param target Path to file/folder.
	 * @return True if target should be overwritten.
	 */
	public static Boolean overWriteFolder(String target) {
		System.out.format("%s already exists.\nOver write? [y]es/[n]o?: ", target);
		
		String reply = Utilities.scanner.nextLine();
		
		return reply.toLowerCase().startsWith("y");
	}
	
	/**
	 * Reports that a commit have been pulled.
	 * @param id Order of the commit.
	 * @param name Name of the commit.
	 */
	public static void commitPulled(int id, String name) {
		System.out.format("Pulled revision %d (Name: %s)\n", id, name);
	}
	
	/**
	 * Reports that a mining of a repository have been finished.
	 * @param repositoryName Name of the repository.
	 */
	public static void miningDone(String repositoryName) {
		System.out.format("Mining of %s done.\n", repositoryName);
	}

	/**
	 * Information if the computer have ran out of disk space during mining.
	 * Also give information that the mining can be continued after clearing
	 * disk space and informs what commit that the disk ran out at.
	 * @param name Name of the commit that couldn't be mined.
	 */
	public static void outOfSpace(String name) {
		System.out.println("Mining aborted:");
		System.out.println("Out of hard drive space.");
		System.out.println("Free more hard drive space and restart doris using the -e flag.");
		System.out.format("Commit to continue from: %s", name);
	}

	/**
	 * Generic "print error message".
	 * @param message Error message.
	 */
	public static void somethingWentWrong(String message) {
		System.out.println("Oups, something went wrong!");
		System.out.format("Error message:\n%s", message);
	}
}
