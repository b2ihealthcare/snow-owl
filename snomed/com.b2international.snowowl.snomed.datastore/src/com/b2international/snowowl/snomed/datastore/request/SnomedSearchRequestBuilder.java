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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Date;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedSearchRequest.OptionKey;

/**
 * Abstract class for SNOMED CT search request builders. It collects functionality common to SNOMED CT components.
 * 
 * @since 4.5
 */
public abstract class SnomedSearchRequestBuilder<B extends SnomedSearchRequestBuilder<B, R>, R> 
		extends SearchResourceRequestBuilder<B, BranchContext, R>
		implements RevisionIndexRequestBuilder<R> {

	/**
	 * Filter to return components with the specified module id. 
	 * Commonly used module IDs are listed in the {@link com.b2international.snowowl.snomed.Concepts} class.
	 * 
	 * @param moduleId
	 * @return this builder
	 */
	public final B filterByModule(String moduleId) {
		return addOption(OptionKey.MODULE, moduleId);
	}
	
	/**
	 * Filter to return components with the specified module id set. 
	 * Commonly used module IDs are listed in the {@link com.b2international.snowowl.snomed.Concepts} class.
	 * 
	 * @param moduleIds
	 * @return
	 */
	public final B filterByModules(Iterable<String> moduleIds) {
		return addOption(OptionKey.MODULE, moduleIds);
	}

	/**
	 * Filter to return components with the specified state (active/inactive)
	 * 
	 * @param active
	 * @return this builder
	 */
	public final B filterByActive(Boolean active) {
		return addOption(OptionKey.ACTIVE, active);
	}
	
	/**
	 * Filter to return components with the specified released flag value (released|unreleased)
	 * 
	 * @param released
	 * @return this builder
	 */
	public final B filterByReleased(Boolean released) {
		return addOption(OptionKey.RELEASED, released);
	}

	/**
	 * Filter to return components with the specified effective time represented as a string in {@value DateFormats#SHORT} format
	 * 
	 * @param effectiveTime - the effective time filter.
	 * @return this builder
	 * @see DateFormats#SHORT
	 * @see EffectiveTimes
	 */
	public final B filterByEffectiveTime(String effectiveTime) {
		if (CompareUtils.isEmpty(effectiveTime)) {
			return getSelf(); 
		} else {
			return filterByEffectiveTime(EffectiveTimes.parse(effectiveTime, DateFormats.SHORT).getTime());
		}
	}
	
	/**
	 * Filter to return components with the specified effective time represented as a long (ms since epoch) format.
	 * 
	 * @param effectiveTime - in long (ms since epoch) format
	 * @return this builder
	 * 
	 * @see EffectiveTimes
	 * @see Date#Date(long)
	 * @see Date#getTime()
	 */
	public final B filterByEffectiveTime(long effectiveTime) {
		return filterByEffectiveTime(effectiveTime, effectiveTime);
	}
	
	/**
	 * Filter to return components with the effective times that fall between the start and end dates
	 * represented as longs (ms since epoch).
	 * 
	 * @param from - effectiveTime starting effective time in long (ms since epoch) format
	 * @param to - effectiveTime ending effective time in long (ms since epoch) format
	 * @return this builder

	 * @see EffectiveTimes
	 * @see Date#Date(long)
	 * @see Date#getTime()
	 */
	public final B filterByEffectiveTime(long from, long to) {
		return addOption(OptionKey.EFFECTIVE_TIME_START, from).addOption(OptionKey.EFFECTIVE_TIME_END, to);
	}

}
