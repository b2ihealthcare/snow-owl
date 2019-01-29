/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.history;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * Utility class for the history framework.
 */
public abstract class SnomedHistoryUtils {
	
	private SnomedHistoryUtils() { }
	
	public static String getLabelForConcept(final Concept concept, final List<ExtendedLocale> locales) {
		checkArgument(CDOUtils.checkObject(concept));
		
		// TODO this code belongs to the client where lang.refset preference is actually available
		Optional<Description> preferredTerm = FluentIterable.from(concept.getDescriptions()).firstMatch(new Predicate<Description>() {
			@Override public boolean apply(Description input) {
				return SnomedModelExtensions.isPreferred(input, locales.get(0).getLanguageRefSetId());
			}
		});
		
		return preferredTerm.isPresent() ? preferredTerm.get().getTerm() : concept.getFullySpecifiedName();
	}
	
	public static String getLabelForDescription(final Description description) {
		checkArgument(CDOUtils.checkObject(description));
		return description.getTerm();
	}
	
	public static String getLabelForRelationship(final Relationship relationship, List<ExtendedLocale> locales) {
		checkArgument(CDOUtils.checkObject(relationship));
		return String.format("%s %s%s%s", 
				getLabelForConcept(relationship.getSource(), locales), 
				getLabelForConcept(relationship.getType(), locales), 
				relationship.isDestinationNegated() ? " NOT " : " ", 
				getLabelForConcept(relationship.getDestination(), locales));
	}
	
	public static Concept getConcept(final String id, final CDOView view) {
		return new SnomedConceptLookupService().getComponent(id, view);
	}
	
	public static Relationship getRelationship(final String id, final CDOView view) {
		return new SnomedRelationshipLookupService().getComponent(id, view);
	}
	
	public static Description getDescription(final String id, final CDOView view) {
		return new SnomedDescriptionLookupService().getComponent(id, view);
	}

	/**
	 * Returns the reference set class of the specified {@link CDOObject}.
	 * 
	 * @param cdoObject
	 *            the object to inspect
	 * 
	 * @return the reference set class of the object, or {@code null}
	 */
	public static Class<?> getRefSetClass(final CDOObject cdoObject) {
		if (SnomedMappingRefSet.class.isAssignableFrom(cdoObject.getClass())) {
			return SnomedMappingRefSet.class;
		} else if (SnomedRefSet.class.isAssignableFrom(cdoObject.getClass())) {
			return SnomedRefSet.class;
		}

		throw new RuntimeException("Could not determine reference set class type for passed in object: " + cdoObject + " [" + cdoObject.getClass() + "]");
	}
}
