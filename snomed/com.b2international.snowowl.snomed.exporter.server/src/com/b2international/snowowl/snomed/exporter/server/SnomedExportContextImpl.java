/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.exporter.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.Date;
import java.util.Set;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.exporter.server.rf1.Id2Rf1PropertyMapper;

/**
 * Export context for the SNOMED CT export process.
 */
public class SnomedExportContextImpl implements SnomedExportContext {

	private final IBranchPath currentBranchPath;
	private final ContentSubType contentSubType;
	private final String unsetEffectiveTimeLabel;

	private Date startEffectiveTime;
	private Date endEffectiveTime;
	
	//the modules to export
	private Set<String> moduleIds;
	
	private Id2Rf1PropertyMapper id2Rf1PropertyMapper;
	private String namespaceId;
	private Path releaseRootPath;
	private boolean unpublishedExport;
	
	public SnomedExportContextImpl( 
			final IBranchPath currentBranchPath,
			final ContentSubType contentSubType,
			final String unsetEffectiveTimeLabel,
			@Nullable final Date startEffectiveTime, 
			@Nullable final Date endEffectiveTime,
			final String namespaceId,
			final Set<String> moduleIds,
			final Id2Rf1PropertyMapper id2Rf1PropertyMapper,
			final Path releaseRootPath) {

		this.currentBranchPath = checkNotNull(currentBranchPath, "currentBranchPath");
		this.contentSubType = checkNotNull(contentSubType, "contentSubType");
		this.unsetEffectiveTimeLabel = checkNotNull(unsetEffectiveTimeLabel, "unsetEffectiveTimeLabel");
		this.startEffectiveTime = startEffectiveTime;
		this.endEffectiveTime = endEffectiveTime;
		this.namespaceId = namespaceId;
		this.moduleIds = moduleIds;
		this.id2Rf1PropertyMapper = id2Rf1PropertyMapper;
		this.releaseRootPath = releaseRootPath;
		this.unpublishedExport = false;
	}
	
	@Override
	public IBranchPath getCurrentBranchPath() {
		return currentBranchPath;
	}

	@Override
	public ContentSubType getContentSubType() {
		return contentSubType;
	}

	@Override
	public String getUnsetEffectiveTimeLabel() {
		return unsetEffectiveTimeLabel;
	}
	
	@Override
	@Nullable public Date getStartEffectiveTime() {
		return startEffectiveTime;
	}

	@Override
	public void setStartEffectiveTime(Date startEffectiveTime) {
		this.startEffectiveTime = startEffectiveTime;
	}
	
	@Override
	@Nullable public Date getEndEffectiveTime() {
		return endEffectiveTime;
	}
	
	@Override
	public void setEndEffectiveTime(Date endEffectiveTime) {
		this.endEffectiveTime = endEffectiveTime;
	}
	
	@Override
	public Set<String> getModulesToExport() {
		return moduleIds;
	}

	@Override
	public Id2Rf1PropertyMapper getId2Rf1PropertyMapper() {
		return id2Rf1PropertyMapper;
	}

	@Override
	public String getNamespaceId() {
		return namespaceId;
	}
	
	@Override
	public Path getReleaseRootPath() {
		return releaseRootPath;
	}
	
	@Override
	public boolean isUnpublishedExport() {
		return unpublishedExport;
	}
	
	@Override
	public void setUnpublishedExport(boolean isUnpublishedExport) {
		this.unpublishedExport = isUnpublishedExport;
	}
}
