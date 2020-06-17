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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Captures metadata about a code system, which holds a set of concepts of
 * medical significance (optionally with other, supporting components that
 * together make up the definition of concepts) and their corresponding unique
 * code.
 * 
 * @since 
 */
@JsonDeserialize(builder=CodeSystem.Builder.class)
public class CodeSystem implements Serializable {
	
	private static final long serialVersionUID = 760L;

	/**
	 * @since 7.6.0
	 */
	public static final class Expand {
		public static final String AVAILABLE_UPGRADES = "availableUpgrades";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(final CodeSystemEntry input) {
		return builder()
				.oid(input.getOid())
				.name(input.getName())
				.shortName(input.getShortName())
				.organizationLink(input.getOrgLink())
				.primaryLanguage(input.getLanguage())
				.citation(input.getCitation())
				.branchPath(input.getBranchPath())
				.iconPath(input.getIconPath())
				.terminologyId(input.getTerminologyComponentId())
				.repositoryId(input.getRepositoryId())
				.extensionOf(input.getExtensionOf())
				.locales(input.getLocales())
				.additionalProperties(input.getAdditionalProperties());
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		
		private String oid;
		private String name;
		private String shortName;
		private String organizationLink;
		private String primaryLanguage;
		private String citation;
		private String branchPath;
		private String iconPath;
		private String terminologyId;
		private String repositoryId;
		private CodeSystemURI extensionOf;
		private List<ExtendedLocale> locales;
		private Map<String, Object> additionalProperties;
		
		@JsonCreator
		private Builder() {}
		
		public Builder oid(final String oid) {
			this.oid = oid;
			return getSelf();
		}
		
		public Builder name(final String name) {
			this.name = name;
			return getSelf();
		}
		
		public Builder shortName(final String shortName) {
			this.shortName = shortName;
			return getSelf();
		}
		
		public Builder organizationLink(final String organizationLink) {
			this.organizationLink = organizationLink;
			return getSelf();
		}
		
		public Builder primaryLanguage(final String primaryLanguage) {
			this.primaryLanguage = primaryLanguage;
			return getSelf();
		}
		
		public Builder citation(final String citation) {
			this.citation = citation;
			return getSelf();
		}
		
		public Builder branchPath(final String branchPath) {
			this.branchPath = branchPath;
			return getSelf();
		}
		
		public Builder iconPath(final String iconPath) {
			this.iconPath = iconPath;
			return getSelf();
		}
		
		public Builder terminologyId(final String terminologyId) {
			this.terminologyId = terminologyId;
			return getSelf();
		}
		
		public Builder repositoryId(final String repositoryId) {
			this.repositoryId = repositoryId;
			return getSelf();
		}
		
		public Builder extensionOf(final CodeSystemURI extensionOf) {
			this.extensionOf = extensionOf;
			return getSelf();
		}
		
		public Builder locales(final List<ExtendedLocale> locales) {
			this.locales = Optional.ofNullable(locales)
					.map(Lists::newArrayList)
					.orElse(null);
			
			return getSelf();
		}
		
		public Builder additionalProperties(final Map<String, Object> additionalProperties) {
			this.additionalProperties = Optional.ofNullable(additionalProperties)
					.map(Maps::newHashMap)
					.orElse(null);
			
			return getSelf();
		}
		
		public CodeSystem build() {
			return new CodeSystem(
					oid, 
					name, 
					shortName, 
					organizationLink, 
					primaryLanguage, 
					citation, 
					branchPath, 
					iconPath, 
					terminologyId, 
					repositoryId,
					extensionOf,
					locales,
					additionalProperties);
		}
		
		private Builder getSelf() {
			return this;
		}
	}
	
	private CodeSystem(final String oid, 
			final String name, 
			final String shortName, 
			final String organizationLink, 
			final String primaryLanguage,
			final String citation, 
			final String branchPath, 
			final String iconPath, 
			final String terminologyId, 
			final String repositoryId,
			final CodeSystemURI extensionOf, 
			final List<ExtendedLocale> locales,
			final Map<String, Object> additionalProperties) {

		this.oid = oid;
		this.name = name;
		this.shortName = shortName;
		this.organizationLink = organizationLink;
		this.primaryLanguage = primaryLanguage;
		this.citation = citation;
		this.branchPath = branchPath;
		this.iconPath = iconPath;
		this.terminologyId = terminologyId;
		this.repositoryId = repositoryId;
		this.extensionOf = extensionOf;
		this.locales = locales;
		this.additionalProperties = additionalProperties;
	}

