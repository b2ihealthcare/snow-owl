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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

/**
 * Represents the results of validating an {@link EditorSession}.
 * 
 */
public class SessionValidationResults implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final ListMultimap<String, BeanValidationStatus> beanIdToValidationStatusMultimap;
	private final ListMultimap<String, BeanValidationStatus> beanTypeNameToValidationStatusMultimap;
	
	public SessionValidationResults(ListMultimap<String, BeanValidationStatus> beanIdToValidationStatusMultimap) {
		this.beanIdToValidationStatusMultimap = ImmutableListMultimap.copyOf(beanIdToValidationStatusMultimap);
		this.beanTypeNameToValidationStatusMultimap = Multimaps.index(beanIdToValidationStatusMultimap.values(),
				new Function<BeanValidationStatus, String>() {
					@Override
					public String apply(BeanValidationStatus input) {
						return input.getBeanTypeName();
					}
				});
	}

	public List<BeanValidationStatus> getValidationResultsByBeanId(String beanId) {
		return beanIdToValidationStatusMultimap.get(beanId);
	}
	
	public List<BeanValidationStatus> getValidationResultsByBeanTypeName(String beanTypeName) {
		return beanTypeNameToValidationStatusMultimap.get(beanTypeName);
	}

	public boolean isValid() {
		Collection<BeanValidationStatus> statuses = beanIdToValidationStatusMultimap.values();
		for (BeanValidationStatus status : statuses) {
			if (status.getSeverity() == IStatus.ERROR) {
				return false;
			}
		}
		return true;
	}
	
}