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

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * Contains utility methods providing low-level access to database contents. 
 *
 */
public abstract class LocalDbUtils {

	/**
	 * Returns the table name for the given {@link EObject} instance.
	 * @param eObject
	 * @return
	 */
	public static String tableNameFor(EObject eObject) {
		return tableNameFor(eObject instanceof EClass ? (EClass) eObject : eObject.eClass());
	}
	
	/**
	 * Returns the table name for the given {@link EClass} instance.
	 * @return
	 */
	public static String tableNameFor(EClass eClass) {
		return defaultTableName(eClass);
	}
	
	private static String defaultTableName(EClass eClass) {
		return EMFUtil.getQualifiedName(eClass, "_");
	}

	
	private LocalDbUtils() {
		// Prevent instantiation
	}
}