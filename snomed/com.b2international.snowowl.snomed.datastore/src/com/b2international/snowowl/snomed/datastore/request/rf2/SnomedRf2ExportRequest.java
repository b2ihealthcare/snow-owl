/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.FileUtils;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.authorization.BranchAccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.repository.RepositoryCodeSystemProvider;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.BranchRequest;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.core.request.RevisionIndexReadRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.exporter.Rf2ConceptExporter;
import com.b2international.snowowl.snomed.datastore.request.rf2.exporter.Rf2DescriptionExporter;
import com.b2international.snowowl.snomed.datastore.request.rf2.exporter.Rf2LanguageRefSetExporter;
import com.b2international.snowowl.snomed.datastore.request.rf2.exporter.Rf2RefSetDescriptorRefSetExporter;
import com.b2international.snowowl.snomed.datastore.request.rf2.exporter.Rf2RefSetExporter;
import com.b2international.snowowl.snomed.datastore.request.rf2.exporter.Rf2RelationshipExporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

/**
 * @since 5.7
 */
final class SnomedRf2ExportRequest extends ResourceRequest<BranchContext, Attachment> implements BranchAccessControl {

	private static final String DESCRIPTION_TYPES_EXCEPT_TEXT_DEFINITION = "<<" + Concepts.DESCRIPTION_TYPE_ROOT_CONCEPT + " MINUS " + Concepts.TEXT_DEFINITION;
	private static final String NON_STATED_CHARACTERISTIC_TYPES = "<<" + Concepts.CHARACTERISTIC_TYPE + " MINUS " + Concepts.STATED_RELATIONSHIP;

	private static final long serialVersionUID = 1L;

	private static final Ordering<CodeSystemVersionEntry> EFFECTIVE_DATE_ORDERING = Ordering.natural()
			.onResultOf(CodeSystemVersionEntry::getEffectiveDate);

	@JsonProperty 
	@NotNull 
	private Rf2ReleaseType releaseType;

	@JsonProperty
	@NotNull
	private Rf2RefSetExportLayout refSetExportLayout;

	@JsonProperty
	@NotEmpty
	private String countryNamespaceElement;

	@JsonProperty
	private String namespaceFilter;

	@JsonProperty 
	private Long startEffectiveTime;

	@JsonProperty 
	private Long endEffectiveTime;

	@JsonProperty
	private boolean includePreReleaseContent;

	@JsonProperty
	private Collection<String> componentTypes;

	@JsonProperty 
	private Collection<String> modules;

	@JsonProperty
	private Collection<String> refSets;

	@JsonProperty
	private String transientEffectiveTime;

	@JsonProperty 
	private boolean extensionOnly;
	
	SnomedRf2ExportRequest() {}

	void setReleaseType(final Rf2ReleaseType releaseType) {
		this.releaseType = releaseType;
	}

	void setRefSetExportLayout(final Rf2RefSetExportLayout refSetExportLayout) {
		this.refSetExportLayout = refSetExportLayout;		
	}

	void setCountryNamespaceElement(final String countryNamespaceElement) {
		this.countryNamespaceElement = countryNamespaceElement;
	}

	void setNamespaceFilter(final String namespaceFilter) {
		this.namespaceFilter = namespaceFilter;
	}

	void setStartEffectiveTime(final Long startEffectiveTime) {
		this.startEffectiveTime = startEffectiveTime;
	}

	void setEndEffectiveTime(final Long endEffectiveTime) {
		this.endEffectiveTime = endEffectiveTime;
	}

	void setIncludePreReleaseContent(final boolean includeUnpublished) {
		this.includePreReleaseContent = includeUnpublished;
	}

	void setComponentTypes(final Collection<String> componentTypes) {
		/*
		 * All component types should be exported if the input value is null; no
		 * component type should be exported if the input value is an empty collection.
		 */
		this.componentTypes = (componentTypes != null) 
				? ImmutableSet.copyOf(componentTypes) 
				: ImmutableSet.of(SnomedTerminologyComponentConstants.CONCEPT, 
						SnomedTerminologyComponentConstants.DESCRIPTION, 
						SnomedTerminologyComponentConstants.RELATIONSHIP, 
						SnomedTerminologyComponentConstants.REFSET_MEMBER);
	}

	void setModules(final Collection<String> modules) {
		/*
		 * All modules should be exported if the input value is null; no module
		 * should be exported if the input value is an empty collection.
		 */
		this.modules = (modules != null) ? ImmutableSet.copyOf(modules) : null;
	}

