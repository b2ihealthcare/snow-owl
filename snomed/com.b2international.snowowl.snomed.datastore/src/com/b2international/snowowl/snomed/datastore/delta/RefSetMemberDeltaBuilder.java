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
package com.b2international.snowowl.snomed.datastore.delta;

import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;

import com.b2international.commons.Change;
import com.b2international.commons.ChangeKind;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.Triple;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentIconIdProvider;
import com.b2international.snowowl.core.api.IComponentIconIdProvider.NoopComponentIconIdProvider;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.delta.CompositeComponentDelta;
import com.b2international.snowowl.datastore.delta.HierarchicalComponentDelta;
import com.b2international.snowowl.datastore.tasks.ITaskStateManager;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

/**
 * Builder for processing SNOMED&nbsp;CT reference set member changes performed on 'Reference set' and
 * 'Mapping' authoring tasks.
 * TODO consider propagating NEW, or MODIFIED nature of the task, 
 * If nature is known we could ignore detached and dirty changes in case of MODIFIED. 
 */
public class RefSetMemberDeltaBuilder extends SnomedConceptDeltaBuilder {

	/**CDO ID of the reference set.*/
	private final CDOID cdoId;
	/**Flag indicating whether the tracked SNOMED&nbspCT reference set has a targeted type.*/
	private final boolean hasTarget;
	/*Referenced component type as a short primitive.*/
	private final short referencedComponentType;
	/**Target component type as a short primitive. Could be ignored in case of *NON* targeting reference sets.*/
	private final short targetComponentType;
	/**Flag indicating whether the change set contains modification that will be ignored.
	 *<br>E.g.: the user made SNOMED&nbsp;CT concept changes in 'reference set authoring' task.*/
//	private final AtomicBoolean hasProhibitedChanges = new AtomicBoolean(false); //TODO use this somewhere. probably at IComponentDeltaPredicate??
	/**Predicate for filtering out irrelevant component changes.*/
	private final IComponentDeltaPredicate deltaPredicate;
	/**Name provider for the referenced component type.*/
	private final IComponentNameProvider referencedComponentNameProvider;
	/**Name provider for the targeted type.*/
	private final IComponentNameProvider targetComponentNameProvider;
	/**Icon ID provider for the referenced components.*/
	private IComponentIconIdProvider<String> referencedComponentIconIdProvider;
	/**Icon ID provider for the targeted components (if any).*/
	private IComponentIconIdProvider<String> targetComponentIconIdProvider;
	private Supplier<IBranchPath> targetComponentBranchPathSupplier;
	private String targetComponentRepositoryUuid;
	private SnomedRefSetType refsetType;

