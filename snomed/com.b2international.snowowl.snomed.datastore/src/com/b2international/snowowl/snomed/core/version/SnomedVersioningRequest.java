/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.version;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.*;

import org.slf4j.Logger;

import com.b2international.commons.CompareUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.request.version.VersioningConfiguration;
import com.b2international.snowowl.core.request.version.VersioningRequest;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.*;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;


/**
 * Publish manager for SNOMED&nbsp;CT ontology.
 * <p>This class is responsible for the followings:
 * <ul>
 * <li>Adjust the effective time on all unpublished SNOMED&nbsp;CT components and reference set members.</li>
 * <li>Sets the released flags on all un-released SNOMED&nbsp;CT components and reference set members.</li>
 * <li>Updates the module dependency reference set state based on the overall component changes.</li>
 * </ul>
 */
public final class SnomedVersioningRequest extends VersioningRequest {

	private static final long serialVersionUID = 1L;

	private static final List<Class<? extends SnomedComponentDocument>> CORE_COMPONENT_TYPES = ImmutableList.of(
		SnomedConceptDocument.class,
		SnomedDescriptionIndexEntry.class, 
		SnomedRelationshipIndexEntry.class
	);
	
	private final Set<String> componentIdsToPublish = newHashSet();
	private final long effectiveTime;
	
	public SnomedVersioningRequest(VersioningConfiguration config) {
		super(config);
		this.effectiveTime = EffectiveTimes.getEffectiveTime(config().getEffectiveTime());
	}
	
