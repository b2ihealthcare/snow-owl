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

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoProcessingJob;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Job for processing CDO commit informations triggered by remote changes on a SNOMED&nbsp;CT reference set.
 *
 */
public class SnomedRefSetEditorCommitInfoProcessingJob extends CDOCommitInfoProcessingJob {

	
	/**
	 * Creates a new job processing SNOMED&nbsp;CT reference set related changes for editor.
	 * @param component the SNOMED&nbsp;CT reference set.
	 * @param transaction the underlying CDO transaction.
	 * @param commitInfo commit info describing the changes.
	 */
	public SnomedRefSetEditorCommitInfoProcessingJob(final SnomedRefSet component, final CDOTransaction transaction, final CDOCommitInfo commitInfo) {
		super(component, transaction, commitInfo);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		if (null == getCommitInfo()) {
			return Status.CANCEL_STATUS; //nothing to do;
		}
		
		final CDOID refSetCdoId = getComponent().cdoID();
		final Set<CDOID> remotelyChangedIds = Sets.newHashSet(Lists.transform(getCommitInfo().getChangedObjects(), CDOIDUtils.getIdAndVersionToIdFunction()));
		
		if (remotelyChangedIds.contains(refSetCdoId)) { //reference set changed
			setAction(CommitInfoAction.MERGE_WITH_REFRESH); //editor has to refreshed
			return Status.OK_STATUS;
		}
		
		final CDOTransaction cdoTransaction = getTransactionSafe();
		if (null != cdoTransaction) {

			if (getCommitInfo().getDetachedObjects().size() > 0) {

				final Iterable<CDORevisionDelta> deltas = Iterables.filter(getCommitInfo().getChangedObjects(), CDORevisionDelta.class);

				final Set<CDORevisionDelta> resources = Sets.newHashSet(Iterables.filter(deltas, new Predicate<CDORevisionDelta>() {
					@Override public boolean apply(CDORevisionDelta revisionDelta) {
						return EresourcePackage.eINSTANCE.getCDOResource().equals(revisionDelta.getEClass());
					}
				}));

				if (!CompareUtils.isEmpty(resources)) {

					final Set<String> classNames = CoreTerminologyBroker.getInstance().getClassesForComponentId(((SnomedRefSet) getComponent()).getReferencedComponentType());

					for (final CDORevisionDelta delta : resources) {

						final CDOObject object = CDOUtils.getObjectIfExists(cdoTransaction, delta.getID());

						if (object instanceof CDOResource) {

							final CDOResource cdoResource = (CDOResource) object;

							if (!CompareUtils.isEmpty(cdoResource.getContents())) {

								final EObject firstElement = cdoResource.getContents().get(0);

								if (firstElement instanceof CDOObject) {

									if (classNames.contains(firstElement.eClass().getInstanceClass().getName())) {

										setAction(CommitInfoAction.MERGE_WITH_REFRESH); //editor has to refreshed
										return Status.OK_STATUS;

									}

								}

							}

						}

					}

				}
			}
			
		}
		
		
		//if the changed object is a reference set member we have to check if the underlying reference set is its container
		for (final CDORevisionKey revisionKey : getCommitInfo().getChangedObjects()) {
			if (revisionKey instanceof InternalCDORevisionDelta) {
				final InternalCDORevisionDelta delta = (InternalCDORevisionDelta) revisionKey;
				final EClass eClass = delta.getEClass();
				if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(eClass)) { //we check the class first and we do not have to load object if not necessary
					
					final SnomedRefSetPackage epackage = SnomedRefSetPackage.eINSTANCE;
					if (epackage.getSnomedLanguageRefSetMember().equals(eClass)) {
						continue; //we do not care about language member changes
					} else if (epackage.getSnomedConcreteDataTypeRefSetMember().equals(eClass)) {
						continue; //it is a CDT keep searching
					} else if (epackage.getSnomedAssociationRefSetMember().equals(eClass)) {
						continue; //we do not care about association members either
					}
					
					final CDOTransaction transaction = getTransactionSafe();
					if (null != transaction) {
						
						if (!delta.getID().isTemporary()) { //persisted reference set member
							final CDOObject cdoObject = transaction.getObject(delta.getID());
							final EObject container = cdoObject.eContainer();
							
							if (container instanceof CDOObject) {
								final CDOObject cdoContainer = (CDOObject) container;
								
								if (cdoContainer.cdoID().equals(refSetCdoId)) {
									setAction(CommitInfoAction.MERGE_WITH_REFRESH); //editor has to refreshed
									return Status.OK_STATUS;
								}
							}
						}
						
					}
					
				}
				
			}
		}
		
		return Status.CANCEL_STATUS;
	}

}