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

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.ExtendedComponentImpl;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IconIdProvider;
import com.b2international.snowowl.core.api.NullComponent;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.MappingWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.WidgetModel;

/**
 */
public class MappingWidgetBean extends LeafWidgetBean implements Serializable {
	
	private static final long serialVersionUID = 8251883806071758605L;

	public static final String PROP_SELECTED_VALUE = "selectedValue";
	public static final String UNINITIALIZED = "-1";
	private final String selectedLabel = "ATC code";
	private String selectedValue;
	private String uuid = UNINITIALIZED;
	private ConceptWidgetBean cwb;
	
	/**
	 * Default constructor for serialization.
	 */
	protected MappingWidgetBean() {
		super();
	}
	
	public MappingWidgetBean(final ConceptWidgetBean cwb, final WidgetModel model, final boolean released) {
		super(model, released);
		this.cwb = cwb;
		addPropertyChangeListener(PROP_SELECTED_VALUE, actionEnablingListener);
	}
	
	@Override
	public boolean isCloneActionEnabled() {
		return false;
	}
	
	@Override
	public boolean isCloneAndRetireActionEnabled() {
		return false;
	}
	
	@Override
	protected boolean canBeCloned() {
		return false;
	}
	
	@Override
	public MappingWidgetModel getModel() {
		return (MappingWidgetModel) super.getModel();
	}

	@Override
	protected boolean isPopulated() {
		return !NullComponent.isNullComponent(getSelectedValue());
	}

	@Override
	protected LeafWidgetBean replicate() {
		throw new UnsupportedOperationException();
	}
	
	public String getSelectedLabel() {
		return selectedLabel;
	}
	
	public void setSelectedValue(final IComponent<String> newSelectedValue) {
		final String oldSelectedValue = this.selectedValue;
		this.selectedValue = newSelectedValue.getId();
		final String iconId = newSelectedValue instanceof IconIdProvider ? ((IconIdProvider<String>) newSelectedValue).getIconId() : null;
		getConcept().add(new ExtendedComponentImpl(newSelectedValue.getId(), newSelectedValue.getLabel(), iconId, CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsShort(newSelectedValue)));
		firePropertyChange(PROP_SELECTED_VALUE, oldSelectedValue, newSelectedValue);
	}
	
	public void setSelectedValue(final String newSelectedValue) {
		final String oldSelectedValue = this.selectedValue;
		this.selectedValue = newSelectedValue;
		getConcept().add(newSelectedValue);
		firePropertyChange(PROP_SELECTED_VALUE, oldSelectedValue, newSelectedValue);
	}
	
	public IComponent<String> getSelectedValue() {
		return getConcept().getComponent(selectedValue);
	}
	
	public void clearSelectedValue(){
		setSelectedValue(NullComponent.<String>getNullImplementation());
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	@Override
	public ConceptWidgetBean getConcept() {
		return cwb;
	}
	
	@Override
	public String toString() {
		return String.format("MappingWidgetBean [selectedValue=%s, model=%s]", 
				selectedValue,	getModel());
	}

}