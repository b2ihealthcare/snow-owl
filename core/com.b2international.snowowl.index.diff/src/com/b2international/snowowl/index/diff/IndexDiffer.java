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
package com.b2international.snowowl.index.diff;

import org.apache.lucene.index.IndexCommit;

/**
 * Index differ representation. Responsible for calculating {@link IndexDiff index difference} between two arbitrary {@link IndexCommit index commit}s.
 *
 */
public interface IndexDiffer {

	/**
	 * Calculates the {@link IndexDiff index difference} between two {@link IndexCommit index commit}.
	 * @param sourceCommit the source commit.
	 * @param targetCommit the target commit.
	 * @return the index difference between the index commits.
	 */
	IndexDiff calculateDiff(final IndexCommit sourceCommit, final IndexCommit targetCommit);
	
	/**
	 * Calculates the {@link IndexDiff index difference} between three {@link IndexCommit index commit}s.
	 * @param ancestorCommit the ancestor of the source and target commits.
	 * @param sourceCommit the source commit.
	 * @param targetCommit the target commit.
	 * @return the index difference between the index commits.
	 */
	IndexDiff calculateDiff(final IndexCommit ancestorCommit, final IndexCommit sourceCommit, final IndexCommit targetCommit);

}