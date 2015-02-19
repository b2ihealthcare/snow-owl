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
package com.b2international.snowowl.datastore.history;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.inject.ImplementedBy;

/**
 * Can filter {@link EStructuralFeature}s when collecting history information for {@link CDOObject}s.
 * 
 * @since SDD 1.2
 */
@ImplementedBy(IHistoryFeatureFilter.NullImpl.class)
public interface IHistoryFeatureFilter {

	/**
	 * Return whether the given feature should be included in the history view or not.
	 * 
	 * @param feature
	 * @return <code>false</code> if you want to filter the given {@link EStructuralFeature}, <code>true</code>
	 *         otherwise.
	 */
	boolean filter(EStructuralFeature feature);

	/**
	 * The NullImpl implementation enables all features in the history.
	 * 
	 * @since SDD 1.2
	 */
	public static class NullImpl implements IHistoryFeatureFilter {

		@Override
		public boolean filter(EStructuralFeature feature) {
			return true;
		}

	}

}