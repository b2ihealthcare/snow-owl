/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.ServiceProvider;

/**
 * @since 7.7
 */
public abstract class ResourceExpander {

	public static final int DEFAULT_LIMIT = 50;
	
	private final ServiceProvider context;
	private final Options expand;
	private final List<ExtendedLocale> locales;

	protected ResourceExpander(ServiceProvider context, Options expand, List<ExtendedLocale> locales) {
		this.context = checkNotNull(context, "context");
		this.expand = expand == null ? OptionsBuilder.newBuilder().build() : expand;
		this.locales = locales == null ? Collections.<ExtendedLocale>emptyList() : locales;
	}

	protected final Options expand() {
		return expand;
	}

	protected ServiceProvider context() {
		return context;
	}
	
	protected final List<ExtendedLocale> locales() {
		return locales;
	}

	protected final int getLimit(final Options expandOptions) {
		return expandOptions.containsKey("limit") ? expandOptions.get("limit", Integer.class) : DEFAULT_LIMIT;
	}

}
