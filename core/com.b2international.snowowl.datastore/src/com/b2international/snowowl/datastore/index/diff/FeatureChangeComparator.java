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

import java.util.Comparator;

import com.b2international.commons.AlphaNumericComparator;

/**
 * Comparator for {@link FeatureChange feature change}s.
 *
 */
public enum FeatureChangeComparator implements Comparator<FeatureChange> {

	/**Shared comparator for {@link FeatureChange} instances.*/
	INSTANCE;
	
	private static final Comparator<String> DELEGATE = new AlphaNumericComparator();
	
	@Override
	public int compare(final FeatureChange o1, final FeatureChange o2) {
		return compareByFeatureName(o1, o2);
	}
	
	/**Compare the changes by their feature names. If equals, falls back to the {@link FeatureChange#getFromValue()} comparison.*/
	public static int compareByFeatureName(final FeatureChange o1, final FeatureChange o2) {
		final int result = DELEGATE.compare(o1.getFeatureName(), o2.getFeatureName());
		return 0 == result ? compareByFromValue(o1, o2) : result;
	}
	
	/**Compare the changes by their from values. If equals, falls back to the {@link FeatureChange#getToValue()} comparison.*/
	public static int compareByFromValue(final FeatureChange o1, final FeatureChange o2) {
		final int result = DELEGATE.compare(o1.getFromValue(), o2.getFromValue());
		return 0 == result ? compareByToValue(o1, o2) : result;
	}
	
	/**Compare the changes by their to values.*/
	public static int compareByToValue(final FeatureChange o1, final FeatureChange o2) {
		return DELEGATE.compare(o1.getToValue(), o2.getToValue());
	}

}