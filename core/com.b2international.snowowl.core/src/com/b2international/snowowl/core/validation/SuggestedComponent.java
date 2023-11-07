/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.google.common.base.MoreObjects;

/**
 * Represents a component that supplements the suggested action for a validation issue.
 * 
 * @since 9.0.0
 */
public final class SuggestedComponent {

	/**
	 * Qualifier indicating that the component is an inactive concept
	 */
	public static final String QUALIFIER_INACTIVE_CONCEPT = "inactiveConcept";

	/**
	 * Qualifier indicating that the suggested component is a historical association
	 * reference set type
	 */
	public static final String QUALIFIER_ASSOCIATION_TYPE = "associationType";

	/**
	 * Qualifier indicating that the suggested component is a target component (ie.
	 * potential replacement for an inactive concept)
	 */
	public static final String QUALIFIER_REPLACEMENT = "replacement";

	private final String qualifier;
	private final ComponentIdentifier identifier; 
	private String label;

	public SuggestedComponent(final String qualifier, final ComponentIdentifier identifier) {
		this(qualifier, identifier, identifier.getComponentId());
	}

	public SuggestedComponent(final String qualifier, final ComponentIdentifier identifier, final String label) {
		this.qualifier = qualifier;
		this.identifier = identifier;
		this.label = label;
	}

	public String getQualifier() {
		return qualifier;
	}

	public ComponentIdentifier getIdentifier() {
		return identifier;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("qualifier", qualifier)
			.add("identifier", identifier)
			.add("label", label)
			.toString();
	}
}
