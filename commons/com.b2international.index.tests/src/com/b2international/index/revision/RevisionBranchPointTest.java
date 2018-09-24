package com.b2international.index.revision;

import static org.junit.Assert.*;

import org.junit.Test;

public class RevisionBranchPointTest {

	@Test
	public void serialization() throws Exception {
		RevisionBranchPoint original = new RevisionBranchPoint(1024, 2048);
		assertEquals(original, RevisionBranchPoint.valueOf(original.toIpAddress()));
	}
	
}
