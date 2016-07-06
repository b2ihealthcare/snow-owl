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
package com.b2international.snowowl.snomed.exporter.server.rf1;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;

/**
 * Abstract iterator for RF1 format exports.
 * The iterator loads chunks of data specified by the PAGE_SIZE constant.
 */
public abstract class AbstractSnomedRf1Exporter<T extends SnomedDocument> implements SnomedRf1Exporter {
	
	protected SnomedExportContext exportContext;
	protected Id2Rf1PropertyMapper mapper;
	
	//never been queried
	private int totalSize = -1;
	
	private int currentOffset;
	private int currentIndex;
	private Hits<T> conceptHits;
	private Class<T> clazz;
	private final static int PAGE_SIZE = 100;
	protected RevisionSearcher revisionSearcher;
	
	/**
	 * 
	 * @param clazz type to manage
	 * @param id2Rf1PropertyMapper 
	 * @param snomedExportConfiguration 
	 */
	public AbstractSnomedRf1Exporter(final Class<T> clazz, final SnomedExportContext snomedExportConfiguration, 
			final Id2Rf1PropertyMapper id2Rf1PropertyMapper, final RevisionSearcher revisionSearcher) {
		this.clazz = checkNotNull(clazz, "class");
		this.exportContext = checkNotNull(exportContext, "exportContext");
		this.mapper = checkNotNull(mapper, "mapper");
		this.revisionSearcher = checkNotNull(revisionSearcher);
	}
	
	@Override
	public boolean hasNext() {
		
		if (totalSize == -1 || (currentIndex >= conceptHits.getHits().size()) && currentOffset < totalSize ) {
			try {
				Query<T> query = Query.select(clazz).where(Expressions.matchAll()).limit(PAGE_SIZE).offset(currentOffset).build();
				conceptHits = revisionSearcher.search(query);
				
				//to avoid getting the size every time
				if (totalSize == -1) {
					totalSize = conceptHits.getTotal();
				}
				currentIndex = 0;
				currentOffset += conceptHits.getHits().size();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return conceptHits != null && conceptHits.getHits().size() > 0 && currentIndex < conceptHits.getHits().size();
	}
	
	@Override
	public String next() {
		
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		
		T revisionIndexEntry = conceptHits.getHits().get(currentIndex++);
		try {
			return convertToRF1(revisionIndexEntry);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Converts an SNOMED index entry to an RF1 line.
	 * @param snomedConceptDocument
	 * @return RF1 line representation
	 * @throws IOException 
	 */
	protected abstract String convertToRF1(T revisionDocument) throws IOException;
	
	@Override
	public Iterator<String> iterator() {
		return this;
	}
	
	@Override
	public SnomedExportContext getExportContext() {
		return exportContext;
	}
	
	@Override
	public String getRelativeDirectory() {
		return RF1_CORE_RELATIVE_DIRECTORY;
	}
	
	@Override
	public String getFileName() {
		return SnomedRfFileNameBuilder.buildCoreRf1FileName(getType(), exportContext);
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void close() throws Exception {
		//intentionally ignored
	}

}
