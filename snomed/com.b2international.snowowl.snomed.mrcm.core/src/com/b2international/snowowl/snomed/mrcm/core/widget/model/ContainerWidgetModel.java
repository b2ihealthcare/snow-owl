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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.b2international.commons.StringUtils;
import com.google.common.collect.ImmutableList;

/**
 * A widget model that collects other widget models, and carries a descriptive label. The label may also be used as a
 * format string in widget beans or the UI.
 * 
 */
public abstract class ContainerWidgetModel extends WidgetModel {

	private static final long serialVersionUID = -7502785738640245751L;

	private String label;
	private List<WidgetModel> children;
	
	/**
	 * Default constructor for serialization.
	 */
	protected ContainerWidgetModel() {
	}
	
	/**
	 * Creates a new infrastructure container of cardinality 1..1 with the given list of contained models and label.
	 * 
	 * @param label the container label (may not be {@code null} or empty)
	 * @param children the list of contained models (may not be {@code null}; elements may not be {@code null})
	 */
	protected ContainerWidgetModel(final String label, final List<? extends WidgetModel> children) {
		super(LowerBound.REQUIRED, UpperBound.SINGLE, ModelType.INFRASTRUCTURE);
		checkArgument(!StringUtils.isEmpty(label), "label is null or empty.");
		this.label = label;
		this.children = ImmutableList.copyOf(checkNotNull(children, "children"));
	}

	public List<WidgetModel> getChildren() {
		return children;
	}
	
	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		
		return String.format(
				"ContainerWidgetModel [\n"
				+ "        label=%s,\n" 
				+ "        children=%s\n" 
				+ "    ]",
				label, StringUtils.toString(children));
	}
}