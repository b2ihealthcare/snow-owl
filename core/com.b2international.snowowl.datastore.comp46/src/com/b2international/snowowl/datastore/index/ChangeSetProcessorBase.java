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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.datastore.ChangeSetProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 4.3
 */
public abstract class ChangeSetProcessorBase<D extends DocumentBuilderBase<D>> implements ChangeSetProcessor<D> {

	private final Multimap<String, DocumentUpdater<D>> updates = LinkedHashMultimap.create();
	private final Set<Long> deletedStorageKeys = newHashSet();
	private String description;

	public ChangeSetProcessorBase(String description) {
		this.description = description;
	}
	
	@Override
	public final String description() {
		return description;
	}

	@Override
	public final Multimap<String, DocumentUpdater<D>> getUpdates() {
		return updates;
	}
	
	@Override
	public final Set<Long> getDeletedStorageKeys() {
		return deletedStorageKeys;
	}
	
	protected final void registerUpdate(String component, DocumentUpdater<D> updater) {
		updates.put(component, updater);
	}
	
	protected final void registerDelete(CDOID cdoId) {
		deletedStorageKeys.add(CDOIDUtil.getLong(cdoId));
	}
	
	protected final void registerDeletions(Iterable<CDOID> cdoIds) {
		for (CDOID cdoId : cdoIds) {
			registerDelete(cdoId);
		}
	}

	protected <T> Iterable<T> getNewComponents(ICDOCommitChangeSet commitChangeSet, final Class<T> type) {
		return FluentIterable.from(commitChangeSet.getNewComponents()).filter(type).toSet();
	}
	
	protected <T> Iterable<T> getDirtyComponents(ICDOCommitChangeSet commitChangeSet, final Class<T> type) {
		return FluentIterable.from(commitChangeSet.getDirtyComponents()).filter(type).toSet();
	}
	
	public static final Collection<CDOID> getDetachedComponents(ICDOCommitChangeSet commitChangeSet, final EClass eClass) {
		return FluentIterable.from(commitChangeSet.getDetachedComponents().entrySet()).filter(new Predicate<Entry<CDOID, EClass>>() {
			@Override
			public boolean apply(Entry<CDOID, EClass> input) {
				return eClass.isSuperTypeOf(input.getValue());
			}
		}).transform(new Function<Entry<CDOID, EClass>, CDOID>() {
			@Override
			public CDOID apply(Entry<CDOID, EClass> input) {
				return input.getKey();
			}
		}).toSet();
	}
	
	@Override
	public void process(ICDOCommitChangeSet commitChangeSet) {
		deleteDocuments(commitChangeSet);
		indexDocuments(commitChangeSet);
		updateDocuments(commitChangeSet);
	}

	protected void indexDocuments(ICDOCommitChangeSet commitChangeSet) {
	}
	
	protected void updateDocuments(ICDOCommitChangeSet commitChangeSet) {
	}

	protected void deleteDocuments(ICDOCommitChangeSet commitChangeSet) {
	}

}