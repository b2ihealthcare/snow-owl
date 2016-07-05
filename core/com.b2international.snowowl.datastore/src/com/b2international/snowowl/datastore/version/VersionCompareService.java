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

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.datastore.index.diff.CompareResult;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;

/**
 * Service for comparing the content of a terminology between two given versions.
 * @deprecated - reimplement using {@link RevisionIndex}
 */
public interface VersionCompareService {

	/**
	 * Compares the content of a terminology between two given versions (given as a configuration) and 
	 * returns with a collection representing a change set.
	 * @param configuration configuration for the compare operation.
	 * @param monitor for monitoring the progress of the compare. Optional. Can be {@code null}.
	 * @return a model instance representing the change set between two given versions. 
	 */
	CompareResult compare(final VersionCompareConfiguration configuration, @Nullable final IProgressMonitor monitor);
	
}