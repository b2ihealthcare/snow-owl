/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.rf2;

import static com.b2international.commons.pcj.LongSets.newLongSet;
import static com.b2international.commons.pcj.LongSets.toStringList;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.commons.csv.CsvLexer.EOL;
import com.b2international.commons.csv.CsvParser;
import com.b2international.commons.csv.CsvSettings;
import com.b2international.commons.csv.RecordParserCallback;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOTransactionFunction;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.index.ComponentCompareFieldsUpdater;
import com.b2international.snowowl.datastore.index.DocumentCompositeUpdater;
import com.b2international.snowowl.datastore.index.DocumentUpdater;
import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.snomed.index.NamespaceMapping;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.datastore.server.snomed.index.init.DoiInitializer;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.MrcmEditingContext;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedRelationshipIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange.MemberChangeKind;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberImmutablePropertyUpdater;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberMutablePropertyUpdater;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMutablePropertyUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ComponentConstraintUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.RefSetIconIdUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.RefSetParentageUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ReferenceSetMembershipUpdater;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.snor.ConstraintFormIsApplicableForValidationPredicate;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportUnit;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import bak.pcj.map.LongKeyFloatMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

/**
 * RF2 based incremental index initializer job.
 */
public class SnomedRf2IndexInitializer extends Job {

