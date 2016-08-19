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
package com.b2international.snowowl.snomed.datastore.index.refset;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.base.Function;

/**
 * Contains frequently used utility functions related to {@link SnomedRefSetMemberIndexEntry}s.
 *
 */
public abstract class SnomedRefSetMemberIndexEntryFunctions {

	public static final Function<SnomedRefSetMemberIndexEntry, String> EXTRACT_REFSET_ID = new Function<SnomedRefSetMemberIndexEntry, String>() {
		@Override
		public String apply(final SnomedRefSetMemberIndexEntry entry) {
			return entry.getRefSetIdentifierId();
		}
	};

	private SnomedRefSetMemberIndexEntryFunctions() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}