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
package com.b2international.snowowl.fhir.core.search;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.dt.Coding.Builder;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;

/**
 * Search request result parameter keys
 * @since 6.4
 */
public class SearchRequestParameters {
	
	public enum SearchRequestParameterKey  {
		
		_id,
		_lastUpdated,
		_tag,
		_profile,
		_security,
		_text,
		_content,
		_list,
		_has,
		_type,
		_query,
		
		//result parameters
		//TODO: probably should be moved somewhere else
		_sort,
		_count,
		_include,
		_revinclude,
		_summary,
		_elements, 
		_contained,
		_containedType;

		public static SearchRequestParameterKey fromRequestParameter(String requestParam) {
			return valueOf(requestParam.toLowerCase());
		}
	}

	private final String id;
	private final SummaryParameter summary;

	/**
	 * @param id
	 * @param summary 
	 */
	public SearchRequestParameters(String id, SummaryParameter summary) {
		this.id = id;
		this.summary = summary;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String id;
		private SummaryParameter summary;

		public Builder id(final String id) {
			this.id = id;
			return this;
		}
		
		public Builder summary(final SummaryParameter summaryParameter) {
			this.summary = summaryParameter;
			return this;
		}
		
		public Builder summary(final String summaryParameter) {
			this.summary = SummaryParameter.valueOf(summaryParameter);
			return this;
		}
		
		public SearchRequestParameters build() {
			return new SearchRequestParameters(id, summary);
		}
		
	}
	

}
