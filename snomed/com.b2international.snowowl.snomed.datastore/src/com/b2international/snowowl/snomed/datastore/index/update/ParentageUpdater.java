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

import java.util.Objects;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongIterator;
import com.b2international.snowowl.datastore.index.mapping.IndexField;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.google.common.base.Strings;

/**
 * @since 4.3
 */
public class ParentageUpdater extends SnomedDocumentUpdaterBase {

	private String fieldSuffix;

	public ParentageUpdater(ISnomedTaxonomyBuilder taxonomyBuilder, String conceptId) {
		this(taxonomyBuilder, conceptId, null);
	}
	
	public ParentageUpdater(ISnomedTaxonomyBuilder taxonomyBuilder, String conceptId, String fieldSuffix) {
		super(taxonomyBuilder, conceptId);
		this.fieldSuffix = Strings.nullToEmpty(fieldSuffix);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getComponentId(), getClass(), this.fieldSuffix);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			ParentageUpdater other = (ParentageUpdater) obj;
			return Objects.equals(fieldSuffix, other.fieldSuffix);
		}
		return false;
	}

	@Override
	public final void doUpdate(SnomedDocumentBuilder doc) {
		// throw out any parent or ancestor fields
		final IndexField<Long> parentField = SnomedMappings.parent(fieldSuffix);
		final IndexField<Long> ancestorField = SnomedMappings.ancestor(fieldSuffix);
		
		parentField.removeAll(doc);
		ancestorField.removeAll(doc);
		
		final LongCollection parentIds = getParentIds(getComponentId());
		final LongCollection ancestorIds = getAncestorIds(getComponentId());
		final LongIterator parentIdIterator = parentIds.iterator();
		final LongIterator ancestorIdIterator = ancestorIds.iterator();
		// index ROOT_ID
		if (!parentIdIterator.hasNext()) {
			doc.addToDoc(parentField, SnomedMappings.ROOT_ID);
		} else {
			doc.addToDoc(ancestorField, SnomedMappings.ROOT_ID);
		}
		// index parentage info
		while (parentIdIterator.hasNext()) {
			doc.addToDoc(parentField, parentIdIterator.next());
		}
		while (ancestorIdIterator.hasNext()) {
			doc.addToDoc(ancestorField, ancestorIdIterator.next());
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
