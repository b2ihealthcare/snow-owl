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
package com.b2international.snowowl.datastore.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

public abstract class EMFUtil2 {

	// Prevent instantiation
	private EMFUtil2() { }
	
	public static void collectDependencies(final EPackage ePackage, final Set<EPackage> dependencies) {
		
		final Resource eResource = ePackage.eResource();
		final Collection<EObject> crossReferencedElements = EcoreUtil.ExternalCrossReferencer.find(eResource).keySet();
		
		for (final Object crossReferencedElement : crossReferencedElements) {
			
			if (!(crossReferencedElement instanceof EClassifier)) {
				continue;
			}
			
			final EClassifier eClass = (EClassifier) crossReferencedElement;
			final EPackage referencedPackage = eClass.getEPackage();
			
			if (referencedPackage == null) {
				continue;
			}

			final EPackage topPackage = EMFUtil.getTopLevelPackage(referencedPackage);
			
			if (dependencies.add(topPackage)) {
				collectDependencies(topPackage, dependencies);
			}
		}
	}
	
	/**
	 * Returns the XMI representation of the passed-in eObject.
	 * @param eObject
	 * @return the XMI representation of the eObject
	 * @throws IOException 
	 */
	public static String getXMIString(EObject eObject) throws IOException {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Resource resource = new XMLResourceImpl();
		resource.getContents().add(EcoreUtil.copy(eObject));
		resource.save(os, null);
		return new String(os.toByteArray(), StandardCharsets.UTF_8);
	}
	
	
}