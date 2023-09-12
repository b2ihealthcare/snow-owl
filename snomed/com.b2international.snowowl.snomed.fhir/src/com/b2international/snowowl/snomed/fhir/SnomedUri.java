/*
 * Copyright 2018-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_URI_SCT;

import java.time.LocalDate;
import java.util.List;

import org.hl7.fhir.r5.model.UriType;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.google.common.base.Splitter;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;

/**
 * SNOMED CT URI representation
 * 
 * @see <a href="https://confluence.ihtsdotools.org/display/DOCURI">SNOMED CT URI specification</a>
 * @see <a href="https://terminology.hl7.org/SNOMEDCT.html#snomed-ct-implicit-value-sets">SNOMED CT Implicit Value Sets</a> 
 * @see <a href="https://terminology.hl7.org/SNOMEDCT.html#snomed-ct-implicit-concept-maps">SNOMED CT Implicit Concept Maps</a> 
 * 
 * @since 6.7
 */
public class SnomedUri {

	// Limit is set so that at most one extra segment is returned, just so we can tell if there are too many of them
	private static final Splitter PATH_SPLITTER = Splitter.on('/').limit(4);
	private static final Splitter QUERY_SPLITTER = Splitter.on('?').limit(3);
	private static final Splitter VALUE_SPLITTER = Splitter.on('=').limit(3);

	public static final String VERSION_PATH_SEGMENT = "version"; //$NON-NLS-N$
	public static final UriType SNOMED_BASE_URI = new UriType(SNOMED_URI_SCT);
	public static final UriType SNOMED_INT_CORE_URI = new UriType(SNOMED_URI_SCT + "/" + Concepts.MODULE_SCT_CORE);

	public enum QueryPartDefinition {

		ALL_CONCEPTS("") {
			@Override
			protected boolean matches(final String parameter) {
				return getUrlString().equals(parameter);
			}
		},

		DESCENDANTS_OF("isa/"),

		REFERENCE_SET_IDENTIFIERS("refset") {
			@Override
			protected boolean matches(final String parameter) {
				return getUrlString().equals(parameter);
			}
		},

		REFERENCE_SET_MEMBERS("refset/"),

		ECL_CONSTRAINT("ecl/");

		private String urlString;

		private QueryPartDefinition(final String urlString) {
			this.urlString = urlString;
		}

		public String getUrlString() {
			return urlString;
		}

		public String getValue(final String parameter) {
			return parameter.substring(urlString.length());
		}

		protected boolean matches(final String parameter) {
			return parameter.startsWith(urlString);
		}

		public static QueryPartDefinition getByParameter(final String parameter) {
			for (final QueryPartDefinition definition : QueryPartDefinition.values()) {
				if (definition.matches(parameter)) {
					return definition;
				}
			}

			return null;
		}
	}

	/**
	 * Supported query parameters for implicit value set URLs:
	 *  
	 * ...?fhir_vs - all Concept IDs in the edition/version. If the base URI is http://snomed.info/sct, this means all possible SNOMED CT concepts
	 * ...?fhir_vs=isa/[sctid] - all concept IDs that are subsumed by the specified Concept.
	 * ...?fhir_vs=refset - all concept ids that correspond to real references sets defined in the specified SNOMED CT edition
	 * ...?fhir_vs=refset/[sctid] - all concept IDs in the specified reference set
	 *	
	 * Supported query parameters for implicit concept map URLs:
	 * 
	 * ...?fhir_cm=[sctid] - where [sctid] is a historical association or simple map type reference set
	 */
	public static class QueryPart {

		private static final String PREFIX_VS = "fhir_vs";
		private static final String PREFIX_CM = "fhir_cm";

		private final String queryParameter;
		private QueryPartDefinition queryPartDefinition = QueryPartDefinition.ALL_CONCEPTS;
		private final String queryValue;

		public QueryPart(final String queryParameter, final QueryPartDefinition queryPartDefinition, final String queryValue) {
			this.queryParameter = queryParameter;
			this.queryPartDefinition = queryPartDefinition;
			this.queryValue = queryValue;
		}

