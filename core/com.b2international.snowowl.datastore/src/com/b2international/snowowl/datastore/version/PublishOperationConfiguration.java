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
package com.b2international.snowowl.datastore.version;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.Date;

import com.b2international.snowowl.core.date.EffectiveTimes;

/**
 * Configuration for the publication process.
 */
public final class PublishOperationConfiguration {

	private String codeSystemShortName;
	private String versionId;
	private String description;
	private Date effectiveTime;
	private String parentBranchPath;
	
	public PublishOperationConfiguration(
			String codeSystemShortName,
			String versionId, 
			String description,
			Date effectiveTime,
			String parentBranchPath) {
		this.codeSystemShortName = codeSystemShortName;
		this.versionId = versionId;
		this.description = description;
		this.effectiveTime = effectiveTime;
		this.parentBranchPath = parentBranchPath;
	}

	public String getVersionId() {
		return versionId;
	}

	public Date getEffectiveTime() {
		return null == effectiveTime ? null : new Date(effectiveTime.getTime());
	}

	public String getDescription() {
		return nullToEmpty(description);
	}
	
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}
	
	public String getParentBranchPath() {
		return parentBranchPath;
	}
	
	@Override
	public String toString() {
		return toStringHelper(this)
				.add("Version ID", versionId)
				.add("Effective time", null == effectiveTime ? "unset" : EffectiveTimes.format(effectiveTime))
				.add("Description", nullToEmpty(description))
				.toString();
	}

}