package com.b2international.snowowl.api.japi.cdo;

import static org.junit.Assert.*;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.session.CDOSession;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;

public class RenameCDOBranchTest {

	@Test
	public void testRenameBranch() throws Exception {
		
		ICDOConnection connection = ApplicationContext.getServiceForClass(ICDOConnectionManager.class).getByUuid("snomedStore");

		CDOSession session = connection.getSession();
		CDOBranch mainBranch = session.getBranchManager().getMainBranch();
		CDOBranch branch = mainBranch.createBranch("testing");
		branch.rename("renamed");
		session.close();
		
		session = connection.getSession();
		CDOBranch renamedBranch = session.getBranchManager().getBranch("MAIN/renamed");
		assertNotNull(renamedBranch);

		CDOBranch testingBranch = session.getBranchManager().getBranch("MAIN/testing");
		assertNull(testingBranch);

		try {
			session.getBranchManager().getMainBranch().rename("test");
			fail("Main branch can't be renamed");
		} catch (Exception expected) {
			// SUCCESS
		}

		String name = session.getBranchManager().getMainBranch().getName();
		assertEquals("Main branch can't be renamed", CDOBranch.MAIN_BRANCH_NAME, name);
	}
}
