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
package com.b2international.snowowl.snomed.mrcm.core.server.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.junit.Test;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.datastore.MrcmEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.mrcm.core.server.XMIMrcmExporter;
import com.b2international.snowowl.snomed.mrcm.core.server.XMIMrcmImporter;

/**
 * @since 4.4
 */
public class MrcmImportExportTest {

	@Test
	public void importTest() throws Exception {
		// default/old MRCM import file contains 58 rules
		final IBranchPath branch = BranchPathUtils.createMainPath();
		final File defaultMrcmFile = new File(PlatformUtil.toAbsolutePath(MrcmImportExportTest.class, "mrcm_defaults.xmi"));
		
		new XMIMrcmImporter().doImport("test", defaultMrcmFile);
		
		// verify CDO content
		try (MrcmEditingContext context = new MrcmEditingContext(branch)) {
			assertEquals(58, context.getConceptModel().getConstraints().size());
		}
		// verify index
		final Collection<PredicateIndexEntry> allPredicates = ApplicationContext.getServiceForClass(SnomedPredicateBrowser.class).getAllPredicates(branch);
		assertEquals(58, allPredicates.size());
	}
	
	@Test
	public void exportTest() throws Exception {
		importTest();
		
		final Path exportedFile = new XMIMrcmExporter().doExport("test", Paths.get("target"));
		assertTrue(exportedFile.toFile().exists());
	}
	
}
