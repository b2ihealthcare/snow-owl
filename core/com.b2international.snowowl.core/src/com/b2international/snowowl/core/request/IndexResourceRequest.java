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
package com.b2international.snowowl.core.request;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ServiceProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.5
 */
public abstract class IndexResourceRequest<C extends ServiceProvider, R> extends ResourceRequest<C, R> {

	@NotNull
	private Options expand;

	private List<String> fields = Collections.emptyList();
	
	@JsonProperty
	protected final Options expand() {
		return expand;
	}
	
	@JsonProperty
	protected final List<String> fields() {
		return fields;
	}
	
	final void setExpand(Options expand) {
		this.expand = expand;
	}
	
	final void setFields(List<String> fields) {
		this.fields = fields;
	}
	
}
