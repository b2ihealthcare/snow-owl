/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
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
