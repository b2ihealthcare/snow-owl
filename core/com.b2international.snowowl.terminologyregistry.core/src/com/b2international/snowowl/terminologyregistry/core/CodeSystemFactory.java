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
package com.b2international.snowowl.terminologyregistry.core;

import org.eclipse.emf.cdo.CDOState;

import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataFactory;

/**
 * Abstract factory for creating {@link CodeSystem code system} instances.
 *
 */
public abstract class CodeSystemFactory implements ICodeSystemFactory {

	/**Creates and returns with a new {@link CodeSystem code system} instance.
	 *<p>The new code system instance is in {@link CDOState#TRANSIENT transient} by default.*/
	public CodeSystem createNewCodeSystem() {
		
		final CodeSystem codeSystem = createCodeSystem();
		codeSystem.setCitation(getCitation());
		codeSystem.setCodeSystemOID(getCodeSystemOid());
		codeSystem.setIconPath(getIconPath());
		codeSystem.setLanguage(getLanguage());
		codeSystem.setMaintainingOrganizationLink(getMaintainingOrganizationLink());
		codeSystem.setName(getName());
		codeSystem.setShortName(getShortName());
		codeSystem.setTerminologyComponentId(getTerminologyComponentId());
		codeSystem.setRepositoryUuid(getRepositoryUuid());
		
		return codeSystem;
		
	}

	/**
	 * Return with the application specific terminology component ID for the component.  
	 */
	protected abstract String getTerminologyComponentId();

	/**
	 * Creates and returns with a new code system instance.
	 */
	protected CodeSystem createCodeSystem() {
		return TerminologymetadataFactory.eINSTANCE.createCodeSystem();
	}

	/**
	 * Returns with the short name of the code system. Code system short name is globally unique.
	 */
	protected abstract String getShortName();

	/**
	 * Returns with the name of the code system.
	 */
	protected abstract String getName();

	/**
	 * Returns with the HTTP URL of the maintaining organization. Could be {@code null}.
	 */
	protected abstract String getMaintainingOrganizationLink();

	/**
	 * Returns with the language of the code system.
	 */
	protected abstract String getLanguage();

	/**
	 * Returns with the application specific icon path for the code system.
	 */
	protected abstract String getIconPath();

	/**
	 * Returns with the code system OID.
	 */
	protected abstract String getCodeSystemOid();

	/**
	 * Returns with the citation for the code system.
	 */
	protected abstract String getCitation();
	
	/**
	 * Returns with the repository uuid for the code system.
	 */
	protected abstract String getRepositoryUuid();
	
}