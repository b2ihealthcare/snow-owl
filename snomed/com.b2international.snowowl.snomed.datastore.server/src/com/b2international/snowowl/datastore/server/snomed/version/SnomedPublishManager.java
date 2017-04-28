/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.version;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_MODULE_DEPENDENCY_TYPE;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkResponse;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.snomed.SnomedModuleDependencyCollectorService;
import com.b2international.snowowl.datastore.server.version.PublishManager;
import com.b2international.snowowl.datastore.version.PublishOperationConfiguration;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.id.AbstractSnomedIdentifierService.SctIdStatusException;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.google.common.base.Function;

/**
 * Publish manager for SNOMED&nbsp;CT ontology.
 * <p>This class is responsible for the followings:
 * <ul>
 * <li>Adjust the effective time on all unpublished SNOMED&nbsp;CT components and reference set members.</li>
 * <li>Sets the released flags on all un-released SNOMED&nbsp;CT components and reference set members.</li>
 * <li>Updates the module dependency reference set state based on the overall component changes.</li>
 * </ul>
 */
public class SnomedPublishManager extends PublishManager {

	private Set<String> componentIdsToPublish = newHashSet();
	private Collection<SnomedModuleDependencyRefSetMember> newModuleDependencyRefSetMembers;
	
	@Override
	protected LongSet getUnversionedComponentStorageKeys(final String branch) {
		return RepositoryRequests.prepareBulkRead()
				.setBody(BulkRequest.<BranchContext>create()
						.add(SnomedRequests.prepareSearchConcept().all().filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
						.add(SnomedRequests.prepareSearchDescription().all().filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
						.add(SnomedRequests.prepareSearchRelationship().all().filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
						.add(SnomedRequests.prepareSearchMember().all().filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)))
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<BulkResponse, LongSet>() {
					@Override
					public LongSet apply(BulkResponse input) {
						// index all unpublished components by its unique ID and storageKey
						final LongSet unpublishedStorageKeys = PrimitiveSets.newLongOpenHashSet();
						for (CollectionResource<?> hits : input.getResponses(CollectionResource.class)) {
							for (Object hit : hits) {
								if (hit instanceof SnomedComponent) {
									final SnomedComponent component = (SnomedComponent) hit;
									final String id = component.getId();
									if (component instanceof SnomedCoreComponent) {
										// if core component mark ID as publishable
										componentIdsToPublish.add(id);
									}
									unpublishedStorageKeys.add(((SnomedComponent) hit).getStorageKey());
								}
							}
						}
						return unpublishedStorageKeys;
					}
				})
				.getSync();
	}

	@Override
	protected EStructuralFeature getEffectiveTimeFeature(final EClass eClass) {
		if (isCoreComponent(eClass)) {
			return SnomedPackage.eINSTANCE.getComponent_EffectiveTime();
		} else if (isRefSetMember(eClass)) {
			return SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_EffectiveTime();
		}
		throw new IllegalArgumentException("Unsupported or unexpected component type: " + eClass);
	}

	@Override
	protected CDOEditingContext createEditingContext(IBranchPath branchPath) {
		return new SnomedEditingContext(branchPath);
	}
	
	@Override
	protected EStructuralFeature getReleasedFeature(final EClass eClass) {
		if (isCoreComponent(eClass)) {
			return SnomedPackage.eINSTANCE.getComponent_Released();
		} else if (isRefSetMember(eClass)) {
			return SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_Released();
		}
		throw new IllegalArgumentException("Unsupported or unexpected component type: " + eClass);
	}

	@Override
	protected String getRepositoryUuid() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}

	/**
	 * Since effective time and publication cannot be interpreted for SNOMED&nbsp;CT reference sets, we ignore them.
	 * <p>{@inheritDoc}}
	 */
	@Override
	protected boolean isIgnoredType(final EClass eClass) {
		return SnomedRefSetPackage.eINSTANCE.getSnomedRefSet().isSuperTypeOf(eClass);
	}
	
	@Override
	protected void preProcess(final LongSet storageKeys, PublishOperationConfiguration config) {
		collectModuleDependencyChanges(storageKeys, config);
	}

	private void collectModuleDependencyChanges(final LongSet storageKeys, PublishOperationConfiguration config) {
		LOGGER.info("Collecting module dependency changes...");
		newModuleDependencyRefSetMembers = ApplicationContext.getServiceForClass(RepositoryManager.class)
			.get(getRepositoryUuid())
			.service(RevisionIndex.class)
			.read(getBranchPathForPublication(config), new RevisionIndexRead<Collection<SnomedModuleDependencyRefSetMember>>() {
				@Override
				public Collection<SnomedModuleDependencyRefSetMember> execute(RevisionSearcher searcher) throws IOException {
					return SnomedModuleDependencyCollectorService.INSTANCE.collectModuleMembers(searcher, getTransaction(), storageKeys);
				}
			});
		LOGGER.info("Collecting module dependency changes successfully finished.");
	}
	
