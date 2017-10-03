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
package com.b2international.snowowl.snomed.exporter.server.sandbox;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.ContentSubType;

/**
 * Wraps several configuration attributes for the SNOMED&nbsp;CT 
 * export process.
 */
public interface SnomedExportConfiguration {

	/**
	 * Returns with the current branch path of the client who 
	 * triggered the export. 
	 * @return the client's current branch path.
	 */
	IBranchPath getCurrentBranchPath();
	
	/**
	 * Returns with the RF2 content subtype for the export.
	 * <br>Full, snapshot or delta.
	 * @return the RF2 content subtype.
	 */
	ContentSubType getContentSubType();

	/**
	 * Returns with the delta export start effective time. Can be {@code null} even 
	 * if the {@link ContentSubType} is {@link ContentSubType#DELTA delta}.
	 */
	@Nullable Date getDeltaExportStartEffectiveTime();

	/**
	 * Returns with the delta export end effective time. Can be {@code null} even 
	 * if the {@link ContentSubType} is {@link ContentSubType#DELTA delta}.
	 */
	@Nullable Date getDeltaExportEndEffectiveTime();
	
	/**
	 * Returns with a mapping between the branch path for each available version and the index segment names for the versions. 
	 * @return a mapping between branch paths and index segment names.
	 */
	Map<IBranchPath, Collection<String>> getVersionPathToSegmentNameMappings();

	/**
	 * Returns the label to use when a component does not have an effective time assigned. Defaults to {@link EffectiveTimes#UNSET_EFFECTIVE_TIME_LABEL}. 
	 * @return the effective time label for unpublished components
	 */
	String getUnsetEffectiveTimeLabel();
	
	/**
	 * Returns with "INT" in case no language preference is set, or with 
	 * the combination of country code and namespace otherwise
	 */
	String getCountryAndNamespaceElement();
}
