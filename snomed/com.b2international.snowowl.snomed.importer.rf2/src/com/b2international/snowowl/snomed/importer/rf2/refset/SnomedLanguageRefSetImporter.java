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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.BaseCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.internal.cdo.view.AbstractCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOSavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ift.CellProcessor;

import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongSet;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.commons.pcj.LongSets.LongFunction;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.importer.rf2.csv.AssociatingRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ParseUuid;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.importer.rf2.terminology.ComponentLookup;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class SnomedLanguageRefSetImporter extends AbstractSnomedRefSetImporter<AssociatingRefSetRow, SnomedLanguageRefSetMember> {

	/**
	 * CDO root resource name for storing SNOMED&nbsp;CT language type reference set members before committing them.
	 */
	private static final String TEMPORARY_LANGUAGE_MEMBER_ROOT_RESOURCE_NAME = "temporary_language_member_root";
	
	private static final Map<String, CellProcessor> CELL_PROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(AssociatingRefSetRow.PROP_UUID, new ParseUuid())
			.put(AssociatingRefSetRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(AssociatingRefSetRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(AssociatingRefSetRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(AssociatingRefSetRow.PROP_REF_SET_ID, NullObjectPattern.INSTANCE)
			.put(AssociatingRefSetRow.PROP_REFERENCED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.put(AssociatingRefSetRow.PROP_ASSOCIATED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.build();
	
	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER_IDX1000", "SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER_IDX1001", "SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER_IDX1002", "SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER_IDX1003", "SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER_IDX1004", "SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER", "ACCEPTABILITYID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();

	private static final SnomedImportConfiguration<AssociatingRefSetRow> IMPORT_CONFIGURATION = new SnomedImportConfiguration<AssociatingRefSetRow>(
			ComponentImportType.LANGUAGE_TYPE_REFSET, 
			CELL_PROCESSOR_MAPPING, 
			AssociatingRefSetRow.class, 
			SnomedRf2Headers.LANGUAGE_TYPE_HEADER,
			INDEXES);

	private final List<SnomedLanguageRefSetMember> newMembers;

	public SnomedLanguageRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier) {
		super(IMPORT_CONFIGURATION, importContext, releaseFileStream, releaseFileIdentifier);
		newMembers = Lists.newArrayList();
	}

	@Override
	protected SnomedRefSetType getRefSetType() {
		return SnomedRefSetType.LANGUAGE;
	}

	@Override
	protected SnomedLanguageRefSetMember doImportRow(final AssociatingRefSetRow currentRow) {

		final SnomedLanguageRefSetMember editedMember = getOrCreateMember(currentRow.getUuid());
		
		if (skipCurrentRow(currentRow, editedMember)) {
			return null;
		}

		if (currentRow.getEffectiveTime() != null) {
			editedMember.setEffectiveTime(currentRow.getEffectiveTime());
			editedMember.setReleased(true);
		} else {
			editedMember.unsetEffectiveTime();
		}

		editedMember.setRefSet(getOrCreateRefSet(currentRow.getRefSetId(), currentRow.getReferencedComponentId()));
		editedMember.setActive(currentRow.isActive());
		editedMember.setModuleId(currentRow.getModuleId());
		editedMember.setReferencedComponentId(currentRow.getReferencedComponentId());
		editedMember.setAcceptabilityId(currentRow.getAssociatedComponentId());
		
		return editedMember;
	}

	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_LANGUAGE_TYPE;
	}

	@Override
	protected SnomedLanguageRefSetMember createRefSetMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedLanguageRefSetMember();
	}
	
	/**
	 * Member will be added to a list instead. We will deal with the proper container at {@link #preCommit(InternalCDOTransaction)}.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	protected boolean addToMembersList(final SnomedLanguageRefSetMember currentMember) {
		
		//object with dirty state already contained in their proper container, we do not have to set the
		//XXX right here we have to consider the followings: referenced component ID has been changed
		//for the language member, but we will deal with it in the #preCommit
		//there we have to check whether a dirty member has CDOSetFeature delta for the 'referencedComponentId' feature
		//if so we will update its container.
		if (CDOState.TRANSIENT.equals(currentMember.cdoState())) {
		
			final SnomedEditingContext editingContext = getImportContext().getEditingContext();
			final CDOTransaction transaction = editingContext.getTransaction();
			final CDOResource resource = transaction.getOrCreateResource(TEMPORARY_LANGUAGE_MEMBER_ROOT_RESOURCE_NAME);
			
			resource.getContents().add(currentMember);
			
			newMembers.add(currentMember);
			
		}
		
		return true;
	}
	
	@Override
	protected SnomedRefSet createUninitializedRefSet(final String identifierConceptId) {
		return SnomedRefSetFactory.eINSTANCE.createSnomedStructuralRefSet();
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.importer.rf2.model.AbstractSnomedImporter#preCommit(org.eclipse.emf.spi.cdo.InternalCDOTransaction)
	 */
	@Override
	protected void preCommit(final InternalCDOTransaction transaction) throws SnowowlServiceException {
		
		if (CompareUtils.isEmpty(newMembers)) {
			return; //nothing to do. could happen when transaction has dirty objects but new ones
		}
		
		final ComponentLookup<Component> componentLookup = getImportContext().getComponentLookup();
		
		//remove all unrelated description IDs
		final LongSet referencedDescriptionIds = LongSets.newLongSet(LongSets.transform(newMembers, new LongFunction<SnomedLanguageRefSetMember>() {
			@Override public long apply(final SnomedLanguageRefSetMember member) {
				return Long.parseLong(getReferencedComponentId(member));
			}
		}));
		
		
		final LongSet descriptionStorageKeys = LongSets.newLongSet(LongSets.transform(LongSets.toStringSet(referencedDescriptionIds), new LongFunction<String>() {
			@Override public long apply(final String member) {
				return componentLookup.getComponentStorageKey(member);
			}
		})); 
		
		final List<CDOID> ids = CDOIDUtils.getIds(descriptionStorageKeys);
		final List<CDORevision> revisions = CDOServerUtils.getRevisions(
				BranchPointUtils.convert(BranchPointUtils.create(transaction)), 
				ids);
		
		
		if (null == revisions) {
			
			String message = "Cannot load revision for descriptions from store.";
			log("SNOMED CT import failed. " + message);
			throw new SnowowlServiceException(message);
			
		}
		
		//mapping between SNOMED CT description storage key and the CDO revision
		final LongKeyMap storageKeyToRevisionMap = new LongKeyOpenHashMap();
		for (final Iterator<CDORevision> itr = revisions.iterator(); itr.hasNext(); /**/) {
			
			final InternalCDORevision revision = (InternalCDORevision) itr.next();
			
			storageKeyToRevisionMap.put(CDOIDUtils.asLong(revision.getID()), revision);
			
		}
		
		//process all new members
		for (final SnomedLanguageRefSetMember newMember : newMembers) {
			
			final long descriptionStorageKey = componentLookup.getComponentStorageKey(getReferencedComponentId(newMember));
			final Object object = storageKeyToRevisionMap.get(descriptionStorageKey);
			
			final BaseCDORevision memberRevision = (BaseCDORevision) newMember.cdoRevision();
			final InternalCDORevision descriptionRevision = 
					(InternalCDORevision) Preconditions.checkNotNull(object, "Cannot find description revision. CDO ID: " + descriptionStorageKey);
			
			memberRevision.setContainerID(descriptionRevision.getID());
			memberRevision.setContainingFeatureID(InternalEObject.EOPPOSITE_FEATURE_BASE - SnomedPackage.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS);
			memberRevision.setResourceID(CDOID.NULL);
			
			final InternalCDOSavepoint lastSavepoint = transaction.getLastSavepoint();
			
			//get revision delta for description
			InternalCDORevisionDelta revisionDelta = (InternalCDORevisionDelta) lastSavepoint.getRevisionDeltas().get(descriptionRevision.getID());
			
	    if (null == revisionDelta) {
	    
	    	//create empty revision delta
        revisionDelta = (InternalCDORevisionDelta) CDORevisionUtil.createDelta(descriptionRevision);
        //register it
        lastSavepoint.getRevisionDeltas().put(descriptionRevision.getID(), revisionDelta);
        
      	
      }

	    //get current language reference set members for description
	    final CDOList cdoList = descriptionRevision.getList(SnomedPackage.eINSTANCE.getDescription_LanguageRefSetMembers());
	    
	    //create add feature delta with the new member
	    revisionDelta.addFeatureDelta(createAddRevisionDelta(newMember, cdoList.size()));
		}
		
		final CDOResource resource = transaction.getOrCreateResource(TEMPORARY_LANGUAGE_MEMBER_ROOT_RESOURCE_NAME);
		
		//remove temporary resource from new objects
		transaction.getLastSavepoint().getNewObjects().remove(resource.cdoID());
		//remove object by its CDO ID from the CDO view as well to avoid conflicting temp CDO IDs
		((AbstractCDOView) transaction).removeObject(resource.cdoID());
		
		//un-register dirty root-root resource
		transaction.getLastSavepoint().getDirtyObjects().remove(transaction.getRootResource().cdoID());
		
		//un-register root-root CDO resource changes
		transaction.getLastSavepoint().getRevisionDeltas().remove(transaction.getRootResource().cdoID()); 
		
		//clear members
		newMembers.clear();
		
	}

	//creates a new ADD CDO feature delta with the given reference set member and index
	private CDOAddFeatureDelta createAddRevisionDelta(final SnomedLanguageRefSetMember newMember, final int index) {
		return new CDOAddFeatureDeltaImpl(SnomedPackage.eINSTANCE.getDescription_LanguageRefSetMembers(), index, newMember);
	}

	/*returns with the referenced component ID of the member either from the object or from the CDO settings*/
	private String getReferencedComponentId(final SnomedLanguageRefSetMember member) {
		
		String id = member.getReferencedComponentId();
		
		if (StringUtils.isEmpty(id)) {
			
			id = CDOUtils.getAttribute(member, SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_ReferencedComponentId(), String.class);
			
		}
		
		return Preconditions.checkNotNull(id, "Referenced component ID was null for reference set member.  Member: " + member);
		
	}
	
}
