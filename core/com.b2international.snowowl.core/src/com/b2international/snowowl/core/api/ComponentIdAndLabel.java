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
package com.b2international.snowowl.core.api;

import java.io.Serializable;

/**
 * Minimalistic representation of any Snow Owl terminology artefact.
 * Used primarily for logging purposes.
 * 
 */
public class ComponentIdAndLabel implements Serializable {
	
	private static final long serialVersionUID = 7296886932023811136L;

	//the generic label of the component
	private String label;
	
	//the generic id of the component
	private String id;

	public ComponentIdAndLabel(String label, String id) {
		this.label = label;
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "ComponentIdAndLabel [label=" + label + ", id=" + id + "]";
	}
}