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

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP;
import static com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter.createFindByRefSetTypeQuery;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.LANGUAGE;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOWithID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.datastore.browser.AbstractClientStatementBrowser;
import com.b2international.snowowl.datastore.browser.AbstractClientTerminologyBrowser;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionContainerQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter.SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @deprecated - UNSUPPORTED API, please use {@link SnomedRequests#prepareSearchMember()} instead
 */
public abstract class AbstractSnomedRefSetMembershipLookupService {

	protected abstract AbstractClientTerminologyBrowser<SnomedConceptDocument, String> getTerminologyBrowser();

	protected abstract AbstractClientStatementBrowser<SnomedConceptDocument, SnomedRelationshipIndexEntry, String> getStatementBrowser();

	/**
	 * Returns with all the active simple type reference set members where the referenced component is a SNOMED&nbsp;CT concept identified by its unique ID and the map target is an
	 * ATC concept.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @return a collection of simple map type reference sets representing mapping between SNOMED&nbsp;CT concept and ATC classification.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getAtcMappings(final String conceptId) {
		Preconditions.checkNotNull(conceptId, "SNOMED CT concept ID argument cannot be null.");
		final SnomedRefSetMembershipIndexQueryAdapter allMappingQuery = SnomedRefSetMembershipIndexQueryAdapter.createMappingMembershipQuery(CONCEPT, conceptId);
		final Collection<SnomedRefSetMemberIndexEntry> members = getIndexService().searchUnsorted(allMappingQuery);
		return Collections2.filter(members, new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry member) {
				if (!member.isActive()) {
					return false; //exclude inactive ones
				}

				if (SnomedRefSetUtil.isComplexMapping(member.getRefSetType())) {
					return false; //exclude complex and extended map type members
				}

				if (!"com.b2international.snowowl.terminology.atc.concept".equals(member.getMapTargetComponentType())) { //Snow Owl specific unique ID for identifying ATC concepts
					return false; //exclude members without ATC map target
				}

				return CONCEPT.equals(member.getReferencedComponentType()) && conceptId.equals(member.getReferencedComponentId()); //referenced component is the specified SNOMED CT concept  
			}
		});
	}

	/**
	 * Returns with a collection of language type {@link SnomedRefSetMemberIndexEntry reference set members} where the referenced description
	 * is the preferred term of the given SNOMED&nbsp;CT concept.
	 * @param concept the concept.
	 * @param languageRefSetId 
	 * @return a collection of language type reference set members referencing the preferred term of a given concept.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getPreferredTermMembers(final Concept concept, final String languageRefSetId) {

		final int limit = concept.getDescriptions().size();
		final Set<String> preferredIds = SnomedEditingContext.getAvailablePreferredTypeIds();
		final List<SnomedDescriptionIndexEntry> descriptions = getIndexService().search(SnomedDescriptionContainerQueryAdapter.createFindByConceptIds(concept.getId()), limit);

		final List<String> ids = Lists.newArrayList(ComponentUtils.getIds(Iterables.filter(descriptions, new Predicate<SnomedDescriptionIndexEntry>() {
			@Override public boolean apply(final SnomedDescriptionIndexEntry description) {
				return preferredIds.contains(description.getTypeId());
			}
		})));

		return ids.iterator().hasNext() 
				? getIndexService().search(SnomedRefSetMembershipIndexQueryAdapter.createFindPreferredTermMembersQuery(ids, languageRefSetId), limit) 
						: Sets.<SnomedRefSetMemberIndexEntry>newHashSet();
	}

	/**
	 * Returns with a collection of language type {@link SnomedRefSetMemberIndexEntry reference set members} where the referenced 
	 * descriptions are associated with the given concept argument.  
	 * @param concept the concept.
	 * @return a collection of language type reference set members referencing any description of a particular concept.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getLanguageMembers(final Concept concept) {
		final Set<String> descriptionIds = Sets.newHashSet(getDescriptionIds(concept));
		return descriptionIds.isEmpty()
				? Collections.<SnomedRefSetMemberIndexEntry>emptyList()
						: getIndexService().searchUnsorted(createFindByRefSetTypeQuery(DESCRIPTION, wrapArguments(LANGUAGE), descriptionIds));
	}

	/**
	 * Returns with all data type reference set members referencing a SNOMED&nbsp;CT concept.
	 * @return all data type reference set members referencing a concept.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getConceptConcreteDataTypes() {
		final IIndexQueryAdapter<SnomedRefSetMemberIndexEntry> createFindByRefSetTypeQuery = 
				SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter.createFindByRefSetTypeQuery(CONCEPT);
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery);
	}

	/**
	 * Returns with all data type reference set members referencing a SNOMED&nbsp;CT relationship.
	 * @return all data type reference set members referencing a relationship.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getRelationshipConcreteDataTypes() {
		final IIndexQueryAdapter<SnomedRefSetMemberIndexEntry> createFindByRefSetTypeQuery = 
				SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter.createFindByRefSetTypeQuery(RELATIONSHIP);
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery);
	}

	/**
	 * Returns with a collection of active data type {@link SnomedRefSetMemberIndexEntry reference set members} referencing the given SNOMED CT
	 * concept.
	 * 
	 * @param conceptId the unique ID of the concept.
	 * @return a collection of data type reference set members.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getActiveConceptDataTypes(final String conceptId) {
		final IIndexQueryAdapter<SnomedRefSetMemberIndexEntry> createFindByRefSetTypeQuery = SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter
				.createFindActivesByReferencedComponentIdQuery(CONCEPT, conceptId);
		// XXX The number of allowed concrete domain datatypes are maximized in 100 for a concept
		return getIndexService().search(createFindByRefSetTypeQuery, 100);
	}

	/**
	 * Returns with a collection of data type {@link SnomedRefSetMemberIndexEntry reference set members} where the referenced relationships are given
	 * as the relationship ID argument.
	 * 
	 * @param relationshipIds the relationship IDs.
	 * @return a collection of data type reference set members referencing the given relationships.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getRelationshipDataTypes(final String... relationshipIds) {
		if (0 == relationshipIds.length) {
			return Collections.<SnomedRefSetMemberIndexEntry> emptyList();
		}
		final IIndexQueryAdapter<SnomedRefSetMemberIndexEntry> createFindByRefSetTypeQuery = SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter
				.createFindActivesByReferencedComponentIdsQuery(RELATIONSHIP, wrapArguments(relationshipIds));
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery);
	}

	private <T> List<T> wrapArguments(@SuppressWarnings("unchecked") final T... args) {
		return Lists.newArrayList(args);
	}

	private Iterable<String> getDescriptionIds(final Concept concept) {

		try {
			concept.getDescriptions().get(0);
		} catch (final ObjectNotFoundException e) {
			return Collections.emptyList(); //for detached concepts
		}

		final CDOList descriptions = ((InternalCDORevision) concept.cdoRevision()).getList(com.b2international.snowowl.snomed.SnomedPackage.eINSTANCE.getConcept_Descriptions());

		final List<String> descriptionIds = Lists.newArrayList();

		for (final Object description : descriptions) {

			if (description instanceof CDOWithID) {

				final CDOID cdoID = ((CDOWithID) description).cdoID();
				final CDOObject object = CDOUtils.getObjectIfExists(concept.cdoView(), cdoID);

				if (object instanceof Description) {

					descriptionIds.add(((Description) object).getId());

				} 

			} else if (description instanceof CDOID) {

				final CDOObject object = CDOUtils.getObjectIfExists(concept.cdoView(), (CDOID) description);

				if (object instanceof Description) {

					descriptionIds.add(((Description) object).getId());

				}

			}

		}

		return descriptionIds;
	}

}