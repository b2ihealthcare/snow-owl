/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.delta;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils.CDOObjectToCDOIDAdjuster;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.delta.BaseToHeadBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.delta.ComponentDelta;
import com.b2international.snowowl.datastore.delta.CompositeComponentDelta;
import com.b2international.snowowl.datastore.delta.HierarchicalComponentDelta;
import com.b2international.snowowl.datastore.delta.IComponentDeltaBuilder;
import com.b2international.snowowl.datastore.exception.UnresolvedPromoteblesException;
import com.b2international.snowowl.datastore.server.CDOChangeSetDataProvider;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.ComponentDeltaProvider;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.delta.ISnomedConceptDeltaProvider;
import com.b2international.snowowl.snomed.datastore.delta.RefSetMemberDeltaBuilder;
import com.b2international.snowowl.snomed.datastore.delta.SnomedConceptDeltaBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Component delta provider implementation for SNOMED CT.
 * 
 */
public class SnomedConceptDeltaProvider extends ComponentDeltaProvider<HierarchicalComponentDelta> implements ISnomedConceptDeltaProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedConceptDeltaProvider.class);
	
	@Override
	protected IComponentDeltaBuilder<HierarchicalComponentDelta> createComponentDeltaBuilder() {
		return new SnomedConceptDeltaBuilder();
	}

	@Override
	public <D extends ComponentDelta> Collection<D> getRefSetMemberDeltas(final IBranchPathMap branchPathMap, final String identifierConceptId) {
		final IBranchPath branchPath = branchPathMap.getBranchPath(SnomedPackage.eINSTANCE);
		
		final ICDOConnection connection = getConnection();
		final CDOBranch taskBranch = connection.getBranch(branchPath);
		
		if (null == taskBranch) {
			
			return Collections.emptySet();
			
		}
		
		CDOView taskView = null;
		
		try {
			
			taskView = connection.createView(taskBranch);
			
			final SnomedRefSet refSet = new SnomedRefSetLookupService().getComponent(identifierConceptId, taskView);
			
			if (null == refSet) {
				
				return Collections.emptySet();
				
			}
			
			
			short targetComponentType = CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
			
			if (refSet instanceof SnomedMappingRefSet) {
				targetComponentType = ((SnomedMappingRefSet) refSet).getMapTargetComponentType();
			} else if (SnomedRefSetType.ATTRIBUTE_VALUE.equals(refSet.getType())) {
				targetComponentType = SnomedTerminologyComponentConstants.CONCEPT_NUMBER; // value type refset always has concepts as values /'targets'.
			}
					
			
					final RefSetMemberDeltaBuilder builder = new RefSetMemberDeltaBuilder(
							branchPathMap,
							identifierConceptId,
							refSet.cdoID(), 
							refSet.getType(), 
							refSet.getReferencedComponentType(), 
							targetComponentType);
					

				final SnomedConceptDeltaProvider delegateProvider = new SnomedConceptDeltaProvider() {
					@SuppressWarnings({ "rawtypes", "unchecked" }) 
					@Override protected IComponentDeltaBuilder createComponentDeltaBuilder() {
						return builder;
					}
				};
				
				@SuppressWarnings("unchecked")
				final Collection<D> deltas = (Collection<D>) delegateProvider.getComponentDeltas(new BaseToHeadBranchPointCalculationStrategy(getConnection(), branchPath));
				
				return deltas;
			
		} finally {
			
			LifecycleUtil.deactivate(taskView);
			
		}
			
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.delta.ISnomedConceptDeltaProvider#promoteChanges(java.util.Collection, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Throwable promoteChanges(final Collection<ComponentDelta> deltas, final String identifierId, final String userId, final String commitComment) {

		Preconditions.checkNotNull(deltas, "Component deltas argument cannot be null.");
		Preconditions.checkNotNull(identifierId, "Reference set identifier concept ID cannot be null.");
		Preconditions.checkState(!CompareUtils.isEmpty(deltas), "Nothing to merge and promote.");
		
		//caches the change set data - CDO view pairs grouped by branch paths.
		LoadingCache<IBranchPath, ChangeSetDataWithTransaction> cache = null;
		//the parent of the changed branches
		final AtomicReference<IBranchPath> destinationBranch = new AtomicReference<IBranchPath>();
		
		//supplies a transaction to the HEAD of the destination branch. assumes initialized destination branch path
		final Supplier<SnomedEditingContext> editingContextSupplier = Suppliers.memoize(new Supplier<SnomedEditingContext>() {
			@Override public SnomedEditingContext get() {
				return new SnomedEditingContext(destinationBranch.get());
			}
		});
		
		//supplies a single boolean flag whether the reference set that has to be authored exists on the destination branch or not
		final SnomedReferenceSet referenceSet = Iterables.getOnlyElement(SnomedRequests.prepareSearchRefSet()
				.setLimit(1)
				.filterById(identifierId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, destinationBranch.get().getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync(), null);
		
		try {
			
			//caches the change set data - CDO view pairs grouped by branch paths.
			cache = CacheBuilder.newBuilder().build(new CacheLoader<IBranchPath, ChangeSetDataWithTransaction>() {
				
				@Override public ChangeSetDataWithTransaction load(final IBranchPath branchPath) throws Exception {
	
					Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
					Preconditions.checkState(!BranchPathUtils.isMain(branchPath), "Branch path was the '" + branchPath + "'branch.");
					
					final IBranchPath parent = branchPath.getParent();
					if (null == destinationBranch.get()) {
						
						destinationBranch.set(parent);
						
					} else {
						
						Preconditions.checkState(parent.equals(destinationBranch.get()), "Mismatching parent branches.");
						
					}
					
					final BaseToHeadBranchPointCalculationStrategy strategy = new BaseToHeadBranchPointCalculationStrategy(getConnection(), branchPath);
					final CDOChangeSetData changeSetData = CDOChangeSetDataProvider.INSTANCE.getChangeSetData(strategy);
					
					final ICDOConnection connection = getConnection();
					final CDOBranch cdoBranch = connection.getBranch(branchPath);
					Preconditions.checkNotNull(cdoBranch, "Branch does not exist. Path: '" + branchPath.getPath() + "'.");
					
					final CDOTransaction view = connection.createTransaction(cdoBranch);
					
					final ChangeSetDataWithTransaction $ = new ChangeSetDataWithTransaction();
					//map changes to CDO IDs.
					$.changes = Maps.uniqueIndex(changeSetData.getChangedObjects(), new Function<CDORevisionKey, CDOID>() {
						@Override public CDOID apply(final CDORevisionKey revisionKey) {
							return revisionKey.getID();
						}
					});
					$.transaction = view;
					
					return $;
					
				}
				
			});
	
			//reference set to author
			SnomedRegularRefSet refSet = null;
			
			//a collection of component deltas which cannot be promoted due to its referenced component deletion
			//may contain elements that cannot be promoted because of it's map target deletion
			final Collection<ComponentDelta> ignoredDeltas = Lists.newArrayList();
			
			for (final ComponentDelta delta : deltas) {
				
				final ChangeSetDataWithTransaction changeSetDataWithView = cache.get(delta.getBranchPath());
				
				//XXX consider thread safety if component delta loop runs parallel on the server
				if (referenceSet == null && refSet == null) {

					//as the reference set does not exist on the destination branch, we copied the reference set
					//and the identifier concept to the destination branch. the copy operation already copied 
					//actually created the reference set members as well. 
					//so we clear the new reference set's member list, that will remove all members from the transaction as well.
					refSet = createRefSetOnTransaction(identifierId, editingContextSupplier.get(), changeSetDataWithView);
					refSet.getMembers().clear();
					
				} else if (refSet == null) {
					refSet = CDOUtils.getObjectIfExists(editingContextSupplier.get().getTransaction(), referenceSet.getStorageKey());
				}

				//check whether referenced component (and map target) exists
				if (null != checkExistance(delta, destinationBranch.get())) {
					ignoredDeltas.add(delta);
					continue; //item processing is ignored
				}
				
				//if deleted we assume, that member exists on the target branch, it is just a deletion
				if (delta.isDeleted()) {
					
					final CDOObject member = CDOUtils.getObjectIfExists(editingContextSupplier.get().getTransaction(), delta.getCdoId());
					member.eSet(SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_RefSet(), null);
					CDOUtils.resolveElementProxies(refSet);
					EcoreUtil.remove(member);
					((InternalCDORevision) refSet.cdoRevision()).adjustReferences(CDOUtils.CDOObjectToCDOIDAdjuster.INSTANCE);
					
				//just load component on task branch, detach from it's container, re-attache to the new transaction
				} else if (delta.isNew()) {
					
					final SnomedRefSetMember member = (SnomedRefSetMember) CDOUtils.getObjectIfExists(changeSetDataWithView.transaction, delta.getCdoId());
					if (member == null) {
						throw new RuntimeException(String.format("Reference Set member '%s' was deleted before promotion. Please review your changes and try again!", delta.getLabel()));
					}
					member.eSet(SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_RefSet(), null);
					detachObject(member);
					editingContextSupplier.get().getTransaction().reload(member);
					refSet.getMembers().add(member);
					member.eSet(SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_RefSet(), refSet);
					
				//we assume update, so we apply the feature deltas of the changed revision delta to the object on the destination branch
				} else if (delta.hasChanged()) {
					
					final SnomedRefSetMember member = (SnomedRefSetMember) CDOUtils.getObjectIfExists(editingContextSupplier.get().getTransaction(), delta.getCdoId());
					
					final CDORevisionKey revisionKey = changeSetDataWithView.changes.get(member.cdoID());
					final CDORevisionDelta revisionDelta;
					
					if (revisionKey instanceof CDORevisionDelta) {
						
						revisionDelta = (CDORevisionDelta) revisionKey;
						
					} else {
						
						final InternalCDORevision revision = getRevisionManager(SnomedPackage.eINSTANCE).getRevision(
								revisionKey.getID(), 
								BranchPointUtils.convert(BranchPointUtils.create(getConnection(), delta.getBranchPath())),
								CDORevision.UNCHUNKED,
								CDORevision.DEPTH_NONE, 
								true);
						
						revisionDelta = CDORevisionUtil.createDelta(revision);
						
					}
					
					for (final CDOFeatureDelta featureDelta : revisionDelta.getFeatureDeltas()) {
						
						if (featureDelta instanceof CDOSetFeatureDelta) {
							
							final CDOSetFeatureDelta setFeatureDelta = (CDOSetFeatureDelta) featureDelta;
							member.eSet(setFeatureDelta.getFeature(), setFeatureDelta.getValue());
							
						}
						
					}
					
				}
				
				
			}
			
		
			final CDOTransaction transaction = editingContextSupplier.get().getTransaction();
			//adjust references at revision level for revision deltas
			CDOUtils.adjustRevsions(transaction, CDOObjectToCDOIDAdjuster.INSTANCE);
			
			CDOServerUtils.commit(transaction, userId, commitComment, new NullProgressMonitor());
			
			if (CompareUtils.isEmpty(ignoredDeltas)) {
				return null;
			} else {
				return new UnresolvedPromoteblesException(ignoredDeltas);
			}

		} catch (final ExecutionException e) {
			
			LOGGER.error(e.getMessage(), e);
			return new SnowowlServiceException("Error while applying changes to " + destinationBranch.get() + "'. Promotion failed.", e);
			
		} catch (final CommitException e) {
			
			LOGGER.error(e.getMessage(), e);
			return new SnowowlServiceException("Error while committing changed to " + destinationBranch.get() + "'. Promotion failed.", e);
			
		} catch (final Exception e) {
			
			LOGGER.error(e.getMessage(), e);
			return new SnowowlServiceException("Unexpected error while promoting task. Promotion failed.");
			
		} finally {
			
			if (null != cache) {
				
				final Collection<ChangeSetDataWithTransaction> values = cache.asMap().values();
				for (final Iterator<ChangeSetDataWithTransaction> itr = values.iterator(); itr.hasNext(); /**/) {
					
					LifecycleUtil.deactivate(itr.next().transaction);
					
				}
				
			}
			
			if (null != editingContextSupplier) {
				
				if (null != editingContextSupplier.get()) {
					
					LifecycleUtil.deactivate(editingContextSupplier.get().getTransaction());
					
				}
				
			}
			
		}
		
	}

	//method to check if a reference set member change (given as a component delta) can be merged to a branch given by its branch path
	//this method returns with null if the component exists on the target branch
	//if the reference set member change cannot be merged, then this method will return as a pair of component delta.
	//the first argument is the member that cannot be promoted/merged, the second value of the pair is the reason,
	//that can be the referenced component, or a map target in case of mapping reference sets
	private ComponentDelta checkExistance(final ComponentDelta delta, final IBranchPath targetBranchPath) {
		
		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == delta.getTerminologyComponentId()) {
			//nothing to do
			return null;
		}
		
		final String terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(delta.getTerminologyComponentId());
		final boolean exists = CoreTerminologyBroker.getInstance().getLookupService(terminologyComponentId).exists(targetBranchPath, delta.getId());
		
		if (!exists) {
			return delta;
		}
		
		if (delta instanceof CompositeComponentDelta) {
			
			final ComponentDelta target = ((CompositeComponentDelta) delta).getTarget();
			
			if (null != checkExistance(target, targetBranchPath)) {
				
				return delta;
				
			}
			
		}
		
		return null;
		
	}
	
	/*creates the new reference set (with the identifier concept as well) to the destination transaction and returns with the reference set*/
	private SnomedRegularRefSet createRefSetOnTransaction(final String identifierId, final SnomedEditingContext destinationEditingContext, final ChangeSetDataWithTransaction changeSetDataWithView) throws SnowowlServiceException {
		
		Preconditions.checkNotNull(identifierId, "Reference set identifier concept ID argument cannot be null.");
		Preconditions.checkNotNull(destinationEditingContext, "Destination editing context argument cannot be null.");
		Preconditions.checkNotNull(changeSetDataWithView, "Change set data and view argument cannot be null.");
		
		final Concept concept = new SnomedConceptLookupService().getComponent(identifierId, changeSetDataWithView.transaction);
		final SnomedRegularRefSet refSet = (SnomedRegularRefSet) new SnomedRefSetLookupService().getComponent(identifierId, changeSetDataWithView.transaction);
		
		try {
			
			CDOUtils.copy(concept, destinationEditingContext, true, false);
			return  CDOUtils.copy(refSet, destinationEditingContext.getRefSetEditingContext(), true, false);
			
		} catch (final SnowowlServiceException e) {
			
			throw e;
			
		}
		
		
	}
	
	/*returns with the net4j CDO connection*/
	private ICDOConnection getConnection() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE);
	}
	
	/**
	 * A pair of {@link CDORevisionKey revisions keys} mapped by the CDO IDs with the a proper {@link CDOTransaction transaction}.
	 */
	private static final class ChangeSetDataWithTransaction {
		private Map<CDOID, CDORevisionKey> changes;
		private CDOTransaction transaction;
	}
	
}