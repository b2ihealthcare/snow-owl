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
package com.b2international.snowowl.snomed.exporter.server.refset;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.Date;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf1Exporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;

/**
 * Abstract map set RF1 exporter for SNOMED&nbsp;CT simple map type and complex map type reference sets.
 * @see SnomedRf1Exporter
 * @see AbstractSnomedRefSetExporter
 */
public abstract class AbstractSnomedCrossMapExporter implements SnomedRf1Exporter {
	
	private final SnomedMapSetSetting mapSetSetting;
	private final String label;
	private SnomedExportConfiguration configuration;
	private String refSetId;

	protected AbstractSnomedCrossMapExporter(final SnomedExportConfiguration configuration, final String refSetId, final SnomedMapSetSetting mapSetSetting) {
		this.refSetId = checkNotNull(refSetId, "refSetId");
		this.configuration = checkNotNull(configuration, "configuration");
		this.mapSetSetting = mapSetSetting;
		label = ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(getBranchPath(), refSetId);
	}

	protected IBranchPath getBranchPath() {
		return configuration.getCurrentBranchPath();
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
		.append("_" + configuration.getCountryAndNamespaceElement() + "_")
		.append(getExportTime(new Date()))
		.append(".txt")
		.toString();
	}
	
	@Override
	public SnomedExportConfiguration getConfiguration() {
		return configuration;
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
	protected SnomedMapSetSetting getMapSetSetting() {
		return mapSetSetting;
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
