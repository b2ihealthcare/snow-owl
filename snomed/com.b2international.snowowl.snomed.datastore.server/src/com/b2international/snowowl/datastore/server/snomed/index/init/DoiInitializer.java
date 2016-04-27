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
package com.b2international.snowowl.datastore.server.snomed.index.init;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyFloatMap;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.server.snomed.SnomedDatastoreServerActivator;
import com.google.common.base.Preconditions;

/**
 * Class for initializing the DOI data on concept documents.
 *
 */
public class DoiInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(DoiInitializer.class);
	private static final String USAGE_RESOURCE_FILE = "resources/out.bin";

	public LongKeyFloatMap run(IProgressMonitor monitor) {
		
		monitor = null == monitor ? new NullProgressMonitor() : monitor;

		InputStream is = null;
		
		try {
			
			LOGGER.info("Collecting usage data for concept documents...");
			
			is = getResourceStream();
			Preconditions.checkNotNull(is, "Cannot load usage data resource.");
			
			final DataInputStream dis = new DataInputStream(is);
			
			final int size = dis.readInt();
			
			final LongKeyFloatMap data = size > 0 ? PrimitiveMaps.newLongKeyFloatOpenHashMapWithExpectedSize(size) : PrimitiveMaps.newLongKeyFloatOpenHashMap();
			
			for (int i = 0; i < size; i++) {
				
				data.put(dis.readLong(), dis.readFloat());
				
			}

			LOGGER.info("Collecting usage data for concept documents successfully finished.");
			
			return data;
			
			
		} catch (final Throwable t) {
			
			LOGGER.warn("Failed to collect usage data for concept documents. Falling back to default degree of interest values.");
			
			return PrimitiveMaps.newLongKeyFloatOpenHashMap();
			
		} finally {
			
			if (null != is) {
				
				try {
					is.close();
				} catch (final IOException e) {
					try {
						is.close();
					} catch (final IOException e1) {
					}
					throw new IndexException("Error while closing DOI resource file stream.", e);
				}
				
			}
			
			monitor.worked(1);
			
		}
	
		
	}
	
	/*returns with the stream pointing to the doi resource file.*/
	private InputStream getResourceStream() {
		try {
			final StringBuilder sb = new StringBuilder();
			sb.append("platform:/plugin/");
			sb.append(SnomedDatastoreServerActivator.PLUGIN_ID);
			sb.append("/");
			sb.append(USAGE_RESOURCE_FILE);
			return new URL(sb.toString()).openStream();
		} catch (final Exception e) {
			return null;
		}
	}
	
}