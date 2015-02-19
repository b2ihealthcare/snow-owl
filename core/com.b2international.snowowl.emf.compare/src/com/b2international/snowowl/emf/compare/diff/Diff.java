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
package com.b2international.snowowl.emf.compare.diff;

import javax.annotation.Nullable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.Change;

/**
 * Represents a difference. This interface if used to hide EMF Compare's
 * {@link org.eclipse.emf.compare.Diff} interface from clients.
 * @param <F> the type of the changed feature.
 * @param <V> the value of the changed object for the given {@code F} feature.
 *
 */
public interface Diff<F extends EStructuralFeature, V> {

	/**
	 * Returns with the changed feature.
	 * @return the changed feature.
	 */
	F getChangedFeature();
	
	/**
	 * Returns with the current value of the change.
	 * <p>Could be {@code null} if not set.
	 * @return the actual value of the change.
	 */
	@Nullable V getValue();
	
	/**
	 * Returns with the object that has changed.
	 * @return the changed object.
	 */
	EObject getTarget();
	
	/**
	 * Returns with the change. Could be used to identify whether it was a deletion 
	 * or addition of modification.
	 * @return the change.
	 */
	Change getChange();
	
}