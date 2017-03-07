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
package com.b2international.snowowl.terminologyregistry.core.builder;

import java.util.Date;

import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataFactory;

/**
 * @since 4.7
 */
public class CodeSystemVersionBuilder {

	private String versionId;
	private String description;
	private String parentBranchPath;
	private Date importDate;
	private Date effectiveDate;
	private Date lastUpdateDate;

	public CodeSystemVersionBuilder withVersionId(final String versionId) {
		this.versionId = versionId;
		return getSelf();
	}

	public CodeSystemVersionBuilder withDescription(final String description) {
		this.description = description;
		return getSelf();
	}

	public CodeSystemVersionBuilder withParentBranchPath(final String parentBranchPath) {
		this.parentBranchPath = parentBranchPath;
		return getSelf();
	}

	public CodeSystemVersionBuilder withImportDate(final Date importDate) {
		this.importDate = importDate;
		return getSelf();
	}

	public CodeSystemVersionBuilder withEffectiveDate(final Date effectiveDate) {
		this.effectiveDate = effectiveDate;
		return getSelf();
	}

	public CodeSystemVersionBuilder withLastUpdateDate(final Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
		return getSelf();
	}

	private CodeSystemVersionBuilder getSelf() {
		return this;
	}

	public CodeSystemVersion build() {
		final CodeSystemVersion version = TerminologymetadataFactory.eINSTANCE.createCodeSystemVersion();
		version.setVersionId(versionId);
		version.setDescription(description);
		version.setParentBranchPath(parentBranchPath);
		version.setImportDate(importDate);
		version.setEffectiveDate(effectiveDate);
		version.setLastUpdateDate(lastUpdateDate);

		return version;
	}

}
