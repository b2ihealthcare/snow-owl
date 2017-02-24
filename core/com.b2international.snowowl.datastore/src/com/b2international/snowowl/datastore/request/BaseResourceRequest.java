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
package com.b2international.snowowl.datastore.request;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.6
 */
public abstract class BaseResourceRequest<C extends ServiceProvider, B> implements Request<C, B> {

	@NotNull
	private List<ExtendedLocale> locales;

	@NotNull
	private Options expand;

	private Set<String> fields = Collections.emptySet();
	
	@JsonProperty
	protected final List<ExtendedLocale> locales() {
		return locales;
	}
	
	@JsonProperty
	protected final Options expand() {
		return expand;
	}
	
	@JsonProperty
	protected final Set<String> fields() {
		return fields;
	}
	
	final void setLocales(List<ExtendedLocale> locales) {
		this.locales = locales;
	}
	
	final void setExpand(Options expand) {
		this.expand = expand;
	}
	
	final void setFields(Set<String> fields) {
		this.fields = fields;
	}
	
}
