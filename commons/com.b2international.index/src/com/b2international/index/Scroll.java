/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import com.b2international.index.query.IndexSelection;
import com.b2international.index.query.Query;
import com.google.common.base.Strings;

/**
 * @since 6.0
 */
public final class Scroll<T> {

	private final IndexSelection<T> selection;
	private final String scrollId;
	private final String keepAlive;
	private final List<String> fields;

	public Scroll(IndexSelection<T> selection, List<String> fields, String scrollId, String keepAlive) {
		this.selection = selection;
		this.scrollId = scrollId;
		this.keepAlive = Strings.isNullOrEmpty(keepAlive) ? Query.DEFAULT_SCROLL_KEEP_ALIVE : keepAlive;
		this.fields = fields;
	}

	public IndexSelection<T> getSelection() {
		return selection;
	}
	
	public String getScrollId() {
		return scrollId;
	}
	
	public String getKeepAlive() {
		return keepAlive;
	}
	
	public List<String> getFields() {
		return fields;
	}
	
}
