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
package com.b2international.snowowl.snomed.datastore;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.annotations.Client;
import com.b2international.snowowl.core.api.browser.IPredicateBrowser;
import com.b2international.snowowl.datastore.browser.AbstractClientPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.google.common.base.Function;

/**
 * MRMC rule based attribute constraint browser service for the SNOMED&nbsp;CT ontology.
 * @see AbstractClientPredicateBrowser
 */
@Client
public class SnomedClientPredicateBrowser extends AbstractClientPredicateBrowser<PredicateIndexEntry> {

	/**
	 * Function for converting {@link AttributeConstraint attribute constraint} into the bottom most predicate UUIDs.
	 */
	public static final Function<AttributeConstraint, String> ATTRIBUTE_CONSTRAINT_TO_UUID_FUNCTION = new Function<AttributeConstraint, String>() {
		@Override public String apply(final AttributeConstraint component) {
			return getBottomMostPredicateUuid(component);
		}

		/*method for returning the UUID of the bottom most predicate associated to the concrete concept model component*/
		private String getBottomMostPredicateUuid(ConceptModelComponent component) {
			if (component instanceof AttributeConstraint) {
				component = ((AttributeConstraint) component).getPredicate();
			}
			if (component instanceof CardinalityPredicate) {
				final CardinalityPredicate cardinalityPredicate = (CardinalityPredicate) component;
				return getBottomMostPredicateUuid(cardinalityPredicate.getPredicate());
			} else {
				return component.getUuid(); 
			}
		} 
	};

	/**
	 * Creates a new service instance for the client side
	 * @param delegateBrowser the server side predicate browser service.
	 */
	public SnomedClientPredicateBrowser(final IPredicateBrowser<PredicateIndexEntry> delegateBrowser) {
		super(delegateBrowser);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.BranchPathAwareService#getEPackage()
	 */
	@Override
	protected EPackage getEPackage() {
		return MrcmPackage.eINSTANCE;
	}
}