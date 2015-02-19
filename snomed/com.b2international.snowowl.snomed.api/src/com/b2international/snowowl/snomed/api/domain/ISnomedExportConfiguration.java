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
package com.b2international.snowowl.snomed.api.domain;

import java.util.Collection;
import java.util.Date;

import com.b2international.snowowl.snomed.api.ISnomedExportService;

/**
 * Representation of a configuration used by the 
 * {@link ISnomedExportService SNOMED&nbsp;CT export service}.
 *
 */
public interface ISnomedExportConfiguration {

	/**
	 * Returns with the RF2 release type of the current export configuration.
	 * @return the desired RF2 release type.
	 */
	Rf2ReleaseType getRf2ReleaseType();
	
	/**
	 * Returns the code system version identifier, eg. "{@code 2014-01-31}".
	 * @return the code system version identifier
	 */
	String getVersion();
	
	/**
	 * Returns the task identifier, eg. "{@code 1747}". A {@code null} value points to the repository version,
	 * when the component is not part of an editing task.
	 * @return the task identifier, or {@code null} in case of a component on a version
	 */
	String getTaskId();
	
	/**
	 * Returns with the delta export start effective time.
	 * <br>Can be {@code null} even 
	 * if the {@link Rf2ReleaseType release type} is {@link Rf2ReleaseType#DELTA delta}.
	 */
	Date getDeltaExportStartEffectiveTime();

	/**
	 * Returns with the delta export end effective time.
	 * <br>May return with {@code null} even 
	 * if the {@link Rf2ReleaseType release type} is {@link Rf2ReleaseType#DELTA delta}.
	 */
	Date getDeltaExportEndEffectiveTime();
	
	/**
	 * Returns with the namespace ID.
	 * <p>The namespace ID will be used when generating the folder structure 
	 * for the RF2 release format export.
	 * @return the namespace ID.
	 */
	String getNamespaceId();
	
	/**
	 * Returns with a collection of SNOMED&nbsp;CT module concept IDs.
	 * <p>This collection of module IDs will define which components will be included in the export.
	 * Components having a module that is not included in the returning set will be excluded from 
	 * the export result.
	 * @return a collection of module dependency IDs.
	 */
	Collection<String> getModuleDependencyIds();
	
}