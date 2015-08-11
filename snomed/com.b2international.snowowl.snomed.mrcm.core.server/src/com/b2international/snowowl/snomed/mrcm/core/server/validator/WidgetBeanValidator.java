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
package com.b2international.snowowl.snomed.mrcm.core.server.validator;

import static com.b2international.snowowl.snomed.mrcm.core.validator.util.ConceptWidgetBeanUtil.isFsn;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import bak.pcj.set.LongSet;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationStatus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.FullySpecifiedNameUniquenessValidator;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.mrcm.core.MrcmCoreActivator;
import com.b2international.snowowl.snomed.mrcm.core.validator.IWidgetBeanValidator;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DescriptionWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ModeledWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipGroupWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipWidgetBean;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.inject.Provider;

/**
 * Server side {@link ConceptWidgetBean} validator.
 */
public class WidgetBeanValidator implements IWidgetBeanValidator {

	private Provider<SnomedTerminologyBrowser> browserProvider;

	public WidgetBeanValidator(Provider<SnomedTerminologyBrowser> browserProvider) {
		this.browserProvider = browserProvider;
	}
	
	/**
	 * Validates a {@link ConceptWidgetBean} and returns with a multimap of status informations.
	 * <br>Keys are the {@link ModeledWidgetBean} instances.
	 * <br>Values are the associated validation status. 
	 * @param conceptWidgetBean the {@link ConceptWidgetBean} to validate. Represents a SNOMED&nbsp;CT concept.
	 * @return a multimap of validation status. Can be empty.
	 */
	@Override
	public Multimap<ModeledWidgetBean, IStatus> validate(IBranchPath branchPath, ConceptWidgetBean conceptWidgetBean) {
		final Multimap<ModeledWidgetBean, IStatus> results = HashMultimap.create();
		results.putAll(validateDescriptions(branchPath, conceptWidgetBean));
		results.putAll(validateRelationships(branchPath, conceptWidgetBean));
		return results;
	}

	/*validates the descriptions associated with the specified concept widget bean*/
	private Multimap<ModeledWidgetBean, IStatus> validateDescriptions(IBranchPath branchPath, final ConceptWidgetBean concept) {
		
		final Multimap<ModeledWidgetBean, IStatus> results = HashMultimap.create();
		int numberOfFsns = 0;
		final List<ModeledWidgetBean> descriptions = concept.getDescriptions().getElements();
		for (final DescriptionWidgetBean description : Iterables.filter(descriptions, DescriptionWidgetBean.class)) {
			
			if (isFsn(description)) {
				numberOfFsns++;
				if (StringUtils.isEmpty(description.getTerm())) {
					results.put(description, createError("Fully specified name should be specified."));
				} else {
					//check fsn uniqueness for unpersisted concept.
					if (null == ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class).getConcept(branchPath, concept.getConceptId())) {
						final IStatus status = new FullySpecifiedNameUniquenessValidator().validate(description.getTerm());
						if (!status.isOK()) results.put(description, status);
					}
				}
				
			} else if (description.isPreferred()) { // TODO: validate description type along with preferred flag?
				
				if (StringUtils.isEmpty(description.getTerm())) {
					results.put(description, createError("Preferred term should be specified."));
				}
			}
		}
		
		if (numberOfFsns > 1) {
			results.put(concept, createError("Concept should have exactly one active fully specified name."));
		}
		
		return results;
	}

	/*validates the relationships associated with the concept widget model*/
	private Multimap<ModeledWidgetBean, IStatus> validateRelationships(final IBranchPath branchPath, final ConceptWidgetBean concept) {
		final SnomedTerminologyBrowser browser = browserProvider.get();
		
		final Multimap<ModeledWidgetBean, IStatus> results = HashMultimap.create();
		final List<ModeledWidgetBean> groups = concept.getProperties().getElements();
		
		boolean hasActiveIsA = false;
		final Multimap<String, String> isaTypeToParentIdMap = HashMultimap.create();
		
		for (final RelationshipGroupWidgetBean group : Iterables.filter(groups, RelationshipGroupWidgetBean.class)) {
			
			final List<ModeledWidgetBean> relationships = group.getElements();
			
			for (final RelationshipWidgetBean relationship : Iterables.filter(relationships, RelationshipWidgetBean.class)) {
				if (!relationship.isValid()) {
					continue;
				}
				
				if (relationship.isUnsanctioned()) {
					results.put(relationship, createWarning("This property violates the concept model."));
				}

				// The rest of the loop only deals with IS A relationships
				final String destinationId = relationship.getSelectedValue().getId();
				if (relationship.isIsA()) {
					final String characteristicTypeId = relationship.getSelectedCharacteristicType().getId();
					final String characteristicTypeLabel = browser.getConcept(branchPath, characteristicTypeId).getLabel();
					final Collection<String> parents = isaTypeToParentIdMap.get(characteristicTypeId);
					if (parents.contains(destinationId)) {
						results.put(relationship, createError("The concept has duplicate (%s) Is-a relationships.", characteristicTypeLabel));
					} else {
						isaTypeToParentIdMap.put(characteristicTypeId, destinationId);
					}
					
					// check for active IS_A in ungrouped properties
					if (!hasActiveIsA && 0 == group.getGroupNumber()) {
						hasActiveIsA = true;
					}

					final String snomedConceptId = concept.getConceptId();
					if (destinationId.equals(snomedConceptId)) {
						results.put(relationship, createError("For Is-a relationships, the destination should not be the same as the edited concept."));
					} else {
						final SnomedConceptIndexEntry snomedConceptMini = browser.getConcept(branchPath, snomedConceptId);
						final Long snomedConceptIdLong = Long.valueOf(snomedConceptId);
						final LongSet allSubTypesAndSelfIds = browser.getAllSubTypeIds(branchPath, snomedConceptIdLong);
						allSubTypesAndSelfIds.add(snomedConceptIdLong);
						
						if (null == snomedConceptMini) { //new concept. it does not exist in store. cannot cause cycle.
							continue;
						}
						
						//the IS_A relationship destination cannot be in the subtype set
						if (allSubTypesAndSelfIds.contains(Long.valueOf(destinationId))) {
							results.put(relationship, createError("For Is-a relationships, the destination should not be the descendant of the edited concept."));
						}
					}
				}
			}
		}
		
		//root concept and inactive concepts do not need active IS_A relationship
		if (!hasActiveIsA && !Concepts.ROOT_CONCEPT.equals(concept.getConceptId()) && concept.isActive()) {
			results.put(concept, createError("Concept must have at least one active ungrouped 'Is a' relationship."));
		}
		
		return results;
	}

	/*generates status with error severity. the message of the status is based on the specified message*/
	private IStatus createError(final String message, Object...args) {
		return createStatus(IStatus.ERROR, message, args);
	}
	
	/*generates status with warning severity. the message of the status is based on the specified message*/
	private IStatus createWarning(final String message, Object...args) {
		return createStatus(IStatus.WARNING, message, args);
	}
	
	/*generates status with a specified severity and message*/
	private IStatus createStatus(final int severity, final String message, final Object...args) {
		return new ComponentValidationStatus(severity, MrcmCoreActivator.PLUGIN_ID, String.format(message, args));
	}
}