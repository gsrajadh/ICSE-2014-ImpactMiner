package edu.wm.flat3.analysis.impact;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import raykernel.apps.deltadoc2.record.MethodMap;
import raykernel.apps.deltadoc2.record.MethodSet;
import raykernel.lang.dom.naming.MethodSignature;
import raykernel.lang.parse.MethodParser;

/**
 * @author mmwagner@email.wm.edu
 *
 */
public class DifferenceParser {
	
	public static void main(String[] args) throws Exception
	{
		//FLATTT.debugOutput("\t\tAbout to process file: " + pair.getOldFile(), System.out);
		String source1 = raykernel.io.FileReader.readFile(new File("DeltaDoc/test/ClassDiagramRenderer.java.v12275"));
		String source2 = raykernel.io.FileReader.readFile(new File("DeltaDoc/test/ClassDiagramRenderer.java.v12276"));

		//FLATTT.debugOutput("Used FileReaders, about to call MethodParser.parse", System.out);
		MethodMap m1 = MethodParser.parse(source1);
		MethodMap m2 = MethodParser.parse(source2);
		//FLATTT.debugOutput("Finished calling MethodParser.parse",System.out);
		
		MethodSet m1Set=m1.toSet();
		MethodSet m2Set=m2.toSet();
//		for (Iterator iterator=m1Set.iterator();iterator.hasNext();)
//		{
//			MethodSignature methodSignature=(MethodSignature)iterator.next();
//			
//			System.out.println(methodSignature);
//			System.out.println(methodSignature.getFullName());
//		}
		System.out.println("M1");
		System.out.println(m1Set.toString());
		System.out.println("M2");
		System.out.println(m2Set.toString());
		System.out.println("===");

		ChangeSet changeSet = new ChangeSet();
		
		MethodSet changes = MethodMap.changes(m1, m2);
		for (MethodSignature m : changes){
			//System.out.println(m.getFullName());
			changeSet.add(m.getFullName());
			System.out.println(m.getFullName());
		}

		
	}
	
