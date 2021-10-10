/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.validation.constraints.NotNull;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DBMaker.Maker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.ApiException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.attachments.InternalAttachmentRegistry;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.locks.Locks;
import com.b2international.snowowl.core.repository.ContentAvailabilityInfoProvider;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.request.io.ImportDefectAcceptor;
import com.b2international.snowowl.core.request.io.ImportDefectAcceptor.ImportDefectBuilder;
import com.b2international.snowowl.core.request.io.ImportResponse;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionUtils;
import com.b2international.snowowl.snomed.datastore.config.SnomedLanguageConfig;
import com.b2international.snowowl.snomed.datastore.index.entry.*;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.importer.*;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2GlobalValidator;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationIssueReporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * @since 6.0.0
 */
final class SnomedRf2ImportRequest implements Request<BranchContext, ImportResponse>, AccessControl {

	private static final long serialVersionUID = 1L;

	private static final String TXT_EXT = ".txt";
	
	@NotNull
	@JsonProperty
	private final Attachment rf2Archive;
	
	@NotNull
	@JsonProperty
	private Rf2ReleaseType releaseType;
	
	@JsonProperty
	private boolean createVersions = true;
	
	@NotNull
	@JsonProperty
	private Set<String> ignoreMissingReferencesIn;
	
	@JsonProperty
	private LocalDate importUntil;
	
	@JsonProperty
	private boolean dryRun = false;
	
	private transient Logger log;

	SnomedRf2ImportRequest(Attachment rf2Archive) {
		this.rf2Archive = rf2Archive;
	}
	
	void setReleaseType(Rf2ReleaseType rf2ReleaseType) {
		this.releaseType = rf2ReleaseType;
	}
	
	void setCreateVersions(boolean createVersions) {
		this.createVersions = createVersions;
	}
	
	void setIgnoreMissingReferencesIn(Set<String> ignoreMissingReferencesIn) {
		this.ignoreMissingReferencesIn = ignoreMissingReferencesIn;
	}
	
