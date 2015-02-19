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
package com.b2international.snowowl.datastore.server.validation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.datastore.editor.bean.IdentifiedBean;
import com.b2international.snowowl.datastore.validation.BeanValidationStatus;
import com.b2international.snowowl.datastore.validation.IBeanValidationConstraint;
import com.b2international.snowowl.datastore.validation.IBeanValidationConstraintProvider;
import com.b2international.snowowl.datastore.validation.IBeanValidationService;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Generic bean validation service implementation.
 * 
 */
public class BeanValidationService implements IBeanValidationService {

	private final class ValidationFunction implements Function<IBeanValidationConstraint, BeanValidationStatus> {
		private final IdentifiedBean bean;

		private ValidationFunction(IdentifiedBean bean) {
			this.bean = bean;
		}

		@Override public BeanValidationStatus apply(IBeanValidationConstraint input) {
			return input.validate(bean);
		}
	}

	private final IBeanValidationConstraintProvider beanValidationConstraintProvider;

	public BeanValidationService(IBeanValidationConstraintProvider beanValidationConstraintProvider) {
		this.beanValidationConstraintProvider = checkNotNull(beanValidationConstraintProvider, "Bean validation constraint provider must not be null.");
	}
	
	@Override
	public List<BeanValidationStatus> validate(final IdentifiedBean bean, final IProgressMonitor monitor) {
		checkNotNull(bean, "Bean must not be null.");
		checkNotNull(monitor, "Progress monitor must not be null.");
		List<IBeanValidationConstraint> constraints = beanValidationConstraintProvider.getConstraints(bean);
		List<BeanValidationStatus> constraintResults = Lists.transform(constraints, new ValidationFunction(bean));
		return ImmutableList.copyOf(constraintResults);
	}

}