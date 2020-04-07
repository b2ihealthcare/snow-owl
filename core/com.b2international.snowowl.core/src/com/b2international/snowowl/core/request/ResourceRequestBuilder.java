/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 5.2
 * @param <B>
 * @param <C>
 * @param <R>
 */
public abstract class ResourceRequestBuilder<B extends ResourceRequestBuilder<B, C, R>, C extends ServiceProvider, R> extends BaseRequestBuilder<B, C, R> {

	private List<ExtendedLocale> locales = Collections.emptyList();
	
	/**
	 * Sets the request to return the preferred locale for the returned display labels. 
	 * 
	 * @param locales - the locale list in Accept-Language header format
	 * @return ResourceRequestBuilder
	 */
	public final B setLocales(String locales) {
		if (locales != null) {
			setLocales(ExtendedLocale.parseLocales(locales));
		}
		return getSelf();
	}
	
	/**
	 * Sets the request to return the preferred locale for the returned display labels.
	 * 
	 * @param locales for the labels returns by the request
	 * @return ResourceRequestBuilder   
	 */
	public final B setLocales(List<ExtendedLocale> locales) {
		if (!CompareUtils.isEmpty(locales)) {
			this.locales = locales;
		}
		return getSelf();
	}
	
	@Override
	protected final Request<C, R> doBuild() {
		final ResourceRequest<C, R> req = create();
		req.setLocales(locales);
		init(req);
		return req;
	}
	
	/**
	 * Subclasses may override this method to configure the request further with additional properties.
	 *  
	 * @param req - the request instance to configure
	 */
	protected void init(ResourceRequest<C, R> req) {
	}

	/**
	 * @return the request instance
	 */
	protected abstract ResourceRequest<C, R> create();
	
}
