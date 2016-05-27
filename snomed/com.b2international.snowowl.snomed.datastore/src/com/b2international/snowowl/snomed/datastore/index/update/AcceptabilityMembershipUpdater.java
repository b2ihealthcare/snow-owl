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

import java.util.Collection;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.datastore.index.mapping.LongCollectionIndexField;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;

/**
 * @since 4.5
 */
public class AcceptabilityMembershipUpdater extends DocumentUpdaterBase<SnomedDocumentBuilder> {

	private Collection<RefSetMemberChange> preferredMemberChanges;
	private Collection<RefSetMemberChange> acceptableMemberChanges;

	public AcceptabilityMembershipUpdater(String descriptionId, Collection<RefSetMemberChange> preferredMemberChanges, Collection<RefSetMemberChange> acceptableMemberChanges) {
		super(descriptionId);
		this.preferredMemberChanges = preferredMemberChanges;
		this.acceptableMemberChanges = acceptableMemberChanges;
	}

	@Override
	public void doUpdate(SnomedDocumentBuilder doc) {
		Document document = doc.build();
		final LongCollectionIndexField preferredField = SnomedMappings.descriptionPreferredReferenceSetId();
		final LongCollectionIndexField acceptableField = SnomedMappings.descriptionAcceptableReferenceSetId();
		
		final LongSet preferredRefSetIds = preferredField.getValueAsLongSet(document);
		final LongSet acceptableRefSetIds = acceptableField.getValueAsLongSet(document);
		
		doc.removeAll(preferredField);
		doc.removeAll(acceptableField);
		
		registerChanges(preferredMemberChanges, preferredRefSetIds);
		registerChanges(acceptableMemberChanges, acceptableRefSetIds);
		
		LongIterator itr = preferredRefSetIds.iterator();
		while (itr.hasNext()) {
			doc.descriptionPreferredReferenceSetId(itr.next());
		}

		itr = acceptableRefSetIds.iterator();
		while (itr.hasNext()) {
			doc.descriptionAcceptableReferenceSetId(itr.next());
		}
	}

	private void registerChanges(Collection<RefSetMemberChange> changes, LongSet refSetIds) {
		for (final RefSetMemberChange change : changes) {
			switch (change.getChangeKind()) {
				case REMOVED:
					refSetIds.remove(change.getRefSetId());
					break;
				default:
					break;
			}
		}
		
		for (final RefSetMemberChange change : changes) {
			switch (change.getChangeKind()) {
				case ADDED:
					refSetIds.add(change.getRefSetId());
					break;
				default:
					break;
			}
		}
	}
}