	void setRefSets(final Collection<String> refSets) {
		/*
		 * All reference sets should be exported if the input value is null; no component
		 * should be exported if the input value is an empty collection.
		 */
		this.refSets = (refSets != null) ? ImmutableSet.copyOf(refSets) : null;
	}

	void setTransientEffectiveTime(final String transientEffectiveTime) {
		if (Strings.isNullOrEmpty(transientEffectiveTime)) {
			// Effective time columns should be left blank
			this.transientEffectiveTime = "";
		} else if ("NOW".equals(transientEffectiveTime)) {
			// Special flag indicating "today"
			this.transientEffectiveTime = EffectiveTimes.format(Dates.todayGmt(), DateFormats.SHORT);
		} else {
			// Otherwise, it should be a valid short date
			Dates.parse(transientEffectiveTime, DateFormats.SHORT);
			this.transientEffectiveTime = transientEffectiveTime;
		}
	}

	void setExtensionOnly(final boolean extensionOnly) {
		this.extensionOnly = extensionOnly;
	}
	
	@Override
	public Attachment execute(final BranchContext context) {
		final String referenceBranch = context.path();
		
		// register export start time for later use
		final long exportStartTime = Instant.now().toEpochMilli();

		// Step 1: check if the export reference branch is a working branch path descendant
		final CodeSystem referenceCodeSystem = context.service(RepositoryCodeSystemProvider.class).get(referenceBranch);

		// Step 2: retrieve code system versions that are visible from the reference branch
		final TreeSet<CodeSystemVersionEntry> versionsToExport = getAllExportableCodeSystemVersions(context, referenceCodeSystem);
		
		// Step 3: compute branches to export
		final List<String> branchesToExport = computeBranchesToExport(referenceBranch, versionsToExport);
			
		// Step 4: compute possible language codes
		Multimap<String, String> availableLanguageCodes = getLanguageCodes(context, branchesToExport);
		
		Path exportDirectory = null;

		try {
			
			final UUID exportId = UUID.randomUUID();
			
			// create temporary export directory
			exportDirectory = createExportDirectory(exportId);

			// get archive effective time based on latest version effective / transient effective time / current date
			final Date archiveEffectiveDate = getArchiveEffectiveTime(context, versionsToExport);
			final String archiveEffectiveDateShort = Dates.format(archiveEffectiveDate, TimeZone.getTimeZone("UTC"), DateFormats.SHORT);
			
			// create main folder including release status and archive effective date
			final Path releaseDirectory = createReleaseDirectory(exportDirectory, archiveEffectiveDate);

			final Set<String> visitedComponentEffectiveTimes = newHashSet();
			
			final long effectiveTimeStart = startEffectiveTime != null ? startEffectiveTime : 0;
			final long effectiveTimeEnd =  endEffectiveTime != null ? endEffectiveTime : Long.MAX_VALUE;

			// export content from the pre-computed version branches
			for (String branch : branchesToExport) {
				
				exportBranch(releaseDirectory, 
						context,
						branch, 
						archiveEffectiveDateShort, 
						effectiveTimeStart,
						effectiveTimeEnd,
						visitedComponentEffectiveTimes,
						availableLanguageCodes.get(branch));
				
			}
			
			// export content from reference branch
			if (includePreReleaseContent) {
				final String referenceBranchToExport = String.format("%s%s%s", referenceBranch, RevisionIndex.AT_CHAR, exportStartTime);
				exportBranch(releaseDirectory, 
						context, 
						referenceBranchToExport, 
						archiveEffectiveDateShort, 
						EffectiveTimes.UNSET_EFFECTIVE_TIME,
						EffectiveTimes.UNSET_EFFECTIVE_TIME,
						visitedComponentEffectiveTimes,
						availableLanguageCodes.get(referenceBranch));
			}

			// Step 6: compress to archive and upload to the file registry
			final AttachmentRegistry fileRegistry = context.service(AttachmentRegistry.class);
			registerResult(fileRegistry, exportId, exportDirectory);
			final String fileName = releaseDirectory.getFileName() + ".zip";
			return new Attachment(exportId, fileName);
			
		} catch (final Exception e) {
			throw new SnowowlRuntimeException("Failed to export terminology content to RF2.", e);
		} finally {
			if (exportDirectory != null) {
				FileUtils.deleteDirectory(exportDirectory.toFile());
			}
		}
	}

