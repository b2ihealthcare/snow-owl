/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.model;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.Annotatable;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.common.SnomedConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * Static utility extension to work with SNOMED CT model components.
 * 
 * 
 * @since 3.6
 */
public class SnomedModelExtensions {

	private SnomedModelExtensions() {
	}
	
	/**
	 * Deactivates the given {@link Component} by unsetting the effective time and setting the active flag to <code>false</code>.
	 * 
	 * @param component
	 */
	public static void deactivate(final Component component) {
		if (component.isActive()) {
			component.setActive(false);
			component.unsetEffectiveTime();
		}
	}

	/**
	 * Deactivates the given {@link SnomedRefSetMember} by unsetting the effective time and setting the active flag to <code>false</code>.
	 * 
	 * @param refSetMember
	 */
	public static void deactivate(final SnomedRefSetMember refSetMember) {
		if (refSetMember.isActive()) {
			refSetMember.setActive(false);
			refSetMember.unsetEffectiveTime();
		}
	}

	/**
	 * Deactivates all {@link SnomedConcreteDataTypeRefSetMember}s of the given {@link Annotatable} component.
	 * 
	 * @param annotatable
	 */
	public static void deactivateConcreteDomains(final Annotatable annotatable) {
		for (final SnomedConcreteDataTypeRefSetMember member : annotatable.getConcreteDomainRefSetMembers()) {
			deactivate(member);
		}
	}

	/**
	 * Removes the {@link Relationship} from its {@link EObject#eResource containing} resource and/or its {@link EObject#eContainer containing}
	 * object.
	 * 
	 * @param relationship
	 */
	public static void remove(final Relationship relationship) {
		if (isNewObject(relationship) || !relationship.isReleased()) {
			relationship.setSource(null);
			relationship.setDestination(null);
		}
	}

	/**
	 * Removes the {@link Description} from its {@link EObject#eResource containing} resource and/or its {@link EObject#eContainer containing} object.
	 * 
	 * @param description
	 */
	public static void remove(final Description description) {
		if (isNewObject(description) || !description.isReleased()) {
			description.setConcept(null);
		}
	}

	/**
	 * Removes the {@link SnomedRefSetMember} from its {@link EObject#eResource containing} resource and/or its {@link EObject#eContainer containing}
	 * object.
	 * 
	 * @param refSetMember
	 */
	public static void remove(final SnomedRefSetMember refSetMember) {
		if (isNewObject(refSetMember) || !refSetMember.isReleased()) {
			EcoreUtil.remove(refSetMember);
		}
	}

	/**
	 * Removes all {@link SnomedConcreteDataTypeRefSetMember} from its {@link EObject#eResource containing} resource and/or its
	 * {@link EObject#eContainer containing} object for the given {@link Annotatable} component.
	 * 
	 * @param annotatable
	 */
	public static void removeConcreteDomains(final Annotatable annotatable) {
		for (final SnomedConcreteDataTypeRefSetMember member : annotatable.getConcreteDomainRefSetMembers()) {
			remove(member);
		}
	}

	/**
	 * Deactivates the {@link Relationship} and its {@link SnomedConcreteDataTypeRefSetMember}s if they were already released, or removes them if not.
	 * 
	 * @param relationship
	 */
	public static void removeOrDeactivate(final Relationship relationship) {
		if (relationship.isReleased()) {
			deactivate(relationship);
			deactivateConcreteDomains(relationship);
			for (SnomedAttributeValueRefSetMember refSetMember : relationship.getRefinabilityRefSetMembers()) {
				deactivate(refSetMember);
			}
		} else {
			remove(relationship);
		}
	}

	/**
	 * Deactivates the {@link SnomedRefSetMember} if it was released or removes it if it was not.
	 * 
	 * @param refSetMember
	 */
	public static void removeOrDeactivate(final SnomedRefSetMember refSetMember) {
		if (refSetMember.isReleased()) {
			deactivate(refSetMember);
		} else {
			remove(refSetMember);
		}
	}

	/**
	 * Deactivates the {@link Description} if it was released or removes it if it was not.
	 * 
	 * @param description
	 */
	public static void removeOrDeactivate(final Description description) {
		if (description.isReleased()) {
			deactivate(description);
		} else {
			remove(description);
		}
	}

	/**
	 * Determines if a {@link CDOObject} is a newly created object or not.
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isNewObject(final CDOObject object) {
		return CDOUtils.isUnpersisted(object) || null == object.cdoID() || object.cdoID().isTemporary();
	}
	
	public static boolean isPreferred(Description description, String languageRefSetId) {
		if (null == description) {
			return false;
		}
		
		if (!CDOUtils.checkObject(description)) {
			return false;
		}

		if (!description.isActive()) { //inactive description cannot be preferred
			return false;
		}
		
		// TODO fixme, this is NOT true, FSNs can be preferred in a lang.refset, exactly one FSN is allowed to be set to preferred in a lang.refset
		if (SnomedConstants.Concepts.FULLY_SPECIFIED_NAME.equals(description.getType().getId())) { //FSN cannot be preferred term
			return false;
		}
		
		for (final SnomedLanguageRefSetMember languageMember : description.getLanguageRefSetMembers()) {
			if (languageMember.isActive()) { //active language reference set member
				
				if (languageRefSetId.equals(languageMember.getRefSet().getIdentifierId())) { //language is relevant for the configured one
					
					if (SnomedConstants.Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(languageMember.getAcceptabilityId())
							&& languageMember.getRefSetIdentifierId().equals(languageRefSetId)) { //language member is preferred
						return true;
					}
				}
				
			}
		}

		return false;
	}
	
}