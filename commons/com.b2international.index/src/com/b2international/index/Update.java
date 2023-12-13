/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Map;

/**
 * @since 8.1.0
 * 
 * @param <T>
 */
public final class Update<T> {

	private final Class<T> type;
	private final String key;
	private final String script;
	private final Map<String, Object> params;
	private final T upsert;

	public Update(final Class<T> type, final String key, final String script, final Map<String, Object> params) {
		this(type, key, script, params, null);
	}

	public Update(final Class<T> type, final String key, final String script, final Map<String, Object> params, final T upsert) {
		this.type = type;
		this.key = key;
		this.script = script;
		this.params = params;
		this.upsert = upsert;
	}

	public Class<T> getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public String getScript() {
		return script;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public T getUpsert() {
		return upsert;
	}
}
