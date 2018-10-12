/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.valueset;

import java.util.Date;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * This class represents a FHIR ValueSet$expand operation request.
 * 
 * @see <a href="https://www.hl7.org/fhir/valueset-operations.html#expand">FHIR:ValueSet:Operations:expand</a>
 * @since 6.9
 */
@JsonDeserialize(builder = ExpandValueSetRequest.Builder.class)
@JsonPropertyOrder({"url", "valueSet", "context", "filter", "profile", "date", "offset", "count", "includeDesignations", "includeDefinition", "activeOnly", "excludeNested", 
	"excludeNotForUI", "excludePostCoordinated", "displayLanguage", "limitedExpansion"})
public class ExpandValueSetRequest {
	
	//Value set Canonical URL. The server must know the value set (e.g. it is defined explicitly in the server's value sets, or it is defined implicitly by some code system known to the server
	private final Uri url;
	
	private final ValueSet valueSet;

	private final Uri context;
	
	private final String filter;
	
	private final Uri profile;
	
	private final Date date;
	
	private final Integer offset;
	
	private final Integer count;
	
	private final Boolean includeDesignations;
	
	private final Boolean includeDefinition;
	
	private final Boolean activeOnly;
	
	private final Boolean excludeNested;
	
	private final Boolean excludeNotForUI;
	
	private final Boolean excludePostCoordinated;

	private final Code displayLanguage;
	
	private final Boolean limitedExpansion;
	
	ExpandValueSetRequest(
		Uri url,
		ValueSet valueSet,
		Uri context,
		String filter,
		Uri profile,
		Date date,
		Integer offset,
		Integer count,
		Boolean includeDesignations,
		Boolean includeDefinition,
		Boolean activeOnly,
		Boolean excludeNested,
		Boolean excludeNotForUI,
		Boolean excludePostCoordinated,
		Code displayLanguage,
		Boolean limitedExpansion) {
		
		this.url = url;
		this.valueSet = valueSet;
		this.context = context;
		this.filter = filter;
		this.profile = profile;
		this.date = date;
		this.offset = offset;
		this.count = count;
		this.includeDesignations = includeDesignations;
		this.includeDefinition = includeDefinition;
		this.activeOnly = activeOnly;
		this.excludeNested = excludeNested;
		this.excludeNotForUI = excludeNotForUI;
		this.excludePostCoordinated = excludePostCoordinated;
		this.displayLanguage = displayLanguage;
		this.limitedExpansion = limitedExpansion;
	}
	
	public Uri getUrl() {
		return url;
	}
	
	public ValueSet getValueSet() {
		return valueSet;
	}
	
	public Uri getContext() {
		return context;
	}
	
	public String getFilter() {
		return filter;
	}

	public Uri getProfile() {
		return profile;
	}

	public Date getDate() {
		return date;
	}

	public Integer getOffset() {
		return offset;
	}

	public Integer getCount() {
		return count;
	}

	public Boolean getIncludeDesignations() {
		return includeDesignations;
	}

	public Boolean getIncludeDefinition() {
		return includeDefinition;
	}

	public Boolean getActiveOnly() {
		return activeOnly;
	}

	public Boolean getExcludeNested() {
		return excludeNested;
	}

	public Boolean getExcludeNotForUI() {
		return excludeNotForUI;
	}

	public Boolean getExcludePostCoordinated() {
		return excludePostCoordinated;
	}

	public Code getDisplayLanguage() {
		return displayLanguage;
	}

	public Boolean getLimitedExpansion() {
		return limitedExpansion;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder extends ValidatingBuilder<ExpandValueSetRequest> {

		private Uri url;
		private ValueSet valueSet;
		private Uri context;
		private String filter;
		private Uri profile;
		private Date date;
		private Integer offset;
		private Integer count;
		private Boolean includeDesignations;
		private Boolean includeDefinition;
		private Boolean activeOnly;
		private Boolean excludeNested;
		private Boolean excludeNotForUI;
		private Boolean excludePostCoordinated;
		private Code displayLanguage;
		private Boolean limitedExpansion;
		
		Builder() {}
		
		public Builder url(final Uri url) {
			this.url = url;
			return this;
		}
		
		public Builder url(final String urlString) {
			this.url = new Uri(urlString);
			return this;
		}
		
		public Builder valueSet(final ValueSet valueSet) {
			this.valueSet = valueSet;
			return this;
		}
		
		public Builder context(final Uri context) {
			this.context = context;
			return this;
		}
		
		public Builder filter(final String filter) {
			this.filter = filter;
			return this;
		}
		
		public Builder profile(final Uri profile) {
			this.profile = profile;
			return this;
		}
		
		public Builder date(String date) {
			try {
				this.date = Dates.parse(date, FhirConstants.DATE_TIME_FORMAT);
			} catch (SnowowlRuntimeException e) {
				throw new BadRequestException("Incorrect date format '%s'.", date);
			}
			return this;
		}
		
		public Builder offset(final Integer offset) {
			this.offset = offset;
			return this;
		}
		
		public Builder count(final Integer count) {
			this.count = count;
			return this;
		}
		
		public Builder includeDesignations(final Boolean includeDesignations) {
			this.includeDesignations = includeDesignations;
			return this;
		}
		
		public Builder includeDefinition(final Boolean includeDefinition) {
			this.includeDefinition = includeDefinition;
			return this;
		}
		
		public Builder activeOnly(final Boolean activeOnly) {
			this.activeOnly = activeOnly;
			return this;
		}
		
		public Builder excludeNested(final Boolean excludeNested) {
			this.excludeNested = excludeNested;
			return this;
		}
		
		public Builder excludeNotForUI(final Boolean excludeNotForUI) {
			this.excludeNotForUI = excludeNotForUI;
			return this;
		}
		
		public Builder excludePostCoordinated(final Boolean excludePostCoordinated) {
			this.excludePostCoordinated = excludePostCoordinated;
			return this;
		}
		
		public Builder displayLanguage(final Code displayLanguage) {
			this.displayLanguage = displayLanguage;
			return this;
		}
		
		public Builder limitedExpansion(final Boolean limitedExpansion) {
			this.limitedExpansion = limitedExpansion;
			return this;
		}
		
		@Override
		protected ExpandValueSetRequest doBuild() {
			return new ExpandValueSetRequest(url, valueSet, context, filter, profile, date, offset, count, includeDesignations, 
					includeDefinition, activeOnly, excludeNested, excludeNotForUI, excludePostCoordinated, displayLanguage, limitedExpansion);
		}

	}
	
}
