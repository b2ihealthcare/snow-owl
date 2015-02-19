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
import com.google.common.primitives.Ints;

/**
 * Comparator for {@link NodeDelta} instances.
 *
 */
public enum NodeDeltaComparator implements Comparator<NodeDelta> {

	/**Shared singleton for comparing {@link NodeDelta node delta}s.*/
	INSTANCE;
	
	private static final Comparator<String> DELEGATE = new AlphaNumericComparator();
	
	@Override
	public int compare(final NodeDelta o1, final NodeDelta o2) {
		return compareByComponentType(o1, o2);
	}
	
	/**Compares the deltas based on their component type. */
	public static int compareByComponentType(final NodeDelta o1, final NodeDelta o2) {
		final int result = Ints.compare(o1.getTerminologyComponentId(), o2.getTerminologyComponentId());
		return 0 == result ? compareByLabel(o1, o2) : result;
	}

	/**Compares the deltas based on their label. */
	public static int compareByLabel(final NodeDelta o1, final NodeDelta o2) {
		final int result = DELEGATE.compare(o1.getLabel(), o2.getLabel());
		return 0 == result ? compareByChange(o1, o2) : result;
	}
	
	/**Compares the deltas based on their change type. */
	public static int compareByChange(final NodeDelta o1, final NodeDelta o2) {
		final int result = o1.getChange().compareTo(o2.getChange());
		return 0 == result ? FeatureChangeComparator.INSTANCE.compare(o1, o2) : result;
	}

}