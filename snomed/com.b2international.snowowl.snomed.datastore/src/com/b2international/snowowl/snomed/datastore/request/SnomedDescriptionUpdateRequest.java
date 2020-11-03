/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Map;
import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @since 4.5
 */
public final class SnomedDescriptionUpdateRequest extends SnomedComponentUpdateRequest {

	private String caseSignificanceId;
	private Map<String, Acceptability> acceptability;
	
	private String term;
	private String typeId;
	private String languageCode;
	
	SnomedDescriptionUpdateRequest(String componentId) {
		super(componentId);
	}
	
	void setAcceptability(Map<String, Acceptability> acceptability) {
		this.acceptability = acceptability;
	}
	
	void setCaseSignificanceId(String caseSignificanceId) {
		this.caseSignificanceId = caseSignificanceId;
	}
	
	void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
	void setTerm(String term) {
		this.term = term;
	}
	
	void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		final SnomedDescriptionIndexEntry description = context.lookup(componentId(), SnomedDescriptionIndexEntry.class);
		final SnomedDescriptionIndexEntry.Builder updatedDescription = SnomedDescriptionIndexEntry.builder(description); 

		boolean changed = false;
		changed |= updateModule(context, description, updatedDescription);
		changed |= updateCaseSignificanceId(context, description, updatedDescription, caseSignificanceId);
		changed |= updateTypeId(context, description, updatedDescription);
		changed |= updateTerm(context, description, updatedDescription);
		changed |= updateLanguageCode(context, description, updatedDescription);
		changed |= updateEffectiveTime(description, updatedDescription);
		changed |= processInactivation(context, description, updatedDescription);

		// XXX: acceptability and association changes do not push the effective time forward on the description
		// XXX: this should be executed after processInactivation 
		updateAcceptability(context, description);

		if (changed) {
			if (!isEffectiveTimeUpdate() && description.getEffectiveTime() != EffectiveTimes.UNSET_EFFECTIVE_TIME) {
				updatedDescription.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
			}
			context.update(description, updatedDescription.build());
		}

		return changed;
	}
	
	@Override
	protected String getInactivationIndicatorRefSetId() {
		return Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR;
	}

	private void updateAcceptability(TransactionContext context, final SnomedDescriptionIndexEntry description) {
		new SnomedDescriptionAcceptabilityUpdateRequest(description, acceptability, false).execute(context);
	}

	private boolean updateCaseSignificanceId(final TransactionContext context, final SnomedDescriptionIndexEntry original, final SnomedDescriptionIndexEntry.Builder description, final String newCaseSignificanceId) {
		if (null == newCaseSignificanceId) {
			return false;
		}

		final String existingCaseSignificanceId = original.getCaseSignificanceId();
		if (!existingCaseSignificanceId.equals(newCaseSignificanceId)) {
			description.caseSignificanceId(context.lookup(newCaseSignificanceId, SnomedConceptDocument.class).getId());
			return true;
		} else {
			return false;
		}
	}
	
	private boolean updateTypeId(final TransactionContext context, final SnomedDescriptionIndexEntry original, final SnomedDescriptionIndexEntry.Builder description) {
		if (null == typeId) {
			return false;
		}
		
		if (!original.getTypeId().equals(typeId)) {
			checkUpdateOnReleased(original, SnomedRf2Headers.FIELD_TYPE_ID, typeId);
			description.typeId(context.lookup(typeId, SnomedConceptDocument.class).getId());
			return true;
		}
		
		return false;
	}
	
	private boolean updateTerm(final TransactionContext context, final SnomedDescriptionIndexEntry original, final SnomedDescriptionIndexEntry.Builder description) {
		if (null == term) {
			return false;
		}
		
		if (!original.getTerm().equals(term)) {
			description.term(term);
			return true;
		}
		
		return false;
	}
	
	private boolean updateLanguageCode(final TransactionContext context, final SnomedDescriptionIndexEntry original, final SnomedDescriptionIndexEntry.Builder description) {
		if (null == languageCode) {
			return false;
		}
		
		if (!original.getLanguageCode().equals(languageCode)) {
			checkUpdateOnReleased(original, SnomedRf2Headers.FIELD_LANGUAGE_CODE, languageCode);
			description.languageCode(languageCode);
			return true;
		}
		return false;
	}
	
	@Override
	public Set<String> getRequiredComponentIds(TransactionContext context) {
		final Builder<String> ids = ImmutableSet.<String>builder();
		ids.add(componentId());
		if (getInactivationProperties() != null && getInactivationProperties().getInactivationIndicatorId() != null) {
			ids.add(getInactivationProperties().getInactivationIndicatorId());
		}
		if (caseSignificanceId != null) {
			ids.add(caseSignificanceId);
		}
		if (getInactivationProperties() != null && !CompareUtils.isEmpty(getInactivationProperties().getAssociationTargets())) {
			getInactivationProperties().getAssociationTargets().forEach(associationTarget -> {
				ids.add(associationTarget.getReferenceSetId());
				ids.add(associationTarget.getTargetComponentId());
			});
		}
		if (typeId != null) {
			ids.add(typeId);
		}
		return ids.build();
	}
	
}
