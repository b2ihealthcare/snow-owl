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
package com.b2international.snowowl.snomed.datastore;

import java.util.Collection;
import java.util.UUID;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.ecore.EObject;

import com.b2international.collections.longs.LongValueMap;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConstraintForm;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.google.common.collect.Iterables;

/**
 * Context for editing the MRCM rules and constraints with an underlying CDO transaction.
 * @see CDOEditingContext
 */
public class MrcmEditingContext extends BaseSnomedEditingContext {

	/**
	 * Nil implementation of the concept model. Does not contains any SNOMED&nbsp;CT concept attribute constraint.
	 * <br>Does not contain in a CDO view. Has {@link CDOState#TRANSIENT transient} state and does not have CDO related transactionality.
	 */
	public static final ConceptModel NULL_IMPL;
	
	static {
		NULL_IMPL = MrcmFactory.eINSTANCE.createConceptModel();
	}
	
	/**
	 * Returns with the concept model containing the SNOMED&nbsp;CT concept attribute constraints.
	 * If the concept model does not exist creates a new instance. The new concept model instance will be added to the root resource.
	 * @return the concept model.
	 */
	public ConceptModel getOrCreateConceptModel() {
		ConceptModel conceptModel = Iterables.getFirst(Iterables.filter(getContents(), ConceptModel.class), null);
		if (null == conceptModel) {
			conceptModel = createConceptModel();
			add(conceptModel);
		}
		return conceptModel;
	}

	public AttributeConstraint createAttributeConstraint() {
		
		AttributeConstraint attributeConstraint = MrcmFactory.eINSTANCE.createAttributeConstraint();
		
		attributeConstraint.setActive(true);
		attributeConstraint.setUuid(UUID.randomUUID().toString());
		attributeConstraint.setStrength(null);
		attributeConstraint.setForm(ConstraintForm.ALL_FORMS);
		
		return attributeConstraint;
	}
	
	@Override
	protected String getRootResourceName() {
		return SnomedDatastoreActivator.MRCM_ROOT_RESOURCE_NAME;
	}

	/**
	 * Creates a new MRCM editing context on the specified branch of the SNOMED CT repository.
	 * 
	 * @param branchPath the branch path to use
	 */
	public MrcmEditingContext(final IBranchPath branchPath) {
		super(branchPath);
	}

	@Override
	protected <T extends EObject> LongValueMap<String> getStorageKeys(Collection<String> componentIds, Class<T> type) {
		throw new UnsupportedOperationException("Cannot fetch storage keys for " + type);
	}
	
	/*creates and returns with a new concept model instance.*/
	private ConceptModel createConceptModel() {
		return MrcmFactory.eINSTANCE.createConceptModel();
	}
}