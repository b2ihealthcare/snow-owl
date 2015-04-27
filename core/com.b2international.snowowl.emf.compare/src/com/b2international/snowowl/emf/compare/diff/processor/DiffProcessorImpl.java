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
package com.b2international.snowowl.emf.compare.diff.processor;

import static java.util.Collections.emptySet;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.emf.NsUriProvider;

/**
 * Basic {@link DiffProcessor difference processor} implementation.
 * @param <E> type of the processed value.
 *
 */
public abstract class DiffProcessorImpl<E> implements DiffProcessor<E> {

	@Override
	public Collection<EStructuralFeature> getExcludedFeatures() {
		return emptySet();
	}
	
	@Override
	public String toString(final Notifier notifier) {
		return String.valueOf(notifier);
	}
	
	@Override
	public NsUriProvider getNsUriProvider() {
		return NsUriProvider.NULL_IMPL;
	}
	
}