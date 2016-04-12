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
package com.b2international.snowowl.snomed.datastore.taxonomy;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CDOCommitChangeSet;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedRelationshipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder.TaxonomyBuilderEdge;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder.TaxonomyBuilderNode;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * @since 4.7
 */
public class SnomedTaxonomyUpdateRunnable implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedTaxonomyUpdateRunnable.class);

	private static final Function<CDOObject, EClass> GET_ECLASS_FUNCTION = new Function<CDOObject, EClass>() {
		@Override public EClass apply(CDOObject input) {
			return input.eClass();
		}
	};
	
	private static Function<Component, String> GET_SCT_ID_FUNCTION = new Function<Component, String>() {
		@Override public String apply(final Component component) {
			return Preconditions.checkNotNull(component, "Component argument cannot be null.").getId();
		}
	};

	private ICDOCommitChangeSet commitChangeSet;
	private ISnomedTaxonomyBuilder taxonomyBuilder;
	private String characteristicTypeId;
	private IBranchPath branchPath;

	public SnomedTaxonomyUpdateRunnable(CDOTransaction transaction,
			ISnomedTaxonomyBuilder taxonomyBuilder, 
			String characteristicTypeId) {
		
		this(BranchPathUtils.createPath(transaction),
				new CDOCommitChangeSet(transaction, 
						transaction.getSession().getUserID(), 
						transaction.getCommitComment(), 
						transaction.getNewObjects().values(), 
						transaction.getDirtyObjects().values(), 
						Maps.transformValues(transaction.getDetachedObjects(), GET_ECLASS_FUNCTION), 
						transaction.getRevisionDeltas(), 
						-1L),
				taxonomyBuilder,
				characteristicTypeId);
	}
			
	public SnomedTaxonomyUpdateRunnable(IBranchPath branchPath, 
			ICDOCommitChangeSet commitChangeSet, 
			ISnomedTaxonomyBuilder taxonomyBuilder, 
			String characteristicTypeId) {

		this.branchPath = branchPath;
		this.commitChangeSet = commitChangeSet;
		this.taxonomyBuilder = taxonomyBuilder;
		this.characteristicTypeId = characteristicTypeId;
	}
	
	@Override 
	public void run() {
		
		LOGGER.info("Processing changes taxonomic information.");
		
		//here we have to consider changes triggered by repository state revert
		//this point the following might happen:
		//SNOMED CT concept and/or relationship will be contained by both deleted and new collections
		//with same business (SCT ID) but different primary ID (CDO ID) [this is the way how we handle object resurrection]
		//we decided, to order changes by primary keys. as primary IDs are provided in sequence, one could assume
		//that the larger primary ID happens later, and that is the truth
		
		//but as deletion always happens later than addition, we only have to take care of deletion
		//so if the deletion is about to erase something that has the same SCT ID but more recent (larger) 
		//primary key, we just ignore it when building the taxonomy.
		
		final Iterable<Concept> newConcepts = FluentIterable.from(commitChangeSet.getNewComponents()).filter(Concept.class);
		final Iterable<Concept> dirtyConcepts = FluentIterable.from(commitChangeSet.getDirtyComponents()).filter(Concept.class);
		final Iterable<CDOID> deletedConcepts = ChangeSetProcessorBase.getDetachedComponents(commitChangeSet, SnomedPackage.Literals.CONCEPT);
		final Iterable<Relationship> newRelationships = FluentIterable.from(commitChangeSet.getNewComponents()).filter(Relationship.class);
		final Iterable<Relationship> dirtyRelationships = FluentIterable.from(commitChangeSet.getDirtyComponents()).filter(Relationship.class);
		final Iterable<CDOID> deletedRelationships = ChangeSetProcessorBase.getDetachedComponents(commitChangeSet, SnomedPackage.Literals.RELATIONSHIP);
		
		//SCT ID - relationships
		final Map<String, Relationship> _newRelationships = Maps.newHashMap(Maps.uniqueIndex(newRelationships, GET_SCT_ID_FUNCTION));
		
		//SCT ID - concepts
		final Map<String, Concept> _newConcepts = Maps.newHashMap(Maps.uniqueIndex(newConcepts, GET_SCT_ID_FUNCTION));
		
		for (final Relationship newRelationship : newRelationships) {
			taxonomyBuilder.addEdge(createEdge(newRelationship));
		}
		
		for (final Relationship dirtyRelationship : dirtyRelationships) {
			taxonomyBuilder.addEdge(createEdge(dirtyRelationship));
		}
		
		for (final CDOID relationshipCdoId : deletedRelationships) {
			final long cdoId = CDOIDUtils.asLong(relationshipCdoId);
			final SnomedRelationshipIndexQueryAdapter queryAdapter = SnomedRelationshipIndexQueryAdapter.findByStorageKey(cdoId);
			final Iterable<SnomedRelationshipIndexEntry> results = getIndexService().search(branchPath, queryAdapter, 2);
			
			Preconditions.checkState(!CompareUtils.isEmpty(results), "No relationships were found with unique storage key: " + cdoId);
			Preconditions.checkState(Iterables.size(results) < 2, "More than one relationships were found with unique storage key: " + cdoId);
			
			final SnomedRelationshipIndexEntry relationship = Iterables.getOnlyElement(results);
			final String relationshipId = relationship.getId();
			//same relationship as new and detached
			if (_newRelationships.containsKey(relationshipId)) {
				final Relationship newRelationship = _newRelationships.get(relationshipId);
				final String typeId = newRelationship.getType().getId();
				//ignore everything but IS_As
				if (Concepts.IS_A.equals(typeId)) {
					//check source and destination as well
					if (relationship.getObjectId().equals(newRelationship.getSource().getId())
							&& relationship.getValueId().equals(newRelationship.getDestination().getId())) {
						
						//and if the new relationship has more recent (larger CDO ID), ignore deletion
						if (CDOIDUtils.asLong(newRelationship.cdoID()) > cdoId) {
							continue;
						}
					}
				}
			}
			taxonomyBuilder.removeEdge(createEdge(relationship));
		}
		for (final Concept newConcept : newConcepts) {
			taxonomyBuilder.addNode(createNode(newConcept));
		}
		for (final CDOID conceptCdoId : deletedConcepts) {
			
			//consider the same as for relationship
			//we have to decide if deletion is the 'stronger' modification or not
			final long cdoId = CDOIDUtils.asLong(conceptCdoId);
			final ExtendedComponent concept = getTerminologyBrowser().getExtendedComponent(branchPath, cdoId);
			checkState(concept != null, "No concepts were found with unique storage key: " + cdoId);
			final String conceptId = concept.getId();
			
			//same concept as addition and deletion
			if (_newConcepts.containsKey(conceptId)) {
				final Concept newConcept = _newConcepts.get(conceptId);
				//check whether new concept has more recent (larger CDO ID) or not, ignore deletion
				if (CDOIDUtils.asLong(newConcept.cdoID()) > cdoId) {
					continue;
				}
			}
			
			//else delete it
			taxonomyBuilder.removeNode(createDeletedNode(concept));
		}
		for (final Concept dirtyConcept : dirtyConcepts) {
			if (!dirtyConcept.isActive()) { //we do not need this concept. either it was deactivated now or sometime earlier.
				//nothing can be dirty and new at the same time
				taxonomyBuilder.removeNode(createNode(dirtyConcept.getId(), true));
			} else { //consider reverting inactivation
				if (!taxonomyBuilder.containsNode(dirtyConcept.getId())) {
					taxonomyBuilder.addNode(createNode(dirtyConcept));
				}
			}
		}
		LOGGER.info("Rebuilding taxonomic information based on the changes.");
		taxonomyBuilder.build();
	}
	
	/*returns with index service for SNOMED CT ontology*/
	private SnomedIndexService getIndexService() {
		return ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}
	
	/*returns with the terminology browser service. always represents the previous state of the SNOMED CT ontology*/
	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}
	
	/*creates a taxonomy edge instance based on the given SNOMED CT relationship*/
	private TaxonomyBuilderEdge createEdge(final Relationship relationship) {
		return new TaxonomyBuilderEdge() {
			@Override public boolean isCurrent() {
				return relationship.isActive();
			}
			@Override public String getId() {
				return relationship.getId();
			}
			@Override public boolean isValid() {
				return Concepts.IS_A.equals(relationship.getType().getId()) && characteristicTypeId.equals(relationship.getCharacteristicType().getId());
			}
			@Override public String getSoureId() {
				return relationship.getSource().getId();
			}
			@Override public String getDestinationId() {
				return relationship.getDestination().getId();
			}
		};
	}
	
	/*creates a taxonomy edge instance based on the given SNOMED CT relationship*/
	private TaxonomyBuilderEdge createEdge(final SnomedRelationshipIndexEntry relationship) {
		return new TaxonomyBuilderEdge() {
			@Override public boolean isCurrent() {
				return relationship.isActive();
			}
			@Override public String getId() {
				return relationship.getId();
			}
			@Override public boolean isValid() {
				return Concepts.IS_A.equals(relationship.getAttributeId()) && characteristicTypeId.equals(relationship.getCharacteristicTypeId());
			}
			@Override public String getSoureId() {
				return relationship.getObjectId();
			}
			@Override public String getDestinationId() {
				return relationship.getValueId();
			}
		};
	}
	
	/*creates and returns with a new taxonomy node instance based on the given SNOMED CT concept*/
	private TaxonomyBuilderNode createNode(final Concept concept) {
		return new TaxonomyBuilderNode() {
			@Override public boolean isCurrent() {
				return concept.isActive();
			}
			@Override public String getId() {
				return concept.getId();
			}
		};
	}

	/*creates and returns with a new taxonomy node instance based on the given SNOMED CT concept*/
	private TaxonomyBuilderNode createNode(final String id, final boolean active) {
		return new TaxonomyBuilderNode() {
			@Override public boolean isCurrent() {
				return active;
			}
			@Override public String getId() {
				return id;
			}
		};
	}
	
	private TaxonomyBuilderNode createDeletedNode(final ExtendedComponent concept) {
		return new TaxonomyBuilderNode() {
			@Override public boolean isCurrent() {
				throw new UnsupportedOperationException("This method should not be called when removing taxonomy nodes.");
			}
			@Override public String getId() {
				return concept.getId();
			}
		};
	}
}