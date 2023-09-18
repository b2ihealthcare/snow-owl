/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.r5;

import static com.b2international.snowowl.fhir.core.model.r5.FhirDataTypeConverter.toFhirBundleType;
import static com.b2international.snowowl.fhir.core.model.r5.FhirDataTypeConverter.toFhirDataType;
import static com.b2international.snowowl.fhir.core.model.r5.FhirDataTypeConverter.toFhirInstant;
import static com.b2international.snowowl.fhir.core.model.r5.FhirDataTypeConverter.toFhirUri;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.model.*;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Parameter;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;

/**
 * @since 9.0
 */
public class FhirBundleConverter {

	public static org.hl7.fhir.r5.model.Bundle toFhirBundle(final Bundle coreBundle) {
		if (coreBundle == null) {
			return null;
		}

		final var fhirBundle = new org.hl7.fhir.r5.model.Bundle();

		final Code coreBundleType = coreBundle.getType();
		if (coreBundleType != null) {
			fhirBundle.setType(toFhirBundleType(coreBundleType));
		}

		final Meta coreMeta = coreBundle.getMeta();
		if (coreMeta != null) {
			final var fhirMeta = new org.hl7.fhir.r5.model.Meta();

			final Instant coreLastUpdated = coreMeta.getLastUpdated();
			fhirMeta.setLastUpdatedElement(toFhirInstant(coreLastUpdated));

			if (!fhirMeta.isEmpty()) {
				fhirBundle.setMeta(fhirMeta);
			}
		}

		final Collection<Entry> coreEntries = coreBundle.getEntry();
		if (coreEntries != null) {
			for (final Entry coreEntry : coreEntries) {
				final var fhirEntry = toFhirEntry(coreEntry);
				if (fhirEntry != null) { 
					fhirBundle.addEntry(fhirEntry);
				}
			}
		}

		fhirBundle.setTotal(coreBundle.getTotal());
		fhirBundle.setUserData(ResourceConstants.CURRENT_PAGE_ID, coreBundle.getCurrentPageId());
		fhirBundle.setUserData(ResourceConstants.NEXT_PAGE_ID, coreBundle.getNextPageId());

		return fhirBundle;
	}

	private static org.hl7.fhir.r5.model.Bundle.BundleEntryComponent toFhirEntry(final Entry coreEntry) {
		if (coreEntry == null) {
			return null;
		}

		final var fhirEntry = new org.hl7.fhir.r5.model.Bundle.BundleEntryComponent();

		if (coreEntry instanceof final RequestEntry requestEntry) {
			return toFhirRequestEntry(fhirEntry, requestEntry);
		} else if (coreEntry instanceof final ParametersRequestEntry parametersRequestEntry) {
			return toFhirParametersRequestEntry(fhirEntry, parametersRequestEntry);
		} else if (coreEntry instanceof final ResourceRequestEntry resourceRequestEntry) {
			return toFhirResourceRequestEntry(fhirEntry, resourceRequestEntry);
		} else if (coreEntry instanceof final OperationOutcomeEntry operationOutcomeEntry) {
			return toFhirOperationOutcomeEntry(fhirEntry, operationOutcomeEntry);
		} else if (coreEntry instanceof final ParametersResponseEntry parametersResponseEntry) {
			return toFhirParametersResponseEntry(fhirEntry, parametersResponseEntry);
		} else if (coreEntry instanceof final ResourceResponseEntry resourceResponseEntry) {
			return toFhirResourceResponseEntry(fhirEntry, resourceResponseEntry);
		} else {
			return null;
		}
	}

	private static org.hl7.fhir.r5.model.Bundle.BundleEntryComponent toFhirRequestEntry(
		final org.hl7.fhir.r5.model.Bundle.BundleEntryComponent fhirEntry, 
		final RequestEntry coreEntry
	) {
		populateRequestParameters(fhirEntry, coreEntry.getRequest());
		return fhirEntry;
	}

	private static org.hl7.fhir.r5.model.Bundle.BundleEntryComponent toFhirParametersRequestEntry(
		final org.hl7.fhir.r5.model.Bundle.BundleEntryComponent fhirEntry, 
		final ParametersRequestEntry coreEntry
	) {
		populateRequestParameters(fhirEntry, coreEntry.getRequest());
		fhirEntry.setResource(toFhirParameters(coreEntry.getRequestResource()));
		return fhirEntry;
	}

	private static org.hl7.fhir.r5.model.Bundle.BundleEntryComponent toFhirResourceRequestEntry(
		final org.hl7.fhir.r5.model.Bundle.BundleEntryComponent fhirEntry,
		final ResourceRequestEntry coreEntry
	) {
		populateRequestParameters(fhirEntry, coreEntry.getRequest());
		fhirEntry.setResource(FhirResourceConverter.toFhirResource(coreEntry.getRequestResource()));
		return fhirEntry;
	}

