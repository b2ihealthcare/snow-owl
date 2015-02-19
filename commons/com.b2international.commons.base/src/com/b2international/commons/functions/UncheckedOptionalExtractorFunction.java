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
package com.b2international.commons.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * {@link Function} implementation to extract the wrapped object from an {@link Optional}.
 * This implementation assumes that all optionals are {@link Optional#isPresent() present}; 
 * if not, an {@link IllegalStateException} is thrown.
 *
 * @param <T> the type of the {@link Optional}
 */
public class UncheckedOptionalExtractorFunction<T> implements Function<Optional<T>, T> {

	/**
	 * @throws IllegalStateException if the input is not {@link Optional#isPresent() present}
	 */
	@Override
	public T apply(Optional<T> input) {
		return input.get();
	}

}