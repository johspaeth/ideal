package typestate.tests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.junit.Test;

import test.IDEALTestingFramework;
import typestate.ap.ConcreteState;
import typestate.ap.TypestateChangeFunction;
import typestate.ap.impl.statemachines.PrintWriterStateMachine;

public class PrintWriterTest extends IDEALTestingFramework {

	@Test
	public void test1() throws FileNotFoundException {
		PrintWriter inputStream = new PrintWriter("");
		inputStream.close();
		inputStream.flush();
		mustBeInErrorState(inputStream);
	}

	@Override
	protected TypestateChangeFunction<ConcreteState> createTypestateChangeFunction() {
		return new PrintWriterStateMachine();
	}

}