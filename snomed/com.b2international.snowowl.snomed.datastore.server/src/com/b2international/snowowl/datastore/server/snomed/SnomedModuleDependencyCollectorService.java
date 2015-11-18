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
package com.b2international.snowowl.datastore.server.snomed;

import static com.b2international.commons.pcj.LongSets.forEach;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.b2international.snowowl.datastore.server.snomed.ModuleCollectorConfigurationThreadLocal.getConfiguration;
import static com.b2international.snowowl.datastore.server.snomed.ModuleCollectorConfigurationThreadLocal.reset;
import static com.b2international.snowowl.datastore.server.snomed.ModuleCollectorConfigurationThreadLocal.setConfiguration;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_MODULE_DEPENDENCY_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_OPERATOR_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Stopwatch.createStarted;
import static com.google.common.collect.HashMultimap.create;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Long.parseLong;
import static java.util.UUID.randomUUID;
import static org.apache.lucene.search.MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.eclipse.emf.cdo.view.CDOView;
import org.slf4j.Logger;

import bak.pcj.LongCollection;
import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.set.LongSet;

import com.b2international.commons.pcj.LongSets.LongCollectionProcedure;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.datastore.server.snomed.index.ConcreteDataTypePropertyCollector;
import com.b2international.snowowl.datastore.server.snomed.index.DescriptionPropertyCollector;
import com.b2international.snowowl.datastore.server.snomed.index.RelationshipPropertyCollector;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;

/**
 * Server side stateless singleton service for resolving SNOMED&nbsp;CT module dependencies.
 * This service is responsible for creating new module dependency reference set members as the
 * part of the versioning process.
 *
 */
public enum SnomedModuleDependencyCollectorService {

	/**Singleton service.*/
	INSTANCE;
	
	private static final Logger LOGGER = getLogger(SnomedModuleDependencyCollectorService.class);
	
	private static final long PRIMITIVE = parseLong(Concepts.PRIMITIVE);
	private static final long FULLY_DEFINED = parseLong(Concepts.FULLY_DEFINED);
	private static final long MODULE_ROOT = parseLong(Concepts.MODULE_ROOT);
	private static final long IS_A = parseLong(Concepts.IS_A);
	
	private static final Query ALL_CDT_MEMBERS_QUERY;
	
	static {
		
		final MultiTermQuery allCdtMembersQuery = new PrefixQuery(new Term(REFERENCE_SET_MEMBER_OPERATOR_ID));
		allCdtMembersQuery.setRewriteMethod(CONSTANT_SCORE_FILTER_REWRITE);
		ALL_CDT_MEMBERS_QUERY = allCdtMembersQuery;

	}
	
	/**
	 * Returns with a collection of {@link SnomedModuleDependencyRefSetMember module dependency members}
	 * representing the current module dependency state of the SNOMED&nbsp;CT ontology. The list might contain
	 * new reference set members.
	 * @param view the CDO view. The view is not closed by this method. It's the callers responsibility to clean up the CDO view.
	 * @param unpublishedStorageKeys a collection of unpublished component storage keys.
	 * @return a collection of module dependency members.
	 */
	public Collection<SnomedModuleDependencyRefSetMember> collectModuleMembers(final CDOView view, 
			final LongCollection unpublishedStorageKeys) {
	
		final Stopwatch stopwatch = createStarted();
		final Collection<SnomedModuleDependencyRefSetMember> members = newHashSet();
		
		try {
			
			LOGGER.info("Initializing resources to resolve module dependencies... [1 of 6]");
			setConfiguration(initConfiguration(check(view), checkNotNull(unpublishedStorageKeys, "unpublishedStorageKeys")));

			LOGGER.info("Processing unversioned concept... [2 of 6]");
			tryCreateMembersForConceptChanges();

			LOGGER.info("Processing unversioned descriptions... [3 of 6]");
			tryCreateMembersForDescriptionChanges();
			
			LOGGER.info("Processing unversioned relationships... [4 of 6]");
			tryCreateMembersForRelationshipChanges();
			
			LOGGER.info("Processing unversioned concrete domains... [5 of 6]");
			tryCreateMembersForDataTypeChanges();
			
			LOGGER.info("Processing unversioned module concepts... [6 of 6]");
			tryCreateMembersForNewModules();
			
			members.addAll(getMembers());
			
		} finally {
			reset();
		}
		
		LOGGER.info("SNOMED CT module dependencies have been successfully resolved. Found " + members.size() + " modules. [" + stopwatch + "]");
		return members;
		
	}

	private ModuleCollectorConfiguration initConfiguration(final CDOView view, final LongCollection unpublishedStorageKeys) {
	
		final IBranchPath branchPath = createPath(view);
		final ModuleCollectorConfiguration configuration = new ModuleCollectorConfiguration();
		
		configuration.setView(view);
		configuration.setBranchPath(branchPath);
		configuration.setConceptModuleMapping(getConceptModuleMapping(branchPath));
		configuration.setUnpublishedStorageKeys(unpublishedStorageKeys);
		configuration.setExistingModules(getExistingModules(branchPath));
		configuration.setModuleMapping(getModuleMapping(configuration.getExistingModules()));
		final SnomedRegularRefSet moduleRefSet = getModuleRefSet(view);
		configuration.setModuleDependencyRefSet(moduleRefSet);
		configuration.getMembers().addAll(getModuleMembers(moduleRefSet));
		
		return configuration;
	}

