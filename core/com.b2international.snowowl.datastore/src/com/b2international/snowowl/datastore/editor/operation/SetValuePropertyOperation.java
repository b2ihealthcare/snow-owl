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
package com.b2international.snowowl.datastore.editor.operation;

import com.b2international.commons.beans.BeanPropertyChangeSupporter;

/**
 * Generic {@link AbstractOperation operation} to set a property to a
 * specified value.
 * 
 * @since 2.8
 */
public class SetValuePropertyOperation extends AbstractOperation {

	private static final long serialVersionUID = 8030756963298404065L;
	
	private final BeanPropertyChangeSupporter bean;
	private final String property;
	private final Object value;

	public SetValuePropertyOperation(BeanPropertyChangeSupporter bean, String property, Object value) {
		this.bean = bean;
		this.property = property;
		this.value = value;
	}
	
	public BeanPropertyChangeSupporter getBean() {
		return bean;
	}

	public String getProperty() {
		return property;
	}

	public Object getValue() {
		return value;
	}
	
}