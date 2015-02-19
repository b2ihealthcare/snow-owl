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

import com.b2international.snowowl.datastore.index.diff.NodeChange;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;

/**
 * Service for transforming an arbitrary number of {@link NodeDiff node diff}s into a {@link NodeChange}.
 *
 */
public interface NodeTransformerService {

	/**
	 * Transforms {@link NodeDiff} instances into a {@link NodeChange}.
	 * @param configuration the configuration for the transformation.
	 * @param diff the node to transform.
	 * @return the transformed {@link NodeChange}.
	 */
	NodeChange transform(final VersionCompareConfiguration configuration, final NodeDiff diff);
	
}