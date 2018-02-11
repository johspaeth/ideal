package typestate.incremental.tests;

import org.junit.Before;
import org.junit.Test;

import incremental.IncrementalIdealTestRuner;
import test.IncrementalIDEALTest;
import typestate.test.helper.File;

public class FileMustBeClosedTest extends IncrementalIdealTestRuner {
	
	@Test
	public void simple0() {
		File file = new File();
		File alias = file;
		file.open();
		alias.close();
	}
	
	@Before
	void runTest(String args[]) {
		if(args.length < 3) {
			System.out.println("Invoke the program with the arguments path_of_initial_jar, path_of_updated_jar, class_name");
			System.exit(1);
		}
		String initialVersion = args[0];
		String updatedVersion = args[1];
		String testClassName = args[2];
		IncrementalIDEALTest test = new IncrementalIDEALTest(initialVersion, updatedVersion, testClassName);
		test.runTestAndCompareResults();
	}
}
