/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.rest;

import javax.servlet.http.HttpServletRequest;

import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * {@link FhirBatchRequestProcessor} to process an unknown requests in a batch
 * @since 8.0.0
 */
public class FhirUnknowRequestEntryProcessor extends FhirBatchRequestProcessor {

	private Entry entry;

	public FhirUnknowRequestEntryProcessor(ObjectMapper objectMapper, Entry entry, FhirBundleController batchRequestController) {
		super(objectMapper, batchRequestController);
		this.entry = entry;
	}

	@Override
	public void doProcess(ArrayNode arrayNode, HttpServletRequest request) {
		
		OperationOutcome operationOutcome = OperationOutcome.builder()
				.addIssue(Issue.builder()
						.code(IssueType.NOT_SUPPORTED)
						.addExpression("Request type not supported: + " + entry.getClass())
						.build())
				.build();
		
		OperationOutcomeEntry ooEntry = OperationOutcomeEntry.builder()
				.operationOutcome(operationOutcome)
				.build();
		
		ObjectNode resourceNode = (ObjectNode) objectMapper.valueToTree(ooEntry);
		BatchResponse batchResponse = new BatchResponse(String.valueOf("500"));
		JsonNode responseNode = objectMapper.valueToTree(batchResponse);
		resourceNode.putPOJO("response", responseNode);
		arrayNode.add(resourceNode);

	}

}
