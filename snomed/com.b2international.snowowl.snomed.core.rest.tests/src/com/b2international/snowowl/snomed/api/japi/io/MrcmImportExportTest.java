/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.japi.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmExportFormat;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmExporter;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmImporter;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 4.4
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MrcmImportExportTest {

	@Test
	public void _01_importTest() throws Exception {
		// default/old MRCM import file contains 58 rules
		final IBranchPath branch = BranchPathUtils.createMainPath();
		final Path path = PlatformUtil.toAbsolutePath(MrcmImportExportTest.class, "mrcm_import_test.json");
		
		try (final InputStream stream = Files.newInputStream(path, StandardOpenOption.READ)) {
			Services.service(MrcmImporter.class).doImport("test", stream);
		} 
		
		// verify content
		int numberOfConstraints = SnomedRequests.prepareSearchConstraint()
			.setLimit(0)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch.getPath())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync()
			.getTotal();
		assertEquals(100, numberOfConstraints);
	}
	
	@Test
	public void _02_exportTest() throws Exception {
		Path target = Paths.get("target");
		target.toFile().mkdirs();
		Path exportedFile = target.resolve("mrcm_" + Dates.now() + ".csv");
		assertFalse(exportedFile.toFile().exists());
		try (final OutputStream stream = Files.newOutputStream(exportedFile, StandardOpenOption.CREATE_NEW)) {
			Services.service(MrcmExporter.class).doExport("test", stream, MrcmExportFormat.CSV);
		}
		assertTrue(exportedFile.toFile().exists());
	}
	
}
