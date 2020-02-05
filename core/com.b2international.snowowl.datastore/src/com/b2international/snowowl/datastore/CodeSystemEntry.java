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
package com.b2international.snowowl.datastore;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.matchAny;

import java.io.Serializable;
import java.util.Collection;
import java.util.SortedSet;

import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedSet;


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
		
	}
	
	public static class Fields {
		public static final String STORAGE_KEY = "storageKey";
		public static final String OID = "oid";
		public static final String NAME = "name"; 
		public static final String SHORT_NAME = "shortName"; 
		public static final String ORG_LINK = "orgLink"; 
		public static final String LANGUAGE = "language"; 
		public static final String CITATION = "citation"; 
		public static final String ICON_PATH = "iconPath"; 
		public static final String TERMINOLOGY_COMPONENT_ID = "terminologyComponentId";
		public static final String REPOSITORY_UUID = "repositoryUuid";
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
				.repositoryUuid(codeSystem.getRepositoryUuid())
				.branchPath(codeSystem.getBranchPath())
				.extensionOf(codeSystem.getExtensionOf());
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
		private String repositoryUuid;
		private String branchPath = Branch.MAIN_PATH;
		private String extensionOf;
		
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
		
		public Builder repositoryUuid(String repositoryUuid) {
			this.repositoryUuid = repositoryUuid;
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
		
		public CodeSystemEntry build() {
			return new CodeSystemEntry(oid, name, shortName, orgLink, language, citation, iconPath, terminologyComponentId, repositoryUuid, branchPath, extensionOf);
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
	private final String repositoryUuid;
	private final String branchPath;
	private final String extensionOf;
	
	private CodeSystemEntry(final String oid, final String name, final String shortName, final String orgLink, 
			final String language, final String citation, final String iconPath, final String terminologyComponentId, 
			final String repositoryUuid, final String branchPath, final String extensionOf) {
		this.oid = Strings.nullToEmpty(oid);
		this.name = Strings.nullToEmpty(name);
		this.shortName = Strings.nullToEmpty(shortName);
		this.orgLink = Strings.nullToEmpty(orgLink);
		this.language = Strings.nullToEmpty(language);
		this.citation = Strings.nullToEmpty(citation);
		this.iconPath = Strings.nullToEmpty(iconPath);
		this.terminologyComponentId = terminologyComponentId;
		this.repositoryUuid = repositoryUuid;
		this.branchPath = branchPath;
		this.extensionOf = extensionOf;
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
	 * Returns with the application specific ID to associate the code 
	 * system with any application specific
	 *  feature or container repository.
	 * @return the application specific ID.
	 */
	public String getTerminologyComponentId() {
		return terminologyComponentId;
	}

	/**
	 * Returns with the unique ID of the repository where the current code system belongs to. 
	 * @return the repository UUID for the code system.
	 */
	public String getRepositoryUuid() {
		return repositoryUuid;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((oid == null) ? 0 : oid.hashCode());
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CodeSystemEntry))
			return false;
		final CodeSystemEntry other = (CodeSystemEntry) obj;
		if (oid == null) {
			if (other.oid != null)
				return false;
		} else if (!oid.equals(other.oid))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		return true;
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
		return TerminologyRegistry.INSTANCE.getTerminology(terminologyComponentId).getDependencies();
	}
	
	/**
	 * Returns a new branch path that originates from the code system's branch path
	 */
	@JsonIgnore
	public String getRelativeBranchPath(String relativeTo) {
		return String.format("%s%s%s", branchPath, Branch.SEPARATOR, relativeTo);
	}
	
}
