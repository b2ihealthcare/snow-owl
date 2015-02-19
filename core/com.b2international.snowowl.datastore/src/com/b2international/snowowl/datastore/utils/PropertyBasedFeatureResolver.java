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
package com.b2international.snowowl.datastore.utils;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.Pair;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

/**
 * @since 3.1.0
 */
public class PropertyBasedFeatureResolver {

	public Optional<Pair<EObject, EStructuralFeature>> resolve(EObject eObject, String property) {
		if (property.contains(".")) {
			Optional<Pair<EObject, EStructuralFeature>> nestedFeature = getNestedFeature(eObject, property);
			if(nestedFeature.isPresent()){			
				return nestedFeature;
			}
		} else {
			Optional<EStructuralFeature> feature = getFeature(eObject, property);
			if (feature.isPresent()) {
				return Optional.of(new Pair<EObject, EStructuralFeature>(eObject, feature.get()));
			}
		}
		return Optional.absent();
	}

	private Optional<Pair<EObject, EStructuralFeature>> getNestedFeature(EObject eObject, String propertyName) {
		ArrayList<String> properties = Lists.newArrayList(propertyName.split("[.]"));
		EObject parent = eObject;
		Optional<EStructuralFeature> feature = Optional.absent();

		for (Iterator<String> iterator = properties.iterator(); iterator.hasNext();) {
			String property = iterator.next();
			feature = getFeature(parent, property);
			if (feature.isPresent() && iterator.hasNext()) {
				parent = (EObject) parent.eGet(feature.get());
			}
		}

		if (feature.isPresent()) {
			return Optional.of(new Pair<EObject, EStructuralFeature>(parent, feature.get()));
		}

		return Optional.absent();
	}

	private Optional<EStructuralFeature> getFeature(EObject eObject, String propertyName) {
		Builder<? extends EStructuralFeature> listBuilder = ImmutableList.<EStructuralFeature> builder()
				.addAll(eObject.eClass().getEAllAttributes())
				.addAll(eObject.eClass().getEAllReferences());

		ImmutableList<? extends EStructuralFeature> features = listBuilder.build();
		for (EStructuralFeature feature : features) {
			if (feature.getName().equals(propertyName)) {
				return Optional.of(feature);
			}
		}
		return Optional.absent();
	}

}