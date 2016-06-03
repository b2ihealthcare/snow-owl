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
package com.b2international.snowowl.datastore.server.snomed.version;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_MODULE_DEPENDENCY_TYPE;

import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.snomed.SnomedModuleDependencyCollectorService;
import com.b2international.snowowl.datastore.server.version.PublishManager;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.SnomedRelease;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.store.SnomedReleases;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService.IdStorageKeyPair;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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

	private final Collection<SnomedModuleDependencyRefSetMember> newModuleDependencyRefSetMembers = Sets.newHashSet();
	private final Collection<String> componentIdsToPublish = Sets.newHashSet();
	
	private final SnomedIdentifiers snomedIdentifiers;
	private final ISnomedComponentService componentService;
	
	public SnomedPublishManager() {
		this.snomedIdentifiers = new SnomedIdentifiers(ApplicationContext.getInstance().getServiceChecked(ISnomedIdentifierService.class));
		this.componentService = ApplicationContext.getInstance().getServiceChecked(ISnomedComponentService.class);
	}
	
	@Override
	protected LongSet getUnversionedComponentStorageKeys(final IBranchPath branchPath) {
		final ISnomedComponentService componentService = getServiceForClass(ISnomedComponentService.class);
		return componentService.getAllUnpublishedComponentStorageKeys(branchPath);
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
	protected void preProcess(final LongSet storageKeys) {
		collectModuleDependencyChanges(storageKeys);
		collectComponentIdsToPublish(storageKeys);
	}

	private void collectModuleDependencyChanges(final LongSet storageKeys) {
		LOGGER.info("Collecting module dependency changes...");
		newModuleDependencyRefSetMembers.addAll(collectModuleDependecyRefSetMembers(storageKeys));
		LOGGER.info("Collecting module dependency changes successfully finished.");
	}

	private void collectComponentIdsToPublish(final LongSet storageKeys) {
		LOGGER.info("Collecting component IDs for ID publication...");
		final Collection<IdStorageKeyPair> idStorageKeyPairs = getIdStorageKeyPairs(storageKeys);
		
		for (final IdStorageKeyPair idStorageKeyPair : idStorageKeyPairs) {
			componentIdsToPublish.add(idStorageKeyPair.getId());
		}
		
		LOGGER.info("Collecting component IDs for ID publication successfully finished.");
	}

	private Collection<IdStorageKeyPair> getIdStorageKeyPairs(final LongSet storageKeys) {
		final Collection<IdStorageKeyPair> pairs = Lists.newArrayList();
		pairs.addAll(componentService.getAllComponentIdStorageKeys(getBranchPathForPublication(),
				SnomedTerminologyComponentConstants.CONCEPT_NUMBER));
		pairs.addAll(componentService.getAllComponentIdStorageKeys(getBranchPathForPublication(),
				SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER));
		pairs.addAll(componentService.getAllComponentIdStorageKeys(getBranchPathForPublication(),
				SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER));
		
		final Collection<IdStorageKeyPair> filteredPairs = Collections2.filter(pairs, new Predicate<IdStorageKeyPair>() {
			@Override
			public boolean apply(IdStorageKeyPair input) {
				return storageKeys.contains(input.getStorageKey());
			}
		});
		
		return filteredPairs;
	}
	
	@Override
	protected CodeSystemVersion createCodeSystemVersion() {
		return SnomedReleases.newSnomedVersion()
				.withVersionId(getVersionName())
				.withDescription(getCodeSystemVersionDescription())
				.withImportDate(new Date())
				.withEffectiveDate(getEffectiveTime())
				.withParentBranchPath(getConfiguration().getParentBranchPath())
				// TODO modules?
				.build();
	}

	@Override
	protected void addCodeSystemVersion(final CodeSystemVersion codeSystemVersion) {
		final String shortName = getConfiguration().getCodeSystemShortName();
		if (getEditingContext().getBranch().equals(IBranchPath.MAIN_BRANCH)) {
			final SnomedRelease snomedRelease = ((SnomedEditingContext) getEditingContext()).getSnomedRelease(shortName, null);
			
			if (snomedRelease == null) {
				throw new IllegalStateException(String.format("Couldn't find SNOMED release for %s.", shortName));
			} else {
				snomedRelease.getCodeSystemVersions().add(codeSystemVersion);
			}
		} else {
			try (final SnomedEditingContext ec = new SnomedEditingContext(BranchPathUtils.createMainPath())) {
				final SnomedRelease snomedRelease = ec.getSnomedRelease(shortName, null);
				if (snomedRelease == null) {
					throw new IllegalStateException(String.format("Couldn't find SNOMED release for %s.", shortName));
				} else {
					snomedRelease.getCodeSystemVersions().add(codeSystemVersion);
					
					final String commitComment = String.format("New Snomed Version %s was added to Snomed Release %s.",
							codeSystemVersion.getVersionId(), snomedRelease.getShortName());
					CDOServerUtils.commit(ec, getConfiguration().getUserId(), commitComment, null);
				}
			} catch (Exception e) {
				throw new SnowowlRuntimeException(String.format("An error occurred while adding Snomed Version %s to Snomed Release %s.",
						codeSystemVersion.getVersionId(), shortName), e);
			}
		}
	}
	
	@Override
	protected void postProcess() {
		LOGGER.info("Adjusting effective time changes on module dependency...");
		adjustNewModuleDependencyRefSetMembers();
		LOGGER.info("Effective time adjustment successfully finished on module dependency.");
	}
	
	@Override
	public void postCommit() {
		if (!componentIdsToPublish.isEmpty()) {
			snomedIdentifiers.publish(componentIdsToPublish);
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

	/**Updates all new module dependency reference set members.*/
	private void adjustNewModuleDependencyRefSetMembers() {
		for (final SnomedModuleDependencyRefSetMember member : newModuleDependencyRefSetMembers) {
			adjustRelased(member);
			adjustEffectiveTime(member);
			processNewModuleDependencyMember(member);
		}
	}

	/**Adjust all required and supported effective time changes on the given module dependency reference set member.*/
	private void adjustEffectiveTime(final SnomedModuleDependencyRefSetMember member) {
		if (!getEffectiveTime().equals(member.getEffectiveTime())) {
			member.setEffectiveTime(getEffectiveTime());
		}
		adjustModuleSourceEffectiveTime(member);
		adjustModuleTargetEffectiveTime(member);
	}

	/**Adjusts the module target effective time.*/
	private void adjustModuleTargetEffectiveTime(final SnomedModuleDependencyRefSetMember member) {
		if (canSetModuleTargetEffectiveTime(member)) {
			member.setTargetEffectiveTime(getEffectiveTime());
		}
	}

	/**Returns {@code true} if the module target effective time can be adjusted for the given module dependency reference set member.*/
	private boolean canSetModuleTargetEffectiveTime(final SnomedModuleDependencyRefSetMember member) {
		return null == member.getTargetEffectiveTime();
	}

	/**Adjusts the module source effective time for the given member.*/
	private void adjustModuleSourceEffectiveTime(final SnomedModuleDependencyRefSetMember member) {
		if (!getEffectiveTime().equals(member.getSourceEffectiveTime())) {
			member.setSourceEffectiveTime(getEffectiveTime());
		}
	}

	/**Sets the released flag on the given reference set member.*/
	private void adjustRelased(final SnomedModuleDependencyRefSetMember member) {
		if (!member.isReleased()) {
			member.setReleased(true);
		}
	}

	/**Processes the new module dependency reference set member by adding it to the underlying transaction.*/
	private void processNewModuleDependencyMember(final SnomedModuleDependencyRefSetMember member) {
		getModuleDependencyRefSet().getMembers().add(member);
	}

	private SnomedRegularRefSet getModuleDependencyRefSet() {
		return (SnomedRegularRefSet) new SnomedRefSetLookupService().getComponent(REFSET_MODULE_DEPENDENCY_TYPE, getTransaction());
	}

	/**Collects all new module dependency reference set members*/
	private Collection<SnomedModuleDependencyRefSetMember> collectModuleDependecyRefSetMembers(final LongSet storageKeys) {
		return SnomedModuleDependencyCollectorService.INSTANCE.collectModuleMembers(getTransaction(), storageKeys);
	}
	
	@Override
	protected Collection<ICodeSystemVersion> getAllVersions(final IBranchPath branchPath) {
		return new CodeSystemRequests(getRepositoryUuid())
				.prepareSearchCodeSystemVersion()
				.setCodeSystemShortName(getConfiguration().getCodeSystemShortName())
				.build(IBranchPath.MAIN_BRANCH)
				.executeSync(getEventBus())
				.getItems();
	}
	
}
