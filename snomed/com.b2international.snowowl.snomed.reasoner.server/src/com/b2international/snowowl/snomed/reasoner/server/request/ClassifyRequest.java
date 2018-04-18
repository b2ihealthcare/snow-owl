/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.TimedProgressMonitorWrapper;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ApiError;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.remotejobs.RemoteJob;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationSettings;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerService;
import com.b2international.snowowl.snomed.reasoner.model.ConceptDefinition;
import com.b2international.snowowl.snomed.reasoner.server.classification.CollectingServiceReference;
import com.b2international.snowowl.snomed.reasoner.server.classification.Reasoner;
import com.b2international.snowowl.snomed.reasoner.server.classification.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.reasoner.server.classification.SnomedReasonerServerService;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.7
 */
public class ClassifyRequest implements Request<ServiceProvider, ApiError> {

	private static final Logger LOG = LoggerFactory.getLogger(ClassifyRequest.class);
	
	@JsonProperty
	private final ClassificationSettings settings;

	public ClassifyRequest(ClassificationSettings settings) {
		this.settings = settings;
	}
	
	@JsonProperty
	public String getReasonerId() {
		return settings.getReasonerId();
	}
	
	@JsonProperty
	public String getBranch() {
		return settings.getBranchPath();
	}

	@Override
	public ApiError execute(ServiceProvider context) {
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

			IBranchPath snomedBranchPath = BranchPathUtils.createPath(settings.getBranchPath());
			List<ConceptDefinition> additionalDefinitions = settings.getAdditionalDefinitions();
			String parentContextDescription = settings.getParentContextDescription();

			reasonerReference = serverService.takeServiceReference(snomedBranchPath, additionalDefinitions.isEmpty(), settings);
			ReasonerTaxonomy reasonerTaxonomy = reasonerReference.getService().classify(userId, parentContextDescription, additionalDefinitions);
			serverService.registerTaxonomyBuilder(classificationId, reasonerReference.getService().getTaxonomyBuilder().get());
			serverService.registerResult(classificationId, reasonerTaxonomy);
			return new ApiError.Builder("OK").code(200).build();
		} catch (Exception e) {
			caughtException = e;
			return createApiError(caughtException);
		} finally {
			wrapper.done();

			if (null != reasonerReference) {
				try {
					serverService.retireServiceReference(reasonerReference);
				} catch (InterruptedException e2) {
					if (null != caughtException) {
						caughtException.addSuppressed(e2);
					} else {
						return createApiError(e2);			
					}
				}
			}
		}
	}

	private static ApiError createApiError(final Exception e) {
		LOG.error("Caught exception while running classification.", e);
		return new ApiError.Builder("Caught exception while running classification.").code(500).build();
	}

}
