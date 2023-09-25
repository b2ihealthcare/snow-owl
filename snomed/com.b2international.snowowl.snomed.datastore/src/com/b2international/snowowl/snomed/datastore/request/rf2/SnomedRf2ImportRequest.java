/*
 * Copyright 2017-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayList;

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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DBMaker.Maker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.ApiException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.attachments.InternalAttachmentRegistry;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.codesystem.CodeSystem;
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
	
	public static final AtomicBoolean disableVersionsOnChildBranches = new AtomicBoolean(true);
	
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
	
	@Min(1000)
	@Max(60000)
	private int batchSize;
	
	@JsonProperty
	private boolean dryRun = false;
	
	@JsonProperty
	private String author;

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
	
	void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	
	void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public ImportResponse execute(BranchContext context) {
		log = LoggerFactory.getLogger("import");
		context = context.inject().bind(Logger.class, log).build();
		if (StringUtils.isEmpty(author)) {
			author = context.service(User.class).getUserId();
		}
		
		Rf2ImportConfiguration importConfig = new Rf2ImportConfiguration(releaseType, createVersions, author);
		validate(context, importConfig);
		final InternalAttachmentRegistry fileReg = (InternalAttachmentRegistry) context.service(AttachmentRegistry.class);
		final File rf2Archive = fileReg.getAttachment(this.rf2Archive.getAttachmentId());
		
		try (Locks<BranchContext> locks = Locks.forContext(DatastoreLockContextDescriptions.IMPORT).lock(context)) {
			return doImport(locks.ctx(), rf2Archive, importConfig);
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
		
		
		if (disableVersionsOnChildBranches.get()) {
			String codeSystemWorkingBranchPath = context.service(TerminologyResource.class).getBranchPath();
			
			if (!codeSystemWorkingBranchPath.equals(context.path()) && importConfig.isCreateVersions()) {
				if (!codeSystemWorkingBranchPath.equals(context.path())) {
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
			String importUntilEffectiveTime = importUntil == null ? null : EffectiveTimes.format(importUntil, DateFormats.SHORT);
			final Rf2EffectiveTimeSlices effectiveTimeSlices = new Rf2EffectiveTimeSlices(db, isLoadOnDemandEnabled(), latestVersionEffectiveTime, importUntilEffectiveTime, batchSize);
			Stopwatch w = Stopwatch.createStarted();
			read(rf2Archive, effectiveTimeSlices, reporter);
			log.info("Preparing RF2 import took: {}", w);
			w.reset().start();
			
			// Log issues with rows from the import files, at most 1000 warnings and 1000 errors to log into the log file, everything else should be in the response object
			logValidationIssues(reporter, 1000);
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

	private void logValidationIssues(final Rf2ValidationIssueReporter reporter, int numberOfIssuesToLog) {
		reporter.logWarnings(log, numberOfIssuesToLog);
		reporter.logErrors(log, numberOfIssuesToLog);
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
		csvMapper.enable(CsvParser.Feature.SKIP_EMPTY_LINES);
		
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
		if (Rf2ReleaseType.SNAPSHOT == releaseType) {
			// in case of Snapshot import treat all rows as a single slice
			return Rf2EffectiveTimeSlice.SNAPSHOT_SLICE;
		} if (Strings.isNullOrEmpty(effectiveTime)) {
			// Otherwise if there is not effective time in the current row use the unset effective time label slice 
			return EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL;
		} else {
			// if there is a valid effectiveTime, then use it to import each "chronological layer" in order 
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
	
	@SuppressWarnings("deprecation")
	private void updateCodeSystemSettings(final BranchContext context, final ResourceURI resourceUri) throws Exception {
		
		// fetch codesystem to get the latest settings 
		Resource resource = ResourceRequests.prepareGet(resourceUri)
			.buildAsync()
			.get(context);
		
		if (!(resource instanceof TerminologyResource)) {
			return;
		}
		
		TerminologyResource currentResource = (TerminologyResource) resource;
		
		/////////////
		// Locales //
		/////////////
		
		/*
		 * Given that we have no better approach to calculate proper locales, we can collect the active language reference sets
		 * on the current branch
		 */
		SnomedReferenceSets languageReferenceSets = SnomedRequests.prepareSearchRefSet()
			.all()
			.filterByType(SnomedRefSetType.LANGUAGE)
			.filterByActive(true)
			.setFields(SnomedConceptDocument.Fields.ID)
			.sortBy(Sort.fieldAsc(SnomedConceptDocument.Fields.ID))
			.build()
			.execute(context);
		
		/*
		 * XXX: The default language in locales is always "en", as there is no machine-readable information about what language
		 * code each language type reference set is associated with.
		 */
		final List<ExtendedLocale> computedLocales = languageReferenceSets
			.stream()
			.map(refSet -> new ExtendedLocale("en", "", refSet.getId()))
			.collect(Collectors.toList());
		
		/*
		 * Append the current locale settings if any of the computed locales are missing 
		 */
		
		List<ExtendedLocale> currentLocales = newArrayList();
		
		if (!CompareUtils.isEmpty(currentResource.getLocales())) {
			currentResource.getLocales().stream()
				.map(locale -> ExtendedLocale.valueOf(locale))
				.forEach(currentLocales::add);
		}
		
		computedLocales.forEach( computedLocale -> {
			if (!currentLocales.contains(computedLocale)) {
				currentLocales.add(computedLocale);
			}
		});
		
		////////////////////////////
		// Language configuration //
		////////////////////////////

		Map<String, SnomedLanguageConfig> mergedLanguagesConfiguration = Maps.newLinkedHashMap(); 
		SnomedDescriptionUtils.getLanguagesConfiguration(context.service(ObjectMapper.class), currentResource).forEach(config -> {
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
		
		ResourceRequests.prepareUpdate(resourceUri.getResourceId())
				.setSettings(Map.of(
					CodeSystem.CommonSettings.LOCALES, currentLocales,
					SnomedTerminologyComponentConstants.CODESYSTEM_LANGUAGE_CONFIG_KEY, mergedLanguagesConfiguration.values()
				))
				.build(author, String.format("Update '%s' settings based on RF2 import", resourceUri.getResourceId()))
				.execute(context.service(IEventBus.class))
				.getSync(2, TimeUnit.MINUTES);
	}

	@Override
	public String getOperation() {
		return Permission.OPERATION_IMPORT;
	}

}
