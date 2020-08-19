/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.matchAny;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 5.0
 */
@Doc(type = "codesystem")
@JsonDeserialize(builder = CodeSystemEntry.Builder.class)
public final class CodeSystemEntry implements Serializable {

	/**
	 * Unique terminology component identifier for code systems.
	 */
	public static final short TERMINOLOGY_COMPONENT_ID = 1;
	
	public static class Expressions {

		public static Expression shortName(String shortName) {
			return exactMatch(Fields.SHORT_NAME, shortName);
		}
		
		public static Expression shortNames(Collection<String> shortNames) {
			return matchAny(Fields.SHORT_NAME, shortNames);
		}
		
		public static Expression oid(String oid) {
			return exactMatch(Fields.OID, oid);
		}
		
		public static Expression oids(Collection<String> oids) {
			return matchAny(Fields.OID, oids);
		}
		
		public static Expression toolingIds(Iterable<String> toolingIds) {
			return matchAny(Fields.TERMINOLOGY_COMPONENT_ID, toolingIds);
		}
	}
	
	public static class Fields {
		public static final String OID = "oid";
		public static final String NAME = "name"; 
		public static final String SHORT_NAME = "shortName"; 
		public static final String ORG_LINK = "orgLink"; 
		public static final String LANGUAGE = "language"; 
		public static final String CITATION = "citation"; 
		public static final String ICON_PATH = "iconPath"; 
		public static final String TERMINOLOGY_COMPONENT_ID = "terminologyComponentId";
		public static final String REPOSITORY_ID = "repositoryId";
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(CodeSystemEntry codeSystem) {
		return builder()
				.oid(codeSystem.getOid())
				.name(codeSystem.getName())
				.shortName(codeSystem.getShortName())
				.orgLink(codeSystem.getOrgLink())
				.language(codeSystem.getLanguage())
				.citation(codeSystem.getCitation())
				.iconPath(codeSystem.getIconPath())
				.terminologyComponentId(codeSystem.getTerminologyComponentId())
				.repositoryId(codeSystem.getRepositoryId())
				.branchPath(codeSystem.getBranchPath())
				.extensionOf(codeSystem.getExtensionOf())
				.locales(codeSystem.getLocales())
				.additionalProperties(codeSystem.getAdditionalProperties());
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {
		
		private String oid;
		private String name; 
		private String shortName; 
		private String orgLink; 
		private String language; 
		private String citation; 
		private String iconPath; 
		private String terminologyComponentId;
		private String repositoryId;
		private String branchPath;
		private CodeSystemURI extensionOf;
		private List<ExtendedLocale> locales;
		private Map<String, Object> additionalProperties;
		
		private Builder() {}
		
		public Builder oid(String oid) {
			this.oid = oid;
			return this;
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder shortName(String shortName) {
			this.shortName = shortName;
			return this;
		}
		
		public Builder orgLink(String orgLink) {
			this.orgLink = orgLink;
			return this;
		}
		
		public Builder language(String language) {
			this.language = language;
			return this;
		}
		
		public Builder citation(String citation) {
			this.citation = citation;
			return this;
		}
		
		public Builder iconPath(String iconPath) {
			this.iconPath = iconPath;
			return this;
		}
		
		public Builder terminologyComponentId(String snowOwlId) {
			this.terminologyComponentId = snowOwlId;
			return this;
		}
		
		/**
		 * @param repositoryId
		 * @return
		 * @deprecated - use the {@link #repositoryId(String)} method instead
		 */
		Builder repositoryUuid(String repositoryId) {
			return repositoryId(repositoryId);
		}
		
		public Builder repositoryId(String repositoryId) {
			this.repositoryId = repositoryId;
			return this;
		}
		
		public Builder branchPath(final String branchPath) {
			this.branchPath = branchPath;
			return this;
		}
		
		public Builder extensionOf(final CodeSystemURI extensionOf) {
			this.extensionOf = extensionOf;
			return this;
		}

		public Builder locales(final List<ExtendedLocale> locales) {
			this.locales = Optional.ofNullable(locales)
					.map(ImmutableList::copyOf)
					.orElse(null);

			return this;
		}

		public Builder additionalProperties(final Map<String, Object> additionalProperties) {
			this.additionalProperties = Optional.ofNullable(additionalProperties)
					.map(ImmutableMap::copyOf)
					.orElse(null);

			return this;
		}
		
		public CodeSystemEntry build() {
			return new CodeSystemEntry(oid, 
					name, 
					shortName, 
					orgLink, 
					language, 
					citation, 
					iconPath, 
					terminologyComponentId, 
					repositoryId, 
					branchPath, 
					extensionOf,
					locales,
					additionalProperties);
		}
	}

	private final String oid;
	private final String name; 
	private final String shortName; 
	private final String orgLink; 
	private final String language; 
	private final String citation; 
	private final String iconPath; 
	private final String terminologyComponentId;
	// XXX keeping the field for compatibility purposes with pre-7.6 indexes
	private final String repositoryUuid;
	private final String repositoryId;
	private final String branchPath;
	private final CodeSystemURI extensionOf;
	private final List<ExtendedLocale> locales;
	private final Map<String, Object> additionalProperties;
	
	private CodeSystemEntry(final String oid, 
			final String name, 
			final String shortName, 
			final String orgLink, 
			final String language, 
			final String citation, 
			final String iconPath, 
			final String terminologyComponentId, 
			final String repositoryId, 
			final String branchPath, 
			final CodeSystemURI extensionOf,
			final List<ExtendedLocale> locales,
			final Map<String, Object> additionalProperties) {

		this.oid = Strings.nullToEmpty(oid);
		this.name = Strings.nullToEmpty(name);
		this.shortName = Strings.nullToEmpty(shortName);
		this.orgLink = Strings.nullToEmpty(orgLink);
		this.language = Strings.nullToEmpty(language);
		this.citation = Strings.nullToEmpty(citation);
		this.iconPath = Strings.nullToEmpty(iconPath);
		this.terminologyComponentId = terminologyComponentId;
		this.repositoryId = repositoryId;
		this.repositoryUuid = repositoryId;
		this.branchPath = branchPath;
		this.extensionOf = extensionOf;
		this.locales = locales;
		this.additionalProperties = additionalProperties;
	}

	/**
	 * @return the assigned object identifier (OID) of this code system, eg.
	 *         "{@code 3.4.5.6.10000}" (can be {@code null})
	 */
	public String getOid() {
		return oid;
	}

	/**
	 * @return the name of this code system, eg. "{@code SNOMED Clinical Terms}"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the short name of this code system, usually an abbreviation of the
	 *         name; eg. "{@code SNOMEDCT}"
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @return the URL of the maintaining organization, eg.
	 *         "{@code http://example.com/}" (can be {@code null})
	 */
	public String getOrgLink() {
		return orgLink;
	}

	/**
	 * @return the primary language tag, eg. "en_US"
	 * 
	 * @deprecated Clients should access language information via {@link #getLocales()} instead. 
	 */
	@Deprecated
	public String getLanguage() {
		return language;
	}

	/**
	 * @return a short paragraph describing the origins and purpose of this code
	 *         system (can be {@code null})
	 */
	public String getCitation() {
		return citation;
	}

	/**
	 * @return the application specific icon path for the code system
	 */
	public String getIconPath() {
		return iconPath;
	}

	/**
	 * @return the terminology (tooling) ID, used to associate the code system with
	 *         specific application features
	 */
	public String getTerminologyComponentId() {
		return terminologyComponentId;
	}

	/**
	 * @return the unique ID of the repository where code system content is stored
	 */
	public String getRepositoryId() {
		return repositoryId;
	}
	
	/**
	 * @return the working branch path for the code system, eg.
	 *         "{@code MAIN/2018-07-31/SNOMEDCT-EXT}"
	 */
	public String getBranchPath() {
		return branchPath;
	}

	/**
	 * @return the URI of the code system version this code system is based upon
	 *         (can be {@code null} if this is a stand-alone code system).
	 */
	public CodeSystemURI getExtensionOf() {
		return extensionOf;
	}
	
	/**
	 * @return the list of {@link ExtendedLocale} instances representing the language
	 *         content this code system carries (can be {@code null})
	 */
	public List<ExtendedLocale> getLocales() {
		return locales;
	}

	/**
	 * @return a map storing metadata key-value pairs specific to this code system.
	 *         Interpretation of values is implementation-dependent.
	 */
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	@Override
	public int hashCode() {
		return Objects.hash(oid, shortName);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof CodeSystemEntry)) { return false; }
		
		final CodeSystemEntry other = (CodeSystemEntry) obj;

		/*
		 * FIXME: The original intention was to allow eg. local code systems to co-exist
		 * in case of a short name collision, if they come from different sources (and
		 * so their OID would be different), however:
		 *
		 * - The current implementation does not treat code systems with the same OID 
		 *   as equal unless their short name also matches; 
		 * 
		 * - We can't change the implementation that returns true if _either_ the 
		 *   short name _or_ the OID matches, as it goes against the transitive requirement 
		 *   of the equivalence relation described in the javadoc of equals():
		 * 
		 *   code system | cs1       cs2       cs3
		 *   short name  | ABC       CDE       CDE
		 *   OID         | 1.2.3.4   1.2.3.4   5.6.7.8
		 * 
		 *   In this alternative world, cs1 = cs2 and cs2 = cs3, and so cs1 should be 
		 *   equal to cs3, but it isn't!
		 * 
		 * - While some requests take care to look up both short name and OID for a code system,
		 *   other parts (client and UI) practically treat short names as unique
		 * 
		 * Consider removing "oid" from the equality check.
		 */
		return Objects.equals(oid, other.oid) && Objects.equals(shortName, other.shortName);
	}
}