	private static org.hl7.fhir.r5.model.Bundle.BundleEntryComponent toFhirOperationOutcomeEntry(
		final org.hl7.fhir.r5.model.Bundle.BundleEntryComponent fhirEntry,
		final OperationOutcomeEntry coreEntry
	) {
		populateResponseParameters(fhirEntry, coreEntry.getResponse());
		fhirEntry.setResource(FhirResourceConverter.toFhirOperationOutcome(coreEntry.getOperationOutcome()));
		return fhirEntry;
	}

	private static org.hl7.fhir.r5.model.Bundle.BundleEntryComponent toFhirParametersResponseEntry(
		final org.hl7.fhir.r5.model.Bundle.BundleEntryComponent fhirEntry,
		final ParametersResponseEntry coreEntry
	) {
		populateResponseParameters(fhirEntry, coreEntry.getResponse());
		fhirEntry.setResource(toFhirParameters(coreEntry.getResponseResource()));
		return fhirEntry;
	}

	private static org.hl7.fhir.r5.model.Bundle.BundleEntryComponent toFhirResourceResponseEntry(
		final org.hl7.fhir.r5.model.Bundle.BundleEntryComponent fhirEntry,			
		final ResourceResponseEntry coreEntry
	) {
		populateResponseParameters(fhirEntry, coreEntry.getResponse());
		fhirEntry.setResource(FhirResourceConverter.toFhirResource(coreEntry.getResponseResource()));
		return fhirEntry;
	}

	private static org.hl7.fhir.r5.model.Bundle.BundleEntryComponent populateRequestParameters(
		final org.hl7.fhir.r5.model.Bundle.BundleEntryComponent fhirEntry, 
		final BatchRequest coreRequest
	) {
		if (coreRequest == null) {
			return fhirEntry;
		}

		final var fhirRequest = new org.hl7.fhir.r5.model.Bundle.BundleEntryRequestComponent(); 

		final Uri url = coreRequest.getUrl();
		if (url != null) {
			fhirRequest.setUrlElement(toFhirUri(url));
		}

		final Code method = coreRequest.getMethod();
		if (method != null) {
			fhirRequest.setMethod(org.hl7.fhir.r5.model.Bundle.HTTPVerb.fromCode(method.getCodeValue()));
		}

		if (!fhirRequest.isEmpty()) {
			fhirEntry.setRequest(fhirRequest);
		}

		return fhirEntry;
	}

	private static org.hl7.fhir.r5.model.Bundle.BundleEntryComponent populateResponseParameters(
		final org.hl7.fhir.r5.model.Bundle.BundleEntryComponent fhirEntry, 
		final BatchResponse coreResponse
	) {
		if (coreResponse == null) {
			return fhirEntry;
		}

		final var fhirResponse = new org.hl7.fhir.r5.model.Bundle.BundleEntryResponseComponent(); 

		final String status = coreResponse.getStatus();
		if (!StringUtils.isEmpty(status)) {
			fhirResponse.setStatus(status);
		}

		if (!fhirResponse.isEmpty()) {
			fhirEntry.setResponse(fhirResponse);
		}

		return fhirEntry;
	}
	
	public static org.hl7.fhir.r5.model.Parameters toFhirParameters(final Fhir coreParameters) {
		if (coreParameters == null) {
			return null;
		}
		
		final List<Parameter> coreParameterList = coreParameters.getParameters();
		if (coreParameterList == null) {
			return null;
		}
		
		final var fhirParameters = new org.hl7.fhir.r5.model.Parameters();
		
		for (final Parameter coreParameter : coreParameterList) {
			final var fhirParameter = toFhirParameter(coreParameter);
			if (fhirParameter != null && !fhirParameter.isEmpty()) {
				fhirParameters.addParameter(fhirParameter);
			}
		}
				
		if (!fhirParameters.isEmpty()) {
			return fhirParameters;
		} else {
			return null;
		}
	}

	private static org.hl7.fhir.r5.model.Parameters.ParametersParameterComponent toFhirParameter(final Parameter coreParameter) {
		if (coreParameter == null) {
			return null;
		}
		
		final var fhirParameter = new org.hl7.fhir.r5.model.Parameters.ParametersParameterComponent();
		
		final String coreName = coreParameter.getName();
		if (coreName != null) {
			fhirParameter.setName(coreName);
		}
		
		final Object coreValue = coreParameter.getValue();
		if (coreValue != null) {
			fhirParameter.setValue(toFhirDataType(coreValue));
		}
		
		return fhirParameter;
	}
}
