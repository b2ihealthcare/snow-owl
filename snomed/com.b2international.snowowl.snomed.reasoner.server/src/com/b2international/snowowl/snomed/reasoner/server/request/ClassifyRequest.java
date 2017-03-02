/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.server.request;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.b2international.commons.TimedProgressMonitorWrapper;
import com.b2international.commons.status.SerializableStatus;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJob;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationSettings;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerService;
import com.b2international.snowowl.snomed.reasoner.model.ConceptDefinition;
import com.b2international.snowowl.snomed.reasoner.server.SnomedReasonerServerActivator;
import com.b2international.snowowl.snomed.reasoner.server.classification.CollectingServiceReference;
import com.b2international.snowowl.snomed.reasoner.server.classification.Reasoner;
import com.b2international.snowowl.snomed.reasoner.server.classification.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.reasoner.server.classification.SnomedReasonerServerService;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.7
 */
public class ClassifyRequest implements Request<ServiceProvider, IStatus> {

	private final ClassificationSettings settings;

	public ClassifyRequest(ClassificationSettings settings) {
		this.settings = settings;
	}
	
	@JsonProperty
	public ClassificationSettings getSettings() {
		return settings;
	}

	@Override
	public IStatus execute(ServiceProvider context) {
		RemoteJob job = context.service(RemoteJob.class);
		IProgressMonitor monitor = context.service(IProgressMonitor.class);
		SnomedReasonerServerService serverService = (SnomedReasonerServerService) context.service(SnomedReasonerService.class);
		String userId = job.getUser();
		String classificationId = job.getId();

		TimedProgressMonitorWrapper wrapper = new TimedProgressMonitorWrapper(monitor);
		wrapper.beginTask(job.getName() + "...", IProgressMonitor.UNKNOWN);

		CollectingServiceReference<Reasoner> reasonerReference = null;
		Exception caughtException = null;

		try {

			IBranchPath snomedBranchPath = settings.getSnomedBranchPath();
			List<ConceptDefinition> additionalDefinitions = settings.getAdditionalDefinitions();
			String parentContextDescription = settings.getParentContextDescription();

			reasonerReference = serverService.takeServiceReference(snomedBranchPath, additionalDefinitions.isEmpty(), settings);
			ReasonerTaxonomy reasonerTaxonomy = reasonerReference.getService().classify(userId, parentContextDescription, additionalDefinitions);
			serverService.registerResult(classificationId, reasonerTaxonomy);
			return SerializableStatus.OK_STATUS;

		} catch (Exception e) {
			caughtException = e;
			return createErrorStatus(caughtException);
		} finally {
			wrapper.done();

			if (null != reasonerReference) {
				try {
					serverService.retireServiceReference(reasonerReference);
				} catch (InterruptedException e2) {
					if (null != caughtException) {
						caughtException.addSuppressed(e2);
					} else {
						return createErrorStatus(e2);			
					}
				}
			}
		}
	}

	private static IStatus createErrorStatus(final Exception e) {
		return new SerializableStatus(IStatus.ERROR, SnomedReasonerServerActivator.PLUGIN_ID, "Caught exception while running classification.", e);
	}
}
