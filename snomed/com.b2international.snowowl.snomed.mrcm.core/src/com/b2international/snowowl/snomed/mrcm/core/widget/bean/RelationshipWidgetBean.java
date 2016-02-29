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
import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Set;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.NullComponent;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CharacteristicTypePredicates;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipWidgetModel;
import com.google.common.base.Strings;

/**
 * Backing bean for the relationship selector UI widget.
 * 
 */
public class RelationshipWidgetBean extends LeafWidgetBean implements CharacteristicTypedWidgetBean {

	private static final long serialVersionUID = -4424239339860251671L;

	public static final String PROP_SELECTED_TYPE = "selectedType";

	public static final String PROP_SELECTED_VALUE = "selectedValue";
	
	public static final String PROP_CHAR_TYPE = "selectedCharacteristicType";

	public static final long UNINITIALIZED = -1;

	private long sctId;

	private boolean universalRestriction;

	private String selectedType;
	private String selectedValue;
	private String selectedCharacteristicType;
	private ConceptWidgetBean cwb;

	private boolean inferredEditingEnabled;

	/**
	 * Default constructor for serialization.
	 */
	protected RelationshipWidgetBean() {
		super();
	}

	/**
	 * Creates a new relationship backing bean with the specified arguments.
	 * 
	 * @param model
	 *            the relationship's originating model (may not be {@code null})
	 * @param sctId
	 *            the relationship's SNOMED CT identifier; a value of {@link UNINITIALIZED} indicates a new relationship
	 * @param released
	 *            {@code true} if the represented component is released. Otherwise {@code false}.
	 * @param universalRestriction
	 *            flag indicating the DL restriction for the relationship. {@code true} if universal restriction, otherwise {@code false}.
	 */
	public RelationshipWidgetBean(final ConceptWidgetBean cwb, final RelationshipWidgetModel model, final long sctId, final boolean released,
			final boolean universalRestriction) {
		super(model, released);
		this.cwb = cwb;
		this.sctId = sctId;
		this.universalRestriction = universalRestriction;

		// Listen for changes on source properties of isPopulated()
		// XXX: no need to unregister these as we are pointing to ourselves
		addPropertyChangeListener(PROP_SELECTED_TYPE, actionEnablingListener);
		addPropertyChangeListener(PROP_SELECTED_VALUE, actionEnablingListener);
		
		inferredEditingEnabled = ApplicationContext.getInstance().getServiceChecked(SnomedCoreConfiguration.class).isInferredEditingEnabled();
	}

	@Override
	public RelationshipWidgetModel getModel() {
		return (RelationshipWidgetModel) super.getModel();
	}

	/**
	 * @return the set of concept IDs representing allowed types (set on the model)
	 */
	public Set<String> getAllowedTypeIds() {
		return getModel().getAllowedTypeIds();
	}

	/**
	 * @return an expression representing the set of allowed concept IDs (set on the model)
	 */
	public String getAllowedValueIdsExpression() {
		return getModel().getAllowedValueIdsExpression();
	}

	/**
	 * @return the relationship's SNOMED CT identifier
	 */
	public long getSctId() {
		return sctId;
	}

	/**
	 * Returns <code>true</code> if the relationship type has been specified. Otherwise returns with <code>false</code>.
	 */
	public boolean isTypeSet() {
		return !NullComponent.isNullComponent(getSelectedType());
	}

	/**
	 * Returns <code>true</code> if the relationship destination has been specified. Otherwise returns with <code>false</code>.
	 */
	public boolean isValueSet() {
		return !NullComponent.isNullComponent(getSelectedValue());
	}

	public boolean isValid() {
		return isTypeSet() && isValueSet();
	}

	/**
	 * Returns <code>true</code> if the relationship type is IS_A. Otherwise returns with <code>false</code>.
	 */
	public boolean isIsA() {
		return Concepts.IS_A.equals(getSelectedType().getId());
	}

	/**
	 * Returns <code>true</code> if this {@link RelationshipWidgetBean}'s selected type is set and equals with the given value.
	 * 
	 * @param typeId
	 * @return
	 */
	public boolean isTypeMatches(String typeId) {
		checkArgument(!Strings.isNullOrEmpty(typeId), "TypeId must be specified");
		return isTypeSet() && typeId.equals(getSelectedType().getId());
	}

	/**
	 * @return the currently selected minified type concept
	 */
	public IComponent<String> getSelectedType() {
		return getConcept().getComponent(selectedType);
	}

	@Override
	public RelationshipGroupWidgetBean getParent() {
		return (RelationshipGroupWidgetBean) super.getParent();
	}

	public void setSelectedType(final String newSelectedType) {
		final String oldSelectedType = this.selectedType;
		this.selectedType = newSelectedType;
		getConcept().add(selectedType);
		firePropertyChange(PROP_SELECTED_TYPE, oldSelectedType, newSelectedType);
	}

	public void setSelectedType(final IComponent<String> newSelectedType) {
		final String oldSelectedType = this.selectedType;
		this.selectedType = newSelectedType.getId();
		getConcept().add(newSelectedType);
		firePropertyChange(PROP_SELECTED_TYPE, oldSelectedType, newSelectedType);
	}

	/**
	 * @return the currently selected minified value concept
	 */
	public IComponent<String> getSelectedValue() {
		return getConcept().getComponent(selectedValue);
	}

