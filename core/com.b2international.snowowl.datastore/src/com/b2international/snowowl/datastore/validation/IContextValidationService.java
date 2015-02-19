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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.datastore.editor.bean.IdentifiedBean;

/**
 * Generic {@link IValidationContext context} validation service interface.
 * 
 */
public interface IContextValidationService {

	/**
	 * Validates the contents of the specified context and returns the result.
	 * 
	 * @param context the context to validate
	 * @param terminologyComponentId the terminology component ID
	 * @param monitor the progress monitor
	 * @return the validation results
	 */
	List<BeanValidationStatus> validate(IValidationContext<?> context, String terminologyComponentId, IProgressMonitor monitor);
	
	/**
	 * Validates the bean against the specified context and returns the result.
	 * 
	 * @param context the context to validate against
	 * @param bean the bean to validate
	 * @param monitor the progress monitor
	 * @return the validation results
	 */
	List<BeanValidationStatus> validate(IValidationContext<?> context, IdentifiedBean bean, IProgressMonitor monitor);

}