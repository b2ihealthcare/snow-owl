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
package com.b2international.index;

import com.b2international.index.query.Query;

/**
 * @since 6.0
 */
public final class Scroll<T> {

	private final Class<T> select;
	private final Class<?> from;
	private final String scrollId;
	private final String keepAlive;

	public Scroll(Class<T> select, String scrollId) {
		this(select, select, scrollId);
	}
	
	public Scroll(Class<T> select, Class<?> from, String scrollId) {
		this(select, from, scrollId, Query.DEFAULT_SCROLL_KEEP_ALIVE);
	}
	
	public Scroll(Class<T> select, Class<?> from, String scrollId, String keepAlive) {
		this.select = select;
		this.from = from;
		this.scrollId = scrollId;
		this.keepAlive = keepAlive;
	}
	
	public Class<T> getSelect() {
		return select;
	}
	
	public Class<?> getFrom() {
		return from;
	}
	
	public String getScrollId() {
		return scrollId;
	}
	
	public String getKeepAlive() {
		return keepAlive;
	}
	
}
