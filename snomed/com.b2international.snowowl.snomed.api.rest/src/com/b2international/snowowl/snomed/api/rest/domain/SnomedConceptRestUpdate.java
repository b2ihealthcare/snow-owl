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
package com.b2international.snowowl.snomed.api.rest.domain;

import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.google.common.collect.Multimap;

/**
 * @since 4.0
 */
public class SnomedConceptRestUpdate extends AbstractSnomedComponentRestUpdate {

	private DefinitionStatus definitionStatus;
	private SubclassDefinitionStatus subclassDefinitionStatus;
	private Multimap<AssociationType, String> associationTargets;
	private InactivationIndicator inactivationIndicator;

	/**
	 * @return
	 */
	public DefinitionStatus getDefinitionStatus() {
		return definitionStatus;
	}

	/**
	 * @return
	 */
	public SubclassDefinitionStatus getSubclassDefinitionStatus() {
		return subclassDefinitionStatus;
	}

	public void setDefinitionStatus(final DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
	}

	public void setSubclassDefinitionStatus(final SubclassDefinitionStatus subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
	}

	/**
	 * Returns with the associations between the current component and other SNOMED&nbsp;CT concepts.
	 * The associations are represented as a multimap where the keys are the {@link AssociationType association type}s
	 * and the values are the referred associations. 
	 * @return a multimap of associations.
	 */
	public Multimap<AssociationType, String> getAssociationTargets() {
		return associationTargets;
	}

	/**
	 * Sets the associations for the current concept.
	 * <br>Counterpart of {@link #getAssociationTargets()}.
	 * @param associationTargets the multimap of associations.
	 */
	public void setAssociationTargets(final Multimap<AssociationType, String> associationTargets) {
		this.associationTargets = associationTargets;
	}

	/**
	 * Returns with the concept inactivation reason (if any). May return with {@code null}
	 * if the concept is active or no reason was specified during the concept inactivation process.
	 * @return the inactivation process. Can be {@code null} if the concept is not retired or no
	 * reason was specified.
	 */
	public InactivationIndicator getInactivationIndicator() {
		return inactivationIndicator;
	}

	/**
	 * Counterpart of the {@link #getInactivationIndicator()}.
	 * <br>Sets the inactivation reason for the concept update.
	 * @param inactivationIndicator the desired inactivation reason for the concept update.
	 */
	public void setInactivationIndicator(final InactivationIndicator inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
	}

}