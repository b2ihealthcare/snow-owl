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

import static com.b2international.commons.pcj.LongSets.forEach;
import static com.b2international.commons.pcj.LongSets.newLongSet;
import static com.b2international.commons.pcj.LongSets.toStringList;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_COMPARE_UNIQUE_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ICON_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_IGNORE_COMPARE_UNIQUE_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_STORAGE_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_ANCESTOR;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_DEGREE_OF_INTEREST;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_EXHAUSTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_NAMESPACE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_OTHER_DESCRIPTION;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_PARENT;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_PRIMITIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_SYNONYM;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_CASE_SIGNIFICANCE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_CONCEPT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CORRELATION_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DATA_TYPE_VALUE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_ADVICE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_CATEGORY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_PRIORITY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_RULE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION_SORT_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_OPERATOR_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SERIALIZED_VALUE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UOM_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_VALUE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_REFERENCED_COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_STRUCTURAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_CHARACTERISTIC_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_DESTINATION_NEGATED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_INFERRED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNION_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.ROOT_ID;
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

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.csv.CsvLexer.EOL;
import com.b2international.commons.csv.CsvParser;
import com.b2international.commons.csv.CsvSettings;
import com.b2international.commons.csv.RecordParserCallback;
import com.b2international.commons.pcj.LongCollections;
import com.b2international.commons.pcj.LongSets;
import com.b2international.commons.pcj.LongSets.LongCollectionProcedure;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOTransactionFunction;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.CDOViewFunction;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.SortKeyMode;
import com.b2international.snowowl.datastore.server.snomed.index.NamespaceMapping;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.datastore.server.snomed.index.init.DoiInitializer;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService.TermType;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService.TermWithType;
import com.b2international.snowowl.datastore.server.snomed.index.init.MrcmIndexInitializer;
import com.b2international.snowowl.datastore.server.snomed.index.init.Rf2BasedSnomedTaxonomyBuilder;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedRelationshipIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.SnomedBranchRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportUnit;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import bak.pcj.LongCollection;
import bak.pcj.LongIterator;
import bak.pcj.map.LongKeyFloatMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

/**
 * RF2 based incremental index initializer job.
 */
