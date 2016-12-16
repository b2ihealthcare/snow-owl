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
package com.b2international.snowowl.snomed.api.impl.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.b2international.snowowl.snomed.core.domain.ISnomedExportConfiguration;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;

/**
 * @since 3.7
 */
public class SnomedExportConfiguration implements ISnomedExportConfiguration {

	private Rf2ReleaseType type;
	private String branchPath;
	private String namespaceId;
	private Collection<String> moduleIds;
	private Date startEffectiveTime;
	private Date endEffectiveTime;
	private String transientEffectiveTime;
	private boolean includeUnpublised;
	private String codeSystemShortName;
	private boolean extensionOnly;

	public SnomedExportConfiguration(Rf2ReleaseType type, 
			String branchPath, 
			String namespaceId, Collection<String> moduleIds,
			Date startEffectiveTime, Date endEffectiveTime, 
			String transientEffectiveTime,
			final boolean includeUnpublished,
			String codeSystemShortName,
			boolean extensionOnly) {
		this.type = checkNotNull(type, "type");
		this.namespaceId = checkNotNull(namespaceId, "namespaceId");
		this.branchPath = checkNotNull(branchPath, "branchPath");
		this.moduleIds = moduleIds == null ? Collections.<String>emptySet() : moduleIds;
		this.startEffectiveTime = startEffectiveTime;
		this.endEffectiveTime = endEffectiveTime;
		this.transientEffectiveTime = transientEffectiveTime;
		this.includeUnpublised = includeUnpublished;
		this.codeSystemShortName = checkNotNull(codeSystemShortName, "codeSystemShortName");
		this.extensionOnly = extensionOnly;
	}
	
	@Override
	public Rf2ReleaseType getRf2ReleaseType() {
		return type;
	}

	@Override
	public String getBranchPath() {
		return branchPath;
	}
	
	@Override
	public Date getStartEffectiveTime() {
		return startEffectiveTime;
	}
	
	@Override
	public Date getEndEffectiveTime() {
		return endEffectiveTime;
	}
	
	@Override
	public String getNamespaceId() {
		return namespaceId;
	}

	@Override
	public Collection<String> getModuleIds() {
		return moduleIds;
	}
	
	@Override
	public String getTransientEffectiveTime() {
		return transientEffectiveTime;
	}

	@Override
	public boolean isIncludeUnpublised() {
		return includeUnpublised;
	}
	
	@Override
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}
	
	@Override
	public boolean isExtensionOnly() {
		return extensionOnly;
	}
	
}
