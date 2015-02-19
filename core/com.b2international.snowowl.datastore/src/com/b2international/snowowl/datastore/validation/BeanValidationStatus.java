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
package com.b2international.snowowl.datastore.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

import com.b2international.commons.status.SerializableMultiStatus;
import com.google.common.base.Optional;

/**
 * {@link IStatus} implementation to be used by the {@link IBeanValidationService bean validation service}.
 * 
 */
public class BeanValidationStatus extends SerializableMultiStatus {

	private static final long serialVersionUID = -8246789038909708358L;
	
	private final String beanId;
	private final String beanTypeName;
	private final Optional<String> propertyName;

	/**
	 * Creates a status instance with {@link IStatus#OK} severity with the specified bean ID.
	 * 
	 * @param beanId the unique identifier of the validated bean
	 * @param beanTypeName TODO
	 * @return a status instance with OK severity
	 */
	public static BeanValidationStatus createOkStatus(String beanId, String beanTypeName) {
		return new BeanValidationStatus(IStatus.OK, beanId, beanTypeName, "unknown", IStatus.OK, null, null);
	}
	
	public BeanValidationStatus(String beanId, String beanTypeName, MultiStatus multiStatus) {
		super(multiStatus);
		this.beanId = beanId;
		this.beanTypeName = beanTypeName;
		this.propertyName = Optional.absent();
	}

	public BeanValidationStatus(String beanId, String beanTypeName, String pluginId, int code, IStatus[] newChildren, String message, Throwable exception) {
		super(pluginId, code, newChildren, message, exception);
		this.beanId = beanId;
		this.beanTypeName = beanTypeName;
		this.propertyName = Optional.absent();
	}

	public BeanValidationStatus(int severity, String beanId, String beanTypeName, String pluginId, int code, String message, Throwable exception) {
		super(severity, pluginId, code, message, exception);
		this.beanId = beanId;
		this.beanTypeName = beanTypeName;
		this.propertyName = Optional.absent();
	}
	
	public BeanValidationStatus(int severity, String beanId, String beanTypeName, String propertyName, String pluginId, int code, String message, Throwable exception) {
		super(severity, pluginId, code, message, exception);
		this.beanId = beanId;
		this.propertyName = Optional.of(propertyName);
		this.beanTypeName = beanTypeName;
	}
	
	/**
	 * @return the unique identifier of the validated bean
	 */
	public String getBeanId() {
		return beanId;
	}

	/**
	 * @return the name of the validated bean's type
	 */
	public String getBeanTypeName() {
		return beanTypeName;
	}
	
	/**
	 * @return the property name wrapped into an {@link Optional}
	 */
	public Optional<String> getPropertyName() {
		return propertyName;
	}
}