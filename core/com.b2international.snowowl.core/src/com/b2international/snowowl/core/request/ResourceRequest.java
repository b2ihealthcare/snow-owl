/*
 * Copyright 2011-2024 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.6
 * 
 * @param <C>
 * @param <R>
 */
public abstract class ResourceRequest<C extends ServiceProvider, R> implements Request<C, R> {

	@NotNull
	private List<ExtendedLocale> locales;
	
	@JsonProperty
	protected final List<ExtendedLocale> locales() {
		return locales;
	}
	
	final void setLocales(List<ExtendedLocale> locales) {
		this.locales = locales;
	}
	
}
