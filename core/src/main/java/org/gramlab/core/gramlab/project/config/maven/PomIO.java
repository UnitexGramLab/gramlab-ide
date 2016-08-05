package org.gramlab.core.gramlab.project.config.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.process.commands.CompressCommand;
import org.gramlab.core.umlv.unitex.process.commands.Grf2Fst2Command;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PomIO {

	public final static String PROJECT_START_TAG="<project xmlns=\"http://maven.apache.org/POM/4.0.0\" "
		                    +"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
		                    +"xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 "
		                    +"http://maven.apache.org/maven-v4_0_0.xsd\">";
	public final static String PROJECT_END_TAG="</project>";
	public final static String MODEL_VERSION_TAG="\t<modelVersion>4.0.0</modelVersion>";

	public static final String SOURCE_DIRECTORY = "src";
	public static final String DEPENDENCY_DIRECTORY = "dep";
	public static final String TARGET_DIRECTORY = "target";
	public static final String TARGET_PREPROCESS_DIRECTORY = TARGET_DIRECTORY+File.separatorChar+"Preprocessing";
	
	
	public static boolean savePom(Pom pom,GramlabProject project) {
		try {
			FileOutputStream f=new FileOutputStream(pom.getFile());
			PrintStream p=new PrintStream(f,true,"UTF-8");
			p.println(PROJECT_START_TAG);
			p.println(MODEL_VERSION_TAG);
			p.println();
			p.println("<groupId>"+pom.getArtifact().getGroupId()+"</groupId>");
			p.println("<artifactId>"+pom.getArtifact().getArtifactId()+"</artifactId>");
			p.println("<version>"+pom.getArtifact().getVersion()+"</version>");
			p.println();
			saveDistributionManagement(p,pom);
			inheritsGramlabSuperPom(p);
			savePlugins(p,project);
			p.println();
			saveDependencies(p,pom);
			p.println(PROJECT_END_TAG);
			p.println();
			p.close();
			f.close();
			return true;
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
	}

	private static void saveDistributionManagement(PrintStream p, Pom pom) {
		if (pom.getIdRepoRelease()==null) {
			/* Nothing to do if there is no info to save */
			return;
		}
		p.println("<distributionManagement>");
		p.println("\t<repository>");
		p.println("\t\t<id>"+pom.getIdRepoRelease()+"</id>");
		p.println("\t\t<name>"+pom.getIdRepoRelease()+"</name>");
		p.println("\t\t<url>"+pom.getUrlRepoRelease()+"</url>");
		p.println("\t</repository>");
		p.println("\t<snapshotRepository>");
		p.println("\t<uniqueVersion>false</uniqueVersion>");
		p.println("\t\t<id>"+pom.getIdRepoSnapshot()+"</id>");
		p.println("\t\t<name>"+pom.getIdRepoSnapshot()+"</name>");
		p.println("\t\t<url>"+pom.getUrlRepoSnapshot()+"</url>");
		p.println("\t</snapshotRepository>");		
		p.println("</distributionManagement>");
		p.println();
	}

	private static void saveDependencies(PrintStream p, Pom pom) {
		ArrayList<Artifact> list=pom.getDependencies();
		if (list.isEmpty()) return;
		p.println("\t<dependencies>");
		for (Artifact a:list) {
			saveDependency(p,a);
		}
		p.println("\t</dependencies>");
		p.println();
	}

	private static void saveDependency(PrintStream p, Artifact a) {
		p.println("\t\t<dependency>");
		p.println("\t\t\t<groupId>"+a.getGroupId()+"</groupId>");
		p.println("\t\t\t<artifactId>"+a.getArtifactId()+"</artifactId>");
		p.println("\t\t\t<version>"+a.getVersion()+"</version>");
		p.println("\t\t\t<type>zip</type>");
		p.println("\t\t</dependency>");
	}

	private static void savePlugins(PrintStream p,GramlabProject project) {
		p.println("\t<build>");
		p.println("\t\t<plugins>");
		/* First, we write things about the dependency plugin */
		p.println("\t\t\t<plugin>");
		p.println("\t\t\t\t<groupId>org.apache.maven.plugins</groupId>");
		p.println("\t\t\t\t<artifactId>maven-dependency-plugin</artifactId>");
		p.println("\t\t\t\t<configuration>");
		p.println("\t\t\t\t\t<useRepositoryLayout>false</useRepositoryLayout>");
		p.println("\t\t\t\t\t<excludeTransitive>false</excludeTransitive>");
		p.println("\t\t\t\t\t<outputDirectory>${gramlab.deps.directory}</outputDirectory>");
		p.println("\t\t\t\t</configuration>");
		p.println("\t\t\t\t<executions>");
		p.println("\t\t\t\t\t<execution>");
		p.println("\t\t\t\t\t\t<id>unpack-dependencies</id>");
		p.println("\t\t\t\t\t\t<phase>generate-resources</phase>");
		p.println("\t\t\t\t\t\t<goals>");
		p.println("\t\t\t\t\t\t\t<goal>unpack-dependencies</goal>");
		p.println("\t\t\t\t\t\t</goals>");
		p.println("\t\t\t\t\t</execution>");
		p.println("\t\t\t\t</executions>");
		p.println("\t\t\t</plugin>");
		p.println();
		/* We say what need to be said about the assembly thing we use
		 * to create our packages
		 */
		p.println();
		p.println("\t\t\t<plugin>");
		p.println("\t\t\t\t<artifactId>maven-assembly-plugin</artifactId>");
		p.println("\t\t\t\t<version>${assembly.plugin.version}</version>");
		p.println("\t\t\t\t<dependencies>");
		p.println("\t\t\t\t\t<dependency>");
		p.println("\t\t\t\t\t\t<groupId>org.gramlab</groupId>");
		p.println("\t\t\t\t\t\t<artifactId>gramlab-assembly-descriptors</artifactId>");
		p.println("\t\t\t\t\t\t<version>0.0.2</version>");
		p.println("\t\t\t\t\t</dependency>");
		p.println("\t\t\t\t</dependencies>");
		p.println("\t\t\t\t<configuration>");
		p.println("\t\t\t\t\t<encoding>UTF-8</encoding>");
		p.println("\t\t\t\t\t<appendAssemblyId>false</appendAssemblyId>");
		p.println("\t\t\t\t\t<descriptorRefs>");
		p.println("\t\t\t\t\t\t<descriptorRef>create-base-package</descriptorRef>");
		p.println("\t\t\t\t\t</descriptorRefs>");
		p.println("\t\t\t\t</configuration>");
		p.println("\t\t\t\t<executions>");
		p.println("\t\t\t\t\t<execution>");
		p.println("\t\t\t\t\t\t<id>make-assembly</id>");
		p.println("\t\t\t\t\t\t<phase>package</phase>");
		p.println("\t\t\t\t\t\t<goals>");
		p.println("\t\t\t\t\t\t\t<goal>single</goal>");
		p.println("\t\t\t\t\t\t</goals>");
		p.println("\t\t\t\t\t</execution>");
		p.println("\t\t\t\t</executions>");
        p.println("\t\t\t</plugin>");
		
		/* Then, we write things about the ant plugin used to generate the package */
		p.println("\t\t\t<plugin>");
		p.println("\t\t\t\t<groupId>org.apache.maven.plugins</groupId>");
		p.println("\t\t\t\t<artifactId>maven-antrun-plugin</artifactId>");
		p.println("\t\t\t\t<executions>");
		p.println("\t\t\t\t\t<execution>");
		p.println("\t\t\t\t\t\t<id>test</id>");
        p.println("\t\t\t\t\t\t<phase>test</phase>");
        p.println("\t\t\t\t\t\t<configuration>");
        p.println("\t\t\t\t\t\t\t<tasks>");
        p.println("\t\t\t\t\t\t\t\t<property name=\"gramlab.src\" value=\"${project.basedir}/src\"/>");
        p.println("\t\t\t\t\t\t\t\t<property name=\"gramlab.build\" value=\"${project.basedir}/build\"/>");
        p.println("\t\t\t\t\t\t\t\t<property name=\"gramlab.dst\" value=\"${gramlab.build}/"+project.getName()+"\"/>");
        p.println("\t\t\t\t\t\t\t\t<delete dir=\"${gramlab.build}/\"/>");
        if (project.isMvnSourcePackage()) {
        	p.println("\t\t\t\t\t\t\t\t<copy todir=\"${gramlab.dst}/\">");
        	/* First, the inclusion of *.grf and/or *.dic */
        	MvnSourceConfig s=project.getMvnSourceConfig();
        	p.println("\t\t\t\t\t\t\t\t\t<fileset dir=\"${gramlab.src}/\">");
        	if (s.isIncludeGrfs() || s.isIncludeDics()) {
        		if (s.isIncludeGrfs()) p.println("\t\t\t\t\t\t\t\t\t\t<include name=\"**/*.grf\"/>");
        		if (s.isIncludeDics()) p.println("\t\t\t\t\t\t\t\t\t\t<include name=\"**/*.dic\"/>");
        		for (File f:s.getExcludes()) {
        			p.println("\t\t\t\t\t\t\t\t\t\t<exclude name=\"**/"+MavenDialog.inSrcDirectory(project,f)+"\"/>");
        		}
        	}
        	/* Then, we add the other files to be included */
        	for (File f:s.getIncludes()) {
        		p.println("\t\t\t\t\t\t\t\t\t\t<include name=\"**/"+MavenDialog.inSrcDirectory(project,f)+"\"/>");
        	}
        	/* We also have default files to include */
        	for (String foo:MvnSourceConfig.getDefaultFilesToInclude()) {
        		File f=new File(project.getSrcDirectory(),foo);
        		if (f.exists() && !s.getExcludes().contains(f)) {
        			p.println("\t\t\t\t\t\t\t\t\t\t<include name=\"**/"+foo+"\"/>");
        		}
        	}
        	p.println("\t\t\t\t\t\t\t\t\t</fileset>");	
        	p.println("\t\t\t\t\t\t\t\t</copy>");
        }
       	/* In any case, we add the pom.xml and a modified version of the versionable 
       	 * config file, needed to know which named repositories are assumed by the 
       	 * component */
        p.println("\t\t\t\t\t\t\t\t<copy todir=\"${gramlab.dst}/\">");
        p.println("\t\t\t\t\t\t\t\t\t<fileset dir=\"${project.basedir}\" file=\"pom.xml\"/>");
       	p.println("\t\t\t\t\t\t\t\t</copy>");
       	File repo=new File(project.getProjectDirectory(),"..repositories");
       	if (repo.exists()) {
       		p.println("\t\t\t\t\t\t\t\t<move file=\"${project.basedir}/..repositories\" tofile=\"${gramlab.dst}/repositories\"/>");
       	}
       	/* And we may have to add the operations needed to compile graphs and to compress dictionaries */
       	if (project.isMvnBuildPackage()) {
       		/* First, we have to create the output directories where the .fst2 will be placed,
       		 * because Grf2Fst2 is not able to create the missing intermediate parent
       		 * directories */
       		ArrayList<GrfToCompile> g=project.getMvnBuildConfig().getGrfToCompile();
       		ArrayList<String> dirs=getDirectories(project,g);
       		for (String dir:dirs) {
       			p.println("\t\t\t\t\t\t\t\t<mkdir dir=\"${gramlab.dst}/"+dir+"\"/>");
       		}
       		if (project.getMvnBuildConfig().getBinToBuild().size()>0) {
       			/* If needed, we also create the Dela directory */
       			p.println("\t\t\t\t\t\t\t\t<mkdir dir=\"${gramlab.dst}/Dela/\"/>");
       		}
       		File dstDir=new File(new File(project.getProjectDirectory(),"build"),project.getName());
       		boolean emitUnitexToolLoggerMsg=true;
       		for (GrfToCompile foo:g) {
       			String dir=FileUtil.isAncestor(project.getSrcDirectory(),foo.getGrf().getParentFile());
    			if (dir==null) {
    				throw new IllegalStateException("Should not happen");
    			}
    			File fst2=new File(dstDir,dir+foo.getFst2());
       			Grf2Fst2Command cmd=project.createGrf2Fst2Command(foo.getGrf(), fst2, false, false);
       			String[] args=cmd.getCommandArguments(true);
       			if (emitUnitexToolLoggerMsg) {
       				emitUnitexToolLoggerMsg=false;
           			p.println("<!-- The variable 'unitextoollogger' is supposed to be passed to 'mvn' with -D");
           			p.println("     It should contains the absolute path to the UnitexToolLogger executable -->");
       			}
       			p.println("\t\t\t\t\t\t\t\t<exec executable=\"${unitextoollogger}\">");
       			for (int i=1;i<args.length;i++) {
       				p.println("\t\t\t\t\t\t\t\t\t<arg value=\""+getRelativePath(args[i],project)+"\"/>");
       			}
       			p.println("\t\t\t\t\t\t\t\t</exec>");
       		}
       		for (BinToBuild foo:project.getMvnBuildConfig().getBinToBuild()) {
    			File bin=new File(dstDir,"Dela/"+foo.getBin());
				CompressCommand cmd = new CompressCommand();
				for (File dic:foo.getDics()) {
					cmd=cmd.dic(dic);
				}
				if (project.isSemitic()) {
					cmd = cmd.semitic();
				}
				cmd=cmd.output(bin);
       			String[] args=cmd.getCommandArguments(true);
       			if (emitUnitexToolLoggerMsg) {
       				emitUnitexToolLoggerMsg=false;
           			p.println("<!-- The variable 'unitextoollogger' is supposed to be passed to 'mvn' with -D");
           			p.println("     It should contains the absolute path to the UnitexToolLogger executable -->");
       			}
       			p.println("\t\t\t\t\t\t\t\t<exec executable=\"${unitextoollogger}\">");
       			for (int i=1;i<args.length;i++) {
       				p.println("\t\t\t\t\t\t\t\t\t<arg value=\""+getRelativePath(args[i],project)+"\"/>");
       			}
       			p.println("\t\t\t\t\t\t\t\t</exec>");
       		}
       	}
       	p.println("\t\t\t\t\t\t\t</tasks>");
       	p.println("\t\t\t\t\t\t</configuration>");
       	p.println("\t\t\t\t\t\t<goals>");
       	p.println("\t\t\t\t\t\t\t<goal>run</goal>");
       	p.println("\t\t\t\t\t\t</goals>");
       	p.println("\t\t\t\t\t</execution>");
       	p.println("\t\t\t\t</executions>");
		p.println("\t\t\t</plugin>");
		/* Done */
		p.println("\t\t</plugins>");
		p.println("\t</build>");
		p.println("");
	}

	/**
	 * We try to determine if the given argument is a path name. If not, we return it as is.
	 * If it is a path name, we convert it to a one relative to the project directory.
	 * @param arg
	 * @return
	 */
	private static String getRelativePath(String arg,GramlabProject p) {
		String s=p.getProjectDirectory().getAbsolutePath();
		return arg.replaceAll(s,"\\${project.basedir}");
	}

	private static ArrayList<String> getDirectories(GramlabProject p,ArrayList<GrfToCompile> grfs) {
		ArrayList<String> list=new ArrayList<String>();
		for (GrfToCompile g:grfs) {
			String s=FileUtil.isAncestor(p.getSrcDirectory(),g.getGrf().getParentFile());
			if (s==null) {
				throw new IllegalStateException("Should not happen");
			}
			if (!list.contains(s)) {
				list.add(s);
			}
		}
		return list;
	}

	private static void inheritsGramlabSuperPom(PrintStream p) {
		p.println("\t<parent>");
		p.println("\t\t<groupId>org.gramlab</groupId>");
		p.println("\t\t<artifactId>gramlab-super-pom</artifactId>");
		p.println("\t\t<version>0.0.1</version>");
		p.println("\t\t<relativePath></relativePath>");
		p.println("\t</parent>");
		p.println();
	}


	public static ArrayList<Artifact> parsePOM(File f,Pom pom) {
		return readPomFile(f,pom);
	}

	public static Document readXmlFile(File f) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(f);
			document.getDocumentElement().normalize();
			return document;
		} catch (ParserConfigurationException e) {
			return null;
		} catch (SAXException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Reads a POM.xml file and extracts the current artifact and its
	 * dependencies.
	 * 
	 * @param f
	 *            the pom file
	 * @return a list of artifacts where the first is the POM's artifact and the
	 *         others are its dependencies
	 */
	public static ArrayList<Artifact> readPomFile(File f,Pom pom) {
		ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
		Document xmlDocument = readXmlFile(f);

		// Read first artifact in the <parent> child
		NodeList projectNodeList = xmlDocument.getElementsByTagName("project");
		if (projectNodeList.getLength() > 0) {
			Node projectNode = projectNodeList.item(0);
			artifacts.add(readArtifact(projectNode));

			// Read the dependencies and add them to the artifact list
			NodeList dependencyNodeList = xmlDocument.getElementsByTagName("dependency");
			for (int i = 0; i < dependencyNodeList.getLength(); i++) {
				Node dependencyNode = dependencyNodeList.item(i);

				// Check that we are indeed below the <dependencies> node
				Node dependencyParentNode = dependencyNode.getParentNode();
				if (dependencyParentNode != null && "dependencies".equalsIgnoreCase(dependencyParentNode.getNodeName())) {
					Node dependenciesParentNode=dependencyParentNode.getParentNode();
					if (dependenciesParentNode != null && "project".equalsIgnoreCase(dependenciesParentNode.getNodeName())) { 
						Artifact artifact = readArtifact(dependencyNode);
						if (artifact != null) {
							artifacts.add(artifact);
						}
					}
				}
			}
			String[] release=new String[2];
			String[] snapshot=new String[2];
			NodeList repoNodeList = xmlDocument.getElementsByTagName("repository");
			for (int i = 0; i < repoNodeList.getLength(); i++) {
				Node repoNode = repoNodeList.item(i);
				Node repoParentNode = repoNode.getParentNode();
				if (repoNode != null && "distributionManagement".equalsIgnoreCase(repoParentNode.getNodeName())) {
					readRepositoryInfo(repoNode,release);
				}
			}
			repoNodeList = xmlDocument.getElementsByTagName("snapshotRepository");
				for (int i = 0; i < repoNodeList.getLength(); i++) {
					Node repoNode = repoNodeList.item(i);
					Node repoParentNode = repoNode.getParentNode();
					if (repoNode != null && "distributionManagement".equalsIgnoreCase(repoParentNode.getNodeName())) {
						readRepositoryInfo(repoNode,snapshot);
					}
				}
			if (release[0]!=null && release[1]!=null && snapshot[0]!=null && snapshot[1]!=null) {
				pom.setDistributionManagement(release[0], release[1], snapshot[0], snapshot[1]);
			}
		}
		return artifacts;
	}

	/**
	 * Reads the content of an XML node and extracts its groupId/artifactId/version triplet.
	 * 
	 * @param node an XML node
	 * @return an Artifact instance or null if the required information are not present in the node
	 */
	public static Artifact readArtifact(Node node) {
		String groupId = null, artifactId = null, version = null;

		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String nodeName = child.getNodeName();
			String nodeText = child.getTextContent();
			if (nodeText != null)
				nodeText = nodeText.trim();
			if ("groupId".equalsIgnoreCase(nodeName))
				groupId = nodeText;
			else if ("artifactId".equalsIgnoreCase(nodeName))
				artifactId = nodeText;
			else if ("version".equalsIgnoreCase(nodeName))
				version = nodeText;
		}

		if (groupId == null || artifactId == null || version == null)
			return null;
		return Artifact.checkedArtifactCreation(groupId, artifactId, version,"");
	}

	/**
	 * Reads the content of an XML node representing either a release repository
	 * or a snapshot repository, and stores the repository name and url in the
	 * given String array.
	 */
	public static void readRepositoryInfo(Node node,String[] storage) {
		storage[0]=storage[1]=null;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String nodeName = child.getNodeName();
			String nodeText = child.getTextContent();
			if (nodeText != null)
				nodeText = nodeText.trim();
			if ("id".equalsIgnoreCase(nodeName))
				storage[0] = nodeText;
			else if ("url".equalsIgnoreCase(nodeName))
				storage[1] = nodeText;
		}
		if (storage[0]==null || storage[1]==null) {
			storage[0]=storage[1]=null;
		}
	}

	/**
	 * Reads a settings.xml file and extracts the location of the
	 * local repository, if any; null otherwise. 
	 */
	public static File readLocalRepositoryFromSettingsXml(File f) {
		if (!f.exists()) return null;
		Document xmlDocument = readXmlFile(f);
		NodeList list = xmlDocument.getElementsByTagName("localRepository");
		if (list.getLength()!=0) {
			Node node = list.item(0);
			return new File(node.getTextContent());
		}
		return null;
	}

	
}
