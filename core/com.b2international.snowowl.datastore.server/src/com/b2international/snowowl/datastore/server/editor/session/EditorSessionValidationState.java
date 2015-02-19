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
package com.b2international.snowowl.datastore.server.editor.session;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.datastore.validation.BeanValidationStatus;
import com.b2international.snowowl.datastore.validation.SessionValidationResults;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;

/**
 * Generic editor session validation state object.
 * 
 */
public class EditorSessionValidationState {

	private ListMultimap<String, BeanValidationStatus> beanIdToValidationStatusMultimap = ArrayListMultimap.create();
	
	public SessionValidationResults getValidationResults() {
		synchronized (beanIdToValidationStatusMultimap) {
			return new SessionValidationResults(beanIdToValidationStatusMultimap);
		}
	}

	private <S extends IStatus> List<S> flattenValidationStatus(S status) {
		ImmutableList.Builder<S> builder = ImmutableList.builder();
		if (status.isMultiStatus() && status.getChildren().length > 0) {
			S[] children = (S[]) status.getChildren();
			for (S childStatus : children) {
				builder.addAll(flattenValidationStatus(childStatus));
			}
		} else {
			builder.add(status);
		}
		return builder.build();
	}
	
	public void updateValidationResults(List<BeanValidationStatus> validationResults) {
		List<BeanValidationStatus> flattenedValidationResults = Lists.newArrayList();
		for (BeanValidationStatus validationStatus : validationResults) {
			flattenedValidationResults.addAll(flattenValidationStatus(validationStatus));
		}
		ImmutableListMultimap<String, BeanValidationStatus> newBeanIdToValidationStatusMultimap = Multimaps.index(flattenedValidationResults,
				new Function<BeanValidationStatus, String>() {
					@Override
					public String apply(BeanValidationStatus input) {
						return input.getBeanId();
					}
				});
		synchronized (beanIdToValidationStatusMultimap) {
			for (String beanId : newBeanIdToValidationStatusMultimap.keySet()) {
				ImmutableList<BeanValidationStatus> beanValidationResults = newBeanIdToValidationStatusMultimap.get(beanId);
				// discard OK validation results to reduce network traffic when getting validation results from the client
				Collection<BeanValidationStatus> notOkValidationResults = Collections2.filter(beanValidationResults, new Predicate<BeanValidationStatus>() {
					@Override
					public boolean apply(BeanValidationStatus input) {
						return !input.isOK();
					}
				});
				if (notOkValidationResults.isEmpty()) {
					beanIdToValidationStatusMultimap.removeAll(beanId);
				} else {
					beanIdToValidationStatusMultimap.replaceValues(beanId, notOkValidationResults);
				}
			}
		}
	}

	public void removeValidationResults(Set<String> removedBeanIds) {
		synchronized (beanIdToValidationStatusMultimap) {
			for (String beanId : removedBeanIds) {			
				beanIdToValidationStatusMultimap.removeAll(beanId);
			}
		}
	}
}