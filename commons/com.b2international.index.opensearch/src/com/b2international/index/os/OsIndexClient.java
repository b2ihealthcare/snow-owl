/*
 * Copyright 2017-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.os;

import com.b2international.index.ClusterStatus;
import com.b2international.index.IndexClient;
import com.b2international.index.Searcher;
import com.b2international.index.Writer;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.os.admin.OsIndexAdmin;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 5.10
 */
public final class OsIndexClient implements IndexClient {

	private final OsIndexAdmin admin;
	private final ObjectMapper mapper;

	public OsIndexClient(OsIndexAdmin admin, ObjectMapper mapper) {
		this.admin = admin;
		this.mapper = mapper;
	}

	@Override
	public IndexAdmin admin() {
		return admin;
	}

	@Override
	public Searcher searcher() {
		return new OsDocumentSearcher(admin, mapper);
	}

	@Override
	public Writer writer() {
		return new OsDocumentWriter(admin, searcher(), mapper);
	}
	
	@Override
	public ClusterStatus status(String... indices) {
		return admin.client().status(indices);
	}
}
