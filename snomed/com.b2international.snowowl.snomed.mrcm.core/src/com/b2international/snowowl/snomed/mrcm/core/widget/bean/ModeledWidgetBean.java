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
package com.b2international.snowowl.snomed.mrcm.core.widget.bean;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.b2international.snowowl.snomed.mrcm.core.widget.model.WidgetModel;

/**
 * Represents a backing bean with a corresponding widget model. The model
 * carries the options based on which the user can adjust this bean's state.
 * 
 */
public abstract class ModeledWidgetBean extends WidgetBean implements Serializable {

	private static final long serialVersionUID = -8653341382691169241L;
	
	public static final String PROP_REQUIRED = "required";
	public static final String PROP_MULTIPLE = "multiple";
	
	private WidgetModel model;
	
	/**
	 * Default constructor for serialization.
	 */
	protected ModeledWidgetBean() {
		super();
	}
	
	protected ModeledWidgetBean(final WidgetModel model) {
		this.model = checkNotNull(model, "model");
	}

	public WidgetModel getModel() {
		return model;
	}

	public boolean isRequired() {
		return model.isRequired();
	}

	public boolean isMultiple() {
		return model.isMultiple();
	}
	
	public boolean isUnsanctioned() {
		return model.isUnsanctioned();
	}
	
	public boolean isInfrastructure() {
		return model.isInfrastructure();
	}
	
	public abstract ConceptWidgetBean getConcept();
}