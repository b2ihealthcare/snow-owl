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
package com.b2international.snowowl.snomed.refset.core.automap;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.NullComponent;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

/**
 * @since 3.1
 */
public class SnomedRefSetAutoMapper extends AbstractRefSetAutoMapper {

	public SnomedRefSetAutoMapper(IComponent<String> topLevelConcept, RefSetAutoMapperModel model) {
		super(topLevelConcept, model);
	}

	@Override
	protected String getFirstValidResult(final List<IIndexQueryAdapter<SnomedConceptIndexEntry>> adapters, final int limit, Integer rowIndex) {
		for (final IIndexQueryAdapter<SnomedConceptIndexEntry> adapter : adapters) {

			final List<SnomedConceptIndexEntry> results = indexSearcher.search(adapter, limit);

			//	no matching term found
			if (results == null || results.size() == 0) {
				continue;
			}

			for (final SnomedConceptIndexEntry entry : results) {
				if (topLevelConcept == NullComponent.<String> getNullImplementation()) {
					return entry.getId();
				}

				final Collection<SnomedConceptIndexEntry> conceptSuperTypes = terminologyBrowser.getAllSuperTypes(entry);

				if (conceptSuperTypes.contains(topLevelConcept)) {
					return entry.getId();
				}
			}
		}

		return null;
	}

}