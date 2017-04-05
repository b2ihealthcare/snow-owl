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
	 * Returns the namespace of this identifier which is a seven digit number allocated by the IHTSDO to an organization that is permitted to maintain a SNOMED CT Extension.
	 * The namespace identifier forms part of the SCTID allocated every component that originated as part of an Extension to prevent collision between SCTIDs issued by different organizations.
	 * The namespace-identifier indicates the provenance of each SNOMED CT component .
     * <br>
     * <br>
     * Note: For short format SCTIDs, which are used for components that originate in the International Release 
     * do not include a namespace-identifier. In this case the partition identifier provides sufficient information about the origin of the component.
	 * @return namespace of this identifier
	 */
	public String getNamespace();
	
	/**
	 * Returns true if the identifiers namespace matches the passed in namespace
	 * @param namespace
	 * @return true if the namespace matches
	 */
	public boolean equalsNamespace(String namespace);
	
	/**
	 * Returns true if the identifiers namespace matches the passed in namespace
	 * @param namespace
	 * @return true if the namespace matches
	 */
	public boolean isNamespace(long namespace);

	/**
	 * Returns true if this identifier has a namespace. International components do not have namespaces.
	 * @return
	 */
	public boolean hasNamespace();

	/** 
	 * The first digit of the partition identifier. Possible values are:
	 * <ul>
	 * <li>0 &rarr; short format (component originated in the INT release) 
	 * <li>1 &rarr; long format (component ID has a namespace and orginated in an extension). All other values are reserved for
	 * future use.
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
	 */
	public int getComponentIdentifier();

	public int getCheckDigit();

	public ComponentCategory getComponentCategory();
}
