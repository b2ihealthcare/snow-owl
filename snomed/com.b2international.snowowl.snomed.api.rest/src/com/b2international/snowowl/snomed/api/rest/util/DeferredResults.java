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
package com.b2international.snowowl.snomed.api.rest.util;

import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.events.Action;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 4.5
 */
public class DeferredResults {

	private DeferredResults() {
	}

	/**
	 * Constructs a {@link DeferredResult} for an async action executed via the {@link IEventBus}.
	 * 
	 * @param bus
	 *            - the bus to use when executing the action
	 * @param action
	 *            - the action to send and execute
	 * @param responseType
	 *            - the type of the response object
	 * @return
	 */
	public static <S extends ServiceProvider, B> DeferredResult<B> of(IEventBus bus, Action<S, B> action, Class<B> responseType) {
		final DeferredResult<B> result = new DeferredResult<>();
		action.send(bus, responseType)
			.then(new Procedure<B>() {
				@Override
				protected void doApply(B input) {
					result.setResult(input);
				}
			}).fail(new Procedure<Throwable>() {
				@Override
				protected void doApply(Throwable err) {
					result.setErrorResult(err);
				}
			});
		return result;
	}
	
	public static <S extends ServiceProvider, B> DeferredResult<CollectionResource<B>> ofCollection(IEventBus bus, Action<S, CollectionResource<B>> action, Class<B> responseType) {
		final DeferredResult<CollectionResource<B>> result = new DeferredResult<>();
		action.send(bus, CollectionResource.class)
			.then(new Procedure<CollectionResource>() {
				@Override
				protected void doApply(CollectionResource input) {
					result.setResult(input);
				}
			}).fail(new Procedure<Throwable>() {
				@Override
				protected void doApply(Throwable err) {
					result.setErrorResult(err);
				}
			});
		return result;
	}
	
}
