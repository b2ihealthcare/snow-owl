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

import java.io.Serializable;

import org.apache.lucene.index.IndexCommit;

import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.Change;

/**
 * Represents an index difference between two {@link IndexCommit index commit}s.
 */
public interface IndexDiff extends Serializable {

	/**Returns with a set of new IDs.*/
	LongSet getNewIds();
	
	/**Returns with a set of changed IDs.*/
	LongSet getChangedIds();
	
	/**Returns with a set of detached IDs.*/
	LongSet getDetachedIds();
	
	/**Returns with an iterator traversing all new, changed and detached component.*/
	LongIterator iterator();
	
	/**Returns with the change for the given key.*/
	Change getChange(final long key);
	
	/**
	 * Returns {@code true} if the diff contains the given key, otherwise {@code false}.
	 * <p>More formally, it returns with {@code true} only and if only one of the followings are {@code true}:
	 * <ul>
	 * <li>{@link #getNewIds()#contains(key)}</li>
	 * <li>{@link #getChangedIds()#contains(key)}</li>
	 * <li>{@link #getDetachedIds()#contains(key)}</li>
	 * </ul>
	 *  
	 * @param key the key to check.
	 * @return {@code true} if the diff contains the key, otherwise {@code false}.
	 */
	boolean contains(final long key);
	
}