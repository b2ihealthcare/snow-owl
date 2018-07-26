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
package com.b2international.snowowl.snomed.fhir;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;

/**
 * Snomed CT URI representation
 * 
 * @see <a href="https://confluence.ihtsdotools.org/display/DOCURI">SNOMED CT URI specification</a>
 * @since 6.7
 */
public class SnomedUri {
	
	public static final String VERSION_PATH_SEGMENT = "version"; //$NON-NLS-N$
	public static final String SNOMED_BASE_URI_STRING = "http://snomed.info/sct"; //$NON-NLS-N$
	public static final Uri SNOMED_BASE_URI = new Uri(SNOMED_BASE_URI_STRING);
	
	public enum QueryPartDefinition {
		
		NONE(""),
		ISA("isa/"),
		REFSETS("refset"),
		REFSET("refset/");
		
		private String urlString;

		private QueryPartDefinition(String urlString) {
			this.urlString = urlString;
		}
		
		public String getUrlString() {
			return urlString;
		}
		
	}
	
	/**
	 * VS
	 * 	?fhir_vs - all Concept IDs in the edition/version. If the base URI is http://snomed.info/sct, this means all possible SNOMED CT concepts
	 *	?fhir_vs=isa/[sctid] - all concept IDs that are subsumed by the specified Concept.
	 *	?fhir_vs=refset - all concept ids that correspond to real references sets defined in the specified SNOMED CT edition
	 *	?fhir_vs=refset/[sctid] - all concept IDs in the specified reference set
	 *	
	 * CM
	 *	?fhir_cm=[sctid] - where [sctid] is a value from the table above
	 *
	 */
	public static class QueryPart {
		
		private static final String PREFIX_VS = "fhir_vs";
		private static final String PREFIX_CM = "fhir_cm";
		
		private String queryParameter;
		private QueryPartDefinition queryPartDefinition = QueryPartDefinition.NONE;
		private String queryValue;
		
		public QueryPart(String queryParameter, QueryPartDefinition queryPartDefinition, String conceptId) {
			this.queryParameter = queryParameter;
			this.queryPartDefinition = queryPartDefinition;
			this.queryValue = conceptId;
		}

		
		public QueryPart(String queryParameter, QueryPartDefinition queryPartDefinition) {
			this(queryParameter, queryPartDefinition, null);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (queryParameter != null) {
				sb.append(queryParameter);
				if (queryParameter.equals(PREFIX_CM)) {
					sb.append("=");
				}
			}
			
			if (queryPartDefinition != QueryPartDefinition.NONE) {
				sb.append("=");
				sb.append(queryPartDefinition.getUrlString());
			}

			if (queryValue!=null) {
				sb.append(queryValue);
			}
			
			return sb.toString();
		}

	}
	
	private final String extensionModuleId;
	private final String versionTag;
	
	private QueryPart queryPart;
	
	SnomedUri(String extensionModuleId, String version, QueryPart queryPart) {
		this.extensionModuleId = extensionModuleId;
		this.versionTag = version;
		this.queryPart = queryPart;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		
		private String extensionModuleId;
		private String version;
		private QueryPart queryPart;
		
		public Builder extensionModuleId(final String extensionModuleId) {
			this.extensionModuleId = extensionModuleId;
			return this;
		}

		public Builder version(final String version) {
			this.version = version;
			return this;
		}
		
		public Builder version(final long versionMs) {
			Date date = EffectiveTimes.toDate(versionMs);
			this.version = EffectiveTimes.format(date, DateFormats.SHORT);
			return this;
		}
		
		public Builder conceptMapQuery(String conceptId) {
			this.queryPart = new QueryPart(QueryPart.PREFIX_CM, QueryPartDefinition.NONE, conceptId);
			return this;
		}
		
		public Builder valueSetsQuery() {
			this.queryPart = new QueryPart(QueryPart.PREFIX_VS, QueryPartDefinition.NONE);
			return this;
		}
		
		public Builder refsetsQuery() {
			this.queryPart = new QueryPart(QueryPart.PREFIX_VS, QueryPartDefinition.REFSETS);
			return this;
		}
		
		public Builder isAQuery(String conceptId) {
			this.queryPart = new QueryPart(QueryPart.PREFIX_VS, QueryPartDefinition.ISA, conceptId);
			return this;
		}
		
		public Builder refsetQuery(String conceptId) {
			this.queryPart = new QueryPart(QueryPart.PREFIX_VS, QueryPartDefinition.REFSET, conceptId);
			return this;
		}

		public SnomedUri build() {
			return new SnomedUri(extensionModuleId, version, queryPart);
		}
	}
	
	/**
	 * Factory method to create a new SNOMED CT URI from a valid URI String.
	 * @param uriString
	 * @return new SNOMED CT URI instance
	 */
	public static SnomedUri fromUriString(String uriString) {
		
		Builder builder = builder();
		
		Path uriPath = Paths.get(uriString);
		
		if (!uriPath.startsWith(SNOMED_BASE_URI_STRING)) {
			throw new IllegalArgumentException(String.format("URI '%s' is not a valid SNOMED CT URI. It should start as '%s'.", uriString, SNOMED_BASE_URI_STRING));
		}
		
		Path relativeUri = Paths.get(SNOMED_BASE_URI_STRING).relativize(uriPath);
		
		Iterator<Path> pathIterator = relativeUri.iterator();
	
		//this should not happen
		if (!pathIterator.hasNext()) return builder.build();
		
		//extension
		String pathSegment = pathIterator.next().toString();
			
		//No extension or version definition provided - SNOMED CT INT Edition
		if (StringUtils.isEmpty(pathSegment)) return builder.build();
		
		if (!SnomedIdentifiers.isValid(pathSegment.toString())) {
			throw new IllegalArgumentException(String.format("Invalid extension module ID [%s] defined.", pathSegment));
		} else {
			builder.extensionModuleId(pathSegment);
		}
		
		//version parameter
		if (!pathIterator.hasNext()) return builder.build();
		String versionParameterKeySegment = pathIterator.next().toString();
		if (!VERSION_PATH_SEGMENT.equals(versionParameterKeySegment)) {
			throw new IllegalArgumentException(String.format("Invalid path segment [%s], 'version' expected.", versionParameterKeySegment));
		}
		
		//Version tag
		if (!pathIterator.hasNext()) {
			throw new IllegalArgumentException(String.format("No version tag is specified after the 'version' parameter."));
		}
		String versionTag = pathIterator.next().toString();
		//to validate
		try {
			EffectiveTimes.parse(versionTag, DateFormats.SHORT);
		} catch(RuntimeException re) {
			throw new IllegalArgumentException(String.format("Could not parse version date [%s].", versionTag), re);
		}
		return builder.version(versionTag).build();
	}
	
	public String getExtensionModuleId() {
		return extensionModuleId;
	}
	
	public String getVersionTag() {
		return versionTag;
	}
	
	/**
	 * Returns the standard URI string for this SNOMED CT URI
	 * @return
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(SNOMED_BASE_URI_STRING);
		if (extensionModuleId !=null) {
			sb.append("/");
			sb.append(extensionModuleId);
		}
		if (versionTag !=null) {
			sb.append("/version/");
			sb.append(versionTag);
		}
		
		if (queryPart != null) {
			sb.append("?");
			sb.append(queryPart.toString());
		}
		return sb.toString();
	}
	
	/**
	 * Returns the standard FHIR URI for this SNOMED CT URI
	 * @return
	 */
	public Uri toUri() {
		return new Uri(toString());
	}
	
}