		public QueryPart(final String queryParameter, final QueryPartDefinition queryPartDefinition) {
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
			final StringBuilder sb = new StringBuilder();

			if (queryParameter != null) {
				sb.append(queryParameter);
			}

			if (queryValue != null) {
				sb.append("=");
			}

			sb.append(queryPartDefinition.getUrlString());

			if (queryValue != null) {
				sb.append(queryValue);
			}

			return sb.toString();
		}
	}

	private final String edition;
	private final String version;
	private final QueryPart queryPart;

	SnomedUri(final String edition, final String version, final QueryPart queryPart) {
		this.edition = edition;
		this.version = version;
		this.queryPart = queryPart;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String edition;
		private String version;
		private QueryPart queryPart;

		public Builder edition(final String edition) {
			this.edition = edition;
			return this;
		}

		public Builder version(final String version) {
			this.version = version;
			return this;
		}

		public Builder version(final long timestamp) {
			final LocalDate date = EffectiveTimes.toDate(timestamp);
			return version(EffectiveTimes.format(date, DateFormats.SHORT));
		}

		private Builder queryPart(final QueryPart queryPart) {
			this.queryPart = queryPart;
			return this;
		}

		public Builder conceptMapQuery(final String refSetId) {
			return queryPart(new QueryPart(QueryPart.PREFIX_CM, QueryPartDefinition.ALL_CONCEPTS, refSetId));
		}

		public Builder allConceptsQuery() {
			return queryPart(new QueryPart(QueryPart.PREFIX_VS, QueryPartDefinition.ALL_CONCEPTS));
		}

		public Builder allReferenceSetsQuery() {
			return queryPart(new QueryPart(QueryPart.PREFIX_VS, QueryPartDefinition.REFERENCE_SET_IDENTIFIERS));
		}

		public Builder descendantsOfQuery(final String conceptId) {
			return queryPart(new QueryPart(QueryPart.PREFIX_VS, QueryPartDefinition.DESCENDANTS_OF, conceptId));
		}

		public Builder valueSetQuery(final String refSetId) {
			return queryPart(new QueryPart(QueryPart.PREFIX_VS, QueryPartDefinition.REFERENCE_SET_MEMBERS, refSetId));
		}

		public SnomedUri build() {
			return new SnomedUri(edition, version, queryPart);
		}
	}

	/**
	 * Factory method to create a new SNOMED CT URI from a valid URI String.
	 * @param uriString
	 * @return new SNOMED CT URI instance
	 */
	public static SnomedUri fromUriString(final String uriString) {
		final SnomedUri.Builder builder = SnomedUri.builder();

		if (!uriString.startsWith(SNOMED_URI_SCT)) {
			throw new InvalidRequestException(String.format("URI '%s' is not a valid SNOMED CT URI. It should start with '%s'.", uriString, SNOMED_URI_SCT));
		}

		final String uriWithoutBase = uriString.substring(SNOMED_URI_SCT.length());
		final List<String> uriParts = QUERY_SPLITTER.splitToList(uriWithoutBase);
		if (uriParts.size() > 2) {
			throw new InvalidRequestException(String.format("Invalid SNOMED CT URI [%s].", uriString));
		}

		if (uriParts.size() == 2) {
			final String queryPart = uriParts.get(1);
			parseQueryPart(builder, uriString, queryPart);
		}

		final String pathPart = uriParts.get(0);
		final List<String> pathSegments = PATH_SPLITTER.splitToList(pathPart);

		if (pathSegments.isEmpty()) {
			// This should not happen
			return builder.build();
		}

		// Edition identifier is based on the most dependent module ID
		final String edition = pathSegments.get(0);

		if (StringUtils.isEmpty(edition)) {
			// No extension or version definition provided assume SNOMED CT INT Edition
			return builder.build();
		}

		if (!SnomedIdentifiers.isValid(edition)) {
			throw new InvalidRequestException(String.format("Invalid SNOMED CT module ID '%s' in URI '%s'.", edition, uriString));
		} else {
			builder.edition(edition);
		}

		if (pathSegments.size() < 2) {
			return builder.build();
		}

		// "version" path segment should follow
		final String separator = pathSegments.get(1);
		if (!VERSION_PATH_SEGMENT.equals(separator)) {
			throw new InvalidRequestException(String.format("Invalid path segment '%s', 'version' expected in URI '%s'.", separator, uriString));
		}

		// Finally, a version identifier
		if (pathSegments.size() != 3) {
			throw new InvalidRequestException(String.format("No version tag is specified after the 'version' path segment in URI '%s'.", uriString));
		}

		final String version = pathSegments.get(2);

		try {
			EffectiveTimes.parse(version, DateFormats.SHORT);
			builder.version(version);
		} catch (final RuntimeException re) {
			throw new InvalidRequestException(String.format("Could not parse version tag '%s' to a date in URI '%s'.", version, uriString));
		}


		return builder.build();
	}

