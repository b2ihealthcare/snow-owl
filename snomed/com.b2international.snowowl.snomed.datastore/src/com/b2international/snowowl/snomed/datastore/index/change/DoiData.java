/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.change;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

/**
 * Provides Document of Interest/Occurence scores from a data source for a given SNOMED CT Concept identifier, or it should return the
 * {@link SnomedConceptDocument#DEFAULT_DOI} value if there is no score for the specified concept identifier.
 * 
 * @since 5.2
 */
public interface DoiData {

	DoiData DEFAULT_SCORE = new DoiData() {
		@Override
		public float getDoiScore(long conceptId) {
			return SnomedConceptDocument.DEFAULT_DOI;
		}
	};

	float getDoiScore(long conceptId);

}
