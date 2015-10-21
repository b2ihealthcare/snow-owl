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
package com.b2international.snowowl.snomed.datastore.services;

import static com.b2international.snowowl.datastore.utils.ComponentUtils2.getNewObjects;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Singleton for retrieving a human readable label for SNOMED CT concepts.
 * <p>
 * 
 * 
 * 
 * @see IComponentNameProvider
 */
public enum SnomedConceptNameProvider implements IComponentNameProvider {

	/**
	 * The headless label provider instance for the SNOMED CT concepts.
	 */
	INSTANCE;
	
	/**
	 * Accepts the followings as argument:
	 * <p>
	 * <ul>
	 * <li>{@link Concept SNOMED CT concept}</li>
	 * <li>{@link SnomedConceptIndexEntry SNOMED CT concept (Snor)}</li>
	 * <li>{@link RefSetMini SNOMED CT reference set (Snor)}</li>
	 * <li>{@link SnomedConceptIndexEntry SNOMED CT concept (Lucene)}</li>
	 * <li>{@link String SNOMED CT concept identifier as string}</li>
	 * <li>{@link SnomedRefSet SNOMED CT reference set}</li>
	 * <li>{@link IStatus Status}</li>
	 * </ul>
	 * </p>
	 */
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentNameProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(final Object object) {
		final String label = getConceptLabel(object);
		return null == label ? null == object ? "" : String.valueOf(object) : label;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentNameProvider#getComponentLabel(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public String getComponentLabel(final IBranchPath branchPath, final String componentId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(componentId, "Component ID argument cannot be null.");
		
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class).getComponentLabel(branchPath, componentId);
	}
	
	/**
	 * Returns with the a human readable representation of the SNOMED CT concept's label.
	 * <p>First this method tires to retrieve the preferred term of the
	 * SNOMED CT concept from the underlying view assuming that the concept is in not {@link CDOState#NEW persisted state} then 
	 * tries to retrieve the SNOMED CT concept's label from one of the available lightweight stores or from the
	 * RDBMS backend if no label can be found for the specified SNOMED CT concept.
	 * </p>
	 * <p>
	 * If extracting the preferred term from the transaction also failed then returns with the specified concept identifier. This method never returns with {@code null}.
	 * </p>
	 * @param conceptId unique identifier of the SNOMED CT concept. Cannot be {@code null}. Cannot be empty string. 
	 * @param view the underlying CDO view. Cannot be {@code null}.
	 * @return a human readable label of the SNOMED CT concept. Never {@code null}.
	 */
	public String getText(@Nonnull final String conceptId, @Nonnull final CDOView view) {
		checkNotNull(conceptId, "Concept identifier argument cannot be empty string.");
		checkArgument(!StringUtils.isEmpty(conceptId), "Concept identifier argument cannot be an empty string.");
		checkNotNull(view, "CDO view argument cannot be null.");

		// Register the concept in the view object map if not already present
		CoreTerminologyBroker.getInstance().getLookupService(SnomedTerminologyComponentConstants.CONCEPT).getComponent(conceptId, view);
		
		String label = null;
		for (final Concept concept : getNewObjects(view, Concept.class)) {
			if (conceptId.equals(concept.getId())) {
				label = getPreferredTerm(view, concept.getDescriptions());
				break;
			}
		}
		
		if (StringUtils.isEmpty(label)) {
			label = getConceptLabel(conceptId);
		}
		
		if (StringUtils.isEmpty(label)) {
			//fall back to FSN
			for (final Concept concept : getNewObjects(view, Concept.class)) {
				if (conceptId.equals(concept.getId())) {
					label = getFsn(view, concept.getDescriptions());
					break;
				}
			}
		}
		
		return null == label ? conceptId : label;
	}
	
	/**
	 * Returns the fully specified name of the concept specified by its unique ID.
	 * Multiple FSNs are not handled, the first in the row is returned.
	 * Returns <code>null</code> if no FSN exists for the concept.
	 * 
	 * @param conceptId - the unique identifier of the concept
	 * @return - the fully specified name of the concept
	 */
	public String getFullySpecifiedName(final String conceptId) {
		checkNotNull(conceptId, "conceptId cannot be null");
		
		final SnomedClientIndexService snomedClientIndexService = ApplicationContext.getServiceForClass(SnomedClientIndexService.class);
		
		final SnomedDescriptionIndexQueryAdapter queryAdapter = SnomedDescriptionIndexQueryAdapter.findActiveDescriptionsByType(conceptId, Concepts.FULLY_SPECIFIED_NAME);
		final Collection<SnomedDescriptionIndexEntry> fsns = snomedClientIndexService.searchUnsorted(queryAdapter);
		
		if (!fsns.isEmpty()) {
			// ignore multi-FSN cases, return the first
			return Iterables.getFirst(fsns, null).getLabel();
		}
		
		return null;
	}
	
	/*returns with a human readable representation of the SNOMED CT concept's label*/
	/*note: this method may return with null if no label can be found for a SNOMED CT concept*/
	private String getConceptLabel(Object object) {
		
		if (object instanceof SnomedRefSet) {
			
			final SnomedRefSet refSet = (SnomedRefSet) object;
			object = new SnomedConceptLookupService().getComponent(refSet.getIdentifierId(), refSet.cdoView());
			
		} //try to look up concept be reference set identifier concept ID
		
		
		if (object instanceof SnomedRefSetMember) {
			
			final SnomedRefSetMember member = (SnomedRefSetMember) object;
			
			//if the referenced component is a SNOMED CT concept
			if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER == 
					SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(member.getReferencedComponentId())) {
				
				object = new SnomedConceptLookupService().getComponent(member.getReferencedComponentId(), member.cdoView());
				
			} else {

				//as the default behavior when we used to lookup concept PT based on referenced component ID
				object = member.getReferencedComponentId();
				
			}
			
		} //attempt to lookup concept by referenced component, if the referenced component is a concept
		
		
		if (object instanceof Concept) {
			final Concept concept = (Concept) object;
			String label = null;
			final IComponent<String> component = getComponent(concept.getId());
			if (null != component) {
				label = component.getLabel();
			}
			if (null == label) {
				label = getPreferredTerm(concept.cdoView(), concept.getDescriptions());
			}
			return label == null ? concept.getFullySpecifiedName() : label;
		} else if (object instanceof SnomedConceptIndexEntry) {
			return ((SnomedConceptIndexEntry) object).getLabel();
		} else if (object instanceof SnomedRefSetIndexEntry) {
			return ((SnomedRefSetIndexEntry) object).getLabel();
		} else if (object instanceof IStatus) {
			return ((IStatus) object).getMessage();
		} else if (object instanceof SnomedConceptIndexEntry) {
			return ((SnomedConceptIndexEntry) object).getLabel();
		} else if (object instanceof String) {
			final IComponent<String> concept = getComponent(String.valueOf(object));
			if (null != concept) {
				return concept.getLabel();
			}
		}
		return null;
	}
	
	/*retrieves a concept instance from terminology browser*/
	private IComponent<String> getComponent(final String id) {
		return getTerminologyBrowser().getConcept(id);
	}
	
	/*returns with the browser for the SNOMED CT terminology*/
	private IClientTerminologyBrowser<SnomedConceptIndexEntry, String> getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
	} 
	
	/*returns the preferred term of a concept if neither the concept itself nor its descriptions and the 
	 *associated reference set members have not been persisted yet*/
	private String getPreferredTerm(final CDOView view, final Iterable<Description> synonyms) {
		//all descriptions of the concept
		final Set<Description> descriptions = Sets.newHashSet(synonyms);
		
		//filter out inactive ones
		for (final Iterator<Description> itr = descriptions.iterator(); itr.hasNext(); /* */) {
			final Description description = itr.next();
			if (!description.isActive()) {
				itr.remove();
			}
		}
		
		//get a set of description type concept IDs that can be a preferred term
		final Set<String> availablePreferredTermIds = ApplicationContext.getInstance().getService(IClientSnomedComponentService.class).getAvailablePreferredTermIds();
		
		//filter out every description that has not got proper description type concept
		for (final Iterator<Description> itr = descriptions.iterator(); itr.hasNext(); /* */) {
			final Description description = itr.next();
			if (!availablePreferredTermIds.contains(getDescriptonTypeConceptId(description))) { //description cannot be a preferred term. e.g.: FSN
				itr.remove();
			}
		}
		
		//we have to find the first proper language type reference set member hanging on the description
		//language type reference set member is a proper one if:
		// - active
		// - acceptability is preferred
		// - containing reference set identifier concept ID is the selected one language reference set ID 
		
		final String languageRefSetId = getLanguageRefSetId();
		
		for (final Description description : descriptions) {
			for (final SnomedLanguageRefSetMember languageMember : description.getLanguageRefSetMembers()) {
				if (!languageMember.isActive()) { //keep searching if the member is not active
					continue;
				}
				
				if (!languageRefSetId.equals(languageMember.getRefSetIdentifierId())) { //we do not care about other languages but the selected one
					continue;
				}
				
				if (!Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(languageMember.getAcceptabilityId())) { //if not preferred keep searching
					continue;
				}
				return description.getTerm();
			}
		}
		return null;
	}

	/*returns with the ID of the language reference set identifier concept ID based on the selected language*/
	private String getLanguageRefSetId() {
		return ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId();
	}

	/*returns with the description type concept ID of the specified SNOMED CT description*/
	private String getDescriptonTypeConceptId(final Description description) {
		return description.getType().getId();
	}
	
	/*returns with the term of the first found active fsn*/
	private String getFsn(final CDOView view, final Iterable<Description> descriptions) {
		for (final Description description : descriptions) {
			if (!description.isActive()) {
				continue;
			}
			
			if (isFSN(description)) {
				return description.getTerm();
			}
		}
		return null;
	}

	/*returns true if the type of the description is a FSN*/
	private boolean isFSN(final Description description) {
		return isFSN(getDescriptonTypeConceptId(description));
	}
	
	/*returns true if the specified identifier equals with the fully specified name SNOMED CT concept identifier*/
	private boolean isFSN(final String id) {
		return Concepts.FULLY_SPECIFIED_NAME.equals(id);
	}

}