	@Override
	protected void doVersionComponents(TransactionContext context) throws Exception {
		final Logger log = context.service(Logger.class);
		
		log.info("Publishing SNOMED CT components [effectiveTime: {}]...", EffectiveTimes.format(config().getEffectiveTime()));
		
		// sourceModuleId to targetModuleId map
		final Multimap<String, String> componentIdsByReferringModule = HashMultimap.create();
		
		RevisionSearcher searcher = context.service(RevisionSearcher.class);
			
		searcher.scroll(Query.select(SnomedConceptDocument.class)
				.where(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
				.limit(getCommitLimit(context))
				.build())
				.forEach(componentsToVersion -> versionComponents(context, componentsToVersion, componentIdsByReferringModule));
		
		searcher.scroll(Query.select(SnomedDescriptionIndexEntry.class)
				.where(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
				.limit(getCommitLimit(context))
				.build())
				.forEach(componentsToVersion -> versionComponents(context, componentsToVersion, componentIdsByReferringModule));
		
		searcher.scroll(Query.select(SnomedRelationshipIndexEntry.class)
				.where(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
				.limit(getCommitLimit(context))
				.build())
				.forEach(componentsToVersion -> versionComponents(context, componentsToVersion, componentIdsByReferringModule));
		
		searcher.scroll(Query.select(SnomedRefSetMemberIndexEntry.class)
				.where(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
				.limit(getCommitLimit(context))
				.build())
				.forEach(componentsToVersion -> versionComponents(context, componentsToVersion, componentIdsByReferringModule));
		
		// iterate over each module and get modules of all components registered to componentsByReferringModule
		log.info("Collecting module dependencies of changed components...");
		final Multimap<String, String> moduleDependencies = HashMultimap.create();
		final Map<String, Long> moduleToLatestEffectiveTime = newHashMap();
		for (String module : ImmutableSet.copyOf(componentIdsByReferringModule.keySet())) {
			final Collection<String> dependencies = componentIdsByReferringModule.removeAll(module);
			for (Class<? extends SnomedComponentDocument> type : CORE_COMPONENT_TYPES) {
				Query<String[]> dependencyQuery = Query.select(String[].class)
						.from(type)
						.fields(SnomedComponentDocument.Fields.ID, SnomedComponentDocument.Fields.MODULE_ID, SnomedComponentDocument.Fields.EFFECTIVE_TIME)
						.where(SnomedComponentDocument.Expressions.ids(dependencies))
						.limit(10000)
						.build();
				for (Hits<String[]> dependencyHits : context.service(RevisionSearcher.class).scroll(dependencyQuery)) {
					for (String[] dependency : dependencyHits) {
						String targetModule = dependency[1];
						if (!module.equals(targetModule)) {
							moduleDependencies.put(module, targetModule);
						}
						moduleToLatestEffectiveTime.merge(targetModule, Long.parseLong(dependency[2]), (oldEffectiveTime, newEffectiveTime) -> {
							if (oldEffectiveTime == EffectiveTimes.UNSET_EFFECTIVE_TIME || newEffectiveTime == EffectiveTimes.UNSET_EFFECTIVE_TIME) {
								return EffectiveTimes.UNSET_EFFECTIVE_TIME;
							} else {
								return Math.max(oldEffectiveTime, newEffectiveTime);
							}
						});
					}
				}
			}
		}
		log.info("Collecting module dependencies of changed components successfully finished.");
		
		log.info("Adjusting effective time changes on module dependency...");
		adjustDependencyRefSetMembers(context, moduleDependencies, moduleToLatestEffectiveTime, effectiveTime);
		log.info("Effective time adjustment successfully finished on module dependency.");
		
	}
	
	private void versionComponents(TransactionContext context, Iterable<? extends SnomedDocument> componentsToVersion, Multimap<String, String> componentIdsByReferringModule) {
		
		for (SnomedDocument componentToVersion : componentsToVersion) {
			// register IDs for publication
			if (componentToVersion instanceof SnomedComponentDocument) {
				componentIdsToPublish.add(componentToVersion.getId());
			}
			
			// stage update on components based on actual type
			final SnomedDocument.Builder<?, ?> updatedComponent;
			if (componentToVersion instanceof SnomedConceptDocument) {
				final SnomedConceptDocument concept = (SnomedConceptDocument) componentToVersion;
				componentIdsByReferringModule.put(concept.getModuleId(), concept.isPrimitive() ? Concepts.PRIMITIVE : Concepts.FULLY_DEFINED);
				updatedComponent = SnomedConceptDocument.builder(concept);
			} else if (componentToVersion instanceof SnomedDescriptionIndexEntry) {
				final SnomedDescriptionIndexEntry description = (SnomedDescriptionIndexEntry) componentToVersion;
				componentIdsByReferringModule.put(description.getModuleId(), description.getConceptId());
				componentIdsByReferringModule.put(description.getModuleId(), description.getTypeId());
				componentIdsByReferringModule.put(description.getModuleId(), description.getCaseSignificanceId());
				updatedComponent = SnomedDescriptionIndexEntry.builder(description);
			} else if (componentToVersion instanceof SnomedRelationshipIndexEntry) {
				final SnomedRelationshipIndexEntry relationship = (SnomedRelationshipIndexEntry) componentToVersion;
				componentIdsByReferringModule.put(relationship.getModuleId(), relationship.getSourceId());
				componentIdsByReferringModule.put(relationship.getModuleId(), relationship.getTypeId());
				componentIdsByReferringModule.put(relationship.getModuleId(), relationship.getDestinationId());
				componentIdsByReferringModule.put(relationship.getModuleId(), relationship.getModifierId());
				componentIdsByReferringModule.put(relationship.getModuleId(), relationship.getCharacteristicTypeId());
				updatedComponent = SnomedRelationshipIndexEntry.builder(relationship);
			} else if (componentToVersion instanceof SnomedRefSetMemberIndexEntry) {
				final SnomedRefSetMemberIndexEntry member = (SnomedRefSetMemberIndexEntry) componentToVersion;
				componentIdsByReferringModule.put(member.getModuleId(), member.getReferenceSetId());
				
				registerIfConcept(componentIdsByReferringModule, member.getModuleId(), member.getReferencedComponentId());
				
				final Map<String, Object> additionalFields = member.getAdditionalFields();
				SnomedRf2Headers.MEMBER_FIELDS_WITH_COMPONENT_ID.forEach(field -> {
					registerIfConcept(componentIdsByReferringModule, member.getModuleId(), (String) additionalFields.get(field));
				});
				
				updatedComponent = SnomedRefSetMemberIndexEntry.builder(member);
			} else {
				throw new UnsupportedOperationException("Not implemented case for: " + componentToVersion);
			}
			
			context.update(
				componentToVersion, 
				updatedComponent
					.effectiveTime(effectiveTime)
					.released(true)
				.build()
			);
		}
		
	}

	private void adjustDependencyRefSetMembers(TransactionContext context, Multimap<String, String> moduleDependencies, Map<String, Long> moduleToLatestEffectiveTime, long effectiveTime) {
		// Update existing, add new members to moduleDependencyRefSet
		if (!CompareUtils.isEmpty(moduleDependencies)) {
			moduleDependencies.entries().forEach((entry) -> {
				final String source = entry.getKey();
				final String target = entry.getValue();
				
				final SnomedReferenceSetMember existingLatestMember = SnomedRequests.prepareSearchMember()
						.all()
						.filterByModule(source)
						.filterByReferencedComponent(target)
						.filterByRefSet(Concepts.REFSET_MODULE_DEPENDENCY_TYPE)
						.build()
						.execute(context)
						.stream()
						.sorted((o1, o2) -> {
							if (null == o1.getEffectiveTime() && null == o2.getEffectiveTime()) {
								return 0;
							} else if (null == o1.getEffectiveTime() && null != o2.getEffectiveTime()) {
								return 1;
							} else if (null != o1.getEffectiveTime() && null == o2.getEffectiveTime()) {
								return -1;
							}
							return o1.getEffectiveTime().compareTo(o2.getEffectiveTime());
						})
						.reduce((first, second) -> second)
						.orElse(null);
				
				final SnomedRefSetMemberIndexEntry.Builder updatedMember;
				if (existingLatestMember == null) {
					updatedMember = SnomedRefSetMemberIndexEntry.builder()
							.id(UUID.randomUUID().toString())
							.active(true)
							.referenceSetId(Concepts.REFSET_MODULE_DEPENDENCY_TYPE)
							.referenceSetType(SnomedRefSetType.MODULE_DEPENDENCY)
							.moduleId(source)
							.referencedComponentId(target);
				} else {
					updatedMember = SnomedRefSetMemberIndexEntry.builder(existingLatestMember);
				}

				final long targetEffectiveTime = moduleToLatestEffectiveTime.get(target) == EffectiveTimes.UNSET_EFFECTIVE_TIME ? effectiveTime : moduleToLatestEffectiveTime.get(target);
				
				updatedMember
					.released(true)
					.effectiveTime(effectiveTime)
					.field(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, effectiveTime)
					.field(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, targetEffectiveTime);
				
				if (existingLatestMember == null) {
					context.add(updatedMember.build());
				} else {
					context.update(SnomedRefSetMemberIndexEntry.builder(existingLatestMember).build(), updatedMember.build());
				}
				
			});
		}
	}

	private void registerIfConcept(Multimap<String, String> componentIdsByReferringModule, String moduleId, String dependency) {
		if (SnomedIdentifiers.isValid(dependency)) {
			componentIdsByReferringModule.put(moduleId, dependency);
		}
	}

//	@Override
//	public void postCommit() {
//		if (!CompareUtils.isEmpty(componentIdsToPublish)) {
//			try {
//				SnomedRequests.identifiers().preparePublish()
//					.setComponentIds(componentIdsToPublish)
//					.build(getRepositoryUuid())
//					.execute(getEventBus())
//					.getSync();
//			} catch (SctIdStatusException e) {
//				// report ID issues as warning instead of error
//				LOGGER.warn(e.getMessage(), e);
//			}
//		}
//		super.postCommit();
//	}
	
}
