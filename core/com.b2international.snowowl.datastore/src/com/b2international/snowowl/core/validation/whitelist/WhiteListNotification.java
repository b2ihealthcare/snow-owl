/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.whitelist;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import com.b2international.snowowl.core.events.SystemNotification;

/**
 * @since 6.1
 */
public class WhiteListNotification extends SystemNotification {

	/**
	 * @since 6.1
	 */
	private static final class Added extends WhiteListNotification {
		
		public Added(Set<String> whiteListIds, Set<String> affectedRuleIds) {
			super(whiteListIds, affectedRuleIds);
		}
		
	}
	
	/**
	 * @since 6.1
	 */
	private static final class Removed extends WhiteListNotification {
		
		public Removed(Set<String> whiteListIds, Set<String> affectedRuleIds) {
			super(whiteListIds, affectedRuleIds);
		}
	
	}
	
	private final Set<String> whiteListIds;
	private final Set<String> affectedRuleIds;
	
	public WhiteListNotification(final Set<String> whiteListIds, final Set<String> affectedRuleIds) {
		checkArgument(!whiteListIds.isEmpty(), "At least one whitelist id must be specified");
		checkArgument(!affectedRuleIds.isEmpty(), "At least one affected rule id must be specified");
		this.whiteListIds = whiteListIds;
		this.affectedRuleIds = affectedRuleIds;
	}
	
	public static WhiteListNotification added(Set<String> whiteListIds, Set<String> affectedRuleIds) {
		return new Added(whiteListIds, affectedRuleIds);
	}

	public static WhiteListNotification removed(Set<String> whiteListIds, Set<String> affectedRuleIds) {
		return new Removed(whiteListIds, affectedRuleIds);
	}
	
	public final boolean isAdded() {
		return this instanceof Added;
	}

	public final boolean isRemoved() {
		return this instanceof Removed;
	}
	
	public Set<String> getWhiteListIds() {
		return whiteListIds;
	}
	
	public Set<String> getAffectedRuleIds() {
		return affectedRuleIds;
	}
	
}
