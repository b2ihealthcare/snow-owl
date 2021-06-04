/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core;

import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.fhir.core.provider.IConceptMapApiProvider;
import com.b2international.snowowl.fhir.core.provider.IValueSetApiProvider;

/**
 * @since 7.12
 */
@Component
public final class FhirCorePlugin extends Plugin {

	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		ClassPathScanner scanner = env.service(ClassPathScanner.class);
		env.services().registerService(IConceptMapApiProvider.Registry.class, new IConceptMapApiProvider.Registry(scanner));
		env.services().registerService(IValueSetApiProvider.Registry.class, new IValueSetApiProvider.Registry(scanner));
	}
	
}
