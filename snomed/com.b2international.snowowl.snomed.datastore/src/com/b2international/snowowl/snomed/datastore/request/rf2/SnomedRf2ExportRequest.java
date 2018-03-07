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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.FileUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ExportResult;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.exporter.Rf2ConceptExporter;
import com.b2international.snowowl.snomed.datastore.request.rf2.exporter.Rf2DescriptionExporter;
import com.b2international.snowowl.snomed.datastore.request.rf2.exporter.Rf2LanguageRefSetExporter;
import com.b2international.snowowl.snomed.datastore.request.rf2.exporter.Rf2RefSetExporter;
import com.b2international.snowowl.snomed.datastore.request.rf2.exporter.Rf2RelationshipExporter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

/**
 * @since 5.7
 */
final class SnomedRf2ExportRequest implements Request<RepositoryContext, Rf2ExportResult> {

	private static final long serialVersionUID = 1L;

	private static final Ordering<CodeSystemVersionEntry> EFFECTIVE_DATE_ORDERING = Ordering.natural()
			.onResultOf(CodeSystemVersionEntry::getEffectiveDate);

	@JsonProperty
	@NotEmpty
	private String userId;

	@JsonProperty
	@NotEmpty
	private String codeSystem;

	@JsonProperty
	@NotEmpty
	private String referenceBranch;

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
	private Date startEffectiveTime;

	@JsonProperty 
	private Date endEffectiveTime;

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

	void setUserId(final String userId) {
		this.userId = userId;
	}

	void setCodeSystem(final String codeSystem) {
		this.codeSystem = codeSystem;
	}

	void setReferenceBranch(final String referenceBranch) {
		this.referenceBranch = referenceBranch;
	}

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

	void setStartEffectiveTime(final Date startEffectiveTime) {
		this.startEffectiveTime = startEffectiveTime;
	}

