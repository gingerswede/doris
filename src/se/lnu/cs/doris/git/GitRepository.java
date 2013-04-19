package se.lnu.cs.doris.git;

import java.io.File;
import java.util.Iterator;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.WindowCache;
import org.eclipse.jgit.storage.file.WindowCacheConfig;

import se.lnu.cs.doris.global.ExceptionHandler;
import se.lnu.cs.doris.global.GlobalMessages;
import se.lnu.cs.doris.global.OutOfSpaceException;
import se.lnu.cs.doris.global.Utilities;

/**
 * Class to perform repository mining of git repositories.
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
public class GitRepository {
	//Class specific constants
	//Tests showed that it works faster with four threads on a single core processor
	//hence the static four if the available cores are less than 2.
	private static int MAX_NUMBER_OF_THREADS = (Runtime.getRuntime().availableProcessors() < 2) ? 4 : Runtime.getRuntime().availableProcessors();
	
	//Class usage
	private Repository m_headRepository;
	private String m_repoName;
	private String m_localUri;
	private int m_runningThreads = 0;

	//Inputs
	private String m_uri;
	private String m_target;
	private String m_startPoint;
	private String m_endPoint;
	private Boolean m_noLog;
	private int m_limit;

	/**
	 * Constructor taking the uri flag value.
	 * @param uri URI to the git repository's .git file.
	 */
	public GitRepository(String uri) {		
		this(uri, null, null, null, null, false);
	}

	/**
	 * Constructor taking the URI and target flags.
	 * @param uri URI to the git repository's .git file.
	 * @param target Path to target directory.
	 */
	public GitRepository(String uri, String target) {
		this(uri, target, null, null, null, false);
	}

	/**
	 * Constructor taking the URI, target and start point flags.
	 * @param uri URI to the git repository's .git file.
	 * @param target Path to target directory.
	 * @param startPoint SHA-1 checksum of the commit that is the starting point.
	 */
	public GitRepository(String uri, String target, String startPoint) {
		this(uri, target, startPoint, null, null, false);
	}

	/**
	 * Constructor taking the URI, target, start point flags and end point.
	 * @param uri URI to the git repository's .git file.
	 * @param target Path to target directory.
	 * @param startPoint SHA-1 checksum of the commit that is the starting point.
	 * @param endPoint SHA-1 checksum of the commit that is the end point.
	 */
	public GitRepository(String uri, String target, String startPoint,
			String endPoint) {
		this(uri, target, startPoint, endPoint, null, false);
	}

	/**
	 * Constructor taking the URI, target, start point flags and end point.
	 * @param uri URI to the git repository's .git file.
	 * @param target Path to target directory.
	 * @param startPoint SHA-1 checksum of the commit that is the starting point.
	 * @param endPoint SHA-1 checksum of the commit that is the end point.
	 * @param limit Number of commits to be mined.
	 */
	public GitRepository(String uri, String target, String startPoint,
			String endPoint, String limit) {
		this(uri, target, startPoint, endPoint, limit, false);
	}

	/**
	 * Constructor taking the URI, target, start point flags and end point.
	 * @param uri URI to the git repository's .git file.
	 * @param target Path to target directory.
	 * @param startPoint SHA-1 checksum of the commit that is the starting point.
	 * @param endPoint SHA-1 checksum of the commit that is the end point.
	 * @param limit Number of commits to be mined.
	 * @param noLog Boolean to set if a meta-data log should be created or not.
	 */
	public GitRepository(String uri, String target, String startPoint,
			String endPoint, String limit, Boolean noLog) {
		//Get repository name.
		this.m_repoName = this.getRepoNameFromUri(uri);

		//Set target dir and append repository name.
		if (target == null) {
			target = System.getProperty("user.dir");
		}		
		this.m_target = this.appendProjectName(target, uri);
		File file = new File(this.m_target);
		
		if (file.exists()) {
			if (!GlobalMessages.overWriteFolder(this.m_target)) {
				this.m_target += "_" + Utilities.getNumeralFileAppendix(this.m_target);
			} else {
				Utilities.deleteDirectory(file);
			}
		}
		
		//Set all in parameters.
		this.m_uri = uri;
		this.m_noLog = noLog;
		this.m_startPoint = startPoint;
		this.m_endPoint = endPoint;
		this.m_limit = Utilities.parseInt(limit);

		//Set head repository to null.
		this.m_headRepository = null;
	}

	/**
	 * Method to fetch a bare .git repository for local storage.
	 * @throws Exception 
	 */
	public void pullBare() throws Exception {
		String barePath = this.m_target + "/" + this.m_repoName + ".git";
		this.m_localUri = "file://" + this.m_target.replace('\\', '/') + "/" + this.m_repoName + ".git";
		File file = new File(barePath);
		
		if (file.exists()) {
			Utilities.deleteDirectory(file);
		}
		try {
			Git git = Git.cloneRepository()
					.setURI(this.m_uri)
					.setDirectory(new File(this.m_target, this.m_repoName + ".git"))
					.setCloneAllBranches(true)
					.setBare(true)
					.call();
			this.m_headRepository = git.getRepository();
		} catch (Exception e) {
			this.errorHandlingMining(e, null);
		}

	}

	/**
	 * Method to do start mining a repository.
	 * @throws Exception 
	 */
	public void mine() throws Exception {
		//Fetch the bare .git file to continue working locally only.
		this.pullBare();
		RevWalk rw = this.getRevWalk();
		try {
			//Order all commits from first to last.
			AnyObjectId headId = this.m_headRepository.resolve(Constants.HEAD);
			int i = 0;
			rw.sort(RevSort.REVERSE);

			RevCommit root = rw.parseCommit(headId);
			rw.markStart(root);

			Iterator<RevCommit> revs = rw.iterator();

			RevCommit current = rw.parseCommit(revs.next());;
			Boolean startFound = this.m_startPoint == null,
					stopFound = this.m_endPoint == null,
					checkLimit = this.m_limit != 0;
			int limit = 0;

			//TODO: Fix the limit bug.
			while (true) {
				//TODO: implement wait() and notify()
				//while (this.m_runningThreads > MAX_NUMBER_OF_THREADS) {
				//	Thread.sleep(200);
				//}
				
				if (!stopFound && this.m_endPoint != null) {
					if (current.getName().toLowerCase().equals(this.m_endPoint.toLowerCase())) {
						GlobalMessages.miningDone(this.m_repoName);
						break;
					}
				} 
				if (checkLimit && this.m_limit <= limit) {
					GlobalMessages.miningDone(this.m_repoName);
					break;
				} 
				if (!startFound) {
					if (current.getName().toLowerCase().equals(this.m_startPoint.toLowerCase())) {
						startFound = true;
					}
				}
				if (startFound) {
					//Should work, but not the most beautiful solution.
					while (this.m_runningThreads >= MAX_NUMBER_OF_THREADS) {
						try {
							wait();
						} catch (Exception e) {}
					}
					String name = Integer.toString(i);
					File file = new File(this.m_target + "/" + name);

					//If the commit already have been mined we remove it in case it's
					//an older version or another program.
					if (file.exists()) {
						Utilities.deleteDirectory(file);
					}
					new Cloner(current, name, i);

					if (!this.m_noLog) {
						GitLogger.addNode(this.m_target, this.m_repoName, name, current);
					}
					this.m_runningThreads++;
					limit++;
				}
				if (revs.hasNext()) {
					current = rw.parseCommit(revs.next());
					i++;
				} else {
					//Ugly but working to clean up after multiple threads.
					while (this.m_runningThreads > 0) {
						Thread.sleep(200);
					}
					GlobalMessages.miningDone(this.m_repoName);
					break;
				}
			}
		} catch (Exception e) {
			this.errorHandlingMining(e, null);
		}
		
		if (this.m_headRepository != null) {
			WindowCacheConfig cfg = new WindowCacheConfig();
			cfg.setPackedGitMMAP(false);
			WindowCache.reconfigure(cfg);
			
			this.m_headRepository.close();
		}
	}

	/**
	 * Method to clone a commit to a previous state.
	 * @param current Commit information of where to revert to.
	 * @param name Name of the directory.
	 * @param i Order number of the commit.
	 * @throws Exception 
	 */
	@SuppressWarnings("unused") //Save until multithreading fully implemented.
	private void cloneCommit(RevCommit current, String name, int i) throws Exception {	    
		//Clone from local .git file. Increase speed and lower bandwidth need.
		try {
			File mineDir = new File(this.m_target, name);
			Git g = Git.cloneRepository()
					.setURI(this.m_localUri)
					.setDirectory(mineDir)
					.call();
			
			g.reset().setRef(current.getName()).setMode(ResetType.HARD).call();

			if (!this.m_noLog) {
				GitLogger.addNode(this.m_target, this.m_repoName, name, current);
			}
			
			GlobalMessages.commitPulled(i, current.getName());
			
		} catch (Exception e) {
			this.errorHandlingMining(e, current);
		}
	}

	/**
	 * Minimal error handling
	 * @param e Exception thrown.
	 * @param current RevCommit in case one was loaded.
	 * @throws Exception 
	 */
	private void errorHandlingMining(Exception e, RevCommit current) throws Exception {
		if (e.getMessage().contains("not enough space")) {
			throw new OutOfSpaceException(e.getMessage(), (current != null ? current.getName() : "Initial commit"));
		} else {
			throw e;
		}
	}

	/**
	 * Private function to fetch a revision walk from the head repository.
	 * @return RevWalk
	 * @throws Exception 
	 */
	private RevWalk getRevWalk() throws Exception {
		if (this.m_headRepository == null) {
			this.pullBare();
		}			
		return new RevWalk(this.m_headRepository);
	}

	/**
	 * Add project name to target string. Used to create a new directory at the
	 * target directory with the project name.
	 * @param target
	 * @return String
	 */
	public String appendProjectName(String target, String uri) {
		target = target.replace('\\', '/');
		if (target.charAt(target.length() - 1) == '/') {
			target = target.substring(0, target.length() - 1);
		}
		if (target.toLowerCase().endsWith(getRepoNameFromUri(uri).toLowerCase())) {
			return target;
		} else {
			return target + "/" + getRepoNameFromUri(uri);
		}
	}

	/**
	 * Method to extract project name from the URI.
	 * @param uri
	 * @return String
	 */
	public String getRepoNameFromUri(String uri) {
		return uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf(".git"));
	}
	
	/**
	 * Get the target value.
	 * @return String
	 */
	public String getTarget() {
		return this.m_target;
	}

	/**
	 * Get the project name.
	 * @return String
	 */
	public String getProjectName() {
		return this.m_repoName;
	}
	
	/**
	 * Method to clean up after a project have been downloaded.
	 * Due to file locks this need to be performed after the 
	 * mining is done.
	 * @param targetPath Path to the repository just mined.
	 */
	public static void cleanupProject(String targetPath) {
		File baseTarget = new File(targetPath);
		for (File commit : baseTarget.listFiles()) {
			if (Utilities.tryParseInt(commit.getName())) {
				String gitDirPath = commit.getAbsolutePath().replace("\\", "/") + "/.git";
				File gitDir = new File(gitDirPath);
				if (gitDir.exists()) {
					Utilities.deleteDirectory(gitDir);
				}
			}
		}
	}
	
	/**
	 * Class to mine a repository multithreaded.
	 * @author Emil Carlsson
	 *
	 */
	private class Cloner implements Runnable{

		private String m_name;
		private int m_i;
		private RevCommit m_current;
		private Thread m_thread;

		/**
		 * Constructor for a git cloner object. Sets up the entire
		 * object to prepare for mining.
		 * @param current The rev commit to be cloned.
		 * @param name With the name of the directory to clone into.
		 * @param i What number in the order the clone is.
		 * @throws Exception
		 */
		public Cloner(RevCommit current, String name, int i) throws Exception {	    
			this.m_current = current;
			this.m_name = name;
			this.m_i = i;
			this.m_thread = new Thread(this);
			this.m_thread.start();
		}
		
		/**
		 * Private function to start cloning a commit.
		 * @throws Exception
		 */
		private void cloneCommit() throws Exception 	{
			//Clone from local .git file. Increase speed and lower bandwidth need.
			try {
				File mineDir = new File(m_target, this.m_name);
				Git g = null;
				
				WindowCacheConfig cfg = new WindowCacheConfig();
				cfg.setPackedGitMMAP(false);
				WindowCache.reconfigure(cfg);
				
				g = Git.cloneRepository()
						.setURI(m_localUri)
						.setDirectory(mineDir)
						.call();
				
				g.reset().setRef(this.m_current.getName()).setMode(ResetType.HARD).call();
				
				GlobalMessages.commitPulled(this.m_i, this.m_current.getName());
				
				g.getRepository().close();
				
				m_runningThreads--;
				
			} catch (Exception e) {
				errorHandlingMining(e, this.m_current);
			}
		}
		
		@Override
		public void run() {	
			this.guardedCloner();
		}
		
		/**
		 * Function to start cloning in multithread mode.
		 */
		public synchronized void guardedCloner() {
			try {
				this.cloneCommit();
			} catch (Exception e) {
				if (e.getMessage().contains("not enough space")) {
					ExceptionHandler.HandleException(new OutOfSpaceException(e.getMessage(), (this.m_current != null ? this.m_current.getName() : "Initial commit")));
				} else {
					ExceptionHandler.HandleException(e);
				}
			}
			notifyAll();
		}
	}
}
