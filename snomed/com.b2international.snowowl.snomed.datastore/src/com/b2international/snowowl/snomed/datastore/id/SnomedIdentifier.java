/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id;

import java.io.Serializable;

import com.b2international.snowowl.core.terminology.ComponentCategory;

/**
 * @since 4.0
 * @see <a href="https://confluence.ihtsdotools.org/display/DOCRELFMT/5.+Representing+SNOMED+CT+identifiers">Representing SNOMED CT identifiers</a>
 */
public interface SnomedIdentifier extends Serializable {

	public long getItemId();

	/**
	 * Returns the namespace identifier of this SCTID.
	 * 
	 * @return the namespace identifier or <code>null</code> if this SCTID is a short format ID
	 * @see <a href="https://confluence.ihtsdotools.org/display/DOCGLOSS/Namespace+identifier">SNOMED Glossary: Namespace identifier</a>
	 */
	public String getNamespace();
	
	/**
	 * Returns <code>true</code> if the identifiers namespace matches the specified
	 * namespace, <code>false</code> otherwise.
	 * 
	 * @param namespace the namespace to match (can be <code>null</code>)
	 * @return <code>true</code> if the SCTID's namespace matches the specified value
	 */
	public boolean equalsNamespace(String namespace);

	/**
	 * Returns <code>true</code> if this SCTID has a namespace identifier,
	 * <code>false</code> otherwise. Short format IDs do not have namespaces.
	 * 
	 * @return <code>true</code> for long format SCTIDs, <code>false</code> for short format SCTIDs
	 */
	public boolean hasNamespace();

	/** 
	 * The first digit of the partition identifier. Possible values are:
	 * <ul>
	 * <li>0 &rarr; short format (component originated in the INT release) 
	 * <li>1 &rarr; long format (component ID has a namespace and orginated in an extension).
	 * </ul>
	 * All other values are reserved for future use.
	 * 
	 * @return the format identifier digit
	 */
	public int getFormatIdentifier();

	/**
	 * The second digit of the partition identifier. Possible values are:
	 * <ul>
	 * <li>0 &rarr; Concept
	 * <li>1 &rarr; Description
	 * <li>2 &rarr; Relationship
	 * </ul>
	 * All other values a reserved for future use.
	 * 
	 * @return the component identifier digit
	 */
	public int getComponentIdentifier();

	/**
	 * Returns the last (check) digit of this SCTID, which can be used to verify the
	 * identifier. Note that this is the value that originally came with the
	 * instance; it is not recomputed here.
	 * 
	 * @return the check digit, as entered at creation time
	 * @see <a href="https://confluence.ihtsdotools.org/display/DOCGLOSS/Check-digit">SNOMED Glossary: Check-digit</a>
	 */
	public int getCheckDigit();

	/**
	 * Returns the category of the component this SCTID identifiers. The value is
	 * derived from the second digit of the partition identifier:
	 * <ul>
	 * <li>0 &rarr; {@link ComponentCategory#CONCEPT}
	 * <li>1 &rarr; {@link ComponentCategory#DESCRIPTION}
	 * <li>2 &rarr; {@link ComponentCategory#RELATIONSHIP}
	 * </ul>
	 * 
	 * @return the category of the component identified by this SCTID
	 */
	public ComponentCategory getComponentCategory();
}
