/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import java.io.File;
import java.util.Map;

import com.b2international.index.admin.FSIndexAdmin;
import com.b2international.index.admin.RAMIndexAdmin;
import com.b2international.index.mapping.Mappings;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

/**
 * @since 4.7
 */
public class LuceneIndexClientFactory implements IndexClientFactory {

	@Override
	public IndexClient createClient(String name, ObjectMapper mapper, Mappings mappings, Map<String, Object> settings) {
		mapper.registerModule(new AfterburnerModule());
		if (settings.containsKey(IndexClientFactory.DATA_DIRECTORY)) {
			final Object dir = settings.get(IndexClientFactory.DATA_DIRECTORY);
			final File directory = dir instanceof File ? (File) dir : new File((String) dir);
			return new LuceneIndexClient(new FSIndexAdmin(directory, name, mapper, mappings, settings), mapper);
		} else {
			return new LuceneIndexClient(new RAMIndexAdmin(name, mappings, settings), mapper);
		}
	}
	
}
