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

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.datastore.editor.bean.IdentifiedBean;
import com.b2international.snowowl.datastore.validation.BeanValidationStatus;
import com.b2international.snowowl.datastore.validation.IContextValidationConstraint;
import com.b2international.snowowl.datastore.validation.IContextValidationConstraintProvider;
import com.b2international.snowowl.datastore.validation.IContextValidationService;
import com.b2international.snowowl.datastore.validation.IValidationContext;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Generic context validation service implementation.
 * 
 */
public class ContextValidationService implements IContextValidationService {

	private final IContextValidationConstraintProvider contextValidationConstraintProvider;

	public ContextValidationService(IContextValidationConstraintProvider contextValidationConstraintProvider) {
		this.contextValidationConstraintProvider = checkNotNull(contextValidationConstraintProvider, "Bean validation constraint provider must not be null.");
	}
	
	@Override
	public List<BeanValidationStatus> validate(final IValidationContext<?> context, String terminologyComponentId, IProgressMonitor monitor) {
		checkNotNull(context, "Context must not be null.");
		checkNotNull(terminologyComponentId, "Terminology component ID must not be null.");
		checkNotNull(monitor, "Progress monitor must not be null.");
		List<IContextValidationConstraint> constraints = contextValidationConstraintProvider.getConstraints(terminologyComponentId);
		SubMonitor subMonitor = SubMonitor.convert(monitor, constraints.size());
		List<List<BeanValidationStatus>> statusLists = Lists.transform(constraints, new Function<IContextValidationConstraint, List<BeanValidationStatus>>() {
			@Override public List<BeanValidationStatus> apply(IContextValidationConstraint input) {
				return input.validate(context);
			}
		});
		List<BeanValidationStatus> statuses = processValidationResultLists(statusLists, subMonitor);
		return statuses;
	}

	@Override
	public List<BeanValidationStatus> validate(final IValidationContext<?> context, final IdentifiedBean bean, IProgressMonitor monitor) {
		checkNotNull(context, "Context must not be null.");
		checkNotNull(bean, "Bean must not be null.");
		checkNotNull(monitor, "Progress monitor must not be null.");
		List<IContextValidationConstraint> constraints = contextValidationConstraintProvider.getConstraints(bean);
		SubMonitor subMonitor = SubMonitor.convert(monitor, constraints.size());
		List<List<BeanValidationStatus>> statusLists = Lists.transform(constraints, new Function<IContextValidationConstraint, List<BeanValidationStatus>>() {
			@Override public List<BeanValidationStatus> apply(IContextValidationConstraint input) {
				return input.validate(bean, context);
			}
		});
		List<BeanValidationStatus> statuses = processValidationResultLists(statusLists, subMonitor);
		return statuses;
	}
	
	private List<BeanValidationStatus> processValidationResultLists(List<List<BeanValidationStatus>> statusLists, IProgressMonitor monitor) {
		List<BeanValidationStatus> statuses = Lists.newArrayList();
		for (List<BeanValidationStatus> statusList : statusLists) {
			if (monitor.isCanceled()) {
				return Collections.emptyList();
			}
			statuses.addAll(statusList);
			monitor.worked(1);
		}
		return statuses;
	}
}