/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapdb.DB;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

/**
 * @since 6.0.0
 */
public final class Rf2EffectiveTimeSlices {

	private static final Ordering<Rf2EffectiveTimeSlice> UNSET_EFFECTIVE_TIME_LAST = Ordering.<String>from((effectiveTime1, effectiveTime2) -> {
		if (EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTime1)) {
			return 1;
		} else if(EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTime2)) {
			return -1;
		} else  {
			return effectiveTime1.compareTo(effectiveTime2);
		}
	}).onResultOf(Rf2EffectiveTimeSlice::getEffectiveTime);
	
	private final DB db;
	private final Map<String, Rf2EffectiveTimeSlice> slices = newHashMap();
	private final boolean loadOnDemand;
	private final String latestVersionEffectiveTime;
	private final String importUntil;

	public Rf2EffectiveTimeSlices(DB db, boolean loadOnDemand, String latestVersionEffectiveTime, String importUntil) {
		this.db = db;
		this.loadOnDemand = loadOnDemand;
		this.latestVersionEffectiveTime = latestVersionEffectiveTime;
		this.importUntil = importUntil;
	}
	
	public Rf2EffectiveTimeSlice getOrCreate(String effectiveTime) {
		if (!slices.containsKey(effectiveTime)) {
			// if the incoming effectiveTime value is greater than or equal to the current release, then allow reading
			if (effectiveTime.compareTo(latestVersionEffectiveTime) > 0) {
				if ((Strings.isNullOrEmpty(importUntil) || effectiveTime.compareTo(importUntil) <= 0)) {
					slices.put(effectiveTime, new MapDBRf2EffectiveTimeSlice(effectiveTime, db, loadOnDemand));
				} else {
					slices.put(effectiveTime, new IgnoredRf2EffectiveTimeSlice(effectiveTime, String.format("EffectiveTime '%s' is ignored by importUntil('%s') request parameter.", effectiveTime, importUntil)));
				}
			} else {
				slices.put(effectiveTime, new IgnoredRf2EffectiveTimeSlice(effectiveTime, String.format("EffectiveTime '%s' is already present in the system, skipping.", effectiveTime)));
			}
		}
		return slices.get(effectiveTime);
	}
	
	public List<Rf2EffectiveTimeSlice> slices() {
		return ImmutableList.copyOf(slices.values());
	}

	public void flushAll() {
		slices().forEach(Rf2EffectiveTimeSlice::flush);		
	}

	public List<Rf2EffectiveTimeSlice> consumeInOrder() {
		return slices()
			.stream()
			.sorted(UNSET_EFFECTIVE_TIME_LAST)
			.collect(Collectors.toList());
	}
}
