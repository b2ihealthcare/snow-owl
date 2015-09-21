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
package com.b2international.snowowl.datastore.server.snomed.index.change;

import bak.pcj.LongIterator;
import bak.pcj.set.LongSet;

import com.b2international.commons.Pair;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.update.ParentageUpdater;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.google.common.base.Supplier;

/**
 * @since 4.3
 */
public class TaxonomyChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> {

	private ISnomedTaxonomyBuilder newTaxonomy;
	private ISnomedTaxonomyBuilder previousTaxonomy;
	private Supplier<Pair<LongSet, LongSet>> differenceSupplier;
	private String fieldSuffix;

	public TaxonomyChangeProcessor(ISnomedTaxonomyBuilder newTaxonomy, ISnomedTaxonomyBuilder previousTaxonomy, Supplier<Pair<LongSet, LongSet>> differenceSupplier, String fieldSuffix) {
		super("taxonomy changes");
		this.newTaxonomy = newTaxonomy;
		this.previousTaxonomy = previousTaxonomy;
		this.differenceSupplier = differenceSupplier;
		this.fieldSuffix = fieldSuffix;
	}

	@Override
	public void process(ICDOCommitChangeSet commitChangeSet) {
		// process new relationships
		final LongIterator newIterator = differenceSupplier.get().getA().iterator();
		while (newIterator.hasNext()) {
			final long relationshipId = newIterator.next();
			final String conceptId = newTaxonomy.getSourceNodeId(Long.toString(relationshipId));
			// update only if not new concept
			registerUpdate(conceptId, new ParentageUpdater(newTaxonomy, conceptId, fieldSuffix));
			final LongIterator descendantIds = newTaxonomy.getAllDescendantNodeIds(conceptId).iterator();
			while (descendantIds.hasNext()) {
				final String descendant = Long.toString(descendantIds.next());
				registerUpdate(descendant, new ParentageUpdater(newTaxonomy, descendant, fieldSuffix));
			}
		}
		// process detach relationships
		final LongIterator detachedIterator = differenceSupplier.get().getB().iterator();
		while (detachedIterator.hasNext()) {
			final long relationshipId = detachedIterator.next();
			final String conceptId = previousTaxonomy.getSourceNodeId(Long.toString(relationshipId));
			registerUpdate(conceptId, new ParentageUpdater(newTaxonomy, conceptId, fieldSuffix));
			final LongIterator descendantIds = previousTaxonomy.getAllDescendantNodeIds(conceptId).iterator();
			while (descendantIds.hasNext()) {
				final String descendant = Long.toString(descendantIds.next());
				registerUpdate(descendant, new ParentageUpdater(newTaxonomy, descendant, fieldSuffix));
			}
		}
	}

}
