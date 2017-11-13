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
package com.b2international.snowowl.snomed.core.mrcm;

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.tree.TreeVisitor;
import com.b2international.commons.tree.emf.EObjectTreeNode;
import com.b2international.commons.tree.emf.EObjectWalker;
import com.b2international.commons.tree.emf.EObjectWalker.EObjectContainmentTreeNodeProvider;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * Validator for {@link ConceptModel} instances.
 *
 */
public enum ConceptModelEcoreValidator {

	/**Shared instance.*/
	INSTANCE;

	/**
	 * Method for checking all containment feature of the given {@link ConceptModel}.
	 * If any of the value of a {@link EStructuralFeature} is {@code null}
	 * this will be reported as invalid model. 
	 * @param model model to validate.
	 * @return status indicating the outcome of the validation process.
	 */
	public IStatus validate(final ConceptModel model) {

		if (!CDOUtils.checkObject(model)) {
			return createErrorStatus("Invalid model state.");
		}
		
		final EObjectContainmentTreeNodeProvider childProvider = new EObjectContainmentTreeNodeProvider();
		
		final ConceptModelVisitor visitor = new ConceptModelVisitor();
		final boolean treeValid = new EObjectWalker(visitor, childProvider).walk(model);
		
		if (!treeValid) {
			return createErrorStatus("Concept model contains validation errors.");
		}
		
		return Status.OK_STATUS;
	}
	
	/*creates a status instance with error severity and the given message.*/
	private static IStatus createErrorStatus(final String message) {
		return new Status(IStatus.ERROR, SnomedDatastoreActivator.PLUGIN_ID, Strings.nullToEmpty(message));
	}
	
	private static final class ConceptModelVisitor implements TreeVisitor<EObjectTreeNode> {
		
		private static final Set<EStructuralFeature> ALLOWED_EMPTY_FEATURES = ImmutableSet.<EStructuralFeature>of(
				// Characteristic type ID can be omitted from relationship predicate (defaults to all children of Concepts.CHARACTERISTIC_TYPE)
				MrcmPackage.Literals.RELATIONSHIP_PREDICATE__CHARACTERISTIC_TYPE_CONCEPT_ID,
				// Constraint validation message is optional				
				MrcmPackage.Literals.CONSTRAINT_BASE__VALIDATION_MESSAGE,
				// Constraint descriptions are also optional
				MrcmPackage.Literals.CONSTRAINT_BASE__DESCRIPTION,
				// Predicate labels are derived from their camelCased name when the stored value is empty
				MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__LABEL);

		@Override public boolean visit(final EObjectTreeNode node) {
			return checkNode(node);
		}
		
		@Override public boolean entering(final EObjectTreeNode node) {
			return true;
		}
		
		@Override public boolean leaving(final EObjectTreeNode node) {
			return true;
		}
		
		private boolean checkNode(final EObjectTreeNode node) {
			boolean valid = nodeNotNull(node);
			valid = valid && referencedEObjectNotNull(node);
			valid = valid && valueNotEmptyWithExceptions(node); 
			return valid;
		}

		private boolean nodeNotNull(final EObjectTreeNode node) {
			return null != node;
		}

		private boolean referencedEObjectNotNull(final EObjectTreeNode node) {
			return null != node.getEObject();
		}
		
		private boolean valueNotEmptyWithExceptions(final EObjectTreeNode node) {

			if (null == node.getFeature()) {
				// Not visiting a feature (yet)
				return true;
			}
			
			final Object featureValue = node.getFeatureValue();
			
			if (null != featureValue) {
				
				if (!(featureValue instanceof String)) {
					// Non-null non-String values are OK
					return true;
				}
				
				if (!CompareUtils.isEmpty(featureValue)) {
					// Not empty Strings are also OK
					return true;
				}
			}
			
			if (ALLOWED_EMPTY_FEATURES.contains(node.getFeature())) {
				// Null or empty values for certain features are also OK
				return true;
			}
			
			// Everything else is not OK
			return false;
		}
	}
}