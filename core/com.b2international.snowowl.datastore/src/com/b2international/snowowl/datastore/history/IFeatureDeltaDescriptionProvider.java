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

import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.ecore.EObject;

import com.google.inject.ImplementedBy;

/**
 * Provides description for {@link EObject} feature deltas.
 * @since 3.1
 */
@ImplementedBy(DefaultFeatureDeltaDescriptionProvider.class)
public interface IFeatureDeltaDescriptionProvider {

	/**
	 * Provide a description about the feature delta change within the given {@link EObject}.
	 * @param object
	 * @param featureDelta
	 * @return
	 */
	public String provideDescription(EObject object, CDOSetFeatureDelta featureDelta);

}