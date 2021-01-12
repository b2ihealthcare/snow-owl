/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.es;

import com.b2international.index.IndexClient;
import com.b2international.index.Searcher;
import com.b2international.index.Writer;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.es.admin.EsIndexAdmin;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 5.10
 */
public final class EsIndexClient implements IndexClient {

	private final EsIndexAdmin admin;
	private final ObjectMapper mapper;

	public EsIndexClient(EsIndexAdmin admin, ObjectMapper mapper) {
		this.admin = admin;
		this.mapper = mapper;
	}

	@Override
	public IndexAdmin admin() {
		return admin;
	}

	@Override
	public Searcher searcher() {
		return new EsDocumentSearcher(admin, mapper);
	}

	@Override
	public Writer writer() {
		return new EsDocumentWriter(admin, searcher(), mapper);
	}

}
