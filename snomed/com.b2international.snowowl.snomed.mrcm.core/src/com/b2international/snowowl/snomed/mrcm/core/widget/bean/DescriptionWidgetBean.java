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

import java.util.Set;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.NullComponent;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.CaseSignificance;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.DescriptionWidgetModel;
import com.google.common.collect.Iterables;

/**
 * Represents a backing bean for a SNOMED CT description.
 *
 */
public class DescriptionWidgetBean extends LeafWidgetBean {

	
	private static final long serialVersionUID = 4393351743753400600L;

	/**
	 * Special value to indicate that a synonym is preferred in the context of the currently used language reference set.
	 * <p>
	 * A concept of similar meaning may or may not exist in the terminology. Always handle description widget beans which have this type selected separately.
	 * <p>
	 * The ConceptMini's identifier has been randomly selected from the B2i Healthcare namespace.
	 * 
	 * @deprecated
	 */
	public static final SnomedConceptIndexEntry PREFERRED_TERM_PLACEHOLDER = SnomedConceptIndexEntry.builder()
			.id("225857101000154102") 
			.iconId(Concepts.SYNONYM)
			.moduleId(Concepts.MODULE_SCT_CORE)
			.storageKey(0L)
			.active(true)
			.released(true)
			.primitive(true)
			.effectiveTimeLong(EffectiveTimes.parse("2013-07-31").getTime())
			.build();

	/** The property name of the {@link #getSelectedType() selectedType} property. */
	public static final String PROP_SELECTED_TYPE = "selectedType";

	/** The property name of the {@link #getTerm() term} property. */
	public static final String PROP_TERM = "term";

	public static final String PROP_PREFERRED = "preferred";

	public static final String PROP_CASE_SENSITIVITY = "caseSensitivity";

	public static final long UNINITIALIZED = -1;

	private long sctId;
	private String selectedType;
	private String term;
	private CaseSignificance caseSensitivity = CaseSignificance.ENTIRE_TERM_CASE_INSENSITIVE;
	private ConceptWidgetBean cwb;

	/**
	 * Indicates that the description is preferred in the context of the currently used language reference set.
	 */
	private boolean preferred;


	/**
	 * Default constructor for serialization.
	 */
	protected DescriptionWidgetBean() {
		super();
	}
	
	/**
	 * Creates a new description bean with the specified parameters.
	 * 
	 * @param model
	 *            the model for this description (may not be {@code null})
	 * @param sctId
	 *            the description's SNOMED CT identifier
	 * @param released
	 *            {@code true} if the represented component is released. Otherwise {@code false}.
	 */
	public DescriptionWidgetBean(final ConceptWidgetBean cwb, final DescriptionWidgetModel model, final long sctId, final boolean released, final boolean preferred) {

		super(model, released);
		this.cwb = cwb;
		this.sctId = sctId;
		this.preferred = preferred;

		// Listen for changes on source properties of isPopulated()
		// XXX: no need to unregister these as we are pointing to ourselves
		addPropertyChangeListener(PROP_SELECTED_TYPE, actionEnablingListener);
		addPropertyChangeListener(PROP_TERM, actionEnablingListener);
	}

	@Override
	public DescriptionWidgetModel getModel() {
		return (DescriptionWidgetModel) super.getModel();
	}

	/**
	 * @return the set of concepts IDs that are allowed as a type for this description (set on the model)
	 */
	public Set<String> getAllowedTypeIds() {
		return getModel().getAllowedTypeIds();
	}

	/**
	 * @return the pre-assigned SNOMED CT identifier, or -1 if the description is new
	 */
	public long getSctId() {
		return sctId;
	}

	/**
	 * @return the minified concept representing the description's type
	 */
	public IComponent<String> getSelectedType() {
		return getConcept().getComponent(selectedType);
	}
	
	public String getSelectedTypeId() {
		return selectedType;
	}

	public void setSelectedType(final String newSelectedType) {
		final String oldSelectedType = this.selectedType;
		this.selectedType = newSelectedType;
		getConcept().add(newSelectedType);
		firePropertyChange(PROP_SELECTED_TYPE, oldSelectedType, newSelectedType);
	}
	
	public void setSelectedType(final IComponent<String> newSelectedType) {
		final String oldSelectedType = this.selectedType;
		this.selectedType = newSelectedType.getId();
		getConcept().add(newSelectedType);
		firePropertyChange(PROP_SELECTED_TYPE, oldSelectedType, newSelectedType);
	}

	/**
	 * @return the description term
	 */
	public String getTerm() {
		return term;
	}

	public void setTerm(final String newTerm) {
		final String oldTerm = this.term;
		this.term = newTerm;
		firePropertyChange(PROP_TERM, oldTerm, newTerm);
	}

	public CaseSignificance getCaseSensitivity() {
		return caseSensitivity;
	}

	public void setCaseSensitivity(final CaseSignificance newCaseSensitivity) {
		final CaseSignificance oldCaseSensitivity = this.caseSensitivity;
		this.caseSensitivity = newCaseSensitivity;
		firePropertyChange(PROP_CASE_SENSITIVITY, oldCaseSensitivity, newCaseSensitivity);
	}

	@Override
	public LeafWidgetBean onCloneAction() {
		// Add to parent container, then set preferred term status
		final DescriptionWidgetBean replicate = (DescriptionWidgetBean) super.onCloneAction();
		replicate.setPreferred(this.isPreferred());
		return replicate;
	}

	@Override
	protected DescriptionWidgetBean replicate() {
		final DescriptionWidgetBean descriptionWidgetBean = new DescriptionWidgetBean(cwb, getModel(), UNINITIALIZED, false, false);
		descriptionWidgetBean.setSelectedType(this.getSelectedType());
		descriptionWidgetBean.setTerm(this.getTerm());
		descriptionWidgetBean.setCaseSensitivity(this.getCaseSensitivity());
		return descriptionWidgetBean;
	}

	public boolean isPreferred() {
		return preferred;
	}
	
	/**
	 * Returns <code>true</code> if the underlying description is a fully specified name.
	 * 
	 * @param description
	 * @return
	 */
	public boolean isFsn() {
		return Concepts.FULLY_SPECIFIED_NAME.equals(getSelectedType().getId());
	}

	public void setPreferred(final boolean newPreferred) {

		// There can only be one preferred term. Set all other siblings to not preferred first.
		if (newPreferred && null != getParent()) {
			for (final DescriptionWidgetBean sibling : Iterables.filter(getParent().getElements(), DescriptionWidgetBean.class)) {
				if (this != sibling && sibling.isPreferred()) {
					sibling.setPreferred(false);
				}
			}
		}

		final boolean oldPreferred = this.preferred;
		this.preferred = newPreferred;
		firePropertyChange(PROP_PREFERRED, oldPreferred, newPreferred);
	}

	@Override
	public String toString() {

		return String.format(
				"DescriptionWidgetBean [sctId=%s, selectedType=%s, term=%s, preferred=%s]",
				sctId, selectedType, term, preferred);
	}

	public void setSctId(final Long sctId) {
		this.sctId = sctId;
	}

	public void clearSelectedType() {
		setSelectedType(NullComponent.<String> getNullImplementation());
	}

	@Override
	protected boolean isPopulated() {
		return !isEmpty(getTerm()) && !isEmpty(selectedType);
	}

	public boolean isPropagationMenuItemEnabled() {
		return (!isReleased() && (isPreferred() || Concepts.FULL_NAME.equals(selectedType) || Concepts.SYNONYM.equals(selectedType)));
	}
	
	@Override
	public ConceptWidgetBean getConcept() {
		return cwb;
	}
}