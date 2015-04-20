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

import java.io.Serializable;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupWidgetModel;

/**
 * Represents the backing bean encapsulating information for a relationship group.
 * 
 */
public class RelationshipGroupWidgetBean extends ContainerWidgetBean implements Serializable {

	private static final long serialVersionUID = -5958617187730999325L;

	private int groupNumber;
	
	/**
	 * Default constructor for serialization.
	 */
	protected RelationshipGroupWidgetBean() {
		super();
	}

	/**
	 * Creates a new relationship group widget bean instance with the specified parameters.
	 * 
	 * @param model the group's widget model (may not be {@code null})
	 * @param groupNumber the group's identifying number (0 stands for ungrouped, individual relationships)
	 */
	public RelationshipGroupWidgetBean(final RelationshipGroupWidgetModel model, final int groupNumber, ConceptWidgetBean parent) {
		super(model, parent);
		this.groupNumber = groupNumber;
	}
	
	public boolean isConcreteDomainSupported() {
		return getModel().isConcreteDomainSupported();
	}
	
	@Override
	public RelationshipGroupWidgetModel getModel() {
		return (RelationshipGroupWidgetModel) super.getModel();
	}

	@Override
	public String getLabel() {
		return String.format(getModel().getLabel(), groupNumber);
	}

	/**
	 * @return the group's identifying number
	 */
	public int getGroupNumber() {
		return groupNumber;
	}

	@Override
	public String toString() {
		return String.format("RelationshipGroupWidgetBean [elements=%s, groupNumber=%s]", StringUtils.toString(getElements()), groupNumber);
	}

}