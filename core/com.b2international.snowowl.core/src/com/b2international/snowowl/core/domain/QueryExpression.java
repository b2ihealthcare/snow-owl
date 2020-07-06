/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

import java.io.Serializable;

/**
 * @since 7.7
 */
public final class QueryExpression implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String id;
	private final String query;
	private final boolean pinned;

	public QueryExpression(String id, String query, boolean pinned) {
		this.id = id;
		this.query = query;
		this.pinned = pinned;
	}

	public String getId() {
		return id;
	}

	public String getQuery() {
		return query;
	}

	public boolean isPinned() {
		return pinned;
	}
}
