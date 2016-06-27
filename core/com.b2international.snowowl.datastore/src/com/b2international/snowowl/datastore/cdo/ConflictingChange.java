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
package com.b2international.snowowl.datastore.cdo;

import static com.b2international.commons.ChangeKind.UPDATED;

import java.io.Serializable;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.Change;

/**
 * Wraps a conflicting change.
 */
public class ConflictingChange implements Serializable {
	
	private static final long serialVersionUID = -628532708438664892L;
	
	private final Change change;
	private final CDOID element;
	private String featureName;
	private String stringValue;
	
	public ConflictingChange(final Change change, final CDOID element) {
		this.change = change;
		this.element = element;
	}
	
	public ConflictingChange(final CDOID element, final EStructuralFeature feature, final Object value) {
		this.change = UPDATED;
		this.element = element;
		this.featureName = feature.getName();
		this.stringValue = String.valueOf(value);
	}

	public Change getAction() {
		return change;
	}
	
	public CDOID getConflictingElement() {
		return element;
	}
	
	public String getFeatureName() {
		return featureName;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	@Override
	public String toString() {
		if (featureName == null) {
			return String.valueOf(change) + " " + element;
		} else {
			return String.valueOf(change) + " feature " + featureName + " of element " + element + " to " + stringValue;
		}
	}
}