	private Multimap<String, String> getLanguageCodes(BranchContext context, List<String> branchesToExport) {
		final String referenceBranch = context.path();
		
		List<String> branchesOrRanges = newArrayList(branchesToExport);
		
		if (includePreReleaseContent) {
			branchesOrRanges.add(referenceBranch);
		}
		
		Multimap<String, String> branchToLanguageCodes = HashMultimap.create();
		
		Set<String> filteredLanguageCodes = Stream.of(Locale.getISOLanguages())
				.filter(code -> !Locale.ENGLISH.getLanguage().equals(code))
				.collect(toSet());
		
		for (String branchOrRange : branchesOrRanges) {
			
			String branch = getBranchOrRangeTarget(branchOrRange);
			
			final Set<String> languageCodes = newHashSet();
			
			// check if there are any english terms on the given branch / range
			final Request<BranchContext, SnomedDescriptions> englishLanguageCodeRequest = SnomedRequests.prepareSearchDescription()
					.setLimit(0)
					.filterByLanguageCodes(singleton(Locale.ENGLISH.getLanguage()))
					.build();
			
			final SnomedDescriptions enDescriptions = execute(context, branch, englishLanguageCodeRequest);
			
			if (enDescriptions.getTotal() > 0) {
				languageCodes.add(Locale.ENGLISH.getLanguage());
			}

			// check if there are any terms other than english on the given branch / range
			final Request<BranchContext, SnomedDescriptions> languageCodeRequest = SnomedRequests.prepareSearchDescription()
					.all()
					.filterByLanguageCodes(filteredLanguageCodes)
					.setFields(SnomedRf2Headers.FIELD_ID, SnomedRf2Headers.FIELD_LANGUAGE_CODE)
					.build();
			
			final SnomedDescriptions descriptions = execute(context, branch, languageCodeRequest);
			
			if (!descriptions.isEmpty()) {
				languageCodes.addAll(descriptions.stream().map(SnomedDescription::getLanguageCode).collect(toSet()));
			}
			
			branchToLanguageCodes.putAll(branchOrRange, languageCodes);
		}
		
		return branchToLanguageCodes;
	}

	private List<String> computeBranchesToExport(final String referenceBranch, final TreeSet<CodeSystemVersionEntry> versionsToExport) {
		
		final List<String> branchesToExport = newArrayList();
		
		switch (releaseType) {
			case FULL:
				versionsToExport.stream()
					.map(v -> v.getPath())
					.filter(v -> !branchesToExport.contains(v))
					.forEachOrdered(branchesToExport::add);
				if (!branchesToExport.contains(referenceBranch)) {
					branchesToExport.add(referenceBranch);
				}
				break;
			case DELTA:
				if (startEffectiveTime != null || endEffectiveTime != null || !includePreReleaseContent) {
					versionsToExport.stream()
						.map(v -> v.getPath())
						.filter(v -> !branchesToExport.contains(v))
						.forEachOrdered(branchesToExport::add);
					if (!branchesToExport.contains(referenceBranch)) {
						branchesToExport.add(referenceBranch);
					}
				}
				break;
			case SNAPSHOT:
				branchesToExport.add(referenceBranch);
				break;
		}
		
		Builder<String> branchRangesToExport = ImmutableList.builder();
		
		for (int i = 0; i < branchesToExport.size(); i++) {
			
			final String previousVersion = i == 0 ? null : branchesToExport.get(i - 1);
			final String currentVersion = branchesToExport.get(i);

			branchRangesToExport.add(previousVersion == null ? currentVersion : RevisionIndex.toRevisionRange(previousVersion, currentVersion));
			
		}
		
		return branchRangesToExport.build();
	}

	private Date getArchiveEffectiveTime(final RepositoryContext context, final TreeSet<CodeSystemVersionEntry> versionsToExport) {

		Optional<CodeSystemVersionEntry> lastVersionToExport;
		
		if (endEffectiveTime != null) {
			lastVersionToExport = Optional.ofNullable(getVersionBefore(versionsToExport, endEffectiveTime));
		} else {
			lastVersionToExport = !versionsToExport.isEmpty() ? Optional.ofNullable(versionsToExport.last()) : Optional.empty();
		}
		
		Optional<Date> latestModuleEffectiveTime = lastVersionToExport.flatMap(version -> getLatestModuleEffectiveTime(context, version));
		
		if (includePreReleaseContent) {
			
			if (!transientEffectiveTime.isEmpty()) {
				return adjustCurrentHour(Dates.parse(transientEffectiveTime, DateFormats.SHORT));
			} else if (latestModuleEffectiveTime.isPresent()) {
				return adjustCurrentHour(getNextEffectiveDate(latestModuleEffectiveTime.get().getTime()));
			} else if (lastVersionToExport.isPresent()) {
				return adjustCurrentHour(getNextEffectiveDate(lastVersionToExport.get().getEffectiveDate()));
			}
			
		} else {
			
			if (latestModuleEffectiveTime.isPresent()) {
				return adjustCurrentHour(new Date(latestModuleEffectiveTime.get().getTime()));
			} else if (lastVersionToExport.isPresent()) {
				return adjustCurrentHour(new Date(lastVersionToExport.get().getEffectiveDate()));
			}
			
		}
		
		return adjustCurrentHour(Dates.parse(Dates.format(new Date(), TimeZone.getTimeZone("UTC"), DateFormats.DEFAULT)));
	}

