/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core.index;

import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_BRANCH_PATH;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_CITATION;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_EXTENSION_OF;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_ICON_PATH;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_LANGUAGE;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_NAME;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_OID;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_ORG_LINK;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_REPOSITORY_UUID;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_SHORT_NAME;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_STORAGE_KEY;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_TERMINOLOGY_COMPONENT_ID;

import java.io.Serializable;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.google.common.base.Strings;


/**
 * CDO independent representation of a {@link CodeSystem}.
 *
 */
public class CodeSystemEntry implements Serializable, ICodeSystem {

	private static final long serialVersionUID = 3089017116155820382L;
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(final CodeSystem codeSystem) {
		return builder()
				.oid(codeSystem.getCodeSystemOID())
				.name(codeSystem.getName())
				.shortName(codeSystem.getShortName())
				.link(codeSystem.getMaintainingOrganizationLink())
				.language(codeSystem.getLanguage())
				.citation(codeSystem.getCitation())
				.iconPath(codeSystem.getIconPath())
				.terminologyId(codeSystem.getTerminologyComponentId())
				.storageKey(CDOUtils.isTransient(codeSystem) ? CDOUtils.NO_STORAGE_KEY + "" 
						: Long.toString(CDOIDUtils.asLong(codeSystem.cdoID())))
				.repositoryId(codeSystem.getRepositoryUuid())
				.branchPath(codeSystem.getBranchPath())
				.extensionOf(codeSystem.getExtensionOf() == null ? "" : codeSystem.getExtensionOf().getShortName());
	}
	
	public static Builder builder(final Document doc) {
		return builder()
				.oid(doc.get(SYSTEM_OID))
				.name(doc.get(SYSTEM_NAME))
				.shortName(doc.get(SYSTEM_SHORT_NAME))
				.link(doc.get(SYSTEM_ORG_LINK))
				.language(doc.get(SYSTEM_LANGUAGE))
				.citation(doc.get(SYSTEM_CITATION))
				.iconPath(doc.get(SYSTEM_ICON_PATH))
				.terminologyId(doc.get(SYSTEM_TERMINOLOGY_COMPONENT_ID))
				.storageKey(doc.get(SYSTEM_STORAGE_KEY))
				.repositoryId(doc.get(SYSTEM_REPOSITORY_UUID))
				.branchPath(doc.get(SYSTEM_BRANCH_PATH) == null ? "MAIN" : doc.get(SYSTEM_BRANCH_PATH))
				.extensionOf(doc.get(SYSTEM_EXTENSION_OF));
	}
	
	public static class Builder {
		
		private String oid;
		private String name; 
		private String shortName; 
		private String link; 
		private String language; 
		private String citation; 
		private String iconPath; 
		private String terminologyId;
		private String storageKey;
		private String repositoryId;
		private String branchPath;
		private String extensionOf;
		
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
		
		public Builder link(final String link) {
			this.link = link;
			return getSelf();
		}
		
		public Builder language(final String language) {
			this.language = language;
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
		
		public Builder storageKey(final String storageKey) {
			this.storageKey = storageKey;
			return getSelf();
		}
		
		public Builder repositoryId(final String repositoryId) {
			this.repositoryId = repositoryId;
			return getSelf();
		}
		
		public Builder extensionOf(final String extensionOf) {
			this.extensionOf = extensionOf;
			return getSelf();
		}
		
		public CodeSystemEntry build() {
			return new CodeSystemEntry(
					oid, 
					name, 
					shortName, 
					link, 
					language, 
					citation, 
					iconPath, 
					terminologyId, 
					storageKey, 
					repositoryId, 
					branchPath, 
					extensionOf);
		}
		
		private Builder getSelf() {
			return this;
		}
		
	}

	private final String oid;
	private final String name; 
	private final String shortName; 
	private final String orgLink; 
	private final String language; 
	private final String citation; 
	private final String iconPath; 
	private final String snowOwlId;
	private final long storageKey;
	private final String repositoryUuid;
	private final String branchPath;
	private final String extensionOf;
	
	CodeSystemEntry(final String oid, final String name, final String shortName, final String orgLink, final String language,
			final String citation, final String iconPath, final String snowOwlId, final String storageKey, final String repositoryUuid,
			final String branchPath, final String extensionOf) {
		
		this.storageKey = Long.parseLong(storageKey);
		this.repositoryUuid = repositoryUuid;
		this.oid = Strings.nullToEmpty(oid);
		this.name = name;
		this.shortName = shortName;
		this.orgLink = orgLink;
		this.language = language;
		this.citation = citation;
		this.iconPath = iconPath;
		this.snowOwlId = snowOwlId;
		this.branchPath = branchPath;
		this.extensionOf = extensionOf;
	}

	@Override
	public String getOid() {
		return oid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getShortName() {
		return shortName;
	}

	@Override
	public String getOrgLink() {
		return orgLink;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public String getCitation() {
		return citation;
	}

	@Override
	public String getIconPath() {
		return iconPath;
	}

	@Override
	public String getSnowOwlId() {
		return snowOwlId;
	}
	
	@Override
	public String getRepositoryUuid() {
		return repositoryUuid;
	}
	
	@Override
	public long getStorageKey() {
		return  storageKey;
	}
	
	@Override
	public String getBranchPath() {
		return branchPath;
	}
	
	@Override
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
}