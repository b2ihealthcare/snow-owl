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
package com.b2international.snowowl.snomed.datastore.index.update;

import org.apache.lucene.document.Document;

import bak.pcj.LongCollection;
import bak.pcj.LongIterator;

import com.b2international.commons.pcj.LongCollections;
import com.b2international.snowowl.datastore.index.field.ComponentAncestorField;
import com.b2international.snowowl.datastore.index.field.ComponentAncestorLongField;
import com.b2international.snowowl.datastore.index.field.ComponentParentLongField;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;

/**
 * @since 4.3
 */
public class ParentageUpdater extends SnomedDocumentUpdaterBase {

	public ParentageUpdater(ISnomedTaxonomyBuilder taxonomyBuilder, String conceptId) {
		super(taxonomyBuilder, conceptId);
	}

	@Override
	public final void update(Document doc) {
		// throw out any parent or ancestor fields
		ComponentParentLongField.removeAll(doc);
		ComponentAncestorField.removeAll(doc);
		
		final LongCollection parentIds = getParentIds(getComponentId());
		final LongCollection ancestorIds = getAncestorIds(getComponentId());
		final LongIterator parentIdIterator = parentIds.iterator();
		final LongIterator ancestorIdIterator = ancestorIds.iterator();
		// index ROOT_ID
		if (!parentIdIterator.hasNext()) {
			ComponentParentLongField.ROOT_PARENT.addTo(doc);
		} else {
			ComponentAncestorLongField.ROOT_PARENT.addTo(doc);
		}
		// index parentage info
		while (parentIdIterator.hasNext()) {
			new ComponentParentLongField(parentIdIterator.next()).addTo(doc);
		}
		while (ancestorIdIterator.hasNext()) {
			new ComponentAncestorLongField(ancestorIdIterator.next()).addTo(doc);
		}
	}

	protected LongCollection getParentIds(final String conceptId) {
		if (getTaxonomyBuilder().containsNode(conceptId)) {
			return getTaxonomyBuilder().getAncestorNodeIds(conceptId);
		}
		return LongCollections.emptySet();
	}

	protected LongCollection getAncestorIds(final String conceptId) {
		if (getTaxonomyBuilder().containsNode(conceptId)) {
			return getTaxonomyBuilder().getAllIndirectAncestorNodeIds(conceptId);
		}
		return LongCollections.emptySet();
	}

}
