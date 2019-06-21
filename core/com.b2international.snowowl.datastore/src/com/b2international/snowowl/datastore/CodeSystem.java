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

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Captures metadata about a code system, which holds a set of real-world concepts of medical significance (optionally
 * along with different components forming a description of said concepts) and their corresponding unique code.
 */
@JsonDeserialize(builder=CodeSystem.Builder.class)
public class CodeSystem {
	
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
				.repositoryUuid(input.getRepositoryUuid())
				.extensionOf(input.getExtensionOf());
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
		private String repositoryUuid;
		private String extensionOf;

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
		
		public Builder repositoryUuid(final String repositoryUuid) {
			this.repositoryUuid = repositoryUuid;
			return getSelf();
		}
		
		public Builder extensionOf(final String extensionOf) {
			this.extensionOf = extensionOf;
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
					repositoryUuid,
					extensionOf);
		}
		
		private Builder getSelf() {
			return this;
		}
		
	}
	
	private CodeSystem(final String oid, final String name, final String shortName, final String link, final String language,
			final String citation, final String branchPath, final String iconPath, final String terminologyId, final String repositoryId,
			final String extensionOf) {
		this.oid = oid;
		this.name = name;
		this.shortName = shortName;
		this.organizationLink = link;
		this.primaryLanguage = language;
		this.citation = citation;
		this.branchPath = branchPath;
		this.iconPath = iconPath;
		this.terminologyId = terminologyId;
		this.repositoryUuid = repositoryId;
		this.extensionOf = extensionOf;
	}

	private String oid;
	
	@NotEmpty
	private String name;
	@NotEmpty
	private String shortName;
	
	private String organizationLink;
	
	@NotEmpty
	private String primaryLanguage;
	@NotEmpty
	private String citation;
	@NotEmpty
	private String branchPath;
	@NotEmpty
	private String iconPath;
	@NotEmpty
	private String terminologyId;
	@NotEmpty
	private String repositoryUuid;
	private String extensionOf;

	/**
	 * Returns the assigned object identifier (OID) of this code system.
	 * 
	 * @return the assigned object identifier of this code system, eg. "{@code 3.4.5.6.10000}" (can be {@code null})
	 */
	public String getOid() {
		return oid;
	}

	/**
	 * Returns the name of this code system.
	 * 
	 * @return the name of this code system, eg. "{@code SNOMED Clinical Terms}"
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the short name of this code system, which is usually an abbreviation of the name.
	 * 
	 * @return the short name of this code system, eg. "{@code SNOMEDCT}"
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Returns an URL for this code system which points to the maintaining organization's website.
	 * 
	 * @return the URL of the maintaining organization, eg. "{@code http://example.com/}" (can be {@code null}) 
	 */
	public String getOrganizationLink() {
		return organizationLink;
	}

	/**
	 * Returns the primary language tag for this code system.
	 * 
	 * @return the primary language tag, eg. "en_US"
	 */
	public String getPrimaryLanguage() {
		return primaryLanguage;
	}

	/**
	 * Returns a short paragraph describing the origins and purpose of this code system.
	 * 
	 * @return the citation for this code system (can be {@code null})
	 */	
	public String getCitation() {
		return citation;
	}

	/**
	 * Returns the branch path of the code system.
	 * 
	 * @return the path for the code system.
	 */
	public String getBranchPath() {
		return branchPath;
	}

	/**
	 * Returns with the application specific icon path of the code system.
	 * 
	 * @return the application specific icon path.
	 */
	public String getIconPath() {
		return iconPath;
	}

	/**
	 * Returns with the application specific ID to associate the code system
	 * with any application specific feature or container repository.
	 * 
	 * @return the application specific ID.
	 */
	public String getTerminologyId() {
		return terminologyId;
	}

	/**
	 * Returns with the unique ID of the repository where the current code
	 * system belongs to.
	 * 
	 * @return the repository UUID for the code system.
	 */
	public String getRepositoryUuid() {
		return repositoryUuid;
	}

	/**
	 * Returns the unique ID of the base Code System of this Code System.
	 */
	public String getExtensionOf() {
		return extensionOf;
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

	public void setPrimaryLanguage(final String primaryLanguage) {
		this.primaryLanguage = primaryLanguage;
	}

	public void setCitation(final String citation) {
		this.citation = citation;
	}
	
	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	
	public void setTerminologyId(String terminologyId) {
		this.terminologyId = terminologyId;
	}
	
	public void setRepositoryUuid(String repositoryUuid) {
		this.repositoryUuid = repositoryUuid;
	}
	
	public void setExtensionOf(String extensionOf) {
		this.extensionOf = extensionOf;
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
		builder.append(", repositoryUuid=");
		builder.append(repositoryUuid);
		builder.append(", extensionOf=");
		builder.append(extensionOf);
		builder.append("]");
		return builder.toString();
	}
	
}