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

import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.json.JsonDocumentSearcher;
import com.b2international.index.json.JsonDocumentWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public final class LuceneIndexClient implements IndexClient {

	private final LuceneIndexAdmin admin;
	private final ObjectMapper mapper;

	public LuceneIndexClient(LuceneIndexAdmin admin, ObjectMapper mapper) {
		this.admin = admin;
		this.mapper = mapper;
	}
	
	@Override
	public IndexAdmin admin() {
		return admin;
	}
	
	@Override
	public Writer writer() {
		return new JsonDocumentWriter(admin, mapper);
	}
	
	@Override
	public Searcher searcher() {
		return new JsonDocumentSearcher(admin, mapper);
	}
	
	@Override
	public void close() {
		admin.close();
	}

}
