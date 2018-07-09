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
package com.b2international.snowowl.snomed.core.mrcm.io;

import static com.b2international.snowowl.snomed.core.mrcm.ConceptModelUtils.getContainerConstraint;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.tree.NoopTreeVisitor;
import com.b2international.commons.tree.emf.EObjectTreeNode;
import com.b2international.commons.tree.emf.EObjectWalker;
import com.b2international.commons.tree.emf.EObjectWalker.EObjectContainmentTreeNodeProvider;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.core.mrcm.ConceptModelUtils;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConstraintBase;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @since 4.3
 */
public class ConceptModelSemanticValidator {

	private SnomedConceptLookupService lookupService;

	public ConceptModelSemanticValidator(SnomedConceptLookupService lookupService) {
		this.lookupService = lookupService;
	}
	
	public Collection<ConstraintBase> validate(final IBranchPath branchPath, ConceptModel model) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(model, "model");
		
		final Builder<ConstraintBase> invalidConstraints = ImmutableSet.builder();
		new EObjectWalker(new NoopTreeVisitor<EObjectTreeNode>() {
			@Override
			protected void doVisit(EObjectTreeNode node) {
				if (node != null && ConceptModelUtils.CONCEPT_ID_FEATURES.contains(node.getFeature()) && !exists(branchPath, node.getFeatureValue())) {
					final EObject obj = node.getEObject();
					if (obj instanceof ConceptModelPredicate) {
						invalidConstraints.add(getContainerConstraint((ConceptModelPredicate) obj));
					} else if (obj instanceof ConceptSetDefinition) {
						invalidConstraints.add(getContainerConstraint((ConceptSetDefinition) obj));
					}
				}
			}

		}, new EObjectContainmentTreeNodeProvider()).walk(model);
	
		return invalidConstraints.build();
	}
	
	private boolean exists(IBranchPath branchPath, Object featureValue) {
		if (featureValue instanceof String) {
			final String value = (String) featureValue;
			
			if (value.isEmpty()) {
				return true;
			}
			
			if (lookupService.exists(branchPath, value)) {
				return true;
			}
			
		}

		// XXX: feature values can be optional (eg. relationship predicate, characteristic type)
		return featureValue == null;
	}
}
