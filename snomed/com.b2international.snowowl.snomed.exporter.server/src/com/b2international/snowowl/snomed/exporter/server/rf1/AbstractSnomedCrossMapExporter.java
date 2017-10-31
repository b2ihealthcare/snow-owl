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
package com.b2international.snowowl.snomed.exporter.server.rf1;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.Date;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedExporter;

/**
 * Abstract map set RF1 exporter for SNOMED&nbsp;CT simple map type and complex map type reference sets.
 * @see SnomedRf1Exporter
 * @see AbstractSnomedRefSetExporter
 */
public abstract class AbstractSnomedCrossMapExporter implements SnomedExporter {
	
	private final SnomedMapSetSetting mapSetSetting;
	private final String label;
	private SnomedExportContext exportContext;
	private String refSetId;
	protected RevisionSearcher revisionSearcher;

	protected AbstractSnomedCrossMapExporter(final SnomedExportContext exportContext, final String refSetId, 
			final SnomedMapSetSetting mapSetSetting, final RevisionSearcher revisionSearcher) {
		this.refSetId = checkNotNull(refSetId, "refSetId");
		this.exportContext = checkNotNull(exportContext, "exportContext");
		this.mapSetSetting = checkNotNull(mapSetSetting);
		this.revisionSearcher = checkNotNull(revisionSearcher);
		label = ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(exportContext.getCurrentBranchPath(), refSetId);
	}

	public String getRefSetId() {
		return refSetId;
	}
	
	@Override
	public String getRelativeDirectory() {
		return RF1_CROSSMAP_RELATIVE_DIRECTORY + File.separatorChar + label;
	}

	@Override
	public String getFileName() {
		return new StringBuilder("der1_")
		.append(getFileNameprefix())
		.append("_")
		.append(label)
		.append("_INT_")
		.append(getExportTime(new Date()))
		.append(".txt")
		.toString();
	}
	
	@Override
	public SnomedExportContext getExportContext() {
		return exportContext;
	}
	
	/**
	 * Returns with the file name prefix. E.g.: <b>CrossMaps</b>, <b>CrossMapSets</b>, <b>CrossMapTargets</b>
	 * <p>Clients must implement this method.</p>
	 * @return the file name prefix.
	 */
	protected abstract String getFileNameprefix();
	
	/**
	 * Returns with the per-configured map set settings.
	 * @return the map set setting for the cross map publication.
	 */
	protected final SnomedMapSetSetting getMapSetSetting() {
		return mapSetSetting;
	}
	
	protected final boolean isComplex() {
		return getMapSetSetting().isComplex();
	}
	
	/*returns with the previously configured release time in yyyyMMdd format*/
	private String getExportTime(final Date date) {
		return Dates.formatByGmt(date, DateFormats.SHORT);
	}
	
	protected static final class MapTargetEntry {
		protected String uuid;
		protected String mapTarget;
		protected String mapSource;
		protected String rule;
		protected String advice;
		protected String priority;
		protected String group;
	}
}
