/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain;

import java.text.MessageFormat;

import com.google.common.base.Strings;

/**
 * Enumerates OWL class axiom types currently in use
 * 
 * @since 6.23.0
 *
 */
public enum OWLAxiomType {

	/**
	 * Class type axioms
	 */
	CLASS("Class", 0),
	
	/**
	 * Equivalent classes type axiom
	 */
	EQUIVALENT_CLASS("EquivalentClass", 1),

	/**
	 * General concept inclusion type axiom
	 */
	GCI("GCI", 2);

	private final String axiomType;
	private final int value;

	private OWLAxiomType(final String axiomType, int value) {
		this.axiomType = axiomType;
		this.value = value;
	}

	public String getAxiomType() {
		return axiomType;
	}
	
	public int getValue() {
		return value;
	}

	public static OWLAxiomType getByAxiomType(final String axiomType) {
		if (Strings.isNullOrEmpty(axiomType)) {
			return null;
		}

		for (final OWLAxiomType candidate : values()) {
			if (candidate.getAxiomType().equals(axiomType)) {
				return candidate;
			}
		}

		throw new IllegalArgumentException(MessageFormat.format("No axiom type found for value ''{0}''.", axiomType));
	}
}
