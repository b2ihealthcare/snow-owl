/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.ft.FeatureToggles;
import com.b2international.snowowl.core.ft.Features;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.datastore.internal.file.InternalFileRegistry;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration.ImportStatus;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.rf2.importer.Rf2ContentType;
import com.b2international.snowowl.snomed.datastore.request.rf2.importer.Rf2EffectiveTimeSlice;
import com.b2international.snowowl.snomed.datastore.request.rf2.importer.Rf2EffectiveTimeSlices;
import com.b2international.snowowl.snomed.datastore.request.rf2.importer.Rf2Format;
import com.b2international.snowowl.snomed.datastore.request.rf2.importer.Rf2ImportConfiguration;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2GlobalValidator;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationIssueReporter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.0.0
 */
public class SnomedRf2ImportRequest implements Request<BranchContext, Rf2ImportResponse> {

	private static final Logger LOG = LoggerFactory.getLogger("import");
	
	private static final String TXT_EXT = ".txt";
	
	@NotNull
	private final UUID rf2ArchiveId;
	
	@NotNull
	private Rf2ReleaseType type;
	
	@NotEmpty
	private String codeSystemShortName;

	@NotEmpty
	private String userId;
	
	private boolean createVersions = true;

	SnomedRf2ImportRequest(UUID rf2ArchiveId) {
		this.rf2ArchiveId = rf2ArchiveId;
	}
	
	void setReleaseType(Rf2ReleaseType type) {
		this.type = type;
	}
	
