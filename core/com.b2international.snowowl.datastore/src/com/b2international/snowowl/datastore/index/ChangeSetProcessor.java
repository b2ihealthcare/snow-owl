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
package com.b2international.snowowl.datastore.index;

import java.io.IOException;
import java.util.Map;

import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.RevisionWriter;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.google.common.collect.Multimap;

/**
 * @since 4.3
 */
public interface ChangeSetProcessor {

	/**
	 * Processes the changes and writer index changes using the given {@link RevisionWriter}.
	 * 
	 * @param commitChangeSet
	 *            - the set of changes
	 * @param searcher
	 *            - the revision index searcher to query previous state of the revisions, if required
	 */
	void process(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) throws IOException;

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
	Map<String, RevisionDocument> getChangedMappings();

	/**
	 * Returns the deletions made by this {@link ChangeSetProcessor}
	 * 
	 * @return
	 */
	Multimap<Class<? extends Revision>, String> getDeletions();

}