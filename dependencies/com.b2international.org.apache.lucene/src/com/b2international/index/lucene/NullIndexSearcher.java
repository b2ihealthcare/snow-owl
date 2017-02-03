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
package com.b2international.index.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.RAMDirectory;

/**
 * Null implementation of an {@link IndexSearcher index searcher}.
 */
public class NullIndexSearcher extends IndexSearcher {

	/**Shared NULL instance.*/
	private static IndexSearcher instance;
	
	/**Returns with the shared {@link #instance NULL} instance.*/
	public static IndexSearcher getInstance() {
		
		if (null == instance) {
			
			synchronized (NullIndexSearcher.class) {
				
				if (null == instance) {
					
					try {
						
						instance = new NullIndexSearcher();
						
					} catch (final IOException e) {
						
						throw new RuntimeException("Error while instantiating NULL index searcher.", e);
						
					}
					
				}
				
			}
			
		}
		
		return instance;
		
	}
	
	/*Private constructor wrapping an empty RAM directory.*/
	private NullIndexSearcher() throws IOException {
		super(createRamReader());
	}
	
	/*creates a reader backed with a RAM directory*/
	private static DirectoryReader createRamReader() throws IOException {
		
		final RAMDirectory directory = new RAMDirectory();
		if (!DirectoryReader.indexExists(directory)) {
			
			final IndexWriterConfig conf = new IndexWriterConfig(new WhitespaceAnalyzer());
			final IndexWriter writer = new IndexWriter(directory, conf);
			writer.commit();
			writer.close();
		}
		
		return DirectoryReader.open(directory);
		
	}
	
}