	private CodeSystemVersionEntry getVersionBefore(final TreeSet<CodeSystemVersionEntry> versionsToExport, final long timestamp) {
		CodeSystemVersionEntry versionBeforeEndEffectiveTime = null;
		for (CodeSystemVersionEntry version : versionsToExport) {
			if (version.getEffectiveDate() > timestamp) {
				break;
			}
			versionBeforeEndEffectiveTime = version;
		}
		return versionBeforeEndEffectiveTime;
	}
	
	private Optional<Date> getLatestModuleEffectiveTime(final RepositoryContext context, final CodeSystemVersionEntry version) {
		
		final Optional<Date> sourceEffectiveTime = getLatestModuleEffectiveTime(context, version, SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME);
		final Optional<Date> targetEffectiveTime = getLatestModuleEffectiveTime(context, version, SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME);
		
		if (!sourceEffectiveTime.isPresent() && !targetEffectiveTime.isPresent()) {
			return Optional.empty();
		} else if(!sourceEffectiveTime.isPresent()) {
			return targetEffectiveTime;
		} else if (!targetEffectiveTime.isPresent()) {
			return sourceEffectiveTime;
		} else {
			final Date sourceDate = sourceEffectiveTime.get();
			final Date targetDate = targetEffectiveTime.get();
			if (sourceDate.after(targetDate)) {
				return sourceEffectiveTime;
			}
			
			if (targetDate.after(sourceDate)) {
				return targetEffectiveTime;
			}
			
			// they are the same date
			return sourceEffectiveTime;
		}
	}
	
	private Optional<Date> getLatestModuleEffectiveTime(final RepositoryContext context, final CodeSystemVersionEntry version, String field) {
		SnomedRefSetMemberSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchMember()
				.filterByRefSet(Concepts.REFSET_MODULE_DEPENDENCY_TYPE)
				.filterByActive(true)
				.sortBy(SortField.descending(field))
				.setLimit(1);
			
			// See the comment in setModules; a value of "null" means that all modules should be exported 
			if (modules != null) {
				requestBuilder.filterByModules(modules);
			}
			
			final Optional<SnomedReferenceSetMember> moduleDependencyMember = requestBuilder 
				.build(context.id(), version.getPath())
				.execute(context.service(IEventBus.class))
				.getSync(1, TimeUnit.MINUTES)
				.first();
			
			return moduleDependencyMember.map(m -> {
				return (Date) m.getProperties().get(field);
			});
	}

