package test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.ExtendedICFG;
import boomerang.cfg.IExtendedICFG;
import heros.BiDiInterproceduralCFG;
import heros.incremental.UpdatableWrapper;
import ideal.Analysis;
import ideal.ResultReporter;
import ideal.debug.IDEDebugger;
import ideal.debug.IDebugger;
import soot.G;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Transform;
import soot.Transformer;
import soot.Unit;
import soot.JastAddJ.CompilationUnit;
import soot.JastAddJ.Program;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;
import test.ConcreteState;
import typestate.TypestateAnalysisProblem;
import typestate.TypestateChangeFunction;
import typestate.TypestateDomainValue;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;

public class IdealIncrementalTest {
	
	private String initialCodePath;
	private String updatedCodePath;
	private String testClassName;
	protected ExtendedICFG icfg;
	protected SootMethod sootTestMethod;
	private Analysis<TypestateDomainValue<ConcreteState>> analysis;
	private Path codePath;
	
	List<Table<UpdatableWrapper<Unit>, AccessGraph, TypestateDomainValue<ConcreteState>>> computeResultsPhaseTwo;
	List<Table<UpdatableWrapper<Unit>, AccessGraph, TypestateDomainValue<ConcreteState>>> updateResultsPhaseTwo;
	
	List<Table<UpdatableWrapper<Unit>, AccessGraph, TypestateDomainValue<ConcreteState>>> computeResultsPhaseOne;
	List<Table<UpdatableWrapper<Unit>, AccessGraph, TypestateDomainValue<ConcreteState>>> updateResultsPhaseOne;

	public IdealIncrementalTest(String initialCodePath, String updatedCodePath, String testClassName)
	{
		this.initialCodePath = initialCodePath;
		this.updatedCodePath = updatedCodePath;
		this.testClassName = testClassName;
	}
	
	public static void main(String args[]) {
		if(args.length < 3) {
			System.out.println("Invoke the program with the arguments path_of_initial_jar, path_of_updated_jar, class_name");
			System.exit(1);
		}
		String initialVersion = args[0];
		String updatedVersion = args[1];
		String testClassName = args[2];
		IdealIncrementalTest test = new IdealIncrementalTest(initialVersion, updatedVersion, testClassName);
		test.compareResultsofVersions();
	}
	
