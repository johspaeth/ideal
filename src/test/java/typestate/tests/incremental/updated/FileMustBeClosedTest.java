package typestate.tests.incremental.updated;

import typestate.test.helper.File;

public class FileMustBeClosedTest {
	public void simple0() {
		File file = new File();
		File alias = file;
		file.open();
		alias.close();
	}
}
