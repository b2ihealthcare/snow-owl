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
package com.b2international.snowowl.snomed.dsl.query.queryast.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.snomed.dsl.query.queryast.AndClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup;
import com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.NotClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.OrClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.RefSet;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression;
import com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage
 * @generated
 */
public class ecoreastAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ecoreastPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ecoreastAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = ecoreastPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ecoreastSwitch<Adapter> modelSwitch =
		new ecoreastSwitch<Adapter>() {
			@Override
			public Adapter caseRValue(RValue object) {
				return createRValueAdapter();
			}
			@Override
			public Adapter caseUnaryRValue(UnaryRValue object) {
				return createUnaryRValueAdapter();
			}
			@Override
			public Adapter caseBinaryRValue(BinaryRValue object) {
				return createBinaryRValueAdapter();
			}
			@Override
			public Adapter caseConceptRef(ConceptRef object) {
				return createConceptRefAdapter();
			}
			@Override
			public Adapter caseRefSet(RefSet object) {
				return createRefSetAdapter();
			}
			@Override
			public Adapter caseNotClause(NotClause object) {
				return createNotClauseAdapter();
			}
			@Override
			public Adapter caseSubExpression(SubExpression object) {
				return createSubExpressionAdapter();
			}
			@Override
			public Adapter caseAndClause(AndClause object) {
				return createAndClauseAdapter();
			}
			@Override
			public Adapter caseOrClause(OrClause object) {
				return createOrClauseAdapter();
			}
			@Override
			public Adapter caseAttributeClause(AttributeClause object) {
				return createAttributeClauseAdapter();
			}
			@Override
			public Adapter caseAttributeClauseGroup(AttributeClauseGroup object) {
				return createAttributeClauseGroupAdapter();
			}
			@Override
			public Adapter caseNumericDataClause(NumericDataClause object) {
				return createNumericDataClauseAdapter();
			}
			@Override
			public Adapter caseNumericDataGroupClause(NumericDataGroupClause object) {
				return createNumericDataGroupClauseAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.RValue <em>RValue</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.RValue
	 * @generated
	 */
	public Adapter createRValueAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue <em>Unary RValue</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue
	 * @generated
	 */
	public Adapter createUnaryRValueAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue <em>Binary RValue</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue
	 * @generated
	 */
	public Adapter createBinaryRValueAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef <em>Concept Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef
	 * @generated
	 */
	public Adapter createConceptRefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.RefSet <em>Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.RefSet
	 * @generated
	 */
	public Adapter createRefSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NotClause <em>Not Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NotClause
	 * @generated
	 */
	public Adapter createNotClauseAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression <em>Sub Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression
	 * @generated
	 */
	public Adapter createSubExpressionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.AndClause <em>And Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.AndClause
	 * @generated
	 */
	public Adapter createAndClauseAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.OrClause <em>Or Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.OrClause
	 * @generated
	 */
	public Adapter createOrClauseAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause <em>Attribute Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause
	 * @generated
	 */
	public Adapter createAttributeClauseAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup <em>Attribute Clause Group</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup
	 * @generated
	 */
	public Adapter createAttributeClauseGroupAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause <em>Numeric Data Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause
	 * @generated
	 */
	public Adapter createNumericDataClauseAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause <em>Numeric Data Group Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause
	 * @generated
	 */
	public Adapter createNumericDataGroupClauseAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //ecoreastAdapterFactory