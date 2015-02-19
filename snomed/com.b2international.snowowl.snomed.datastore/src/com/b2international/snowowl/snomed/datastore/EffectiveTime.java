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
package com.b2international.snowowl.snomed.datastore;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.google.common.base.Preconditions;

/**
 * Represents an effective time in the SNOMED&nbsp;CT ontology.
 * <p>Could represent an {@link DateUtils#UNSET_EFFECTIVE_TIME_LABEL unpublished} effective time.
 */
public final class EffectiveTime {

	private final long effectiveTime;

	/**
	 * Creates an effective time from the given string.
	 * <br>Accepts date as {@link DateUtils#DEFAULT_DATE_FORMAT} or {@link DateUtils#UNSET_EFFECTIVE_TIME_LABEL}.
	 */
	public static EffectiveTime create(final String effectiveTime) {
		return EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL.equals(Preconditions.checkNotNull(effectiveTime)) 
				? create(EffectiveTimes.UNSET_EFFECTIVE_TIME) 
				: create(EffectiveTimes.parse(effectiveTime)); 
	}

	/**
	 * Creates an effective time from the given date. {@code null} is permitted, in case of
	 * {@code null} date, the new effective time will represent an {@link DateUtils#UNSET_EFFECTIVE_TIME_LABEL}.
	 */
	public static EffectiveTime create(@Nullable final Date effectiveTime) {
		return null == effectiveTime 
				? new EffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME) 
				: new EffectiveTime(effectiveTime.getTime());
	}

	/**
	 * Creates a new effective time based on the given timestamp.
	 */
	public static EffectiveTime create(final long effectiveTime) {
		return new EffectiveTime(effectiveTime);
	}
	
	private EffectiveTime(final long effectiveTime) {
		Preconditions.checkArgument(
				effectiveTime >= EffectiveTimes.UNSET_EFFECTIVE_TIME, 
				"Effective time timestamp should be greater or equal to -1.");
		this.effectiveTime = effectiveTime;
	}
	
	/**
	 * Returns with the effective time as a string.
	 */
	public String getEffectiveTime() {
		return EffectiveTimes.format(effectiveTime);
	}

	/**Returns with the effective timestamp.*/
	public long getEffectiveTimestamp() {
		return effectiveTime;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (effectiveTime ^ (effectiveTime >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EffectiveTime))
			return false;
		final EffectiveTime other = (EffectiveTime) obj;
		if (effectiveTime != other.effectiveTime)
			return false;
		return true;
	}

	/**
	 * Returns {@code true} if the given effective time argument represents an unpublished 
	 * effective time, otherwise returns with {@code false}.
	 */
	public static boolean isUnpublished(final Object effectiveTime) {
		
		if (effectiveTime == null) {
			return true;
		}
		
		if (EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTime)) {
			return true;
		}
		
		long timestamp = Long.MIN_VALUE;
		
		if (effectiveTime instanceof Date) {
			timestamp = ((Date) effectiveTime).getTime();
		} else if (effectiveTime instanceof java.sql.Date) {
			timestamp = ((java.sql.Date) effectiveTime).getTime();
		} else if (effectiveTime instanceof Timestamp) {
			timestamp = ((Timestamp) effectiveTime).getTime();
		} else if (effectiveTime instanceof Long) {
			timestamp = ((Long) effectiveTime).longValue();
		} else if (effectiveTime instanceof EffectiveTime) {
			timestamp = ((EffectiveTime) effectiveTime).getEffectiveTimestamp();
		}
		
		return EffectiveTimes.UNSET_EFFECTIVE_TIME == timestamp;
	}
	
}