	private String oid;
	private @NotEmpty String name;
	private @NotEmpty String shortName;
	private String organizationLink;
	private @NotEmpty String primaryLanguage;
	private @NotEmpty String citation;
	private String branchPath;
	private @NotEmpty String iconPath;
	private @NotEmpty String terminologyId;
	private @NotEmpty String repositoryId;
	private CodeSystemURI extensionOf;
	private List<ExtendedLocale> locales;
	private Map<String, Object> additionalProperties;
	private List<CodeSystemURI> availableUpgrades;

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
	public String getOrganizationLink() {
		return organizationLink;
	}

	/**
	 * @return the primary language tag, eg. "en_US"
	 * 
	 * @deprecated Clients should access language information via {@link #getLocales()} instead. 
	 */
	@Deprecated
	public String getPrimaryLanguage() {
		return primaryLanguage;
	}

	/**
	 * @return a short paragraph describing the origins and purpose of this code
	 *         system (can be {@code null})
	 */
	public String getCitation() {
		return citation;
	}

	/**
	 * @return the working branch path for the code system, eg.
	 *         "{@code MAIN/2018-07-31/SNOMEDCT-EXT}"
	 */
	public String getBranchPath() {
		return branchPath;
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
	public String getTerminologyId() {
		return terminologyId;
	}

	/**
	 * @return the unique ID of the repository where code system content is stored
	 */
	public String getRepositoryId() {
		return repositoryId;
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
	 * @return a map storing metadata key-value pairs specific to this code system
	 *         (can be {@code null}). Interpretation of values is
	 *         implementation-dependent.
	 */
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	
	/**
	 * @return a list of {@link CodeSystemURI}s pointing to code system versions that have 
	 *         been created after the current {@code extensionOf} version on the parent
	 *         code system (can be {@code null} if not requested as part of an expand() option) 
	 */
	public List<CodeSystemURI> getAvailableUpgrades() {
		return availableUpgrades;
	}

	public void setOid(final String oid) {
		this.oid = oid;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setShortName(final String shortName) {
		this.shortName = shortName;
	}

	public void setOrganizationLink(final String organizationLink) {
		this.organizationLink = organizationLink;
	}

	@Deprecated
	public void setPrimaryLanguage(final String primaryLanguage) {
		this.primaryLanguage = primaryLanguage;
	}

	public void setCitation(final String citation) {
		this.citation = citation;
	}
	
	public void setBranchPath(final String branchPath) {
		this.branchPath = branchPath;
	}
	
	public void setIconPath(final String iconPath) {
		this.iconPath = iconPath;
	}
	
	public void setTerminologyId(final String terminologyId) {
		this.terminologyId = terminologyId;
	}
	
	public void setRepositoryId(final String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public void setExtensionOf(final CodeSystemURI extensionOf) {
		this.extensionOf = extensionOf;
	}
	
	public void setLocales(final List<ExtendedLocale> locales) {
		this.locales = locales;
	}
	
	public void setAdditionalProperties(final Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	
	public void setAvailableUpgrades(final List<CodeSystemURI> availableUpgrades) {
		this.availableUpgrades = availableUpgrades;
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
		return TerminologyRegistry.INSTANCE.getTerminology(terminologyId).getDependencies();
	}
	
	/**
	 * Returns a new branch path that originates from the code system's branch path
	 */
	@JsonIgnore
	public String getRelativeBranchPath(String relativeTo) {
		return String.format("%s%s%s", branchPath, Branch.SEPARATOR, relativeTo);
	}
	
	/**
	 * Returns the CodeSystemURI of this code system at the given active branch
	 */
	@JsonIgnore
	public CodeSystemURI getCodeSystemURI(String activeBranch) {
		final String relativePath = activeBranch.replaceFirst(branchPath, "");
		final String codeSystemPath = relativePath.isEmpty() ? CodeSystemURI.HEAD : relativePath;
		return new CodeSystemURI(String.format("%s%s%s", shortName, Branch.SEPARATOR, codeSystemPath));			
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CodeSystem [oid=");
		builder.append(oid);
		builder.append(", name=");
		builder.append(name);
		builder.append(", shortName=");
		builder.append(shortName);
		builder.append(", organizationLink=");
		builder.append(organizationLink);
		builder.append(", primaryLanguage=");
		builder.append(primaryLanguage);
		builder.append(", citation=");
		builder.append(citation);
		builder.append(", branchPath=");
		builder.append(branchPath);
		builder.append(", iconPath=");
		builder.append(iconPath);
		builder.append(", repositoryId=");
		builder.append(repositoryId);
		builder.append(", extensionOf=");
		builder.append(extensionOf);
		builder.append(", locales=");
		builder.append(locales);
		builder.append(", additionalProperties=");
		builder.append(additionalProperties);
		builder.append("]");
		return builder.toString();
	}
}
