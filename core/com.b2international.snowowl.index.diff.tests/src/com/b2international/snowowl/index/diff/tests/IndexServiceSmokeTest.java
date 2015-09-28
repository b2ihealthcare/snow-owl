/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.index.diff.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.index.diff.tests.mock.MockIndexServerService;


/**
 * @since 4.3
 */
public class IndexServiceSmokeTest {
	
	private static final String V1_VERSION = "v1";
	private static final String DOC_KEY = "1";
	private static final IBranchPath MAIN = BranchPathUtils.createMainPath();
	private MockIndexServerService service;

	@Before
	public void givenIndexService() {
		service = new MockIndexServerService();
	}
	
	@Test
	public void indexSingleDoc() throws Exception {
		service.indexRelevantDocs(MAIN, DOC_KEY);
		assertEquals(1, service.getAllDocsCount(MAIN));
		assertTrue(service.getAllDocsMap(MAIN).containsKey(DOC_KEY));
	}
	
	@Test
	public void deleteSingleDoc() throws Exception {
		indexSingleDoc();
		service.deleteDocs(MAIN, DOC_KEY);
		assertFalse(service.getAllDocsMap(MAIN).containsKey(DOC_KEY));
		assertEquals(0, service.getAllDocsCount(MAIN));
	}
	
	@Test
	public void tag() throws Exception {
		indexSingleDoc();
		service.tag(V1_VERSION);
		assertEquals(1, service.getAllDocsCount(MAIN));
		assertEquals(1, service.getAllDocsCount(BranchPathUtils.createVersionPath(V1_VERSION)));
	}
	
}
