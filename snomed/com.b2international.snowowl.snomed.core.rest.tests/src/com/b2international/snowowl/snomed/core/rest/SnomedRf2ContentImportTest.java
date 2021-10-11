/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.test.commons.Resources;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 7.11
 */
public class SnomedRf2ContentImportTest extends AbstractSnomedApiTest {

	private static final Splitter TAB_SPLITTER = Splitter.on('\t');
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	private static final Set<String> UNSUPPORTED_FILE_TYPES = Set.of("sct2_Identifier", "der2_cciRefset", "Readme");

	private static Multimap<String, String> originalLines;

	@BeforeClass
	public static void beforeClass() throws IOException {
		final File miniFile = PlatformUtil.toAbsolutePathBundleEntry(Resources.class, Resources.Snomed.MINI_RF2_INT_20210731).toFile();
		originalLines = getLines(miniFile);

	}

	@AfterClass
	public static void afterClass() {
		originalLines.clear();
		originalLines = null;
	}

	@Test
	public void verifyFullContent() throws Exception {
		final Map<String, Object> config = ImmutableMap.of(
				"type", Rf2ReleaseType.FULL.name(),
				"includeUnpublished", false
			);
		final File result = SnomedExportRestRequests.doExport(branchPath, config);
		assertNotNull(result);
		assertRf2Equals(originalLines, getLines(result));
	}


	@Test
	public void verifySnapshotContent() throws Exception {
		final Map<String, Object> config = ImmutableMap.of(
				"type", Rf2ReleaseType.SNAPSHOT.name(),
				"includeUnpublished", false
			);
		final File result = SnomedExportRestRequests.doExport(branchPath, config);
		assertNotNull(result);
		assertRf2Equals(getSnapshotLines(originalLines), getLines(result));
	}

	@Test
	public void verifyDeltaContent() throws Exception {
		final Map<String, Object> config = ImmutableMap.of(
				"type", Rf2ReleaseType.DELTA.name(),
				"startEffectiveTime", "20200131",
				"endEffectiveTime", "20200131",
				"includeUnpublished", false
			);
		final File result = SnomedExportRestRequests.doExport(branchPath, config);
		assertNotNull(result);

		final Date effectiveTime = DATE_FORMAT.parse("20200131");

		assertRf2Equals(getDeltaLines(originalLines, effectiveTime, effectiveTime), getLines(result));
	}
	
	@Test
	public void verifyAutomaticDialectConfiguration() throws Exception {
		final Map<String, Object> settings = CodeSystemRequests.prepareGetCodeSystem(SnomedContentRule.SNOMEDCT_ID)
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES)
			.getSettings();
		assertThat(settings)
			.containsEntry(SnomedTerminologyComponentConstants.CODESYSTEM_LANGUAGE_CONFIG_KEY, List.of(
				// this entry is pre-configured, should be kept after import
				Map.of(
					"languageTag", "en",
					"languageRefSetIds", List.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US)
				),
				// these two entries come from the default dialect configuration map, should be appended
				Map.of(
					"languageTag", "en-gb", 
					"languageRefSetIds", List.of(Concepts.REFSET_LANGUAGE_TYPE_UK)
				),
				Map.of(
					"languageTag", "en-us", 
					"languageRefSetIds", List.of(Concepts.REFSET_LANGUAGE_TYPE_US)
				)
			));
	}

	private void assertRf2Equals(final Multimap<String, String> expected, final Multimap<String, String> result) {

		final Multimap<String, String> missingFromResult = Multimaps.filterEntries(expected, entry -> !result.containsEntry(entry.getKey(), entry.getValue()));

		if (!missingFromResult.isEmpty()) {
			missingFromResult.asMap().forEach((k,v) -> v.forEach(line -> System.err.println("Missing from result: " + line)));
		}

		final Multimap<String, String> missingFromExpected = Multimaps.filterEntries(result, entry -> !expected.containsEntry(entry.getKey(), entry.getValue()));

		if (!missingFromExpected.isEmpty()) {
			missingFromExpected.asMap().forEach((k,v) -> v.forEach(line -> System.err.println("Missing from expected: " + line)));
		}

		assertTrue(missingFromResult.isEmpty());
		assertTrue(missingFromExpected.isEmpty());

	}

	private static Multimap<String, String> getDeltaLines(final Multimap<String, String> lines, final Date startEffectiveTime, final Date endEffectiveTime) throws IOException, ParseException {

		final Multimap<String, String> deltaLines = HashMultimap.<String, String>create();

		for (final Entry<String, Collection<String>> entry : lines.asMap().entrySet()) {

			final String header = entry.getKey();
			final Collection<String> fileLines = entry.getValue();

			final Multimap<String, String> idToLineMap = Multimaps.index(fileLines, line -> TAB_SPLITTER.split(line).iterator().next());

			for (final Entry<String, Collection<String>> idEntry : idToLineMap.asMap().entrySet()) {

				final Collection<String> idLines = idEntry.getValue();

				for (final String line : idLines) {

					final Date effectiveTime = DATE_FORMAT.parse(TAB_SPLITTER.splitToList(line).get(1));

					if (effectiveTime.compareTo(startEffectiveTime) == 0
							|| effectiveTime.compareTo(endEffectiveTime) == 0
							|| (effectiveTime.after(startEffectiveTime) && effectiveTime.before(endEffectiveTime))) {

						deltaLines.put(header, line);

					}

				}

			}

		}

		return deltaLines;

	}

	private static Multimap<String, String> getSnapshotLines(final Multimap<String, String> lines) throws IOException {

		final Multimap<String, String> snapshotLines = HashMultimap.<String, String>create();

		for (final Entry<String, Collection<String>> entry : lines.asMap().entrySet()) {

			final String header = entry.getKey();
			final Collection<String> fileLines = entry.getValue();

			final Multimap<String, String> idToLineMap = Multimaps.index(fileLines, line -> TAB_SPLITTER.split(line).iterator().next());

			for (final Entry<String, Collection<String>> idEntry : idToLineMap.asMap().entrySet()) {

				final Collection<String> idLines = idEntry.getValue();

				if (idLines.size() == 1) {
					snapshotLines.putAll(header, idLines);
				} else {

					final Optional<String> lastLine = idLines.stream()
							.max( (l1,l2) -> {
								try {
									return DATE_FORMAT.parse(TAB_SPLITTER.splitToList(l1).get(1)).compareTo(DATE_FORMAT.parse(TAB_SPLITTER.splitToList(l2).get(1)));
								} catch (final ParseException e) {
									return 0;
								}
							});

					if (lastLine.isPresent()) {
						snapshotLines.put(header, lastLine.get());
					}

				}

			}

		}

		return snapshotLines;

	}

	private static Multimap<String, String> getLines(final File input) throws IOException {
		final Multimap<String, String> lines = HashMultimap.create();

		try (FileSystem fs = FileSystems.newFileSystem(input.toPath(), SnomedRf2ContentImportTest.class.getClassLoader())) {
			for (final Path path : fs.getRootDirectories()) {
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {

						if (UNSUPPORTED_FILE_TYPES.stream().noneMatch(fileNamePrefix -> file.getFileName().toString().startsWith(fileNamePrefix))) {

							String header;

							try (Stream<String> linesInFile = Files.lines(file)) {
								header = linesInFile.findFirst().get();
							}

							try (Stream<String> linesInFile = Files.lines(file)) {
								linesInFile.skip(1).forEach(line -> lines.put(header, line));
							}

						}

						return super.visitFile(file, attrs);
					}
				});

			}

		}

		return lines;

	}

}
