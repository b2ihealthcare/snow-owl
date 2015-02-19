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
package com.b2international.snowowl.emf.compare.diff;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.Change;

/**
 * Basic {@link Diff} implementation.
 *
 */
public class DiffImpl<F extends EStructuralFeature, V> implements Diff<F, V> {

	private final F feature;
	private final V value;
	private final EObject target;
	private final Change change;
	
	public DiffImpl(final F feature, @Nullable final V value, final EObject target, final Change change) {
		this.feature = checkNotNull(feature, "feature");
		this.value = value;
		this.target = checkNotNull(target, "target");
		this.change = checkNotNull(change, "change");
	}
	
	@Override
	public F getChangedFeature() {
		return feature;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.emf.compare.diff.Diff#getValue()
	 */
	@Override
	@Nullable public V getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.emf.compare.diff.Diff#getTarget()
	 */
	@Override
	public EObject getTarget() {
		return target;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.emf.compare.diff.Diff#getChange()
	 */
	@Override
	public Change getChange() {
		return change;
	}

}