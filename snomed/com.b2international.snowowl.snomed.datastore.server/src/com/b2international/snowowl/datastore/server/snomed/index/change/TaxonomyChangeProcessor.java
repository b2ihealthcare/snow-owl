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

import bak.pcj.LongCollection;
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
 * Given two {@link ISnomedTaxonomyBuilder} instances and the difference between them (represented as a set of added and a set of
 * removed IS A relationship identifiers), registers the following concepts for updating:
 * <p>
 * <ul>
 * <li>the source concept of an added IS A relationship and its descendants, according to the <b>new</b> taxonomy state;
 * <li>the source concept of a detached IS A relationship and its descendants, according to the <b>previous</b> taxonomy state.
 * </ul>
 * <p>
 * The registered {@link ParentageUpdater} will use the <b>new</b> taxonomy state when updating parent and ancestor
 * fields on concept documents in both of the cases above.
 * 
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
		registerConceptAndDescendants(getNewIsARelationshipIds(), newTaxonomy);
		registerConceptAndDescendants(getDetachedIsARelationshipIds(), previousTaxonomy);
	}

	private void registerConceptAndDescendants(LongCollection relationshipIds, ISnomedTaxonomyBuilder taxonomy) {
		LongIterator relationshipIdIterator = relationshipIds.iterator();
		while (relationshipIdIterator.hasNext()) {
			String relationshipId = Long.toString(relationshipIdIterator.next());
			String conceptId = taxonomy.getSourceNodeId(relationshipId);
			registerConcept(conceptId);
			registerDescendants(conceptId, taxonomy);
		}
	}

	private void registerDescendants(String conceptId, ISnomedTaxonomyBuilder taxonomy) {
		LongIterator descendantIdIterator = taxonomy.getAllDescendantNodeIds(conceptId).iterator();
		while (descendantIdIterator.hasNext()) {
			String descendantId = Long.toString(descendantIdIterator.next());
			registerConcept(descendantId);
		}
	}

	private void registerConcept(String conceptId) {
		registerUpdate(conceptId, new ParentageUpdater(newTaxonomy, conceptId, fieldSuffix));
	}

	private LongSet getNewIsARelationshipIds() {
		return differenceSupplier.get().getA();
	}
	
	private LongSet getDetachedIsARelationshipIds() {
		return differenceSupplier.get().getB();
	}
}
