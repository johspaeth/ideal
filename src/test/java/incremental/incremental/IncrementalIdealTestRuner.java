package incremental;

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
import soot.Type;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;

public class IncrementalIdealTestRuner {
	protected SootMethod sootTestMethod;

	@Rule
	public TestName testMethodName = new TestName();
	
	@Before
	public void prepareTest() {
		System.out.println("test name " + testMethodName.getMethodName());
		System.out.println("target class " + getTargetClass());
		org.junit.Assume.assumeTrue(false);
		Assert.fail("no methods yet");
	}
	
	private String getTargetClass() {
		SootClass sootClass = new SootClass("dummyClass");
		SootMethod mainMethod = new SootMethod("main",
				Arrays.asList(new Type[] { ArrayType.v(RefType.v("java.lang.String"), 1) }), VoidType.v(),
				Modifier.PUBLIC | Modifier.STATIC);
		sootClass.addMethod(mainMethod);
		JimpleBody body = Jimple.v().newBody(mainMethod);
		mainMethod.setActiveBody(body);
		RefType testCaseType = RefType.v(getTestCaseClassName());
		System.out.println(getTestCaseClassName());
		Local allocatedTestObj = Jimple.v().newLocal("dummyObj", testCaseType);
		body.getLocals().add(allocatedTestObj);
		body.getUnits().add(Jimple.v().newAssignStmt(allocatedTestObj, Jimple.v().newNewExpr(testCaseType)));
		
		SootClass sootTestCaseClass = Scene.v().forceResolve(getTestCaseClassName(), SootClass.BODIES);

		for (SootMethod m : sootTestCaseClass.getMethods()) {
			if (m.getName().equals(testMethodName.getMethodName()))
				sootTestMethod = m;
		}
		if (sootTestMethod == null)
			throw new RuntimeException("The method with name " + testMethodName.getMethodName() + " was not found in the Soot Scene.");
		
		body.getUnits().add(
				Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(allocatedTestObj, sootTestMethod.makeRef())));
		Scene.v().addClass(sootClass);
		System.out.println("method count " + sootClass.getMethodCount());
		System.out.println("methods " + sootClass.getMethods());
		return sootClass.toString();
	}
	
	private String getTestCaseClassName() {
		System.out.println(this.getClass().getName().replace("class ", ""));
		return this.getClass().getName().replace("class ", "");
	}
}
