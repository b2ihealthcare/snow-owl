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
package com.b2international.snowowl.datastore.version;

import static com.b2international.commons.StringUtils.EMPTY_STRING;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterators.unmodifiableIterator;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.google.common.collect.Lists;

/**
 * Configuration for the publication process.
 */
public class PublishOperationConfiguration implements IPublishOperationConfiguration {

	private static final long serialVersionUID = -8002761922404393510L;

	private final UUID remoteJobId;
	private final String primaryToolingId;
	private final Collection<String> toolingIds;
	private final String userId;
	private Date effectiveTime;
	private String versionId;
	private String parentBranchPath; 
	private String description;
	private String codeSystemShortName;

	/**Creates a publication operation configuration for the given tooling features with a random remote job identifier.*/
	public PublishOperationConfiguration(final String toolingId, final String... otherToolingIds) {
		this(UUID.randomUUID(), toolingId, otherToolingIds);
	}

	/**Creates a publication operation configuration for the given tooling features.*/
	public PublishOperationConfiguration(final UUID remoteJobId, final String toolingId, final String... otherToolingIds) {
		checkNotNull(toolingId, "toolingId");
		checkNotNull(otherToolingIds, "otherToolingIds");
		
		this.primaryToolingId = toolingId;
		this.toolingIds = Lists.asList(toolingId, otherToolingIds);
		this.remoteJobId = remoteJobId;
		this.userId = getServiceForClass(ICDOConnectionManager.class).getUserId();
		this.description = EMPTY_STRING;
	}

	@Override
	public UUID getRemoteJobId() {
		return remoteJobId;
	}

	@Override
	public String getVersionId() {
		return versionId;
	}

	@Override
	public Date getEffectiveTime() {
		return null == effectiveTime ? null : new Date(effectiveTime.getTime());
	}

	@Override
	public String getDescription() {
		return nullToEmpty(description);
	}
	
	@Override
	public String getPrimaryToolingId() {
		return primaryToolingId;
	}

	@Override
	public Collection<String> getToolingIds() {
		return toolingIds;
	}

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public Iterator<String> iterator() {
		return unmodifiableIterator(getToolingIds().iterator());
	}

	/**
	 * Sets the version ID.
	 * @param versionId the version ID.
	 */
	public void setVersionId(@Nullable final String versionId) {
		this.versionId = versionId;
	}

	/**
	 * Sets the effective time to the desired value.
	 * @param effectiveTime the effective time to set.
	 */
	public void setEffectiveTime(@Nullable final Date effectiveTime) {
		if (null == effectiveTime) {
			this.effectiveTime = null;
		} else {
			if (null == this.effectiveTime) {
				this.effectiveTime = new Date(effectiveTime.getTime());
			} else {
				this.effectiveTime.setTime(effectiveTime.getTime());
			}
		}
	}

	public void setDescription(@Nullable String description) {
		this.description = nullToEmpty(description);
	}


	@Override
	public String getParentBranchPath() {
		return parentBranchPath;
	}

	public void setParentBranchPath(String parentBranchPath) {
		this.parentBranchPath = parentBranchPath;
	}

	@Override
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}
	
	public void setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}
	
	@Override
	public String toString() {
		return toStringHelper(this)
				.add("Tooling ID", toolingIds)
				.add("Version ID", versionId)
				.add("Effective time", null == effectiveTime ? "unset" : EffectiveTimes.format(effectiveTime))
				.add("User ID", userId)
				.add("Description", nullToEmpty(description))
				.toString();
	}
}