	private Date adjustCurrentHour(final Date effectiveDate) {
		
		final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		
		calendar.setTimeInMillis(effectiveDate.getTime());
		calendar.set(Calendar.HOUR_OF_DAY, currentHour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}
	
	private Date getNextEffectiveDate(final long time) {
		final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTimeInMillis(time);
		calendar.roll(Calendar.DATE, true);
		return calendar.getTime();
	}

	private TreeSet<CodeSystemVersionEntry> getAllExportableCodeSystemVersions(final BranchContext context, final CodeSystem codeSystemEntry) {
		final String referenceBranch = context.path();
		final TreeSet<CodeSystemVersionEntry> visibleVersions = newTreeSet(EFFECTIVE_DATE_ORDERING);
		collectExportableCodeSystemVersions(context, visibleVersions, codeSystemEntry, referenceBranch);
		return visibleVersions;
	}

	private void collectExportableCodeSystemVersions(final RepositoryContext context, final Set<CodeSystemVersionEntry> versionsToExport, final CodeSystem codeSystemEntry,
			final String referenceBranch) {
		
		final Collection<CodeSystemVersionEntry> candidateVersions = newArrayList(getCodeSystemVersions(context, codeSystemEntry.getShortName()));
		
		if (candidateVersions.isEmpty()) {
			return;
		}

		final String versionParentPath = candidateVersions.stream()
				.map(CodeSystemVersionEntry::getParentBranchPath)
				.findFirst()
				.get();

		final Set<String> versionNames = candidateVersions.stream()
				.map(CodeSystemVersionEntry::getVersionId)
				.collect(Collectors.toSet());

		final Branches versionBranches = getBranches(context, versionParentPath, versionNames);
		final Map<String, Branch> versionBranchesByName = Maps.uniqueIndex(versionBranches, Branch::name);

		final Branch cutoffBranch = getBranch(context, referenceBranch);
		final long cutoffBaseTimestamp = getCutoffBaseTimestamp(context, cutoffBranch, versionParentPath);

		// Remove all code system versions which were created after the cut-off date, or don't have a corresponding branch 
		candidateVersions.removeIf(v -> false
				|| !versionBranchesByName.containsKey(v.getVersionId())
				|| versionBranchesByName.get(v.getVersionId()).baseTimestamp() > cutoffBaseTimestamp);

		versionsToExport.addAll(candidateVersions);

		// Exit early if only an extension code system should be exported, or we are already at the "base" code system
		final CodeSystemURI extensionOfUri = codeSystemEntry.getExtensionOf();
		if (extensionOnly || extensionOfUri == null) {
			return;
		}

		// Otherwise, collect applicable versions using this code system's working path
		final String extensionOfShortName = extensionOfUri.getCodeSystem();
		final CodeSystem extensionOfCodeSystem = CodeSystemRequests.getCodeSystem(context, extensionOfShortName);
		collectExportableCodeSystemVersions(context, versionsToExport, extensionOfCodeSystem, codeSystemEntry.getBranchPath());
	}

	private Path createExportDirectory(final UUID exportId) {
		try {
			return Files.createTempDirectory("export-" + exportId + "-");
		} catch (final IOException e) {
			throw new SnowowlRuntimeException("Failed to create working directory for export.", e);
		}
	}

	private Path createReleaseDirectory(final Path exportDirectory, final Date archiveEffectiveTime) {
		
		final String releaseStatus = includePreReleaseContent ? "BETA" : "PRODUCTION";
		
		String effectiveDate = Dates.format(archiveEffectiveTime, TimeZone.getTimeZone("UTC"), DateFormats.ISO_8601_UTC);
		
		final Path releaseDirectory = exportDirectory.resolve(String.format("SNOMEDCT_RF2_%s_%s", releaseStatus, effectiveDate));

		try {
			Files.createDirectories(releaseDirectory);
		} catch (final IOException e) {
			throw new SnowowlRuntimeException("Failed to create RF2 release directory for export.", e);
		}

		return releaseDirectory;
	}

	private void exportBranch(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch, 
			final String archiveEffectiveTime, 
			final long effectiveTimeFilterStart, 
			final long effectiveTimeFilterEnd,
			final Set<String> visitedComponentEffectiveTimes,
			final Collection<String> languageCodes) throws IOException {

		for (final String componentToExport : componentTypes) {
			switch (componentToExport) {
				case SnomedTerminologyComponentConstants.CONCEPT:
					exportConcepts(releaseDirectory, 
							context,
							branch,
							archiveEffectiveTime,
							effectiveTimeFilterStart,
							effectiveTimeFilterEnd,
							visitedComponentEffectiveTimes);
					break;
	
				case SnomedTerminologyComponentConstants.DESCRIPTION:
					for (final String languageCode : languageCodes) {
						exportDescriptions(releaseDirectory, 
								context,
								branch,
								archiveEffectiveTime,
								effectiveTimeFilterStart,
								effectiveTimeFilterEnd,
								languageCode,
								visitedComponentEffectiveTimes);
					}
					break;
	
				case SnomedTerminologyComponentConstants.RELATIONSHIP:
					exportRelationships(releaseDirectory, 
							context,
							branch,
							archiveEffectiveTime,
							effectiveTimeFilterStart,
							effectiveTimeFilterEnd,
							visitedComponentEffectiveTimes);
					break;
	
				case SnomedTerminologyComponentConstants.REFSET_MEMBER:
					if (Rf2RefSetExportLayout.COMBINED.equals(refSetExportLayout)) {
						exportCombinedRefSets(releaseDirectory,
								context,
								branch,
								archiveEffectiveTime,
								effectiveTimeFilterStart,
								effectiveTimeFilterEnd,
								languageCodes,
								visitedComponentEffectiveTimes);
					} else {
						exportIndividualRefSets(releaseDirectory,
								context,
								branch,
								archiveEffectiveTime,
								effectiveTimeFilterStart,
								effectiveTimeFilterEnd,
								languageCodes,
								visitedComponentEffectiveTimes);
					}
				break;

			default:
				throw new IllegalStateException("Component type '" + componentToExport + "' can not be exported.");
			}
		}
		
		if (Boolean.valueOf(System.getProperty("so.snomed.refsetdescriptor_preview", "false"))) {
			exportRefSetDescriptor(releaseDirectory,
					context,
					branch,
					archiveEffectiveTime,
					effectiveTimeFilterStart,
					effectiveTimeFilterEnd,
					languageCodes,
					visitedComponentEffectiveTimes);
		}
	}

	private void exportRefSetDescriptor(Path releaseDirectory, 
			RepositoryContext context, 
			String branch, 
			String archiveEffectiveTime, 
			long effectiveTimeFilterStart, 
			long effectiveTimeFilterEnd, 
			Collection<String> languageCodes, 
			Set<String> visitedComponentEffectiveTimes) throws IOException {
		final Rf2RefSetDescriptorRefSetExporter exporter = new Rf2RefSetDescriptorRefSetExporter(releaseType, 
				countryNamespaceElement, 
				namespaceFilter,
				transientEffectiveTime,
				archiveEffectiveTime,
				modules);
		
		exporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilterStart, effectiveTimeFilterEnd, visitedComponentEffectiveTimes);
	}

