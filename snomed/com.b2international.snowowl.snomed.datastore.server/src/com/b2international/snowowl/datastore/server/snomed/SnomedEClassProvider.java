/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed;

import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REPOSITORY_UUID;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.getRefSetMemberClass;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;

import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.server.IEClassProvider;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.collect.ImmutableMap;

/**
 * {@link IEClassProvider EClass provider} for SNOMED&nbsp;CT ontology.
 */
public class SnomedEClassProvider implements IEClassProvider {

	private static final Map<Class<? extends Revision>, EClass> SUPPORTED_TYPES = ImmutableMap.<Class<? extends Revision>, EClass>builder()
			.put(SnomedConceptDocument.class, SnomedPackage.Literals.CONCEPT)
			.put(SnomedDescriptionIndexEntry.class, SnomedPackage.Literals.DESCRIPTION)
			.put(SnomedRelationshipIndexEntry.class, SnomedPackage.Literals.RELATIONSHIP)
			.put(SnomedConstraintDocument.class, MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT)
			.put(SnomedRefSetMemberIndexEntry.class, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER)
			.build();
			
	
	@Override
	public EClass getEClass(RevisionSearcher index, final long storageKey) throws IOException {
		for (Entry<Class<? extends Revision>, EClass> entry : SUPPORTED_TYPES.entrySet()) {
			final Revision rev = index.get(entry.getKey(), storageKey);
			if (rev != null) {
				final EClass value = entry.getValue();
				if (SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER == value) {
					final SnomedRefSetMemberIndexEntry member = (SnomedRefSetMemberIndexEntry) rev;
					return getRefSetMemberClass(member.getReferenceSetType());
				} else {
					return value;
				}
			}
		}
		// if still not found, then try to look for the refset storage key field
		final Hits<SnomedConceptDocument> hits = index.search(Query.select(SnomedConceptDocument.class)
				.where(SnomedConceptDocument.Expressions.refSetStorageKey(storageKey))
				.limit(0)
				.build());
		if (hits.getTotal() > 0) {
			return SnomedRefSetPackage.Literals.SNOMED_REF_SET;
		} else {
			return null;
		}
	}
	
	@Override
	public String getRepositoryUuid() {
		return REPOSITORY_UUID;
	}
	
}