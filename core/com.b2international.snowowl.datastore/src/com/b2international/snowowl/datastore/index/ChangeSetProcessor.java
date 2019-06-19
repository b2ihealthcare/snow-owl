/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.RevisionWriter;
import com.b2international.index.revision.StagingArea;

/**
 * @since 4.3
 */
public interface ChangeSetProcessor {

	int PAGE_SIZE = 10_000;
	
	/**
	 * Processes the changes and writer index changes using the given {@link RevisionWriter}.
	 * 
	 * @param staging
	 *            - the current state of the commit staging area
	 * @param searcher
	 *            - the revision index searcher to query previous state of the revisions, if required
	 */
	void process(StagingArea staging, RevisionSearcher searcher) throws IOException;

	/**
	 * Returns the description of the change set processor.
	 * 
	 * @return
	 */
	String description();

	/**
	 * Returns the new mappings made by this {@link ChangeSetProcessor}
	 * 
	 * @return
	 */
	Map<String, RevisionDocument> getNewMappings();
	
	/**
	 * Returns the new mappings made by this {@link ChangeSetProcessor} on existing documents.
	 * 
	 * @return
	 */
	Map<String, RevisionDocumentChange> getChangedMappings();

	/**
	 * Returns the deletions made by this {@link ChangeSetProcessor}
	 * 
	 * @return
	 */
	Collection<RevisionDocument> getDeletions();

}