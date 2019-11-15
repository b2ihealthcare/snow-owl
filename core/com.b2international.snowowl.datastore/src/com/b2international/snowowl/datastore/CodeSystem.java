/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.matchAny;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.terminology.Terminology;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemCreateRequestBuilder;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedSet;


/**
 * @since 5.0
 */
@Doc(type = "codesystem")
@JsonDeserialize(builder = CodeSystem.Builder.class)
public final class CodeSystem implements Serializable {

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
		
		public static Expression uris(Collection<String> uris) {
			return matchAny(Fields.URIS, uris);
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
		public static final String TOOLING_ID = "toolingId";
		public static final String REPOSITORY_ID = "repositoryId";
		public static final String URIS = "uris";
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(CodeSystem codeSystem) {
		return builder()
				.oid(codeSystem.getOid())
				.name(codeSystem.getName())
				.shortName(codeSystem.getShortName())
				.orgLink(codeSystem.getOrgLink())
				.language(codeSystem.getLanguage())
				.citation(codeSystem.getCitation())
				.iconPath(codeSystem.getIconPath())
				.terminologyComponentId(codeSystem.getToolingId())
				.repositoryUuid(codeSystem.getRepositoryId())
				.branchPath(codeSystem.getBranchPath())
				.extensionOf(codeSystem.getExtensionOf())
				.uris(codeSystem.getUris());
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
		private String toolingId;
		private String repositoryId;
		private String branchPath = Branch.MAIN_PATH;
		private String extensionOf;
		private List<String> uris;
		
		Builder() {
		}
		
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
		
		/**
		 * @param orgLink
		 * @return
		 * @deprecated - present because of backward compatibility in REST API
		 */
		@JsonProperty
		Builder organizationLink(String orgLink) {
			return orgLink(orgLink);
		}

		public Builder orgLink(String orgLink) {
			this.orgLink = orgLink;
			return this;
		}

		/**
		 * @param language
		 * @return
		 * @deprecated - present because of backward compatibility in REST API
		 */
		@JsonProperty
		Builder primaryLanguage(String language) {
			return language(language);
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
		
		/**
		 * @param toolingId
		 * @return
		 * @deprecated - present because of backward compatibility with 7.1 datasets
		 */
		@JsonProperty
		Builder terminologyComponentId(String toolingId) {
			return toolingId(toolingId);
		}
		
		/**
		 * @param toolingId
		 * @return
		 * @deprecated - present because of backward compatibility in REST API
		 */
		@JsonProperty
		Builder terminologyId(String toolingId) {
			return toolingId(toolingId);
		}
		
		public Builder toolingId(String toolingId) {
			this.toolingId = toolingId;
			return this;
		}
		
		/**
		 * @param repositoryId
		 * @return
		 * @deprecated - present because of backward compatibility with 7.1 datasets
		 */
		@JsonProperty
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
		
		public Builder extensionOf(final String extensionOf) {
			this.extensionOf = extensionOf;
			return this;
		}
		
		public Builder uris(List<String> uris) {
			this.uris = uris;
			return this;
		}
		
		public CodeSystem build() {
			return new CodeSystem(oid, name, shortName, orgLink, language, citation, iconPath, toolingId, repositoryId, branchPath, extensionOf, uris);
		}
		
		
	}

	private final String oid;
	private final String name; 
	private final String shortName; 
	private final String orgLink; 
	private final String language; 
	private final String citation; 
	private final String iconPath; 
	private final String toolingId;
	private final String repositoryId;
	private final String branchPath;
	private final String extensionOf;
	private final List<String> uris;
	
	private CodeSystem(final String oid, 
			final String name, 
			final String shortName, 
			final String orgLink, 
			final String language, 
			final String citation, 
			final String iconPath, 
			final String toolingId, 
			final String repositoryId, 
			final String branchPath, 
			final String extensionOf,
			final List<String> uris) {
		this.oid = Strings.nullToEmpty(oid);
		this.name = Strings.nullToEmpty(name);
		this.shortName = Strings.nullToEmpty(shortName);
		this.orgLink = Strings.nullToEmpty(orgLink);
		this.language = Strings.nullToEmpty(language);
		this.citation = Strings.nullToEmpty(citation);
		this.iconPath = Strings.nullToEmpty(iconPath);
		this.toolingId = checkNotNull(toolingId);
		this.repositoryId = checkNotNull(repositoryId);
		this.branchPath = branchPath;
		this.extensionOf = extensionOf;
		this.uris = Collections3.toImmutableList(uris);
	}

	/**
	 * Returns the code system OID. Can be {@code null}.
	 * @return the OID.
	 */
	public String getOid() {
		return oid;
	}

	/**
	 * Returns with the name of the code system.
	 * @return the name of the code system.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns with the code system short name.
	 * @return the code system short name.
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Returns with the maintaining organization link. Can be {@code null}.
	 * @return the link for the maintaining organization. 
	 */
	public String getOrgLink() {
		return orgLink;
	}

	/**
	 * Returns with the language of the code system.
	 * @return the language.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Returns with the citation of the code system.
	 * @return the citation of the code system.
	 */
	public String getCitation() {
		return citation;
	}

	/**
	 * Returns with the application specific icon path of the code system. 
	 * @return the application specific icon path.
	 */
	public String getIconPath() {
		return iconPath;
	}

	/**
	 * @return the identifier that identifies the represented Terminology.
	 */
	public String getToolingId() {
		return toolingId;
	}

	/**
	 * Returns with the unique ID of the repository where the current code system belongs to. 
	 * @return the repository UUID for the code system.
	 */
	public String getRepositoryId() {
		return repositoryId;
	}
	
	/**
	 * Returns the branch path of the code system.
	 * @return the path for the code system.
	 */
	public String getBranchPath() {
		return branchPath;
	}

	/**
	 * Returns the unique ID of the base Code System.
	 */
	public String getExtensionOf() {
		return extensionOf;
	}
	
	/**
	 * @return the associated codesystem specific URIs as a {@link List}.
	 */
	public List<String> getUris() {
		return uris;
	}

	@Override
	public int hashCode() {
		return Objects.hash(oid, shortName);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final CodeSystem other = (CodeSystem) obj;
		return Objects.equals(oid, other.oid) && Objects.equals(shortName, other.shortName);
	}
	
	/**
	 * Returns all code system short name dependencies and itself.
	 */
	@JsonIgnore
	public SortedSet<String> getDependenciesAndSelf() {
		ImmutableSortedSet.Builder<String> affectedCodeSystems = ImmutableSortedSet.naturalOrder();
		affectedCodeSystems.addAll(getDependencies());
		affectedCodeSystems.add(shortName);
		return affectedCodeSystems.build();
	}
	
	/**
	 * Returns the short names of all affected code systems
	 */
	@JsonIgnore
	public SortedSet<String> getDependencies() {
		return TerminologyRegistry.INSTANCE.getTerminology(toolingId).getDependencies();
	}
	
	/**
	 * Returns a new branch path that originates from the code system's branch path
	 */
	@JsonIgnore
	public String getRelativeBranchPath(String relativeTo) {
		return String.format("%s%s%s", branchPath, Branch.SEPARATOR, relativeTo);
	}

	/**
	 * @return a prepared {@link CodeSystemCreateRequestBuilder} that can be built and sent to materialize this {@link CodeSystem}.
	 */
	public CodeSystemCreateRequestBuilder toCreateRequest() {
		return CodeSystemRequests.prepareNewCodeSystem()
			.setName(getName())
			.setOid(getOid())
			.setLanguage(getLanguage())
			.setLink(getOrgLink())
			.setCitation(getCitation())
			.setIconPath(getIconPath())
			.setToolingId(getToolingId())
			.setShortName(getShortName())
			.setRepositoryId(getRepositoryId())
			.setBranchPath(getBranchPath());
	}

	/**
	 * @return the {@link Terminology} specification object (the tooling registration essentially) from the global TerminologyRegistry.
	 */
	@JsonIgnore
	public Terminology getTerminology() {
		return TerminologyRegistry.INSTANCE.getTerminology(getToolingId());
	}
	
}
