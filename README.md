=======
## README ##

Java-RSRepair is a research tool that attempts to repair a buggy program by randomly mutating it. It is essentially a Java implementation of the [RSRepair](http://qiyuhua.github.io/projects/rsrepair/) tool, which is a variation of the [GenProg](http://dijkstra.cs.virginia.edu/genprog/) automated program repair tool. It is important to note that automated repair techniques such as RSRepair and GenProg define a repaired program as one where all test cases pass. The repairs these tools generate often simply avoid running the features of the program that produce the incorrect behaviour (see [this](http://dspace.mit.edu/bitstream/handle/1721.1/94337/MIT-CSAIL-TR-2015-003.pdf?sequence=1) paper). 

While these tools often do not produce correct behaviour, they may still have applications (see [this](https://hal.inria.fr/hal-01054549/PDF/essay-automatic-repair.pdf) paper). For example, modifying a program to cause all test cases to pass by removing graphical features may be desirable if a system needs to be fault tolerant.

Java-RSRepair is a flexible tool that allows researchers to experiment with various settings (e.g., turning mutation operations on or off) and can be easily customized (e.g., adding custom mutation operations).

### Installation ###

Java-RSRepair is a Maven project.

* Install Apache Ant (needed to run the sample program).
* Clone Java-RSRepair.
* Edit ./sample/config/jrsrepair.properties:
   * Set `ant_path = [/path/to/ant]` (use `which ant` to find its location on linux)
* Edit ./sample/config/build.xml:
   * Set the value of `<property name="junit.jar" value="[/path/to/junit.jar]">`
   * Set the value of `<property name="hamcrest-all.jar" value="[/path/to/hamcrest-all.jar]">`
* Build and install the project (`mvn clean install`).

Optional: Create the Eclipse project files (`mvn eclipse:eclipse`).

### Configuring and Running ###

Java-RSRepair comes with a sample program that will run Java-RSRepair (ca.uwaterloo.ece.qhanam.jrsrepair.Java-RSRepairMain). This program can be executed from the command line and takes one argument -> the path to a configuration file. A sample configuration file can be found in `sample\config\jsrepair.properties`.

To run Java-RSRepair from the command line through Maven:
```bash
mvn exec:java -Dexec.mainClass="ca.uwaterloo.ece.qhanam.jrsrepair.JRSRepairMain" -Dexec.args="./sample/config/jrsrepair.properties"
```

### Directory Structure ###

* `src\`: The Java-RSRepair library
* `test\`: Contains the sample program `SampleUse.java` which runs Java-RSRepair.
* `sample\`: A sample program for trying out Java-RSRepair
* `sample\src`: The sample program under test (an LRU cache implementation)
* `sample\test`: The JUnit test cases for the sample program under test
* `sample\config`: The Java-RSRepair configuration files for repairing the sample program. This includes:
    * `jrsrepair.properties`: The configuration file
    * `faulty.cov`: The code coverage file for potentially faulty statements to mutate.
    * `seed.cov`: The code coverage file for seed statements to perform the mutations with.
    * `build.xml`: The Apache Ant build script that runs the JUnit tests for the sample program.

### Output ###

Java-RSRepair outputs result data to the folder specified by the `build_directory` property in `jrsrepair.properties`. The following outputs are stored in this directory:

* `mutation-log`: A log of the mutation operations performed for each candidate and each generation.
* `compile-log`: A log of compiler errors generated by the JDK compiler. These may occur if all variables are in scope but there are still semantic errors (e.g., a return statement is inserted with the incorrect return type). These may also occur if the original build is not set up correctly.
* `classes_Candidate[x]_Generation[y]_[timestamp]`: If a patch was generated that passes all test cases, the binary is stored in a folder with this signature.
* `test-reports`: A directory containing the reports from the test script (Ant script or custom Bash script). This isn't directly output by Java-RSRepair, but for running experiments all test results should be logged by the script.

### APARE Evaluation Method ###

Java-RSRepair is used in the evaluation of [APARE](http://asset.uwaterloo.ca/APARE/). APARE is an automated program repair technique that automatically learns repair patterns, discovers other locations in code where those repair patterns should be applied and applies them to fully or partially repair the code in the same way a developer would. This section describes the steps to replicate the results.

* Localize the fault for the program under repair:
    * Collect code coverage of passing and failing test cases using a tool like [JaCoCo](http://www.eclemma.org/jacoco/)
    * Assign a weight to each statement executed by the failing test cases. A simple metric is to assign a weight of 1.0 to statements that are only executed by failing test cases and 0.1 to statements that are executed by both failing and passing test cases. Better weighting can be achieved using a similarity coefficient such as Jaccard or Ochiai. 
* Create `faulty.cov`. This is the weighted list of potentially faulty statements from step 1. These statements will be selected for mutation with probability directly proportional to their weight.
* Create `seed.cov`. This is the set of statements executed by all test cases. These statements will be used for insertion and replacement mutations.
* Set up the Ant build script (build.xml) for running the JUnit tests (Maven will generate this for you if your project uses Maven).
* Check that the Ant JUnit runner fails for the faulty program and passes for the repaired program.
* Set up Java-RSRepair by editing jrsrepair.properties:
* Use the `null_mutation_only` property to make sure Java-RSRepair compiles and runs the program under repair correctly without performing mutations.
* Run Java-RSRepair. If you run Java-RSRepair multiple times, increment the seed for the random number generator in between each run.
