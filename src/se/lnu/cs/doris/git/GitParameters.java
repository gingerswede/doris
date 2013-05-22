package se.lnu.cs.doris.git;

import se.lnu.cs.doris.global.ExceptionHandler;
import se.lnu.cs.doris.global.InputFlag;
import se.lnu.cs.doris.global.Utilities;
import se.lnu.cs.doris.main.Flags;

/**
 * Class to create an abstract layer of the different parameters used by GitRepository.
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
public class GitParameters {
	private String m_target = null;
	private String m_uri = null;
	private String m_branch = null;
	private String m_startPoint = null;
	private String m_endPoint = null;
	private int m_limit = 0;
	private Boolean m_noLog = false;
	
	/**
	 * Empty constructor.
	 */
	public GitParameters() {
		this.m_target = null;
		this.m_uri = null;
		this.m_branch = null;
		this.m_startPoint = null;
		this.m_endPoint = null;
		this.m_limit = 0;
		this.m_noLog = false;
	}
	
	/**
	 * Constructor that takes String array as parameter to populate the object.
	 * @param args Arguments following the flag convention for Doris.
	 * @throws Exception
	 */
	public GitParameters(String[] args) {
		if (args != null) {
			this.populate(args);
		}
	}
	
	/**
	 * Function to populate the object without the flags array.
	 * Internally calls for populate(String[], String[]).
	 * @param args Arguments following the flag convention for Doris.
	 * @throws Exception
	 */
	public void populate(String[] args) {
		this.populate(Flags.getFlags(args), args);
	}
	
	/**
	 * Function to populate the objects different parameters.
	 * @param flags Flags used in the args String[]
	 * @param args Arguments matching the flag convention for Doris.
	 * @throws Exception
	 */
	public void populate(String[] flags, String[] args)  {
		for (String flag : flags) {
			
			InputFlag inputFlag = null;
			try {
				inputFlag = InputFlag.valueOf(flag.replace("-", "").toLowerCase());
			} catch (Exception e) {
				ExceptionHandler.unknownFlag(flag);
			}
			
			String prefix = (inputFlag.name().length() > 1) ? "--" : "-";
			
			//Add new parameters here to make use of them. Should only be used
			//as a core functionality parameter list.
			if (inputFlag == InputFlag.u || inputFlag == InputFlag.uri) {
				this.setUri(Flags.getFlagValue(args, prefix + inputFlag.name()));
			} else if (inputFlag == InputFlag.t || inputFlag == InputFlag.target) {
				this.setTarget(Flags.getFlagValue(args, prefix + inputFlag.name()));
			} else if (inputFlag == InputFlag.s || inputFlag == InputFlag.startpoint) {
				this.setStartPoint(Flags.getFlagValue(args, prefix + inputFlag.name()));
			} else if (inputFlag == InputFlag.e || inputFlag == InputFlag.endpoint) {
				this.setEndPoint(Flags.getFlagValue(args, prefix + inputFlag.name()));
			} else if (inputFlag == InputFlag.l || inputFlag == InputFlag.limit) {
				String limit = Flags.getFlagValue(args, prefix + inputFlag.name());
				
				if (Utilities.tryParseInt(limit))
					this.setLimit(Utilities.parseInt(limit));
				
				else
					this.setLimit(0);
			} else if (inputFlag == InputFlag.n || inputFlag == InputFlag.nolog) {
				this.setLogStatus(true);
			} else if (inputFlag == InputFlag.b || inputFlag == InputFlag.branch) {
				//NOTICE: This functionality have not been added yet. But the parameter exists.
				//The parameter will do nothing in practice.
				this.setBranch(Flags.getFlagValue(args, prefix + inputFlag.name()));
			}
		}
	}
	
	public Boolean haveUri() {
		return (this.m_uri != null);
	}

	public String getTarget() {
		return m_target;
	}
	public void setTarget(String m_target) {
		this.m_target = m_target;
	}
	
	public String getUri() {
		return m_uri;
	}
	public void setUri(String uri) {
		m_uri = uri;
	}
	
	public String getStartPoint() {
		return m_startPoint;
	}
	public void setStartPoint(String startPoint) {
		m_startPoint = startPoint;
	}
	
	public String getEndPoint() {
		return m_endPoint;
	}
	public void setEndPoint(String endPoint) {
		m_endPoint = endPoint;
	}
	
	public int getLimit() {
		return m_limit;
	}
	public void setLimit(int limit) {
		m_limit = limit;
	}
	
	public Boolean getLogStatus() {
		return m_noLog;
	}
	public void setLogStatus(Boolean logStatus) {
		m_noLog = logStatus;
	}

	public String getBranch() {
		return this.m_branch;
	}
	
	public void setBranch(String branch) {
		this.m_branch = branch;
	}
}
