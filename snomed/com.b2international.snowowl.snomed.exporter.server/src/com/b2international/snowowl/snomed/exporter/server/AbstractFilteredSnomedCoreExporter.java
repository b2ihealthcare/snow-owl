/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.exporter.server;

import java.io.IOException;

import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;

/**
 * @since 5.10.12
 */
public abstract class AbstractFilteredSnomedCoreExporter<T extends SnomedDocument> extends AbstractSnomedCoreExporter<T> {

	protected AbstractFilteredSnomedCoreExporter(SnomedExportContext exportContext, Class<T> clazz, RevisionSearcher revisionSearcher) {
		super(exportContext, clazz, revisionSearcher);
	}

	@Override
	public boolean hasNext() {
		
		if (getCurrentIndex() == getHits().getHits().size() && getCurrentOffset() != getHits().getTotal()) {
			if (!getNextSearchResult()) {
				return false;
			}
		}
		
		return getHits().getHits().size() > 0 && getCurrentIndex() < getHits().getHits().size();
	}

	private boolean getNextSearchResult() {
		
		try {
			
			while (getCurrentOffset() != getHits().getTotal()) {
				
				final Query<T> exportQuery = Query.select(getClazz()).where(getQueryExpression()).offset(getCurrentOffset()).limit(PAGE_SIZE).build();
				Hits<T> allResults = getRevisionSearcher().search(exportQuery);
				setHits(filter(allResults));
				
				setCurrentIndex(0);
				setCurrentOffset(getCurrentOffset() + allResults.getHits().size());
				
				if (!getHits().getHits().isEmpty()) {
					return true;
				}
				
			}
			
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
		
		return false;
	}

	protected abstract Hits<T> filter(Hits<T> allResults) throws IOException;
	
}
