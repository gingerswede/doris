#Doris
##Table of contents
* [License](#license)
* [About](#about)
	* [Recent changes](#recent-changes)
	* [Coming updates](#coming-updates)
	* [Working JARs](#working-jars)
* [Usage guide](#usage-guide)
	* [Help](#help)
	* [URI](#uri)
	* [Target](#target)
	* [Start point](#start-point) 
	* [End point](#end-point)
	* [Limit](#limit)
	* [No log](#no-log)
	* [Important](#important)
* [Log file](#log-file)
* [JavaDocs](http://gingerswede.github.io/doris/)

##License
Doris is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
Doris is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with Doris.  If not, see <[http://www.gnu.org/licenses/](http://www.gnu.org/licenses/)>.

[Back to top](#table-of-contents)
##About
Doris was created by Emil Carlsson as part of a bachelor thesis about problems encountered when mining software repositories. The main goal of the thesis was to find a mining tool that could handle git, work with as few dependencies as possible, and also provide automated reproducible extraction and measurement pipeline.

[Back to top](#table-of-contents)
###Recent changes
* Fixed bug with not working tags.
* Added parameter object.

[Back to top](#table-of-contents)
###Coming updates
* Working -b flag to specify branch. At the moment only master-branch can be mined.

[Back to top](#table-of-contents)
###Working JARs
Most common on MacOS is 1.6. To find out your JRE type *java -version*.
####JRE 1.7
* [Version 1.1.0](http://gingerswede.github.io/doris/jar/doris-v1.1.0.jar) (Current version)
####JRE 1.6
* [Version 1.1.0](http://gingerswede.github.io/doris/jar/1_6/doris-v1.1.0.jar) (Current version)

[Back to top](#table-of-contents)
##Dependencies
Doris is written in Java and requires Java (JRE 1.7 or newer) to be installed on the computer running it.

[Back to top](#table-of-contents)
##Usage guide
When using parameters and not specifying target directory, Doris will automatically create a directory with the same name as the .git file used for mining. If no parameters are passed to Doris, Doris will prompt for URI to .git file and the target to store the results from the mining.
All flags are to be appended after the command to initialize Doris. When using flags the URI flag must be included as a minimum.
**Notice:** If you have downloaded a working JAR this will have version number appended to it. Then change Doris.jar to doris-vX.Y.Z.jar to run these commands or rename jar to remove version numbering.

Run Doris on Windows:

	C:\> java -jar c:\path\to\doris.jar
	
Run Doris on Unix-like OS:

	$ java -jar doris.jar

[Back to top](#table-of-contents)
###Help
	-h, --help [flag]
Shows help information. If a flag is appended it will show help information of that particular flag.

[Back to top](#table-of-contents)
###URI
	-u, --uri <link to .git-file>
Specifies the URI where the .git file can be found. The protocols that Doris can handle is http(s)://, git:// and file://.  Example of formatting: *git://github.com/GingerSwede/Doris.git*.

[Back to top](#table-of-contents)
###Target
	-t, --target <path to target directory>
Specifies the target where the different commits should be stored. When omitted Doris will use the current working directory and set up a folder named after the .git-file used in the URI.

[Back to top](#table-of-contents)
###Start point
	-s, --startpoint <commit sha-1>
Set a starting point for Doris to start mining the repository from. Full sha-1 is needed. **If the sha-1 value is incorrect the mining will never be started**.

[Back to top](#table-of-contents)
###End point
	-e, --endpoint <commit sha-1>
Set a commit where Doris should stop mining. Full sha-1 is needed. **If the sha-1 value is incorrect the mining will not stop**. The given sha-1 commit will not be included in the mining results.

[Back to top](#table-of-contents)
###Limit
	-l, --limit <max number of commits>
Set a maximum number of commits Doris should mine. Amount is to be given as an integer (e.g., 6, 10, 600). 

[Back to top](#table-of-contents)
###No log
	-n, --nolog
When this flag is passed the logging option in Doris is turned off. This is recommended when mining larger repositories that will generate many commits. All information that is logged by Doris can manually be obtained through the .git-file copied to local access. It can be found in the same directory as the mining results.

[Back to top](#table-of-contents)
###Important
If the -e and the -l flag is used in combination Doris will end on the flags criteria that is reached first.

[Back to top](#table-of-contents)
##Log file
Unless the -n flag is used Doris will automatically log basic information about the different commits in an xml-file. The log contain information about parent commit, author, committer, commit message and commit time (given in UNIX time).
Example:

		<project project_name="ExampleRepository">

			<commit commit_name="08046e7b57f772f270619601d1a9420f76320066" commit_number="0" commit_time="1358168496">

				<author e_mail="john.doe@example.com" name="John Doe"/>

				<committer e_mail="john.doe@example.com" name="John Doe"/>

				<commit_message>

					Initial commit

				</commit_message>

			</commit>

		</project>

[Back to top](#table-of-contents)
