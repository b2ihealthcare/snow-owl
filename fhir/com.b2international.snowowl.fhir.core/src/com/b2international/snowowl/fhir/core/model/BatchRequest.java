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
package com.b2international.snowowl.fhir.core.model;

import com.b2international.snowowl.fhir.core.codesystems.HttpVerb;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Resource Metadata
 * 
 * @see <a href="https://www.hl7.org/fhir/bundle-definitions.html#Bundle.entry.request">FHIR:Bunlde:Entry:Request</a>
 * @since 8.0.0
 */
public class BatchRequest {
	
	@Mandatory
	@JsonProperty
	private Code method;
	
	@Mandatory
	@JsonProperty
	private Uri url;
	
	@JsonCreator
	public BatchRequest(@JsonProperty("method") final Code method, @JsonProperty("url") final Uri url) {
		this.method = method;
		this.url = url;
	}
	
	public static BatchRequest createGetRequest(String url) {
		return new BatchRequest(HttpVerb.GET.getCode(), new Uri(url));
	}
	
	public static BatchRequest createPostRequest(String url) {
		return new BatchRequest(HttpVerb.POST.getCode(), new Uri(url));
	}
	
	public Code getMethod() {
		return method;
	}
	
	public Uri getUrl() {
		return url;
	}
	
}
