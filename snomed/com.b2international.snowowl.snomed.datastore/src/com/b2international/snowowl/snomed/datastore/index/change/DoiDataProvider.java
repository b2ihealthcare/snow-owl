/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyFloatMap;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

/**
 * Class for initializing the DOI data on concept documents.
 */
enum DoiDataProvider implements DoiData {

	INSTANCE;
	
	private static final String USAGE_RESOURCE_FILE = "resources/out.bin";
	private LongKeyFloatMap doiData;

	private DoiDataProvider() {
		final Logger log = LoggerFactory.getLogger("index");
		try {
			log.info("Initializing usage data for concept documents...");
			this.doiData = run();
			log.info("Initialization of usage data for concept documents successfully finished.");
		} catch (Throwable e) {
			log.warn("Failed to collect usage data for concept documents. Falling back to default degree of interest values.", e);
			this.doiData = PrimitiveMaps.newLongKeyFloatOpenHashMap();
		}
	}
	
	@Override
	public float getDoiScore(String conceptId) {
		return getDoiScore(Long.parseLong(conceptId));
	}
	
	@Override
	public float getDoiScore(long conceptId) {
		return doiData.containsKey(conceptId) ? doiData.get(conceptId) : SnomedConceptDocument.DEFAULT_DOI;
	}
	
	private LongKeyFloatMap run() throws Exception {
		try (final DataInputStream dis = new DataInputStream(getResourceStream())) {
			final int size = dis.readInt();
			final LongKeyFloatMap data = size > 0 ? PrimitiveMaps.newLongKeyFloatOpenHashMapWithExpectedSize(size) : PrimitiveMaps.newLongKeyFloatOpenHashMap();
			for (int i = 0; i < size; i++) {
				data.put(dis.readLong(), dis.readFloat());
			}
			return data;
		}
	}
	
	/*returns with the stream pointing to the doi resource file.*/
	private InputStream getResourceStream() throws Exception {
		final StringBuilder sb = new StringBuilder();
		sb.append("platform:/plugin/");
		sb.append(SnomedDatastoreActivator.PLUGIN_ID);
		sb.append("/");
		sb.append(USAGE_RESOURCE_FILE);
		return new URL(sb.toString()).openStream();
	}
	
}