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

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.lucene.document.Document;

/**
 * Wraps a document for an index search hit and its associated score.
 */
public final class DocumentWithScore {

	private static final float ZERO_SCORE = 0.0F; 

	private final Document document;
	private final float score;

	public DocumentWithScore(final Document document) {
		this(document, ZERO_SCORE);
	}

	public DocumentWithScore(final Document document, final float score) {
		this.document = checkNotNull(document, "Document argument cannot be null.");
		this.score = score;
	}

	/**
	 * @return the {@link Document} retrieved for the search hit
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * @return the relevance (score) of the search hit
	 */
	public float getScore() {
		return score;
	}
}
