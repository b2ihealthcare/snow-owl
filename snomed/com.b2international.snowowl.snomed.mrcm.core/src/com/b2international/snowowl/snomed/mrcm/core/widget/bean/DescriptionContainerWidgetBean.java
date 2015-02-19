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

import com.b2international.snowowl.snomed.mrcm.core.widget.model.DescriptionContainerWidgetModel;

/**
 * Represents the backing bean encapsulating information for all descriptions of a concept.
 * 
 */
public class DescriptionContainerWidgetBean extends ContainerWidgetBean {

	private static final long serialVersionUID = 8170944594752804449L;
	
	private String languageRefSetId;

	/**
	 * Default constructor for serialization.
	 */
	protected DescriptionContainerWidgetBean() {
		super();
	}

	/**
	 * 
	 * @param model
	 * @param parent
	 * @param languageRefSetId
	 */
	public DescriptionContainerWidgetBean(final DescriptionContainerWidgetModel model, final ConceptWidgetBean parent, final String languageRefSetId) {
		super(model, parent);
		this.languageRefSetId = languageRefSetId;
	}

	public String getLanguageRefSetId() {
		return languageRefSetId;
	}
	
	public void setLanguageRefSetId(final String languageRefSetId) {
		this.languageRefSetId = languageRefSetId;
	}
	
	@Override
	public String toString() {
		return String.format("DescriptionContainerWidgetBean [languageRefSetId=%s]", languageRefSetId);
	}
}