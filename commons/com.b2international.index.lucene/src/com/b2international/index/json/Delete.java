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
package com.b2international.index.json;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;

import com.b2international.index.Searcher;

/**
 * @since 4.7
 */
public final class Delete implements Operation {

	private final String type;
	private final String uid;
	private final String key;

	public Delete(String type, String key, String uid) {
		this.type = type;
		this.key = key;
		this.uid = uid;
	}
	
	@Override
	public void execute(IndexWriter writer, Searcher searcher) throws IOException {
		writer.deleteDocuments(JsonDocumentMapping._uid().toTerm(uid));
	}
	
	public String uid() {
		return uid;
	}
	
	public String type() {
		return type;
	}
	
	public String key() {
		return key;
	}

}
