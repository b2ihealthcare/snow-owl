/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;

import com.b2international.commons.CompareUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkResponse;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.version.VersioningConfiguration;
import com.b2international.snowowl.datastore.version.VersioningRequest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
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

	private static final List<Class<? extends SnomedComponentDocument>> CORE_COMPONENT_TYPES = ImmutableList.of(
		SnomedConceptDocument.class,
		SnomedDescriptionIndexEntry.class, 
		SnomedRelationshipIndexEntry.class
	);
	
	private Set<String> componentIdsToPublish = newHashSet();
	
	public SnomedVersioningRequest(VersioningConfiguration config) {
		super(config);
	}
	
	@Override
	protected void doVersionComponents(TransactionContext context) {
		final Logger log = context.service(Logger.class);
		
		log.info("Collecting unpublished components...");
		BulkResponse response = RepositoryRequests.prepareBulkRead()
			.setBody(BulkRequest.<BranchContext>create()
					.add(SnomedRequests.prepareSearchConcept()
							.all()
							.filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
							.setExpand(SnomedConcept.Expand.REFERENCE_SET + "()"))
					.add(SnomedRequests.prepareSearchDescription()
							.all()
							.filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
					.add(SnomedRequests.prepareSearchRelationship()
							.all()
							.filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
					.add(SnomedRequests.prepareSearchMember()
							.all()
							.filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)))
			.build()
			.execute(context);

		// sourceModuleId to targetModuleId map
		final Multimap<String, String> componentIdsByReferringModule = HashMultimap.create();
		
		log.info("Publishing components...");
		for (CollectionResource<?> hits : response.getResponses(CollectionResource.class)) {
			for (SnomedComponent hit : Iterables.filter(hits, SnomedComponent.class)) {
				
				// register IDs for publication
				if (hit instanceof SnomedCoreComponent) {
					componentIdsToPublish.add(hit.getId());
				}
				
				// stage update on components based on actual type
				final SnomedDocument.Builder updatedComponent; 
				if (hit instanceof SnomedConcept) {
					final SnomedConcept concept = (SnomedConcept) hit;
					componentIdsByReferringModule.put(concept.getModuleId(), concept.getDefinitionStatus().getConceptId());
					updatedComponent = SnomedConceptDocument.builder(concept);
				} else if (hit instanceof SnomedDescription) {
					final SnomedDescription description = (SnomedDescription) hit;
					componentIdsByReferringModule.put(description.getModuleId(), description.getConceptId());
					componentIdsByReferringModule.put(description.getModuleId(), description.getTypeId());
					componentIdsByReferringModule.put(description.getModuleId(), description.getCaseSignificance().getConceptId());
					updatedComponent = SnomedDescriptionIndexEntry.builder(description);
				} else if (hit instanceof SnomedRelationship) {
					final SnomedRelationship relationship = (SnomedRelationship) hit;
					componentIdsByReferringModule.put(relationship.getModuleId(), relationship.getSourceId());
					componentIdsByReferringModule.put(relationship.getModuleId(), relationship.getTypeId());
					componentIdsByReferringModule.put(relationship.getModuleId(), relationship.getDestinationId());
					componentIdsByReferringModule.put(relationship.getModuleId(), relationship.getModifier().getConceptId());
					componentIdsByReferringModule.put(relationship.getModuleId(), relationship.getCharacteristicType().getConceptId());
					updatedComponent = SnomedRelationshipIndexEntry.builder(relationship);
				} else if (hit instanceof SnomedReferenceSetMember) {
					final SnomedReferenceSetMember member = (SnomedReferenceSetMember) hit;
					componentIdsByReferringModule.put(member.getModuleId(), member.getReferenceSetId());

					registerIfConcept(componentIdsByReferringModule, member.getModuleId(), member.getReferencedComponent().getId());
					
					SnomedRf2Headers.MEMBER_FIELDS_WITH_COMPONENT_ID.forEach(field -> {
						registerIfConcept(componentIdsByReferringModule, member.getModuleId(), (String) member.getProperties().get(field));
					});
					
					updatedComponent = SnomedRefSetMemberIndexEntry.builder(member);
				} else {
					throw new UnsupportedOperationException("Not implemented case for: " + hit);
				}
				
				context.update(
					updatedComponent.build(), 
					updatedComponent
						.effectiveTime(EffectiveTimes.getEffectiveTime(config().getEffectiveTime()))
						.released(true)
						.build()
				);
			}
		}
		
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
		adjustDependencyRefSetMembers(context, moduleDependencies, moduleToLatestEffectiveTime, config().getEffectiveTime());
		log.info("Effective time adjustment successfully finished on module dependency.");
		
	}
	
	private void adjustDependencyRefSetMembers(TransactionContext context, Multimap<String, String> moduleDependencies, Map<String, Long> moduleToLatestEffectiveTime, Date effectiveTime) {
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
							.moduleId(source)
							.referencedComponentId(target);
				} else {
					updatedMember = SnomedRefSetMemberIndexEntry.builder(existingLatestMember);
				}

				final long targetEffectiveTime = moduleToLatestEffectiveTime.get(target) == EffectiveTimes.UNSET_EFFECTIVE_TIME ? effectiveTime.getTime() : moduleToLatestEffectiveTime.get(target);
				
				updatedMember
					.released(true)
					.effectiveTime(effectiveTime.getTime())
					.field(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, effectiveTime.getTime())
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
//	protected void createCodeSystemVersion(final CDOEditingContext editingContext, VersioningConfiguration config) {
//		if (Branch.MAIN_PATH.equals(editingContext.getBranch())) {
//			super.createCodeSystemVersion(editingContext, config);
//		} else {
//			try (final SnomedEditingContext mainEditingContext = new SnomedEditingContext(BranchPathUtils.createMainPath())) {
//				super.createCodeSystemVersion(mainEditingContext, config);
//				final String commitComment = String.format("New Snomed Version %s was added to Snomed Release %s.", config.getVersionId(), config.getCodeSystemShortName());
//				CDOServerUtils.commit(mainEditingContext, "System", commitComment, null);
//			} catch (Exception e) {
//				throw new SnowowlRuntimeException(String.format("An error occurred while adding Snomed Version %s to Snomed Release %s.",
//						config.getVersionId(), config.getCodeSystemShortName()), e);
//			}
//		}
//	}
//	
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
