/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.identity;

import java.util.Map;

import com.b2international.snowowl.core.setup.Environment;

/**
 * @since 5.11
 */
public interface IdentityProviderFactory {

	/**
	 * Creates a new {@link IdentityProvider} instance based on the given {@link Environment} and configuration properties.
	 * 
	 * @param env
	 * @param configuration
	 * @return
	 * @throws Exception 
	 */
	IdentityProvider create(Environment env, Map<String, Object> configuration) throws Exception;

	/**
	 * Returns the type of the identity provider this factory can create.
	 * 
	 * @return
	 */
	String getType();

}