	void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}
	
	void setImportUntil(LocalDate importUntil) {
		this.importUntil = importUntil;
	}
	
	@Override
	public ImportResponse execute(BranchContext context) {
		log = LoggerFactory.getLogger("import");
		context = context.inject().bind(Logger.class, log).build();
		
		Rf2ImportConfiguration importConfig = new Rf2ImportConfiguration(releaseType, createVersions);
		validate(context, importConfig);
		final InternalAttachmentRegistry fileReg = (InternalAttachmentRegistry) context.service(AttachmentRegistry.class);
		final File rf2Archive = fileReg.getAttachment(this.rf2Archive.getAttachmentId());
		
		try (Locks locks = Locks.on(context).lock(DatastoreLockContextDescriptions.IMPORT)) {
			return doImport(context, rf2Archive, importConfig);
		} catch (Exception e) {
			if (e instanceof ApiException) {
				throw (ApiException) e;
			}
			throw SnowowlRuntimeException.wrap(e);
		}
	}

	private void validate(BranchContext context, final Rf2ImportConfiguration importConfig) {
		final boolean contentAvailable = context.service(ContentAvailabilityInfoProvider.class).isAvailable(context);
		
		if (!contentAvailable && Rf2ReleaseType.DELTA.equals(releaseType)) {
			throw new BadRequestException("Importing a Delta release of SNOMED CT "
					+ "from an archive to any branch is prohibited when SNOMED CT "
					+ "ontology is not available on the terminology server. "
					+ "Please perform either a Full or a Snapshot import instead.");
		}
		
		String codeSystemWorkingBranchPath = context.service(TerminologyResource.class).getBranchPath();
		
		if (importConfig.isCreateVersions()) {
			if (!codeSystemWorkingBranchPath.equals(context.branch().path())) {
				throw new BadRequestException("Creating a version during RF2 import from a branch is not supported. "
						+ "Please perform the import process from the corresponding CodeSystem's working branch, '%s'.", codeSystemWorkingBranchPath);
			} else {
				// importing into main development branch with create version enabled should be allowed only and only if the branch does not have any unpublished content present
				for (Class<?> type : List.of(SnomedConceptDocument.class, SnomedDescriptionIndexEntry.class, SnomedRelationshipIndexEntry.class, SnomedRefSetMemberIndexEntry.class)) {
					try {
						int numberOfUnpublishedDocuments = context.service(RevisionSearcher.class).search(Query.select(type)
							.where(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
							.limit(0)
							.build()).getTotal();
						if (numberOfUnpublishedDocuments > 0) {
							throw new BadRequestException("Creating a version during RF2 import is prohibited when unpublished content is present on the target.");
						}
					} catch (IOException e) {
						throw new RuntimeException("Failed to check unpublished content presence for type: " + type.getSimpleName(), e);
					}
				}
			}
			
		}
	}

	ImportResponse doImport(final BranchContext context, final File rf2Archive, final Rf2ImportConfiguration importconfig) throws Exception {
		final ResourceURI codeSystemUri = context.service(ResourceURI.class);
		final Rf2ValidationIssueReporter reporter = new Rf2ValidationIssueReporter();
		
		String latestVersionEffectiveTime = EffectiveTimes.format(ResourceRequests.prepareSearchVersion()
			.one()
			.filterByResource(codeSystemUri.withoutPath())
			.sortBy("effectiveTime:desc")
			.buildAsync()
			.execute(context)
			.first()
			.map(Version::getEffectiveTime)
			.orElse(LocalDate.EPOCH), DateFormats.SHORT);
		
		try (final DB db = createDb()) {

			// Read effective time slices from import files
			final Rf2EffectiveTimeSlices effectiveTimeSlices = new Rf2EffectiveTimeSlices(db, isLoadOnDemandEnabled(), latestVersionEffectiveTime, EffectiveTimes.format(importUntil, DateFormats.SHORT));
			Stopwatch w = Stopwatch.createStarted();
			read(rf2Archive, effectiveTimeSlices, reporter);
			log.info("Preparing RF2 import took: {}", w);
			w.reset().start();
			
			// Log issues with rows from the import files
			logValidationIssues(reporter);
			if (reporter.hasErrors()) {
				return ImportResponse.defects(reporter.getDefects());
			}
			
			// Run validation that takes current terminology content into account
			final List<Rf2EffectiveTimeSlice> orderedEffectiveTimeSlices = effectiveTimeSlices.consumeInOrder();
			final Rf2GlobalValidator globalValidator = new Rf2GlobalValidator(log, ignoreMissingReferencesIn);
			
			/* 
			 * TODO: Use Attachment to get the release file name and/or track file and line number sources for each row 
			 * so that they can be referenced in this stage as well
			 */
			final ImportDefectAcceptor globalDefectAcceptor = reporter.getDefectAcceptor("RF2 release");
			globalValidator.validateTerminologyComponents(orderedEffectiveTimeSlices, globalDefectAcceptor, context);
//			globalValidator.validateMembers(orderedEffectiveTimeSlices, globalDefectAcceptor, context);
			
			// Log validation issues (but just the ones found during global validation)
			logValidationIssues(globalDefectAcceptor);
			if (reporter.hasErrors()) {
				return ImportResponse.defects(reporter.getDefects());
			}
			
			// Import effective time slices in chronological order
			final ImmutableSet.Builder<ComponentURI> visitedComponents = ImmutableSet.builder(); 
			
			// if not a dryRun, perform import
			if (!dryRun) {
				// Import effective time slices in chronological order
				for (Rf2EffectiveTimeSlice slice : orderedEffectiveTimeSlices) {
					slice.doImport(context, codeSystemUri, importconfig, visitedComponents);
				}
					
			    // Update locales registered on the code system
				updateCodeSystemSettings(context, codeSystemUri);
			}
			
			return ImportResponse.success(visitedComponents.build(), reporter.getDefects());
		}
	}

	private void logValidationIssues(final Rf2ValidationIssueReporter reporter) {
		reporter.logWarnings(log);
		reporter.logErrors(log);
	}
	
	private void logValidationIssues(final ImportDefectAcceptor defectAcceptor) {
		defectAcceptor.getDefects()
			.forEach(defect -> {
				if (defect.isError()) {
					log.error(defect.getMessage());
				} else if (defect.isWarning()) {
					log.warn(defect.getMessage());
				}
			});
	}
	
	private boolean isLoadOnDemandEnabled() {
		return Rf2ReleaseType.DELTA == releaseType;
	}
	
	private void read(File rf2Archive, Rf2EffectiveTimeSlices slices, Rf2ValidationIssueReporter reporter) {
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
				if (fileName.endsWith(TXT_EXT)) {
					if (fileName.contains(releaseType.toString().toLowerCase())) {
						w.reset().start();
						try (final InputStream in = zip.getInputStream(entry)) {
							readFile(entry, in, oReader, slices, reporter);
						}
						log.info("{} - {}", entry.getName(), w);
					}
				}
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
		
		slices.flushAll();
	}

	private void readFile(ZipEntry entry, final InputStream in, final ObjectReader oReader, Rf2EffectiveTimeSlices effectiveTimeSlices, Rf2ValidationIssueReporter reporter)
			throws IOException, JsonProcessingException {
		
		final String entryName = entry.getName();
		final ImportDefectAcceptor defectAcceptor = reporter.getDefectAcceptor(entryName);
		
		boolean header = true;
		Rf2ContentType<?> resolver = null;
		int lineNumber = 1;
		
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
					log.warn("Unrecognized RF2 file: {}", entryName);
					break;
				}
				
				header = false;
			} else {
				final String effectiveTimeKey = getEffectiveTimeKey(line[1]);
				final ImportDefectBuilder defectBuilder = defectAcceptor.on(Integer.toString(lineNumber));
				resolver.register(line, effectiveTimeSlices.getOrCreate(effectiveTimeKey), defectBuilder);
			}

			lineNumber++;
		}
	}

	private String getEffectiveTimeKey(final String effectiveTime) {
		if (Strings.isNullOrEmpty(effectiveTime)) {
			// Unset effective time rows are getting their own time slice in all import modes 
			return EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL;
		} else if (Rf2ReleaseType.SNAPSHOT == releaseType) {
			// All other rows are imported in a single run in snapshot mode
			return Rf2EffectiveTimeSlice.SNAPSHOT_SLICE;
		} else {
			// Delta and full modes, however, import each "chronological layer" in order 
			return effectiveTime;
		}
	}

	private DB createDb() {
		try {
			Maker dbMaker = DBMaker 
					.fileDB(Files.createTempDirectory(rf2Archive.toString()).resolve("rf2-import.db").toFile())
					.fileDeleteAfterClose()
					.fileMmapEnable()
					.fileMmapPreclearDisable();
			
			// for non-delta releases increase the allocation size
			if (releaseType != Rf2ReleaseType.DELTA) {
				dbMaker = dbMaker
					.allocateStartSize(256 * 1024*1024)  // 256MB
				    .allocateIncrement(128 * 1024*1024);  // 128MB
			}
			
			DB db = dbMaker.make();
			
			// preload file content into disk cache
			db.getStore().fileLoad();
			return db;
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Couldn't create temporary db", e);
		}
	}
	
	private void updateCodeSystemSettings(final BranchContext context, final ResourceURI codeSystemUri) throws Exception {
		SnomedReferenceSets languageReferenceSets = SnomedRequests.prepareSearchRefSet()
			.all()
			.filterByType(SnomedRefSetType.LANGUAGE)
			.filterByActive(true)
			.setFields(SnomedConceptDocument.Fields.ID)
			.sortBy(Sort.fieldAsc(SnomedConceptDocument.Fields.ID))
			.build()
			.execute(context);
		
		/*
		 * XXX: The default language in locales is always "en", as there is no
		 * machine-readable information about what language code each language type
		 * reference set is associated with.
		 */
		final List<ExtendedLocale> locales = languageReferenceSets
			.stream()
			.map(refSet -> new ExtendedLocale("en", "", refSet.getId()))
			.collect(Collectors.toList());
		
		// fetch codesystem again to get the latest settings 
		CodeSystem currentSnomedCodeSystem = CodeSystemRequests.prepareGetCodeSystem(codeSystemUri.getResourceId())
			.buildAsync()
			.get(context);
		
		Map<String, SnomedLanguageConfig> mergedLanguagesConfiguration = Maps.newLinkedHashMap(); 
		SnomedDescriptionUtils.getLanguagesConfiguration(context.service(ObjectMapper.class), currentSnomedCodeSystem).forEach(config -> {
			mergedLanguagesConfiguration.put(config.getLanguageTag(), config);
		});
		
		languageReferenceSets.stream()
				.map(SnomedReferenceSet::getId)
				.filter(SnomedTerminologyComponentConstants.LANG_REFSET_DIALECT_ALIASES::containsKey)
				.forEach(langRefsetId -> {
					final String dialect = SnomedTerminologyComponentConstants.LANG_REFSET_DIALECT_ALIASES.get(langRefsetId);
					// ignore any aliases that are already defined by using computeIfAbsent
					mergedLanguagesConfiguration.computeIfAbsent(dialect, languageTag -> new SnomedLanguageConfig(languageTag, langRefsetId));
					
				});
		
		CodeSystemRequests.prepareUpdateCodeSystem(codeSystemUri.getResourceId())
				.setSettings(Map.of(
					CodeSystem.CommonSettings.LOCALES, locales,
					SnomedTerminologyComponentConstants.CODESYSTEM_LANGUAGE_CONFIG_KEY, mergedLanguagesConfiguration.values()
				))
				.build(context.service(User.class).getUsername(), String.format("Update '%s' settings based on RF2 import", codeSystemUri.getResourceId()))
				.execute(context.service(IEventBus.class))
				.getSync(2, TimeUnit.MINUTES);
	}

	@Override
	public String getOperation() {
		return Permission.OPERATION_IMPORT;
	}

}
