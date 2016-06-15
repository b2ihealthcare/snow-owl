/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core.preferences;

import java.io.Serializable;

import com.b2international.snowowl.datastore.ICodeSystem;
import com.google.common.base.Function;

/**
 * Light weight, {@link Serializable} object for persisting the preferred terminology extension. 
 */
public class PreferredTerminologyExtension implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String shortName;
	
	private String repositoryUuid;
	
	private String branchPath;

	private String oid;
	
	private String iconPath;
	
	public PreferredTerminologyExtension() {
	}
	
	public PreferredTerminologyExtension(ICodeSystem codeSystem) {
		this.shortName = codeSystem.getShortName();
		this.repositoryUuid = codeSystem.getRepositoryUuid();
		this.branchPath = codeSystem.getBranchPath();
		this.oid = codeSystem.getOid();
		this.iconPath = codeSystem.getIconPath();
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getRepositoryUuid() {
		return repositoryUuid;
	}

	public void setRepositoryUuid(String repositoryUuid) {
		this.repositoryUuid = repositoryUuid;
	}

	public String getBranchPath() {
		return branchPath;
	}

	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getIconPath() {
		return iconPath;
	}
	
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PreferredTerminologyExtension [shortName=").append(shortName).append(", repositoryUuid=")
				.append(repositoryUuid).append(", branchPath=").append(branchPath).append(", oid=").append(oid)
				.append(", iconPath=").append(iconPath).append("]");
		return builder.toString();
	}


	public static class CodeSystemToPojoFunction implements Function<ICodeSystem, PreferredTerminologyExtension> {
		
		@Override
		public PreferredTerminologyExtension apply(ICodeSystem input) {
			return new PreferredTerminologyExtension(input);
		}
		
	}
	
}
