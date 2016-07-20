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
package com.b2international.snowowl.datastore.server.index;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.CompareUtils;
import com.b2international.index.lucene.Fields;
import com.b2international.snowowl.core.api.ComponentIdAndLabel;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.datastore.index.IndexRead;

/**
 * Abstract superclass for index based name providers.
 */
public abstract class AbstractIndexNameProvider implements IComponentNameProvider {

	private final IndexServerService<?> service;

	public AbstractIndexNameProvider(final IIndexService<?> service) {
		this.service = (IndexServerService<?>) service;
	}

	/**
	 * Returns with the human readable label of a terminology independent component identified by its unique ID
	 * from the given branch. This method may return with {@code null} if the component cannot be found on the 
	 * specified branch with the given component ID.
	 * @param branchPath the branch path uniquely identifying the branch where the lookup has to be performed.
	 * @param componentId the terminology specific unique ID of the component.
	 * @return the name/label of the component. Or {@code null} if the component cannot be found.
	 */
	@Override
	public String getComponentLabel(final IBranchPath branchPath, final String componentId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(componentId, "Component ID argument cannot be null.");

		return service.executeReadTransaction(branchPath, new IndexRead<String>() {
			@Override
			public String execute(final IndexSearcher index) throws IOException {
				final Query query = getIdQuery(componentId);
				final TopDocs topDocs = index.search(query, 1);
				if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
					return null;
				}

				final Document doc = index.doc(topDocs.scoreDocs[0].doc, Fields.fieldsToLoad()
						.label()
						.build());

				return getLabel(doc);
			}
		});
	}

	/**
	 * Returns with the terminology dependent unique ID and the human readable label of a component specified by its unique
	 * storage key. This method could return with {@code null} if the component does not exist in the store on the specified
	 * branch.
	 * @param branchPath the branch path.
	 * @param storageKey the primary storage key of the component
	 * @return the {@link ComponentIdAndLabel ID and label pair} of a component. May return with {@code null} if the component
	 * does not exist in store.
	 */
	public ComponentIdAndLabel getComponentIdAndLabel(final IBranchPath branchPath, final long storageKey) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");

		return service.executeReadTransaction(branchPath, new IndexRead<ComponentIdAndLabel>() {
			@Override
			public ComponentIdAndLabel execute(final IndexSearcher index) throws IOException {
				final Query query = getStorageKeyQuery(storageKey);
				final TopDocs topDocs = index.search(query, 1);
				if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
					return null;
				}

				final Document doc = index.doc(topDocs.scoreDocs[0].doc, Fields.fieldsToLoad()
						.id()
						.label()
						.build());

				final String label = checkNotNull(getLabel(doc), "Component label was null for component. CDO ID: " + storageKey);
				final String id = checkNotNull(getId(doc), "Component ID was null for component. CDO ID: " + storageKey);
				return new ComponentIdAndLabel(label, id);
			}
		});
	}

	protected Query getIdQuery(final String componentId) {
		return Fields.newQuery().id(componentId).matchAll();
	}

	protected Query getStorageKeyQuery(final long storageKey) {
		return Fields.newQuery().storageKey(storageKey).matchAll();
	}

	protected String getId(final Document doc) {
		return Fields.id().getValue(doc);
	}

	protected String getLabel(final Document doc) {
		return Fields.label().getValue(doc);
	}
}
