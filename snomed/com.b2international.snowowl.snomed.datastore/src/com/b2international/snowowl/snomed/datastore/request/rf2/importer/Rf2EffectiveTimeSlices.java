/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapdb.DB;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

/**
 * @since 6.0.0
 */
public final class Rf2EffectiveTimeSlices {

	private final DB db;
	private final Map<String, Rf2EffectiveTimeSlice> slices = newHashMap();
	private final Map<String, Long> storageKeysByComponent;
	private final Map<String, Long> storageKeysByRefSet;
	private final boolean loadOnDemand; 

	public Rf2EffectiveTimeSlices(DB db, Map<String, Long> storageKeysByComponent, Map<String, Long> storageKeysByRefSet, boolean loadOnDemand) {
		this.db = db;
		this.storageKeysByComponent = storageKeysByComponent;
		this.storageKeysByRefSet = storageKeysByRefSet;
		this.loadOnDemand = loadOnDemand;
	}
	
	public Rf2EffectiveTimeSlice getOrCreate(String effectiveTime) {
		if (!slices.containsKey(effectiveTime)) {
			slices.put(effectiveTime, new Rf2EffectiveTimeSlice(db, effectiveTime, storageKeysByComponent, storageKeysByRefSet, loadOnDemand));
		}
		return slices.get(effectiveTime);
	}
	
	public Iterable<Rf2EffectiveTimeSlice> slices() {
		return ImmutableList.copyOf(slices.values());
	}

	public void flushAll() {
		slices().forEach(Rf2EffectiveTimeSlice::flush);		
	}

	public Iterable<Rf2EffectiveTimeSlice> consumeInOrder() {
		return Ordering.from((o1, o2) -> {
			if (o1 instanceof String && o2 instanceof String) {
				final String effectiveTime1 = (String) o1;
				final String effectiveTime2 = (String) o2;
				if (EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTime1) || EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTime2)) {
					return -1;
				} else {
					// both are effective times and we can safely convert them
					final Date effectiveDate1 = EffectiveTimes.parse(effectiveTime1, DateFormats.SHORT);
					final Date effectiveDate2 = EffectiveTimes.parse(effectiveTime2, DateFormats.SHORT);
					return effectiveDate1.compareTo(effectiveDate2);
				}
			}
			return -1;
		}).immutableSortedCopy(slices.keySet()).stream().map(slices::get).collect(Collectors.toList());
	}
	
}
