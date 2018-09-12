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
package com.b2international.snowowl.core.ft;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

/**
 * Simple implementation of a singleton application level feature toggling service.
 * @since 5.0
 */
public class FeatureToggles {

	private final ConcurrentMap<String, Boolean> featureToggles = new MapMaker().makeMap();
	
	public boolean isEnabled(String feature) {
		return exists(feature) ? check(feature) : false;
	}
	
	public boolean isDisabled(String feature) {
		return !isEnabled(feature);
	}
	
	public void enable(String feature) {
		featureToggles.put(feature, Boolean.TRUE);
	}
	
	public void disable(String feature) {
		featureToggles.put(feature, Boolean.FALSE);
	}
	
	private boolean check(String feature) {
		checkArgument(exists(feature), "Unknown feature: " + feature);
		return Boolean.TRUE.equals(featureToggles.get(feature));
	}
	
	private boolean exists(String feature) {
		return featureToggles.containsKey(feature);
	}
	
}
