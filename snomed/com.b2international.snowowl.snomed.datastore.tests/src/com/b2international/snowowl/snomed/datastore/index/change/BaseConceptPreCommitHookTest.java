/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomies;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;

/**
 * @since 7.0
 */
public abstract class BaseConceptPreCommitHookTest extends BaseChangeProcessorTest {

	protected final Collection<String> availableImages = newHashSet(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT, Concepts.NAMESPACE_ROOT);
	protected final LongSet statedChangedConceptIds = PrimitiveSets.newLongOpenHashSet();
	protected final LongSet inferredChangedConceptIds = PrimitiveSets.newLongOpenHashSet();
	
	protected final ConceptChangeProcessor process() {
		return index().read(MAIN, new RevisionIndexRead<ConceptChangeProcessor>() {
			@Override
			public ConceptChangeProcessor execute(RevisionSearcher searcher) throws IOException {
				final Taxonomy inferredTaxonomy = Taxonomies.inferred(searcher, staging(), inferredChangedConceptIds, true);
				final Taxonomy statedTaxonomy = Taxonomies.stated(searcher, staging(), statedChangedConceptIds, true);
				final ConceptChangeProcessor processor = new ConceptChangeProcessor(DoiData.DEFAULT_SCORE, availableImages, statedTaxonomy, inferredTaxonomy);
				processor.process(staging(), searcher);
				return processor;
			}
		});
	}
	
}