	private void tryCreateMembersForConceptChanges() {
		
		for (final long[] ids : getAllConceptIdsStorageKeys()) {
			
			final long conceptId = ids[0];
			final long storageKey = ids[1];
			
			if (getUnpublishedStorageKeys().contains(storageKey)) {
				final long conceptModuleId = getConceptModuleMapping().get(conceptId);
				tryCreateMember(conceptModuleId, getConceptModuleMapping().get(PRIMITIVE));
				tryCreateMember(conceptModuleId, getConceptModuleMapping().get(FULLY_DEFINED));
			}
			
		}
		
	}

	private void tryCreateMembersForDescriptionChanges() {
		final DescriptionPropertyCollector collector = new DescriptionPropertyCollector(getUnpublishedStorageKeys());
		getIndexServerService().search(getBranchPath(), SnomedMappings.newQuery().description().matchAll(), collector);
		
		for (final LongKeyMapIterator itr = collector.getMapping().entries(); itr.hasNext(); /**/) {
			itr.next();
			
			final long[] properties = (long[]) itr.getValue();
			final long conceptId = properties[0];
			final long moduleId = properties[1];
			final long typeId = properties[2];
			final long caseSignificanceId = properties[3];
			
			tryCreateMember(moduleId, getConceptModuleMapping().get(conceptId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(typeId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(caseSignificanceId));
		}
	}

	private void tryCreateMembersForRelationshipChanges() {
		final RelationshipPropertyCollector collector = new RelationshipPropertyCollector(getUnpublishedStorageKeys());
		getIndexServerService().search(getBranchPath(), SnomedMappings.newQuery().relationship().matchAll(), collector);
		
		for (final LongKeyMapIterator itr = collector.getMapping().entries(); itr.hasNext(); /**/) {
			itr.next();
			
			final long[] properties = (long[]) itr.getValue();
			final long characteristicTypeId = properties[0];
			final long moduleId = properties[1];
			final long typeId = properties[2];
			final long sourceId = properties[3];
			final long destinationId = properties[4];
			final long modifierId = properties[5];
			
			tryCreateMember(moduleId, getConceptModuleMapping().get(characteristicTypeId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(typeId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(sourceId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(destinationId));
			tryCreateMember(moduleId, getConceptModuleMapping().get(modifierId));
		}
	}

	private void tryCreateMembersForDataTypeChanges() {
		final ConcreteDataTypePropertyCollector collector = new ConcreteDataTypePropertyCollector(getUnpublishedStorageKeys());
		getIndexServerService().search(getBranchPath(), ALL_CDT_MEMBERS_QUERY, collector);
		
		for (final LongKeyMapIterator itr = collector.getMapping().entries(); itr.hasNext(); /**/) {
			itr.next();
			
			final long[] properties = (long[]) itr.getValue();
			final long moduleId = properties[0];
			final long containerModuleId = properties[1];
			
			tryCreateMember(moduleId, containerModuleId);
		}
	}

	private void tryCreateMembersForNewModules() {
		forEach(getAllModuleConceptIds(), new LongCollectionProcedure() {
			@Override
			public void apply(final long moduleConceptId) {
				
				for (final SnomedRelationshipIndexEntry isARelationship : getInboundIsARelationships(moduleConceptId)) {
					if (isComponentAffected(isARelationship.getStorageKey())) {
						
						final Concept concept = new SnomedConceptLookupService().getComponent(isARelationship.getObjectId(), getView());
						final Concept module = concept.getModule();
						
						tryCreateMember(module.getId(), isARelationship.getModuleId());
						
						for (final Description description : concept.getDescriptions()) {
							tryCreateMember(module, description.getModule());
							tryCreateMember(module, description.getType().getModule());
							tryCreateMember(module, description.getCaseSignificance().getModule());
						}
						
						for (final Relationship relationship : concept.getOutboundRelationships()) {
							tryCreateMember(module, relationship.getModule());
							tryCreateMember(module, relationship.getCharacteristicType().getModule());
							tryCreateMember(module, relationship.getDestination().getModule());
							tryCreateMember(module, relationship.getModifier().getModule());
							tryCreateMember(module, relationship.getType().getModule());
						}
						
					}
				}
				
			}
		});
	}

	private void tryCreateMember(final long sourceModuleId, final long targetModuleId) {
		
		if (sourceModuleId != targetModuleId && !isKnownModuleDependency(sourceModuleId, targetModuleId)) {
			
			final String targetId = Long.toString(targetModuleId);
			final SnomedModuleDependencyRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedModuleDependencyRefSetMember();
			member.setUuid(randomUUID().toString());
			member.setActive(true);
			member.setReleased(false);
			member.setModuleId(Long.toString(sourceModuleId));
			member.setReferencedComponentId(targetId);
			member.setRefSet(getModuleDependencyRefSet());
			member.setEffectiveTime(getExistingModuleEffectiveTime(targetId));
			
			getMembers().add(member);
			getModuleMapping().put(sourceModuleId, targetModuleId);
			
		}
		
	}
	
	private void tryCreateMember(final Concept sourceConcept, final Concept targetConcept) {
		tryCreateMember(parseLong(sourceConcept.getId()), parseLong(targetConcept.getId()));
	}
	
	private void tryCreateMember(final String sourceModuleId, final String targetModuleId) {
		tryCreateMember(parseLong(sourceModuleId), parseLong(targetModuleId));
	}

	private Collection<SnomedRelationshipIndexEntry> getInboundIsARelationships(final long moduleConceptId) {
		return getStatementBrowser().getInboundStatementsById(getBranchPath(), moduleConceptId, IS_A);
	}

	private SnomedStatementBrowser getStatementBrowser() {
		return getServiceForClass(SnomedStatementBrowser.class);
	}
	
	private boolean isComponentAffected(final long storageKey) {
		return getUnpublishedStorageKeys().contains(storageKey);
	}
	
	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return getServiceForClass(SnomedTerminologyBrowser.class);
	}

	private LongSet getAllModuleConceptIds() {
		final LongSet moduleConceptIds = getTerminologyBrowser().getAllSubTypeIds(getBranchPath(), MODULE_ROOT);
		moduleConceptIds.add(MODULE_ROOT);
		return moduleConceptIds;
	}
	
	private boolean isKnownModuleDependency(final long sourceModuleId, final long targetModuleId) {
		return getModuleMapping().get(sourceModuleId).contains(targetModuleId);
	}

	private Date getExistingModuleEffectiveTime(final String expectedModuleId) {
		for (final SnomedModuleDependencyRefSetMemberFragment module : getExistingModules()) {
			final Date sourceEffectiveTime = module.getSourceEffectiveTime();
			final String moduleId = module.getModuleId();
			if (null != sourceEffectiveTime && moduleId.equals(expectedModuleId)) {
				return sourceEffectiveTime;
			}
			final Date targetEffectiveTime = module.getTargetEffectiveTime();
			if (null != targetEffectiveTime && moduleId.equals(expectedModuleId)) {
				return targetEffectiveTime;
			}
		}
		return null;
	}

	private HashSet<SnomedModuleDependencyRefSetMember> getModuleMembers(final SnomedRegularRefSet moduleRefSet) {
		return newHashSet(filter(moduleRefSet.getMembers(), SnomedModuleDependencyRefSetMember.class));
	}

	private SnomedRegularRefSet getModuleRefSet(final CDOView view) {
		return checkNotNull((SnomedRegularRefSet) new SnomedRefSetLookupService().getComponent(REFSET_MODULE_DEPENDENCY_TYPE, view), "Missing module reference set %s", REFSET_MODULE_DEPENDENCY_TYPE);
	}
	
	private long[][] getAllConceptIdsStorageKeys() {
		return getTerminologyBrowser().getAllConceptIdsStorageKeys(getBranchPath());
	}

	private Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules(final IBranchPath branchPath) {
		return getComponentService().getExistingModules(branchPath);
	}
	
	private Multimap<Long, Long> getModuleMapping(final Collection<SnomedModuleDependencyRefSetMemberFragment> existingModules) {
		final Multimap<Long, Long> moduleMapping = create();
		for (final SnomedModuleDependencyRefSetMemberFragment module : existingModules) {
			moduleMapping.put(parseLong(module.getModuleId()), parseLong(module.getReferencedComponentId()));
		}
		return moduleMapping;
	}

	private LongKeyLongMap getConceptModuleMapping(final IBranchPath branchPath) {
		return getComponentService().getConceptModuleMapping(branchPath);
	}

	private IndexServerService<?> getIndexServerService() {
		return (IndexServerService<?>) getServiceForClass(SnomedIndexService.class);
	}


	private ISnomedComponentService getComponentService() {
		return getServiceForClass(ISnomedComponentService.class);
	}
	
	private LongCollection getUnpublishedStorageKeys() {
		return getConfiguration().getUnpublishedStorageKeys();
	}

	private CDOView getView() {
		return getConfiguration().getView();
	}
	
	private IBranchPath getBranchPath() {
		return getConfiguration().getBranchPath();
	}

	private LongKeyLongMap getConceptModuleMapping() {
		return getConfiguration().getConceptModuleMapping();
	}

	private Collection<SnomedModuleDependencyRefSetMember> getMembers() {
		return getConfiguration().getMembers();
	}
	
	private Multimap<Long, Long> getModuleMapping() {
		return getConfiguration().getModuleMapping();
	}
	
	private SnomedRefSet getModuleDependencyRefSet() {
		return getConfiguration().getModuleDependencyRefSet();
	}
	
	private Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules() {
		return getConfiguration().getExistingModules();
	}
	
}