	/*
	 * Example URIs that should be parsed successfully:
	 * 
	 * - http://snomed.info/sct?fhir_cm=138875005
	 * - http://snomed.info/sct?fhir_vs
	 * - http://snomed.info/sct?fhir_vs=isa/138875005
	 * - http://snomed.info/sct?fhir_vs=refset
	 * - http://snomed.info/sct?fhir_vs=refset/138875005
	 */
	private static void parseQueryPart(final Builder builder, final String uriString, final String queryPartString) {

		if (queryPartString.startsWith(QueryPart.PREFIX_VS)) {
			parseVsQueryPart(builder, uriString, queryPartString);
		} else if (queryPartString.startsWith(QueryPart.PREFIX_CM)) {
			parseCmQueryPart(builder, uriString, queryPartString);
		} else {
			throw new InvalidRequestException(String.format("Invalid query part '%s' for URI '%s', it should start with either '?fhir_vs' or '?fhir_cm'.", queryPartString, uriString));
		}
	}

	private static void parseVsQueryPart(final Builder builder, final String uriString, final String queryPartString) {
		final List<String> queryParts = VALUE_SPLITTER.splitToList(queryPartString);
	
		switch (queryParts.size()) {
			case 1:
				builder.allConceptsQuery();
				break;
	
			case 2:
				final String parameter = queryParts.get(1);
				final QueryPartDefinition definition = QueryPartDefinition.getByParameter(parameter);
				if (definition == null) {
					throw new InvalidRequestException(String.format("Unrecognized 'fhir_vs' query part '%s' for URI '%s'.", queryPartString, uriString)); 
				}
	
				final String value = definition.getValue(parameter);

				switch (definition) {
					case DESCENDANTS_OF:
						if (StringUtils.isEmpty(value)) {
							throw new InvalidRequestException(String.format("Invalid 'fhir_vs=isa/conceptId' query part '%s' for URI '%s'.", queryPartString, uriString));
						} else {
							builder.descendantsOfQuery(value);
						}
						break;
	
					case REFERENCE_SET_MEMBERS:
						if (StringUtils.isEmpty(value)) {
							throw new InvalidRequestException(String.format("Invalid 'fhir_vs=refset/conceptId' query part '%s' for URI '%s'.", queryPartString, uriString));
						} else {
							builder.valueSetQuery(value);
						}
						break;
	
					case REFERENCE_SET_IDENTIFIERS:
						builder.allReferenceSetsQuery();
						break;
	
					default:
						throw new InvalidRequestException(String.format("Unsupported 'fhir_vs' query part '%s' for URI '%s'.", queryPartString, uriString));
				}
	
			default:
				throw new InvalidRequestException(String.format("Invalid 'fhir_vs' query part '%s' for URI '%s'.", queryPartString, uriString));
		}
	}

	private static void parseCmQueryPart(final Builder builder, final String uriString, final String queryPartString) {
		final List<String> queryParts = VALUE_SPLITTER.splitToList(queryPartString);

		if (queryParts.size() != 2) {
			throw new InvalidRequestException(String.format("Invalid 'fhir_cm' query part '%s' for URI '%s', the format is '?fhir_cm=conceptId'.", queryPartString, uriString));
		} else {
			builder.conceptMapQuery(queryParts.get(1));
		}
	}

	public String getExtensionModuleId() {
		return edition;
	}

	public String getVersionTag() {
		return version;
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
		final StringBuilder sb = new StringBuilder(SNOMED_URI_SCT);

		if (edition != null) {
			sb.append("/");
			sb.append(edition);
		}

		if (version != null) {
			sb.append("/version/");
			sb.append(version);
		}

		if (queryPart != null) {
			sb.append("?");
			sb.append(queryPart.toString());
		}

		return sb.toString();
	}
}