	@Override
	protected void addCodeSystemVersion(final String codeSystemShortName, final CodeSystemVersion codeSystemVersion) {
		if (getEditingContext().getBranch().equals(IBranchPath.MAIN_BRANCH)) {
			final CodeSystem codeSystem = getEditingContext().lookup(codeSystemShortName, CodeSystem.class);
			
			if (codeSystem == null) {
				throw new IllegalStateException(String.format("Couldn't find SNOMED release for %s.", codeSystemShortName));
			} else {
				codeSystem.getCodeSystemVersions().add(codeSystemVersion);
			}
		} else {
			try (final SnomedEditingContext ec = new SnomedEditingContext(BranchPathUtils.createMainPath())) {
				final CodeSystem codeSystem = ec.lookup(codeSystemShortName, CodeSystem.class);
				if (codeSystem == null) {
					throw new IllegalStateException(String.format("Couldn't find SNOMED release for %s.", codeSystemShortName));
				} else {
					codeSystem.getCodeSystemVersions().add(codeSystemVersion);
					
					final String commitComment = String.format("New Snomed Version %s was added to Snomed Release %s.",
							codeSystemVersion.getVersionId(), codeSystem.getShortName());
					CDOServerUtils.commit(ec, "System", commitComment, null);
				}
			} catch (Exception e) {
				throw new SnowowlRuntimeException(String.format("An error occurred while adding Snomed Version %s to Snomed Release %s.",
						codeSystemVersion.getVersionId(), codeSystemShortName), e);
			}
		}
	}
	
	@Override
	protected void postProcess(PublishOperationConfiguration config) {
		LOGGER.info("Adjusting effective time changes on module dependency...");
		adjustNewModuleDependencyRefSetMembers(config.getEffectiveTime());
		LOGGER.info("Effective time adjustment successfully finished on module dependency.");
	}
	
	@Override
	public void postCommit() {
		if (!CompareUtils.isEmpty(componentIdsToPublish)) {
			try {
				SnomedRequests.identifiers().preparePublish()
					.setComponentIds(componentIdsToPublish)
					.build(getRepositoryUuid())
					.execute(getEventBus())
					.getSync();
			} catch (SctIdStatusException e) {
				// report ID issues as warning instead of error
				LOGGER.warn(e.getMessage(), e);
			}
		}
		super.postCommit();
	}
	
	/**Returns {@code true} if the given {@link EClass} represents a SNOMED&nbsp;CT reference set member. Otherwise {@code false}.*/
	private boolean isRefSetMember(final EClass eClass) {
		return SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(eClass);
	}

	/**Returns {@code true} if the given {@link EClass} represents a SNOMED&nbsp;CT core component.*/
	private boolean isCoreComponent(final EClass eClass) {
		return SnomedPackage.eINSTANCE.getComponent().isSuperTypeOf(eClass);
	}

	/**Updates all new module dependency reference set members.
	 * @param date */
	private void adjustNewModuleDependencyRefSetMembers(Date effectiveTime) {
		final SnomedRegularRefSet moduleDependencyRefSet = getEditingContext().lookup(REFSET_MODULE_DEPENDENCY_TYPE, SnomedRegularRefSet.class);
		for (final SnomedModuleDependencyRefSetMember member : newModuleDependencyRefSetMembers) {
			adjustRelased(member);
			adjustEffectiveTime(member, effectiveTime);
			moduleDependencyRefSet.getMembers().add(member);
		}
	}

	/**Adjust all required and supported effective time changes on the given module dependency reference set member.
	 * @param effectiveTime */
	private void adjustEffectiveTime(final SnomedModuleDependencyRefSetMember member, Date effectiveTime) {
		if (!effectiveTime.equals(member.getEffectiveTime())) {
			member.setEffectiveTime(effectiveTime);
		}
		adjustModuleSourceEffectiveTime(member, effectiveTime);
		adjustModuleTargetEffectiveTime(member, effectiveTime);
	}

	/**Adjusts the module target effective time.*/
	private void adjustModuleTargetEffectiveTime(final SnomedModuleDependencyRefSetMember member, final Date effectiveTime) {
		if (canSetModuleTargetEffectiveTime(member)) {
			member.setTargetEffectiveTime(effectiveTime);
		}
	}

	/**Returns {@code true} if the module target effective time can be adjusted for the given module dependency reference set member.*/
	private boolean canSetModuleTargetEffectiveTime(final SnomedModuleDependencyRefSetMember member) {
		return null == member.getTargetEffectiveTime();
	}

	/**Adjusts the module source effective time for the given member.*/
	private void adjustModuleSourceEffectiveTime(final SnomedModuleDependencyRefSetMember member, final Date effectiveTime) {
		if (!effectiveTime.equals(member.getSourceEffectiveTime())) {
			member.setSourceEffectiveTime(effectiveTime);
		}
	}

	/**Sets the released flag on the given reference set member.*/
	private void adjustRelased(final SnomedModuleDependencyRefSetMember member) {
		if (!member.isReleased()) {
			member.setReleased(true);
		}
	}
	
}
