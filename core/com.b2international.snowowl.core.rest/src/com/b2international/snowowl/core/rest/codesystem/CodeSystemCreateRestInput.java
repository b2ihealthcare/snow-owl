/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.codesystem;

import java.util.Map;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.rest.BaseResourceCreateRestInput;

/**
 * @since 8.0
 */
public final class CodeSystemCreateRestInput extends BaseResourceCreateRestInput {
	
	private String oid;
	private String branchPath;
	private String toolingId;
	private Map<String, Object> settings;
	private ResourceURI extensionOf;
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public String getOid() {
		return oid;
	}
	
	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	public void setToolingId(String toolingId) {
		this.toolingId = toolingId;
	}
	
	public String getToolingId() {
		return toolingId;
	}
	
	public void setSettings(Map<String, Object> settings) {
		this.settings = settings;
	}
	
	public Map<String, Object> getSettings() {
		return settings;
	}
	
	public void setExtensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
	}
	
	public ResourceURI getExtensionOf() {
		return extensionOf;
	}
}
