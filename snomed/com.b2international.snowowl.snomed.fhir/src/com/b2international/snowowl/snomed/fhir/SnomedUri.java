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

import java.util.Date;
import java.util.StringTokenizer;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;

/**
 * Snomed CT URI representation
 * 
 * @see <a href="https://confluence.ihtsdotools.org/display/DOCURI">SNOMED CT URI specification</a>
 * 
 * <br>The URI can also contain a query part as described:
 * @see <a href="https://www.hl7.org/fhir/snomedct.html">Using SNOMED CT with FHIR</a> 
 * 
 * @since 6.7
 */
public class SnomedUri {
	
	public static final String VERSION_PATH_SEGMENT = "version"; //$NON-NLS-N$
	public static final String SNOMED_BASE_URI_STRING = "http://snomed.info/sct"; //$NON-NLS-N$
	public static final Uri SNOMED_BASE_URI = new Uri(SNOMED_BASE_URI_STRING);
	public static final Uri SNOMED_INT_CORE_MODULE_URI = new Uri(SNOMED_BASE_URI_STRING + "/" + Concepts.MODULE_SCT_CORE );
	
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
		
		public boolean isValueSetQuery() {
			return PREFIX_VS.equals(queryParameter);
		}
		
		public boolean isConceptMapQuery() {
			return PREFIX_CM.equals(queryParameter);
		}
		
		public String getQueryParameter() {
			return queryParameter;
		}
		
		public QueryPartDefinition getQueryPartDefinition() {
			return queryPartDefinition;
		}
		
		public String getQueryValue() {
			return queryValue;
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
	 * @param parameterName for reporting purposes
	 * @return new SNOMED CT URI instance
	 */
	public static SnomedUri fromUriString(String uriString, String parameterName) {
		
		Builder builder = builder();
		
		if (!uriString.startsWith(SNOMED_BASE_URI_STRING)) {
			throw new BadRequestException(String.format("URI '%s' is not a valid SNOMED CT URI. It should start as '%s'.", uriString, SNOMED_BASE_URI_STRING), parameterName);
		}
		
		String extensionString = uriString.replaceFirst(SNOMED_BASE_URI_STRING, "");
		
		String[] splitUri = extensionString.split("\\?");
		if (splitUri.length > 2) {
			throw new BadRequestException(String.format("Invalid SNOMED CT URI [%s].", uriString), parameterName);
		}
		
		if (splitUri.length == 2) {
			parseQueryPart(builder, uriString, splitUri[1], parameterName);
		}
		
		StringTokenizer tokenizer = new StringTokenizer(splitUri[0], "/");
		
		//this should not happen
		if (!tokenizer.hasMoreTokens()) return builder.build();
		
		//extension
		String pathSegment = tokenizer.nextToken();
			
		//No extension or version definition provided - SNOMED CT INT Edition
		if (StringUtils.isEmpty(pathSegment)) return builder.build();
		
		if (!SnomedIdentifiers.isValid(pathSegment)) {
			throw new BadRequestException(String.format("Invalid extension module ID [%s] defined.", pathSegment), parameterName);
		} else {
			builder.extensionModuleId(pathSegment);
		}
		
		//version parameter
		if (!tokenizer.hasMoreTokens()) return builder.build();
		String versionParameterKeySegment = tokenizer.nextToken();
		if (!VERSION_PATH_SEGMENT.equals(versionParameterKeySegment)) {
			throw new BadRequestException(String.format("Invalid path segment [%s], 'version' expected.", versionParameterKeySegment), parameterName);
		}
		
		//Version tag
		if (!tokenizer.hasMoreTokens()) {
			throw new BadRequestException(String.format("No version tag is specified after the 'version' parameter."), parameterName);
		}
		String versionTag = tokenizer.nextToken();
		//to validate
		try {
			EffectiveTimes.parse(versionTag, DateFormats.SHORT);
		} catch(RuntimeException re) {
			throw new BadRequestException(String.format("Could not parse version date [%s].", versionTag), parameterName);
		}
		return builder.version(versionTag).build();
	}
	
	/*
	 URI: http://snomed.info/sct?fhir_cm=138875005
	 URI: http://snomed.info/sct?fhir_vs
	 URI: http://snomed.info/sct?fhir_vs=isa/138875005
	 URI: http://snomed.info/sct?fhir_vs=refset
	 URI: http://snomed.info/sct?fhir_vs=refset/138875005
	*/
	private static void parseQueryPart(Builder builder, String uriString, String queryPartString, String parameterName) {
		
		//parse VS
		if (queryPartString.startsWith(QueryPart.PREFIX_VS)) {
			String[] queryParts = queryPartString.split("=");
			
			if (queryParts.length == 1) {
				builder.valueSetsQuery();  //fhir_vs
			} else if (queryParts.length == 2) {
				String parameter = queryParts[1];
				
				if (parameter.startsWith(QueryPartDefinition.ISA.getUrlString())) {
					String[] split = parameter.split("/");
					if (split.length == 2) {
						builder.isAQuery(split[1]); //isa/138875005
					} else {
						throw new BadRequestException(String.format("Invalid 'fhir_vs=isa/conceptId' query part [%s] for URI [%s].", queryPartString, uriString), parameterName);
					}
				} else if (parameter.startsWith(QueryPartDefinition.REFSET.getUrlString())) {
					String[] split = parameter.split("/");
					if (split.length == 2) {
						builder.refsetQuery(split[1]);  //refset/138875005
					} else {
						throw new BadRequestException(String.format("Invalid 'fhir_vs=refset/conceptId' query part [%s] for URI [%s].", queryPartString, uriString), parameterName);
					}
				} else if (parameter.startsWith(QueryPartDefinition.REFSETS.getUrlString())) {
					builder.refsetsQuery();  //fhir_vs=refset
				}
			}
			else {
				throw new BadRequestException(String.format("Invalid 'fhir_vs' query part [%s] for URI [%s].", queryPartString, uriString), parameterName); 
			}
			
		} else if (queryPartString.startsWith(QueryPart.PREFIX_CM)) {
			String[] split = queryPartString.split("=");
			
			if (split.length == 2) {
				builder.conceptMapQuery(split[1]); //fhir_cm=138875005
			} else {
				throw new BadRequestException(String.format("Invalid 'fhir_cm' query part [%s] for the URI [%s], the format is '?fhir_cm=conceptId'.", queryPartString, uriString), parameterName);
			}
			
		} else {
			throw new BadRequestException(String.format("Invalid query part [%s] for the URI [%s], it should be either '?fhir_vs' or '?fhir_cm'.", queryPartString, uriString), parameterName);
		}
	}

	public String getExtensionModuleId() {
		return extensionModuleId;
	}
	
	public String getVersionTag() {
		return versionTag;
	}
	
	public boolean hasQueryPart() {
		return queryPart !=null;
	}
	
	/**
	 * @return the queryPart
	 */
	public QueryPart getQueryPart() {
		return queryPart;
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