	private void exportConcepts(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilterStart,
			final long effectiveTimeFilterEnd,
			final Set<String> visitedComponentEffectiveTimes) throws IOException {

		final Rf2ConceptExporter conceptExporter = new Rf2ConceptExporter(releaseType, 
				countryNamespaceElement, 
				namespaceFilter,
				transientEffectiveTime,
				archiveEffectiveTime,
				includePreReleaseContent,
				modules);

		conceptExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilterStart, effectiveTimeFilterEnd, visitedComponentEffectiveTimes);
	}

	private void exportDescriptions(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilterStart,
			final long effectiveTimeFilterEnd, 
			final String languageCode,
			final Set<String> visitedComponentEffectiveTimes) throws IOException {

		final Set<String> descriptionTypes = execute(context, getBranchOrRangeTarget(branch), SnomedRequests.prepareSearchConcept()
			.all()
			.filterByEcl(DESCRIPTION_TYPES_EXCEPT_TEXT_DEFINITION)
			.setFields(SnomedDescriptionIndexEntry.Fields.ID)
			.build())
			.stream()
			.map(IComponent::getId)
			.collect(Collectors.toSet());
				
		final Rf2DescriptionExporter descriptionExporter = new Rf2DescriptionExporter(releaseType, 
				countryNamespaceElement,
				namespaceFilter,
				transientEffectiveTime,
				archiveEffectiveTime, 
				modules, 
				descriptionTypes,
				languageCode);

		final Rf2DescriptionExporter textDefinitionExporter = new Rf2DescriptionExporter(releaseType, 
				countryNamespaceElement,
				namespaceFilter, 
				transientEffectiveTime,
				archiveEffectiveTime, 
				modules, 
				ImmutableSet.of(Concepts.TEXT_DEFINITION),
				languageCode);

		descriptionExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilterStart, effectiveTimeFilterEnd, visitedComponentEffectiveTimes);
		textDefinitionExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilterStart, effectiveTimeFilterEnd, visitedComponentEffectiveTimes);
	}

	private String getBranchOrRangeTarget(final String branch) {
		return RevisionIndex.isRevRangePath(branch) ? RevisionIndex.getRevisionRangePaths(branch)[1] : branch;
	}

	private <R> R execute(RepositoryContext context, String branch, Request<BranchContext, R> next) {
		return new BranchRequest<>(branch, new RevisionIndexReadRequest<>(next)).execute(context);
	}
	
	private void exportRelationships(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilterStart,
			final long effectiveTimeFilterEnd,
			final Set<String> visitedComponentEffectiveTimes) throws IOException {

		final Set<String> characteristicTypes = execute(context, getBranchOrRangeTarget(branch), SnomedRequests.prepareSearchConcept()
				.all()
				.filterByEcl(NON_STATED_CHARACTERISTIC_TYPES)
				.setFields(SnomedRelationshipIndexEntry.Fields.ID)
				.build())
				.stream()
				.map(IComponent::getId)
				.collect(Collectors.toSet());
		
		final Rf2RelationshipExporter statedRelationshipExporter = new Rf2RelationshipExporter(releaseType, 
				countryNamespaceElement, 
				namespaceFilter, 
				transientEffectiveTime,
				archiveEffectiveTime, 
				modules, 
				ImmutableSet.of(Concepts.STATED_RELATIONSHIP));

		final Rf2RelationshipExporter relationshipExporter = new Rf2RelationshipExporter(releaseType, 
				countryNamespaceElement, 
				namespaceFilter, 
				transientEffectiveTime,
				archiveEffectiveTime, 
				modules, 
				characteristicTypes);

		statedRelationshipExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilterStart, effectiveTimeFilterEnd, visitedComponentEffectiveTimes);
		relationshipExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilterStart, effectiveTimeFilterEnd, visitedComponentEffectiveTimes);
	}

	private void exportCombinedRefSets(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilterStart,
			final long effectiveTimeFilterEnd,  
			final Collection<String> languageCodes,
			final Set<String> visitedComponentEffectiveTimes) throws IOException {

		final Multimap<SnomedRefSetType, SnomedConcept> referenceSetsByType = FluentIterable.from(getIdentifierConcepts(context, getBranchOrRangeTarget(branch)))
				.index(c -> c.getReferenceSet().getType());

		// Create single exporter instance for each reference set type
		for (final SnomedRefSetType refSetType : referenceSetsByType.keySet()) {

			// We will handle language reference sets separately
			if (SnomedRefSetType.LANGUAGE.equals(refSetType)) {
				continue;
			}

			final Rf2RefSetExporter refSetExporter = new Rf2RefSetExporter(releaseType, 
					countryNamespaceElement, 
					namespaceFilter, 
					transientEffectiveTime,
					archiveEffectiveTime,
					modules,
					refSetExportLayout,
					refSetType,
					referenceSetsByType.get(refSetType));

			refSetExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilterStart, effectiveTimeFilterEnd, visitedComponentEffectiveTimes);
		}

		exportLanguageRefSets(releaseDirectory, 
				context, 
				branch, 
				archiveEffectiveTime, 
				effectiveTimeFilterStart, 
				effectiveTimeFilterEnd, 
				languageCodes, 
				referenceSetsByType.get(SnomedRefSetType.LANGUAGE),
				visitedComponentEffectiveTimes);
	}

	private void exportIndividualRefSets(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String revisionRange,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilterStart,
			final long effectiveTimeFilterEnd, 
			final Collection<String> languageCodes,
			final Set<String> visitedComponentEffectiveTimes) throws IOException {

		final Multimap<SnomedRefSetType, SnomedConcept> referenceSetsByType = FluentIterable.from(getIdentifierConcepts(context, getBranchOrRangeTarget(revisionRange)))
				.index(c -> c.getReferenceSet().getType());

		/* 
		 * Create single exporter instance for each reference set type - reference set concept 
		 * pair (so effectively one for each reference set)
		 */
		for (final Entry<SnomedRefSetType, SnomedConcept> entry : referenceSetsByType.entries()) {

			// We will handle language reference sets separately
			if (SnomedRefSetType.LANGUAGE.equals(entry.getKey())) {
				continue;
			}

			final Rf2RefSetExporter refSetExporter = new Rf2RefSetExporter(releaseType, 
					countryNamespaceElement, 
					namespaceFilter, 
					transientEffectiveTime,
					archiveEffectiveTime,
					modules,
					refSetExportLayout,
					entry.getKey(),
					ImmutableSet.of(entry.getValue()));

			refSetExporter.exportBranch(releaseDirectory, context, revisionRange, effectiveTimeFilterStart, effectiveTimeFilterEnd, visitedComponentEffectiveTimes);
		}

		exportLanguageRefSets(releaseDirectory, 
				context, 
				revisionRange, 
				archiveEffectiveTime, 
				effectiveTimeFilterStart, 
				effectiveTimeFilterEnd, 
				languageCodes, 
				referenceSetsByType.get(SnomedRefSetType.LANGUAGE),
				visitedComponentEffectiveTimes);
	}

	private void exportLanguageRefSets(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilterStart,
			final long effectiveTimeFilterEnd, 
			final Collection<String> languageCodes, 
			final Collection<SnomedConcept> languageRefSets,
			final Set<String> visitedComponentEffectiveTimes) throws IOException {

		if (languageRefSets.isEmpty()) {
			return;
		}
		
		for (final String languageCode : languageCodes) {

			final Rf2LanguageRefSetExporter languageExporter = new Rf2LanguageRefSetExporter(releaseType, 
					countryNamespaceElement,
					namespaceFilter,
					transientEffectiveTime,
					archiveEffectiveTime,
					modules,
					SnomedRefSetType.LANGUAGE,
					languageRefSets,
					languageCode);

			languageExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilterStart, effectiveTimeFilterEnd, visitedComponentEffectiveTimes);
		}
	}

	private List<SnomedConcept> getIdentifierConcepts(final RepositoryContext context, final String currentVersion) {
		final Collection<String> refSetsToLoad;
		
		if (refSets == null) {
			// Retrieve all reference sets if refSets is null
			final Request<BranchContext, SnomedReferenceSets> refSetRequest = SnomedRequests.prepareSearchRefSet()
				.all()
				.build();

			final SnomedReferenceSets allReferenceSets = execute(context, currentVersion, refSetRequest);

			refSetsToLoad = allReferenceSets.stream()
					.map(r -> r.getId())
					.collect(Collectors.toSet());
		} else {
			refSetsToLoad = refSets;
		}
		
		final SnomedConceptSearchRequestBuilder refSetRequestBuilder = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByIds(refSetsToLoad)
				.setExpand("pt(),referenceSet()")
				.setLocales(locales());

		final Request<BranchContext, SnomedConcepts> request = refSetRequestBuilder.build();
		final SnomedConcepts referenceSets = execute(context, currentVersion, request);

		// Return only the identifier concepts which have an existing reference set on this branch
		return referenceSets.stream()
				.filter(c -> c.getReferenceSet() != null)
				.collect(Collectors.toList());
	}

	private void registerResult(final AttachmentRegistry fileRegistry, final UUID exportId, final Path exportDirectory) {
		File archiveFile = null;

		try {
			archiveFile = exportDirectory.resolveSibling(exportDirectory.getFileName() + ".zip").toFile();
			FileUtils.createZipArchive(exportDirectory.toFile(), archiveFile);
			fileRegistry.upload(exportId, new FileInputStream(archiveFile)); // lgtm[java/input-resource-leak]
		} catch (final IOException e) {
			throw new SnowowlRuntimeException("Failed to register archive file from export directory.", e);
		} finally {
			if (archiveFile != null) {
				archiveFile.delete();
			}
		}
	}

	private static long getCutoffBaseTimestamp(final RepositoryContext context, final Branch cutoffBranch, final String versionParentPath) {
//		System.err.println("SnomedRf2ExportRequest.getCutoffBaseTimestamp(): branch[" + cutoffBranch.path() + "], branchParentPath: [" + cutoffBranch.parentPath() +  "], versionParentPath: [" + versionParentPath + "]");
		if (cutoffBranch.path().equals(versionParentPath) || Branch.MAIN_PATH.equals(cutoffBranch.path())) {
			// We are on the working branch of the code system, all versions are visible for export
			return Long.MAX_VALUE;	
		} else if (cutoffBranch.parentPath().equals(versionParentPath)) {
			// We are on a direct child of the working branch, versions should be limited according to the base timestamp
			return cutoffBranch.baseTimestamp();
		} else {
			// Two or more levels down from a working branch, look "upwards"
			return getCutoffBaseTimestamp(context, getBranch(context, cutoffBranch.parentPath()), versionParentPath);
		}
	}

	private static Collection<CodeSystemVersionEntry> getCodeSystemVersions(final RepositoryContext context, final String shortName) {
		return CodeSystemRequests.prepareSearchCodeSystemVersion()
				.all()
				.filterByCodeSystemShortName(shortName)
				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build()
				.execute(context)
				.getItems();
	}

	private static Branch getBranch(final RepositoryContext context, final String path) {
//		System.err.println("SnomedRf2ExportRequest.getBranch(): " + path);
		return RepositoryRequests.branching()
				.prepareGet(path)
				.build()
				.execute(context);
	}

	private static Branches getBranches(final RepositoryContext context, final String parent, final Collection<String> paths) {
//		System.err.println("SnomedRf2ExportRequest.getBranches(): " + parent + ", paths: " + paths + "");
		return RepositoryRequests.branching()
				.prepareSearch()
				.all()
				.filterByParent(parent)
				.filterByName(paths)
				.build()
				.execute(context);
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_EXPORT;
	}

}
