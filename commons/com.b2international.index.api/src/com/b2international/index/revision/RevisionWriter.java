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
package com.b2international.index.revision;

import java.util.Set;

import com.b2international.index.Writer;

/**
 * Writer working on top of a {@link RevisionIndex}. A {@link RevisionWriter} is always working on a single {@link RevisionBranch}.
 * 
 * @since 4.7
 */
public interface RevisionWriter extends Writer {

	String branch();

	@Override
	RevisionSearcher searcher();

	/**
	 * Set the revised time on the given set of document IDs on the given branch revised when searching it from the current set {@link #branch()}. The
	 * given branch won't be affected.
	 * 
	 * @param type
	 *            - the doc type to update
	 * @param keysToUpdate
	 *            - the document IDs to update
	 * @param branch
	 *            - branch where the documents were created by another transaction
	 */
	void setRevised(Class<?> type, Set<String> keysToUpdate, RevisionBranchRef branch);

}