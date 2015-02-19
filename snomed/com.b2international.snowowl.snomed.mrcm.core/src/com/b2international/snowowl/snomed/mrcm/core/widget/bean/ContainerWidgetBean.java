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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ContainerWidgetModel;
import com.google.common.collect.Lists;

/**
 * A modeled widget bean that collects other modeled widget beans.
 * 
 */
public class ContainerWidgetBean extends ModeledWidgetBean implements Serializable {

	private static final long serialVersionUID = -3753622482613167667L;

	/** The property name for the {@link #getLabel() label} property. */
	public static final String PROP_LABEL = "label";
	
	/** The property name for the {@link #getElements() elements} property. */
	public static final String PROP_ELEMENTS = "elements";
	
	private List<ModeledWidgetBean> elements = Lists.newArrayList();

	private ConceptWidgetBean parent;

	/**
	 * Default constructor for serialization.
	 */
	protected ContainerWidgetBean() {
		super();
	}
	
	/**
	 * Creates a new container bean with the specified model.
	 * 
	 * @param model the container's model (may not be {@code null})
	 * @param parent 
	 */
	public ContainerWidgetBean(final ContainerWidgetModel model, ConceptWidgetBean parent) {
		super(model);
		this.parent = parent;
	}
	
	@Override
	public ContainerWidgetModel getModel() {
		return (ContainerWidgetModel) super.getModel();
	}

	/**
	 * @return the descriptive label assigned for this container (set on the model)
	 */
	public String getLabel() {
		return getModel().getLabel();
	}
	
	/**
	 * @return the list of contained widget beans
	 */
	public List<ModeledWidgetBean> getElements() {
		return elements;
	}
	
	/**
	 * Adds a new element to the end of the contained list.
	 * <p>
	 * This is a convenience method that invokes {@link #add(ModeledWidgetBean, ModeledWidgetBean)} with {@code null} as
	 * the second argument.
	 * 
	 * @param element the element to add (may not be {@code null}; may not be already present in the list)
	 */
	public void add(final ModeledWidgetBean element) {
		add(element, null);
	}

	/**
	 * Adds a new element after the specified element.
	 * 
	 * @param element the element to add (may not be {@code null}; may not be already present in the list)
	 * @param previousSibling ({@code null} is allowed; must be present in the list if not {@code null})
	 */
	public void add(final ModeledWidgetBean element, final ModeledWidgetBean previousSibling) {
		
		checkNotNull(element, "element");
		checkArgument(!elements.contains(element), "Added element is already a member of the list.");
		checkArgument(previousSibling == null || elements.contains(previousSibling),
				"Previous sibling must be a member of the list.");
		
		final int insertionIndex = getInsertionIndex(previousSibling);
		
		elements.add(insertionIndex, element);
		
		if (element instanceof LeafWidgetBean) {
			((LeafWidgetBean) element).setParent(this);
		}
		
		firePropertyChange(PROP_ELEMENTS, null, elements);
	}

	private int getInsertionIndex(final ModeledWidgetBean element) {
		final int previousIndex = elements.indexOf(element);
		return (previousIndex != -1) ? previousIndex + 1 : elements.size(); // Add to the end if element is null
	}

	/**
	 * Removes the specified element from the list.
	 * 
	 * @param element the element to remove (must be a current member of the list)
	 */
	public void remove(final ModeledWidgetBean element) {
		
		if (!elements.remove(element)) {
			throw new IllegalArgumentException("Element is not a member of the list.");
		}
		
		if (element instanceof LeafWidgetBean) {
			((LeafWidgetBean) element).setParent(null);
		}
		
		firePropertyChange(PROP_ELEMENTS, null, elements);
	}

	/**
	 * Checks if the specified element is the last instance of its widget model.
	 * 
	 * @param element the element to check (must be a current member of the list)
	 * @return {@code true} if the bean is the last instance of its associated widget model, {@code false} otherwise
	 */
	public boolean isLastInstance(final ModeledWidgetBean element) {

		if (!elements.contains(element)) {
			throw new IllegalArgumentException("Element is not a member of the list.");
		}

		final Object candidateModel = element.getModel();
		
		for (final ModeledWidgetBean existingElement : elements) {
			
			final Object existingModel = existingElement.getModel();
			
			if (existingModel == candidateModel && existingElement != element) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return String.format("ContainerWidgetBean [label=%s, elements=%s]", getLabel(), StringUtils.toString(elements));
	}

	public ConceptWidgetBean getParent() {
		return parent;
	}
	
	@Override
	public ConceptWidgetBean getConcept() {
		return parent;
	}
}