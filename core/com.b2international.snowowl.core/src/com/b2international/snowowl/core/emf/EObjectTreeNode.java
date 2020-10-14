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
package com.b2international.snowowl.core.emf;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * <p>Represents a node in an {@link EObject} instance tree, as traversed by EObjectWalker.</p>
 * 
 * <p>It always contains an {@link EObject}, and optionally contains an
 * {link {@link EStructuralFeature}. The node also contains the path from the root of the EObject tree to this node.</p> 
 * 
 *
 */
public class EObjectTreeNode {

	private EObject eObject;
	private EStructuralFeature feature;
	private String path;
	
	public EObjectTreeNode(String path, EObject eObject, EStructuralFeature feature) {
		this.path = path;
		this.eObject = eObject;
		this.feature = feature;
	}
	
	public EObject getEObject() {
		return eObject;
	}
	
	public EStructuralFeature getFeature() {
		return feature;
	}
	
	public String getPath() {
		return path;
	}
	
	public Object getFeatureValue() {
		if(eObject == null || feature == null) {
			return null;
		} else {
			return eObject.eGet(feature);
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if(eObject != null) {
			buf.append(eObject.eClass().getName());
		}
		if(feature != null) {
			buf.append(".");
			buf.append(feature.getName());
		}
		buf.append(" at ");
		buf.append(path);
		return buf.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eObject == null) ? 0 : eObject.hashCode());
		result = prime * result + ((feature == null) ? 0 : feature.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EObjectTreeNode other = (EObjectTreeNode) obj;
		if (eObject == null) {
			if (other.eObject != null) {
				return false;
			}
		} else if (!eObject.equals(other.eObject)) {
			return false;
		}
		if (feature == null) {
			if (other.feature != null) {
				return false;
			}
		} else if (!feature.equals(other.feature)) {
			return false;
		}
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		return true;
	}
}