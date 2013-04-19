package se.lnu.cs.doris.global;

/**
 * Exception class to manage out of space problems that might occur when mining.
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
public class OutOfSpaceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String m_commit;

	public OutOfSpaceException(String message, String commit) {
		super(message);
		this.m_commit = commit;
	}
	
	public String getCommit() {
		return this.m_commit;
	}
}