	public RefSetMemberDeltaBuilder(final String identifierConceptId, final String userId, final CDOID cdoId, final SnomedRefSetType refsetType, final short referencedComponentType, final short targetComponentType) {
		
		this.refsetType = refsetType;
		this.cdoId = Preconditions.checkNotNull(cdoId, "CDO ID argument cannot be null.");
		hasTarget = SnomedRefSetUtil.isMapping(Preconditions.checkNotNull(refsetType, "Reference set type argument cannot be null.")) || SnomedRefSetType.ATTRIBUTE_VALUE.equals(refsetType);
		this.referencedComponentType = referencedComponentType;
		this.targetComponentType = targetComponentType;
		
		deltaPredicate = new RefSetMemberDeltaPredicate(this.cdoId);
		
		referencedComponentNameProvider = getNameProvider(referencedComponentType);
		referencedComponentIconIdProvider = getComponentIconIdProvider(referencedComponentType);
		
		if (hasTarget) {
			targetComponentNameProvider = getNameProvider(targetComponentType);
			targetComponentIconIdProvider = getComponentIconIdProvider(targetComponentType);
			targetComponentRepositoryUuid = CodeSystemUtils.getRepositoryUuid(targetComponentType);
		} else {
			targetComponentNameProvider = IComponentNameProvider.NOOP_NAME_PROVIDER;
			targetComponentIconIdProvider = NoopComponentIconIdProvider.getInstance();
			targetComponentRepositoryUuid = SnomedDatastoreActivator.REPOSITORY_UUID;
		}
		
		 targetComponentBranchPathSupplier = Suppliers.memoize(new Supplier<IBranchPath>() {
				@Override public IBranchPath get() {
					final IBranchPathMap branchPathMap = ApplicationContext.getInstance().getService(ITaskStateManager.class).getBranchPathMapConfiguration(userId, true);
					return hasTarget ? branchPathMap.getBranchPath(targetComponentRepositoryUuid) : getBranchPath();
				}
			});
		
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.AbstractHierarchicalComponentDeltaBuilder#postProcess()
	 */
	@Override
	protected void postProcess() {

		//if not a targeted
		if (!hasTarget) {
			
			//and the referenced component is a SNOMED CT concept
			if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER == referencedComponentType) {
				
				//we build the terminology but to achieve the same taxonomy as we have in reference set editor
				//we have to collapse nodes
				super.postProcess();
				collapseTaxonomy();
				
			}
			
		}
		
		//otherwise taxonomy building is disabled
		
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.AbstractComponentDeltaBuilder#getDeltaPredicate()
	 */
	@Override
	protected IComponentDeltaPredicate getDeltaPredicate() {
		return deltaPredicate;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.delta.SnomedConceptDeltaBuilder#processChange(org.eclipse.emf.cdo.common.revision.CDOIDAndVersion, org.eclipse.emf.cdo.view.CDOView, com.b2international.snowowl.datastore.delta.ChangeKind)
	 */
	@Override
	protected void processChange(final CDOIDAndVersion idAndVersion, final CDOView view, final ChangeKind change) {
		
		if (idAndVersion instanceof InternalCDORevision) {
		
			final InternalCDORevision revision = (InternalCDORevision) idAndVersion;
			final EClass eClass = revision.getEClass();
			final long cdoId = CDOIDUtils.asLong(revision.getID());
			
			if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(eClass)) {
					
				
					final String referencedComponentId = String.valueOf(revision.getValue(SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_ReferencedComponentId()));
					final String label = referencedComponentNameProvider.getComponentLabel(getBranchPath(), referencedComponentId);
					
					
						final HierarchicalComponentDelta delta = new HierarchicalComponentDelta(
								referencedComponentId, 
								cdoId,
								getBranchPath(),
								label,
								referencedComponentIconIdProvider.getIconId(BranchPointUtils.create(view), referencedComponentId),
								referencedComponentType,
								getCodeSystemOID(referencedComponentType),
								change);
						
						if (!hasTarget) {
							
							put(delta);
							
						} else {
						
							final String targetComponentId;
							String targetComponentLabel;
							final String targetComponentIconId;
							
							if (SnomedRefSetUtil.isMapping(refsetType)) {
								targetComponentId = String.valueOf(revision.getValue(SnomedRefSetPackage.eINSTANCE.getSnomedSimpleMapRefSetMember_MapTargetComponentId()));
								targetComponentLabel = getComponentLabel(targetComponentId);
								targetComponentIconId = getComponentIconId(targetComponentId);
										
								if (StringUtils.isEmpty(targetComponentLabel)) {
									targetComponentLabel = targetComponentId;
								}
								
							} else {
								//it is attribute value type refset
								
								targetComponentId = String.valueOf(revision.getValue(SnomedRefSetPackage.eINSTANCE.getSnomedAttributeValueRefSetMember_ValueId()));
								targetComponentLabel = getComponentLabel(targetComponentId);
								targetComponentIconId = getComponentIconId(targetComponentId);
										
								if (StringUtils.isEmpty(targetComponentLabel)) {
									targetComponentLabel = targetComponentId;
								}
								
								
							}
							
							final HierarchicalComponentDelta target = new HierarchicalComponentDelta(
									targetComponentId, 
									cdoId, 
									CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == targetComponentType ? getBranchPath() : targetComponentBranchPathSupplier.get(),
									targetComponentLabel,
									targetComponentIconId,
									targetComponentType,
									getCodeSystemOID(targetComponentType));
							
							put(new CompositeComponentDelta(delta, target));
							
						
					}
					
					
			}
		}
		
		final CDOObject object = CDOUtils.getObjectIfExists(view, idAndVersion.getID());
		
		if (object instanceof SnomedRefSetMember) {
			
			final SnomedRefSetMember member = (SnomedRefSetMember) object;
			final String referencedComponentId = member.getReferencedComponentId();
			final String label = referencedComponentNameProvider.getComponentLabel(getBranchPath(), referencedComponentId);
			final long cdoId = CDOIDUtils.asLong(member.cdoID());
			
				final HierarchicalComponentDelta delta = new HierarchicalComponentDelta(
						referencedComponentId, 
						cdoId,
						getBranchPath(),
						label,
						referencedComponentIconIdProvider.getIconId(BranchPointUtils.create(view), referencedComponentId),
						referencedComponentType,
						getCodeSystemOID(referencedComponentType),
						change);
				
				if (!hasTarget) {
					
					put(delta);
					
				} else {
				
					String targetComponentId;
					String targetComponentLabel;
					String targetComponentIconId;
					if (SnomedRefSetUtil.isMapping(refsetType)) {
						final SnomedSimpleMapRefSetMember mappinMember = (SnomedSimpleMapRefSetMember) object;
						targetComponentId = mappinMember.getMapTargetComponentId();
						targetComponentLabel = getComponentLabel(targetComponentId);
						targetComponentIconId = getComponentIconId(targetComponentId);
								
						if (StringUtils.isEmpty(targetComponentLabel)) {
							targetComponentLabel = targetComponentId;
						}
					} else {
						SnomedAttributeValueRefSetMember attributeValueMember = (SnomedAttributeValueRefSetMember) object;
						targetComponentId = attributeValueMember.getValueId();
						targetComponentLabel = getComponentLabel(targetComponentId);
						targetComponentIconId = getComponentIconId(targetComponentId);
					}
					
					
					
					final HierarchicalComponentDelta target = new HierarchicalComponentDelta(
							targetComponentId, 
							cdoId, 
							CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == targetComponentType ? getBranchPath() : targetComponentBranchPathSupplier.get(), 
							targetComponentLabel, 
							targetComponentIconId,
							targetComponentType,
							getCodeSystemOID(targetComponentType));
					
					put(new CompositeComponentDelta(delta, target));
			}
		}
	}

	
	private String getComponentLabel(final String targetComponentId) {
		return CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == targetComponentType 
				? targetComponentId 
				: targetComponentNameProvider.getComponentLabel(targetComponentBranchPathSupplier.get(), targetComponentId);
	}

	private String getComponentIconId(final String targetComponentId) {
		return CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == targetComponentType
				? targetComponentId
				: targetComponentIconIdProvider.getIconId(BranchPointUtils.create(targetComponentRepositoryUuid, targetComponentBranchPathSupplier.get()), targetComponentId);
	}
	
	/*collapses the taxonomy built among the components to have the same as we have in reference set editors*/
	private void collapseTaxonomy() {
		
		//get a collection of root nodes first
		final Collection<HierarchicalComponentDelta> roots = Collections2.filter(getDeltas(), new Predicate<HierarchicalComponentDelta>() {
			@Override public boolean apply(final HierarchicalComponentDelta input) {
				return null == input.getParent();
			}
		});
		
		//get the top level concepts. we could be smart enough to calculate terminology level and find the proper top level components, but
		//this is only for SNOMED CT.
		final Collection<HierarchicalComponentDelta> topLevels = Sets.newHashSet();
		for (final HierarchicalComponentDelta root : roots) {
			topLevels.addAll(root.getChildren());
		}
		
		for (final HierarchicalComponentDelta topLevel : topLevels) { //from top to bottom rebuild taxonomy
			
			//collect item to remove and collapse tree by updating parentage
			for (final HierarchicalComponentDelta delta : collectNodesToCollapse(topLevel, topLevel.getChildren().iterator())) {
				
				final HierarchicalComponentDelta parent = delta.getParent();
				final Collection<HierarchicalComponentDelta> children = delta.getChildren();
				
				//update ancestor
				parent.getChildren().remove(delta);
				parent.getChildren().addAll(children);
				
				//update descendants
				for (final HierarchicalComponentDelta child : children) {
					child.setParent(parent);
				}
				
				delta.getChildren().clear();
				remove(delta);
			}
		}
	}
	
	/*collects and returns with a collection of all deltas that has to be removed*/
	private Collection<HierarchicalComponentDelta> collectNodesToCollapse(final HierarchicalComponentDelta ancestor, final Iterator<HierarchicalComponentDelta> descendantItr) {
		
		final Collection<HierarchicalComponentDelta> nodesToCollapse = Sets.newHashSet();
		
		while (descendantItr.hasNext()) {
		
			final HierarchicalComponentDelta descendant = descendantItr.next();
			
			if (CompareUtils.isEmpty(descendant.getChildren().iterator())) {
				return Collections.emptySet();
			}
			
			
			final Iterator<HierarchicalComponentDelta> subDescendantsItr = descendant.getChildren().iterator();
	
			//mark delta for removal
			if (!descendant.hasChanged()) {
				
				nodesToCollapse.add(descendant);
				
			}

			nodesToCollapse.addAll(collectNodesToCollapse(ancestor, subDescendantsItr));
		
		}
		
		return nodesToCollapse;
		
	}

	/*returns with the name provider service for the referenced components*/
	private IComponentNameProvider getNameProvider(final short referencedComponentType) {
		
		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == referencedComponentType) {
			
			return IComponentNameProvider.NOOP_NAME_PROVIDER;
			
		}
		
		return CoreTerminologyBroker.getInstance().getNameProviderFactory(
				CoreTerminologyBroker.getInstance().getTerminologyComponentId(referencedComponentType)).getNameProvider();
	}

