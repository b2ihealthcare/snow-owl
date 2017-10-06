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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.net4j.util.AdapterUtil;

import com.b2international.commons.ChangeKind;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ComponentTextProvider;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.CdoViewComponentTextProvider;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.delta.AbstractHierarchicalComponentDeltaBuilder;
import com.b2international.snowowl.datastore.delta.HierarchicalComponentDelta;
import com.b2international.snowowl.datastore.index.AbstractIndexEntry;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIconIdProvider;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import bak.pcj.set.LongSet;

/**
 * Visitor to collect feature changes that are specific for SNOMED CT.
 * The changes are always 'owned' by a concept regardless where the change occurs concept,
 * relationships or descriptions.
 * 
 */
public class SnomedConceptDeltaBuilder extends AbstractHierarchicalComponentDeltaBuilder<HierarchicalComponentDelta> {

	/**
	 * Mapping between CDO IDs and the corresponding SNOMED&nbsp;CT concept IDs.
	 * <br>Also contains mapping between the CDO ID of a reference 
	 * sets and the concept ID of the reference set identifier concept.
	 * <br><b>NOTE:&nbsp;</b>use {@link #getComponentId(long, CDOView)} 
	 * whenever getting concept ID for CDO ID.
	 */
	private final Map<Long, String> storageKeyConceptIds = Maps.newHashMap();
	
	/**
	 * Mapping between SNOMED&nbsp;CT concept ID to the concept CDO ID.
	 * <br><b>NOTE:&nbsp;</b>use {@link #getStorageKey(String, CDOView)} 
	 * whenever getting CDO ID for concept ID.
	 */
	private final Map<String, Long> conceptIdStorageKeyIds = Maps.newHashMap();
	
	private ComponentTextProvider currentTextProvider;
	
