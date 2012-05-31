[![Build Status](https://secure.travis-ci.org/OpenSpaces/PetClinic.png)](http://travis-ci.org/OpenSpaces/PetClinic)

<h2>General</h2>

<p>This example is an implementation of the Spring PetClinic application based on GigaSpaces as the backbone instead of a relational database. It is a reference implementation rather than a fully optimized production application.<br/>
It is consisted of a web application that stores the pet clinic information into the space (by implementing the <tt>Clinic</tt> DAO interface which accesses a relational database in the original petclinic), a space (partitioned-sync2backup, 2 primaries and two backup) and a mirror service to which the space is connected and saves data asynchronously. The mirror service connected to a MySql database and stores information into it using hibernate.<br/>
The following presentation provides an overview of the migration process from JEE to GigaSpaces XAP and discusses the differences between the two platforms, using the PetClinic application as a basis for comparison.</p>
<a style="font:14px Helvetica,Arial,Sans-serif;display:block;margin:12px 0 3px 0;text-decoration:underline;" href="http://www.slideshare.net/uri1803/porting-spring-petclinic-to-gigaspaces-presentation?type=powerpoint" title="Porting Spring PetClinic to GigaSpaces">Porting Spring PetClinic to GigaSpaces</a>

<h2><a name="GigaSpacesPetClinic-ModificationstotheOriginalPetClinic"></a>Modifications to the Original PetClinic</h2>

<p>A few modifications were made to adjust the application to GigaSpaces:</p>
<ul>
  <li>The <tt>Clinic</tt> interface has two implementation on top of GigaSpaces:
	<ul>
		<li>Naive implementation, made using direct space calls</li>
		<li>Optimized implementation, using GigaSpaces executors, that minimizes the network calls instead of calling the space more than once<br/>
for each operation</li>
	</ul>
	</li>
	<li>Some changes have been made to the domain model since GigaSpaces does not support object relationships out of the box. In addition, The space does not have an integer-based id generator (only String based), so one was implemented to preserve the applications semantics.</li>
</ul>


<h2><a name="GigaSpacesPetClinic-Prerequisites"></a>Prerequisites</h2>

<p>You should have the following installed on your machine for the demo to run work properly:</p>
<ul>
	<li><a href="http://www.gigaspaces.com/LatestProductVersion" rel="nofollow">GigaSpaces XAP 6.6</a> or higher (with web container support). Please note that this example only supports Java 1.5, so you cannot use Java 1.4 with it. Make sure to download GigaSpaces XAP 6.6 for Java 1.5 or higher</li>
	<li><a href="http://dev.mysql.com/downloads/connector/j/5.1.html" rel="nofollow">MySql JDBC driver</a></li>
	<li><a href="http://dev.mysql.com/downloads/mysql/5.1.html" rel="nofollow">MySql 5.1 database server</a></li>
</ul>


<h2><a name="GigaSpacesPetClinic-Modules"></a>Modules</h2>

<p>The application contains the following modules, each has its own Eclipse project and IntelliJ iml file (You need to have the following path variables in either IDE for you're project to compile correctly within it: GS_HOME - points to GigaSpacesXAP home, SPRING25_HOME - points to the Spring framework 2.5.4 (or higher) distribution, MYSQL_DRIVER_JAR - points to the MySql driver jar. Note that in Eclipse you will have to define the user libraries):</p>
<ul>
	<li>space-pu: contains the space to which the data is saved and an initialization service for the space based id generator.</li>
	<li>mirror: contains the mirror service to which the space. The space replicates changes to the domain model asynchronously to be saved in the database. The database is MySql 5.1, and should be initialized with the provided SQL script (see below).</li>
	<li>common: contains classes shared by the PUs and web application, namely the domain model classes <tt>Clinic</tt> DAO interface.</li>
	<li>webapp: contains the web application, including all web related resources.</li>
</ul>


<p><a name="GigaSpacesPetClinic-builddeploy"></a> </p>
<h2><a name="GigaSpacesPetClinic-BuildandDeployment"></a>Build and Deployment</h2>

<p>If you wish not to build the project yourself, you should download the binary distribution of each module's processing unit <a href="http://www.openspaces.org/display/DAE/Project+Downloads" rel="nofollow">here</a>. <br/>
Otherwise, you will have to download and install <a href="http://s3.amazonaws.com/dist.springframework.org/release/SPR/spring-framework-2.5.5-with-dependencies.zip" rel="nofollow">Spring framework 2.5.5 (with dependencies)</a>. If you have a higher version of Spring it's also fine, just make sure to update the Spring jar if you're using GigaSpaces 6.6.0 build 2601 (which is bundled with Spring 2.5.4). The jar is located under <tt>lib/spring</tt> in the GigaSpaces distribution. </p>
<div class='panelMacro'><table class='noteMacro'><colgroup><col width='24'><col></colgroup><tr><td valign='top'><img src="/images/icons/emoticons/warning.gif" width="16" height="16" align="absmiddle" alt="" border="0"></td><td><b>Use the appropriate Spring distribution</b><br />It's important to download The Spring distribution <b>with dependencies</b>, as the build relies on the 3rd party jars included with this distribution. This project has been tested with Spring 2.5.5, which can be downloaded <a href="http://s3.amazonaws.com/dist.springframework.org/release/SPR/spring-framework-2.5.5-with-dependencies.zip" rel="nofollow">here</a>. Since Spring is fully backwards-compatible, you may also use version 2.5.6, which can be downloaded <a href="http://s3.amazonaws.com/dist.springframework.org/release/SPR/spring-framework-2.5.6-with-dependencies.zip" rel="nofollow">here</a></td></tr></table></div>

<p>The example uses ant as its build tool and uses a standard build.xml file. To run the build script, use the provided <tt>build.sh/bat</tt> file.<br/>
Running the build script with no parameters within the current directory will launch the <tt>all</tt> task, which will build all the packages. The build depends on GigaSpacesXAP6.6, Spring 2.5.4 (or higher) and MySql JDBC driver libraries. To run the build script simply <tt>cd</tt> to the example's directory and call <tt>build.sh/bat</tt>. </p>
<div class='panelMacro'><table class='noteMacro'><colgroup><col width='24'><col></colgroup><tr><td valign='top'><img src="/images/icons/emoticons/warning.gif" width="16" height="16" align="absmiddle" alt="" border="0"></td><td><b>Change setenv.sh/bat</b><br />
<p>Make sure to edit the setenv.sh/bat file in the root of the project to provide the locations of GigaSpaces6.6 XAP, Spring framework 2.5.4 and MySql Driver locations.<br/>
Also, make sure to validate the JDBC credentials under <tt>common/src/jdbc.properties</tt>.</p></td></tr></table></div>

<p>The output of the build is the mirror and space PUs and the web application war file.<br/>
(As mentioned above, these are also available as a separate download <a href="http://www.openspaces.org/display/DAE/Project+Downloads" rel="nofollow">here</a> if you don't want to build the project yourself).</p>
<div class='panelMacro'><table class='noteMacro'><colgroup><col width='24'><col></colgroup><tr><td valign='top'><img src="/images/icons/emoticons/warning.gif" width="16" height="16" align="absmiddle" alt="" border="0"></td><td><b>Initialize the database</b><br />
<p>Before you start the application, you will need to initialize the MySql database with the provided SQL script (located under the common directory in the example).<br/>
This is typically done by calling:<br/>
<tt>mysql &lt; [Example Root]/common/createDB.sql</tt></p></td></tr></table></div>
<p>In order to deploy the application onto the Service Grid, a GSM and two or three GSCs will need to be started (note, we need two GSCs because of the SLA defined within the space-pu module). Also note that you can use the new GigaSpaces Agent available in version 7.0. <br/>
Once you start the agent, it will start all the required components locally. This is done by calling <tt>&lt;GigaSpaces root&gt;/bin/gs-agent.sh(bat)</tt>. <br/>
To deploy the application, you can run the <tt>deploy</tt> task in the build script by typing <tt>build.sh(bat) deploy</tt>. This will deploy the mirror, the space-pu and the web application. <br/>
Alternatively, you can deploy the application manually: First deploy the mirror, then deploy the space-pu and last deploy the generated war file using the UI for example. <br/>
Some ways to play with the demo can be:</p>
<ol>
	<li>Start another GSC and relocate (drag and drop in the GS-UI) the web application / one of the spaces to the the new GSC.</li>
	<li>Kill one of the GSC that runs the web application processing unit. The web application will be started on one of the remaining GSCs.</li>
</ol>


<p>Note that you can also run the processing units from within your IDE using the <a href="http://www.gigaspaces.com/wiki/display/XAP66/Integrated+Processing+Unit+Container" rel="nofollow">IntegratedProcessingUnitContainer</a>. </p>