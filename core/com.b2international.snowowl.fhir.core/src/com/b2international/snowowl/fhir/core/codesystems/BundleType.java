/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.codesystems;

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;

/**
 * FHIR Bundle type code system
 * 
 * @since 6.3
 */
public enum BundleType implements FhirCodeSystem {
	
	//The bundle is a document. The first resource is a Composition
	DOCUMENT("Document"),
	
	//The bundle is a message. The first resource is a MessageHeader
	MESSAGE("Message"), 
	
	//The bundle is a transaction - intended to be processed by a server as an atomic commit.
	TRANSACTION("Transaction"), 
	
	//Transaction Response	The bundle is a transaction response. 
	//Because the response is a transaction response, the transaction has succeeded, and all responses are error free.
	TRANSACTION_RESPONSE("Transaction Response"),
	
	//The bundle is a transaction - intended to be processed by a server as a group of actions.
	BATCH("Batch"),
	
	//The bundle is a batch response. Note that as a batch, some responses may indicate failure and others success.
	BATCH_RESPONSE("Batch Response"),
	
	//The bundle is a list of resources from a history interaction on a server.
	HISTORY("History List"),
	
	//Search Results	The bundle is a list of resources returned as a result of a search/query interaction, operation, or message.
	SEARCHSET("Search Results"),
	
	//The bundle is a set of resources collected into a single package for ease of distribution.
	COLLECTION("Collection");
	
	public final static String CODE_SYSTEM_URI = "https://www.hl7.org/fhir/codesystem-bundle-type.html";
	
	private String displayName;

	private BundleType(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public Code getCode() {
		return new Code(getCodeValue());
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase().replaceAll("_", "-");
	}
	
	@Override
	public Uri getUri() {
		return new Uri(CODE_SYSTEM_URI + "/" + getCodeValue());
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}

}
