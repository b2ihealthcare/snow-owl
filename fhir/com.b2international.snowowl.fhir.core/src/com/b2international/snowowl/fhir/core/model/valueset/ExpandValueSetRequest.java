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

import java.util.Collection;
import java.util.Date;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

/**
 * This class represents a FHIR ValueSet$expand operation request.
 * 
 * @see <a href="https://www.hl7.org/fhir/valueset-operations.html#expand">FHIR:ValueSet:Operations:expand</a>
 * @since 6.9
 */
@JsonDeserialize(builder = ExpandValueSetRequest.Builder.class)
@JsonPropertyOrder({"url", "valueSet", "valueSetVersion", "context", "contextDirection", "filter", "date", "offset", "count", "includeDesignations", "designation", "includeDefinition", "activeOnly", "excludeNested", 
	"excludeNotForUI", "excludePostCoordinated", "displayLanguage", "excludeSystem", "systemVersion", "checkSystemVersion", "forceSystemVersion" })
public class ExpandValueSetRequest {
	
	//Value set Canonical URL. The server must know the value set (e.g. it is defined explicitly in the server's value sets, or it is defined implicitly by some code system known to the server
	private final Uri url;
	
	private final ValueSet valueSet;
	
	//The identifier that is used to identify a specific version of the value set to be used when generating the expansion.
	private final String valueSetVersion;

	private final Uri context;
	
	/*
	 * If a context is provided, a context direction may also be provided. Valid values are:
	 * 'incoming' - the codes a client can use for PUT/POST operations, and
	 * 'outgoing' -  the codes a client might receive from the server.
	 */
	private final Code contextDirection;
	
	private final String filter;
	
	private final Date date;
	
	private final Integer offset;
	
	private final Integer count;
	
	private final Boolean includeDesignations;
	
	/*
	 * A token that specifies a system+code that is either a use or a language. 
	 * Designations that match by language or use are included in the expansion.
	 * If no designation is specified, it is at the server discretion which designations to return. 
	 */
	private final Collection<String> designation;
	
	private final Boolean includeDefinition;
	
	private final Boolean activeOnly;
	
	private final Boolean excludeNested;
	
	private final Boolean excludeNotForUI;
	
	private final Boolean excludePostCoordinated;

	private final Code displayLanguage;
	
	private final Uri excludeSystem;
	
	private final Uri systemVersion;

	private final Uri checkSystemVersion;

	private final Uri forceSystemVersion;
	
	ExpandValueSetRequest(
		Uri url,
		ValueSet valueSet,
		String valueSetVersion,
		Uri context,
		Code contextDirection,
		String filter,
		Date date,
		Integer offset,
		Integer count,
		Boolean includeDesignations,
		Collection<String> designation,
		Boolean includeDefinition,
		Boolean activeOnly,
		Boolean excludeNested,
		Boolean excludeNotForUI,
		Boolean excludePostCoordinated,
		Code displayLanguage,
		Uri excludeSystem,
		Uri systemVersion,
		Uri checkSystemVersion,
		Uri forceSystemVersion) {
		
		this.url = url;
		this.valueSet = valueSet;
		this.valueSetVersion = valueSetVersion;
		this.context = context;
		this.contextDirection = contextDirection;
		this.filter = filter;
		this.date = date;
		this.offset = offset;
		this.count = count;
		this.includeDesignations = includeDesignations;
		this.designation = designation;
		this.includeDefinition = includeDefinition;
		this.activeOnly = activeOnly;
		this.excludeNested = excludeNested;
		this.excludeNotForUI = excludeNotForUI;
		this.excludePostCoordinated = excludePostCoordinated;
		this.displayLanguage = displayLanguage;
		this.excludeSystem = excludeSystem;
		this.systemVersion = systemVersion;
		this.checkSystemVersion = checkSystemVersion;
		this.forceSystemVersion = forceSystemVersion;
	}
	
	public Uri getUrl() {
		return url;
	}
	
	public ValueSet getValueSet() {
		return valueSet;
	}
	
	public String getValueSetVersion() {
		return valueSetVersion;
	}
	
	public Uri getContext() {
		return context;
	}
	
	public Code getContextDirection() {
		return contextDirection;
	}
	
	public String getFilter() {
		return filter;
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
	
	public Collection<String> getDesignations() {
		return designation;
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
	
	public Uri getExcludeSystem() {
		return excludeSystem;
	}
	
	public Uri getSystemVersion() {
		return systemVersion;
	}
	
	public Uri getCheckSystemVersion() {
		return checkSystemVersion;
	}
	
	public Uri getForceSystemVersion() {
		return forceSystemVersion;
	}

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder extends ValidatingBuilder<ExpandValueSetRequest> {

		private Uri url;
		private ValueSet valueSet;
		private String valueSetVersion;
		private Uri context;
		private Code contextDirection;
		private String filter;
		private Date date;
		private Integer offset;
		private Integer count;
		private Boolean includeDesignations;
		private ImmutableList.Builder<String> designations = ImmutableList.builder();
		private Boolean includeDefinition;
		private Boolean activeOnly;
		private Boolean excludeNested;
		private Boolean excludeNotForUI;
		private Boolean excludePostCoordinated;
		private Code displayLanguage;
		private Uri excludeSystem;
		private Uri systemVersion;
		private Uri checkSystemVersion;
		private Uri forceSystemVersion;
		
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

		public Builder valueSetVersion(final String valueSetVersion) {
			this.valueSetVersion = valueSetVersion;
			return this;
		}
		
		public Builder context(final Uri context) {
			this.context = context;
			return this;
		}

		public Builder contextDirection(final Code contextDirection) {
			this.contextDirection = contextDirection;
			return this;
		}
		
		public Builder contextDirection(final String contextDirection) {
			this.contextDirection = new Code(contextDirection);
			return this;
		}
		
		public Builder filter(final String filter) {
			this.filter = filter;
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
		
		public Builder addDesignation(String designation) {
			designations.add(designation);
			return this;
		}
		
		public void addDesignations(Collection<String> additionalDesignations) {
			designations.addAll(additionalDesignations);
		}
		
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder designation(Collection<String> designationCollection) {
			designations = ImmutableList.builder();
			designations.addAll(designationCollection);
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
		
		public Builder excludeSystem(final Uri excludeSystem) {
			this.excludeSystem = excludeSystem;
			return this;
		}
		
		public Builder systemVersion(final Uri systemVersion) {
			this.systemVersion = systemVersion;
			return this;
		}
		
		public Builder checkSystemVersion(final Uri checkSystemVersion) {
			this.checkSystemVersion = checkSystemVersion;
			return this;
		}
		
		public Builder forceSystemVersion(final Uri forceSystemVersion) {
			this.forceSystemVersion = forceSystemVersion;
			return this;
		}
		
		@Override
		protected ExpandValueSetRequest doBuild() {
			return new ExpandValueSetRequest(url, valueSet, valueSetVersion, context, contextDirection, filter, date, offset, count, includeDesignations, designations.build(),
					includeDefinition, activeOnly, excludeNested, excludeNotForUI, excludePostCoordinated, displayLanguage, excludeSystem, systemVersion, checkSystemVersion, forceSystemVersion);
		}

	}
	
}
