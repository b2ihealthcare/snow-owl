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
package com.b2international.snowowl.datastore.index.diff;

import java.io.Serializable;

import javax.annotation.Nullable;

/**
 * Represents a feature change.
 *
 */
public interface FeatureChange extends Serializable {

	/**
	 * Shared empty feature change.
	 * @see EmptyFeatureChange#EMPTY_INSTANCE
	 */
	FeatureChange EMPTY_INSTANCE = EmptyFeatureChange.EMPTY_INSTANCE;
	
	/**
	 * Returns with the humane readable name of the changed attribute/reference of the current delta.
	 * <p>Could be {@code null}.
	 * @return the human readable name of the attribute that changed.
	 */
	@Nullable String getFeatureName();
	
	/**
	 * Returns with the human readable name of the old value of the changed attribute/reference.
	 * <p>Could be {@code null}.
	 * @return the old/from value of the changed attribute/reference.
	 */
	@Nullable String getFromValue();
	
	/**
	 * Returns with the human readable name of the new value of the changed attribute/reference.
	 * <p>Could be {@code null}.
	 * @return the new/to value of the changed attribute/reference.
	 */
	@Nullable String getToValue();
	
}