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

import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;


/**
 * Representation of a SNOMED&nbsp;CT relationship. Represents some kind of association between two 
 * SNOMED&nbsp;CT {@link Concept concept}s.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.Relationship#getGroup <em>Group</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Relationship#getUnionGroup <em>Union Group</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Relationship#isDestinationNegated <em>Destination Negated</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Relationship#getSource <em>Source</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Relationship#getDestination <em>Destination</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Relationship#getType <em>Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Relationship#getCharacteristicType <em>Characteristic Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Relationship#getModifier <em>Modifier</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Relationship#getRefinabilityRefSetMembers <em>Refinability Ref Set Members</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.SnomedPackage#getRelationship()
 * @model
 * @generated
 */
public interface Relationship extends Component, Annotatable {
	
	/**
	 * Returns with the relationship group where this relationship belongs to.
	 * @return the relationship group number.
	 * @see #setGroup(int)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getRelationship_Group()
	 * @model required="true"
	 * @generated
	 */
	int getGroup();

	/**
	 * Counterpart of {@link #getGroup()}.
	 * @param value the new relationship group value.
	 * @see #getGroup()
	 * @generated
	 */
	void setGroup(int value);

	/**
	 * Returns with the relationship union group for the relationship.
	 * @return the relationship union group value. 
	 * @see #setUnionGroup(int)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getRelationship_UnionGroup()
	 * @model required="true"
	 * @generated
	 */
	int getUnionGroup();

	/**
	 * Counterpart of the {@link #getUnionGroup()}.
	 * @param value the relationship union group value.
	 * @see #getUnionGroup()
	 * @generated
	 */
	void setUnionGroup(int value);

	/**
	 * Returns {@code true} if the association between the source concept and the destination concept is negated.
	 * Otherwise {@code false}.
	 * @return {@code true} if the association is negated between the source and destination concepts. Otherwise {@code false}.
	 * @see #setDestinationNegated(boolean)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getRelationship_DestinationNegated()
	 * @model required="true"
	 * @generated
	 */
	boolean isDestinationNegated();

	/**
	 * Counterpart of the {@link #isDestinationNegated()}.
	 * @param value the new value for indicating negated association.
	 * @see #isDestinationNegated()
	 * @generated
	 */
	void setDestinationNegated(boolean value);

	/**
	 * Returns with the source {@link Concept concept} of the association represented by the current relationship. 
	 * @return the source concept of the relationship.
	 * @see #setSource(Concept)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getRelationship_Source()
	 * @see com.b2international.snowowl.snomed.Concept#getOutboundRelationships
	 * @model opposite="outboundRelationships" required="true" transient="false"
	 * @generated
	 */
	Concept getSource();

	/**
	 * Counterpart of the {@link #getSource()}.
	 * @param value the new source concept of the relationship.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(Concept value);

	/**
	 * Returns with the target concept of an association represented by the current relationship.
	 * @return the target/source of the current association.
	 * @see #setDestination(Concept)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getRelationship_Destination()
	 * @see com.b2international.snowowl.snomed.Concept#getInboundRelationships
	 * @model opposite="inboundRelationships" required="true"
	 * @generated
	 */
	Concept getDestination();

	/**
	 * Counterpart of {@link #getDestination()}.
	 * @param value the new target/destination of the current statement between concepts.
	 * @see #getDestination()
	 * @generated
	 */
	void setDestination(Concept value);

	/**
	 * Returns with a concept representing the nature of the association between the source and target concepts.
	 * @return the relationship type concept.
	 * @see #setType(Concept)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getRelationship_Type()
	 * @model required="true"
	 * @generated
	 */
	Concept getType();

	/**
	 * Counterpart of the {@link #getType()}.
	 * @param value the new the new type concept of the relationship. 
	 * @see #getType()
	 * @generated
	 */
	void setType(Concept value);

	/**
	 * Returns with a concept indicating that the current relationship specified defining, qualifying, historical
	 * or an additional characteristic.  
	 * @return the SNOMED&nbsp;CT {@link Concept concept} indicating the relationship's characteristic type.
	 * @see #setCharacteristicType(Concept)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getRelationship_CharacteristicType()
	 * @model required="true"
	 * @generated
	 */
	Concept getCharacteristicType();

	/**
	 * Counterpart of {@link #getCharacteristicType()}.
	 * @param value the concept specifying the new characteristic type of the relationship.
	 * @see #getCharacteristicType()
	 * @generated
	 */
	void setCharacteristicType(Concept value);

	/**
	 * Returns with a {@link Concept concept} representing the Description Logic (DL) restriction of
	 * the current relationship. (E.g.: some, all). 
	 * @return the value of the '<em>Modifier</em>' reference.
	 * @see #setModifier(Concept)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getRelationship_Modifier()
	 * @model required="true"
	 * @generated
	 */
	Concept getModifier();

	/**
	 * Counterpart of {@link #getModifier()}.
	 * @param value the concept representing the new modifier value of the current relationship.
	 * @see #getModifier()
	 * @generated
	 */
	void setModifier(Concept value);

	/**
	 * Returns with all the SNOMED&nbsp;CT reference set members describing the refinability of the current relationship.  
	 * @return the a list of reference set members associated with the relationship refinability behavior.
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getRelationship_RefinabilityRefSetMembers()
	 * @model containment="true"
	 * @generated
	 */
	EList<SnomedAttributeValueRefSetMember> getRefinabilityRefSetMembers();

} // Relationship