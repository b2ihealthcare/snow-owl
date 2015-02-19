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
package com.b2international.commons.collections;

import com.google.common.base.Supplier;

/**
 * {@link Supplier} extension.
 * @see #get(F)
 * @param <F> - from type
 * @param <T> - the provided type
 */
public interface ISupplier2<F, T> extends Supplier<T> {

	/**
	 * Returns with the instance created from the given argument.
	 * <br>Clients may decide whether the a new instance has to be created
	 * on each individual {@link #get(Object)} invocation or not. 
	 * @param from the from argument.
	 * @return the new instance that is supplied.
	 */
	T get(final F from);
	
}