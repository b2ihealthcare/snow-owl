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
package com.b2international.snowowl.datastore.setup;

import com.b2international.snowowl.core.setup.DefaultBootstrapFragment;
import com.b2international.snowowl.core.setup.ModuleConfig;
import com.b2international.snowowl.core.setup.ModuleConfigs;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.rpc.RpcConfiguration;

/**
 * @since 3.4
 */
@ModuleConfigs({
		@ModuleConfig(fieldName = "repository", type = RepositoryConfiguration.class),
		@ModuleConfig(fieldName = "rpc", type = RpcConfiguration.class)
})
public class RepositoryBootstrap extends DefaultBootstrapFragment {
}