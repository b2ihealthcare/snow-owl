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
package com.b2international.snowowl.snomed.reasoner.server.classification;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.b2international.commons.TimedProgressMonitorWrapper;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils;
import com.b2international.snowowl.datastore.server.remotejobs.AbstractRemoteJob;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationRequest;
import com.b2international.snowowl.snomed.reasoner.model.ConceptDefinition;
import com.b2international.snowowl.snomed.reasoner.server.SnomedReasonerServerActivator;

public class ReasonerRemoteJob extends AbstractRemoteJob {

	private final SnomedReasonerServerService serverService;
	private final ClassificationRequest classificationRequest;
	
	public ReasonerRemoteJob(final SnomedReasonerServerService serverService, final ClassificationRequest classificationRequest) {
		super(MessageFormat.format("Classifying the ontology on {0}", classificationRequest.getSnomedBranchPath()));
		this.serverService = serverService;
		this.classificationRequest = classificationRequest;
	}

	@Override
	protected IStatus runWithListenableMonitor(final IProgressMonitor monitor) {
		final TimedProgressMonitorWrapper wrapper = new TimedProgressMonitorWrapper(monitor);
		wrapper.beginTask(getName() + "...", IProgressMonitor.UNKNOWN);
		
		CollectingServiceReference<Reasoner> reasonerReference = null;
		Exception caughtException = null;
		
		try {
			
			final IBranchPath snomedBranchPath = classificationRequest.getSnomedBranchPath();
			final List<ConceptDefinition> additionalDefinitions = classificationRequest.getAdditionalDefinitions();
			final String lockUserId = classificationRequest.getLockUserId();
			final String parentContextDescription = classificationRequest.getParentContextDescription();
			
			reasonerReference = serverService.takeServiceReference(snomedBranchPath, additionalDefinitions.isEmpty(), classificationRequest);
			final ReasonerTaxonomy reasonerTaxonomy = reasonerReference.getService().classify(lockUserId, parentContextDescription, additionalDefinitions);
			serverService.registerResult(RemoteJobUtils.getRemoteJobId(this), reasonerTaxonomy);
			return Status.OK_STATUS;
			
		} catch (final Exception e) {
			caughtException = e;
			return createExceptionStatus(caughtException);
		} finally {
			wrapper.done();
			
			if (null != reasonerReference) {
				try {
					serverService.retireServiceReference(reasonerReference);
				} catch (final InterruptedException e2) {
					if (null != caughtException) {
						caughtException.addSuppressed(e2);
					} else {
						return createExceptionStatus(e2);			
					}
				}
			}
		}
	}

	private Status createExceptionStatus(final Exception e) {
		return new Status(IStatus.ERROR, SnomedReasonerServerActivator.PLUGIN_ID, "Caught exception while running classification.", e);
	}
}