	@Override
	public Collection<HierarchicalComponentDelta> processChanges(CDOChangeSetData changeSetData, CDOView baseView, CDOView currentView) {

		currentTextProvider = new CdoViewComponentTextProvider(ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class), currentView);
		return super.processChanges(changeSetData, baseView, currentView);
	}

	/**
	 * Tires to resolve as much CDO ID to SNOMED&nbsp;CT concept ID mapping as possible from the {@link InternalCDORevisionDelta changed deltas}
	 * and the {@link InternalCDORevision new revisions}.
	 */
	@Override
	protected void preProcess() {
		
		super.preProcess();
		
		final Set<InternalCDORevision> revisions = Sets.newHashSet(Iterables.filter(getChangeSetData().getNewObjects(), InternalCDORevision.class));
		
		final CDOBranch branch = getCurrentView().getBranch();
		final CDOBranchPoint branchPoint = branch.getPoint(getCurrentView().getLastUpdateTime());

		for (final CDORevisionDelta delta : Iterables.filter(getChangeSetData().getChangedObjects(), InternalCDORevisionDelta.class)) {

			//for dirty SNOMED CT concepts
			if (SnomedPackage.eINSTANCE.getConcept().equals(delta.getEClass())) {

				final InternalCDORevision revision = getRevisionManager().getRevision(
						delta.getID(), 
						branchPoint,
						CDORevision.UNCHUNKED,
						CDORevision.DEPTH_NONE, 
						true);
				
				revisions.add(revision);
				
			//for dirty SNOMED CT reference sets
			} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSet().isSuperTypeOf(delta.getEClass())) {
				
				final InternalCDORevision revision = getRevisionManager().getRevision(
						delta.getID(), 
						branchPoint, 
						CDORevision.UNCHUNKED,
						CDORevision.DEPTH_NONE,
						true);
				
				revisions.add(revision);
			} 
			
		}
		
		for (final InternalCDORevision revision : revisions) {
			
			//for SNOMED CT concept CDO ID to concept ID mapping
			if (SnomedPackage.eINSTANCE.getConcept().equals(revision.getEClass())) {
				
				long cdoId = CDOIDUtils.asLong(revision.getID());
				final String conceptId = String.valueOf(revision.getValue(SnomedPackage.eINSTANCE.getComponent_Id()));
				
				storageKeyConceptIds.put(
						cdoId, //concept CDO ID
						conceptId); //SCT ID
				
				//for inverse mapping
				//for creating concept delta -> reference set member change -> reference set identifier ID -> concept SCT ID
				conceptIdStorageKeyIds.put(
						conceptId, //concept SCT ID 
						cdoId); //concept CDO ID
				
			//for SNOMED CT reference set CDO ID to reference set identifier concept ID mapping
			} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSet().equals(revision.getEClass())) {
				
				storageKeyConceptIds.put(
						CDOIDUtils.asLong(revision.getID()), //reference set CDO ID
						String.valueOf(revision.getValue(SnomedRefSetPackage.eINSTANCE.getSnomedRefSet_IdentifierId()))); //identifier concept ID
				
			}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.AbstractHierarchicalComponentDeltaBuilder#processChange(org.eclipse.emf.cdo.common.revision.CDOIDAndVersion, org.eclipse.emf.cdo.view.CDOView, com.b2international.snowowl.datastore.delta.ChangeKind)
	 */
	@Override
	protected void processChange(final CDOIDAndVersion idAndVersion, final CDOView view, final ChangeKind change) {
		
		if (idAndVersion instanceof InternalCDORevision) {
			
			final InternalCDORevision revision = (InternalCDORevision) idAndVersion;
			final EClass eClass = revision.getEClass();
			final long cdoId = CDOIDUtils.asLong(revision.getID());
			
			//ignore new and retired components (if any)
			if (isNew(change)) {
				
				//core component
				if (SnomedPackage.eINSTANCE.getComponent().isSuperTypeOf(eClass)) {
					
					//if inactive, simple do not process component
					final boolean active = (Boolean) revision.getValue(SnomedPackage.eINSTANCE.getComponent_Active());
					if (!active) {
						return;
					}
				}
				
				//reference set member
				if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(eClass)) {
					
					//if member is new but inactive, skip processing
					final boolean active = (Boolean) revision.getValue(SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_Active());
					if (!active) {
						return;
					}
				}
			}
			
			//concepts
			if (SnomedPackage.eINSTANCE.getConcept().equals(eClass)) {
				
				
				final HierarchicalComponentDelta delta = createDelta(revision, change, view);
				put(delta).getRelatedCdoIds().add(cdoId);
				
				
			//descriptions
			} else if (SnomedPackage.eINSTANCE.getDescription().equals(eClass)) {
				
				
				final Object value = revision.getContainerID();
				
				if (value instanceof CDOID) {
					
					final long containerConceptCdoId = CDOIDUtils.asLong((CDOID) value);
					final String containerConceptId = getComponentId(containerConceptCdoId, view);
					
					final HierarchicalComponentDelta delta = createDelta(containerConceptId, containerConceptCdoId, ChangeKind.UPDATED, view);
					put(delta).getRelatedCdoIds().add(cdoId);
					
				} else if (value instanceof Concept) { //in case of new objects in a transaction with temporary IDs 
					
					final HierarchicalComponentDelta delta = createDelta((Concept) value, ChangeKind.UPDATED);
					put(delta).getRelatedCdoIds().add(cdoId);
					
				}
				
				
			//relationships
			} else if (SnomedPackage.eINSTANCE.getRelationship().equals(eClass)) {
				
				
				final Object value = revision.getContainerID();
				
				if (value instanceof CDOID) {
					
					final long containerConceptCdoId = CDOIDUtils.asLong((CDOID) value);
					final String containerConceptId = getComponentId(containerConceptCdoId, view);
					
					final HierarchicalComponentDelta delta = createDelta(containerConceptId, containerConceptCdoId, ChangeKind.UPDATED, view);
					put(delta).getRelatedCdoIds().add(cdoId);
					
				} else if (value instanceof Concept) { //in case of new objects in a transaction with temporary IDs 
					
					final HierarchicalComponentDelta delta = createDelta((Concept) value, ChangeKind.UPDATED);
					put(delta).getRelatedCdoIds().add(cdoId);
					
				}
				
			
			//reference set members
			} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(eClass)) {
				
				final Object value = revision.getContainerID();
				
				if (value instanceof CDOID) {
					
					final long refSetCdoId = CDOIDUtils.asLong((CDOID) value);
					final String refSetIdentifierId = getComponentId(refSetCdoId, view);
					
					final long conceptCdoId = getStorageKey(refSetIdentifierId, view);					
					final HierarchicalComponentDelta delta = createDelta(refSetIdentifierId, conceptCdoId, ChangeKind.UPDATED, view);
					put(delta).getRelatedCdoIds().add(cdoId);
					
				} else if (value instanceof SnomedRefSet) {
					
					final String refSetIdentifierId = ((SnomedRefSet) value).getIdentifierId();
					final long conceptCdoId = getStorageKey(refSetIdentifierId, view);
					
					final HierarchicalComponentDelta delta = createDelta(refSetIdentifierId, conceptCdoId, ChangeKind.UPDATED, view);
					put(delta).getRelatedCdoIds().add(cdoId);
					
				}
				
			}
			//XXX note: reference set member changes intentionally ignored, as it will processed as new concept
			//in case of new reference sets or will be processed on reference set members. other property of the
			//reference set cannot change
			
			return;
		}
		
		final CDOObject object = CDOUtils.getObjectIfExists(view, idAndVersion.getID());
		
		//concept
		if (object instanceof Concept) {
			
			final Concept concept = (Concept) object;
			final HierarchicalComponentDelta delta = createDelta(concept, change);
			put(delta).getRelatedCdoIds().add(CDOIDUtils.asLong(concept.cdoID()));

		
		//description
		} else if (object instanceof Description) {
			
			final Description description = (Description) object;
			final HierarchicalComponentDelta delta = createDelta(description.getConcept(), ChangeKind.UPDATED);
			put(delta).getRelatedCdoIds().add(CDOIDUtils.asLong(description.cdoID()));
			
			
		//relationship
		} else if (object instanceof Relationship) {
			
			final Relationship relationship = (Relationship) object;
			final HierarchicalComponentDelta delta = createDelta(relationship.getSource(), ChangeKind.UPDATED);
			put(delta).getRelatedCdoIds().add(CDOIDUtils.asLong(relationship.cdoID()));

		
		//reference set member
		} else if (object instanceof SnomedRefSetMember) {
			
			final SnomedRefSetMember member = (SnomedRefSetMember) object;
			final EObject container = member.eContainer();
			final Long storageKey = CDOUtils.getStorageKey((CDOObject) container);
			final String conceptId = getComponentId(storageKey, object.cdoView());
			final Long conceptStorageKey = getStorageKey(conceptId, view);
			
			final HierarchicalComponentDelta delta = createDelta(conceptId, conceptStorageKey, ChangeKind.UPDATED, view);
			put(delta).getRelatedCdoIds().add(CDOIDUtils.asLong(member.cdoID()));
			
			
		//reference set member
		} else if (object instanceof SnomedRefSet) {
			
			final SnomedRefSet refSet = (SnomedRefSet) object;
			final String refSetIdentifierId = refSet.getIdentifierId();
			final long conceptCdoId = getStorageKey(refSetIdentifierId, object.cdoView());
			
			final HierarchicalComponentDelta delta = createDelta(refSetIdentifierId, conceptCdoId, ChangeKind.UPDATED, view);
			put(delta).getRelatedCdoIds().add(CDOIDUtils.asLong(object.cdoID()));
			
		}
		
	}

	@Override
	protected SnomedConceptLookupService createLookupService() {
		return new SnomedConceptLookupService();
	}

	@Override
	protected String getParentIdFromCdoObject(final CDOObject cdoObject) {

		if (cdoObject instanceof Concept) {
			
			final Concept concept = (Concept) cdoObject;
			for (final Relationship relationship : concept.getOutboundRelationships()) {
				
				if (!relationship.isActive()) {
					continue;
				}
				
				if (Concepts.IS_A.equals(relationship.getType().getId())) {
					return relationship.getDestination().getId();
				}
			}
		}
		
		return null;
	}
	
	@Override
	protected String getParentIdFromTerminologyBrowser(String id) {
		final LongSet ancestorIds = getTerminologyBrowser().getSuperTypeIds(getBranchPath(), Long.parseLong(id));
		return LongSets.isEmpty(ancestorIds) ? null : Long.toString(ancestorIds.iterator().next());
	}
	
	@Override
	protected AbstractIndexEntry getIndexEntryFromCdoObject(String ancestorId) {
		
		final Concept concept = createLookupService().getComponent(ancestorId, getCurrentView());

		if (null == concept) {
			return null;
		} else {
			return AdapterUtil.adapt(concept, SnomedConceptIndexEntry.class);
		}
	}

	@Override
	protected SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}

	@Override
	protected short getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
	}
	
	@Override
	protected AbstractIndexEntry getIndexEntryFromTerminologyBrowser(final String id) {
		final SnomedConceptIndexEntry concept = (SnomedConceptIndexEntry) super.getIndexEntryFromTerminologyBrowser(id);
		return SnomedConceptIndexEntry.builder(concept).label(getConceptLabel(id, concept.getStorageKey(), getCurrentView())).build();
	}
	
	/**
	 * Creates and returns with a brand new component delta instance based on the specified values. 
	 * @param conceptId the unique SNOMED&nbsp;CT concept ID.
	 * @param cdoId a unique CDO ID. Preferably the CDO ID of the concept. 
	 * @param change the change kind.
	 * @param view the view to resolve missing attributes, if any.
	 * @return the component delta.
	 */
	private HierarchicalComponentDelta createDelta(final String conceptId, final long cdoId, final ChangeKind change, final CDOView view) {
		
		final String label = getConceptLabel(conceptId, cdoId, view);
		
		short terminologyComponentId = getTerminologyComponentId();
		String iconId = new SnomedConceptIconIdProvider().getIconId(BranchPointUtils.create(view), conceptId);
		return new HierarchicalComponentDelta(
				conceptId, 
				cdoId,
				getBranchPath(),
				label,
				iconId != null ? iconId : conceptId,
				terminologyComponentId,
				getCodeSystemOID(terminologyComponentId),
				change);
		
	}

	/*returns with the unique CDO ID of a SNOMED CT concept identified by its unique SCT ID. 
	 *if the storage key was not cached yet, this method will cache it*/
	private long getStorageKey(final String conceptId, final CDOView view) {
		
		Preconditions.checkNotNull(conceptId, "Concept ID argument cannot be null.");
		
		//if inverse mapping does not cached yet due to above described
		//cache inverse mapping
		Long conceptCdoId = conceptIdStorageKeyIds.get(conceptId); /*intentionally object long to avoid NPE on un-boxing*/
		if (null == conceptCdoId) {
			
			final Concept concept = createLookupService().getComponent(conceptId, view);
			conceptCdoId = CDOIDUtils.asLong(concept.cdoID());
			conceptIdStorageKeyIds.put(conceptId, conceptCdoId);
			
		}
		
		return conceptCdoId;
		
	}
	
	/*returns with the SNOMED&nbsp;CT component ID associated with the given CDO ID. 
	 *if the component ID cannot be found among the cached ID pairs, it will be cached.*/
	private String getComponentId(final long cdoId, final CDOView view) {
		
		String componentId = storageKeyConceptIds.get(cdoId);
		
		if (StringUtils.isEmpty(componentId)) {
			
			CDOObject object = CDOUtils.getObjectIfExists(view, cdoId);
			if (object instanceof Concept) {
				
				componentId = ((Concept) object).getId();
				storageKeyConceptIds.put(cdoId, componentId);
				
			} else if (object instanceof SnomedRefSet) {
				
				componentId = ((SnomedRefSet) object).getIdentifierId();
				storageKeyConceptIds.put(cdoId, componentId);
				
			} else if (object instanceof Description || object instanceof Relationship) {
				
				componentId = getComponentId( CDOUtils.getStorageKey((CDOObject)object.eContainer()), view);
				storageKeyConceptIds.put(cdoId, componentId);
				
			}
			
		}
		
		return Preconditions.checkNotNull(componentId, "Cannot find component ID for CDO ID: " + cdoId + ". [" + view + "]");
		
	}
	
	/*returns true if the specified change kind is the 'added'.*/
	private boolean isNew(final ChangeKind change) {
		return ChangeKind.ADDED.equals(change);
	}

	/*creates a component deltas based on the given change kind and internal CDO revision*/
	private HierarchicalComponentDelta createDelta(final InternalCDORevision revision, final ChangeKind change, final CDOView view) {
		
		final String conceptId = String.valueOf(revision.getValue(SnomedPackage.eINSTANCE.getComponent_Id()));
		final long cdoId = CDOIDUtils.asLong(revision.getID());
		final String label = getConceptLabel(conceptId, cdoId, view);
		
		
		
		
		short terminologyComponentId = getTerminologyComponentId();
		String iconId = new SnomedConceptIconIdProvider().getIconId(BranchPointUtils.create(view), conceptId);
		return new HierarchicalComponentDelta(
				conceptId, 
				cdoId,
				getBranchPath(),
				label,
				iconId != null ? iconId : conceptId,
				terminologyComponentId,
				getCodeSystemOID(terminologyComponentId),
				change);
		
	}
	
	/*creates a component deltas based on the given change kind and internal CDO revision*/
	private HierarchicalComponentDelta createDelta(final Concept concept, final ChangeKind change) {
		
		Preconditions.checkNotNull(concept, "SNOMED CT concept argument cannot be null.");
		
		final String conceptId = concept.getId();
		
		final String label = getConceptLabel(conceptId, CDOIDUtil.getLong(concept.cdoID()), concept.cdoView());
		
		final long cdoId = CDOIDUtils.asLong(concept.cdoID());
		
		short terminologyComponentId = getTerminologyComponentId();
		String iconId = new SnomedConceptIconIdProvider().getIconId(BranchPointUtils.create(concept), conceptId);
		return new HierarchicalComponentDelta(
				conceptId, 
				cdoId,
				getBranchPath(),
				label,
				iconId != null ? iconId : conceptId,
				terminologyComponentId,
				getCodeSystemOID(terminologyComponentId),
				change);
		
	}

	/*returns with the PT of a concept. first checks cached components, then index, then CDO, finally falls back to ID*/
	private String getConceptLabel(final String conceptId, final long storageKey, final CDOView view) {

		Preconditions.checkNotNull(conceptId, "Concept ID argument cannot be null.");
		CDOUtils.check(view);
		
		//try to get object from already processed deltas (we can avoid one label lookup)
		String label = null;

		//it does not matter (at least it should not) whether it is a concept or reference set as the PT is the same
		final Iterable<HierarchicalComponentDelta> deltas = get(conceptId);
		if (!CompareUtils.isEmpty(deltas)) {

			final HierarchicalComponentDelta delta = Iterables.get(deltas, 0);
			
			if (null != delta) {
				label = delta.getLabel(); 
			}
		}

		//fall back to CDO
		if (StringUtils.isEmpty(label)) {
			if (view == getBaseView()) {
				CDOObject cdoObject = view.getObject(CDOIDUtil.createLong(storageKey));
				if (cdoObject instanceof Concept) {
					Concept concept = (Concept) cdoObject;
					label = concept.getFullySpecifiedName();
				}
			} else {
				label = currentTextProvider.getText(conceptId);
			}
		}

		//if still empty fall back to ID
		if (StringUtils.isEmpty(label)) {
			label = conceptId;
		}
		return label;
	}
	
	/*returns with the revision manager*/
	private InternalCDORevisionManager getRevisionManager() {
		return getSession().getRevisionManager();
	}

	/*returns with the CDO session*/
	private InternalCDOSession getSession() {
		return (InternalCDOSession) getConnection().getSession();
	}

	/*returns with the connection*/
	private ICDOConnection getConnection() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE);
	}

}