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
package com.b2international.snowowl.core.api;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.commons.collections.Procedure;

/**
 * Represents a {@link IMutable mutable}.
 */
public interface IMutable {

	/**
	 * Enables the current mutable. By default this is a <b>not thread-safe</b> method. Client should ensure 
	 * <b>unconditional thread-safety</b> when implementing this method.  
	 */
	void enable();
	
	/**
	 * Disables the current mutable. By default this is a <b>not thread-safe</b> method. Client should ensure 
	 * <b>unconditional thread-safety</b> when implementing this method.  
	 */
	void disable();
	
	/**Procedure to invoke {@link #enable()} on a {@link IMutable mutable} instance.*/
	Procedure<IMutable> ENABLE_PROCEDURE = new Procedure<IMutable>() {
		@Override protected void doApply(final IMutable mutable) {
			checkNotNull(mutable, "mutable").enable();
		}
	};
	
	/**Procedure to invoke {@link #disable()} on a {@link IMutable mutable} instance.*/
	Procedure<IMutable> DISABLE_PROCEDURE = new Procedure<IMutable>() {
		@Override protected void doApply(final IMutable mutable) {
			checkNotNull(mutable, "mutable").disable();
		}
	};
	
}