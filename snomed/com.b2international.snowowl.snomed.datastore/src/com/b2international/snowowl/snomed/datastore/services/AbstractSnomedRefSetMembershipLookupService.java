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
import static com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexQueryAdapter.findByConceptId;
import static com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter.createFindByRefSetIdQuery;
import static com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter.createFindByRefSetTypeQuery;
import static com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter.createFindReferencingMembers;
import static com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter.createMappingMembershipQuery;
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
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IRefSetMembershipLookupService;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.datastore.browser.AbstractClientStatementBrowser;
import com.b2international.snowowl.datastore.browser.AbstractClientTerminologyBrowser;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.AbstractClientIndexService;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionContainerQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedConcreteDataTypeRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter.SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 */
public abstract class AbstractSnomedRefSetMembershipLookupService implements IRefSetMembershipLookupService<String> {

	protected abstract AbstractClientIndexService<SnomedIndexEntry> getIndexService();

	protected abstract AbstractClientTerminologyBrowser<SnomedConceptIndexEntry, String> getTerminologyBrowser();

	protected abstract AbstractClientStatementBrowser<SnomedConceptIndexEntry, SnomedRelationshipIndexEntry, String> getStatementBrowser();

	@Override
	public Collection<IIndexEntry> getMembers(final String terminologyComponentId, final String componentId) {
		final Collection<SnomedRefSetMemberIndexEntry> results = getIndexService().searchUnsorted(createMappingMembershipQuery(terminologyComponentId, componentId));
		final Set<IIndexEntry> $ = Sets.newHashSetWithExpectedSize(results.size());
		$.addAll(results);

		return $;
	}

