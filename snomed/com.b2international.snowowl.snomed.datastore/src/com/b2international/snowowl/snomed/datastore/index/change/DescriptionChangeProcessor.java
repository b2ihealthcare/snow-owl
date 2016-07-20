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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry.Builder;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @since 4.3
 */
public class DescriptionChangeProcessor extends ChangeSetProcessorBase {

	public DescriptionChangeProcessor() {
		super("description changes");
	}

	@Override
	public void process(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) throws IOException {
		final Map<String, Multimap<Acceptability, RefSetMemberChange>> acceptabilityChangesByDescription = new DescriptionAcceptabilityChangeProcessor().process(commitChangeSet, searcher);
		// delete detached descriptions
		deleteRevisions(SnomedDescriptionIndexEntry.class, commitChangeSet.getDetachedComponents(SnomedPackage.Literals.DESCRIPTION));
		// (re)index new and dirty descriptions
		final Map<String, Description> changedDescriptionsById = FluentIterable.from(Iterables.concat(commitChangeSet.getNewComponents(Description.class), commitChangeSet.getDirtyComponents(Description.class)))
				.uniqueIndex(new Function<Description, String>() {
					@Override
					public String apply(Description input) {
						return input.getId();
					}
				});
		
		// load the known descriptions 
		final Iterable<Long> storageKeys = CDOIDUtils.createCdoIdToLong(CDOIDUtils.getIds(changedDescriptionsById.values()));
		final Map<String, SnomedDescriptionIndexEntry> currentRevisionsById = newHashMap(Maps.uniqueIndex(searcher.get(SnomedDescriptionIndexEntry.class, storageKeys), ComponentUtils.<String>getIdFunction()));
		
		// load missing descriptions with only changed acceptability values
		final Set<String> descriptionsToBeLoaded = newHashSet();
		for (String descriptionWithAccepatibilityChange : acceptabilityChangesByDescription.keySet()) {
			if (!changedDescriptionsById.containsKey(descriptionWithAccepatibilityChange)) {
				descriptionsToBeLoaded.add(descriptionWithAccepatibilityChange);
			}
		}
		
		// process changes
		for (final Description description : changedDescriptionsById.values()) {
			final String descriptionId = description.getId();
			final long storageKey = CDOIDUtil.getLong(description.cdoID());
			final Builder doc = SnomedDescriptionIndexEntry.builder(description);
			final SnomedDescriptionIndexEntry currentDoc = currentRevisionsById.get(descriptionId);
			processChanges(doc, currentDoc, acceptabilityChangesByDescription.get(descriptionId));
			indexRevision(storageKey, doc.build());
		}
		
		// process cascading acceptability changes in unchanged docs
		if (!descriptionsToBeLoaded.isEmpty()) {
			final Query<SnomedDescriptionIndexEntry> descriptionsToBeLoadedQuery = Query.select(SnomedDescriptionIndexEntry.class).where(SnomedDocument.Expressions.ids(descriptionsToBeLoaded)).limit(descriptionsToBeLoaded.size()).build();
			for (SnomedDescriptionIndexEntry unchangedDescription : searcher.search(descriptionsToBeLoadedQuery)) {
				final Builder doc = SnomedDescriptionIndexEntry.builder(unchangedDescription);
				processChanges(doc, unchangedDescription, acceptabilityChangesByDescription.get(unchangedDescription.getId()));
				indexRevision(unchangedDescription.getStorageKey(), doc.build());
			}
		}
	}

	private void processChanges(final Builder doc, final SnomedDescriptionIndexEntry currentRevision, Multimap<Acceptability, RefSetMemberChange> acceptabilityChanges) {
		final Multimap<Acceptability, String> acceptabilityMap = currentRevision == null ? ImmutableMultimap.<Acceptability, String>of() : ImmutableMap.copyOf(currentRevision.getAcceptabilityMap()).asMultimap().inverse();
		
		final Collection<String> preferredLanguageRefSets = newHashSet(acceptabilityMap.get(Acceptability.PREFERRED));
		final Collection<String> acceptableLanguageRefSets = newHashSet(acceptabilityMap.get(Acceptability.ACCEPTABLE));
		
		
		if (acceptabilityChanges != null) {
			collectChanges(acceptabilityChanges.get(Acceptability.PREFERRED), preferredLanguageRefSets);
			collectChanges(acceptabilityChanges.get(Acceptability.ACCEPTABLE), acceptableLanguageRefSets);
		}
		
		for (String preferredLanguageRefSet : preferredLanguageRefSets) {
			doc.acceptability(preferredLanguageRefSet, Acceptability.PREFERRED);
		}
		
		for (String acceptableLanguageRefSet : acceptableLanguageRefSets) {
			doc.acceptability(acceptableLanguageRefSet, Acceptability.ACCEPTABLE);
		}
	}
	
	private void collectChanges(Collection<RefSetMemberChange> changes, Collection<String> refSetIds) {
		for (final RefSetMemberChange change : changes) {
			switch (change.getChangeKind()) {
				case ADDED:
					refSetIds.add(change.getRefSetId());
					break;
				default:
					break;
			}
		}
		
		for (final RefSetMemberChange change : changes) {
			switch (change.getChangeKind()) {
				case REMOVED:
					refSetIds.remove(change.getRefSetId());
					break;
				default:
					break;
			}
		}
	}
	
}