	void setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}
	
	void setCreateVersions(boolean createVersions) {
		this.createVersions = createVersions;
	}
	
	void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Override
	public Rf2ImportResponse execute(BranchContext context) {
		final FeatureToggles features = context.service(FeatureToggles.class);
		final String feature = Features.getImportFeatureToggle(SnomedDatastoreActivator.REPOSITORY_UUID, context.branchPath());

		final Rf2ImportConfiguration importConfig = new Rf2ImportConfiguration(userId, createVersions, codeSystemShortName, type);
		try {
			features.enable(feature);
			return doImport(importConfig, context);
		} catch (Exception e) {
			throw new SnowowlRuntimeException(e);
		} finally {
			features.disable(feature);
		}
	}

	Rf2ImportResponse doImport(final Rf2ImportConfiguration importconfig, final BranchContext context) throws Exception {
		final Rf2ValidationIssueReporter reporter = new Rf2ValidationIssueReporter();
		final Rf2ImportResponse response = new Rf2ImportResponse();
		
		try (final DB db = createDb()) {
			final Map<String, Long> storageKeysByComponent = db.hashMap("storageKeysByComponent", Serializer.STRING, Serializer.LONG).create();
			final Map<String, Long> storageKeysByRefSet = db.hashMap("storageKeysByRefSet", Serializer.STRING, Serializer.LONG).create();
			
			if (!isLoadOnDemandEnabled()) {
				Stopwatch w = Stopwatch.createStarted();
				for (Class<?> type : ImmutableList.of(SnomedConceptDocument.class, SnomedDescriptionIndexEntry.class, SnomedRelationshipIndexEntry.class, SnomedRefSetMemberIndexEntry.class)) {
					LOG.info(String.format("Loading available '%s' IDs and StorageKeys...", type.getName()));
					final List<String> fieldsToLoad;
					if (SnomedConceptDocument.class == type) {
						fieldsToLoad = ImmutableList.of(RevisionDocument.Fields.ID, RevisionDocument.Fields.STORAGE_KEY, SnomedConceptDocument.Fields.REFSET_STORAGEKEY);
					} else {
						fieldsToLoad = ImmutableList.of(RevisionDocument.Fields.ID, RevisionDocument.Fields.STORAGE_KEY);
					}
					
					for (Hits<Map> hits : context.service(RevisionSearcher.class).scroll(Query.select(Map.class)
							.from(type)
							.fields(fieldsToLoad)
							.where(Expressions.matchAll())
							.limit(10_000)
							.build())) {
						for (Map hit : hits) {
							final String componentId = (String) hit.get(RevisionDocument.Fields.ID);
							final long storageKey = (long) hit.get(RevisionDocument.Fields.STORAGE_KEY);
							storageKeysByComponent.put(componentId, storageKey);
							// add refset storagekey if this concept has a non negative value
							if (type == SnomedConceptDocument.class && hit.containsKey(SnomedConceptDocument.Fields.REFSET_STORAGEKEY)) {
								final long refSetStorageKey = (long) hit.get(SnomedConceptDocument.Fields.REFSET_STORAGEKEY);
								if (refSetStorageKey != -1L) {
									storageKeysByRefSet.put(componentId, refSetStorageKey);
								}
							}
						}
					}
				}
				LOG.info("Loading available components IDs and StorageKeys took: " + w);
			}
			
			// create executor service to parallel update the underlying index store

			final Rf2EffectiveTimeSlices effectiveTimeSlices = new Rf2EffectiveTimeSlices(db, storageKeysByComponent, storageKeysByRefSet, isLoadOnDemandEnabled());
			Stopwatch w = Stopwatch.createStarted();
			
			final InternalFileRegistry fileReg = (InternalFileRegistry) context.service(FileRegistry.class);
			final File rf2Archive = fileReg.getFile(rf2ArchiveId);
			read(rf2Archive, effectiveTimeSlices, storageKeysByComponent, storageKeysByRefSet, reporter);
			LOG.info("Preparing RF2 import took: " + w);
			w.reset().start();
			
			// log issues with rows
			logValidationIssues(reporter, response);
			
			// run global validation
			final Iterable<Rf2EffectiveTimeSlice> orderedEffectiveTimeSlices = effectiveTimeSlices.consumeInOrder();
			final Rf2GlobalValidator globalValidator = new Rf2GlobalValidator();
			globalValidator.validate(db, orderedEffectiveTimeSlices, reporter, context);
			
			// log global validation issues
			logValidationIssues(reporter, response);
			
			for (Rf2EffectiveTimeSlice slice : orderedEffectiveTimeSlices) {
				slice.doImport(importconfig, context);
			}
			// import completed successfully
			response.setStatus(ImportStatus.COMPLETED);
		}
		return response;
	}

	private void logValidationIssues(final Rf2ValidationIssueReporter reporter, Rf2ImportResponse response) {
		reporter.logWarnings(LOG);
		response.getIssues().addAll(reporter.getIssues());
		if (reporter.getNumberOfErrors() > 0) {
			response.setStatus(ImportStatus.FAILED);
			reporter.logErrors(LOG);
			throw new BadRequestException(String.format("There were %s validation errors with the RF2 import files", reporter.getNumberOfErrors()));
		}
	}
	
	private boolean isLoadOnDemandEnabled() {
		return Rf2ReleaseType.DELTA == type;
	}
	
	private void read(File rf2Archive, Rf2EffectiveTimeSlices slices, Map<String, Long> storageKeysByComponent, Map<String, Long> storageKeysByRefSet, Rf2ValidationIssueReporter reporter) {
		final CsvMapper csvMapper = new CsvMapper();
		csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
		final CsvSchema schema = CsvSchema.emptySchema()
				.withoutQuoteChar()
				.withColumnSeparator('\t')
				.withLineSeparator("\r\n");
		final ObjectReader oReader = csvMapper.readerFor(String[].class).with(schema);

		final Stopwatch w = Stopwatch.createStarted();
		try (final ZipFile zip = new ZipFile(rf2Archive)) {
			for (ZipEntry entry : Collections.list(zip.entries())) {
				final String fileName = Paths.get(entry.getName()).getFileName().toString().toLowerCase();
				if (fileName.contains(type.toString().toLowerCase()) && fileName.endsWith(TXT_EXT)) {
					w.reset().start();
					try (final InputStream in = zip.getInputStream(entry)) {
						readFile(entry, in, oReader, slices, storageKeysByComponent, storageKeysByRefSet, reporter);
					}
					LOG.info(entry.getName() + " - " + w);
				}
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
		
		slices.flushAll();
	}

	private void readFile(ZipEntry entry, final InputStream in, final ObjectReader oReader, Rf2EffectiveTimeSlices effectiveTimeSlices, Map<String, Long> storageKeysByComponent, Map<String, Long> storageKeysByRefSet, Rf2ValidationIssueReporter reporter)
			throws IOException, JsonProcessingException {
		boolean header = true;
		Rf2ContentType<?> resolver = null;
		
		MappingIterator<String[]> mi = oReader.readValues(in);
		while (mi.hasNext()) {
			String[] line = mi.next();
			
			if (header) {
				for (Rf2ContentType<?> contentType : Rf2Format.getContentTypes()) {
					if (contentType.canResolve(line)) {
						resolver = contentType;
						break;
					}
				}

				if (resolver == null) {
					LOG.warn("Unrecognized RF2 file: " + entry.getName());
					break;
				}
				
				header = false;
			} else {
				final String effectiveTime = Strings.isNullOrEmpty(line[1]) ? EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL : line[1];
				resolver.register(line, effectiveTimeSlices.getOrCreate(effectiveTime), reporter);
			}

		}

	}

	private DB createDb() {
		try {
			DB db = DBMaker 
					.fileDB(Files.createTempDirectory(rf2ArchiveId.toString()).resolve("rf2-import.db").toFile())
					.fileDeleteAfterClose()
					.fileMmapEnable()
					.fileMmapPreclearDisable()
					// Unmap (release resources) file when its closed.
					// That can cause JVM crash if file is accessed after it was unmapped
					// (there is possible race condition).
					.cleanerHackEnable()
					.allocateStartSize(256 * 1024*1024)  // 256MB
				    .allocateIncrement(128 * 1024*1024)  // 128MB
					.make();
			
			// preload file content into disk cache
			db.getStore().fileLoad();
			return db;
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Couldn't create temporary db", e);
		}
	}

}