	/**
	 * Predicate for tracking reference set member changes of a particular reference set given with its unique CDO ID.
	 */
	private static final class RefSetMemberDeltaPredicate implements IComponentDeltaPredicate {

		private final CDOID cdoId;

		private RefSetMemberDeltaPredicate(final CDOID cdoId) {
			this.cdoId = Preconditions.checkNotNull(cdoId, "CDO ID argument cannot be null.");
		}
		
		@Override
		public boolean apply(final Triple<CDOIDAndVersion, CDOView, Change> input) {
			checkNotNull(input, "input");
			return apply(input.getA(), check(input.getB()), input.getC());
		}

		@Override
		public boolean apply(final CDOIDAndVersion cdoidAndVersion, final CDOView view, final Change change) {
			
			if (cdoidAndVersion instanceof InternalCDORevision) {
				
				final InternalCDORevision revision = (InternalCDORevision) cdoidAndVersion;
				final EClass eClass = revision.getEClass();
				
				//accept only reference set members
				if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(eClass)) {
					
					final Object value = revision.getValue(SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_RefSet());
					
					if (value instanceof CDOID) {
						
						return cdoId.equals(value); //member of the tracked reference set. include processing
						
					} else if (value instanceof SnomedRefSet) {
						
						return cdoId.equals(((SnomedRefSet) value).cdoID()); //member of the tracked reference set. include and process it.
						
					}
					
					throw new IllegalArgumentException("Cannot process reference set reference for member: " + revision);
					
				}
				
			}
			
			final CDOObject object = CDOUtils.getObjectIfExists(view, cdoidAndVersion.getID());
			
			if (object instanceof SnomedRefSetMember) {
				
				final SnomedRefSetMember member = (SnomedRefSetMember) object;
				
				return cdoId.equals(member.getRefSet().cdoID());
				
			}
			
			//ignore any other changes
			return false;
		}
		
	}

	private IComponentIconIdProvider<String> getComponentIconIdProvider(final short terminologyComponentId) {
		
		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == terminologyComponentId) {
			
			return NoopComponentIconIdProvider.<String>getInstance();
			
		}
		
		final String _terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(terminologyComponentId);
		return CoreTerminologyBroker.getInstance().getComponentIconIdProvider(_terminologyComponentId);
		
	}
}