public class SnomedRf2IndexInitializer extends Job {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SnomedRf2IndexInitializer.class);
	
	private static final CsvSettings CSV_SETTINGS = new CsvSettings('\0', '\t', EOL.LF, true);
	private static final String ACTIVE_STATUS = "1";
	private static final float DEFAULT_DOI = 1.0F;
	private final String effectiveTimeKey;
	private final List<ComponentImportUnit> importUnits;
	private final IBranchPath branchPath;

	private Multimap<Long, String> conceptIdToPredicateMap;
	private Multimap<Long, String> refSetIdToPredicateMap;
	private Multimap<String, String> newRefSetMemberships;
	private Multimap<String, String> newMappingMemberships;
	private Multimap<String, String> detachedRefSetMemberships;
	private Multimap<String, String> detachedMappingMemberships;
	private Set<String> visitedConcepts;
	private Set<String> visitedMembers;
	private Set<String> visitedConceptsViaMemberships;
	private Set<String> visitedConceptsViaLanguageMemberships;
	private Set<String> visitedConceptsViaIsAStatements;
	private Set<String> visitedConceptsViaOtherStatements;
	private Map<String, String> descriptiptionsToConceptIds;
	private Map<String, SnomedRefSetType> visitedRefSets;
	private Set<String> skippedReferenceSets;
	private LongKeyFloatMap doiData;
	//when a reference set is imported where the concept is being created on the fly
	private final Map<String, SnomedRefSetType> identifierConceptIdsForNewRefSets = newHashMap();

	public SnomedRf2IndexInitializer(final IBranchPath branchPath, final String lastUnitEffectiveTimeKey, final List<ComponentImportUnit> importUnits, final String languageRefSetId) {
		super("SNOMED CT RF2 based index initializer...");
		this.branchPath = branchPath;
		this.effectiveTimeKey = lastUnitEffectiveTimeKey;
		this.importUnits = Collections.unmodifiableList(importUnits);
		//check services
		getImportIndexService();
		getTaxonomyBuilder();
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(final IProgressMonitor monitor) {

		final IProgressMonitor delegateMonitor = null == monitor ? new NullProgressMonitor() : monitor;
		final List<ComponentImportUnit> importUnits = collectImportUnits();
		
		delegateMonitor.beginTask("Indexing SNOMED CT...", importUnits.size() + 3);
		
		LOGGER.info("Initializing SNOMED CT semantic content from RF2 release format for key '{}'...", effectiveTimeKey);
		LOGGER.info("Pre-processing phase [1 of 3]...");
		
		doiData = new DoiInitializer().run(delegateMonitor);
		
		LOGGER.info("Collecting MRCM rule related changes...");
		final MrcmIndexInitializer mrcmIndexInitializer = new MrcmIndexInitializer(getSnomedIndexService(), branchPath);
		LOGGER.info("MRCM rule related changes successfully collected.");
		mrcmIndexInitializer.run(SubMonitor.convert(delegateMonitor, 1));
		conceptIdToPredicateMap = mrcmIndexInitializer.getConceptIdToPredicateMap();
		refSetIdToPredicateMap = mrcmIndexInitializer.getRefSetIdToPredicateMap();
		
		newRefSetMemberships = HashMultimap.create();
		newMappingMemberships = HashMultimap.create();
		detachedRefSetMemberships = HashMultimap.create();
		detachedMappingMemberships = HashMultimap.create();
		visitedConcepts = Sets.newHashSet();
		visitedMembers = Sets.newHashSet();
		visitedConceptsViaMemberships = Sets.newHashSet();
		visitedConceptsViaLanguageMemberships = newHashSet();
		LOGGER.info("Gathering mappings between descriptions and concepts...");
		descriptiptionsToConceptIds = getDescriptionToConceptIds(importUnits);
		LOGGER.info("Description to concept mapping successfully finished.");
		visitedRefSets = Maps.newHashMap();
		visitedConceptsViaIsAStatements = Sets.newHashSet();
		visitedConceptsViaOtherStatements = Sets.newHashSet();
		skippedReferenceSets = Sets.newHashSet();
		
		delegateMonitor.setTaskName("Collecting reference set memberships...");
		collectRefSetMembershipAndMapping(importUnits);
		delegateMonitor.worked(1);
		
		LOGGER.info("Pre-processing phase successfully finished.");
		LOGGER.info("Indexing phase [2 of 3]...");
		doImport(importUnits, delegateMonitor);
		
		LOGGER.info("SNOMED CT semantic content for key '{}' have been successfully initialized.", effectiveTimeKey);
		
		return Status.OK_STATUS;
	}

	private Map<String, String> getDescriptionToConceptIds(final List<ComponentImportUnit> importUnits) {
		
		final Map<String, String> $ = Maps.newHashMap();
		
		for (final ComponentImportUnit unit : importUnits) {
			if (ComponentImportType.DESCRIPTION == unit.getType()) {
				
				parseFile(unit.getUnitFile().getAbsolutePath(), 9, new RecordParserCallback<String>() {

					@Override
					public void handleRecord(final int recordCount, final List<String> record) {
						$.put(record.get(0), record.get(4));
					}
				});
				
			}
		}
		
		return $;
	}


	private void collectRefSetMembershipAndMapping(final List<ComponentImportUnit> importUnits) {
		
		LOGGER.info("Collecting reference set member related changes...");
		
		for (final ComponentImportUnit unit : importUnits) {
			
			switch (unit.getType()) {
				
				case ATTRIBUTE_VALUE_REFSET:
					
					LOGGER.info("Collecting attribute value type reference set member changes.");
					collectMembership(unit, visitedConceptsViaMemberships, newRefSetMemberships, detachedRefSetMemberships);
					break;
				case SIMPLE_MAP_TYPE_REFSET:
					LOGGER.info("Collecting simple map type reference set member changes.");
					collectMembership(unit, visitedConceptsViaMemberships, newMappingMemberships, detachedMappingMemberships);
					break;
				case SIMPLE_TYPE_REFSET:
					LOGGER.info("Collecting simple type reference set member changes.");
					collectMembership(unit, visitedConceptsViaMemberships, newRefSetMemberships, detachedRefSetMemberships);
					break;
				case LANGUAGE_TYPE_REFSET:
					//language reference set membership may vary the PT of a concept :(
					LOGGER.info("Collecting language type reference set member changes.");
					collectLanguageMembership(unit, visitedConceptsViaLanguageMemberships, descriptiptionsToConceptIds);
					break;
					
				default:
					//ignored
				
			}
			
		}
		
		LOGGER.info("Reference set member related changes have been successfully finished.");
		
	}

	private void collectLanguageMembership(final ComponentImportUnit unit, final Set<String> visitedConceptsViaLanguageMemberships, final Map<String, String> descriptiptionsToConceptIds) {
		if (null == unit.getUnitFile()) {
			return;
		}
		parseFile(unit.getUnitFile().getAbsolutePath(), 7/*magic*/, new RecordParserCallback<String>() {

			@Override
			public void handleRecord(final int recordCount, final List<String> record) {
				
				final String descriptionId = record.get(5);
				String conceptId = descriptiptionsToConceptIds.get(descriptionId);
				if (StringUtils.isEmpty(conceptId)) {
					conceptId = ApplicationContext.getInstance().getService(ISnomedComponentService.class).getDescriptionProperties(branchPath, descriptionId)[0];
				}
				
				visitedConceptsViaLanguageMemberships.add(conceptId);
			}
		});
	}


	private void collectMembership(final ComponentImportUnit unit, final Set<String> visitedConceptsViaMemberships, final Multimap<String, String> newMembership, final Multimap<String, String> detachedMemberships) {
		
		int columnCount = Integer.MIN_VALUE;
		
		switch (unit.getType()) {
			
			case ATTRIBUTE_VALUE_REFSET: //$FALL-THROUGH$
			case SIMPLE_MAP_TYPE_REFSET: //$FALL-THROUGH$
			case LANGUAGE_TYPE_REFSET:
				columnCount = 7;
				break;
			case SIMPLE_TYPE_REFSET:
				columnCount = 6;
				break;
			default: throw new IllegalArgumentException("Unknown import type: " + unit.getType());
		}
		
		if (Integer.MIN_VALUE != columnCount) {
			
			final File unitFile = unit.getUnitFile();
			if (null != unitFile && unitFile.canRead() && unitFile.isFile()) {
				
				parseFile(unitFile.getAbsolutePath(), columnCount, new RecordParserCallback<String>() {

					@Override
					public void handleRecord(final int recordCount, final List<String> record) {
						
							final String uuid = record.get(0);
							visitedMembers.add(uuid);
							
							final String refSetId = record.get(4);
							final String id = record.get(5);
							if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER == SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(id)) {
		
								if (ACTIVE_STATUS.equals(record.get(2))) {
									newMembership.put(id, refSetId);
								} else {
									detachedMemberships.put(id, refSetId);
								}
								
								visitedConceptsViaMemberships.add(id);
		
							}
		
						}
					
				});
				
			}		
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
	
	private ISnomedTaxonomyBuilder getTaxonomyBuilder() {
		return Preconditions.checkNotNull(ApplicationContext.getInstance().getService(Rf2BasedSnomedTaxonomyBuilder.class), "RF2 based taxonomy builder service was null");
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
	
					//concept change processing (if any) should happen in the very end
					case CONCEPT:
						LOGGER.info("Indexing concepts...");
						indexConcepts(absolutePath);
						getSnomedIndexService().commit(branchPath);
						LOGGER.info("Concepts have been successfully indexed.");
						break;
					case DESCRIPTION:
						LOGGER.info("Indexing descriptions...");
						indexDescriptions(absolutePath);
						getSnomedIndexService().commit(branchPath);
						LOGGER.info("Descriptions have been successfully indexed.");
						break;
					case RELATIONSHIP:
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
				getSnomedIndexService().commit(branchPath);
				delegateMonitor.worked(1);
			}
			
		}
		LOGGER.info("Reference sets and their members have been successfully indexed.");
		
		LOGGER.info("Indexing phase successfully finished.");
		LOGGER.info("Post-processing phase [3 of 3]...");
		
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Visited concepts via reference set membership count: " + visitedConceptsViaMemberships.size());
			LOGGER.trace("Visited concepts via IS A statement count: " + visitedConceptsViaIsAStatements.size());
			LOGGER.trace("Visited concepts via other statement count: " + visitedConceptsViaOtherStatements.size());
		}
		
		LOGGER.info("Collecting unvisited concepts...");
		
		/**For these IDS component_compare_uniqe_key has to be indexed on the concept document*/
		final Set<String> dirtyConceptsForCompareReindex = Sets.newHashSet(visitedConceptsViaOtherStatements);
		dirtyConceptsForCompareReindex.addAll(visitedConceptsViaIsAStatements);
		dirtyConceptsForCompareReindex.addAll(visitedConceptsViaLanguageMemberships);
		
		final Set<String> union = Sets.newHashSet(visitedConceptsViaMemberships);
		union.addAll(visitedConceptsViaLanguageMemberships);
		union.addAll(visitedConceptsViaIsAStatements);
		
		//add descendant concepts as well, since it requires taxonomy information updates
		final Set<String> statementDescendants = getAllDescendants(visitedConceptsViaIsAStatements);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Statement descendant count: " + statementDescendants.size());
		}
		union.addAll(statementDescendants);
		//add all descendants for visited concepts as well
		final Set<String> visitedDescendants = getAllDescendants(visitedConcepts);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Visited concept descendant count: " + visitedDescendants.size());
			LOGGER.trace("Visited concept count: " + visitedConcepts.size());
		}
		union.addAll(visitedConceptsViaOtherStatements); //this will not cause explicit taxonomy information changes
		union.addAll(visitedDescendants);
		//index MRCM related changed on the concept.
		//See issue: https://snowowl.atlassian.net/browse/SO-1532?focusedCommentId=29572&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-29572
		union.addAll(toStringList(newLongSet(conceptIdToPredicateMap.keySet())));
		
		final Set<String> difference = Sets.newHashSet(Sets.difference(union, visitedConcepts));
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Overall difference count: " + difference.size());
		}

		LOGGER.info("Unvisited concepts have been successfully collected.");

		if (!CompareUtils.isEmpty(difference)) {
			LOGGER.info("Reindexing unvisited concepts...");
			indexUnvisitedConcepts(difference, dirtyConceptsForCompareReindex);
			getSnomedIndexService().commit(branchPath);
			LOGGER.info("Unvisited concepts have been successfully reindexed.");
		} else {
			LOGGER.info("No unvisited concepts have been found.");
		}
		
		if (!CompareUtils.isEmpty(visitedConceptsViaLanguageMemberships)) {
			LOGGER.info("Reindexing reference set members on concepts where the preferred term changed...");
			indexPreferredTermChangesOnMembers();
			getSnomedIndexService().commit(branchPath);
			LOGGER.info("Preferred term changes successfully updated on reference set members.");
		} else {
			LOGGER.info("No preferred term changes have been found.");
		}
		
		LOGGER.info("Post-processing phase successfully finished.");
		
	}

	private Set<String> getAllDescendants(final Set<String> union) {
		final LongSet $ = new LongOpenHashSet();
		for (final String conceptId : union) {
			if (getTaxonomyBuilder().containsNode(conceptId)) { //inactive one
				$.addAll(getTaxonomyBuilder().getAllDescendantNodeIds(conceptId));
			}
		}
		return LongSets.toStringSet($);
	}


	private void indexRelationships(final String absolutePath) {
		
		final SnomedIndexServerService snomedIndexService = getSnomedIndexService();
		
		parseFile(absolutePath, 10, new RecordParserCallback<String>() {
			public void handleRecord(final int recordCount, final java.util.List<String> record) { 
				
				final long storageKey = getImportIndexService().getComponentCdoId(record.get(0));
				final long sctId = Long.parseLong(record.get(0));
				final long sourceConceptId = Long.parseLong(record.get(4));
				final long typeConceptId = Long.parseLong(record.get(7));
				final long destinationConceptId = Long.parseLong(record.get(5));
				
				//track concept taxonomy changes even if the concept is not among the changed concepts 
				//but either its source or destination has changed.
				//destination concept changes are intentionally ignored, as taxonomy information is propagated from top to bottom.
				if (!visitedConcepts.contains(record.get(4))) {
					if (Concepts.IS_A.equals(record.get(7))) {
						visitedConceptsViaIsAStatements.add(record.get(4));
					} else {
						//process non IS_A statements. although it will not cause taxonomy changes, we have to reindex compare unique key
						visitedConceptsViaOtherStatements.add(record.get(4));
					}
				}
				
				
				final long characteristicTypeConceptSctId = Long.parseLong(record.get(8));
				final boolean active = ACTIVE_STATUS.equals(record.get(2));
				final int group = Integer.parseInt(record.get(6));
				final int unionGroup = 0;
				final boolean destinationNegated = false;
				final long moduleConceptId = Long.parseLong(record.get(3));
				final boolean inferred = Concepts.INFERRED_RELATIONSHIP.equals(record.get(8));
				final boolean universal = Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(record.get(9));
				
				final long effectiveTime = getEffectiveTime(record);
				final boolean released = isReleased(effectiveTime);
				
				// Create relationship document
				final Document doc = new Document();
				doc.add(new LongField(COMPONENT_ID, sctId, Store.YES));
				doc.add(new IntField(COMPONENT_TYPE, SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, Store.YES));
				doc.add(new StoredField(COMPONENT_RELEASED, released ? 1 : 0));
				doc.add(new IntField(COMPONENT_ACTIVE, active ? 1 : 0, Store.YES));
				doc.add(new LongField(COMPONENT_STORAGE_KEY, storageKey, Store.YES));
				doc.add(new LongField(RELATIONSHIP_OBJECT_ID, sourceConceptId, Store.YES));
				doc.add(new LongField(RELATIONSHIP_ATTRIBUTE_ID, typeConceptId, Store.YES));
				doc.add(new LongField(RELATIONSHIP_VALUE_ID, destinationConceptId, Store.YES));
				doc.add(new LongField(RELATIONSHIP_CHARACTERISTIC_TYPE_ID, characteristicTypeConceptSctId, Store.YES));
				doc.add(new StoredField(RELATIONSHIP_GROUP, group));
				doc.add(new StoredField(RELATIONSHIP_UNION_GROUP, unionGroup));
				doc.add(new StoredField(RELATIONSHIP_DESTINATION_NEGATED, destinationNegated ? 1 : 0));
				doc.add(new StoredField(RELATIONSHIP_INFERRED, inferred ? 1 : 0));
				doc.add(new StoredField(RELATIONSHIP_UNIVERSAL, universal ? 1 : 0));
				doc.add(new LongField(RELATIONSHIP_MODULE_ID, moduleConceptId, Store.YES));
				doc.add(new LongField(RELATIONSHIP_EFFECTIVE_TIME, effectiveTime, Store.YES));
				
				doc.add(new NumericDocValuesField(COMPONENT_STORAGE_KEY, storageKey));
				doc.add(new NumericDocValuesField(COMPONENT_ID, sctId));
				doc.add(new NumericDocValuesField(RELATIONSHIP_VALUE_ID, destinationConceptId));
				doc.add(new NumericDocValuesField(RELATIONSHIP_OBJECT_ID, sourceConceptId));
				doc.add(new NumericDocValuesField(RELATIONSHIP_ATTRIBUTE_ID, typeConceptId));
				doc.add(new NumericDocValuesField(RELATIONSHIP_CHARACTERISTIC_TYPE_ID, characteristicTypeConceptSctId));
				doc.add(new NumericDocValuesField(RELATIONSHIP_GROUP, group));
				doc.add(new NumericDocValuesField(RELATIONSHIP_UNION_GROUP, unionGroup));
				doc.add(new NumericDocValuesField(RELATIONSHIP_UNIVERSAL, universal ? 1 : 0));
				doc.add(new NumericDocValuesField(RELATIONSHIP_DESTINATION_NEGATED, destinationNegated ? 1 : 0));
				doc.add(new NumericDocValuesField(RELATIONSHIP_MODULE_ID, moduleConceptId));
				
				
				snomedIndexService.index(branchPath, doc, IndexUtils.getStorageKeyTerm(storageKey));
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
				
				final SnomedIndexServerService snomedIndexService = getSnomedIndexService();
				final ImportIndexServerService importIndexService = getImportIndexService();
				
				final String refSetId = record.get(4);
				
				if (skippedReferenceSets.contains(refSetId)) {
					return; //reference set member and all its members has to be excluded from import.
				}
				
				if (!visitedRefSets.containsKey(refSetId)) {
					
					final Document refSetDoc = new Document();
					final int refSetType = getTypeOrdinal(type);
					final Long id = Long.valueOf(refSetId);
					final boolean structural = isStructural(type, refSetId);
					final boolean indexAsRelevantForCompare = !structural;
					final int refComponentType = getRefSetComponentType(record.get(5), refSetType);
					final long storageKey = importIndexService.getRefSetCdoId(refSetId);
					refSetDoc.add(new IntField(COMPONENT_TYPE, SnomedTerminologyComponentConstants.REFSET_NUMBER, Store.YES));
					refSetDoc.add(new LongField(COMPONENT_ID, id, Store.YES));
					refSetDoc.add(new IntField(REFERENCE_SET_TYPE, refSetType, Store.YES));
					refSetDoc.add(new IntField(REFERENCE_SET_REFERENCED_COMPONENT_TYPE, refComponentType, Store.YES));
					refSetDoc.add(new NumericDocValuesField(COMPONENT_COMPARE_UNIQUE_KEY, indexAsRelevantForCompare ? storageKey : CDOUtils.NO_STORAGE_KEY));
					if (!indexAsRelevantForCompare) {
						refSetDoc.add(new NumericDocValuesField(COMPONENT_IGNORE_COMPARE_UNIQUE_KEY, storageKey));
					}
					
					//consider excluded reference sets
					if (CDOUtils.NO_STORAGE_KEY == storageKey) {
						skippedReferenceSets.add(refSetId);
						return;
					}
					
					refSetDoc.add(new LongField(COMPONENT_STORAGE_KEY, storageKey, Store.YES));
					refSetDoc.add(new IntField(REFERENCE_SET_STRUCTURAL, structural ? 1 : 0, Store.YES));
					
					final String label = importIndexService.getConceptLabel(refSetId);
					refSetDoc.add(new TextField(COMPONENT_LABEL, label, Store.YES));
					
					final String moduleId;
					final SnomedConceptIndexEntry identifierConcept = ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class).getConcept(branchPath, refSetId);
					if (null != identifierConcept) {
						moduleId = identifierConcept.getModuleId();
					} else {
						//might happen that a reference set is an unknown one, hence its identifier concept
						//is being created in this effective time cycle. check concept in CDO
						final ICDOConnection connection = getServiceForClass(ICDOConnectionManager.class).getByUuid(SnomedDatastoreActivator.REPOSITORY_UUID);
						moduleId = CDOUtils.apply(new CDOTransactionFunction<String>(connection, branchPath) {
							protected String apply(final CDOTransaction view) {
								final Concept concept = new SnomedConceptLookupService().getComponent(refSetId, view);
								//we have to index its descriptions, relationships and the concept itself
								
								identifierConceptIdsForNewRefSets.put(refSetId, SnomedRefSetType.get(getTypeOrdinal(type)));								
								
								final String conceptModuleId = concept.getModule().getId();
								
								final Collection<String> currentRefSetMemberships = getCurrentRefSetMemberships(refSetId, newRefSetMemberships, detachedRefSetMemberships);
								final Collection<String> currentMappingMemberships = getCurrentMappingMemberships(refSetId, newMappingMemberships, detachedMappingMemberships);
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
										currentRefSetMemberships, 
										currentMappingMemberships, 
										EffectiveTimes.getEffectiveTime(concept.getEffectiveTime()), 
										true);
								
								snomedIndexService.index(branchPath, conceptDocument, IndexUtils.getStorageKeyTerm(conceptStorageKey));
								
								for (final Description description : concept.getDescriptions()) {
									snomedIndexService.index(branchPath, new SnomedDescriptionIndexMappingStrategy(description));
									for (final SnomedLanguageRefSetMember languageRefSetMember : description.getLanguageRefSetMembers()) {
										snomedIndexService.index(branchPath, new SnomedRefSetMemberIndexMappingStrategy(languageRefSetMember));	
									}
								}
								
								for (final Relationship relationship : concept.getOutboundRelationships()) {
									snomedIndexService.index(branchPath, new SnomedRelationshipIndexMappingStrategy(relationship));
								}
								
								return conceptModuleId;
							}
						});
						
					}
					
					refSetDoc.add(new LongField(REFERENCE_SET_MODULE_ID, Long.parseLong(moduleId), Store.YES));
					final String iconId;
					
					if (getTaxonomyBuilder().containsNode(refSetId)) {
						iconId = SnomedIconProvider.getInstance().getIconComponentId(refSetId, getTaxonomyBuilder());
					} else {
						if (identifierConceptIdsForNewRefSets.containsKey(refSetId)) {
							final SnomedRefSetType snomedRefSetType = identifierConceptIdsForNewRefSets.get(refSetId);
							iconId = SnomedRefSetUtil.getConceptId(snomedRefSetType);
						} else {
							iconId = Concepts.ROOT_CONCEPT;
						}
					}
							
					refSetDoc.add(new LongField(COMPONENT_ICON_ID, Long.parseLong(iconId), Store.YES));
					
					for (final String predicateKey : refSetIdToPredicateMap.get(Long.parseLong(refSetId))) {
						refSetDoc.add(new StringField(COMPONENT_REFERRING_PREDICATE, predicateKey, Store.YES));
					}
					
					snomedIndexService.index(branchPath, refSetDoc, IndexUtils.getStorageKeyTerm(storageKey));
					visitedRefSets.put(refSetId, SnomedRefSetType.get(refSetType));
				}
				
				
				final String uuid = record.get(0);
				final boolean active = ACTIVE_STATUS.equals(record.get(2));
				final long module = Long.parseLong(record.get(3));
				final String refComponentId = record.get(5);
				final SnomedRefSetType refSetType = visitedRefSets.get(refSetId);
				final int refComponentType = SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(refComponentId);
				final long memberCdoId = importIndexService.getMemberCdoId(uuid);
				
				final long effectiveTime = getEffectiveTime(record);
				final boolean released = isReleased(effectiveTime);

				String label = null;
				
				if (ComponentImportType.EXTENDED_CONCRETE_DOMAIN_REFSET == type) {
					label = record.get(8);
				}
				
				if (StringUtils.isEmpty(label)) {
				
					if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER == refComponentType
							|| SnomedTerminologyComponentConstants.REFSET_NUMBER == refComponentType) {
						label = importIndexService.getConceptLabel(refComponentId);
					} else if (SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER == refComponentType) {
						label = importIndexService.getDescriptionLabel(refComponentId);
					} else if (SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER == refComponentType) {
						label = importIndexService.getRelationshipLabel(refComponentId);
					} else {
						label = refComponentId;
					}
						
				}
				
				final Document doc = new Document();

				doc.add(new StringField(REFERENCE_SET_MEMBER_UUID, uuid, Store.YES));
				doc.add(new IntField(COMPONENT_ACTIVE, active ? 1 : 0, Store.YES));
				doc.add(new IntField(REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE, refSetType.ordinal(), Store.YES));
				doc.add(new LongField(COMPONENT_STORAGE_KEY, memberCdoId, Store.YES));
				doc.add(new StoredField(COMPONENT_RELEASED, released ? 1 : 0));
				doc.add(new IntField(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_TYPE, refComponentType, Store.YES));
				doc.add(new StringField(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, refComponentId, Store.YES));
				doc.add(new LongField(REFERENCE_SET_MEMBER_MODULE_ID, module, Store.YES));
				doc.add(new LongField(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, Long.valueOf(refSetId), Store.YES));
				doc.add(new LongField(REFERENCE_SET_MEMBER_EFFECTIVE_TIME, effectiveTime, Store.YES));
				doc.add(new TextField(COMPONENT_LABEL, label, Store.YES));
				
				switch (refSetType) {
					
					case SIMPLE: 
						break;
						
					case ASSOCIATION:
						
						doc.add(new StringField(REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID, record.get(6), Store.YES));
						break;
						
					case ATTRIBUTE_VALUE:
						
						doc.add(new StringField(REFERENCE_SET_MEMBER_VALUE_ID, record.get(6), Store.YES));
						break;
						
					case QUERY:
						throw new IllegalStateException();
						
					case EXTENDED_MAP: //$FALL-THROUGH$
					case COMPLEX_MAP:
						//cast member to complex map and set complex map properties to the document
						doc.add(new StoredField(REFERENCE_SET_MEMBER_MAP_GROUP, Byte.parseByte(record.get(6))));
						doc.add(new StoredField(REFERENCE_SET_MEMBER_MAP_PRIORITY, Byte.parseByte(record.get(7))));
						if (!StringUtils.isEmpty(record.get(8))) {
							doc.add(new StringField(REFERENCE_SET_MEMBER_MAP_RULE, record.get(8), Store.YES));
						}
						if (!StringUtils.isEmpty(record.get(9))) {
							doc.add(new StringField(REFERENCE_SET_MEMBER_MAP_ADVICE, record.get(9), Store.YES));
						}
						doc.add(new LongField(REFERENCE_SET_MEMBER_CORRELATION_ID, Long.valueOf(record.get(11)), Store.YES));
						
						final String complexMapTargetComponentId = record.get(10);
						final short complexMapTargetComponentType = CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
						
						doc.add(new StringField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, complexMapTargetComponentId, Store.YES));
						doc.add(new IntField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID, complexMapTargetComponentType, Store.YES));
						doc.add(new StoredField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_LABEL, complexMapTargetComponentId)); //unknown map target
						
						if (record.size() > 12) { //extended map
							final String complexMapCategoryId = record.get(12);
							if (!StringUtils.isEmpty(complexMapCategoryId)) {
								doc.add(new LongField(REFERENCE_SET_MEMBER_MAP_CATEGORY_ID, Long.valueOf(complexMapCategoryId), Store.YES));
							}
						}
						
						break;
						
					case DESCRIPTION_TYPE:
						
						//set description type ID, label and description length
						final String formatId = record.get(6);
						doc.add(new LongField(REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID, Long.parseLong(formatId), Store.YES));
						doc.add(new StoredField(REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH, Integer.parseInt(record.get(7))));
						//description type must be a SNOMED CT concept
						doc.add(new StringField(REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_LABEL, importIndexService.getConceptLabel(formatId), Store.YES));
						break;
						
					case LANGUAGE:
						
						//set description acceptability label and ID
						final long acceptabilityId = Long.parseLong(record.get(6));
						doc.add(new LongField(REFERENCE_SET_MEMBER_ACCEPTABILITY_ID, acceptabilityId, Store.YES));
						//acceptability ID always represents a SNOMED CT concept
						final String acceptabilityLabel = Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(acceptabilityId) ? "Preferred" : "Acceptable";
						doc.add(new StringField(REFERENCE_SET_MEMBER_ACCEPTABILITY_LABEL, acceptabilityLabel, Store.YES));
						break;
						
					case CONCRETE_DATA_TYPE:
						//set operator ID, serialized value, UOM ID (if any) and characteristic type ID
						doc.add(new LongField(REFERENCE_SET_MEMBER_OPERATOR_ID, Long.valueOf(record.get(7)), Store.YES));

						if (ComponentImportType.EXTENDED_CONCRETE_DOMAIN_REFSET == type) {
							doc.add(new StringField(REFERENCE_SET_MEMBER_SERIALIZED_VALUE, record.get(9), Store.YES));
							doc.add(new BinaryDocValuesField(REFERENCE_SET_MEMBER_SERIALIZED_VALUE, new BytesRef(record.get(9))));
						} else if (ComponentImportType.CONCRETE_DOMAIN_REFSET == type) {
							doc.add(new StringField(REFERENCE_SET_MEMBER_SERIALIZED_VALUE, record.get(8), Store.YES));
							doc.add(new BinaryDocValuesField(REFERENCE_SET_MEMBER_SERIALIZED_VALUE, new BytesRef(record.get(8))));
						}
						
						final String uomId = record.get(6);
						
						if (!StringUtils.isEmpty(uomId)) {
							doc.add(new LongField(REFERENCE_SET_MEMBER_UOM_ID, Long.parseLong(uomId), Store.YES));
							doc.add(new NumericDocValuesField(REFERENCE_SET_MEMBER_UOM_ID, Long.parseLong(uomId)));
						}
						
						if (ComponentImportType.EXTENDED_CONCRETE_DOMAIN_REFSET == type) {
							
							final String charTypeId = record.get(10);
							if (!StringUtils.isEmpty(charTypeId)) {
								doc.add(new LongField(REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID, Long.valueOf(charTypeId), Store.YES));
							}
							
						}
						
						final com.b2international.snowowl.snomed.mrcm.DataType dataType = SnomedRefSetUtil.getDataType(refSetId);
						doc.add(new NumericDocValuesField(REFERENCE_SET_MEMBER_DATA_TYPE_VALUE, (byte) dataType.ordinal()));
						
						if (null != label) {
							doc.add(new BinaryDocValuesField(COMPONENT_LABEL, new BytesRef(label)));
							SortKeyMode.SEARCH_ONLY.add(doc, label);
						}
						
						doc.add(new NumericDocValuesField(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, Long.parseLong(refComponentId)));
						doc.add(new NumericDocValuesField(COMPONENT_STORAGE_KEY, memberCdoId));
						doc.add(new NumericDocValuesField(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, Long.parseLong(refSetId)));
						break;
						
					case SIMPLE_MAP:
						//set map target ID, type and label
						final String simpleMapTargetComponentId = record.get(6);
						final short simpleMapTargetComponentType = CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
						
						doc.add(new StringField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, simpleMapTargetComponentId, Store.YES));
						doc.add(new IntField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID, simpleMapTargetComponentType, Store.YES));
						doc.add(new StoredField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_LABEL, simpleMapTargetComponentId)); //unknown map target
						
						if (record.size() > 7) {
							final String componentDescription = record.get(7);
							if (null != componentDescription) {
								doc.add(new TextField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION, componentDescription, Store.YES));
								doc.add(new StringField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION_SORT_KEY, IndexUtils.getSortKey(componentDescription), Store.NO));
							}
						}
						
						break;
						
					case MODULE_DEPENDENCY:
						
						doc.add(new LongField(REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME, getEffectiveTime(record, 6), Store.YES));
						doc.add(new LongField(REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME, getEffectiveTime(record, 7), Store.YES));
						break;
					
					}
				
				snomedIndexService.index(branchPath, doc, IndexUtils.getStorageKeyTerm(memberCdoId));
			}

			private int getRefSetComponentType(final String representativeComponentId, final int refSetType) {
				if (refSetType == SnomedRefSetType.CONCRETE_DATA_TYPE_VALUE) {
					// Concrete domain reference sets can have both concepts and relationships as referenced components
					return CoreTerminologyBroker.UNSPECIFIED_NUMBER;
				} else {
					return SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(representativeComponentId); 
				}
			}
			
			private boolean isStructural(final ComponentImportType type, final String refSetId) {
				switch (type) {
					case LANGUAGE_TYPE_REFSET: //$FALL-THROUGH$
					case EXTENDED_CONCRETE_DOMAIN_REFSET: //$FALL-THROUGH$
					case CONCRETE_DOMAIN_REFSET: //$FALL-THROUGH$
					case ASSOCIATION_TYPE_REFSET: //$FALL-THROUGH$
					case MODULE_DEPENDENCY_REFSET: //$FALL-THROUGH$
						return true;
					case ATTRIBUTE_VALUE_REFSET:
						return 
								Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(refSetId) 
								|| Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR.equals(refSetId) 
								|| Concepts.REFSET_RELATIONSHIP_REFINABILITY.equals(refSetId);
					default: return false;
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
	
	private void indexPreferredTermChangesOnMembers() {
		final SnomedBranchRefSetMembershipLookupService lookupService = new SnomedBranchRefSetMembershipLookupService(branchPath);
		final ICDOConnection connection = ApplicationContext.getServiceForClass(ICDOConnectionManager.class).getByUuid(SnomedDatastoreActivator.REPOSITORY_UUID);
		
		CDOUtils.apply(new CDOViewFunction<Void, CDOView>(connection, branchPath) {
			
			@Override
			protected Void apply(final CDOView view) {
				
				for (final String conceptId : visitedConceptsViaLanguageMemberships) {
		
					final Collection<SnomedRefSetMemberIndexEntry> referringMembers = lookupService.getReferringMembers(conceptId);
					final String conceptLabel = getImportIndexService().getConceptLabel(conceptId);
					final LongSet referringMemberStorageKeys = new LongOpenHashSet();
					
					for (final SnomedRefSetMemberIndexEntry referringMember : referringMembers) {
						if (!visitedMembers.contains(referringMember.getId())) {
							referringMemberStorageKeys.add(referringMember.getStorageKey());
						}
					}
			
					forEach(referringMemberStorageKeys, new LongCollectionProcedure() {
						@Override
						public void apply(final long referringMemberStorageKey) {
							final CDOObject referringMember = CDOUtils.getObjectIfExists(view, referringMemberStorageKey);
							if (referringMember instanceof SnomedConcreteDataTypeRefSetMember) {
								final SnomedConcreteDataTypeRefSetMember cdtMember = (SnomedConcreteDataTypeRefSetMember) referringMember;
								getSnomedIndexService().index(branchPath, new SnomedRefSetMemberIndexMappingStrategy(cdtMember, cdtMember.getLabel()));
							} else if (referringMember instanceof SnomedRefSetMember) {
								getSnomedIndexService().index(branchPath, new SnomedRefSetMemberIndexMappingStrategy((SnomedRefSetMember) referringMember, conceptLabel));
							}
						}
					});
				}
				
				return null;
			}
		});
	}

	private void indexDescriptions(final String absolutePath) {
		
		final SnomedIndexServerService snomedIndexService = getSnomedIndexService();
		
		parseFile(absolutePath, 9, new RecordParserCallback<String>() {
			public void handleRecord(final int recordCount, final java.util.List<String> record) { 
				
				final long storageKey = getImportIndexService().getComponentCdoId(record.get(0));
				final long sctId = Long.parseLong(record.get(0));
				final String term = record.get(7);
				final boolean active = ACTIVE_STATUS.equals(record.get(2));
				final long moduleId = Long.parseLong(record.get(3));
				final long typeId = Long.parseLong(record.get(6));
				final long caseSignificanceId = Long.parseLong(record.get(8));
				final long containerConceptId = Long.parseLong(record.get(4));
				
				final long effectiveTime = getEffectiveTime(record);
				final boolean released = isReleased(effectiveTime);

				// Create description document.
				final Document doc = new Document();
				doc.add(new LongField(COMPONENT_ID, sctId, Store.YES));
				doc.add(new IntField(COMPONENT_TYPE, SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, Store.YES));
				doc.add(new TextField(COMPONENT_LABEL, term, Store.YES));
				SortKeyMode.SEARCH_ONLY.add(doc, term);
				doc.add(new BinaryDocValuesField(COMPONENT_LABEL, new BytesRef(term)));
				doc.add(new IntField(COMPONENT_ACTIVE, active ? 1 : 0, Store.YES));
				doc.add(new LongField(COMPONENT_STORAGE_KEY, storageKey, Store.YES));
				doc.add(new StoredField(DESCRIPTION_CASE_SIGNIFICANCE_ID, caseSignificanceId));
				doc.add(new StoredField(COMPONENT_RELEASED, released ? 1 : 0));
				doc.add(new LongField(DESCRIPTION_TYPE_ID, typeId, Store.YES));
				doc.add(new LongField(DESCRIPTION_CONCEPT_ID, containerConceptId, Store.YES));
				doc.add(new LongField(DESCRIPTION_MODULE_ID, moduleId, Store.YES));
				doc.add(new LongField(DESCRIPTION_EFFECTIVE_TIME, effectiveTime, Store.YES));

				doc.add(new NumericDocValuesField(COMPONENT_ID, sctId));
				doc.add(new NumericDocValuesField(COMPONENT_STORAGE_KEY, storageKey));
				doc.add(new NumericDocValuesField(DESCRIPTION_CASE_SIGNIFICANCE_ID, caseSignificanceId));
				doc.add(new NumericDocValuesField(DESCRIPTION_TYPE_ID, typeId));
				doc.add(new NumericDocValuesField(DESCRIPTION_CONCEPT_ID, containerConceptId));
				doc.add(new NumericDocValuesField(DESCRIPTION_MODULE_ID, moduleId));
				doc.add(new NumericDocValuesField(DESCRIPTION_EFFECTIVE_TIME, effectiveTime));
				
				snomedIndexService.index(branchPath, doc, IndexUtils.getStorageKeyTerm(storageKey));
			}
		});
	}
	

	private void indexUnvisitedConcepts(final Set<String> unvisitedConcepts, final Set<String> dirtyConceptsForCompareReindex) {
		
		final SnomedIndexServerService snomedIndexService = getSnomedIndexService();
		for (final String sConceptId : unvisitedConcepts) {
			
			final SnomedConceptIndexEntry concept = ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class).getConcept(branchPath, sConceptId);
			
			final long conceptId = Long.parseLong(sConceptId);
			final long conceptStorageKey = concept.getStorageKey();
			final boolean active = concept.isActive(); 
			final boolean released = concept.isReleased();
			final boolean primitive = concept.isPrimitive();
			final boolean exhaustive = concept.isExhaustive();
			final long moduleId = Long.parseLong(concept.getModuleId());
			final Collection<String> currentRefSetMemberships = getCurrentRefSetMemberships(sConceptId, newRefSetMemberships, detachedRefSetMemberships);
			final Collection<String> currentMappingMemberships = getCurrentMappingMemberships(sConceptId, newMappingMemberships, detachedMappingMemberships);
			
			final Document doc = createConceptDocument(
					conceptIdToPredicateMap, 
					conceptId, 
					conceptStorageKey, 
					active, 
					released, 
					primitive, 
					exhaustive, 
					moduleId, 
					currentRefSetMemberships,
					currentMappingMemberships,
					concept.getEffectiveTimeAsLong(),
					dirtyConceptsForCompareReindex.contains(sConceptId));
			
			snomedIndexService.index(branchPath, doc, IndexUtils.getStorageKeyTerm(conceptStorageKey));
		}
		
		
	}
	
	private void indexConcepts(final String absolutePath) {
		
		final SnomedIndexServerService snomedIndexService = getSnomedIndexService();
		
		parseFile(absolutePath, 5, new RecordParserCallback<String>() {

			@Override
			public void handleRecord(final int recordCount, final List<String> record) {
				
				final String sConceptId = record.get(0);
				visitedConcepts.add(sConceptId);

				final long conceptId = Long.parseLong(sConceptId);
				
				final long conceptStorageKey = getImportIndexService().getComponentCdoId(sConceptId);
				final boolean active = ACTIVE_STATUS.equals(record.get(2)); 
				final boolean primitive = isPrimitiveConcept(conceptId, Long.parseLong(record.get(4)));
				final boolean exhaustive = false;
				final long moduleId = Long.parseLong(record.get(3));
				final Collection<String> currentRefSetMemberships = getCurrentRefSetMemberships(sConceptId, newRefSetMemberships, detachedRefSetMemberships);
				final Collection<String> currentMappingMemberships = getCurrentMappingMemberships(sConceptId, newMappingMemberships, detachedMappingMemberships);
				
				final long effectiveTime = getEffectiveTime(record);
				final boolean released = isReleased(effectiveTime);
				
				final Document doc = createConceptDocument(
						conceptIdToPredicateMap, 
						conceptId, 
						conceptStorageKey, 
						active, 
						released, 
						primitive, 
						exhaustive, 
						moduleId, 
						currentRefSetMemberships,
						currentMappingMemberships,
						effectiveTime,
						true);
				
				snomedIndexService.index(branchPath, doc, IndexUtils.getStorageKeyTerm(conceptStorageKey));
			}
		});
	}
	
	private Document createConceptDocument(final Multimap<Long, String> conceptIdToPredicateMap, final long conceptId, final long conceptStorageKey, final boolean active,
			final boolean released, final boolean primitive, final boolean exhaustive, final long moduleId, final Collection<String> currentRefSetMemberships,
			final Collection<String> currentMappingMemberships, final long effectiveTime, final boolean indexAsRelevantForCompare) {
		
		final Document doc = new Document();
		
		final String conceptIdString = Long.toString(conceptId);
		
		doc.add(new LongField(COMPONENT_ID, conceptId, Store.YES));
		doc.add(new LongField(COMPONENT_STORAGE_KEY, conceptStorageKey, Store.YES));
		doc.add(new IntField(COMPONENT_TYPE, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Store.YES));
		doc.add(new IntField(CONCEPT_EXHAUSTIVE, exhaustive ? 1 : 0, Store.YES));
		doc.add(new IntField(COMPONENT_ACTIVE, active ? 1 : 0, Store.YES));
		doc.add(new IntField(CONCEPT_PRIMITIVE, primitive ? 1 : 0, Store.YES));
		doc.add(new StoredField(COMPONENT_RELEASED, released ? 1 : 0));
		doc.add(new LongField(CONCEPT_MODULE_ID, moduleId, Store.YES));
		doc.add(new NumericDocValuesField(COMPONENT_STORAGE_KEY, conceptStorageKey));
		doc.add(new NumericDocValuesField(COMPONENT_COMPARE_UNIQUE_KEY, indexAsRelevantForCompare ? conceptStorageKey : CDOUtils.NO_STORAGE_KEY));
		if (!indexAsRelevantForCompare) {
			doc.add(new NumericDocValuesField(COMPONENT_IGNORE_COMPARE_UNIQUE_KEY, conceptStorageKey));
		}
		doc.add(new NumericDocValuesField(COMPONENT_ID, conceptId));
		doc.add(new LongField(CONCEPT_NAMESPACE_ID, NamespaceMapping.getExtensionNamespaceId(conceptId), Store.NO));
		doc.add(new LongField(CONCEPT_EFFECTIVE_TIME, effectiveTime, Store.YES));
		
		final String iconId;

		//it might happen that a concept has been active previously hence it's in the taxonomy builder
		//but its just being inactivated.
		if (!active) {
			iconId = Concepts.ROOT_CONCEPT;
		} else {
			if (getTaxonomyBuilder().containsNode(conceptIdString)) {
				iconId = SnomedIconProvider.getInstance().getIconComponentId(conceptIdString, getTaxonomyBuilder());
			} else {
				if (identifierConceptIdsForNewRefSets.containsKey(conceptIdString)) {
					final SnomedRefSetType snomedRefSetType = identifierConceptIdsForNewRefSets.get(conceptIdString);
					iconId = SnomedRefSetUtil.getConceptId(snomedRefSetType);
				} else {
					iconId = Concepts.ROOT_CONCEPT;
				}
			}
		}
			
		doc.add(new LongField(COMPONENT_ICON_ID, Long.valueOf(iconId), Store.YES));
		doc.add(new NumericDocValuesField(COMPONENT_ICON_ID, Long.valueOf(iconId)));

		final String preferredTerm = getImportIndexService().getConceptLabel(conceptIdString);
		doc.add(new TextField(COMPONENT_LABEL, preferredTerm, Store.YES));
		doc.add(new BinaryDocValuesField(COMPONENT_LABEL, new BytesRef(preferredTerm)));
		SortKeyMode.SORT_ONLY.add(doc, preferredTerm);
		
		if (conceptIdToPredicateMap.containsKey(conceptId)) {
			final Collection<String> predicateKeys = conceptIdToPredicateMap.get(conceptId);
			for (final String predicateKey : predicateKeys) {
				doc.add(new StringField(COMPONENT_REFERRING_PREDICATE, predicateKey, Store.YES));
			}
		}
		
		// reference set membership information
		for (final String refSetId : currentRefSetMemberships) {
			doc.add(new LongField(CONCEPT_REFERRING_REFERENCE_SET_ID, Long.parseLong(refSetId), Store.YES));
		}
		for (final String mappingRefSetId : currentMappingMemberships) {
			doc.add(new LongField(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID, Long.parseLong(mappingRefSetId), Store.YES));
		}

		// query direct parents
		final LongCollection parentIds = getParentIds(String.valueOf(conceptId));
		if (parentIds.isEmpty()) {
			// if it has no parents, then it is the root
			doc.add(new LongField(CONCEPT_PARENT, ROOT_ID, Store.YES));
		} else {
			final LongIterator parentIdIterator = parentIds.iterator();
			while (parentIdIterator.hasNext()) {
				doc.add(new LongField(CONCEPT_PARENT, parentIdIterator.next(), Store.YES));
			}
		}
		// query ancestors
		final LongCollection ancestorIds = getAncestorIds(String.valueOf(conceptId));
		final LongIterator ancestorIdIterator = ancestorIds.iterator();
		while (ancestorIdIterator.hasNext()) {
			doc.add(new LongField(CONCEPT_ANCESTOR, ancestorIdIterator.next(), Store.YES));
		}
		
		final List<TermWithType> descriptions = getImportIndexService().getConceptDescriptions(String.valueOf(conceptId));
		for (final TermWithType termWithType : descriptions) {
			
			final String term = termWithType.term;
			final TermType type = termWithType.type;
			
			switch (type) {
				case FSN:
					doc.add(new TextField(CONCEPT_FULLY_SPECIFIED_NAME, term, Store.YES));
					break;

				case SYNONYM_AND_DESCENDANTS:
					doc.add(new TextField(CONCEPT_SYNONYM, term, Store.YES));
					break;

				case OTHER:
					doc.add(new TextField(CONCEPT_OTHER_DESCRIPTION, term, Store.YES));
					break;
					
				default:
					throw new IllegalStateException(MessageFormat.format("Unhandled term type ''{0}''.", type.name()));
			}
		}
			
		float doi = doiData.get(conceptId);
		if (0.0f == doi) {
			doi = DEFAULT_DOI;
		}
		
		doc.add(new StoredField(CONCEPT_DEGREE_OF_INTEREST, doi));
		doc.add(new FloatDocValuesField(CONCEPT_DEGREE_OF_INTEREST, doi));
		return doc;
	}
	
	private Collection<String> getCurrentRefSetMemberships(final String sConceptId, final Multimap<String, String> newRefSetMemberships, final Multimap<String, String> detachedRefSetMemberships) {
		final Collection<String> $ = Sets.newHashSet(ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class).getContainerRefSetIds(branchPath, sConceptId));
		$.addAll(newRefSetMemberships.get(sConceptId));
		$.removeAll(detachedRefSetMemberships.get(sConceptId));
		return $;
	}
	
	private Collection<String> getCurrentMappingMemberships(final String sConceptId, final Multimap<String, String> newMappingMemberships, final Multimap<String, String> detachedMappingMemberships) {
		final Collection<String> $ = Sets.newHashSet(ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class).getContainerMappingRefSetIds(branchPath, sConceptId));
		$.addAll(newMappingMemberships.get(sConceptId));
		$.removeAll(detachedMappingMemberships.get(sConceptId));
		return $;
	}
	
	private LongCollection getParentIds(final String conceptId) {
		if (getTaxonomyBuilder().containsNode(conceptId)) {
			return getTaxonomyBuilder().getAncestorNodeIds(conceptId); 
		}
		
		if (identifierConceptIdsForNewRefSets.containsKey(conceptId)) {
			final SnomedRefSetType type = identifierConceptIdsForNewRefSets.get(conceptId);
			return LongCollections.singletonSet(Long.parseLong(SnomedRefSetUtil.getConceptId(type)));
		}
		
		return LongCollections.emptySet();
	}
	
	private LongCollection getAncestorIds(final String conceptId) {
		if (getTaxonomyBuilder().containsNode(conceptId)) {
			return getTaxonomyBuilder().getAllIndirectAncestorNodeIds(conceptId);
		}
		
		if (identifierConceptIdsForNewRefSets.containsKey(conceptId)) {
			final SnomedRefSetType type = identifierConceptIdsForNewRefSets.get(conceptId);
			return getTaxonomyBuilder().getAllAncestorNodeIds(SnomedRefSetUtil.getConceptId(type));
		}
		
		return LongCollections.emptySet();
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
