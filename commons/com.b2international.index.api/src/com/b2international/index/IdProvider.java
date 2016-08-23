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
package com.b2international.index;

/**
 * @since 5.0
 * @param <T>
 */
public interface IdProvider<T> {

	/**
	 * Returns the id of the given object.
	 * 
	 * @param object
	 * @return
	 */
	String getId(T object);

	/**
	 * Returns the same identifier for all objects. Useful when updating a single document with a known identifier.
	 * 
	 * @since 5.0
	 */
	class ConstantIdProvider<T> implements IdProvider<T> {

		private final String identifier;

		public ConstantIdProvider(String identifier) {
			this.identifier = identifier;
		}

		@Override
		public String getId(T object) {
			return identifier;
		}

	}

	/**
	 * {@link IdProvider} implementation that works with {@link WithId} subtypes.
	 * 
	 * @since 5.0
	 */
	IdProvider<WithId> WITH_ID = new IdProvider<WithId>() {
		@Override
		public String getId(WithId object) {
			return object._id();
		}
	};

}
