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
package com.b2international.snowowl.datastore.editor.service;

import java.text.MessageFormat;

/**
 * @since 3.0.1
 */
public class ComponentNotFoundException extends EditorSessionInitializationException {

	private static final long serialVersionUID = 1L;
	private static final String messagePattern = "{0} not found by ID {1}.";
	
	private final String componentType;
	private final String componentId;

	public ComponentNotFoundException(String componentType, String componentId) {
		super(MessageFormat.format(messagePattern, componentType, componentId));
		this.componentType = componentType;
		this.componentId = componentId;
	}
	
	public String getComponentType() {
		return componentType;
	}
	
	public String getComponentId() {
		return componentId;
	}

}