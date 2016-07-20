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

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;

/**
 * A subclass of {@link Filter} that does not accept any documents in any reader context.
 * 
 * @since 4.6
 */
public class MatchNoDocsFilter extends Filter {

	@Override
	public DocIdSet getDocIdSet(LeafReaderContext context, Bits acceptDocs) throws IOException {
		return null;
	}

	@Override
	public String toString(String arg0) {
		return "NODOCS";
	}
}
