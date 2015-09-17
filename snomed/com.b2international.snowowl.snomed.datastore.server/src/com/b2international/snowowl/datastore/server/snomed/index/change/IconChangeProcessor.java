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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import bak.pcj.LongIterator;
import bak.pcj.set.LongSet;

import com.b2international.commons.Pair;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.update.IconIdUpdater;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.3
 */
public class IconChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> {

	private Supplier<Pair<LongSet, LongSet>> differenceSupplier;
	private ISnomedTaxonomyBuilder newTaxonomy;
	private IBranchPath branchPath;

	public IconChangeProcessor(IBranchPath branchPath, ISnomedTaxonomyBuilder newTaxonomy, Supplier<Pair<LongSet, LongSet>> differenceSupplier) {
		super("icon changes");
		this.branchPath = branchPath;
		this.newTaxonomy = newTaxonomy;
		this.differenceSupplier = differenceSupplier;
	}

	@Override
	public void process(ICDOCommitChangeSet commitChangeSet) {
		final Set<String> iconIdUpdates = newHashSet();
		final LongIterator it = differenceSupplier.get().getB().iterator();
		while (it.hasNext()) {
			final long relationshipId = it.next();
			final String sourceNodeId = newTaxonomy.getSourceNodeId(Long.toString(relationshipId));
			final LongSet allAncestorNodeIds = newTaxonomy.getAllAncestorNodeIds(sourceNodeId);
			final String oldIconId = SnomedIconProvider.getInstance().getIconId(sourceNodeId, branchPath);
			if (!allAncestorNodeIds.contains(Long.parseLong(oldIconId))) {
				iconIdUpdates.add(sourceNodeId);
				// add all descendants
				iconIdUpdates.addAll(LongSets.toStringSet(newTaxonomy.getAllDescendantNodeIds(sourceNodeId)));
			}
		}
		
		FluentIterable.from(getNewComponents(commitChangeSet, Concept.class)).transform(new Function<Concept, String>() {
			@Override
			public String apply(Concept input) {
				return input.getId();
			}
		}).copyInto(iconIdUpdates);
		
		for (String conceptId : iconIdUpdates) {
			final IconIdUpdater updater = new IconIdUpdater(newTaxonomy, conceptId,
					newTaxonomy.containsNode(conceptId), SnomedIconProvider.getInstance().getAvailableIconIds());
			registerUpdate(conceptId, updater);
		}
	}

}
