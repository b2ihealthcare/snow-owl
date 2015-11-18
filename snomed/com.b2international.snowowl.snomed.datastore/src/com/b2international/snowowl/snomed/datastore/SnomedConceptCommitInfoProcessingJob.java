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
package com.b2international.snowowl.snomed.datastore;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.InvalidObjectException;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoProcessingJob;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Represents a SNOMED CT concept editor-specific commit information processing job. 
 *
 */
public class SnomedConceptCommitInfoProcessingJob extends CDOCommitInfoProcessingJob {

	public SnomedConceptCommitInfoProcessingJob(Concept component, CDOTransaction transaction, CDOCommitInfo commitInfo) {
		super(component, transaction, commitInfo);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.CDOCommitInfoProcessingJob#getComponent()
	 */
	@Override
	protected Concept getComponent() {
		return (Concept) super.getComponent();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(final IProgressMonitor monitor) {

		if (null == getCommitInfo()) {
		
			return Status.CANCEL_STATUS;
			
		}
		
		// can happen if an editor is opened and the underlying component has been deleted -> do nothing.
		if (!checkTransaction()) {
			
			return Status.CANCEL_STATUS;
			
		}

		try {
		
			final CDOID conceptCdoID = getComponent().cdoID();
			final String conceptId = getComponent().getId();
			final boolean conceptStatus = getComponent().isActive();
			
			/* 
			 * If we find a new, preferred language reference set member in both local and remote change sets, this is an indication that either
			 * 
			 * a) the FSN has changed, or
			 * b) the preferred term has changed on both ends. We can't merge these cases cleanly, so we report a mid-air collision.
			 */
			final Set<CDOID> remotelyAddedIds = Sets.newHashSet(Lists.transform(getCommitInfo().getNewObjects(), CDOIDUtils.getIdAndVersionToIdFunction()));
			final Set<CDOID> remotelyChangedIds = Sets.newHashSet(Lists.transform(getCommitInfo().getChangedObjects(), CDOIDUtils.getIdAndVersionToIdFunction()));
			
			CDOTransaction cdoTransaction = getTransactionSafe();
			
			if (null == cdoTransaction) {
				return Status.CANCEL_STATUS;
			}
	
			//check if the local transaction has new language reference set members with preferred state
			//this means either FSn or preferred term of the concept has been chanced
			final Iterable<SnomedLanguageRefSetMember> locallyAddedPreferredMembers = Iterables.filter(Iterables.filter(cdoTransaction.getNewObjects().values(), SnomedLanguageRefSetMember.class), new Predicate<SnomedLanguageRefSetMember>() {
				@Override public boolean apply(final SnomedLanguageRefSetMember member) {
					return Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(member.getAcceptabilityId());
				}
			});
			
			Collection<SnomedRefSetMemberIndexEntry> newLanguageMembers = Collections2.filter(new SnomedRefSetMembershipLookupService().getLanguageMembers(getComponent()), new Predicate<SnomedRefSetMemberIndexEntry>() {
				@Override public boolean apply(final SnomedRefSetMemberIndexEntry member) { //filter out not preferred language reference set members
					return Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(member.getAcceptabilityId());
				}
			});
			
			Set<CDOID> newLanguageMemberIds = Sets.newHashSet(Collections2.transform(newLanguageMembers, new Function<SnomedRefSetMemberIndexEntry, CDOID>() {
				@Override public CDOID apply(SnomedRefSetMemberIndexEntry input) {
					return CDOIDUtil.createLong(input.getStorageKey());
				}
			}));
			
			if (!Sets.intersection(newLanguageMemberIds, remotelyAddedIds).isEmpty()) {
				if (Iterables.isEmpty(locallyAddedPreferredMembers)) {
					setAction(CommitInfoAction.MERGE_WITH_REFRESH);	
				} else {
					setAction(CommitInfoAction.CONFLICT);
				}
				return Status.OK_STATUS;
			}
			
			//XXX maybe check IS_A relationship as well
					
			//TODO check description and relationship changes //later
			//description case significance
			//relationship characteristic type
			//modules
			//status
			//relationship CDTs
			
			
			//TODO check if detached. maybe move editor closing on detached object feature to this job
			final SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
			
			if (remotelyChangedIds.contains(conceptCdoID)) {
				//first check concept status since we need CONLIFCTING state.
				
				if (conceptStatus != terminologyBrowser.getConcept(conceptId).isActive() 
						&& !CDOState.CLEAN.equals(getComponent().cdoState())) {
					
					setAction(CommitInfoAction.CONFLICT);
					
				} else {
					
					setAction(CommitInfoAction.MERGE_WITH_REFRESH);
					
				} 
				return Status.OK_STATUS;
			}
			
	
			//check 
			if (CDOState.NEW.equals(getComponent().cdoState())) {
				final CDOID resourceCdoId = getComponent().cdoResource().cdoID();
				if (remotelyChangedIds.contains(resourceCdoId)) {
					setAction(CommitInfoAction.MERGE_NO_REFRESH);
					return Status.OK_STATUS;
				}
			}

		} catch (final InvalidObjectException e) {
			return Status.CANCEL_STATUS;
		}
			
		return Status.CANCEL_STATUS; //this means other job (if any) has to decide if the editor has to change 
	}

}