	public void setSelectedValue(final IComponent<String> newSelectedValue) {
		final String oldSelectedValue = this.selectedValue;
		this.selectedValue = newSelectedValue.getId();
		getConcept().add(newSelectedValue);
		firePropertyChange(PROP_SELECTED_VALUE, oldSelectedValue, newSelectedValue);
	}

	public void setSelectedValue(final String newSelectedValue) {
		final String oldSelectedValue = this.selectedValue;
		this.selectedValue = newSelectedValue;
		getConcept().add(newSelectedValue);
		firePropertyChange(PROP_SELECTED_VALUE, oldSelectedValue, newSelectedValue);
	}

	/**
	 * @return the currently selected minified characteristic type concept
	 */
	public IComponent<String> getSelectedCharacteristicType() {
		return getConcept().getComponent(selectedCharacteristicType);
	}

	public void setSelectedCharacteristicType(final IComponent<String> selectedCharacteristicType) {
		final String oldSelectedCharType = this.selectedCharacteristicType;
		this.selectedCharacteristicType = selectedCharacteristicType.getId();
		getConcept().add(selectedCharacteristicType);
		firePropertyChange(PROP_CHAR_TYPE, oldSelectedCharType, this.selectedCharacteristicType);
	}

	public void setSelectedCharacteristicType(final String selectedCharacteristicType) {
		final String oldSelectedCharType = this.selectedCharacteristicType;
		this.selectedCharacteristicType = selectedCharacteristicType;
		getConcept().add(selectedCharacteristicType);
		firePropertyChange(PROP_CHAR_TYPE, oldSelectedCharType, selectedCharacteristicType);
	}
	
	@Override
	public String getSelectedCharacteristicTypeId() {
		return selectedCharacteristicType;
	}

	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT relationship's Description Logic (DL) restriction is the
	 * {@link Concepts#UNIVERSAL_RESTRICTION_MODIFIER all} concept. Otherwise returns with {@code false}.
	 * 
	 * @return {@code true} if modifier is the universal restriction, {@code false} if existential.
	 */
	public boolean isUniversalRestriction() {
		return universalRestriction;
	}

	/**
	 * Sets the flag whether the DL restriction of the current relationship is either {@code some} or {@code all}. {@code true} if {@code all},
	 * otherwise {@code false}.
	 * 
	 * @param universalRestriction
	 *            the universalRestriction to set
	 */
	public void setUniversalRestriction(final boolean universalRestriction) {
		this.universalRestriction = universalRestriction;
	}

	@Override
	protected RelationshipWidgetBean replicate() {
		final RelationshipWidgetBean relationshipWidgetBean = new RelationshipWidgetBean(cwb, getModel(), UNINITIALIZED, false,
				isUniversalRestriction());
		relationshipWidgetBean.setSelectedType(this.getSelectedType());
		relationshipWidgetBean.setSelectedValue(this.getSelectedValue());
		relationshipWidgetBean.setSelectedCharacteristicType(this.getSelectedCharacteristicType());
		// Do not replicate selected characteristic type, set it on the replicated instance when returned!
		return relationshipWidgetBean;
	}

	@Override
	public String toString() {
		return String.format("RelationshipWidgetBean [sctId=%s, selectedType=%s, selectedValue=%s, model=%s]", sctId, selectedType, selectedValue,
				getModel());
	}

	public void setSctId(final long sctId) {
		this.sctId = sctId;
	}

	public void clearSelectedValue() {
		setSelectedValue(NullComponent.<String> getNullImplementation());
	}

	public void clearSelectedType() {
		setSelectedType(NullComponent.<String> getNullImplementation());
	}

	@Override
	protected boolean isPopulated() {
		return !isEmpty(selectedType) && !isEmpty(selectedValue);
	}

	@Override
	protected boolean canBeCloned() {
		return (super.canBeCloned() && CharacteristicTypePredicates.manuallyCreatableCharacteristicTypesIDsPredicate().apply(getSelectedCharacteristicTypeId()))
				|| inferredEditingEnabled;
	}
	
	//Orsi: Allow inferred relationships to be manually retired as the classifier does not always remove them 
	@Override
	protected boolean canBeRetired() {
		return super.canBeRetired() && !hasAssociatedMembers();
	}

	@Override
	protected boolean canBeClonedAndRetired() {
		return (super.canBeClonedAndRetired() && CharacteristicTypePredicates.manuallyCreatableCharacteristicTypesIDsPredicate().apply(getSelectedCharacteristicTypeId()))
				|| inferredEditingEnabled;
	}
	
	public boolean hasAssociatedMembers() {

		if (null == getParent()) {
			return false;
		}

		if (UNINITIALIZED == getSctId()) {
			return false;
		}

		final List<ModeledWidgetBean> siblings = getParent().getElements();
		final String id = Long.toString(getSctId());

		for (final ModeledWidgetBean sibling : siblings) {

			if (!(sibling instanceof DataTypeWidgetBean)) {
				continue;
			}

			final DataTypeWidgetBean siblingDataTypeWidgetBean = (DataTypeWidgetBean) sibling;

			if (id.equals(siblingDataTypeWidgetBean.getReferencedComponentId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ConceptWidgetBean getConcept() {
		return cwb;
	}

}