	/**
	 * Compute the changeSet for a given revision
	 * @param filePairs A list of modified <code>File</code>s.  Each pair should contain the older and newer version
	 * @param singles Added <code>File</code>s.
	 * @return A <code>changeSet</code> of all methods changed or added at this revision
	 */
	// filePairs: use deltadoc to find differences
	// singles: use deltadoc to simply return a list of methods
	public static ChangeSet getRevisionChanges(ArrayList<ChangedFilePair> filePairs, ArrayList<File> singles){
		//FLATTT.debugOutput("Just entered function, about to instatiate ChangeSet", System.out);
		
		ChangeSet changeSet = new ChangeSet();
		
		for (ChangedFilePair pair : filePairs) {
			try{
				//FLATTT.debugOutput("\t\tAbout to process file: " + pair.getOldFile(), System.out);
				String source1 = raykernel.io.FileReader.readFile(pair.getOldFile());
				String source2 = raykernel.io.FileReader.readFile(pair.getNewFile());

				//FLATTT.debugOutput("Used FileReaders, about to call MethodParser.parse", System.out);
				MethodMap m1 = MethodParser.parse(source1);
				MethodMap m2 = MethodParser.parse(source2);
				//FLATTT.debugOutput("Finished calling MethodParser.parse",System.out);

				MethodSet changes = MethodMap.changes(m1, m2);
				for (MethodSignature m : changes){
					//System.out.println(m.getFullName());
					changeSet.add(m.getFullName());
				}
			}catch (IOException e){
				e.printStackTrace();
			}catch (NullPointerException e){
				//no-op
			}
		}
		//FLATTT.debugOutput("\tAbout to process single files", System.out);
		for (File file : singles){
			try{
				//FLATTT.debugOutput("\t\tAbout to read file\t" + file, System.out);
				String source = raykernel.io.FileReader.readFile(file);
				//FLATTT.debugOutput("\t\tAbout to parse file\t" + file, System.out);
				MethodMap m3 = MethodParser.parse(source);
				//FLATTT.debugOutput("\t\tAbout to extract methodSet " + file, System.out);
				MethodSet additions = m3.toSet();
				for (MethodSignature m : additions){
					//System.out.println(m.getFullName());
					changeSet.add(m.getFullName());
				}
			}catch (IOException e){
				e.printStackTrace();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return changeSet;
	}
	
	
	/**
	 * Parses the changes described in a file generated by a call to <code>SVNDiffClient.doDiff()</code>.
	 * 
	 * @param directory the directory in which the data is stored.  This directory should include the changes.txt file as well as copies of all changed files
	 * @return A list of all source methods changed in this revision
	 * @throws IOException 
	 */
//	public static ArrayList<FLATTTMember> doParse(String directory) throws IOException{
//		if (pathDel == null){ SVNsearchAlt.assignPathDel(); pathDel = SVNsearchAlt.pathDel;}
//		String revisionChangesFile = directory + "changes.txt";
//		rcfReader = new BufferedReader(new java.io.FileReader(revisionChangesFile));
//		changedEntities = new ArrayList<FLATTTMember>();
//		
//		
//		
//		File dir = new File(directory);
//		String sourceFile = null;
//		
//		String line = rcfReader.readLine();
//		
//		
//		//boolean done = false;
//		while (line != null){
//			sourceFile = null;
//			String[] lineList = line.split(" ");
//			String rawSourceFile = lineList[1].replaceAll("/", "_"); // src/blah/blah/blah/etc/ClassName.java
//			rcfReader.readLine(); // ignore the === line
//			line = rcfReader.readLine();	// read the --- line
//			long oldR = Long.valueOf(line.split(" ")[3].replace(")", ""));	//get the old revision number
//			rcfReader.readLine();	// ignore the +++ line
//			for (String fname : dir.list())
//				if (fname.startsWith("M-"+oldR) && fname.endsWith(rawSourceFile)){
//					sourceFile = fname;
//					break;
//				}
//			if (SVNsearchAlt.suffixTest(sourceFile)){
//				line = processSourceFile(directory + pathDel + sourceFile);
//			}
//		}
//		return changedEntities;
//	}
//
//	private static String processSourceFile(String sourceFile) throws IOException {
//		BufferedReader srcReader = new BufferedReader(new java.io.FileReader(sourceFile));
//		String line;
//		String srcLine = null;
//		ArrayList<String> signaturesOfChangedMethods = new ArrayList<String>();
//		String mostRecentlyFoundSignature = null;
//		line = rcfReader.readLine();
//		while (line != null && line.startsWith("@@ ")){ // process one modified area
//			mostRecentlyFoundSignature = null;
//			int startLine = Integer.valueOf(line.split(" ")[1].split(",")[0].replace(",", ""));
//			int numLines = 0;
//			int i;
//			for (i = 0; i < startLine+3; i++){ // read file up to the start of the edit
//				srcLine = srcReader.readLine();
//				if (isMethodSignature(srcLine))
//					mostRecentlyFoundSignature = srcLine;
//			}
//			//add the most recently found signature to the list of changed signatures
//			if (mostRecentlyFoundSignature != null &&
//					!signaturesOfChangedMethods.contains(mostRecentlyFoundSignature)) 
//				signaturesOfChangedMethods.add(mostRecentlyFoundSignature);
//			for ( ; i < startLine + numLines -3; i++){ // read file through end of edit
//				srcLine = srcReader.readLine();
//				if (isMethodSignature(srcLine) &&
//						!signaturesOfChangedMethods.contains(srcLine))
//					signaturesOfChangedMethods.add(srcLine);
//			}
//			// fast forward thru rcf file to the end of this modified area
//			// if line starts with "@@ " we process another modified area in the same file
//			// if line starts with "Index: " or is null we return the line
//			while (line != null && !line.startsWith("@@ ") && !line.startsWith("Index:")){
//				line = rcfReader.readLine();
//				numLines++;
//			}
//		}	//end one modified area
//		//done processing modified areas for this file
//		//TODO: beging iterating through signaturesOfChandedMethods and parse the signatures into
//		// keys.  From there get FLATTTMember objects, add them to changedEntities.
//		
//		return line;
//	}
//
//	private static boolean isMethodSignature(String srcLine) {
//		// TODO parse srcLine.
//		// Return true if srcLine represents a method signature
//		// Return false otherwise
//		return false;
//	}

}
