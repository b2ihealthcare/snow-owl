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
package com.b2international.snowowl.datastore.commitinfo;

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.domain.PageableCollectionResource;

/**
 * @since 5.2
 */
public class CommitInfos extends PageableCollectionResource<CommitInfo> {

	private static final long serialVersionUID = 1L;

	public CommitInfos(int offset, int limit, int total) {
		this(Collections.emptyList(), offset, limit, total);
	}

	public CommitInfos(List<CommitInfo> items, int offset, int limit, int total) {
		super(items, offset, limit, total);
	}

}
