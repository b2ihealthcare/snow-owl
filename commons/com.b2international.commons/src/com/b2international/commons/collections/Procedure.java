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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * Represents a procedure. A function without and return value.
 * @param <F> - type of the input
 * @see Function
 */
public abstract class Procedure<F> implements Function<F, Void> {

	/*
	 * (non-Javadoc)
	 * @see com.google.common.base.Function#apply(java.lang.Object)
	 */
	@Override
	public final Void apply(final F input) {
		
		doApply(Preconditions.checkNotNull(input, "Input argument cannot be null."));
		
		return com.b2international.commons.Void.VOID;
		
	};
	
	/**Apply the function on the given input.*/
	protected abstract void doApply(final F input);
}