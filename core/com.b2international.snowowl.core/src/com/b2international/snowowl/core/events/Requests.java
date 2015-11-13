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
package com.b2international.snowowl.core.events;

import com.b2international.snowowl.core.ServiceProvider;

/**
 * @since 4.5
 */
@SuppressWarnings("rawtypes")
public class Requests {

	private static final Request NOOP = new BaseRequest() {
		private static final long serialVersionUID = -7606836203771540944L;

		@Override
		public Object execute(ServiceProvider context) {
			return null;
		}
		
		@Override
		protected Class getReturnType() {
			return Object.class;
		}
	};
	
	private Requests() {}
	
	@SuppressWarnings("unchecked")
	public static <C extends ServiceProvider, R> Request<C, R> noop() {
		return (Request<C, R>) NOOP;
	}
	
	public static <C extends ServiceProvider> Request<C, Void> noContent(final Request<C, ?> req) {
		return new BaseRequest<C, Void>() {
			@Override
			public Void execute(C context) {
				req.execute(context);
				return null;
			}

			@Override
			protected Class<Void> getReturnType() {
				return Void.class;
			}
		};
	}
	
}
