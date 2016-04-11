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

import static com.google.common.base.Preconditions.checkArgument;

import java.text.MessageFormat;
import java.util.List;

import com.b2international.commons.StringUtils;
import com.google.common.collect.Iterables;

/**
 * A container model for description models.
 * 
 */
public class DescriptionContainerWidgetModel extends DataTypeContainerWidgetModel {

	private static final long serialVersionUID = 8381585718884297194L;

	/**
	 * Default constructor for serialization.
	 */
	protected DescriptionContainerWidgetModel() {
	}
	
	/**
	 * Creates a new infrastructure description container with the given description widget models.
	 * 
	 * @param children the list of contained description models (may not be {@code null}; elements may not be
	 * {@code null})
	 */
	public DescriptionContainerWidgetModel(final List<? extends WidgetModel> children) {
		super("Descriptions", children);
	}
	
	/**
	 * Returns the first matching description widget model for the specified description type.
	 * 
	 * @param typeId the description type to look for (may not be {@code null})
	 * @param preferred {@code true} if the description to match is a preferred term, {@code false} otherwise
	 * @return the first matching description widget model which accepts the type
	 * @throws IllegalStateException if no matching model can be found
	 */
	public DescriptionWidgetModel getFirstMatching(final String typeId) {
		
		checkArgument(!StringUtils.isEmpty(typeId), "typeId is null or empty.");
	
		for (final DescriptionWidgetModel candidate : Iterables.filter(getChildren(), DescriptionWidgetModel.class)) {
			if (candidate.matches(typeId)) {
				return candidate;
			}
		}
	
		throw new IllegalStateException(MessageFormat.format("No matching description widget model could be found for type identifier ''{0}''.", 
				typeId));
	}
}