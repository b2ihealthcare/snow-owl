/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Test;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmExportFormat;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmExporter;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmImporter;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 4.4
 */
public class MrcmImportExportTest {

	@Test
	public void importTest() throws Exception {
		// default/old MRCM import file contains 58 rules
		final IBranchPath branch = BranchPathUtils.createMainPath();
		final Path path = Paths.get(PlatformUtil.toAbsolutePath(MrcmImportExportTest.class, "mrcm_defaults.xmi"));
		
		try (final InputStream stream = Files.newInputStream(path, StandardOpenOption.READ)) {
			Services.service(MrcmImporter.class).doImport("test", stream);
		} 
		
		
		// verify CDO content
		try (SnomedEditingContext context = new SnomedEditingContext(branch)) {
			assertEquals(58, context.getConstraints().size());
		}
		// verify index
		final SnomedConstraints allPredicates = SnomedRequests.prepareSearchConstraint()
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
		assertEquals(58, allPredicates.getTotal());
	}
	
	@Test
	public void exportTest() throws Exception {
		importTest();
		
		Path target = Paths.get("target");
		target.toFile().mkdirs();
		Path exportedFile = target.resolve("mrcm_" + Dates.now() + ".xmi");
		assertFalse(exportedFile.toFile().exists());
		try (final OutputStream stream = Files.newOutputStream(exportedFile, StandardOpenOption.CREATE_NEW)) {
			Services.service(MrcmExporter.class).doExport("test", stream, MrcmExportFormat.XMI);
		}
		assertTrue(exportedFile.toFile().exists());
		
		target = Paths.get("target");
		target.toFile().mkdirs();
		exportedFile = target.resolve("mrcm_" + Dates.now() + ".csv");
		assertFalse(exportedFile.toFile().exists());
		try (final OutputStream stream = Files.newOutputStream(exportedFile, StandardOpenOption.CREATE_NEW)) {
			Services.service(MrcmExporter.class).doExport("test", stream, MrcmExportFormat.CSV);
		}
		assertTrue(exportedFile.toFile().exists());
		
	}
	
}