	private static final Collection<String> AVAILABLE_ICON_IDS = SnomedIconProvider.getInstance().getAvailableIconIds();
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedRf2IndexInitializer.class);
	private static final CsvSettings CSV_SETTINGS = new CsvSettings('\0', '\t', EOL.LF, true);
	private static final String ACTIVE_STATUS = "1";
	
	private final String effectiveTimeKey;
	private final List<ComponentImportUnit> importUnits;
	private final IBranchPath branchPath;

	private Multimap<Long, String> conceptIdToPredicateMap;
	private Map<String, String> nonFsnIdToConceptIdMap;
	
	private Multimap<String, RefSetMemberChange> refSetMemberChanges;
	private Multimap<String, RefSetMemberChange> mappingRefSetMemberChanges;
	private Multimap<String, RefSetMemberChange> preferredMemberChanges;
	private Multimap<String, RefSetMemberChange> acceptableMemberChanges;
	
	private Set<String> conceptsInImportFile;
	private Set<String> descriptionsInImportFile;
	
	private Set<String> conceptsWithMembershipChanges;
	private Set<String> conceptsWithTaxonomyChanges;
	private Set<String> conceptsWithCompareUniqueKeyChanges;
	
	// refset ID to fake SnomedRefSet EObject
	private Map<String, SnomedRefSet> visitedRefSets;
	private Set<String> skippedReferenceSets;
	private LongKeyFloatMap doiData;
	//when a reference set is imported where the concept is being created on the fly
	private final Map<String, SnomedRefSetType> identifierConceptIdsForNewRefSets = newHashMap();

	private ISnomedTaxonomyBuilder inferredTaxonomyBuilder;
	private ISnomedTaxonomyBuilder statedTaxonomyBuilder;

	public SnomedRf2IndexInitializer(final IBranchPath branchPath, final String lastUnitEffectiveTimeKey, final List<ComponentImportUnit> importUnits, final String languageRefSetId, ISnomedTaxonomyBuilder inferredTaxonomyBuilder, ISnomedTaxonomyBuilder statedTaxonomyBuilder) {
		super("SNOMED CT RF2 based index initializer...");
		this.branchPath = branchPath;
		this.effectiveTimeKey = lastUnitEffectiveTimeKey;
		this.statedTaxonomyBuilder = checkNotNull(statedTaxonomyBuilder, "statedTaxonomyBuilder");
		this.inferredTaxonomyBuilder = checkNotNull(inferredTaxonomyBuilder, "inferredTaxonomyBuilder");
		this.importUnits = Collections.unmodifiableList(importUnits);
		//check services
		getImportIndexService();
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) {

		final IProgressMonitor delegateMonitor = null == monitor ? new NullProgressMonitor() : monitor;
		final List<ComponentImportUnit> importUnits = collectImportUnits();
		
		delegateMonitor.beginTask("Indexing SNOMED CT...", importUnits.size() + 3);
		
		LOGGER.info("Initializing SNOMED CT semantic content from RF2 release format for key '{}'...", effectiveTimeKey);
		LOGGER.info("Pre-processing phase [1 of 3]...");
		
		doiData = new DoiInitializer().run(delegateMonitor);
		
		LOGGER.info("Collecting MRCM rule related changes...");
		conceptIdToPredicateMap = HashMultimap.create();
		
		try (MrcmEditingContext context = new MrcmEditingContext()) {
			ConceptModel conceptModel = context.getOrCreateConceptModel();
			
			final Iterable<AttributeConstraint> constraints = FluentIterable.from(conceptModel.getConstraints())
					.filter(new ConstraintFormIsApplicableForValidationPredicate())
					.filter(AttributeConstraint.class);
			
			for (final AttributeConstraint constraint : constraints) {
				final long storageKey = CDOIDUtil.getLong(constraint.cdoID());
				for (final ConstraintDomain constraintDomain : PredicateUtils.processConstraintDomain(storageKey, constraint.getDomain())) {
					conceptIdToPredicateMap.put(constraintDomain.getComponentId(), constraintDomain.getPredicateKey());
				}
			}
		}
		
		conceptsInImportFile = Sets.newHashSet();
		descriptionsInImportFile = Sets.newHashSet();
		
		conceptsWithMembershipChanges = Sets.newHashSet();
		conceptsWithTaxonomyChanges = Sets.newHashSet();
		conceptsWithCompareUniqueKeyChanges = Sets.newHashSet();
		
		refSetMemberChanges = HashMultimap.create();
		mappingRefSetMemberChanges = HashMultimap.create();
		preferredMemberChanges = HashMultimap.create();
		acceptableMemberChanges = HashMultimap.create();
		visitedRefSets = Maps.newHashMap();
		skippedReferenceSets = Sets.newHashSet();

		LOGGER.info("Gathering mappings between descriptions and concepts...");
		collectDescriptionToConceptIds(importUnits);
		LOGGER.info("Description to concept mapping successfully finished.");

		delegateMonitor.setTaskName("Collecting reference set memberships...");
		collectRefSetMembershipAndMapping(importUnits);
		delegateMonitor.worked(1);
		
		LOGGER.info("Pre-processing phase successfully finished.");
		LOGGER.info("Indexing phase [2 of 3]...");
		doImport(importUnits, delegateMonitor);
		
		LOGGER.info("SNOMED CT semantic content for key '{}' have been successfully initialized.", effectiveTimeKey);
		
		return Status.OK_STATUS;
	}

	private void collectDescriptionToConceptIds(final List<ComponentImportUnit> importUnits) {
		nonFsnIdToConceptIdMap = Maps.newHashMap();
		
		for (final ComponentImportUnit unit : importUnits) {
			switch (unit.getType()) {
				case DESCRIPTION:
				case TEXT_DEFINITION:
					parseFile(unit.getUnitFile().getAbsolutePath(), 9, new RecordParserCallback<String>() {
						@Override
						public void handleRecord(final int recordCount, final List<String> record) {
							if (!Concepts.FULLY_SPECIFIED_NAME.equals(record.get(6)) && !Concepts.TEXT_DEFINITION.equals(record.get(6))) {
								nonFsnIdToConceptIdMap.put(record.get(0), record.get(4));
							}
						}
					});
					break;
					
				default:
					break;
			}
		}
	}

	private void collectRefSetMembershipAndMapping(final List<ComponentImportUnit> importUnits) {
		
		LOGGER.info("Collecting reference set member related changes...");
		
		for (final ComponentImportUnit unit : importUnits) {
			
			switch (unit.getType()) {
				
				case SIMPLE_TYPE_REFSET:
				case ATTRIBUTE_VALUE_REFSET:
					LOGGER.info("Collecting reference set membership changes.");
					collectMembership(unit, refSetMemberChanges);
					break;
				case SIMPLE_MAP_TYPE_REFSET:
					LOGGER.info("Collecting map type reference set membership changes.");
					collectMembership(unit, mappingRefSetMemberChanges);
					break;
				case LANGUAGE_TYPE_REFSET:
					LOGGER.info("Collecting language type reference set membership changes.");
					collectLanguageMembership(unit);
					break;
				default:
					break;
			}
		}
		
		LOGGER.info("Reference set member related changes have been successfully finished.");
	}

	private void collectMembership(final ComponentImportUnit unit, final Multimap<String, RefSetMemberChange> memberChanges) {
		
		final int columnCount;
		final SnomedRefSetType refSetType;
		
		switch (unit.getType()) {
			case ATTRIBUTE_VALUE_REFSET:
				refSetType = SnomedRefSetType.ATTRIBUTE_VALUE;
				columnCount = 7;
				break;
			case SIMPLE_MAP_TYPE_REFSET:
				refSetType = SnomedRefSetType.SIMPLE_MAP;
				columnCount = 7;
				break;
			case SIMPLE_TYPE_REFSET:
				refSetType = SnomedRefSetType.SIMPLE;
				columnCount = 6;
				break;
			default: 
				throw new IllegalArgumentException("Unhandled import type for membership collection: " + unit.getType());
		}

		final File unitFile = unit.getUnitFile();
		if (null != unitFile && unitFile.canRead() && unitFile.isFile()) {
			parseFile(unitFile.getAbsolutePath(), columnCount, new RecordParserCallback<String>() {

				@Override
				public void handleRecord(final int recordCount, final List<String> record) {

					final String uuid = record.get(0);
					final long refSetId = Long.parseLong(record.get(4));
					final String conceptId = record.get(5);
					
					if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER == SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(conceptId)) {
						final RefSetMemberChange change;
						
						if (ACTIVE_STATUS.equals(record.get(2))) {
							change = new RefSetMemberChange(uuid, refSetId, MemberChangeKind.ADDED, refSetType);
						} else {
							change = new RefSetMemberChange(uuid, refSetId, MemberChangeKind.REMOVED, refSetType);
						}
						
						memberChanges.put(conceptId, change);

						// Add the concept ID to the set that needs a membership field update
						conceptsWithMembershipChanges.add(conceptId);
					}
				}
			});
		}		
	}

	private void collectLanguageMembership(ComponentImportUnit unit) {
		
		final File unitFile = unit.getUnitFile();
		
		if (null != unitFile && unitFile.canRead() && unitFile.isFile()) {
			parseFile(unitFile.getAbsolutePath(), 7, new RecordParserCallback<String>() {

				@Override
				public void handleRecord(final int recordCount, final List<String> record) {

					final String uuid = record.get(0);
					final long refSetId = Long.parseLong(record.get(4));
					final String descriptionId = record.get(5);
					final String acceptabilityId = record.get(6);
					
					final RefSetMemberChange positiveChange = new RefSetMemberChange(uuid, refSetId, MemberChangeKind.ADDED, SnomedRefSetType.LANGUAGE);
					final RefSetMemberChange negativeChange = new RefSetMemberChange(uuid, refSetId, MemberChangeKind.REMOVED, SnomedRefSetType.LANGUAGE);
					
					if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(acceptabilityId)) {
						
						if (ACTIVE_STATUS.equals(record.get(2))) {
							// Preferred field gains this member, acceptable field should lose this member if it was present previously
							preferredMemberChanges.put(descriptionId, positiveChange);
							acceptableMemberChanges.put(descriptionId, negativeChange);
						} else {
							// Both fields lose this member
							preferredMemberChanges.put(descriptionId, negativeChange);
							acceptableMemberChanges.put(descriptionId, negativeChange);
						}
						
						String conceptId = nonFsnIdToConceptIdMap.get(descriptionId);
						if (StringUtils.isEmpty(conceptId)) {
							// FIXME: This lookup can be avoided if the description is an FSN in the current RF2 file, and did not exist previously 
							 final String[] descriptionProperties = ApplicationContext.getServiceForClass(ISnomedComponentService.class).getDescriptionProperties(branchPath, descriptionId);
							 if (descriptionProperties != null) {
								 final String typeId = descriptionProperties[1];
								 if (!Concepts.FULLY_SPECIFIED_NAME.equals(typeId) && !Concepts.TEXT_DEFINITION.equals(typeId)) { 
									 conceptId = descriptionProperties[0];
								 }
							 }
						}
						
						if (!StringUtils.isEmpty(conceptId)) {
							// Collect the concept for needing an "important change" compare unique key update (a potential PT change is sensed)
							conceptsWithCompareUniqueKeyChanges.add(conceptId);
						}
						
					} else {
						
						if (ACTIVE_STATUS.equals(record.get(2))) {
							// Acceptable field gains this member, preferred field should lose this member if it was present previously
							preferredMemberChanges.put(descriptionId, negativeChange);
							acceptableMemberChanges.put(descriptionId, positiveChange);
						} else {
							// Both fields lose this member
							preferredMemberChanges.put(descriptionId, negativeChange);
							acceptableMemberChanges.put(descriptionId, negativeChange);
						}
					}
				}
			});
		}
	}
	
	private void parseFile(final String filePath, final int columnCount, final RecordParserCallback<String> callback) {
		Reader reader = null;
		try {
			reader = new FileReader(new File(filePath));
			new CsvParser(reader, CSV_SETTINGS, callback, columnCount).parse();
		} catch (final IOException e) {
			throw new SnowowlRuntimeException("Populating reference set memberships '" + filePath + "' file.");
		} finally {
			if (null != reader) {

				try {
					reader.close();
				} catch (final IOException e) {
					
					try {
						reader.close();
					} catch (final IOException e1) {
					}

					throw new ImportException("Error while closing " + filePath);
					
				}
			}
		}
	}

	private ImportIndexServerService getImportIndexService() {
		return Preconditions.checkNotNull(ApplicationContext.getInstance().getService(ImportIndexServerService.class), "Import index server service was null.");
	}
	
	private SnomedIndexServerService getSnomedIndexService() {
		return (SnomedIndexServerService) Preconditions.checkNotNull(ApplicationContext.getInstance().getService(SnomedIndexService.class), "SNOMED CT index server service was null.");
	}
	
	private List<ComponentImportUnit> collectImportUnits() {
		
		final List<ComponentImportUnit> importUnitsForCurrentEffectiveTime = newArrayList();
		
		for (final ComponentImportUnit unit : importUnits) {
			if (Objects.equal(effectiveTimeKey, unit.getEffectiveTimeKey())) {
				importUnitsForCurrentEffectiveTime.add(unit);
			}
		}
		
		Collections.sort(importUnitsForCurrentEffectiveTime, ComponentImportUnit.ORDERING);
		return Collections.unmodifiableList(importUnitsForCurrentEffectiveTime);
	}
	
	private void doImport(final Iterable<ComponentImportUnit> units, final IProgressMonitor delegateMonitor) {
		
		boolean loggedReferenceSetImport = false;
		
		for (final ComponentImportUnit unit : units) {
			
			try {
	
				final String absolutePath = unit.getUnitFile().getAbsolutePath();
				switch (unit.getType()) {
	
					case CONCEPT:
						LOGGER.info("Indexing concepts...");
						indexConcepts(absolutePath);
						getSnomedIndexService().commit(branchPath);
						LOGGER.info("Concepts have been successfully indexed.");
						break;
					case DESCRIPTION:
					case TEXT_DEFINITION:
						LOGGER.info("Indexing descriptions...");
						indexDescriptions(absolutePath);
						getSnomedIndexService().commit(branchPath);
						LOGGER.info("Descriptions have been successfully indexed.");
						break;
					case RELATIONSHIP:
					case STATED_RELATIONSHIP:
						LOGGER.info("Indexing relationships...");
						indexRelationships(absolutePath);
						getSnomedIndexService().commit(branchPath);
						LOGGER.info("Relationships have been successfully indexed.");
						break;
					case TERMINOLOGY_REGISTRY:
						//do nothing
						break;
					default:
						if (!loggedReferenceSetImport) {
							LOGGER.info("Indexing reference sets and their members...");
							loggedReferenceSetImport = true;
						}
						indexRefSets(unit);
						getSnomedIndexService().commit(branchPath);
						break;
				}
				
			} finally {
				delegateMonitor.worked(1);
			}
		}

		LOGGER.info("Reference sets and their members have been successfully indexed.");
		LOGGER.info("Indexing phase successfully finished.");
		LOGGER.info("Post-processing phase [3 of 3]...");
		
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Concepts needing reference set membership updates: " + conceptsWithMembershipChanges.size());
			LOGGER.trace("Concepts needing taxonomy updates: " + conceptsWithTaxonomyChanges.size());
			LOGGER.trace("Concepts needing compare unique key updates: " + conceptsWithCompareUniqueKeyChanges.size());
		}
		
		LOGGER.info("Collecting unvisited concepts...");
		
		final Set<String> unvisitedConcepts = Sets.newHashSet();
		unvisitedConcepts.addAll(conceptsWithCompareUniqueKeyChanges);
		unvisitedConcepts.addAll(conceptsWithMembershipChanges);
		unvisitedConcepts.addAll(conceptsWithTaxonomyChanges);
		
		// Add descendant concepts as well, since they require taxonomy information updates
		final Set<String> statementDescendants = getAllDescendants(conceptsWithTaxonomyChanges);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Statement descendant count: " + statementDescendants.size());
		}
		unvisitedConcepts.addAll(statementDescendants);
		
		// Add all descendants of seen concepts as well
		final Set<String> visitedDescendants = getAllDescendants(conceptsInImportFile);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Visited concept descendant count: " + visitedDescendants.size());
			LOGGER.trace("Visited concept count: " + conceptsInImportFile.size());
		}
		unvisitedConcepts.addAll(visitedDescendants);
		
		//index MRCM related changed on the concept.
		//See issue: https://snowowl.atlassian.net/browse/SO-1532?focusedCommentId=29572&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-29572
		unvisitedConcepts.addAll(toStringList(newLongSet(conceptIdToPredicateMap.keySet())));
		
		// Finally, remove all concepts that were already processed because an RF2 row was visited.
		unvisitedConcepts.removeAll(conceptsInImportFile);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Overall difference count: " + unvisitedConcepts.size());
		}

		LOGGER.info("Unvisited concepts have been successfully collected.");

		if (!unvisitedConcepts.isEmpty()) {
			LOGGER.info("Reindexing unvisited concepts...");
			indexUnvisitedConcepts(unvisitedConcepts, conceptsWithCompareUniqueKeyChanges);
			LOGGER.info("Unvisited concepts have been successfully reindexed.");
		} else {
			LOGGER.info("No unvisited concepts have been found.");
		}
		
		LOGGER.info("Collecting unvisited descriptions...");
		final Set<String> unvisitedDescriptions = Sets.newHashSet();
		unvisitedDescriptions.addAll(preferredMemberChanges.keySet());
		unvisitedDescriptions.addAll(acceptableMemberChanges.keySet());
		unvisitedDescriptions.removeAll(descriptionsInImportFile);
		LOGGER.info("Unvisited descriptions have been successfully collected.");

		if (!unvisitedDescriptions.isEmpty()) {
			LOGGER.info("Reindexing unvisited descriptions...");
			indexUnvisitedDescriptions(unvisitedDescriptions);
			LOGGER.info("Unvisited descriptions have been successfully reindexed.");
		} else {
			LOGGER.info("No unvisited descriptions have been found.");
		}
		
		if (!unvisitedConcepts.isEmpty() || !unvisitedDescriptions.isEmpty()) {
			getSnomedIndexService().commit(branchPath);
		}
		
		LOGGER.info("Post-processing phase successfully finished.");
	}

	private Set<String> getAllDescendants(final Set<String> union) {
		final LongSet $ = new LongOpenHashSet();
		for (final String conceptId : union) {
			if (inferredTaxonomyBuilder.containsNode(conceptId)) { //inactive one
				$.addAll(inferredTaxonomyBuilder.getAllDescendantNodeIds(conceptId));
			}
		}
		return LongSets.toStringSet($);
	}

	private void indexRelationships(final String absolutePath) {
		
		final SnomedIndexServerService snomedIndexService = getSnomedIndexService();
		
		parseFile(absolutePath, 10, new RecordParserCallback<String>() {
			@Override
			public void handleRecord(final int recordCount, final java.util.List<String> record) { 
				
				final long storageKey = getImportIndexService().getComponentCdoId(record.get(0));
				final String sctId = record.get(0);
				final long sourceConceptId = Long.parseLong(record.get(4));
				final long typeConceptId = Long.parseLong(record.get(7));
				final long destinationConceptId = Long.parseLong(record.get(5));
				
				//track concept taxonomy changes even if the concept is not among the changed concepts 
				//but either its source or destination has changed.
				//destination concept changes are intentionally ignored, as taxonomy information is propagated from top to bottom.
				if (!conceptsInImportFile.contains(record.get(4))) {
					if (Concepts.IS_A.equals(record.get(7))) {
						conceptsWithTaxonomyChanges.add(record.get(4));
					}

					// The concept will also need a compare unique key update
					conceptsWithCompareUniqueKeyChanges.add(record.get(4));
				}
				
				final long characteristicTypeConceptSctId = Long.parseLong(record.get(8));
				final boolean active = ACTIVE_STATUS.equals(record.get(2));
				final int group = Integer.parseInt(record.get(6));
				final boolean destinationNegated = false;
				final long moduleConceptId = Long.parseLong(record.get(3));
				final boolean inferred = Concepts.INFERRED_RELATIONSHIP.equals(record.get(8));
				final boolean universal = Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(record.get(9));
				
				final long effectiveTime = getEffectiveTime(record);
				final boolean released = isReleased(effectiveTime);

				final int unionGroup;
				if (Concepts.HAS_ACTIVE_INGREDIENT.equals(record.get(7)) && universal) {
					unionGroup = 1;
				} else {
					unionGroup = 0;
				}
				
				// Create relationship document
				final Document doc = SnomedMappings.doc()
					.id(sctId)
					.type(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER)
					.storageKey(storageKey)
					.active(active)
					.relationshipType(typeConceptId)
					.relationshipCharacteristicType(characteristicTypeConceptSctId)
					.module(moduleConceptId)
					.effectiveTime(effectiveTime)
					.relationshipSource(sourceConceptId)
					.relationshipDestination(destinationConceptId)
					.released(released)
					.relationshipGroup(group)
					.relationshipUnionGroup(unionGroup)
					.relationshipDestinationNegated(destinationNegated)
					.relationshipInferred(inferred)
					.relationshipUniversal(universal)
					.build();
				
				snomedIndexService.index(branchPath, doc, storageKey);
			}
		});
	}

	private void indexRefSets(final ComponentImportUnit unit) {
		
		final ComponentImportType type = unit.getType();
		int columnCount = Integer.MIN_VALUE;
		
		switch (type) {
			case LANGUAGE_TYPE_REFSET: columnCount = 7; break;
			case SIMPLE_TYPE_REFSET: columnCount = 6; break;
			case ATTRIBUTE_VALUE_REFSET: columnCount = 7; break;
			case ASSOCIATION_TYPE_REFSET: columnCount = 7; break;
			case SIMPLE_MAP_TYPE_REFSET: columnCount = 7; break;
			case COMPLEX_MAP_TYPE_REFSET: columnCount = 12; break;
			case DESCRIPTION_TYPE_REFSET: columnCount = 8; break;
			case CONCRETE_DOMAIN_REFSET:  columnCount = 9; break;
			case EXTENDED_CONCRETE_DOMAIN_REFSET: columnCount = 11; break;
			case MODULE_DEPENDENCY_REFSET: columnCount = 8; break;
			case EXTENDED_MAP_TYPE_REFSET: columnCount = 13; break;
			case SIMPLE_MAP_TYPE_REFSET_WITH_DESCRIPTION: columnCount = 8; break;
			default: throw new IllegalArgumentException("Unknown reference set type: " + type);
		}
		
		Preconditions.checkState(Integer.MIN_VALUE != columnCount);
		
		parseFile(unit.getUnitFile().getAbsolutePath(), columnCount, new RecordParserCallback<String>() {
			@Override public void handleRecord(final int recordCount, final java.util.List<String> record) { 
				
				final SnomedIndexServerService index = getSnomedIndexService();
				final ImportIndexServerService importIndexService = getImportIndexService();
				
				final String refSetId = record.get(4);
				
				if (skippedReferenceSets.contains(refSetId)) {
					return; //reference set member and all its members has to be excluded from import.
				}
				
				if (!visitedRefSets.containsKey(refSetId)) {
					
					final long storageKey = importIndexService.getRefSetCdoId(refSetId);
					//consider excluded reference sets
					if (CDOUtils.NO_STORAGE_KEY == storageKey) {
						skippedReferenceSets.add(refSetId);
						return;
					}
					
					final SnomedRefSetType refSetType = SnomedRefSetType.get(getTypeOrdinal(type));
					final short refComponentType = getRefSetComponentType(record.get(5), refSetType);
					final SnomedRefSet refSet = new Rf2RefSet(storageKey, refSetId, refSetType, refComponentType);
					final SnomedDocumentBuilder.Factory factory = new SnomedDocumentBuilder.Factory();
					final RefSetMutablePropertyUpdater updater = new RefSetMutablePropertyUpdater(refSet);
					
					final SnomedConceptIndexEntry identifierConcept = ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class).getConcept(branchPath, refSetId);
					if (null == identifierConcept) {
						//might happen that a reference set is an unknown one, hence its identifier concept
						//is being created in this effective time cycle. check concept in CDO
						final ICDOConnection connection = getServiceForClass(ICDOConnectionManager.class).getByUuid(SnomedDatastoreActivator.REPOSITORY_UUID);
						CDOUtils.apply(new CDOTransactionFunction<String>(connection, branchPath) {
							@Override
							protected String apply(final CDOTransaction view) {
								final Concept concept = new SnomedConceptLookupService().getComponent(refSetId, view);
								//we have to index its descriptions, relationships and the concept itself
								identifierConceptIdsForNewRefSets.put(refSetId, SnomedRefSetType.get(getTypeOrdinal(type)));								
								
								final String conceptModuleId = concept.getModule().getId();
								
								final Collection<String> refSetIds = ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class).getContainerRefSetIds(branchPath, refSetId);
								final Collection<String> mappingRefSetIds = ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class).getContainerMappingRefSetIds(branchPath, refSetId);
								final Collection<String> updatedRefSetIds = getCurrentRefSetMemberships(refSetIds, refSetMemberChanges.get(refSetId));
								final Collection<String> updatedMappingRefSetIds = getCurrentRefSetMemberships(mappingRefSetIds, mappingRefSetMemberChanges.get(refSetId));

								final long conceptStorageKey = CDOIDUtils.asLong(concept.cdoID());
								
								final Document conceptDocument = createConceptDocument(
										conceptIdToPredicateMap, 
										Long.parseLong(concept.getId()),
										conceptStorageKey,
										concept.isActive(),
										concept.isReleased(),
										concept.isPrimitive(),
										concept.isExhaustive(), 
										Long.parseLong(conceptModuleId), 
										updatedRefSetIds, 
										updatedMappingRefSetIds, 
										EffectiveTimes.getEffectiveTime(concept.getEffectiveTime()));
								
								updater.update(factory.createBuilder(conceptDocument));
								
								index.index(branchPath, conceptDocument, Mappings.storageKey().toTerm(conceptStorageKey));
								
								for (final Description description : concept.getDescriptions()) {
									index.index(branchPath, new SnomedDescriptionIndexMappingStrategy(description));
									for (final SnomedLanguageRefSetMember languageRefSetMember : description.getLanguageRefSetMembers()) {
										indexRefSetMember(languageRefSetMember);
									}
								}
								
								for (final Relationship relationship : concept.getOutboundRelationships()) {
									index.index(branchPath, new SnomedRelationshipIndexMappingStrategy(relationship));
								}
								
								return conceptModuleId;
							}
						});
					} else {
						
						final List<DocumentUpdaterBase<SnomedDocumentBuilder>> updaters = ImmutableList.<DocumentUpdaterBase<SnomedDocumentBuilder>>builder()
								.add(updater)
								.add(new ComponentCompareFieldsUpdater<SnomedDocumentBuilder>(refSetId, identifierConcept.getStorageKey()))
								.build();
						
						final DocumentCompositeUpdater<SnomedDocumentBuilder> compositeUpdater = new DocumentCompositeUpdater<SnomedDocumentBuilder>(updaters);
						
						getSnomedIndexService().upsert(branchPath, 
								SnomedMappings.newQuery().concept().id(refSetId).matchAll(), 
								compositeUpdater, 
								factory);
					}
					
					visitedRefSets.put(refSetId, refSet);
				}
				
				final SnomedRefSet refSet = visitedRefSets.get(refSetId);
				final String uuid = record.get(0);
				final long memberCdoId = importIndexService.getMemberCdoId(uuid);
				final SnomedRefSetMember member = new Rf2RefSetMember(record, refSet, memberCdoId);
				
				indexRefSetMember(member);
			}

			private short getRefSetComponentType(final String representativeComponentId, final SnomedRefSetType refSetType) {
				if (refSetType == SnomedRefSetType.CONCRETE_DATA_TYPE) {
					// Concrete domain reference sets can have both concepts and relationships as referenced components
					return CoreTerminologyBroker.UNSPECIFIED_NUMBER;
				} else {
					return SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(representativeComponentId); 
				}
			}
			
			private int getTypeOrdinal(final ComponentImportType type) {
				switch (type) {
					case LANGUAGE_TYPE_REFSET: return SnomedRefSetType.LANGUAGE_VALUE;
					case SIMPLE_TYPE_REFSET: return SnomedRefSetType.SIMPLE_VALUE;
					case ATTRIBUTE_VALUE_REFSET: return SnomedRefSetType.ATTRIBUTE_VALUE_VALUE;
					case ASSOCIATION_TYPE_REFSET: return SnomedRefSetType.ASSOCIATION_VALUE;
					case SIMPLE_MAP_TYPE_REFSET: return SnomedRefSetType.SIMPLE_MAP_VALUE;
					case COMPLEX_MAP_TYPE_REFSET: return SnomedRefSetType.COMPLEX_MAP_VALUE;
					case CONCRETE_DOMAIN_REFSET: //$FALL-THROUGH$
					case EXTENDED_CONCRETE_DOMAIN_REFSET: return SnomedRefSetType.CONCRETE_DATA_TYPE_VALUE;
					case MODULE_DEPENDENCY_REFSET: return SnomedRefSetType.MODULE_DEPENDENCY_VALUE;
					case DESCRIPTION_TYPE_REFSET: return SnomedRefSetType.DESCRIPTION_TYPE_VALUE;
					case QUERY_TYPE_REFSET: return SnomedRefSetType.QUERY_VALUE;
					case EXTENDED_MAP_TYPE_REFSET: return SnomedRefSetType.EXTENDED_MAP_VALUE;
					case SIMPLE_MAP_TYPE_REFSET_WITH_DESCRIPTION: return SnomedRefSetType.SIMPLE_MAP_VALUE;
					default:  throw new IllegalArgumentException("Unknown type: " + type);
				}
			}
		});
	}
	
	private void indexRefSetMember(final SnomedRefSetMember member) {
		final Document doc = SnomedMappings.doc()
			.with(new RefSetMemberImmutablePropertyUpdater(member))
			.with(new RefSetMemberMutablePropertyUpdater(member))
			.build();
		
		getSnomedIndexService().index(branchPath, doc, CDOIDUtil.getLong(member.cdoID()));
	}

	private void indexDescriptions(final String absolutePath) {
		
		final SnomedIndexServerService snomedIndexService = getSnomedIndexService();
		
		parseFile(absolutePath, 9, new RecordParserCallback<String>() {
			@Override
			public void handleRecord(final int recordCount, final java.util.List<String> record) { 
				
				final long storageKey = getImportIndexService().getComponentCdoId(record.get(0));

				final String descriptionId = record.get(0);
				final boolean active = ACTIVE_STATUS.equals(record.get(2));
				final long moduleId = Long.parseLong(record.get(3));
				final long containerConceptId = Long.parseLong(record.get(4));
				final String languageCode = record.get(5);
				final long typeId = Long.parseLong(record.get(6));
				final String term = record.get(7);
				final long caseSignificanceId = Long.parseLong(record.get(8));
				
				final long effectiveTime = getEffectiveTime(record);
				final boolean released = isReleased(effectiveTime);

				// Mark description changes received via RF2 as a relevant change on the concept
				if (!conceptsInImportFile.contains(record.get(4))) {
					conceptsWithCompareUniqueKeyChanges.add(record.get(4));
				}
				
				final SnomedDescriptionIndexEntry description = new SnomedDescriptionLookupService().getComponent(branchPath, descriptionId);
				final Multimap<Acceptability, String> invertedAcceptabilityMap = ArrayListMultimap.create();
				
				if (description != null) {
					Multimaps.invertFrom(Multimaps.forMap(description.getAcceptabilityMap()), invertedAcceptabilityMap);				
				}
				
				final Collection<String> preferredRefSetIds = invertedAcceptabilityMap.get(Acceptability.PREFERRED);
				final Collection<String> acceptableRefSetIds = invertedAcceptabilityMap.get(Acceptability.ACCEPTABLE);
				
				final Collection<String> updatedPreferredRefSetIds = getCurrentRefSetMemberships(preferredRefSetIds, preferredMemberChanges.get(descriptionId));
				final Collection<String> updatedAcceptableRefSetIds = getCurrentRefSetMemberships(acceptableRefSetIds, acceptableMemberChanges.get(descriptionId));

				// Create description document.
				final SnomedDocumentBuilder builder = SnomedMappings.doc()
						.id(descriptionId)
						.storageKey(storageKey)
						.type(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER)
						.descriptionTerm(term)
						.descriptionLanguageCode(languageCode)
						.active(active)
						.descriptionType(typeId)
						.descriptionConcept(containerConceptId)
						.module(moduleId)
						.descriptionCaseSignificance(caseSignificanceId)
						.released(released)
						.effectiveTime(effectiveTime);

				for (String preferredRefSetId : updatedPreferredRefSetIds) {
					builder.descriptionPreferredReferenceSetId(Long.valueOf(preferredRefSetId));
				}
				
				for (String acceptableRefSetId : updatedAcceptableRefSetIds) {
					builder.descriptionAcceptableReferenceSetId(Long.valueOf(acceptableRefSetId));
				}
				
				snomedIndexService.index(branchPath, builder.build(), storageKey);
				descriptionsInImportFile.add(descriptionId);
			}
		});
	}
	
	private void indexUnvisitedConcepts(final Set<String> unvisitedConcepts, final Set<String> dirtyConceptsForCompareReindex) {
		
		final SnomedIndexServerService snomedIndexService = getSnomedIndexService();
		
		Collection<SnomedConceptIndexEntry> concepts = ApplicationContext.getServiceForClass(SnomedTerminologyBrowser.class).getConcepts(branchPath, unvisitedConcepts);
		
		for (SnomedConceptIndexEntry concept : concepts) {
			
			String conceptIdString = concept.getId();

			List<DocumentUpdater<SnomedDocumentBuilder>> updaters = newArrayList();
			
			Set<RefSetMemberChange> allRefsetMemberChanges = newHashSet();
			
			allRefsetMemberChanges.addAll(refSetMemberChanges.get(conceptIdString));
			allRefsetMemberChanges.addAll(mappingRefSetMemberChanges.get(conceptIdString));
			
			updaters.add(new RefSetIconIdUpdater(inferredTaxonomyBuilder, statedTaxonomyBuilder, conceptIdString, concept.isActive(), AVAILABLE_ICON_IDS, identifierConceptIdsForNewRefSets));
			updaters.add(new ReferenceSetMembershipUpdater(conceptIdString, allRefsetMemberChanges));
			updaters.add(new ComponentConstraintUpdater(conceptIdString, conceptIdToPredicateMap.get(Long.valueOf(conceptIdString))));
			updaters.add(new RefSetParentageUpdater(inferredTaxonomyBuilder, conceptIdString, identifierConceptIdsForNewRefSets));
			updaters.add(new RefSetParentageUpdater(statedTaxonomyBuilder, conceptIdString, identifierConceptIdsForNewRefSets, Concepts.STATED_RELATIONSHIP));
			updaters.add(new ComponentCompareFieldsUpdater<SnomedDocumentBuilder>(conceptIdString, concept.getStorageKey()));
			
			snomedIndexService.upsert(branchPath, SnomedMappings.newQuery().id(conceptIdString).matchAll(), new DocumentCompositeUpdater<>(updaters), new SnomedDocumentBuilder.Factory());
			
		}
		
	}
	
	private void indexUnvisitedDescriptions(final Set<String> unvisitedDescriptions) {
		final SnomedIndexServerService snomedIndexService = getSnomedIndexService();
		for (final String descriptionIdString : unvisitedDescriptions) {
			final SnomedDescriptionIndexEntry description = new SnomedDescriptionLookupService().getComponent(branchPath, descriptionIdString);
			if (description != null) {
				
				final Multimap<Acceptability, String> invertedAcceptabilityMap = ArrayListMultimap.create();
				Multimaps.invertFrom(Multimaps.forMap(description.getAcceptabilityMap()), invertedAcceptabilityMap);
				
				final Collection<String> preferredRefSetIds = invertedAcceptabilityMap.get(Acceptability.PREFERRED);
				final Collection<String> updatedPreferredRefSetIds = getCurrentRefSetMemberships(preferredRefSetIds, preferredMemberChanges.get(descriptionIdString));
				
				final Collection<String> acceptableRefSetIds = invertedAcceptabilityMap.get(Acceptability.ACCEPTABLE);
				final Collection<String> updatedAcceptableRefSetIds = getCurrentRefSetMemberships(acceptableRefSetIds, acceptableMemberChanges.get(descriptionIdString));
				
				final SnomedDocumentBuilder builder = SnomedMappings.doc()
						.id(description.getId())
						.storageKey(description.getStorageKey())
						.type(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER)
						.descriptionTerm(description.getTerm())
						.descriptionLanguageCode(description.getLanguageCode())
						.active(description.isActive())
						.descriptionType(Long.valueOf(description.getTypeId()))
						.descriptionConcept(Long.valueOf(description.getConceptId()))
						.module(Long.valueOf(description.getModuleId()))
						.descriptionCaseSignificance(Long.valueOf(description.getCaseSignificance()))
						.released(description.isReleased())
						.effectiveTime(description.getEffectiveTimeAsLong());
				
				for (String preferredRefSetId : updatedPreferredRefSetIds) {
					builder.descriptionPreferredReferenceSetId(Long.valueOf(preferredRefSetId));
				}
				
				for (String acceptableRefSetId : updatedAcceptableRefSetIds) {
					builder.descriptionAcceptableReferenceSetId(Long.valueOf(acceptableRefSetId));
				}
				
				snomedIndexService.index(branchPath, builder.build(), description.getStorageKey());
			}
		}
	}
	
	private void indexConcepts(final String absolutePath) {
		final SnomedIndexServerService snomedIndexService = getSnomedIndexService();
		parseFile(absolutePath, 5, new RecordParserCallback<String>() {

			@Override
			public void handleRecord(final int recordCount, final List<String> record) {
				
				final String conceptIdString = record.get(0);
				conceptsInImportFile.add(conceptIdString);

				final long conceptIdLong = Long.parseLong(conceptIdString);
				
				final long conceptStorageKey = getImportIndexService().getComponentCdoId(conceptIdString);
				final boolean active = ACTIVE_STATUS.equals(record.get(2)); 
				final boolean primitive = isPrimitiveConcept(conceptIdLong, Long.parseLong(record.get(4)));
				final boolean exhaustive = false;
				final long moduleId = Long.parseLong(record.get(3));
				
				final Collection<String> refSetIds = ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class).getContainerRefSetIds(branchPath, conceptIdString);
				final Collection<String> mappingRefSetIds = ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class).getContainerMappingRefSetIds(branchPath, conceptIdString);
				final Collection<String> updatedRefSetIds = getCurrentRefSetMemberships(refSetIds, refSetMemberChanges.get(conceptIdString));
				final Collection<String> updatedMappingRefSetIds = getCurrentRefSetMemberships(mappingRefSetIds, mappingRefSetMemberChanges.get(conceptIdString));
				
				final long effectiveTime = getEffectiveTime(record);
				final boolean released = isReleased(effectiveTime);
				
				final Document doc = createConceptDocument(
						conceptIdToPredicateMap, 
						conceptIdLong, 
						conceptStorageKey, 
						active, 
						released, 
						primitive, 
						exhaustive, 
						moduleId, 
						updatedRefSetIds,
						updatedMappingRefSetIds,
						effectiveTime);
				
				snomedIndexService.index(branchPath, doc, conceptStorageKey);
			}
		});
	}
	
	private Document createConceptDocument(final Multimap<Long, String> conceptIdToPredicateMap, final long conceptId, final long conceptStorageKey, final boolean active,
			final boolean released, final boolean primitive, final boolean exhaustive, final long moduleId, final Collection<String> currentRefSetMemberships,
			final Collection<String> currentMappingMemberships, final long effectiveTime) {
		
		final String conceptIdString = Long.toString(conceptId);
		final SnomedDocumentBuilder docBuilder = SnomedMappings.doc()
				.id(conceptId)
				.type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.storageKey(conceptStorageKey)
				.active(active)
				.exhaustive(exhaustive)
				.primitive(primitive)
				.released(released)
				.effectiveTime(effectiveTime)
				.conceptNamespaceId(NamespaceMapping.getExtensionNamespaceId(conceptIdString))
				.module(moduleId)
				.with(new ComponentCompareFieldsUpdater<SnomedDocumentBuilder>(conceptIdString, conceptStorageKey));

		new RefSetIconIdUpdater(inferredTaxonomyBuilder, statedTaxonomyBuilder, conceptIdString, active, AVAILABLE_ICON_IDS, identifierConceptIdsForNewRefSets).update(docBuilder);

		if (conceptIdToPredicateMap.containsKey(conceptId)) {
			final Collection<String> predicateKeys = conceptIdToPredicateMap.get(conceptId);
			for (final String predicateKey : predicateKeys) {
				docBuilder.componentReferringPredicate(predicateKey);
			}
		}
		
		// reference set membership information
		for (final String refSetId : currentRefSetMemberships) {
			docBuilder.conceptReferringRefSetId(Long.parseLong(refSetId));
		}
		for (final String mappingRefSetId : currentMappingMemberships) {
			docBuilder.conceptReferringMappingRefSetId(Long.parseLong(mappingRefSetId));
		}

		// update parents and ancestors
		new RefSetParentageUpdater(inferredTaxonomyBuilder, conceptIdString, identifierConceptIdsForNewRefSets).update(docBuilder);
		new RefSetParentageUpdater(statedTaxonomyBuilder, conceptIdString, identifierConceptIdsForNewRefSets, Concepts.STATED_RELATIONSHIP).update(docBuilder);
			
		if (doiData.containsKey(conceptId)) {
			docBuilder.conceptDegreeOfInterest(doiData.get(conceptId));
		}
		
		return docBuilder.build();
	}
	
	private Collection<String> getCurrentRefSetMemberships(final Collection<String> refSetIds, final Collection<RefSetMemberChange> changes) {
		final Collection<String> refSetMemberships = HashMultiset.create(refSetIds);
		
		for (RefSetMemberChange change : changes) {
			if (change.getChangeKind().equals(MemberChangeKind.REMOVED)) {
				refSetMemberships.remove(Long.toString(change.getRefSetId()));
			}
		}
		
		for (RefSetMemberChange change : changes) {
			if (change.getChangeKind().equals(MemberChangeKind.ADDED)) {
				refSetMemberships.add(Long.toString(change.getRefSetId()));
			}
		}
		
		return refSetMemberships;
	}
	
	private boolean isPrimitiveConcept(final long conceptId, final long definitionStatusConceptId) {
		
		if (definitionStatusConceptId == Long.valueOf(SnomedConstants.Concepts.PRIMITIVE)) {
			return true;
		} else if (definitionStatusConceptId == Long.valueOf(SnomedConstants.Concepts.FULLY_DEFINED)) {
			return false;
		} else {
			throw new IllegalArgumentException(MessageFormat.format("Could not determine if concept is primitive: {0}", conceptId));
		}
	}
	
	private long getEffectiveTime(final List<String> record) {
		return getEffectiveTime(record, 1);
	}

	private long getEffectiveTime(final List<String> record, int index) {
		final String csvEffectiveTime = record.get(index);

		if (csvEffectiveTime.isEmpty()) {
			return EffectiveTimes.UNSET_EFFECTIVE_TIME;
		} else {
			return EffectiveTimes.parse(csvEffectiveTime, DateFormats.SHORT).getTime();
		}
	}
	
	private boolean isReleased(final long effectiveTime) {
		return effectiveTime != EffectiveTimes.UNSET_EFFECTIVE_TIME;
	}
}
