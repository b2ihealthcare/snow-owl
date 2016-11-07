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
package com.b2international.snowowl.snomed.api.rest.io;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.expectStatus;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.b2international.commons.Pair;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 5.4
 */
public abstract class AbstractSnomedExportApiTest extends AbstractSnomedApiTest {

	private static final String EXPORTS_POST_ENDPOINT = "/exports";
	private static final String EXPORTS_GET_STATUS_ENDPOINT = "/exports/{id}";
	private static final String EXPORTS_GET_ARCHIVE_ENDPOINT= "/exports/{id}/archive";

	protected Response whenCreatingExportConfiguration(final Map<?, ?> exportConfiguration) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.with().contentType(ContentType.JSON)
				.and().body(exportConfiguration)
				.when().post(EXPORTS_POST_ENDPOINT);
	}
	
	protected String assertExportConfigurationCanBeCreated(final Map<?, ?> exportConfiguration) {
		final Response response = whenCreatingExportConfiguration(exportConfiguration);
		final String location = expectStatus(response, 201).and().extract().response().header("Location");
		return lastPathSegment(location);
	}
	
	protected void assertExportConfigurationFails(final Map<?, ?> exportConfiguration) {
		whenCreatingExportConfiguration(exportConfiguration)
			.then().assertThat().statusCode(400)
			.and().body("status", equalTo(400));
	}
	
	protected ValidatableResponse assertExportConfiguration(final String exportId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.when().get(EXPORTS_GET_STATUS_ENDPOINT, exportId)
				.then().assertThat().statusCode(200);
	}
	
	protected File assertExportFileCreated(final String exportId) {
		
		File tmpDir = null;
		File exportArchive = null;
		
		try {
			
			final InputSupplier<InputStream> supplier = new InputSupplier<InputStream>() {
				@Override
				public InputStream getInput() throws IOException {
					return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
							.with().contentType(ContentType.JSON)
							.when().get(EXPORTS_GET_ARCHIVE_ENDPOINT, exportId)
							.thenReturn().asInputStream();
				}
			};
			
			tmpDir = Files.createTempDir();
			exportArchive = new File(tmpDir, "export.zip");
			Files.copy(supplier, exportArchive);
			
		} catch (final Exception e) {
			fail(e.getMessage());
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
	
	protected void assertArchiveContainsLines(final File exportArchive, final Multimap<String, String> fileToLinesMap) {
		
		final Multimap<String, Pair<Boolean, String>> resultMap = ArrayListMultimap.<String, Pair<Boolean, String>>create();
		
		try (FileSystem fs = FileSystems.newFileSystem(exportArchive.toPath(), null)) {
			
			for (final Path path : fs.getRootDirectories()) {
				
				java.nio.file.Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
						
						for (final Entry<String, Collection<String>> entry : fileToLinesMap.asMap().entrySet()) {
							
							if (file.getFileName().toString().startsWith(entry.getKey())) {
								
								final List<String> lines = java.nio.file.Files.readAllLines(file, Charsets.UTF_8);
								
								for (final String line : entry.getValue()) {
									if (lines.contains(line)) {
										resultMap.put(entry.getKey(), Pair.of(true, line));
									} else {
										resultMap.put(entry.getKey(), Pair.of(false, line));
									}
								}
								
								break;
							}
							
						}
						
						return super.visitFile(file, attrs);
					}
				});
				
			}
			
		} catch (final Exception e) {
			fail(e.getMessage());
		}
		
		final Set<String> difference = Sets.difference(fileToLinesMap.keySet(), resultMap.keySet());
		
		assertTrue(String.format("File(s) starting with <%s> are missing from the export archive", Joiner.on(", ").join(difference)), difference.isEmpty());
		
		for (final Entry<String, Collection<Pair<Boolean, String>>> entry : resultMap.asMap().entrySet()) {
			
			for (final Pair<Boolean, String> pair : entry.getValue()) {
				assertEquals(String.format("Line: %s must be contained in %s", pair.getB(), entry.getKey()), true, pair.getA());
			}
			
		}
	}
	
}
