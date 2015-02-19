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
package com.b2international.snowowl.datastore.server.index;

import java.io.Serializable;
import java.util.Map.Entry;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.ecore.EClass;

/**
 * Represents a pair of CDOID and EClass mutable {@link Entry}.
 */
public class SimpleEntry extends java.util.AbstractMap.SimpleEntry<CDOID, EClass> implements Serializable {
	
	private static final long serialVersionUID = 4996161014302530741L;
	
	public SimpleEntry(final CDOObject object) {
		super(object.cdoID(), object.eClass());
	}
}