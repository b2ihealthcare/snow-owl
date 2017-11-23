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

import static com.b2international.commons.StringUtils.isEmpty;

import java.io.Serializable;
import java.util.Set;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CharacteristicTypePredicates;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.DataTypeWidgetModel;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * Bean to back data type widget
 */
public class DataTypeWidgetBean extends LeafWidgetBean implements Serializable, CharacteristicTypedWidgetBean {

	private static final long serialVersionUID = -6852248209336970236L;

	public static final String UNINITIALIZED = "";
	public static final String PROP_SELECTED_LABEL = "selectedLabel";
	public static final String PROP_SELECTED_VALUE = "selectedValue";
	public static final String PROP_SELECTED_UOM = "selectedUom";
	
	private String uuid;
	private String referencedComponentId; 
	private String selectedLabel = "";
	private String selectedValue = "";
	private String selectedUom;
	private String characteristicTypeId = Concepts.STATED_RELATIONSHIP;
	private String referencedComponentCharType = "";

	private ConceptWidgetBean cwb;

	private boolean inferredEditingEnabled;
	private boolean isRelationshipDataType;
	
	/**
	 * Default constructor for serialization.
	 */
	protected DataTypeWidgetBean() {
		super();
	}
	
	public DataTypeWidgetBean(final ConceptWidgetBean cwb, DataTypeWidgetModel model, String referencedComponentId) {
		this(cwb, model, referencedComponentId, UNINITIALIZED, false, false);
	}
	
	public DataTypeWidgetBean(ConceptWidgetBean cwb, DataTypeWidgetModel model, String referencedComponentId, String uuid, final boolean released, boolean isRelationshipDataType) {
		
		super(model, released);

		this.cwb = cwb;
		if (model.getAllowedLabels().size() == 1) {
			this.selectedLabel = Iterables.getFirst(model.getAllowedLabels(), null);
		}
		
		this.isRelationshipDataType = isRelationshipDataType;
		this.referencedComponentId = referencedComponentId;
		this.uuid = uuid;
		
		// Listen for changes on source properties of isPopulated()
		// XXX: no need to unregister these as we are pointing to ourselves
		addPropertyChangeListener(PROP_SELECTED_LABEL, actionEnablingListener);
		addPropertyChangeListener(PROP_SELECTED_VALUE, actionEnablingListener);
		addPropertyChangeListener(PROP_SELECTED_UOM, actionEnablingListener);
		
		inferredEditingEnabled = ApplicationContext.getInstance().getServiceChecked(SnomedCoreConfiguration.class).isInferredEditingEnabled();
	}
	
	public DataTypeWidgetBean(ConceptWidgetBean cwb, DataTypeWidgetModel matchingModel, String referencedComponentId,
			String referencedComponentCharacteristicType, String id, boolean released, boolean isRelationshipDataType) {
		this(cwb, matchingModel, referencedComponentId, id, released, isRelationshipDataType);
		this.referencedComponentCharType = referencedComponentCharacteristicType;
	}

	@Override
	public DataTypeWidgetModel getModel() {
		return (DataTypeWidgetModel) super.getModel();
	}

	public boolean isRelationshipDataType() {
		return isRelationshipDataType;
	}
	
	public String getreferencedComponentCharType() {
		return referencedComponentCharType;
	}
	
	public IComponent<String> getSelectedUom() {
		return getConcept().getComponent(selectedUom);
	}
	
	public void setSelectedUom(IComponent<String> newSelectedUom) {
		String oldSelectedUom = this.selectedUom;
		this.selectedUom = newSelectedUom.getId();
		getConcept().add(newSelectedUom);
		firePropertyChange(PROP_SELECTED_UOM, oldSelectedUom, newSelectedUom);
	}
	
	public void setSelectedUom(String newSelectedUom) {
		String oldSelectedUom = this.selectedUom;
		this.selectedUom = newSelectedUom;
		getConcept().add(newSelectedUom);
		firePropertyChange(PROP_SELECTED_UOM, oldSelectedUom, newSelectedUom);
	}
	
	public String getSelectedValue() {
		return selectedValue;
	}
	
	public void setSelectedValue(String newSelectedValue) {
		String oldSelectedValue = this.selectedValue;
		this.selectedValue = newSelectedValue;
		firePropertyChange(PROP_SELECTED_VALUE, oldSelectedValue, newSelectedValue);
	}
	
