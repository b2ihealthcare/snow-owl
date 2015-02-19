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
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.collections.Procedure;

/**
 * Utility class for {@link Lifecycle} instances.
 *
 */
public abstract class LifecycleUtils {

	/**Procedure calling {@link Lifecycle#deactivate()} on the object if instance of {@link Lifecycle}, otherwise does nothing.*/
	private static final Procedure<Object> DEACTIVATE_FUNCTION = new Procedure<Object>() {
		@Override protected void doApply(final Object input) {
			LifecycleUtil.deactivate(input);
		}
	};

	/**
	 * Deactivates each element of the given iterable.
	 * @param iterable objects to deactivate.
	 */
	public static void deactivate(final Iterable<? extends Object> iterable) {
		Collections3.forEach(iterable, DEACTIVATE_FUNCTION);
	}
		
	private LifecycleUtils() { /*hahaha!!, cannot instantiate*/ }

}