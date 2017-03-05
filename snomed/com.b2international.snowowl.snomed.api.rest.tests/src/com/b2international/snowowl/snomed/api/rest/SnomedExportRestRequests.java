/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 5.4
 */
public abstract class SnomedExportRestRequests extends AbstractSnomedApiTest {

	public static ValidatableResponse createExport(final Map<?, ?> exportConfiguration) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(ContentType.JSON)
				.body(exportConfiguration)
				.post("/exports")
				.then();
	}

	public static String getExportId(ValidatableResponse response) {
		return lastPathSegment(response.statusCode(201).extract().header("Location"));
	}

	public static ValidatableResponse getExport(String exportId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.when().get("/exports/{id}", exportId)
				.then();
	}

	public static File getExportFile(final String exportId) throws Exception {

		File tmpDir = null;
		File exportArchive = null;

		try {

			final InputSupplier<InputStream> supplier = new InputSupplier<InputStream>() {
				@Override
				public InputStream getInput() throws IOException {
					return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
							.contentType(ContentType.JSON)
							.get("/exports/{id}/archive", exportId)
							.thenReturn()
							.asInputStream();
				}
			};

			tmpDir = Files.createTempDir();
			exportArchive = new File(tmpDir, "export.zip");
			Files.copy(supplier, exportArchive);

		} catch (final Exception e) {
			throw e;
		} finally {
			if (tmpDir != null) {
				tmpDir.deleteOnExit();
			}

			if (exportArchive != null) {
				exportArchive.deleteOnExit();
			}
		}

		assertNotNull(exportArchive);
		return exportArchive;
	}

	private SnomedExportRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
