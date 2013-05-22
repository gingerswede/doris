package se.lnu.cs.doris.git;

import java.io.File;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import org.eclipse.jgit.revwalk.RevCommit;

import se.lnu.cs.doris.global.GlobalStrings;
/**
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
public class GitLogger {
	
	//Used in function parseXMLUnsafeCharacters().
	//Not my prettiest work, but it had to be fixed fast.
	private final static char[] unsafeChars = {0, 1, 2, 3, 4, 5, 6, 7, 8, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
	
	/**
	 * Method to add an xml-node to the log for the repository.
	 * @param target Path to the mining base dir.
	 * @param repoName Name of the repository.
	 * @param id What number of order the commit is.
	 * @param commit RevCommit of the commit.
	 */
	public static void addNode(String target, String repoName, String id, RevCommit commit) {
		try {
			//Set up the base path to the xml file.
			String path = String.format("%s/%s.xml", target, repoName);
			
			//Instantiate some of the tools needed to build the xml document.
			DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = df.newDocumentBuilder();
			
			//Used to see if the file exists or not.
			File file = new File(path);
			
			Document log;
			Element root;
			
			//If the file doesn't exists a file need to be created
			//and also some root elements etc.
			if (!file.exists()) {
				file.createNewFile();
				log = db.newDocument();
				root = log.createElement(GlobalStrings.PROJECT);
				root.setAttribute(GlobalStrings.PROJECT_NAME, repoName);
				log.appendChild(root);				
			} else { //Load the existing document if it exists.
				log = db.parse(file);
				root = (Element) log.getFirstChild();
			}
			
			//Create a new commit node with attributes.
			Element commitNode = log.createElement(GlobalStrings.COMMIT);
			commitNode.setAttribute(GlobalStrings.COMMIT_NUMBER, id);
			commitNode.setAttribute(GlobalStrings.COMMIT_NAME, commit.getName());
			commitNode.setAttribute(GlobalStrings.COMMIT_TIME, Integer.toString(commit.getCommitTime()));
			
			//Find all parents. If initial commit no parent node is created.
			if (commit.getParentCount() > 0) {
				for (RevCommit rc : commit.getParents()) {
					Element parentNode = log.createElement(GlobalStrings.PARENT);
					parentNode.setAttribute(GlobalStrings.COMMIT_NAME, rc.getName());
					
					commitNode.appendChild(parentNode);
				}
			}
			
			//Populate with other nodes.
			Element authorNode = log.createElement(GlobalStrings.AUTHOR);
			authorNode.setAttribute(GlobalStrings.NAME, commit.getAuthorIdent().getName());
			authorNode.setAttribute(GlobalStrings.E_MAIL, commit.getAuthorIdent().getEmailAddress());
			
			commitNode.appendChild(authorNode);
			
			Element committerNode = log.createElement(GlobalStrings.COMMITTER);
			committerNode.setAttribute(GlobalStrings.NAME, commit.getCommitterIdent().getName());
			committerNode.setAttribute(GlobalStrings.E_MAIL, commit.getCommitterIdent().getEmailAddress());
			
			commitNode.appendChild(committerNode);
			
			Element messageNode = log.createElement(GlobalStrings.COMMIT_MESSAGE);
			//Ugly quick fix to repair broken XML returns.
			byte[] messageByteArray = commit.getFullMessage().getBytes("UTF-8");
			String message = parseXMLUnsafeCharacters(new String(messageByteArray, GlobalStrings.UTF8_CHATSET));
			messageNode.appendChild(log.createTextNode(message));
			
			commitNode.appendChild(messageNode);
			
			root.appendChild(commitNode);
			
			//Write to file.
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			DOMSource source = new DOMSource(log);
			StreamResult result = new StreamResult(path);
			
			t.transform(source, result);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes characters that isn't safe for XML and replaces them
	 * with a single space.
	 * @param s String to be parsed
	 * @return Parsed String.
	 */
	private static String parseXMLUnsafeCharacters(String s) {
		for (char c : unsafeChars) {
			s = s.replace(c, (char) 32);
		}
		
		return s;
	}
}
