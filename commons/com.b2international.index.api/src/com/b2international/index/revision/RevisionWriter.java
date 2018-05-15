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
package com.b2international.index.revision;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.b2international.index.Writer;

/**
 * Writer working on top of a {@link RevisionIndex}. A {@link RevisionWriter} is always working on a single {@link RevisionBranch}.
 * 
 * @since 4.7
 */
public interface RevisionWriter {

	void put(long storageKey, Revision object);

	void putAll(Map<Long, Revision> revisionsByStorageKey);

	void remove(Class<? extends Revision> type, long storageKey);

	void remove(Class<? extends Revision> type, Collection<Long> storageKeys);

	void removeAll(Map<Class<? extends Revision>, Collection<Long>> storageKeysByType);

	void commit() throws IOException;

	String branch();

	RevisionSearcher searcher();

	/**
	 * Returns the underlying raw writer.
	 * 
	 * @return
	 */
	Writer writer();

}