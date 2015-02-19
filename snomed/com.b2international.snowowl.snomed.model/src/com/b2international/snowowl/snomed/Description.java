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

import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;

/**
 * Representation of a SNOMED&nbsp;CT description. This associates some human readable literal with 
 * a {@link Concept SNOMED&nbsp;CT concept} that it describes. 
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.Description#getLanguageCode <em>Language Code</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Description#getTerm <em>Term</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Description#getConcept <em>Concept</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Description#getType <em>Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Description#getCaseSignificance <em>Case Significance</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Description#getLanguageRefSetMembers <em>Language Ref Set Members</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.SnomedPackage#getDescription()
 * @model
 * @generated
 */
public interface Description extends Component, Inactivatable {
	
	/**
	 * Returns with the code of the particular language or dialect which the current description is associated with.
	 * @return the code of the associated language or dialect. 
	 * @see #setLanguageCode(String)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getDescription_LanguageCode()
	 * @model required="true"
	 * @generated
	 */
	String getLanguageCode();

	/**
	 * Counterpart of {@link #getLanguageCode()}. 
	 * @param value the new value of the language code.
	 * @see #getLanguageCode()
	 * @generated
	 */
	void setLanguageCode(String value);

	/**
	 * Returns with the human readable phrase that used to name the {@link Concept concept}.
	 * @return the human readable literal of the description instance.
	 * @see #setTerm(String)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getDescription_Term()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getTerm();

	/**
	 * Counterpart of {@link #getTerm()}.
	 * @param value the new human readable phrase for the description.
	 * @see #getTerm()
	 * @generated
	 */
	void setTerm(String value);

	/**
	 * Returns wit the {@link Concept concept} this description is associated with.
	 * @return the SNOMED&nbsp;CT concept which is described with the current description.
	 * @see #setConcept(Concept)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getDescription_Concept()
	 * @see com.b2international.snowowl.snomed.Concept#getDescriptions
	 * @model opposite="descriptions" required="true" transient="false"
	 * @generated
	 */
	Concept getConcept();

	/**
	 * Counterpart of the {@link #getConcept()}.
	 * @param value the new concept value.
	 * @see #getConcept()
	 * @generated
	 */
	void setConcept(Concept value);

	/**
	 * Returns with the concept that specified the type of the description. 
	 * @return the SNOMED&nbsp; description type concept.
	 * @see #setType(Concept)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getDescription_Type()
	 * @model required="true"
	 * @generated
	 */
	Concept getType();

	/**
	 * Counterpart of the {@link #getType()}.
	 * @param value the new description type concept of the description. 
	 * @see #getType()
	 * @generated
	 */
	void setType(Concept value);

	/**
	 * Returns with a SNOMED&nbsp;CT concept defining the case significance property of the description.
	 * @return the SNOME&nbsp;CT case significance concept of the description.
	 * @see #setCaseSignificance(Concept)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getDescription_CaseSignificance()
	 * @model required="true"
	 * @generated
	 */
	Concept getCaseSignificance();

	/**
	 * Counterpart of the {@link #getCaseSignificance()}.
	 * @param value the new case significance concept.
	 * @see #getCaseSignificance()
	 * @generated
	 */
	void setCaseSignificance(Concept value);

	/**
	 * Returns with a list of SNOMED&nbsp;CT language type reference set member referencing the current descriptions.
	 * @return a list of language type reference set members.
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getDescription_LanguageRefSetMembers()
	 * @model containment="true" ordered="false"
	 * @generated
	 */
	EList<SnomedLanguageRefSetMember> getLanguageRefSetMembers();

} // Description