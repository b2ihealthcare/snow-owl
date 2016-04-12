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
package com.b2international.snowowl.snomed;

import org.eclipse.emf.common.util.EList;

/**
 * Representation of a clinical idea with a unique SNOMED&nbsp;CT identifier.
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.Concept#getDefinitionStatus <em>Definition Status</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Concept#getOutboundRelationships <em>Outbound Relationships</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Concept#getInboundRelationships <em>Inbound Relationships</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Concept#getDescriptions <em>Descriptions</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Concept#isExhaustive <em>Exhaustive</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Concept#getFullySpecifiedName <em>Fully Specified Name</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Concept#isPrimitive <em>Primitive</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.SnomedPackage#getConcept()
 * @model
 * @generated
 */
public interface Concept extends Component, Inactivatable, Annotatable {
	/**
	 * Returns with a SNOMED&nbsp;CT {@link Concept concept} indicating whether the concept is primitive all fully defined.
	 * @return the concept indicating the definition status of the current concept.
	 * @see #setDefinitionStatus(Concept)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getConcept_DefinitionStatus()
	 * @model required="true"
	 * @generated
	 */
	Concept getDefinitionStatus();

	/**
	 * Counterpart of {@link #getDefinitionStatus()}.
	 * @param value the concept representing the new definition status of the current concept.
	 * @see #getDefinitionStatus()
	 * @generated
	 */
	void setDefinitionStatus(Concept value);

	/**
	 * Returns with all the source {@link Relationship relationships} of the concept.
	 * @return a list of source/outbound relationships of the concept.
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getConcept_OutboundRelationships()
	 * @see com.b2international.snowowl.snomed.Relationship#getSource
	 * @model opposite="source" containment="true"
	 * @generated
	 */
	EList<Relationship> getOutboundRelationships();

	/**
	 * Returns with all the descriptions {@link Description descriptions} of the concept.
	 * @return a list of descriptions associated with the concept.
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getConcept_Descriptions()
	 * @see com.b2international.snowowl.snomed.Description#getConcept
	 * @model opposite="concept" containment="true"
	 * @generated
	 */
	EList<Description> getDescriptions();

	/**
	 * Returns {@code true} if the concept is exhaustive, otherwise returns with {@code false}.
	 * @return {@code true} if the concept is exhaustive, otherwise {@code false}.
	 * @see #setExhaustive(boolean)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getConcept_Exhaustive()
	 * @model required="true"
	 * @generated
	 */
	boolean isExhaustive();

	/**
	 * Counterpart of {@link #setExhaustive(boolean)}.
	 * @param value the new exhaustive value. {@code true} if active, otherwise {@code false}. 
	 * @see #isExhaustive()
	 * @generated
	 */
	void setExhaustive(boolean value);

	/**
	 * Returns with the unique human readable meaning of the concept.
	 * @return the unique, human readable meaning of the concept.
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getConcept_FullySpecifiedName()
	 * @model default="" required="true" transient="true" changeable="false" derived="true"
	 * @generated
	 */
	String getFullySpecifiedName();

	/**
	 * Returns {@code true} if the current concept is primitive, otherwise returns with {@code false}.
	 * @return {@code true} if the concept is primitive, otherwise returns with {@code false}.
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getConcept_Primitive()
	 * @model required="true" transient="true" changeable="false" derived="true"
	 * @generated
	 */
	boolean isPrimitive();

} // Concept