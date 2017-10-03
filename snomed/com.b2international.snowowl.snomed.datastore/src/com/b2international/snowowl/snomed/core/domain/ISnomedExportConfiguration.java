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
package com.b2international.snowowl.snomed.core.domain;

import java.util.Collection;
import java.util.Date;

/**
 * Represents the configuration object used by a SNOMED CT RF2 Export implementation.
 */
public interface ISnomedExportConfiguration extends ISnomedRF2Configuration {

	/**
	 * Returns the transient effective time to use for unpublished components.
	 * 
	 * @return the transient effective time, or {@code null} if the default {@code UNPUBLISHED} value should be printed
	 * for unpublished components
	 */
	String getTransientEffectiveTime();
	
	/**
	 * Returns the export's starting effective time range. Only applicable if the release type is set to
	 * {@link Rf2ReleaseType#DELTA}.
	 * 
	 * @return the starting effective time range, or {@code null} if the export has an open-ended beginning date
	 */
	Date getDeltaExportStartEffectiveTime();

	/**
	 * Returns the export's ending effective time range. Only applicable if the release type is set to
	 * {@link Rf2ReleaseType#DELTA}.
	 * <p>
	 * If both beginning and ending dates are set to {@code null}, all unpublished components will be exported.
	 * 
	 * @return the ending effective time range, or {@code null} if the export has an open-ended finishing date
	 */
	Date getDeltaExportEndEffectiveTime();

	/**
	 * Returns the country/namespace element to use when generating file and folder names for the RF2 export.
	 * 
	 * @return the country/namespace element to use in exported file and folder names
	 */
	String getCountryAndNamespaceId();

	/**
	 * Returns a collection of SNOMED CT modules selected for export.
	 * <p>
	 * This collection of module IDs will define which components will be included in the export. Components having a
	 * module that is not included in the returning set will be excluded from the resulting archive.
	 * 
	 * @return a collection of module identifiers
	 */
	Collection<String> getModuleIds();
}
