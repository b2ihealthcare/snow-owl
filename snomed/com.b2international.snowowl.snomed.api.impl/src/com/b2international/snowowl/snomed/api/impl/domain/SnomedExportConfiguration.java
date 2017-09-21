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
	private String countryAndNamespaceId;
	private Collection<String> moduleIds;
	private Date deltaExportStartEffectiveTime;
	private Date deltaExportEndEffectiveTime;
	private String transientEffectiveTime;

	public SnomedExportConfiguration(Rf2ReleaseType type, 
			String branchPath, 
			String namespaceId, Collection<String> moduleIds,
			Date deltaExportStartEffectiveTime, Date deltaExportEndEffectiveTime, 
			String transientEffectiveTime) {
		this.type = checkNotNull(type, "type");
		this.countryAndNamespaceId = checkNotNull(countryAndNamespaceId, "countryAndNamespaceId");
		this.branchPath = checkNotNull(branchPath, "branchPath");
		this.moduleIds = moduleIds == null ? Collections.<String>emptySet() : moduleIds;
		this.deltaExportStartEffectiveTime = deltaExportStartEffectiveTime;
		this.deltaExportEndEffectiveTime = deltaExportEndEffectiveTime;
		this.transientEffectiveTime = transientEffectiveTime;
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
	public Date getDeltaExportStartEffectiveTime() {
		return deltaExportStartEffectiveTime;
	}
	
	@Override
	public Date getDeltaExportEndEffectiveTime() {
		return deltaExportEndEffectiveTime;
	}
	
	@Override
	public String getCountryAndNamespaceId() {
		return countryAndNamespaceId;
	}

	@Override
	public Collection<String> getModuleIds() {
		return moduleIds;
	}
	
	@Override
	public String getTransientEffectiveTime() {
		return transientEffectiveTime;
	}
}
