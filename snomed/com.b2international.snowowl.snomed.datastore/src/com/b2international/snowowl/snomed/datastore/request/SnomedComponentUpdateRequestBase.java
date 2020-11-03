/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.request.UpdateRequest;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.11
 */
public abstract class SnomedComponentUpdateRequestBase extends UpdateRequest<TransactionContext> implements SnomedComponentRequest<Boolean> {

	private static final long serialVersionUID = 1L;

	@NotNull
	@JsonProperty
	private Boolean force;

	public SnomedComponentUpdateRequestBase(String componentId) {
		super(componentId);
	}
	
	protected final Boolean isForce() {
		return force;
	}
	
	void setForce(Boolean force) {
		this.force = force;
	}
	
	protected final <B extends SnomedDocument.Builder<B, T>, T extends SnomedDocument> boolean updateEffectiveTime(T original, B updated) {
		if (!isEffectiveTimeUpdate()) {
			return false;
		}
		
		Date newEffectiveTime = EffectiveTimes.parse(effectiveTime(), DateFormats.SHORT);
		if (!Objects.equals(newEffectiveTime, EffectiveTimes.toDate(original.getEffectiveTime()))) {
			if (newEffectiveTime == null) {
				// if effective time is null, then unset the effective time but don't change the released flag
				updated.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
			} else {
				// otherwise set the value and toggle the "relased" flag
				updated.effectiveTime(newEffectiveTime.getTime());
				updated.released(true);
			}
			return true;
		} else {
			return false;
		}
	}

	protected final boolean isEffectiveTimeUpdate() {
		if (effectiveTime() != null) {
			if (isForce()) {
				return true;
			} else {
				throw new BadRequestException("EffectiveTime updates are not allowed by default. force=true is required to perform effective time updates.");
			}
		} else {
			return false;
		}
	}

	protected abstract String effectiveTime();
	
}
