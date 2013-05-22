package se.lnu.cs.doris.global;

/**
 * Rudimentary exception handler class.
 * 
 * TODO: Arrange for a more complex and better error handling.
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

public class ExceptionHandler {
	public static void HandleException(Exception e) {
		
		if (e.getClass() == OutOfSpaceException.class) {
			OutOfSpaceException oos = (OutOfSpaceException) e;
			GlobalMessages.outOfSpace(oos.getCommit());
		} else {
			GlobalMessages.somethingWentWrong(e.getMessage());
		}

		Runtime.getRuntime().halt(0);
	}

	public static void unknownFlag(String flag) {
		System.out.println("Unknown flag: " + flag);
		System.out.println("Flag ignored.");
	}
}