	private void computeResults() {
		System.out.println("Analysing the updated code from path " + updatedCodePath);
		try {
			codePath = Files.createTempFile("updated", "code.jar");
			Files.copy(Paths.get(updatedCodePath), codePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		soot.G.reset();
		initializeSoot();
		compute();
	}
	
	private void updateResults() {
		System.out.println("Analysing the initial code from path " + initialCodePath);
		try {
			codePath = Files.createTempFile("initial", "code.jar");
			Files.copy(Paths.get(initialCodePath), codePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		soot.G.reset();
		initializeSoot();
		update();
	}
	
	private void compute(){
//		PackManager.v().getPack("wjtp").add(new Transform("wjtp.prepare", new PreparationTransformer()));
		Transform transformer = new Transform("wjtp.ifds", createAnalysisComputationTransformer());
		PackManager.v().getPack("wjtp").add(transformer);
		PackManager.v().getPack("cg").apply();
		PackManager.v().getPack("wjtp").apply();
	}
	
	private void update() {
//		PackManager.v().getPack("wjtp").add(new Transform("wjtp.prepare", new PreparationTransformer()));
		Transform transformer = new Transform("wjtp.ifds", createAnalysisUpdateTransformer());
		PackManager.v().getPack("wjtp").add(transformer);
		PackManager.v().getPack("cg").apply();
		PackManager.v().getPack("wjtp").apply();
	}
	
	protected Analysis<TypestateDomainValue<ConcreteState>> createAnalysis() {
		return new Analysis<TypestateDomainValue<ConcreteState>>(new TypestateAnalysisProblem<ConcreteState>() {
			@Override
			public ResultReporter<TypestateDomainValue<ConcreteState>> resultReporter() {
				return null;
			}

			@Override
			public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
				return icfg.getBaseECFG();
			}

			@Override
			public TypestateChangeFunction<ConcreteState> createTypestateChangeFunction() {
				return new FileMustBeClosedStateMachine();
			}

			@Override
			public IExtendedICFG<Unit, SootMethod> eIcfg() {
				return icfg;
			}

			@Override
			public IDebugger<TypestateDomainValue<ConcreteState>> debugger() {
				return new IDEDebugger<>();
			}
		});
	}
	
	private <V> Transformer createAnalysisComputationTransformer() {
		return new SceneTransformer() {
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				icfg = new ExtendedICFG(new JimpleBasedInterproceduralCFG(true));
				analysis = createAnalysis();
				analysis.run();
				computeResultsPhaseOne = analysis.phaseOneResults();
				computeResultsPhaseTwo = analysis.phaseTwoResults();
			}
		};
	}
	
	private Transformer createAnalysisUpdateTransformer() {
		return new SceneTransformer() {
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				icfg = new ExtendedICFG(new JimpleBasedInterproceduralCFG(true));
				analysis = createAnalysis();
				analysis.run();
				try {
					Files.copy(Paths.get(updatedCodePath), codePath, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
				patchGraph();
				icfg = new ExtendedICFG(new JimpleBasedInterproceduralCFG(true));
				analysis.update(icfg);
				updateResultsPhaseOne = analysis.phaseOneResults();
				updateResultsPhaseTwo = analysis.phaseTwoResults();
			}
		};
	}
	
	private void initializeSoot() {
		Options.v().set_whole_program(true);
		Options.v().setPhaseOption("jb", "use-original-names:true");
		Options.v().setPhaseOption("cg.spark", "on");
		Options.v().setPhaseOption("cg.spark", "verbose:true");
		Options.v().set_output_format(Options.output_format_none);
		String sootCp;
		sootCp = codePath.toString();
		if (includeJDK()) {
			String javaHome = System.getProperty("java.home");
			if (javaHome == null || javaHome.equals(""))
				throw new RuntimeException("Could not get property java.home!");
			sootCp += File.pathSeparator + javaHome + "/lib/rt.jar";
			Options.v().setPhaseOption("cg", "trim-clinit:false");
			Options.v().set_no_bodies_for_excluded(true);
			Options.v().set_allow_phantom_refs(true);
			
			List<String> includeList = new LinkedList<String>();
			includeList.add("java.lang.*");
			includeList.add("java.util.*");
			includeList.add("java.io.*");
			includeList.add("sun.misc.*");
			includeList.add("java.net.*");
			includeList.add("javax.servlet.*");
			includeList.add("javax.crypto.*");

			Options.v().set_include(includeList);

		} else {
			Options.v().set_no_bodies_for_excluded(true);
			Options.v().set_allow_phantom_refs(true);
			 Options.v().setPhaseOption("cg", "all-reachable:true");
		}

//		Options.v().set_src_prec(Options.src_prec_java);
		Options.v().set_exclude(excludedPackages());
		Options.v().set_soot_classpath(sootCp);
		
		System.out.println("soot CP " + sootCp);
		
		SootClass c = Scene.v().forceResolve(testClassName, SootClass.BODIES);
		Scene.v().loadNecessaryClasses();
		if (c != null) {
			c.setApplicationClass();
		}
		
		if(c.getMethods().get(0).equals(c.getMethodByName("main")))
			sootTestMethod = c.getMethods().get(1);
		else
			sootTestMethod = c.getMethods().get(0);
		
		SootMethod methodByName = c.getMethodByName("main");
		List<SootMethod> ePoints = new LinkedList<>();
		ePoints.add(methodByName);
		Scene.v().setEntryPoints(ePoints);
	}
	
	protected boolean includeJDK() {
		return false;
	}

	public List<String> excludedPackages() {
		List<String> excludedPackages = new LinkedList<>();
		excludedPackages.add("sun.*");
		excludedPackages.add("javax.*");
		excludedPackages.add("com.sun.*");
		excludedPackages.add("com.ibm.*");
		excludedPackages.add("org.xml.*");
		excludedPackages.add("org.w3c.*");
		excludedPackages.add("apple.awt.*");
		excludedPackages.add("com.apple.*");
		return excludedPackages;
	}

	private boolean compareResultsofVersions() {
		System.out.println("-------------------------------------------------STEP 1-------------------------------------------------");
		computeResults();
		System.out.println("-------------------------------------------------STEP 2-------------------------------------------------");
		updateResults();
		System.out.println("-------------------------------------------------STEP 3-------------------------------------------------");
		return compareResults();
	}
	
	private <V> boolean compareResults() {
		boolean compareFlag = false; 
		System.out.println("computeResults " + computeResultsPhaseTwo);
		System.out.println("updateResults  " + updateResultsPhaseTwo);
		System.out.println();
		
		if(computeResultsPhaseTwo.size() != updateResultsPhaseTwo.size()) {
			System.out.println("Number of Seeds do not match");
			return false;
		}
		for(int seedCount = 0; seedCount < computeResultsPhaseTwo.size(); seedCount++) {
			Table<UpdatableWrapper<Unit>, AccessGraph, TypestateDomainValue<ConcreteState>> computeTable = computeResultsPhaseTwo.get(seedCount);
			Table<UpdatableWrapper<Unit>, AccessGraph, TypestateDomainValue<ConcreteState>> updateTable = updateResultsPhaseTwo.get(seedCount);
			
			if(computeTable.size() != updateTable.size()) {
				System.out.println("completed comparing the compute and update results and the results are " + (compareFlag ? "equal" : "not equal"));
				return compareFlag;
			}
			else
				compareFlag = true;
			
			Set<Cell<UpdatableWrapper<Unit>, AccessGraph, TypestateDomainValue<ConcreteState>>> computeTableCellSet = computeTable.cellSet();
			Set<Cell<UpdatableWrapper<Unit>, AccessGraph, TypestateDomainValue<ConcreteState>>> updateTableCellSet = updateTable.cellSet();
			
			for (Cell<UpdatableWrapper<Unit>, AccessGraph, TypestateDomainValue<ConcreteState>> computeCell : computeTableCellSet) {
				boolean present = false;
				for (Cell<UpdatableWrapper<Unit>, AccessGraph, TypestateDomainValue<ConcreteState>> updateCell : updateTableCellSet) {
					if(computeCell.toString().contentEquals(updateCell.toString())) {
						present = true;
						break;
					}
				}
				if(!present) {
					compareFlag = false;
					break;
				}
			}
		}
		System.out.println("completed comparing the compute and update results and the results are " + (compareFlag ? "equal" : "not equal"));
		return compareFlag;
	}

	private void patchGraph() {
		final boolean AGGRESSIVE_CHECKS = true;
		
		// Get the original call graph size before we change anything
		System.out.println("Original call graph has " + Scene.v().getCallGraph().size() +  " edges");
			
		// Mark all existing compilation units as unresolved
		Program program = SootResolver.v().getProgram();
		for (CompilationUnit cu : program.getCompilationUnits())
			program.releaseCompilationUnitForFile(cu.pathName());

		// Load a new version of the source file into Soot

		// Release some stale scene information
		try {
			Field vcField = Singletons.class.getDeclaredField("instance_soot_jimple_toolkits_callgraph_VirtualCalls");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_toolkits_pointer_DumbPointerAnalysis");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_toolkits_pointer_FullObjectSet");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			
			vcField = Singletons.class.getDeclaredField("instance_soot_EntryPoints");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);

			vcField = Scene.class.getDeclaredField("doneResolving");
			vcField.setAccessible(true);
			vcField.set(Scene.v(), false);
			
			// Spark data
			Method methClear = HashMap.class.getMethod("clear");
			vcField = G.class.getDeclaredField("Parm_pairToElement");
			vcField.setAccessible(true);
			methClear.invoke(vcField.get(G.v()), (Object[]) null);

			vcField = G.class.getDeclaredField("MethodPAG_methodToPag");
			vcField.setAccessible(true);
			methClear.invoke(vcField.get(G.v()), (Object[]) null);
			
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_spark_sets_AllSharedListNodes");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_spark_sets_AllSharedHybridNodes");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_spark_fieldrw_FieldTagger");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_spark_pag_ArrayElement");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_spark_fieldrw_FieldReadTagAggregator");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_spark_fieldrw_FieldWriteTagAggregator");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_spark_fieldrw_FieldTagAggregator");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_spark_sets_EmptyPointsToSet");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_spark_SparkTransformer");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			
			vcField = Singletons.class.getDeclaredField("instance_soot_jimple_toolkits_pointer_FullObjectSet");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);

			vcField = G.class.getDeclaredField("newSetFactory");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
			vcField = G.class.getDeclaredField("oldSetFactory");
			vcField.setAccessible(true);
			vcField.set(G.v(), null);
		} catch (NoSuchFieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene.v().setDefaultThrowAnalysis(null);
		Scene.v().releaseFastHierarchy();
		Scene.v().releaseReachableMethods();
		Scene.v().releaseActiveHierarchy();
		Scene.v().releasePointsToAnalysis();
		Scene.v().releaseCallGraph();
		Scene.v().setEntryPoints(null);

		// Force a resolve of all soot classes in the scene. We
		// need to copy the list to avoid ConcurrentModificationExceptions.
		Set<SootClass> ac = new HashSet<SootClass>();
		Set<SootClass> lc = new HashSet<SootClass>();
		Set<SootClass> allClasses = new HashSet<SootClass>();
		Set<String> methodBodies = new HashSet<String>();
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			ac.add(sc);
			allClasses.add(sc);
		}
		for (SootClass sc : Scene.v().getLibraryClasses()) {
			lc.add(sc);
			allClasses.add(sc);
		}
		for (SootClass sc : Scene.v().getClasses())
			allClasses.add(sc);
		for (SootClass sc : allClasses)
			for (SootMethod sm : sc.getMethods())
				if (sm.hasActiveBody())
					methodBodies.add(sm.getSignature());
		for (SootClass sc : allClasses) {
			// Remove the class from the scene so that it can be
			// added anew. This helps fixing Soot's internal caches.
			Scene.v().removeClass(sc);
			assert !Scene.v().containsClass(sc.getName());

			// Let the class think it has not been resolved yet. This
			// is important as resolving is aborted if the current
			// resolving level is greater than or equal to the requested
			// one.
			sc.setResolvingLevel(SootClass.DANGLING);
		}

		// Make sure that we load all class dependencies of the new version
		Scene.v().loadNecessaryClasses();

		// Reload all application classes
		List<SootClass> newClasses = new ArrayList<SootClass>();
		for (SootClass sc : allClasses) {
			// Force a new class resolving
			SootClass scNew;
			try {
				scNew = Scene.v().forceResolve(sc.getName(), SootClass.SIGNATURES);
			}
			catch (Exception ex) {
				// The class might have been removed
				System.err.println("Could not load class " + sc + ", skipping...");
				continue;
			}
//				SootClass scNew = Scene.v().forceResolve(sc.getName(), SootClass.BODIES);
			assert scNew != null;
			if (ac.contains(sc))
				scNew.setApplicationClass();
			if (lc.contains(sc))
				scNew.setLibraryClass();
			assert !AGGRESSIVE_CHECKS || scNew != ac;
			assert scNew.isInScene();
			assert Scene.v().getSootClass(sc.getName()) == scNew;
			newClasses.add(scNew);

			for (SootMethod sm : scNew.getMethods())
				if (sm.isConcrete() && methodBodies.contains(sm.getSignature()))
					sm.retrieveActiveBody();
		}
		for (SootClass sc : Scene.v().getClasses())
			if (sc.resolvingLevel() < SootClass.SIGNATURES) {
				Scene.v().forceResolve(sc.getName(), SootClass.SIGNATURES);
				System.out.println("Reloaded class " + sc.getName());
			}
		
		// Fix cached main class - this will automatically fix the main method
		SootClass oldMainClass = Scene.v().getMainClass();
		SootClass mainClass = Scene.v().getSootClass(oldMainClass.getName());
		Scene.v().setMainClass(mainClass);
		System.out.println("Old main class: " + oldMainClass + " - new: " + mainClass);
		assert !AGGRESSIVE_CHECKS || !oldMainClass.isInScene();

		// Patch the entry points
		long timeBeforeEP = System.nanoTime();
		Scene.v().getEntryPoints();
		System.out.println("Updating entry points took "
				+ ((System.nanoTime() - timeBeforeEP) / 1E9) + " seconds");

		// Recreate the exception throw analysis
		Scene.v().getDefaultThrowAnalysis();
		
		// Update the call graph
		long timeBeforeCG = System.nanoTime();
		PackManager.v().getPack("cg").apply();
		int cgSize = Scene.v().getCallGraph().size();
		System.out.println("Updating callgraph took "
				+ ((System.nanoTime() - timeBeforeCG) / 1E9) + " seconds, "
				+ "callgraph now has " + cgSize + " edges.");

		// Invalidate the list of reachable methods. It will automatically be recreated
		// on the next call to "getReachableMethods".
		long timeBeforeRM = System.nanoTime();
		Scene.v().getReachableMethods();
		System.out.println("Updating reachable methods took "
				+ ((System.nanoTime() - timeBeforeRM) / 1E9) + " seconds");
		
		// Update the class hierarchy
		Scene.v().getActiveHierarchy();
		
		List<MethodOrMethodContext> eps = new ArrayList<MethodOrMethodContext>();
		eps.addAll(Scene.v().getEntryPoints());
		ReachableMethods reachableMethods = new ReachableMethods(Scene.v().getCallGraph(), eps.iterator());
		reachableMethods.update();
		
		// Fix the resolving state for the old classes. Otherwise, access to the
		// fields and methods will be blocked and no diff can be performed.
		for (SootClass sc : allClasses)
			sc.setResolvingLevel(SootClass.BODIES);
	}
}
