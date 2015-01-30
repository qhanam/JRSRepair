package ca.uwaterloo.ece.qhanam.jrsrepair.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

import ca.uwaterloo.ece.qhanam.jrsrepair.*;
import ca.uwaterloo.ece.qhanam.jrsrepair.compiler.JavaJDKCompiler;

public class SampleUse {
	
	public static final String[] SOURCE_DIRECTORY = {"/Users/qhanam/Documents/workspace_faultlocalization/ca.uwaterloo.ece.qhanam.localization/src"};
	public static final String FAULTY_COVERAGE = "/Users/qhanam/Documents/workspace_repair/ca.uwaterloo.ece.qhanam.jrsrepair/cov/faulty.cov";
	public static final String SEED_COVERAGE = "/Users/qhanam/Documents/workspace_repair/ca.uwaterloo.ece.qhanam.jrsrepair/cov/seed.cov";
	
	public static final int MUTATION_CANDIDATES = 40; 
	public static final int MUTATION_GENERATIONS = 10;
	public static final int MUTATION_ATTEMPTS = 100;
	
	public static final String ANT_BASE_DIR = "/Users/qhanam/Documents/workspace_faultlocalization/ca.uwaterloo.ece.qhanam.localization/";
	public static final String ANT_PATH = "/usr/bin/ant";
	public static final String ANT_COMPILE_TARGET = "compile";
	public static final String ANT_TEST_TARGET = "junit";
	
	public static final long RANDOM_SEED = 3;
	
	public static final File BUILD_DIRECTORY = new File("/Users/qhanam/Documents/workspace_faultlocalization/ca.uwaterloo.ece.qhanam.localization/build");
	
	/* The class directory is the directory that stores the compiled class files. */
	public static final String CLASS_DIRECTORY = "/Users/qhanam/Documents/workspace_faultlocalization/ca.uwaterloo.ece.qhanam.localization/build/classes";
	
	/* The classpath is used to resolve binding. These are absolute paths to .jar files or base directories containing .class files. This does not need to 
	 * include the default system classpath (i.e., Java standard libraries). */
	public static final String[] CLASSPATH = {};
	
	/* The sourcepath is used to resolve bindings. These are the base directories containing source files. */
	public static final String[] SOURCEPATH = {"/Users/qhanam/Documents/workspace_faultlocalization/ca.uwaterloo.ece.qhanam.localization/src"};

	public static void main(String[] args) throws Exception {
		if(args.length > 0){
			String[] sourceDirectories = readSourceFiles(new File(args[0]));
			JRSRepair repair = readConfigFile(new File(args[1]), sourceDirectories);
            repair.buildASTs();
            repair.repair();
		}
		else{
            TestExecutor testExecutor = new TestExecutor(new File(ANT_BASE_DIR), ANT_PATH, ANT_COMPILE_TARGET, ANT_TEST_TARGET);
            JavaJDKCompiler compiler = new JavaJDKCompiler(CLASS_DIRECTORY, CLASSPATH);
            JRSRepair repair = new JRSRepair(SOURCE_DIRECTORY, CLASSPATH, new File(FAULTY_COVERAGE), new File(SEED_COVERAGE), 
                                             MUTATION_CANDIDATES, MUTATION_GENERATIONS, MUTATION_ATTEMPTS, RANDOM_SEED, 
                                             BUILD_DIRECTORY, compiler, testExecutor);
            repair.buildASTs();
            repair.repair();
		}
		
	}
	
	public static String[] readSourceFiles(File file) throws Exception{
		String[] sourceDirectoryArray;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		LinkedList<String> sourceDirectories = new LinkedList<String>();

		for(String line = reader.readLine(); line != null; line = reader.readLine()){
			sourceDirectories.add(line.trim());
		}
		
		reader.close();
	
		sourceDirectoryArray = new String[sourceDirectories.size()];
		int i = 0;
		for(String sourceDirectory : sourceDirectories){
			sourceDirectoryArray[i] = sourceDirectory;
			i++;
		}
		
		return sourceDirectoryArray;
	}
	
	public static JRSRepair readConfigFile(File file, String[] sourceDirectories) throws Exception{
        TestExecutor testExecutor;
        JavaJDKCompiler compiler;
		JRSRepair repair;

		BufferedReader reader = new BufferedReader(new FileReader(file));

        File faultyCoverage = new File(reader.readLine().trim());
        File seedCoverage = new File(reader.readLine().trim());
	
        int mutationCandidates = Integer.parseInt(reader.readLine().trim());
        int mutationGenerations = Integer.parseInt(reader.readLine().trim());
        int mutationAttempts = Integer.parseInt(reader.readLine().trim()); 

        File antBaseDirectory = new File(reader.readLine().trim());
        String antPath = reader.readLine().trim();
        String antCompileTarget = reader.readLine().trim();
        String antTestTarget = reader.readLine().trim();

        long randomSeed = Integer.parseInt(reader.readLine().trim());

    	File buildDirectory = new File(reader.readLine());
    	String classDirectory = reader.readLine();
    	String[] classPath = reader.readLine().split(";");

		reader.close();
		
        testExecutor = new TestExecutor(antBaseDirectory, antPath, antCompileTarget, antTestTarget);
        compiler = new JavaJDKCompiler(classDirectory, classPath);
        repair = new JRSRepair(sourceDirectories, classPath, faultyCoverage, seedCoverage, 
                                 mutationCandidates, mutationGenerations, mutationAttempts, randomSeed, 
                                 buildDirectory, compiler, testExecutor);

		return repair;
	}
}