	public DataType getAllowedType() {
		return getModel().getAllowedType();
	}
	
	/**
	 * Returns the characteristic type concept id of the data type
	 * @return
	 */
	public String getCharacteristicType() {
		return characteristicTypeId;
	}
	
	/**
	 * Sets the characteristic type concept id for the data type
	 * @param characteristicTypeId
	 */
	public void setCharacteristicTypeId(String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}
	
	public String getSelectedLabel() {
		return selectedLabel;
	}
	
	public void setSelectedLabel(String newSelectedLabel) {
		String oldSelectedLabel = this.selectedLabel;
		this.selectedLabel = newSelectedLabel;
		firePropertyChange(PROP_SELECTED_LABEL, oldSelectedLabel, newSelectedLabel);
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getReferencedComponentId() {
		return referencedComponentId;
	}
	
	public Set<String> getAllowedLabels(){
		return getModel().getAllowedLabels();
	}
	
	@Override
	protected DataTypeWidgetBean replicate() {
		final DataTypeWidgetBean dataTypeWidgetBean = new DataTypeWidgetBean(cwb, getModel(), getReferencedComponentId(), getreferencedComponentCharType(), UNINITIALIZED, false, isRelationshipDataType);
		dataTypeWidgetBean.setPropagationEnabled(isPropagationEnabled());
		dataTypeWidgetBean.setSelectedLabel(getSelectedLabel());
		dataTypeWidgetBean.setSelectedValue(getSelectedValue());
		dataTypeWidgetBean.setSelectedUom(getSelectedUom());
		dataTypeWidgetBean.setCharacteristicTypeId(getCharacteristicType());
		return dataTypeWidgetBean;
	}
	
	public void clearSelectedLabel(){
		setSelectedLabel("");
	}
	
	@Override
	protected boolean isPopulated() {
		return !isEmpty(getSelectedLabel()) && !isEmpty(selectedValue);
	}
	
	@Override
	public ConceptWidgetBean getConcept() {
		return cwb;
	}

	public final boolean isUninitialized() {
		return DataTypeWidgetBean.UNINITIALIZED.equals(getUuid());
	}
	
	/**
	 * Returns <code>true</code> if the boolean value of this {@link DataTypeWidgetBean} is Yes.
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 *             if the given {@link DataTypeWidgetBean}'s allowed type is not a boolean.
	 */
	public boolean isTrue() {
		if (DataType.BOOLEAN.equals(getAllowedType())) {
			return "1".equals(getSelectedValue());
		} else {
			throw new IllegalArgumentException("The given dataType is not a boolean.");
		}
	}

	/**
	 * Returns <code>true</code> if the boolean value of this {@link DataTypeWidgetBean} is No.
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 *             if the given {@link DataTypeWidgetBean}'s allowed type is not a boolean.
	 */
	public boolean isFalse() {
		if (DataType.BOOLEAN.equals(getAllowedType())) {
			return "0".equals(getSelectedValue());
		} else {
			throw new IllegalArgumentException("The given dataType is not a boolean.");
		}
	}

	/**
	 * Returns <code>true</code> if the boolean value of this {@link DataTypeWidgetBean} is N/A.
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 *             if the given {@link DataTypeWidgetBean}'s allowed type is not a boolean.
	 */
	public boolean isNotSpecified() {
		if (DataType.BOOLEAN.equals(getAllowedType())) {
			return Strings.isNullOrEmpty(getSelectedValue());
		} else {
			throw new IllegalArgumentException("The given dataType is not a boolean.");
		}
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.mrcm.core.widget.bean.CharacteristicTypedWidgetBean#getSelectedCharacteristicTypeId()
	 */
	@Override
	public String getSelectedCharacteristicTypeId() {
		return characteristicTypeId;
	}
	
	@Override
	protected boolean canBeCloned() {
		return (super.canBeCloned() && CharacteristicTypePredicates.manuallyCreatableCharacteristicTypesIDsPredicate().apply(getSelectedCharacteristicTypeId()))
				|| inferredEditingEnabled;
	}

	@Override
	protected boolean canBeClonedAndRetired() {
		return (super.canBeClonedAndRetired() && CharacteristicTypePredicates.manuallyCreatableCharacteristicTypesIDsPredicate().apply(getSelectedCharacteristicTypeId()))
				|| inferredEditingEnabled;
	}
	
}