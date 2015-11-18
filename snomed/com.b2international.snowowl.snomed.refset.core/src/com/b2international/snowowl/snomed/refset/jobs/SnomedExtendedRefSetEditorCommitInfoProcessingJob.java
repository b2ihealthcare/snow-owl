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
package com.b2international.snowowl.snomed.refset.jobs;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoProcessingJob;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedConceptCommitInfoProcessingJob;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.collect.Sets;

/**
 * Job for processing CDO commit informations triggered by remote changes. Determines if the
 * remote changes effects a SNOMED&nbsp;CT reference set and a concept. 
 * @see CDOCommitInfoProcessingJob
 */
public class SnomedExtendedRefSetEditorCommitInfoProcessingJob extends SnomedRefSetEditorCommitInfoProcessingJob {

	private final Concept concept;
	
	/**
	 * Creates a new instance of this job.
	 * @param component the component to investigate.
	 * @param transaction the underlying CDO transaction.
	 * @param commitInfo the CDO commit info triggered by a remote CDO invalidation event. Can be {@code null}.
	 * @param snomedRefSetMemberIndexEntry the edited reference set member. Can be {@code null} if there is no selection.
	 */
	public SnomedExtendedRefSetEditorCommitInfoProcessingJob(final SnomedRefSet component, final CDOTransaction transaction, 
			@Nullable final CDOCommitInfo commitInfo, @Nullable final SnomedRefSetMemberIndexEntry snomedRefSetMemberIndexEntry) {
		super(component, transaction, commitInfo);
		concept = null == commitInfo ? null : getConcept(snomedRefSetMemberIndexEntry); //nothing to calculate if the commit info is null
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.ui.editor.CDOCommitInfoProcessingJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {

		if (Status.OK_STATUS.equals(super.run(monitor))) {
			return Status.OK_STATUS;
		}
		
		
		if (null != concept) { //check the selected concept (if any) first.
			final CDOTransaction transaction = getTransactionSafe();
			if (null != transaction) {
				final SnomedConceptCommitInfoProcessingJob conceptProcessingJob = new SnomedConceptCommitInfoProcessingJob(concept, transaction, getCommitInfo());
				final IStatus status = conceptProcessingJob.run(monitor);
				if (status.isOK()) { //the concept has changes, we have to refresh the whole editor
					setAction(conceptProcessingJob.getAction());
					return status;
				}
			}
		}
		
		return Status.CANCEL_STATUS;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.CDOCommitInfoProcessingJob#getComponent()
	 */
	@Override
	protected SnomedRegularRefSet getComponent() {
		return (SnomedRegularRefSet) super.getComponent();
	}
	
	/*extracts the SNOMED CT concept from the specified reference set member
	 * may return with null if the reference set member is null
	 * or the reference set member is referencing a *NON* concept*/
	@Nullable private Concept getConcept(final SnomedRefSetMemberIndexEntry member) {
		if (null == member) {
			return null;
		}
		
		if (!SnomedTerminologyComponentConstants.CONCEPT.equals(member.getReferencedComponentType())) {
			return null; //not a concept
		}
		
		final String conceptId = member.getReferencedComponentId();
		
		final SnomedConceptIndexEntry concept = getTerminologyBrowser().getConcept(conceptId);
		
		if (null == concept) { //our concept is not a persisted one, we have to check it in the transaction

			final CDOTransaction transaction = getTransactionSafe();
			if (null == transaction) {
				return null;
			}
			for (final Concept newConcept : Sets.newHashSet(ComponentUtils2.getNewObjects(transaction, Concept.class))) {
				if  (conceptId.equals(newConcept.getId())) {
					return newConcept;
				}
			}
			
			return null;
			
		} else {
			final CDOTransaction transaction = getTransactionSafe();
			if (null == transaction) {
				return null;
			}
			return new SnomedConceptLookupService().getComponent(conceptId, transaction);
		}
	}
	
	/*returns with the terminology browser for SNOMED CT terminology*/
	private SnomedClientTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
	}
	
}