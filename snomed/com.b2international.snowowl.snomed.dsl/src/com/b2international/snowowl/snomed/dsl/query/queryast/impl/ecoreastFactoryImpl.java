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
package com.b2international.snowowl.snomed.dsl.query.queryast.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import com.b2international.snowowl.snomed.dsl.query.queryast.AndClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.NotClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.OrClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.RefSet;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastFactory;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ecoreastFactoryImpl extends EFactoryImpl implements ecoreastFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ecoreastFactory init() {
		try {
			ecoreastFactory theecoreastFactory = (ecoreastFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.b2international.com/snowowl/dsl/ast"); 
			if (theecoreastFactory != null) {
				return theecoreastFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ecoreastFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ecoreastFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case ecoreastPackage.CONCEPT_REF: return createConceptRef();
			case ecoreastPackage.REF_SET: return createRefSet();
			case ecoreastPackage.NOT_CLAUSE: return createNotClause();
			case ecoreastPackage.SUB_EXPRESSION: return createSubExpression();
			case ecoreastPackage.AND_CLAUSE: return createAndClause();
			case ecoreastPackage.OR_CLAUSE: return createOrClause();
			case ecoreastPackage.ATTRIBUTE_CLAUSE: return createAttributeClause();
			case ecoreastPackage.ATTRIBUTE_CLAUSE_GROUP: return createAttributeClauseGroup();
			case ecoreastPackage.NUMERIC_DATA_CLAUSE: return createNumericDataClause();
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE: return createNumericDataGroupClause();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case ecoreastPackage.SUBSUMPTION_QUANTIFIER:
				return createSubsumptionQuantifierFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case ecoreastPackage.SUBSUMPTION_QUANTIFIER:
				return convertSubsumptionQuantifierToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConceptRef createConceptRef() {
		ConceptRefImpl conceptRef = new ConceptRefImpl();
		return conceptRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RefSet createRefSet() {
		RefSetImpl refSet = new RefSetImpl();
		return refSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotClause createNotClause() {
		NotClauseImpl notClause = new NotClauseImpl();
		return notClause;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SubExpression createSubExpression() {
		SubExpressionImpl subExpression = new SubExpressionImpl();
		return subExpression;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AndClause createAndClause() {
		AndClauseImpl andClause = new AndClauseImpl();
		return andClause;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OrClause createOrClause() {
		OrClauseImpl orClause = new OrClauseImpl();
		return orClause;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttributeClause createAttributeClause() {
		AttributeClauseImpl attributeClause = new AttributeClauseImpl();
		return attributeClause;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttributeClauseGroup createAttributeClauseGroup() {
		AttributeClauseGroupImpl attributeClauseGroup = new AttributeClauseGroupImpl();
		return attributeClauseGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NumericDataClause createNumericDataClause() {
		NumericDataClauseImpl numericDataClause = new NumericDataClauseImpl();
		return numericDataClause;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NumericDataGroupClause createNumericDataGroupClause() {
		NumericDataGroupClauseImpl numericDataGroupClause = new NumericDataGroupClauseImpl();
		return numericDataGroupClause;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SubsumptionQuantifier createSubsumptionQuantifierFromString(EDataType eDataType, String initialValue) {
		SubsumptionQuantifier result = SubsumptionQuantifier.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSubsumptionQuantifierToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ecoreastPackage getecoreastPackage() {
		return (ecoreastPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ecoreastPackage getPackage() {
		return ecoreastPackage.eINSTANCE;
	}

} //ecoreastFactoryImpl