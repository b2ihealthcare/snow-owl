/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.converter;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.request.BaseRevisionResourceConverter;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 4.5
 */
final class SnomedReferenceSetConverter extends BaseRevisionResourceConverter<SnomedConceptDocument, SnomedReferenceSet, SnomedReferenceSets> {
	
	protected SnomedReferenceSetConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected SnomedReferenceSets createCollectionResource(List<SnomedReferenceSet> results, String scrollId, int limit, int total) {
		return new SnomedReferenceSets(results, scrollId, limit, total);
	}
	
	@Override
	protected void expand(List<SnomedReferenceSet> results) {
		expandMembers(results);
	}

	private void expandMembers(List<SnomedReferenceSet> results) {
		if (expand().containsKey("members")) {
			Options expandOptions = expand().get("members", Options.class);
			
			for (SnomedReferenceSet refSet : results) {
				SnomedRefSetMemberSearchRequestBuilder req = SnomedRequests.prepareSearchMember()
						.filterByRefSet(refSet.getId())
						.setLocales(locales())
						.setExpand(expandOptions.get("expand", Options.class));
				
				if (expandOptions.containsKey("limit")) {
					req.setLimit(expandOptions.get("limit", Integer.class));
				}

				refSet.setMembers(req.build().execute(context()));
			}
		}
	}
	
	@Override
	public SnomedReferenceSet toResource(SnomedConceptDocument entry) {
		final SnomedReferenceSet refset = new SnomedReferenceSet();
		refset.setStorageKey(entry.getRefSetStorageKey());
		refset.setId(entry.getId());
		refset.setEffectiveTime(EffectiveTimes.toDate(entry.getEffectiveTime()));
		refset.setActive(entry.isActive());
		refset.setReleased(entry.isReleased());
		refset.setModuleId(entry.getModuleId());
		refset.setIconId(entry.getIconId());
		refset.setScore(entry.getScore());
		final int referencedComponentType = entry.getReferencedComponentType();
		if (referencedComponentType > 0) {
			refset.setReferencedComponentType(getReferencedComponentType(referencedComponentType));
		}
		final int mapTargetComponentType = entry.getMapTargetComponentType();
		if (mapTargetComponentType > 0) {
			refset.setMapTargetComponentType(getReferencedComponentType(mapTargetComponentType));
		}
		refset.setType(entry.getRefSetType());
		return refset;
	}

	private String getReferencedComponentType(final int referencedComponentType) {
		return CoreTerminologyBroker.getInstance().getComponentInformation((short) referencedComponentType).getId();
	}
}
