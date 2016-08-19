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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Map;
import java.util.Objects;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.5
 */
final class SnomedRefSetMemberUpdateRequest extends BaseRequest<TransactionContext, Void> {

	@NotEmpty
	private final String memberId;
	
	@NotEmpty
	private final Map<String, Object> properties;

	SnomedRefSetMemberUpdateRequest(String memberId, Map<String, Object> properties) {
		this.memberId = memberId;
		this.properties = properties;
	}

	@Override
	public Void execute(TransactionContext context) {
		final SnomedRefSetMember member = context.lookup(memberId, SnomedRefSetMember.class);
		final SnomedRefSetType type = member.getRefSet().getType();
		RefSetSupport.check(type);

		boolean changed = false;
		
		changed |= updateActivityStatus(member);
		changed |= updateModule(member);
		
		switch (type) {
		case SIMPLE:
			// XXX only activity flag and module ID are supported
			break;
		case QUERY:
			changed |= updateQuery((SnomedQueryRefSetMember) member);
			break;
		default: throw new UnsupportedOperationException("Not implemented update of " + type + " member"); 
		}
		
		if (changed) {
			member.unsetEffectiveTime();
		}
		return null;
	}

	private boolean updateActivityStatus(SnomedRefSetMember member) {
		final Object activeValue = properties.get("active");
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
		final Object value = properties.get("moduleId");
		if (value instanceof String) {
			final String newModuleId = (String) value;
			if (!Objects.equals(member.getModuleId(), newModuleId)) {
				member.setModuleId(newModuleId);
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

	@Override
	protected Class<Void> getReturnType() {
		return Void.class;
	}

}
