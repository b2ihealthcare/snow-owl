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
package com.b2international.snowowl.snomed.core.store;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Date;

import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.SnomedVersion;

/**
 * @since 4.7
 */
public class SnomedVersionBuilder extends SnomedSimpleComponentBuilder<SnomedVersionBuilder, SnomedVersion> {

	private String versionId;
	private String description;
	private String parentBranchPath; 
	private Date importDate;
	private Date effectiveDate;
	private Date lastUpdateDate;
	private final Collection<String> modules = newHashSet();
	
	public SnomedVersionBuilder withVersionId(final String versionId) {
		this.versionId = versionId;
		return getSelf();
	}
	
	public SnomedVersionBuilder withDescription(final String description) {
		this.description = description;
		return getSelf();
	}
	
	public SnomedVersionBuilder withParentBranchPath(final String parentBranchPath) {
		this.parentBranchPath = parentBranchPath;
		return getSelf();
	}
	
	public SnomedVersionBuilder withImportDate(final Date importDate) {
		this.importDate = importDate;
		return getSelf();
	}
	
	public SnomedVersionBuilder withEffectiveDate(final Date effectiveDate) {
		this.effectiveDate = effectiveDate;
		return getSelf();
	}
	
	public SnomedVersionBuilder withLastUpdateDate(final Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
		return getSelf();
	}
	
	public SnomedVersionBuilder withModule(final String moduleId) {
		this.modules.add(moduleId);
		return getSelf();
	}
	
	public SnomedVersionBuilder withModules(final Collection<String> modules) {
		this.modules.addAll(modules);
		return getSelf();
	}
	
	@Override
	protected void init(final SnomedVersion component) {
		component.setVersionId(versionId);
		component.setDescription(description);
		component.setParentBranchPath(parentBranchPath);
		component.setImportDate(importDate);
		component.setEffectiveDate(effectiveDate);
		component.setLastUpdateDate(lastUpdateDate);
		component.getModules().addAll(modules);
	}

	@Override
	protected SnomedVersion create() {
		return SnomedFactory.eINSTANCE.createSnomedVersion();
	}

}
