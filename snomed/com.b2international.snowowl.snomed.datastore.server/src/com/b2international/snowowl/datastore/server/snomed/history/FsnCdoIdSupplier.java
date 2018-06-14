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
package com.b2international.snowowl.datastore.server.snomed.history;

import static com.b2international.snowowl.datastore.cdo.CDOUtils.NO_STORAGE_KEY;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;

import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.google.common.base.Supplier;

/**
 * Lazily supplies supplies the CDO ID of the 'Fully specified name' description type concept.
 *
 */
public enum FsnCdoIdSupplier implements Supplier<Long> {

	/**The shared singleton supplier.*/
	INSTANCE {

		private long stoageKey = NO_STORAGE_KEY;
		private final Object mutex = new Object();

		@Override
		public Long get() {
			if (NO_STORAGE_KEY == stoageKey) {
				synchronized (mutex) {
					if (NO_STORAGE_KEY == stoageKey) {
						final long stoageKey = getStorageKey(FULLY_SPECIFIED_NAME);
						if (NO_STORAGE_KEY != stoageKey) {
							this.stoageKey = stoageKey;
						}
					}
				}
			}
			return stoageKey;
		}
		
		private long getStorageKey(String conceptId) {
			return new SnomedConceptLookupService().getStorageKey(BranchPathUtils.createMainPath(), Concepts.FULLY_SPECIFIED_NAME);
		}
		
	};

	
	
}