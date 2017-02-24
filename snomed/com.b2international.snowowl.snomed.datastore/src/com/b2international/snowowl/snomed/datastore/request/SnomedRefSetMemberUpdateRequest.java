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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
final class SnomedRefSetMemberUpdateRequest implements Request<TransactionContext, Boolean> {

	@NotEmpty
	private final String memberId;
	
	@NotEmpty
	private final Map<String, Object> properties;

	@NotNull
	private final Boolean force;

	SnomedRefSetMemberUpdateRequest(String memberId, Map<String, Object> properties, Boolean force) {
		this.memberId = memberId;
		this.properties = properties;
		this.force = force;
	}

	@Override
	public Boolean execute(TransactionContext context) {
		final SnomedRefSetMember member = context.lookup(memberId, SnomedRefSetMember.class);
		final SnomedRefSetType type = member.getRefSet().getType();
		RefSetSupport.check(type);

		boolean changed = false;
		
		changed |= updateActivityStatus(member);
		changed |= updateModule(member);
		changed |= updateEffectiveTime(member);
		
		switch (type) {
		case SIMPLE:
			// XXX only activity flag and module ID are supported
			break;
		case QUERY:
			changed |= updateQuery((SnomedQueryRefSetMember) member);
			break;
		case LANGUAGE:
			changed |= updateAcceptability((SnomedLanguageRefSetMember) member);
			break;
		default: throw new UnsupportedOperationException("Not implemented update of " + type + " member"); 
		}
		
		if (changed && !isEffectiveTimeUpdate()) {
			member.unsetEffectiveTime();
		}
		return changed;
	}
	
	private boolean updateAcceptability(SnomedLanguageRefSetMember member) {
		final Object value = properties.get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID);
		if (value instanceof String) {
			final String newAcceptability = (String) value;
			if (!Objects.equals(newAcceptability, member.getAcceptabilityId())) {
				member.setAcceptabilityId(newAcceptability);
				return true;
			}
		}
		return false;
	}


	private boolean isEffectiveTimeUpdate() {
		return force && !Strings.isNullOrEmpty((String) properties.get(SnomedRf2Headers.FIELD_EFFECTIVE_TIME));
	}

	private boolean updateActivityStatus(SnomedRefSetMember member) {
		final Object activeValue = properties.get(SnomedRf2Headers.FIELD_ACTIVE);
		if (activeValue instanceof Boolean) {
			final Boolean newStatus = (Boolean) activeValue;
			if (!Objects.equals(member.isActive(), newStatus)) {
				member.setActive(newStatus);
				return true;
			}
		}
		return false;
	}
	
	private boolean updateModule(SnomedRefSetMember member) {
		final Object value = properties.get(SnomedRf2Headers.FIELD_MODULE_ID);
		if (value instanceof String) {
			final String newModuleId = (String) value;
			if (!Objects.equals(member.getModuleId(), newModuleId)) {
				member.setModuleId(newModuleId);
				return true;
			}
		}
		return false;
	}
	
	private boolean updateEffectiveTime(SnomedRefSetMember member) {
		if (force) {
			final String effectiveTime = (String) properties.get(SnomedRf2Headers.FIELD_EFFECTIVE_TIME);
			if (!Strings.isNullOrEmpty(effectiveTime)) {
				final Date effectiveTimeDate = EffectiveTimes.parse(effectiveTime, DateFormats.SHORT);
				if (!effectiveTimeDate.equals(member.getEffectiveTime())) {
					// if not null set the effective time to the given value and set released to true
					member.setEffectiveTime(effectiveTimeDate);
					member.setReleased(true);
					return true;
				}
			} else {
				// if effective time is null, then unset the effective time but don't change the released flag
				member.unsetEffectiveTime();
				return true;
			}
		}
		return false;
	}
	
	private boolean updateQuery(SnomedQueryRefSetMember member) {
		final Object value = properties.get("query");
		if (value instanceof String) {
			final String newQuery = (String) value;
			if (!Objects.equals(member.getQuery(), newQuery)) {
				member.setQuery(newQuery);
				return true;
			}
		}
		return false;
	}

}
