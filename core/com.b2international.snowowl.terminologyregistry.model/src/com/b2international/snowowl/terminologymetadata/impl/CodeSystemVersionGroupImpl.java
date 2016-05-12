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
package com.b2international.snowowl.terminologymetadata.impl;

import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersionGroup;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Code System Version Group</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionGroupImpl#getRepositoryUuid <em>Repository Uuid</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionGroupImpl#getCodeSystems <em>Code Systems</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionGroupImpl#getCodeSystemVersions <em>Code System Versions</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class CodeSystemVersionGroupImpl extends CDOObjectImpl implements CodeSystemVersionGroup {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CodeSystemVersionGroupImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION_GROUP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected int eStaticFeatureCount() {
		return 0;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRepositoryUuid() {
		return (String)eGet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION_GROUP__REPOSITORY_UUID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRepositoryUuid(String newRepositoryUuid) {
		eSet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION_GROUP__REPOSITORY_UUID, newRepositoryUuid);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<CodeSystem> getCodeSystems() {
		return (EList<CodeSystem>)eGet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION_GROUP__CODE_SYSTEMS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<CodeSystemVersion> getCodeSystemVersions() {
		return (EList<CodeSystemVersion>)eGet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION_GROUP__CODE_SYSTEM_VERSIONS, true);
	}

} //CodeSystemVersionGroupImpl