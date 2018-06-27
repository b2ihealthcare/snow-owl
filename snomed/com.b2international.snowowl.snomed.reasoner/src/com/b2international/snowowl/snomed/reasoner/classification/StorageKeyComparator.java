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
package com.b2international.snowowl.snomed.reasoner.classification;

import java.util.Comparator;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.google.common.primitives.Longs;

/**
 * 
 */
public enum StorageKeyComparator implements Comparator<SnomedConceptDocument> {
	INSTANCE;
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final SnomedConceptDocument o1, final SnomedConceptDocument o2) {
		return Longs.compare(o1.getStorageKey(), o2.getStorageKey());
	}
}