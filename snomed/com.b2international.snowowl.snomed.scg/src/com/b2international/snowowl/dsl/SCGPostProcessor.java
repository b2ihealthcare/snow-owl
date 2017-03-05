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
package com.b2international.snowowl.dsl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.xtext.GeneratedMetamodel;

import com.google.common.collect.FluentIterable;

/**
 * Simple PostProcessor class. Changes can be applied to the Ecore model, that
 * is inferred from the grammar.
 * 
 * 
 */
public class SCGPostProcessor {

	public static final String CONCEPT_ECLASS_NAME = "Concept";
	
	public static void process(GeneratedMetamodel generatedMetamodel) {
		process(generatedMetamodel.getEPackage());
	}

	public static void process(EPackage ePackage) {
		for (EClass eClass : FluentIterable.from(ePackage.getEClassifiers()).filter(EClass.class)) {
			if (CONCEPT_ECLASS_NAME.equals(eClass.getName())) {
				processConcept(eClass);
			}
		}
	}

	public static void processConcept(EClass eClass) {
		eClass.getEStructuralFeatures().add(createSuperTypeAttribute());
	}
	
	private static EAttribute createSuperTypeAttribute() {
		EAttribute superTypesAttribute = EcoreFactory.eINSTANCE.createEAttribute();
		superTypesAttribute.setEType(getEClassifier("ELong"));
		superTypesAttribute.setLowerBound(0);
		superTypesAttribute.setUpperBound(-1);
		superTypesAttribute.setName("superTypes");
		return superTypesAttribute;
	}

	public static EClassifier getEClassifier(String name) {
		return EcorePackage.eINSTANCE.getEClassifier(name);
	}
	
}