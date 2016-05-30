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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongKeyFloatMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.StringUtils;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.csv.CsvLexer.EOL;
import com.b2international.commons.csv.CsvParser;
import com.b2international.commons.csv.CsvSettings;
import com.b2international.commons.csv.RecordParserCallback;
import com.b2international.commons.functions.LongToStringFunction;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionWriter;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOTransactionFunction;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.server.snomed.index.init.DoiInitializer;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
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
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange.MemberChangeKind;
import com.b2international.snowowl.snomed.datastore.index.update.RefSetIconIdUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.RefSetParentageUpdater;
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
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * RF2 based incremental index initializer job.
 */
public class SnomedRf2IndexInitializer extends Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedRf2IndexInitializer.class);
	private static final CsvSettings CSV_SETTINGS = new CsvSettings('\0', '\t', EOL.LF, true);
	private static final String ACTIVE_STATUS = "1";
	
	private final String effectiveTimeKey;
	private final List<ComponentImportUnit> importUnits;
	private final IBranchPath branchPath;
	private final RevisionWriter writer;

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

	public SnomedRf2IndexInitializer(final RevisionWriter writer, final String lastUnitEffectiveTimeKey, final List<ComponentImportUnit> importUnits, final String languageRefSetId, ISnomedTaxonomyBuilder inferredTaxonomyBuilder, ISnomedTaxonomyBuilder statedTaxonomyBuilder) {
		super("SNOMED CT RF2 based index initializer...");
		this.writer = writer;
		this.branchPath = BranchPathUtils.createPath(writer.branch());
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
		try {
			LOGGER.info("Indexing phase [2 of 3]...");
			doImport(importUnits, delegateMonitor);
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
		
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
					final String refSetId = record.get(4);
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
					final String refSetId = record.get(4);
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
	
	private void doImport(final Iterable<ComponentImportUnit> units, final IProgressMonitor delegateMonitor) throws IOException {
		
		boolean loggedReferenceSetImport = false;
		
		for (final ComponentImportUnit unit : units) {
			
			try {
	
				final String absolutePath = unit.getUnitFile().getAbsolutePath();
				switch (unit.getType()) {
	
					case CONCEPT:
						LOGGER.info("Indexing concepts...");
						indexConcepts(absolutePath);
						writer.commit();
						LOGGER.info("Concepts have been successfully indexed.");
						break;
					case DESCRIPTION:
					case TEXT_DEFINITION:
						LOGGER.info("Indexing descriptions...");
						indexDescriptions(absolutePath);
						writer.commit();
						LOGGER.info("Descriptions have been successfully indexed.");
						break;
					case RELATIONSHIP:
					case STATED_RELATIONSHIP:
						LOGGER.info("Indexing relationships...");
						indexRelationships(absolutePath);
						writer.commit();
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
						writer.commit();
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
		unvisitedConcepts.addAll(LongToStringFunction.copyOf(conceptIdToPredicateMap.keySet()));
		
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
			writer.commit();
		}
		
		LOGGER.info("Post-processing phase successfully finished.");
	}

	private Set<String> getAllDescendants(final Set<String> union) {
		final LongSet $ = PrimitiveSets.newLongOpenHashSet();
		for (final String conceptId : union) {
			if (inferredTaxonomyBuilder.containsNode(conceptId)) { //inactive one
				$.addAll(inferredTaxonomyBuilder.getAllDescendantNodeIds(conceptId));
			}
		}
		return LongSets.toStringSet($);
	}

	private void indexRelationships(final String absolutePath) {
		
		parseFile(absolutePath, 10, new RecordParserCallback<String>() {
			@Override
			public void handleRecord(final int recordCount, final java.util.List<String> record) { 
				
				final String sctId = record.get(0);
				final long storageKey = getImportIndexService().getComponentCdoId(sctId);
				final boolean active = ACTIVE_STATUS.equals(record.get(2));
				final String moduleId = record.get(3);
				final String sourceId = record.get(4);
				final String destinationId = record.get(5);
				final int group = Integer.parseInt(record.get(6));
				final String typeId = record.get(7);
				final String characteristicTypeId = record.get(8);
				final String modifierId = record.get(9);
				final long effectiveTime = getEffectiveTime(record);
				final boolean released = isReleased(effectiveTime);
				final int unionGroup = Concepts.HAS_ACTIVE_INGREDIENT.equals(typeId) && Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(modifierId) ? 1 : 0;
				
				//track concept taxonomy changes even if the concept is not among the changed concepts 
				//but either its source or destination has changed.
				//destination concept changes are intentionally ignored, as taxonomy information is propagated from top to bottom.
				if (!conceptsInImportFile.contains(sourceId)) {
					if (Concepts.IS_A.equals(typeId)) {
						conceptsWithTaxonomyChanges.add(sourceId);
					}
					// The concept will also need a compare unique key update
					conceptsWithCompareUniqueKeyChanges.add(sourceId);
				}

				final SnomedRelationshipIndexEntry entry = SnomedRelationshipIndexEntry.builder()
						.id(sctId)
						.active(active)
						.released(released)
						.effectiveTime(effectiveTime)
						.moduleId(moduleId)
						.sourceId(sourceId)
						.destinationId(destinationId)
						.characteristicTypeId(characteristicTypeId)
						.typeId(typeId)
						.destinationNegated(false) // XXX no RF2 information about this field
						.group(group)
						.unionGroup(unionGroup)
						.modifierId(modifierId)
						.build();
				indexDocument(storageKey, entry);
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
					
					final SnomedConceptDocument identifierConcept = ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class).getConcept(branchPath, refSetId);
					if (null == identifierConcept) {
						//might happen that a reference set is an unknown one, hence its identifier concept
						//is being created in this effective time cycle. check concept in CDO
						final ICDOConnection connection = getServiceForClass(ICDOConnectionManager.class).getByUuid(SnomedDatastoreActivator.REPOSITORY_UUID);
						CDOUtils.apply(new CDOTransactionFunction<String>(connection, branchPath) {
							@Override
							protected String apply(final CDOTransaction view) {
								final Concept concept = new SnomedConceptLookupService().getComponent(refSetId, view);
								// we have to index its descriptions, relationships and the concept itself
								identifierConceptIdsForNewRefSets.put(refSetId, refSetType);								
								
								final Collection<String> refSetIds = newArrayList();
								final Collection<String> updatedRefSetIds = getCurrentRefSetMemberships(refSetIds, refSetMemberChanges.get(refSetId));
								final Collection<String> mappingRefSetIds = newArrayList();
								final Collection<String> updatedMappingRefSetIds = getCurrentRefSetMemberships(mappingRefSetIds, mappingRefSetMemberChanges.get(refSetId));

								final long conceptStorageKey = CDOIDUtils.asLong(concept.cdoID());
								
								final String conceptModuleId = concept.getModule().getId();
								final SnomedConceptDocument.Builder conceptDocument = createConceptDocument(
										conceptIdToPredicateMap, 
										concept.getId(),
										concept.isActive(),
										concept.isReleased(),
										concept.getDefinitionStatus().getId(),
										concept.isExhaustive(), 
										conceptModuleId, 
										updatedRefSetIds, 
										updatedMappingRefSetIds, 
										EffectiveTimes.getEffectiveTime(concept.getEffectiveTime()));
								conceptDocument.refSet(refSet);
								
								indexDocument(conceptStorageKey, conceptDocument.build());
								
								for (final Description description : concept.getDescriptions()) {
									final SnomedDescriptionIndexEntry descriptionEntry = SnomedDescriptionIndexEntry.builder(description).build();
									indexDocument(CDOIDUtil.getLong(description.cdoID()), descriptionEntry);
									for (final SnomedLanguageRefSetMember languageRefSetMember : description.getLanguageRefSetMembers()) {
										indexRefSetMember(languageRefSetMember);
									}
								}
								
								for (final Relationship relationship : concept.getOutboundRelationships()) {
									indexDocument(CDOIDUtil.getLong(relationship.cdoID()), SnomedRelationshipIndexEntry.builder(relationship).build());
								}
								
								return conceptModuleId;
							}
						});
					} else {
						// TODO add compare key
						final SnomedConceptDocument updatedConcept = SnomedConceptDocument.builder(identifierConcept).refSet(refSet).build();
						indexDocument(identifierConcept.getStorageKey(), updatedConcept);
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
		indexDocument(CDOIDUtil.getLong(member.cdoID()), SnomedRefSetMemberIndexEntry.builder(member).build());
	}
	
	private void indexDocument(final long storageKey, final Revision revision) {
		try {
			writer.put(storageKey, revision);
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}
	
	protected void updateIconId(String conceptId, boolean active, SnomedConceptDocument.Builder doc, boolean withDocValues) {
		final Collection<String> availableImages = SnomedIconProvider.getInstance().getAvailableIconIds();
		new RefSetIconIdUpdater(inferredTaxonomyBuilder, statedTaxonomyBuilder, availableImages, identifierConceptIdsForNewRefSets).update(conceptId, active, doc);		
	}

	private void indexDescriptions(final String absolutePath) {
		parseFile(absolutePath, 9, new RecordParserCallback<String>() {
			@Override
			public void handleRecord(final int recordCount, final java.util.List<String> record) { 
				
				final String descriptionId = record.get(0);
				final long storageKey = getImportIndexService().getComponentCdoId(descriptionId);
				final boolean active = ACTIVE_STATUS.equals(record.get(2));
				final String moduleId = record.get(3);
				final String conceptId = record.get(4);
				final String languageCode = record.get(5);
				final String typeId = record.get(6);
				final String term = record.get(7);
				final String caseSignificanceId = record.get(8);
				final long effectiveTime = getEffectiveTime(record);
				final boolean released = isReleased(effectiveTime);

				// Mark description changes received via RF2 as a relevant change on the concept
				if (!conceptsInImportFile.contains(conceptId)) {
					conceptsWithCompareUniqueKeyChanges.add(conceptId);
				}
				
				final SnomedDescriptionIndexEntry description = new SnomedDescriptionLookupService().getComponent(branchPath, descriptionId);
				final Multimap<Acceptability, String> invertedAcceptabilityMap;
				
				if (description != null) {
					invertedAcceptabilityMap = ImmutableMap.copyOf(description.getAcceptabilityMap()).asMultimap().inverse();
				} else {
					invertedAcceptabilityMap = ImmutableMultimap.of();
				}
				
				final Collection<String> preferredRefSetIds = invertedAcceptabilityMap.get(Acceptability.PREFERRED);
				final Collection<String> acceptableRefSetIds = invertedAcceptabilityMap.get(Acceptability.ACCEPTABLE);
				
				final Collection<String> updatedPreferredRefSetIds = getCurrentRefSetMemberships(preferredRefSetIds, preferredMemberChanges.get(descriptionId));
				final Collection<String> updatedAcceptableRefSetIds = getCurrentRefSetMemberships(acceptableRefSetIds, acceptableMemberChanges.get(descriptionId));

				// Create description document.
				final SnomedDescriptionIndexEntry.Builder descriptionEntry = SnomedDescriptionIndexEntry.builder()
						.id(descriptionId)
						.active(active)
						.effectiveTime(effectiveTime)
						.released(released)
						.moduleId(moduleId)
						.conceptId(conceptId)
						.term(term)
						.typeId(typeId)
						.languageCode(languageCode)
						.caseSignificanceId(caseSignificanceId);
				
				for (String preferredRefSetId : updatedPreferredRefSetIds) {
					descriptionEntry.acceptability(preferredRefSetId, Acceptability.PREFERRED);
				}
				
				for (String acceptableRefSetId : updatedAcceptableRefSetIds) {
					descriptionEntry.acceptability(acceptableRefSetId, Acceptability.ACCEPTABLE);
				}
				
				indexDocument(storageKey, descriptionEntry.build());
				descriptionsInImportFile.add(descriptionId);
			}
		});
	}
	
	private void indexUnvisitedConcepts(final Set<String> unvisitedConcepts, final Set<String> dirtyConceptsForCompareReindex) {
		for (final SnomedConceptDocument concept : ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class).getConcepts(branchPath, unvisitedConcepts)) {
			// can happen as concepts referenced in MRCM rules might not exist at this time
			final String conceptId = concept.getId();
			final long conceptStorageKey = concept.getStorageKey();
			
			final Collection<String> refSetIds = concept.getReferringRefSets();
			final Collection<String> updatedRefSetIds = getCurrentRefSetMemberships(refSetIds, refSetMemberChanges.get(conceptId));
			
			final Collection<String> mappingRefSetIds = concept.getReferringMappingRefSets();
			final Collection<String> updatedMappingRefSetIds = getCurrentRefSetMemberships(mappingRefSetIds, mappingRefSetMemberChanges.get(conceptId));

			final SnomedConceptDocument.Builder doc = createConceptDocument(
					conceptIdToPredicateMap, 
					conceptId, 
					concept.isActive(), 
					concept.isReleased(), 
					concept.isPrimitive() ? Concepts.PRIMITIVE : Concepts.FULLY_DEFINED, 
					concept.isExhaustive(), 
					concept.getModuleId(), 
					updatedRefSetIds,
					updatedMappingRefSetIds,
					concept.getEffectiveTime());
			
			indexDocument(conceptStorageKey, doc.build());
		}
	}
	
	private void indexUnvisitedDescriptions(final Set<String> unvisitedDescriptions) {
		// TODO fetch description in one query
		for (final String descriptionIdString : unvisitedDescriptions) {
			final SnomedDescriptionIndexEntry description = new SnomedDescriptionLookupService().getComponent(branchPath, descriptionIdString);
			if (description != null) {
				
				final Multimap<Acceptability, String> invertedAcceptabilityMap = ImmutableMap.copyOf(description.getAcceptabilityMap()).asMultimap().inverse();
				
				final Collection<String> preferredRefSetIds = invertedAcceptabilityMap.get(Acceptability.PREFERRED);
				final Collection<String> updatedPreferredRefSetIds = getCurrentRefSetMemberships(preferredRefSetIds, preferredMemberChanges.get(descriptionIdString));
				
				final Collection<String> acceptableRefSetIds = invertedAcceptabilityMap.get(Acceptability.ACCEPTABLE);
				final Collection<String> updatedAcceptableRefSetIds = getCurrentRefSetMemberships(acceptableRefSetIds, acceptableMemberChanges.get(descriptionIdString));
				
				final SnomedDescriptionIndexEntry.Builder doc = SnomedDescriptionIndexEntry.builder()
						.id(description.getId())
						.active(description.isActive())
						.effectiveTime(description.getEffectiveTime())
						.released(description.isReleased())
						.moduleId(description.getModuleId())
						.conceptId(description.getConceptId())
						.term(description.getTerm())
						.languageCode(description.getLanguageCode())
						.typeId(description.getTypeId())
						.caseSignificanceId(description.getCaseSignificanceId());
				
				for (String preferredRefSetId : updatedPreferredRefSetIds) {
					doc.acceptability(preferredRefSetId, Acceptability.PREFERRED);
				}
				
				for (String acceptableRefSetId : updatedAcceptableRefSetIds) {
					doc.acceptability(acceptableRefSetId, Acceptability.ACCEPTABLE);
				}
				
				indexDocument(description.getStorageKey(), doc.build());
			}
		}
	}
	
	private void indexConcepts(final String absolutePath) {
		parseFile(absolutePath, 5, new RecordParserCallback<String>() {

			@Override
			public void handleRecord(final int recordCount, final List<String> record) {
				final String conceptId = record.get(0);
				conceptsInImportFile.add(conceptId);

				final long conceptStorageKey = getImportIndexService().getComponentCdoId(conceptId);
				final boolean active = ACTIVE_STATUS.equals(record.get(2)); 
				final String definitionStatusId = record.get(4);
				final boolean exhaustive = false;
				final String moduleId = record.get(3);
				
				final Collection<String> refSetIds = currentRevision.getReferringRefSets();
				final Collection<String> updatedRefSetIds = getCurrentRefSetMemberships(refSetIds, refSetMemberChanges.get(conceptId));
				
				final Collection<String> mappingRefSetIds = currentRevision.getReferringMappingRefSets();
				final Collection<String> updatedMappingRefSetIds = getCurrentRefSetMemberships(mappingRefSetIds, mappingRefSetMemberChanges.get(conceptId));
				
				final long effectiveTime = getEffectiveTime(record);
				final boolean released = isReleased(effectiveTime);
				
				final SnomedConceptDocument.Builder doc = createConceptDocument(
						conceptIdToPredicateMap, 
						conceptId, 
						active, 
						released, 
						definitionStatusId, 
						exhaustive, 
						moduleId, 
						updatedRefSetIds,
						updatedMappingRefSetIds,
						effectiveTime);
				
				indexDocument(conceptStorageKey, doc.build());
			}
		});
	}
	
	private SnomedConceptDocument.Builder createConceptDocument(final Multimap<Long, String> conceptIdToPredicateMap, final String conceptId, final boolean active,
			final boolean released, final String definitionStatusId, final boolean exhaustive, final String moduleId, final Collection<String> currentRefSetMemberships,
			final Collection<String> currentMappingMemberships, final long effectiveTime) {
		
		final long conceptIdLong = Long.parseLong(conceptId);
		// TODO add compare key
		final SnomedConceptDocument.Builder builder = SnomedConceptDocument.builder()
				.id(conceptId)
				.active(active)
				.released(released)
				.effectiveTime(effectiveTime)
				.moduleId(moduleId)
				.primitive(Concepts.PRIMITIVE.equals(definitionStatusId))
				.exhaustive(exhaustive)
				.doi(doiData.containsKey(conceptIdLong) ? doiData.get(conceptIdLong) : SnomedConceptDocument.DEFAULT_DOI)
				.referringRefSets(currentRefSetMemberships)
				.referringMappingRefSets(currentMappingMemberships)
				.predicates(conceptIdToPredicateMap.get(Long.valueOf(conceptId)));

		updateIconId(conceptId, active, builder, true);

		// update parents and ancestors
		new RefSetParentageUpdater(inferredTaxonomyBuilder, identifierConceptIdsForNewRefSets, false).update(conceptId, builder);
		new RefSetParentageUpdater(statedTaxonomyBuilder, identifierConceptIdsForNewRefSets, true).update(conceptId, builder);
			
		return builder;
	}
	
	private Collection<String> getCurrentRefSetMemberships(final Collection<String> refSetIds, final Collection<RefSetMemberChange> changes) {
		final Collection<String> refSetMemberships = Sets.newHashSet(refSetIds);

		for (RefSetMemberChange change : changes) {
			if (change.getChangeKind().equals(MemberChangeKind.REMOVED)) {
				refSetMemberships.remove(change.getRefSetId());
			}
		}
		
		for (RefSetMemberChange change : changes) {
			if (change.getChangeKind().equals(MemberChangeKind.ADDED)) {
				refSetMemberships.add(change.getRefSetId());
			}
		}
		
		return refSetMemberships;
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
