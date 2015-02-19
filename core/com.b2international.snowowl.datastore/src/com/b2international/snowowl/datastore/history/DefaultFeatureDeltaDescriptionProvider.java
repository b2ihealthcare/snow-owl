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
package com.b2international.snowowl.datastore.history;

import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.util.PolymorphicDispatcher;

/**
 * Default implementation of {@link IFeatureDeltaDescriptionProvider}. Returns changed-from-to descriptions.
 * 
 * @since 3.1
 */
public class DefaultFeatureDeltaDescriptionProvider implements IFeatureDeltaDescriptionProvider {

	private final PolymorphicDispatcher<String> provideDescriptionDispatcher = createDescriptionDispatcher();

	protected PolymorphicDispatcher<String> createDescriptionDispatcher() {
		return PolymorphicDispatcher.createForSingleTarget(
				PolymorphicDispatcher.Predicates.forName("_provideDescription", 2), this);
	}

	@Override
	public String provideDescription(final EObject object, final CDOSetFeatureDelta featureDelta) {
		return this.provideDescriptionDispatcher.invoke(object, featureDelta);
	}

	protected String _provideDescription(final EObject object, final CDOSetFeatureDelta featureDelta) {
		return defaultDescription(object, featureDelta);
	}

	protected String defaultDescription(final EObject object, final CDOSetFeatureDelta featureDelta) {
		return defaultDescription(featureDelta.getFeature(), featureDelta.getOldValue(), featureDelta.getValue());
	}

	/**
	 * Returns a default change description.
	 * 
	 * @param featureName
	 * @param from
	 * @param to
	 * @return
	 */
	protected String defaultDescription(final EStructuralFeature feature, final Object from, final Object to) {
		return String.format("Changed '%s' from '%s' to '%s'.", feature.getName(),
				HistoryUtil.unknownOrActual(feature, from), HistoryUtil.unknownOrActual(feature, to));
	}

}