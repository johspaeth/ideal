package test;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import junit.framework.Assert;
import soot.ArrayType;
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.Type;
import soot.VoidType;
import soot.jimple.JasminClass;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.options.Options;
import soot.util.JasminOutputStream;

public class IncrementalIdealTestRuner {
	protected SootMethod sootTestMethod;

	@Rule
	public TestName testMethodName = new TestName();

	private SootClass sootInitialTestClass;
	private SootClass sootUpdatedTestClass;
	
	private String basePath = "C:\\Users\\Shashank B S\\Documents\\Masters\\classes\\SoSe2017\\Thesis\\Implementaion\\incrementalIDEalTest";
	
	@Before
	public void prepareTest() {
		initializeSootWithEntryPoints();
		
		sootInitialTestClass = Scene.v().forceResolve(getTestCaseClassName(), SootClass.BODIES);
		generateTargetClass(sootInitialTestClass, "Initial");
		
		sootUpdatedTestClass = Scene.v().forceResolve(getTestCaseClassName().replace("initial", "updated"), SootClass.BODIES);
		generateTargetClass(sootUpdatedTestClass, "Updated");
		
		IncrementalIDEALTest test = new IncrementalIDEALTest(basePath + File.separator + "Initial" + File.separator + "sootOutput" + File.separator, basePath + File.separator + "Updated" + File.separator + "sootOutput" + File.separator, "dummyClass");
		test.runTestAndCompareResults();
		
		org.junit.Assume.assumeTrue(false);
		Assert.fail("no methods yet");
	}
	
	private void generateTargetClass(SootClass classToGenerate, String path) {
		SootClass sootClass = new SootClass("dummyClass");
		SootMethod mainMethod = new SootMethod("main",
				Arrays.asList(new Type[] { ArrayType.v(RefType.v("java.lang.String"), 1) }), VoidType.v(),
				Modifier.PUBLIC | Modifier.STATIC);
		sootClass.addMethod(mainMethod);
		JimpleBody body = Jimple.v().newBody(mainMethod);
		mainMethod.setActiveBody(body);
		RefType testCaseType = RefType.v(getTestCaseClassName());
		Local allocatedTestObj = Jimple.v().newLocal("dummyObj", testCaseType);
		body.getLocals().add(allocatedTestObj);
		body.getUnits().add(Jimple.v().newAssignStmt(allocatedTestObj, Jimple.v().newNewExpr(testCaseType)));
		
		for (SootMethod m : classToGenerate.getMethods()) {
			if (m.getName().equals(testMethodName.getMethodName()))
				sootTestMethod = m;
		}
		if (sootTestMethod == null)
			throw new RuntimeException("The method with name " + testMethodName.getMethodName() + " was not found in the Soot Scene.");
		
//		sootClass.addMethod(sootTestMethod);
		body.getUnits().add(
				Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(allocatedTestObj, sootTestMethod.makeRef())));
		body.getUnits().add(
				Jimple.v().newReturnVoidStmt());
		Local param = Jimple.v().newLocal("args", ArrayType.v(RefType.v("java.lang.String"), 1));
		body.getLocals().add(param);
		body.getUnits().insertBefore(
                Jimple.v().newIdentityStmt(param, Jimple.v().newParameterRef(ArrayType.v(RefType.v("java.lang.String"),1), 0)),body.getFirstNonIdentityStmt());

		
		Scene.v().addClass(sootClass);
		System.out.println(sootClass.getMethodCount());
		System.out.println(sootClass.getMethods().get(0).getActiveBody());	
		
		try {
			Files.createDirectories(Paths.get(basePath + File.separator + path + File.separator + "sootOutput"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			String fileName = basePath + File.separator + path + File.separator + SourceLocator.v().getFileNameFor(sootClass, Options.output_format_class);
			JasminOutputStream streamOut = new JasminOutputStream(
					new FileOutputStream(fileName));
			PrintWriter writerOut = new PrintWriter(
					new OutputStreamWriter(streamOut));
			JasminClass jasminClass = new soot.jimple.JasminClass(sootClass);
			jasminClass.print(writerOut);
			writerOut.flush();
			streamOut.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initializeSootWithEntryPoints() {
		soot.G.reset();
		Options.v().set_whole_program(true);
		Options.v().setPhaseOption("jb", "use-original-names:true");
		Options.v().setPhaseOption("cg.spark", "on");
		Options.v().setPhaseOption("cg.spark", "verbose:true");
		Options.v().set_output_format(Options.output_format_none);
		String userdir = System.getProperty("user.dir");
		String sootCp = userdir + "/target/test-classes";
		System.out.println("soot CP " + sootCp);
		
		String javaHome = System.getProperty("java.home");
		if (javaHome == null || javaHome.equals(""))
			throw new RuntimeException("Could not get property java.home!");
		sootCp += File.pathSeparator + javaHome + "/lib/rt.jar";
		Options.v().setPhaseOption("cg", "trim-clinit:false");
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
		
		Options.v().set_soot_classpath(sootCp);
	}
	
	private String getTestCaseClassName() {
		return this.getClass().getName().replace("class ", "");
	}
}