	/**
	 * Returns with a collection of active {@link SnomedRefSetMemberIndexEntry reference set members} where the referenced component is the given 
	 * SNOMED&nbsp;CT concept.
	 * @param conceptId the concept ID.
	 * @return a collection of active reference set members referring the given concept.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getReferringMembers(final String conceptId) {
		final Collection<SnomedRefSetMemberIndexEntry> members = getIndexService().searchUnsorted(createFindReferencingMembers(conceptId));
		return Collections.unmodifiableCollection(Sets.newHashSet(members));
	}

	/**
	 * Returns with a collection of active mapping {@link SnomedRefSetMemberIndexEntry reference set members} where the map source (referenced component) is the given 
	 * SNOMED&nbsp;CT concept.
	 * @param conceptId the concept ID.
	 * @return a collection of active mapping reference set members referring the given concept as the map source.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getMappedMembers(final String conceptId) {
		final Collection<SnomedRefSetMemberIndexEntry> members = getIndexService().searchUnsorted(createMappingMembershipQuery(SnomedTerminologyComponentConstants.CONCEPT, conceptId));
		return Collections.unmodifiableCollection(Sets.newHashSet(members));
	}

	/**
	 * Returns with a collection of {@link SnomedRefSetMemberIndexEntry reference set members} whose application specific terminology component identifier, 
	 * the unique component ID and the container reference set identifier concept ID matches with the given arguments.
	 * @param terminologyComponentId the application specific terminology component ID.
	 * @param refSetId an iterable of possible container reference set IDs.
	 * @param componentId the unique component ID.
	 * @return a collection of {@link SnomedRefSetMemberIndexEntry reference set members}.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getMembers(final String terminologyComponentId, final Iterable<String> refSetId, final String componentId) {
		return getIndexService().searchUnsorted(createFindByRefSetIdQuery(terminologyComponentId, refSetId, wrapArguments(componentId)));
	}

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

				if (!"com.b2international.snowowl.terminology.atc.concept".equals(member.getSpecialFieldComponentType())) { //Snow Owl specific unique ID for identifying ATC concepts
					return false; //exclude members without ATC map target
				}

				return CONCEPT.equals(member.getReferencedComponentType()) && conceptId.equals(member.getReferencedComponentId()); //referenced component is the specified SNOMED CT concept  
			}
		});
	}

	/**
	 * Returns with a collection of lightweight representation of the SNOMED CT reference set members that matches with the specified arguments.
	 * @param terminologyComponentId the terminology component ID of the referenced component.
	 * @param types the types of the SNOMED CT reference sets.
	 * @param componentIds the unique identifiers of the referenced components.
	 * @return the looked up reference set members.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getMembersForType(final String terminologyComponentId, final Iterable<SnomedRefSetType> types, final Iterable<String> componentIds) {
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery(terminologyComponentId, types, componentIds));
	}

	/**
	 * Returns with a collection of lightweight representation of the SNOMED CT reference set members that matches with the specified arguments.
	 * @param terminologyComponentId the terminology component ID of the referenced component.
	 * @param types the types of the SNOMED CT reference sets.
	 * @param componentId the unique identifiers of the referenced component.
	 * @return the looked up reference set members.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getMembersForType(final String terminologyComponentId, final Iterable<SnomedRefSetType> types, final String componentId) {
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery(terminologyComponentId, types, wrapArguments(componentId)));
	}

	/**
	 * Returns with a collection of lightweight representation of the SNOMED CT reference set members that matches with the specified arguments. The returning result set
	 * size could be limited.
	 * @param terminologyComponentId the terminology component ID of the referenced component.
	 * @param types the types of the SNOMED CT reference sets.
	 * @param componentId the unique identifiers of the referenced component.
	 * @param limit the result set limit.
	 * @return the looked up reference set members.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getMembersForType(final String terminologyComponentId, final Iterable<SnomedRefSetType> types, final String componentId, final int limit) {
		return getIndexService().search(createFindByRefSetTypeQuery(terminologyComponentId, types, wrapArguments(componentId)), limit);
	}

	/**
	 * Returns with a collection of {@link SnomedRefSetMemberIndexEntry reference set members} where the referenced descriptions
	 * are associated with the given SNOMED&nbsp;CT concept identifier.
	 * @param refSetIds the container reference set identifier concept ID.
	 * @param conceptId the unique SNOMED&nbsp;CT concept ID.
	 * @return a collection of reference set members referencing a bunch of descriptions associated with a given concept. 
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getDescriptionMembers(final Iterable<String> refSetIds, final String conceptId) {
		return getIndexService().searchUnsorted(createFindByRefSetIdQuery(DESCRIPTION, refSetIds, getDescriptionIds(conceptId)));
	}

	/**
	 * Can be used for querying the members of e.g. the <i>Dose Form Synonym Plural</i> mapping reference set, where Descriptions are mapped to Strings.
	 * @param refSetId the reference set identifier concept ID.
	 * @param descriptionId the unique SNOMED&nbsp;CT description ID.
	 * @return returns with all reference set members from the given reference set referencing the given description.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getDescriptionMembers(final String refSetId, final String descriptionId) {
		return getIndexService().searchUnsorted(createFindByRefSetIdQuery(DESCRIPTION, wrapArguments(refSetId), wrapArguments(descriptionId)));
	}

	/**
	 * Returns with a collection of {@link SnomedRefSetMemberIndexEntry reference set members} where any of the referenced description
	 * is associated with a particular SNOMED&nbsp;CT concept given by its unique ID.
	 * @param types the allowed reference set members types.
	 * @param conceptId the concept ID.
	 * @return a collection of reference set members referencing any description of a given concept.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getDescriptionMembersByType(final Iterable<SnomedRefSetType> types, final String conceptId) {
		final Set<String> descriptionIds = getDescriptionIds(conceptId);
		return descriptionIds.isEmpty()
				? Collections.<SnomedRefSetMemberIndexEntry>emptyList()
						: getIndexService().searchUnsorted(createFindByRefSetTypeQuery(DESCRIPTION, types, descriptionIds));
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
	 * Returns a collection of {@link SnomedRefSetMemberIndexEntry reference set members} where the referenced component is the specified
	 * description and the type is {@link SnomedRefSetType#LANGUAGE}.
	 * @param descriptionId the description identifier to retrieve language reference set members for (may not be {@code null})
	 * @return a collection of language reference set members for the specified description
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getLanguageMembersForDescription(final String descriptionId) {
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery(DESCRIPTION, wrapArguments(LANGUAGE), wrapArguments(descriptionId)));
	}

	/**
	 * Returns with a collection of {@link SnomedRefSetMemberIndexEntry reference set members} referencing any of the given relationships.
	 * @param refSetIds the container reference set identifier concept IDs.
	 * @param componentIds the IDs of the relationships referenced by the reference set members.
	 * @return the reference set members referencing a subset of relationships.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getRelationshipMembers(final Iterable<String> refSetIds, final Iterable<String> componentIds) {
		return getIndexService().searchUnsorted(createFindByRefSetIdQuery(RELATIONSHIP, refSetIds, componentIds));
	}

	/**
	 * Returns with a collection of {@link SnomedRefSetMemberIndexEntry reference set members} referencing any relationship of a given SNOMED&nbsp;CT concept.
	 * @param refSetIds the container reference set identifier concept IDs.
	 * @param conceptId the unique ID of the concept.
	 * @return a collection of reference set
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getRelationshipMembers(final Iterable<String> refSetIds, final String conceptId) {
		final Set<String> relationshipIds = Sets.newHashSet(getRelationshipIds(conceptId));
		return relationshipIds.isEmpty()
				? Collections.<SnomedRefSetMemberIndexEntry>emptyList()
						: getIndexService().searchUnsorted(createFindByRefSetIdQuery(RELATIONSHIP, refSetIds, relationshipIds));
	}

	/**
	 * Returns with a collection of {@link SnomedRefSetMemberIndexEntry reference set members} referencing any of the given relationships.
	 * @param types the allowed reference set member types.
	 * @param componentIds the IDs of the relationships referenced by the reference set members.
	 * @return the reference set members referencing a subset of relationships.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getRelationshipMembersByTypes(final Iterable<SnomedRefSetType> types, final Iterable<String> componentIds) {
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery(RELATIONSHIP, types, componentIds));
	}

	/**
	 * Returns with a collection of {@link SnomedRefSetMemberIndexEntry reference set members} referencing a bunch of relationships 
	 * associated with a concept given by its unique ID.
	 * @param types the allowed reference set member types.
	 * @param conceptId the unique ID of the concept. 
	 * @return a collection of reference set members referencing any relationship of a concept.
	 */
	public Collection<SnomedRefSetMemberIndexEntry> getRelationshipMembersByType(final Iterable<SnomedRefSetType> types, final String conceptId) {
		final Set<String> relationshipIds = Sets.newHashSet(getRelationshipIds(conceptId));
		return relationshipIds.isEmpty()
				? Collections.<SnomedRefSetMemberIndexEntry>emptyList()
						: getIndexService().searchUnsorted(createFindByRefSetTypeQuery(RELATIONSHIP, types, relationshipIds));
	}

