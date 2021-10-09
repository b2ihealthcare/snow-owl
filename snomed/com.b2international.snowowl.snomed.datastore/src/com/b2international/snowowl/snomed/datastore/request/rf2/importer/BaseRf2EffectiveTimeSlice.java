/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.importer;

import java.time.LocalDate;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;

/**
 * @since 8.0
 */
abstract class BaseRf2EffectiveTimeSlice implements Rf2EffectiveTimeSlice {

	private final LocalDate effectiveDate;
	private final String effectiveTime;
	
	public BaseRf2EffectiveTimeSlice(String effectiveTime) {
		if (EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTime) || SNAPSHOT_SLICE.equals(effectiveTime)) {
			this.effectiveDate = null;
			this.effectiveTime = effectiveTime;
		} else {
			this.effectiveDate = EffectiveTimes.parse(effectiveTime, DateFormats.SHORT);
			this.effectiveTime = EffectiveTimes.format(effectiveDate, DateFormats.DEFAULT);
		}
	}
	
	@Override
	public final String getEffectiveTime() {
		return effectiveTime;
	}
	
	protected final LocalDate getEffectiveDate() {
		return effectiveDate;
	}
	
	protected final boolean isUnpublishedSlice() {
		return EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTime);
	}
	
	protected final boolean isSnapshotSlice() {
		return SNAPSHOT_SLICE.equals(effectiveTime);
	}

}
