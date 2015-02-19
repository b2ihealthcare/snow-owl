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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.Change;

/**
 * {@link SingleValueAttributeDiff Single valued attribute difference} implementation.
 *
 */
public class SingleValueAttributeDiffImpl extends SingleValueDiffImpl<EAttribute, Object> implements SingleValueAttributeDiff {

	public SingleValueAttributeDiffImpl(final EAttribute feature, @Nullable final Object value, 
			final EObject target, final Change change, @Nullable final Object oldValue) {
		
		super(checkNotNull(feature, "feature"), value, checkNotNull(target, "target"), checkNotNull(change, "change"), oldValue);
	}

	

}