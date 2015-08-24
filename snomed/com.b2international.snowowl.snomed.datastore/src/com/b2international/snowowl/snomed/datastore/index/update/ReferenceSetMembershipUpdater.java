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

import static java.lang.Long.parseLong;

import java.util.Arrays;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.IndexableField;

import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @since 4.3
 */
public class ReferenceSetMembershipUpdater extends DocumentUpdaterBase {

	private Set<RefSetMemberChange> memberChanges;

	public ReferenceSetMembershipUpdater(String componentId, Set<RefSetMemberChange> memberChanges) {
		super(componentId);
		this.memberChanges = memberChanges;
	}

	@Override
	public void update(Document doc) {
		// get reference set membership fields
		final IndexableField[] referenceSetIdfields = doc.getFields(SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID);
		
		// get the reference set IDs
		final Set<String> referencingRefSetIds = Sets.newHashSet(Iterables.transform(Arrays.asList(referenceSetIdfields), new Function<IndexableField, String>() {
			@Override public String apply(final IndexableField field) {
				return field.stringValue();
			}
		}));
		
		// get reference set mapping membership fields
		final IndexableField[] mappingReferenceSetIdfields = doc.getFields(SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID);
		
		// get the mapping reference set IDs
		final Set<String> mappingReferencingRefSetIds = Sets.newHashSet(Iterables.transform(Arrays.asList(mappingReferenceSetIdfields), new Function<IndexableField, String>() {
			@Override public String apply(final IndexableField field) {
				return field.stringValue();
			}
		}));
		

		// remove all fields
		doc.removeFields(SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID);
		// mapping fields as well
		doc.removeFields(SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID);
		
		// merge reference set membership with the changes extracted from the transaction, if any.
		for (final RefSetMemberChange change : memberChanges) {
			switch (change.getChangeKind()) {
				case ADDED:
					if (SnomedRefSetType.SIMPLE.equals(change.getType()) || SnomedRefSetType.ATTRIBUTE_VALUE.equals(change.getType())) {
						referencingRefSetIds.add(change.getRefSetId());
					} else if (SnomedRefSetType.SIMPLE_MAP.equals(change.getType())) {
						mappingReferencingRefSetIds.add(change.getRefSetId());
					}
					break;
				case REMOVED:
					if (SnomedRefSetType.SIMPLE.equals(change.getType()) || SnomedRefSetType.ATTRIBUTE_VALUE.equals(change.getType())) {
						referencingRefSetIds.remove(change.getRefSetId());
					} else if (SnomedRefSetType.SIMPLE_MAP.equals(change.getType())) {
						mappingReferencingRefSetIds.remove(change.getRefSetId());
					}
					break;
				default:
					throw new IllegalArgumentException("Unknown reference set member change kind: " + change.getChangeKind());
			}
			
		}
		
		// re-add reference set membership fields
		for (final String refSetId : referencingRefSetIds) {
			doc.add(new LongField(SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID, parseLong(refSetId), Store.YES));
		}
		
		// re-add mapping reference set membership fields
		for (final String refSetId : mappingReferencingRefSetIds) {
			doc.add(new LongField(SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID, parseLong(refSetId), Store.YES));
		}
	}

}
