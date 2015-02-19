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
package com.b2international.snowowl.snomed.refset.derivation;

import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.core.api.SnowowlServiceException;

/**
 * Derivator class to derive simple type refset to simple type refset.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public class SnomedSimpleTypeRefSetDerivator extends AbstractSnomedRefSetDerivator {

	private final SimpleTypeRefSetDerivationModel derivationModel;

	public SnomedSimpleTypeRefSetDerivator(final String refSetId, final SimpleTypeRefSetDerivationModel derivationModel) {
		super(refSetId, derivationModel.getRefSetName(), false);
		this.derivationModel = derivationModel;
	}

	@Override
	protected int getTotalWork() {
		switch (derivationModel.getDeriveType()) {
		case DESCRIPTION:
			return 4;
		case RELATIONSHIP:
			return 4;
		case BOTH:
			return 6;
		default:
			return 6;
		} 
	}

	@Override
	protected void deriveComponents(final SubMonitor monitor) throws SnowowlServiceException {
		switch (derivationModel.getDeriveType()) {
		case DESCRIPTION:
			deriveDescriptions(monitor);
			break;
		case RELATIONSHIP:
			deriveRelationships(monitor);
			break;
		case BOTH:
			deriveDescriptions(monitor);
			deriveRelationships(monitor);
			break;
		}
	}

}