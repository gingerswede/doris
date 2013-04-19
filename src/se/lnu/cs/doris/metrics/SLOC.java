package se.lnu.cs.doris.metrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import se.lnu.cs.doris.global.Utilities;

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
public class SLOC {

	private File m_mainDir;
	private final String m_avoid = ".git";
	private int m_baseValueTotal = -1;
	private int m_baseValueCode = -1;
	private int m_baseValueComments = -1;
	private String m_projectName;
	private String[] m_fileEndings;
	
	public SLOC(String path, String[] fileEndings, String projectName) {
		this(new File(path), fileEndings, projectName);
	}
	
	public SLOC(File dir, String[] fileEndings, String projectName) {
		this.m_mainDir = dir;
		this.m_projectName = projectName;
		this.m_fileEndings = fileEndings;
	}
	
	public void generatePNG() throws Exception {
		this.generateImage(ImageType.PNG);
	}
	
	public void generateJPG() throws Exception {
		this.generateImage(ImageType.JPEG);
	}
	
	private void generateImage(ImageType type) throws Exception {
		if (this.m_mainDir == null) {
			throw new Exception("Base directory not set.");
		}
		
		XYSeries linesOfCodeTotal= new XYSeries("Total lines");
		XYSeries linesOfCode = new XYSeries("Lines of code");
		XYSeries linesOfComments = new XYSeries("Lines of comments");
		XYSeries baseLine = new XYSeries("index 100");
		
		for (File f : this.m_mainDir.listFiles()) {
			if (f.isDirectory() && !f.getName().contains(this.m_avoid)) {
				
				int commitNumber = Utilities.parseInt(f.getName());
				int slocd = 0;
				int slocmt = 0;
				int sloct = 0;
				
				for (File sd : f.listFiles()) {
					if (!sd.getName().toLowerCase().contains(this.m_avoid)) {
						slocd += this.countLines(sd, false);
						slocmt += this.countLines(sd, true);
						sloct += slocd + slocmt;
					}
				}
				
				if (this.m_baseValueTotal < 0) {
					this.m_baseValueTotal = sloct;
					this.m_baseValueComments = slocmt;
					this.m_baseValueCode = slocd;
					
					sloct = 100;
					slocmt = 100;
					slocd = 100;
				} else {
					sloct = (int)((double)sloct/(double)this.m_baseValueTotal * 100);
					slocmt = (int)((double)slocmt/(double)this.m_baseValueComments * 100);
					slocd = (int)((double)slocd/(double)this.m_baseValueCode * 100);
				}
				
				linesOfCodeTotal.add(commitNumber, sloct);
				linesOfCode.add(commitNumber, slocd);
				linesOfComments.add(commitNumber, slocmt);
				baseLine.add(commitNumber, 100);
				
			}
		}
		
		XYSeriesCollection collection = new XYSeriesCollection();
		
		collection.addSeries(linesOfCodeTotal);
		collection.addSeries(linesOfCode);
		collection.addSeries(linesOfComments);
		collection.addSeries(baseLine);
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Source lines of code change for " + this.m_projectName + " \nBase value code: " + this.m_baseValueCode + "\nBase value comments: " + this.m_baseValueComments,
				"Commit",
				"SLOC change %",
				collection,
				PlotOrientation.VERTICAL,
				true,
				true,
				false
			);
		
		XYPlot plot = chart.getXYPlot();
		
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();		
		xAxis.setTickUnit(new NumberTickUnit(2));
		
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();		
		yAxis.setTickUnit(new NumberTickUnit(10));
		
		switch (type) {
		case JPEG:
			ChartUtilities.saveChartAsJPEG(new File(this.m_mainDir.getAbsolutePath(), "sloc_chart.jpg"), chart, 1000, 720);
			break;
		case PNG:
			ChartUtilities.saveChartAsPNG(new File(this.m_mainDir.getAbsolutePath(), "sloc_chart.png"), chart, 1000, 720);
			break;
		default:
			break;
		}
	}

	//TODO: Find decent api for getting sloc.
	private int countLines(File file, Boolean countComments) throws Exception {
		int sloc = 0;
		
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				sloc += this.countLines(f, countComments);
			}
		} else {
			
			Boolean readFile = true;
			
			if (this.m_fileEndings == null) {
				readFile = true;
			} else {
				for (String s : this.m_fileEndings) {
					if (file.getName().endsWith(s)) {
						readFile = true;
						break;
					} else {
						readFile = false;
					}
				}
			}
			
			if (readFile) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				Boolean isEOF = true;

				Boolean isComment;
				Boolean isBlankLine;
				Boolean inMultiLineComment = false;
				Boolean prevMultiLineComment = inMultiLineComment;
				
				do {
					String t = br.readLine();
					
					if (t != null) {
						isComment = this.lineIsComment(t);
						isBlankLine = t.trim().equals("");
						prevMultiLineComment = inMultiLineComment;
						inMultiLineComment = this.resolveMultiLineComment(t, inMultiLineComment);
						
						isEOF = false;
						if (!isBlankLine && (!countComments ? 
								!isComment && !prevMultiLineComment :
								isComment && prevMultiLineComment
							)) {
							sloc++;
						}
					} else {
						isEOF = true;
					}
					
				} while (!isEOF);
				
				br.close();
			}
		}
		
		return sloc;
	}
	
	private Boolean lineIsComment(String line) {
		return (line.trim().startsWith("//") || (line.trim().startsWith("/*") && line.contains("*/")));
	}
	
	private Boolean resolveMultiLineComment(String line, Boolean inCommentBlock) {
		return (line.trim().startsWith("/*") || !line.contains("*/"));
	}
}
