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

import com.b2international.snowowl.core.events.SystemNotification;
import com.google.common.base.Strings;

/**
 * @since 6.1
 */
public class WhiteListNotification extends SystemNotification {

	/**
	 * @since 6.1
	 */
	private static final class Added extends WhiteListNotification {
		
		public Added(String whiteListId) {
			super(whiteListId);
		}
		
	}
	
	/**
	 * @since 6.1
	 */
	private static final class Removed extends WhiteListNotification {
		
		public Removed(String whiteListId) {
			super(whiteListId);
		}
	
	}
	
	private final String whiteListId;
	
	public WhiteListNotification(final String whiteListId) {
		checkArgument(!Strings.isNullOrEmpty(whiteListId), "Whitelist id must be specified");
		this.whiteListId = whiteListId;
	}
	
	public static WhiteListNotification added(String whiteListId) {
		return new Added(whiteListId);
	}

	public static WhiteListNotification removed(String whiteListId) {
		return new Removed(whiteListId);
	}
	
	public final boolean isAdded() {
		return this instanceof Added;
	}

	public final boolean isRemoved() {
		return this instanceof Removed;
	}
	
	public String getWhiteListId() {
		return whiteListId;
	}
}
