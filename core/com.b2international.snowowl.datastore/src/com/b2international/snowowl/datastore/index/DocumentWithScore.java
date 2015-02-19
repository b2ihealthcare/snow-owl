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
package com.b2international.snowowl.datastore.index;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.base.Preconditions;

/**
 * Wraps a document with the associated score retrieved from index service.
 * 
 */
public final class DocumentWithScore {
	
	private static final float NULL_SCORE = 0.0F; 
	
	private final Document document;
	private final float score;

	private IBranchPath branchPath;
	
	public DocumentWithScore(final Document document, final IBranchPath branchPath) {
		this(Preconditions.checkNotNull(document, "Document argument cannot be null."), branchPath, NULL_SCORE);
	}
	
	public DocumentWithScore(final Document document, final IBranchPath branchPath, final float score) {
		this.branchPath = checkNotNull(branchPath, "branchPath");
		this.document = Preconditions.checkNotNull(document, "Document argument cannot be null.");
		this.score = score;
	}
	
	/**
	 * Returns with the wrapped {@link Document} instance.
	 * @return the document.
	 */
	public Document getDocument() {
		return document;
	}
	
	/**
	 * Returns with the score associated with the document as the outcome of an index query.
	 * @return the score.
	 */
	public float getScore() {
		return score;
	}
	
	/**
	 * Returns with the branch path.
	 * @return the branch path.
	 */
	public IBranchPath getBranchPath() {
		return branchPath;
	}
}