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
package com.b2international.snowowl.snomed.api.domain;

import com.b2international.snowowl.api.domain.IComponentNode;
import com.google.common.collect.Multimap;

/**
 * TODO: review documentation
 * 
 * This interface represents a SNOMED CT specific concept with a unique SNOMED CT identifier, 
 * a human readable preferred term, its child and parent concepts. Due to its SNOMED CT specific nature it, also has some additional 
 * SNOMED CT specific meta-data: <i>module</i>, <i>effective time</i>, <i>definition status</i> and <i>status</i> .
 * 
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link IIdentifiedElement#getId() <em>Identifier</em>}</li>
 *   <li>{@link INamedElement#getLabel() <em>Label</em>}</li>
 *   <li>{@link IContainerElement#getChildCount() <em>Child count</em>}</li>
 *   <li>{@link IParentElement#getChildren() <em>Children</em>}</li>
 *   <li>{@link IChildElement#getParents() <em>Parents</em>}</li>
 *   <li>{@link ISnomedElement#getModule() <em>Module</em>}</li>
 *   <li>{@link ISnomedElement#getEffectiveTime() <em>Effective Time</em>}</li>
 *   <li>{@link ISnomedElement#isActive() <em>Status</em>}</li>
 * </ul>
 * </p>
 * 
 */
public interface ISnomedConcept extends ISnomedComponent, IComponentNode {

	/**
	 * TODO document
	 * @return
	 */
	DefinitionStatus getDefinitionStatus();

	/**
	 * TODO document
	 * @return
	 */
	SubclassDefinitionStatus getSubclassDefinitionStatus();

	/**
	 * TODO document
	 * @return
	 */
	InactivationIndicator getInactivationIndicator();

	/**
	 * TODO document
	 * @return
	 */
	Multimap<AssociationType, String> getAssociationTargets();
}