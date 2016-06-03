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
package com.b2international.snowowl.datastore;

import java.io.Serializable;

import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.google.common.base.Strings;


/**
 * CDO independent representation of a {@link CodeSystem}.
 *
 */
public class CodeSystemEntry implements Serializable, ICodeSystem {

	private static final long serialVersionUID = 3089017116155820382L;

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
	
	public CodeSystemEntry(final String oid, final String name, final String shortName, final String orgLink, final String language,
			final String citation, final String iconPath, final String snowOwlId, final String storageKey, final String repositoryUuid,
			final String branchPath) {
		
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