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

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.index.diff.NodeChange;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.NodeTransformer;
import com.b2international.snowowl.datastore.index.diff.NodeTransformerCache;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;

/**
 * {@link NodeTransformerService} service implementation.
 * <br>This service is registered to the {@link ApplicationContext application context} as {@link NodeTransformerService}.
 *
 */
public class NodeTransformerServiceImpl implements NodeTransformerService {

	@Override
	public NodeChange transform(final VersionCompareConfiguration configuration, final NodeDiff diff) {
		final NodeTransformer transformer = NodeTransformerCache.INSTANCE.getTransformer(checkNotNull(configuration, "configuration").getRepositoryUuid());
		return transformer.transform(configuration, checkNotNull(diff, "diff"));
	}

}