	void setEndEffectiveTime(final Date endEffectiveTime) {
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
			this.transientEffectiveTime = "";
		} else if ("NOW".equals(transientEffectiveTime)) {
			this.transientEffectiveTime = EffectiveTimes.format(Dates.todayGmt(), DateFormats.SHORT);
		} else {
			this.transientEffectiveTime = transientEffectiveTime;
		}
	}

	void setExtensionOnly(final boolean extensionOnly) {
		this.extensionOnly = extensionOnly;
	}

	@Override
	public Rf2ExportResult execute(final RepositoryContext context) {

		// Step 1: check if the export reference branch is a working branch path descendant
		final CodeSystemEntry referenceCodeSystem = getCodeSystem(codeSystem);
		final IBranchPath codeSystemPath = BranchPathUtils.createPath(referenceCodeSystem.getBranchPath());
		final IBranchPath referencePath = BranchPathUtils.createPath(referenceBranch);

		if (!isDescendantOf(codeSystemPath, referencePath)) {
			throw new BadRequestException("Export path '%s' is not a descendant of the working path of code system '%s'.", referenceBranch, codeSystem);
		}

		// Step 2: retrieve code system versions that are visible from the reference branch
		final TreeSet<CodeSystemVersionEntry> versionsToExport = getVisibleVersions(referenceCodeSystem);

		// Step 3: determine which versions to keep for the export
		filterVersions(versionsToExport);

		// Step 4: convert code system versions to branches; add reference branch if exported content should also be considered
		final Map<String, Long> pathEffectiveTimeMap = versionsToExport.stream()
				.collect(Collectors.toMap(
						v -> v.getPath(), // the version's full branch path
						v -> v.getEffectiveDate(), // the version's effective time
						(u,v) -> { throw new IllegalStateException(String.format("Duplicate value: %s", u)); },
						LinkedHashMap::new));

		if (Rf2ReleaseType.SNAPSHOT.equals(releaseType) && includePreReleaseContent) {
			// Export all visible components from the reference branch (remove single existing map entry)
			final String singleVersionBranch = Iterables.getOnlyElement(pathEffectiveTimeMap.keySet());
			pathEffectiveTimeMap.remove(singleVersionBranch);
			pathEffectiveTimeMap.put(referenceBranch, Long.MIN_VALUE);
		} else if (Rf2ReleaseType.SNAPSHOT.equals(releaseType)) {
			// Export all visible components from the reference branch, skip unpublished components (remove single existing map entry)
			final String singleVersionBranch = Iterables.getOnlyElement(pathEffectiveTimeMap.keySet());
			pathEffectiveTimeMap.remove(singleVersionBranch);
			pathEffectiveTimeMap.put(referenceBranch, 0L);
		} else if (includePreReleaseContent) {
			// Export pre-release components from the reference branch (add map entry)
			pathEffectiveTimeMap.put(referenceBranch, EffectiveTimes.UNSET_EFFECTIVE_TIME);
		}

		final UUID exportId = UUID.randomUUID();
		Path exportDirectory = null;

		try {

			exportDirectory = createExportDirectory(exportId);

			// Step 5: export content for each branch
			String archiveEffectiveTime;
			
			if (versionsToExport.isEmpty() || includePreReleaseContent) {
				
				if (transientEffectiveTime.isEmpty()) {
					archiveEffectiveTime = EffectiveTimes.format(Dates.todayGmt(), DateFormats.SHORT);
				} else {
					archiveEffectiveTime = transientEffectiveTime;
				}
				
			} else {
				archiveEffectiveTime = EffectiveTimes.format(versionsToExport.last().getEffectiveDate(), DateFormats.SHORT);
			}
			
			final Path releaseDirectory = createReleaseDirectory(exportDirectory, archiveEffectiveTime);

			for (final Entry<String, Long> pathEntry : pathEffectiveTimeMap.entrySet()) {
				exportBranch(releaseDirectory, 
						context, 
						pathEntry.getKey(), 
						archiveEffectiveTime, 
						pathEntry.getValue());
			}

			// Step 6: compress to archive and upload to the file registry
			final FileRegistry fileRegistry = context.service(FileRegistry.class);
			registerResult(fileRegistry, exportId, exportDirectory);
			final String fileName = releaseDirectory.getFileName() + ".zip";
			return new Rf2ExportResult(fileName, exportId);
			
		} catch (final IOException e) {
			throw new SnowowlRuntimeException("Failed to export terminology content to RF2.", e);
		} finally {
			if (exportDirectory != null) {
				FileUtils.deleteDirectory(exportDirectory.toFile());
			}
		}
	}

	private TreeSet<CodeSystemVersionEntry> getVisibleVersions(final CodeSystemEntry codeSystemEntry) {
		final TreeSet<CodeSystemVersionEntry> visibleVersions = newTreeSet(EFFECTIVE_DATE_ORDERING);
		collectVersionsToExport(visibleVersions, codeSystemEntry, referenceBranch);
		return visibleVersions;
	}

	private void collectVersionsToExport(final Set<CodeSystemVersionEntry> versionsToExport, final CodeSystemEntry codeSystemEntry, final String cutoffPath) {
		final Collection<CodeSystemVersionEntry> candidates = newArrayList(getCodeSystemVersions(codeSystemEntry.getShortName()));
		if (candidates.isEmpty()) {
			return;
		}

		final String versionParentPath = candidates.stream()
				.map(CodeSystemVersionEntry::getParentBranchPath)
				.findFirst()
				.get();

		final Set<String> versionNames = candidates.stream()
				.map(CodeSystemVersionEntry::getVersionId)
				.collect(Collectors.toSet());

		final Branches versionBranches = getBranches(versionParentPath, versionNames);
		final Map<String, Branch> versionBranchesByName = FluentIterable.from(versionBranches)
				.uniqueIndex(b -> b.name());

		final Branch cutoffBranch = getBranch(cutoffPath);
		final long cutoffBaseTimestamp = getCutoffBaseTimestamp(cutoffBranch, versionParentPath);

		// Remove all code system versions which were created after the cut-off date, or don't have a corresponding branch 
		candidates.removeIf(v -> false
				|| !versionBranchesByName.containsKey(v.getVersionId())
				|| versionBranchesByName.get(v.getVersionId()).baseTimestamp() > cutoffBaseTimestamp);

		versionsToExport.addAll(candidates);

		// Exit early if only an extension code system should be exported, or we are already at the "base" code system
		if (extensionOnly || Strings.isNullOrEmpty(codeSystemEntry.getExtensionOf())) {
			return;
		}

		// Otherwise, collect applicable versions using this code system's working path
		final CodeSystemEntry extensionEnty = getCodeSystem(codeSystemEntry.getExtensionOf());
		collectVersionsToExport(versionsToExport, extensionEnty, codeSystemEntry.getBranchPath());
	}

	private void filterVersions(final TreeSet<CodeSystemVersionEntry> versionsToExport) {
		// Is there anything to filter?
		if (versionsToExport.isEmpty()) {
			return;
		}

		switch (releaseType) {
			case DELTA:
				// Delta export: keep versions that fall into the effective time range
				if (startEffectiveTime == null && endEffectiveTime == null) {
					versionsToExport.clear();
				} else {
					versionsToExport.removeIf(v -> {
						return false
								|| (startEffectiveTime != null && v.getEffectiveDate() < startEffectiveTime.getTime())
								|| (endEffectiveTime != null && v.getEffectiveDate() > endEffectiveTime.getTime());
					});
				}
				break;
			case FULL:
				// Full export: nothing to remove, all versions should be exported
				break;
			case SNAPSHOT:
				// Snapshot export: keep only the latest visible version
				versionsToExport.retainAll(ImmutableSet.of(versionsToExport.last()));
				break;
			default:
				throw new IllegalStateException("Unexpected RF2 release type '" + releaseType + "'.");
		}
	}

	private Path createExportDirectory(final UUID exportId) {
		try {
			return Files.createTempDirectory("export-" + exportId + "-");
		} catch (final IOException e) {
			throw new SnowowlRuntimeException("Failed to create working directory for export.", e);
		}
	}

	private Path createReleaseDirectory(final Path exportDirectory, final String latestEffectiveTime) {
		final String releaseStatus = includePreReleaseContent
				? "BETA"
				: "PRODUCTION";
		
		final Path releaseDirectory = exportDirectory.resolve(String.format("SNOMEDCT_RF2_%s_%sT120000Z", releaseStatus, latestEffectiveTime));

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
			final long effectiveTimeFilter) throws IOException {

		final Set<String> languageCodes = getLanguageCodes(context, branch);
		
		for (final String componentToExport : componentTypes) {
			switch (componentToExport) {
				case SnomedTerminologyComponentConstants.CONCEPT:
					exportConcepts(releaseDirectory, 
							context, 
							branch, 
							archiveEffectiveTime, 
							effectiveTimeFilter);
					break;
	
				case SnomedTerminologyComponentConstants.DESCRIPTION:
					for (final String languageCode : languageCodes) {
						exportDescriptions(releaseDirectory, 
								context, 
								branch, 
								archiveEffectiveTime, 
								effectiveTimeFilter, 
								languageCode);
					}
					break;
	
				case SnomedTerminologyComponentConstants.RELATIONSHIP:
					exportRelationships(releaseDirectory, 
							context, 
							branch, 
							archiveEffectiveTime, 
							effectiveTimeFilter);
					break;
	
				case SnomedTerminologyComponentConstants.REFSET_MEMBER:
					if (Rf2RefSetExportLayout.COMBINED.equals(refSetExportLayout)) {
						exportCombinedRefSets(releaseDirectory,
								context,
								branch,
								archiveEffectiveTime,
								effectiveTimeFilter,
								languageCodes);
					} else {
						exportIndividualRefSets(releaseDirectory,
								context,
								branch,
								archiveEffectiveTime,
								effectiveTimeFilter,
								languageCodes);
					}
				break;

			default:
				throw new IllegalStateException("Component type '" + componentToExport + "' can not be exported.");
			}
		}
	}

	private void exportConcepts(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilter) throws IOException {

		final Rf2ConceptExporter conceptExporter = new Rf2ConceptExporter(releaseType, 
				countryNamespaceElement, 
				namespaceFilter,
				transientEffectiveTime,
				archiveEffectiveTime,
				includePreReleaseContent,
				modules);

		conceptExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilter);
	}

	private void exportDescriptions(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilter, 
			final String languageCode) throws IOException {

		final Rf2DescriptionExporter descriptionExporter = new Rf2DescriptionExporter(releaseType, 
				countryNamespaceElement,
				namespaceFilter,
				transientEffectiveTime,
				archiveEffectiveTime, 
				includePreReleaseContent, 
				modules, 
				"<<" + Concepts.DESCRIPTION_TYPE_ROOT_CONCEPT + " MINUS " + Concepts.TEXT_DEFINITION,
				languageCode);

		final Rf2DescriptionExporter textDefinitionExporter = new Rf2DescriptionExporter(releaseType, 
				countryNamespaceElement,
				namespaceFilter, 
				transientEffectiveTime,
				archiveEffectiveTime, 
				includePreReleaseContent, 
				modules, 
				Concepts.TEXT_DEFINITION,
				languageCode);

		descriptionExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilter);
		textDefinitionExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilter);
	}

	private void exportRelationships(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilter) throws IOException {

		final Rf2RelationshipExporter statedRelationshipExporter = new Rf2RelationshipExporter(releaseType, 
				countryNamespaceElement, 
				namespaceFilter, 
				transientEffectiveTime,
				archiveEffectiveTime, 
				includePreReleaseContent, 
				modules, 
				Concepts.STATED_RELATIONSHIP);

		final Rf2RelationshipExporter relationshipExporter = new Rf2RelationshipExporter(releaseType, 
				countryNamespaceElement, 
				namespaceFilter, 
				transientEffectiveTime,
				archiveEffectiveTime, 
				includePreReleaseContent, 
				modules, 
				"<<" + Concepts.CHARACTERISTIC_TYPE + " MINUS " + Concepts.STATED_RELATIONSHIP);

		statedRelationshipExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilter);
		relationshipExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilter);
	}

	private void exportCombinedRefSets(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilter, 
			final Set<String> languageCodes) throws IOException {

		final Multimap<SnomedRefSetType, SnomedConcept> referenceSetsByType = FluentIterable.from(getIdentifierConcepts(context, branch))
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
					includePreReleaseContent,
					modules,
					refSetExportLayout,
					refSetType,
					referenceSetsByType.get(refSetType));

			refSetExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilter);
		}

		exportLanguageRefSets(releaseDirectory, 
				context, 
				branch, 
				archiveEffectiveTime, 
				effectiveTimeFilter, 
				languageCodes, 
				referenceSetsByType.get(SnomedRefSetType.LANGUAGE));
	}

	private void exportIndividualRefSets(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilter, 
			final Set<String> languageCodes) throws IOException {

		final Multimap<SnomedRefSetType, SnomedConcept> referenceSetsByType = FluentIterable.from(getIdentifierConcepts(context, branch))
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
					includePreReleaseContent,
					modules,
					refSetExportLayout,
					entry.getKey(),
					ImmutableSet.of(entry.getValue()));

			refSetExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilter);
		}

		exportLanguageRefSets(releaseDirectory, 
				context, 
				branch, 
				archiveEffectiveTime, 
				effectiveTimeFilter, 
				languageCodes, 
				referenceSetsByType.get(SnomedRefSetType.LANGUAGE));
	}

	private void exportLanguageRefSets(final Path releaseDirectory, 
			final RepositoryContext context, 
			final String branch,
			final String archiveEffectiveTime, 
			final long effectiveTimeFilter, 
			final Set<String> languageCodes, 
			final Collection<SnomedConcept> languageRefSets) throws IOException {

		if (languageRefSets.isEmpty()) {
			return;
		}
		
		for (final String languageCode : languageCodes) {

			final Rf2LanguageRefSetExporter languageExporter = new Rf2LanguageRefSetExporter(releaseType, 
					countryNamespaceElement,
					namespaceFilter,
					transientEffectiveTime,
					archiveEffectiveTime,
					includePreReleaseContent,
					modules,
					SnomedRefSetType.LANGUAGE,
					languageRefSets,
					languageCode);

			languageExporter.exportBranch(releaseDirectory, context, branch, effectiveTimeFilter);
		}
	}

	private Set<String> getLanguageCodes(final RepositoryContext context, final String branch) {
		final Set<String> languageCodes = newHashSet();

		// TODO: there should be an easier way than trying all possible language codes...
		for (final String code : Locale.getISOLanguages()) {
			final Request<BranchContext, SnomedDescriptions> languageCodeRequest = SnomedRequests.prepareSearchDescription()
					.setLimit(0)
					.filterByLanguageCodes(ImmutableSet.of(code))
					.build();

			final SnomedDescriptions descriptions = new BranchRequest<>(branch, new RevisionIndexReadRequest<>(languageCodeRequest))
					.execute(context);

			if (descriptions.getTotal() > 0) {
				languageCodes.add(code);
			}
		}

		return languageCodes;
	}

	private List<SnomedConcept> getIdentifierConcepts(final RepositoryContext context, final String branch) {
		final Collection<String> refSetsToLoad;
		
		if (refSets == null) {
			// Retrieve all reference sets if refSets is null
			final Request<BranchContext, SnomedReferenceSets> refSetRequest = SnomedRequests.prepareSearchRefSet()
				.all()
				.build();

			final SnomedReferenceSets allReferenceSets = new BranchRequest<>(branch, new RevisionIndexReadRequest<>(refSetRequest))
					.execute(context);

			refSetsToLoad = allReferenceSets.stream()
					.map(r -> r.getId())
					.collect(Collectors.toSet());
		} else {
			refSetsToLoad = refSets;
		}
		
		final LanguageSetting languageSetting = getServiceForClass(LanguageSetting.class);
		final SnomedConceptSearchRequestBuilder refSetRequestBuilder = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByIds(refSetsToLoad)
				.setExpand("pt(),referenceSet()")
				.setLocales(languageSetting.getLanguagePreference());

		final Request<BranchContext, SnomedConcepts> request = refSetRequestBuilder.build();
		final SnomedConcepts referenceSets = new BranchRequest<>(branch, new RevisionIndexReadRequest<>(request))
				.execute(context);

		// Return only the identifier concepts which have an existing reference set on this branch
		return referenceSets.stream()
				.filter(c -> c.getReferenceSet() != null)
				.collect(Collectors.toList());
	}

	private void registerResult(final FileRegistry fileRegistry, final UUID exportId, final Path exportDirectory) {
		File archiveFile = null;

		try {
			archiveFile = exportDirectory.resolveSibling(exportDirectory.getFileName() + ".zip").toFile();
			FileUtils.createZipArchive(exportDirectory.toFile(), archiveFile);
			fileRegistry.upload(exportId, new FileInputStream(archiveFile));
		} catch (final IOException e) {
			throw new SnowowlRuntimeException("Failed to register archive file from export directory.", e);
		} finally {
			if (archiveFile != null) {
				archiveFile.delete();
			}
		}
	}

	private static boolean isDescendantOf(final IBranchPath codeSystemPath, final IBranchPath referencePath) {
		for (final Iterator<IBranchPath> itr = BranchPathUtils.bottomToTopIterator(referencePath); itr.hasNext(); /* empty */) {
			if (itr.next().equals(codeSystemPath)) {
				return true;
			}
		}

		return false;
	}

	private static long getCutoffBaseTimestamp(final Branch cutoffBranch, final String versionParentPath) {
		if (cutoffBranch.path().equals(versionParentPath)) {
			// We are on the working branch of the code system, all versions are visible for export
			return Long.MAX_VALUE;
		} else if (cutoffBranch.parentPath().equals(versionParentPath)) {
			// We are on a direct child of the working branch, versions should be limited according to the base timestamp
			return cutoffBranch.baseTimestamp();
		} else {
			// Two or more levels down from a working branch, look "upwards"
			return getCutoffBaseTimestamp(getBranch(cutoffBranch.parentPath()), versionParentPath);
		}
	}

	private static CodeSystemEntry getCodeSystem(final String shortName) {
		return CodeSystemRequests.prepareSearchCodeSystem()
				.one()
				.filterById(shortName)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getEventBus())
				.getSync()
				.first()
				.orElse(null);
	}

	private static Collection<CodeSystemVersionEntry> getCodeSystemVersions(final String shortName) {
		return CodeSystemRequests.prepareSearchCodeSystemVersion()
				.all()
				.filterByCodeSystemShortName(shortName)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getEventBus())
				.getSync()
				.getItems();
	}

	private static Branch getBranch(final String path) {
		return RepositoryRequests.branching()
				.prepareGet(path)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getEventBus())
				.getSync();
	}

	private static Branches getBranches(final String parent, final Collection<String> paths) {
		return RepositoryRequests.branching().prepareSearch()
				.all()
				.filterByParent(parent)
				.filterByName(paths)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getEventBus())
				.getSync();
	}

	private static IEventBus getEventBus() {
		return getServiceForClass(IEventBus.class);
	}
}
