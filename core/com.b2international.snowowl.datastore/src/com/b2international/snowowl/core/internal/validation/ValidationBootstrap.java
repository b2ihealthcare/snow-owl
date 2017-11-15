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
package com.b2international.snowowl.core.internal.validation;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.DefaultBootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.datastore.config.IndexSettings;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 6.0
 */
public class ValidationBootstrap extends DefaultBootstrapFragment {

	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		if (env.isEmbedded() || env.isServer()) {
			final Index validationIndex = Indexes.createIndex(
				"validations", 
				env.service(ObjectMapper.class), 
				new Mappings(ValidationIssue.class, ValidationRule.class), 
				env.service(IndexSettings.class)
			);
			env.services().registerService(ValidationRepository.class, new ValidationRepository(validationIndex));
			// TODO initialize repository with default validation rules if the repository is empty
		}
	}
	
}
