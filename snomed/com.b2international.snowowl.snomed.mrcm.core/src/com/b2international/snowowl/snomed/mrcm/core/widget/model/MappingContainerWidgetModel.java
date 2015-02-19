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
package com.b2international.snowowl.snomed.mrcm.core.widget.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.List;

import com.google.common.collect.Iterables;

/**
 */
public class MappingContainerWidgetModel extends ContainerWidgetModel {

	private static final long serialVersionUID = 8173280746565102254L;

	public MappingContainerWidgetModel(List<? extends MappingWidgetModel> children) {
		this("Mapping elements", children);
	}
	
	/**
	 * Creates a new infrastructure mapping container with the given mapping widget models.
	 * 
	 * @param children the list of contained mapping models (may not be {@code null}; elements may not be {@code null})
	 */
	public MappingContainerWidgetModel(final String label, final List<? extends MappingWidgetModel> children) {
		super(label, children);
	}
	
	public MappingWidgetModel getFirstMatching(final String targetComponentId) {
		checkNotNull(targetComponentId, "targetComponentId");
		
		for (final MappingWidgetModel candidate : Iterables.filter(getChildren(), MappingWidgetModel.class)) {
			if (candidate.matches(targetComponentId)) {
				return candidate;
			}
		}
		
		throw new IllegalStateException(MessageFormat.format("No matching mapping model found for label ''{0}''.", 
				targetComponentId));
	}

}