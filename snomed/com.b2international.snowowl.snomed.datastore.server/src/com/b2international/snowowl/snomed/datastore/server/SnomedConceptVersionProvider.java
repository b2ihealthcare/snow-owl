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
package com.b2international.snowowl.snomed.datastore.server;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.terminologyregistry.core.util.CodeSystemVersionProvider;
import com.google.common.collect.ImmutableSet;

/**
 * @since 3.1.0
 */
public class SnomedConceptVersionProvider extends CodeSystemVersionProvider {

	/* Features where CDO changes may result in a new concept version */
	private static final Set<EStructuralFeature> RELEVANT_FEATURE_SET = ImmutableSet.<EStructuralFeature> of(
			SnomedPackage.Literals.COMPONENT__ACTIVE, 
			SnomedPackage.Literals.COMPONENT__MODULE,
			SnomedPackage.Literals.CONCEPT__DEFINITION_STATUS);

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}

	@Override
	protected SnomedConceptLookupService getLookupService() {
		return new SnomedConceptLookupService();
	}

	@Override
	protected boolean isHeadChanged(CDOObject headVersion, CDOObject specifiedVersion) {
		if (super.isHeadChanged(headVersion, specifiedVersion)) {
			// examine CDO changes
			CDORevision headRevision = headVersion.cdoRevision();
			CDORevision specifiedRevision = specifiedVersion.cdoRevision();
			CDOUtils.resolveElementProxies(headVersion);
			CDOUtils.resolveElementProxies(specifiedVersion);
			CDORevisionDelta delta = headRevision.compare(specifiedRevision);
			return hasRelevantChange(delta);
		} else {
			return false;
		}
	}

	private boolean hasRelevantChange(CDORevisionDelta delta) {
		List<CDOFeatureDelta> deltas = delta.getFeatureDeltas();
		for (CDOFeatureDelta featureDelta : deltas) {
			if (RELEVANT_FEATURE_SET.contains(featureDelta.getFeature())) {
				return true;
			}
		}
		return false;
	}
}
