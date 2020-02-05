/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.identity.file;

import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.IdentityProviderFactory;

/**
 * @since 5.11
 */
public final class FileIdentityProviderFactory implements IdentityProviderFactory<FileIdentityProviderConfig> {

	@Override
	public IdentityProvider create(Environment env, FileIdentityProviderConfig configuration) throws Exception {
		return new FileIdentityProvider(env.getConfigPath().resolve(configuration.getName()));
	}

	@Override
	public Class<FileIdentityProviderConfig> getConfigType() {
		return FileIdentityProviderConfig.class;
	}

}
