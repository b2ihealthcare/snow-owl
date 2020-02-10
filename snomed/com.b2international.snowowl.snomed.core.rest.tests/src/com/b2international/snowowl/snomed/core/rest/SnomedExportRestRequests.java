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
package com.b2international.snowowl.snomed.core.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.b2international.snowowl.test.commons.rest.AbstractApiTest;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 5.4
 */
public abstract class SnomedExportRestRequests extends AbstractApiTest {

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

			tmpDir = Files.createTempDir();
			exportArchive = new File(tmpDir, "export.zip");
			new ByteSource() {
				@Override
				public InputStream openStream() throws IOException {
					return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
							.contentType(ContentType.JSON)
							.get("/exports/{id}/archive", exportId)
							.thenReturn()
							.asInputStream();
				}
			}.copyTo(Files.asByteSink(exportArchive));

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