	/**
	 * Returns with all data type reference set members from the SNOMED&nbsp;CT ontology.
	 * @return all data type reference set members.
	 */
	public Collection<SnomedConcreteDataTypeRefSetMemberIndexEntry> getAllConcreteDataTypes() {
		final IIndexQueryAdapter<SnomedConcreteDataTypeRefSetMemberIndexEntry> createFindByRefSetTypeQuery = 
				SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter.createFindByRefSetTypeQuery();
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery);
	}

	/**
	 * Returns with all data type reference set members referencing a SNOMED&nbsp;CT concept.
	 * @return all data type reference set members referencing a concept.
	 */
	public Collection<SnomedConcreteDataTypeRefSetMemberIndexEntry> getConceptConcreteDataTypes() {
		final IIndexQueryAdapter<SnomedConcreteDataTypeRefSetMemberIndexEntry> createFindByRefSetTypeQuery = 
				SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter.createFindByRefSetTypeQuery(CONCEPT);
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery);
	}

	/**
	 * Returns with all data type reference set members referencing a SNOMED&nbsp;CT relationship.
	 * @return all data type reference set members referencing a relationship.
	 */
	public Collection<SnomedConcreteDataTypeRefSetMemberIndexEntry> getRelationshipConcreteDataTypes() {
		final IIndexQueryAdapter<SnomedConcreteDataTypeRefSetMemberIndexEntry> createFindByRefSetTypeQuery = 
				SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter.createFindByRefSetTypeQuery(RELATIONSHIP);
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery);
	}

	/**
	 * Returns with a collection of data type {@link SnomedRefSetMemberIndexEntry reference set members} referencing the
	 * given SNOMED&nbsp;CT concept.
	 * @param conceptId the unique ID of the concept.
	 * @return a collection of data type reference set members.
	 */
	public Collection<SnomedConcreteDataTypeRefSetMemberIndexEntry> getConceptDataTypes(final String conceptId) {
		final IIndexQueryAdapter<SnomedConcreteDataTypeRefSetMemberIndexEntry> createFindByRefSetTypeQuery = 
				SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter.createFindByReferencedComponentIdsQuery(
						CONCEPT, 
						wrapArguments(conceptId));
		//XXX we maximum 100 CDT is associated with a concept
		return getIndexService().search(createFindByRefSetTypeQuery, 100);
	}

	/**
	 * Returns with a collection of data type {@link SnomedRefSetMemberIndexEntry reference set members} where the referenced relationships 
	 * are given as the relationship ID argument.
	 * @param relationshipIds the relationship IDs.
	 * @return a collection of data type reference set members referencing the given relationships.
	 */
	public Collection<SnomedConcreteDataTypeRefSetMemberIndexEntry> getRelationshipDataTypes(final String... relationshipIds) {
		if (0 == relationshipIds.length)
			return Lists.newArrayList();

		final IIndexQueryAdapter<SnomedConcreteDataTypeRefSetMemberIndexEntry> createFindByRefSetTypeQuery = 
				SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter.createFindByReferencedComponentIdsQuery(
						RELATIONSHIP, 
						wrapArguments(relationshipIds));
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery);
	}

	/**
	 * Returns with a collection of data type {@link SnomedRefSetMemberIndexEntry reference set members} where the referenced components
	 * are either source or destination relationship of a particular concept given via its unique SNOMED&nbsp;CT identifier.
	 * @param conceptId the unique ID of the concept.
	 * @return a collection of data type reference set members.
	 */
	public Collection<SnomedConcreteDataTypeRefSetMemberIndexEntry> getRelationshipDataTypesForConcept(final String conceptId) {
		final Set<String> relationshipIds = Sets.newHashSet(getRelationshipIds(conceptId));
		if (relationshipIds.isEmpty())
			return Collections.<SnomedConcreteDataTypeRefSetMemberIndexEntry>emptyList();

		final IIndexQueryAdapter<SnomedConcreteDataTypeRefSetMemberIndexEntry> createFindByRefSetTypeQuery = 
				SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter.createFindByReferencedComponentIdsQuery(
						RELATIONSHIP, 
						relationshipIds);
		return getIndexService().searchUnsorted(createFindByRefSetTypeQuery);
	}

	/**
	 * Returns with the {@link IBranchPath branch path} where the current service is acting on.
	 * @return the branch path for the current service.
	 */
	public IBranchPath getBranchPath() {
		return getIndexService().getBranchPath();
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

	private Set<String> getDescriptionIds(final String conceptId) {
		return ComponentUtils.getIdSet(getIndexService().searchUnsorted(findByConceptId(conceptId)));
	}

	private Iterable<String> getRelationshipIds(final String conceptId) {
		final SnomedConceptIndexEntry concept = getTerminologyBrowser().getConcept(conceptId);
		if (null == concept)
			return Lists.newArrayList();
		return ComponentUtils.getIds(getStatementBrowser().getOutboundStatements(concept));
	}
}