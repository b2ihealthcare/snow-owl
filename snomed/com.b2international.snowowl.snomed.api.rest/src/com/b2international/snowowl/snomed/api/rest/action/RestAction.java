/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.action;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public final class RestAction {

	private String action;

	private Map<String, Object> source = newHashMap();

	@JsonCreator
	RestAction(@JsonProperty("action") String action) {
		this.action = action;
	}

	@JsonAnySetter
	void setSource(String key, Object value) {
		source.put(key, value);
	}

	/**
	 * Converts this {@link RestAction} to a {@link Request} with the given {@link ActionResolver} that can be executed.
	 *
	 * @param resolver
	 *            - the resolver to use for {@link Request} resolution
	 * @return
	 */
	public Request<ServiceProvider, ?> resolve(ActionResolver resolver) {
		return resolver.resolve(action, source);
	}

}
