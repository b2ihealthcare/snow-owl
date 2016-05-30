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

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.RevisionWriter;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @since 4.3
 */
public abstract class ChangeSetProcessorBase implements ChangeSetProcessor {

	private final String description;
	private final Map<Long, Revision> mappings = newHashMap();
	private final Multimap<Class<? extends Revision>, Long> deletions = HashMultimap.create();

	protected ChangeSetProcessorBase(String description) {
		this.description = description;
	}
	
	@Override
	public final String description() {
		return description;
	}
	
	@Override
	public final void process(ICDOCommitChangeSet commitChangeSet, RevisionWriter writer) throws IOException {
		final RevisionSearcher searcher = writer.searcher();
		doProcess(commitChangeSet, searcher);
		
		// execute document updates and deletes after the commit change set processing
		for (Class<? extends Revision> type : ImmutableMultimap.copyOf(deletions).keySet()) {
			writer.remove(type, Sets.newHashSet(deletions.get(type)));
		}
		
		writer.putAll(ImmutableMap.copyOf(mappings));
	}

	protected void doProcess(ICDOCommitChangeSet commitChangeSet, final RevisionSearcher searcher) throws IOException {
	}

	protected final void indexRevision(CDOID storageKey, Revision revision) {
		indexRevision(CDOIDUtil.getLong(storageKey), revision);
	}
	
	protected final void indexRevision(long storageKey, Revision revision) {
		mappings.put(storageKey, revision);
	}
	
	protected final void deleteRevisions(Class<? extends Revision> type, Collection<CDOID> storageKeys) {
		deletions.putAll(type, CDOIDUtils.createCdoIdToLong(storageKeys));
	}
	
}