/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.validation.whitelist;

import java.io.Serializable;

import com.b2international.index.Doc;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @since 6.1
 */
@Doc
public final class ValidationWhiteList implements Serializable {

	/**
	 * @since 6.1
	 */
	public static final class Fields {
		public static final String ID = "id";
		public static final String RULE_ID = "ruleId";
		public static final String COMPONENT_IDENTIFIER = "componentIdentifier";
	}
	
	private final String id;
	private final String ruleId;
	private final ComponentIdentifier componentIdentifier;

	@JsonCreator
	public ValidationWhiteList(
			@JsonProperty("id") final String id,
			@JsonProperty("ruleId") final String ruleId,
			@JsonProperty("componentIdentifier") final ComponentIdentifier componentIdentifier) {
	
		this.id = id;
		this.ruleId = ruleId;
		this.componentIdentifier = componentIdentifier;
	}
	
	public String getId() {
		return id;
	}

	public String getRuleId() {
		return ruleId;
	}
	
	public ComponentIdentifier getComponentIdentifier() {
		return componentIdentifier;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
			.add("id", id)
			.add("ruleId", ruleId)
			.add("componentIdentifier", componentIdentifier)
			.toString();
	}
	
}