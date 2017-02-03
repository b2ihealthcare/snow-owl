/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;

import java.util.Date;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Code System Version</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionImpl#getVersionId <em>Version Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionImpl#getParentBranchPath <em>Parent Branch Path</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionImpl#getEffectiveDate <em>Effective Date</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionImpl#getImportDate <em>Import Date</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionImpl#getLastUpdateDate <em>Last Update Date</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CodeSystemVersionImpl extends CDOObjectImpl implements CodeSystemVersion {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CodeSystemVersionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION;
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
	public String getVersionId() {
		return (String)eGet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__VERSION_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVersionId(String newVersionId) {
		eSet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__VERSION_ID, newVersionId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription() {
		return (String)eGet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__DESCRIPTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription) {
		eSet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__DESCRIPTION, newDescription);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getParentBranchPath() {
		return (String)eGet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__PARENT_BRANCH_PATH, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentBranchPath(String newParentBranchPath) {
		eSet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__PARENT_BRANCH_PATH, newParentBranchPath);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Date getEffectiveDate() {
		return (Date)eGet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__EFFECTIVE_DATE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEffectiveDate(Date newEffectiveDate) {
		eSet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__EFFECTIVE_DATE, newEffectiveDate);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Date getImportDate() {
		return (Date)eGet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__IMPORT_DATE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImportDate(Date newImportDate) {
		eSet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__IMPORT_DATE, newImportDate);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Date getLastUpdateDate() {
		return (Date)eGet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__LAST_UPDATE_DATE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLastUpdateDate(Date newLastUpdateDate) {
		eSet(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION__LAST_UPDATE_DATE, newLastUpdateDate);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public CodeSystem getCodeSystem() {
		return (CodeSystem) this.eContainer();
	}

} //CodeSystemVersionImpl
