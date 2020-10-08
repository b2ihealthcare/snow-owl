/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.google.common.collect.ImmutableMap;

/**
 * @since 7.11
 */
public class SnomedRf2ContentImportTest extends AbstractSnomedApiTest {

	@Test
	public void verifyFullContent() throws Exception {
		final Map<String, Object> config = ImmutableMap.of(
			"type", Rf2ReleaseType.FULL.name(),
			"includeUnpublished", false
		);
		File result = SnomedExportRestRequests.doExport(branchPath, config);
		assertNotNull(result);
		// TODO verify file is semantically equivalent with the original RF2 import source
	}
	
	@Test
	public void verifySnapshotContent() throws Exception {
		final Map<String, Object> config = ImmutableMap.of(
			"type", Rf2ReleaseType.SNAPSHOT.name(),
			"includeUnpublished", true
		);
		File result = SnomedExportRestRequests.doExport(branchPath, config);
		assertNotNull(result);
		// TODO verify file is semantically equivalent with the original RF2 import source
	}
	
}
