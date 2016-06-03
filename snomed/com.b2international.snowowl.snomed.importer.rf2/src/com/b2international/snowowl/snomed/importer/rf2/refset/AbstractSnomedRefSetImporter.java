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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongValueMap;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIds;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.importer.ImportAction;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.importer.rf2.csv.AbstractRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.model.AbstractSnomedImporter;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.collect.Maps;

public abstract class AbstractSnomedRefSetImporter<T extends AbstractRefSetRow, M extends SnomedRefSetMember> extends AbstractSnomedImporter<T, M> {

	private final Map<String, SnomedRefSet> refSetMap = Maps.newHashMap(); 
	
	public AbstractSnomedRefSetImporter(final SnomedImportConfiguration<T> importConfiguration, final SnomedImportContext importContext, 
			final InputStream releaseFileStream, final String releaseFileIdentifier) {
		
		super(importConfiguration, importContext, releaseFileStream, releaseFileIdentifier);
	}

	protected SnomedRefSetEditingContext getRefSetEditingContext() {
		return getImportContext().getEditingContext().getRefSetEditingContext();
	}
	
	@Override
	protected LongValueMap<String> getAvailableComponents(IndexSearcher index) throws IOException {
		final Query query = SnomedMappings.newQuery().memberRefSetType(getRefSetType()).matchAll();
		final DocIdCollector docIdCollector = new DocIdCollector(index.getIndexReader().maxDoc());
		index.search(query, docIdCollector);
		final DocIds docIDs = docIdCollector.getDocIDs();
		if (docIDs.size() <= 0) {
			return PrimitiveMaps.newObjectKeyLongOpenHashMap();
		} else {
			final LongValueMap<String> result = PrimitiveMaps.newObjectKeyLongOpenHashMapWithExpectedSize(docIDs.size());
			final DocIdsIterator it = docIDs.iterator();
			final Set<String> fields = SnomedMappings.fieldsToLoad().memberUuid().effectiveTime().build();
			while (it.next()) {
				final int docID = it.getDocID();
				final Document doc = index.doc(docID, fields);
				final String memberUuid = SnomedMappings.memberUuid().getValue(doc);
				final long effectiveTime = SnomedMappings.effectiveTime().getValue(doc);
				result.put(memberUuid, effectiveTime);
			}
			return result;
		}
	}

	@Override
	protected void importRow(final T currentRow) {
		
		if (getImportContext().isRefSetIgnored(currentRow.getRefSetId())) {
			// Member belongs to ignored reference set
			return;
		}
		
		final M currentMember = doImportRow(currentRow);
		
		if (null == currentMember) {
			return;
		}
		
		if (addToMembersList(currentMember)) {
			getImportContext().refSetVisited(currentRow.getRefSetId());
		}
	}

	protected abstract M doImportRow(final T currentRow);
	
	@Override
	protected Date getComponentEffectiveTime(M editedComponent) {
		return editedComponent.getEffectiveTime();
	}
	
	protected boolean addToMembersList(final M currentMember) {
		
		if (!(currentMember.getRefSet() instanceof SnomedRegularRefSet)) {
			final String message = MessageFormat.format("Couldn''t determine members list for member ''{0}''.", currentMember.getClass().getSimpleName());
			getLogger().warn(message);
			log(message);
			return false;
		}
		
		((SnomedRegularRefSet) currentMember.getRefSet()).getMembers().add(currentMember);
		return true;
	}
	
	protected void createIdentifierConceptIfNotExists(final String identifierId) throws ImportException {
		
		Concept identifierConcept = getConcept(identifierId);
		
		if (identifierConcept == null) {
			
			final Concept identiferParentConcept = getConcept(getIdentifierParentConceptId(identifierId));
			
			if (identiferParentConcept == null) {
				String message = MessageFormat.format("Reference set parent concept ''{0}'' not found in database.", 
						getIdentifierParentConceptId(identifierId));
				log("SNOMED CT import failed. " + message);
				throw new ImportException(message);
			}

			final SnomedEditingContext editingContext = getImportContext().getEditingContext();
			
			// Create identifier concept with explicit ID
			identifierConcept = editingContext.buildDefaultConcept(
					identifierId,
					getUnindentifiedRefSetFSN(identifierId), 
					identiferParentConcept);
			
			identifierConcept.getDescriptions().add( //add synonym
					editingContext.buildDefaultDescription(
							getUnindentifiedRefSetPT(identifierId), //PT 
							Concepts.SYNONYM)); //synonym description type
			
			//attempt to create proper language type reference set members for the concept
			String languageRefSetId = editingContext.getLanguageRefSetId();
			final SnomedRefSet languageRefSet = new SnomedRefSetLookupService().getComponent(languageRefSetId, editingContext.getTransaction());
			
			if (languageRefSet instanceof SnomedStructuralRefSet) {
				
				for (final Description description : identifierConcept.getDescriptions()) {
					//here we assume active descriptions.
					//one FSN and one synonym as PT
					
					final ComponentIdentifierPair<String> referencedComponentPair = SnomedRefSetEditingContext.createDescriptionTypePair(description.getId());
					final ComponentIdentifierPair<String> acceptabilityPair = SnomedRefSetEditingContext.createConceptTypePair(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
					final String moduleId = identifierConcept.getModule().getId();
					
					//set language type reference set member to the description
					description.getLanguageRefSetMembers().add(editingContext.getRefSetEditingContext().createLanguageRefSetMember(
							referencedComponentPair, 
							acceptabilityPair, 
							moduleId, 
							(SnomedStructuralRefSet) languageRefSet));
					
				}
			} else {
				String message = "Unable to create proper language type reference set members for concept: " + identifierConcept;
				getLogger().warn(message);
				log(message);
			}
			
			getImportContext().getComponentLookup().addNewComponent(identifierConcept, identifierId);
		}
	}

	private String getUnindentifiedRefSetFSN(final String identifierId) {
		return MessageFormat.format("{0} {1} (foundation metadata concept)", identifierId, getImportConfiguration().getType().getDisplayName());
	}
	
	private String getUnindentifiedRefSetPT(final String identifierId) {
		return MessageFormat.format("{0} {1}", identifierId, getImportConfiguration().getType().getDisplayName());
	}
	
	protected abstract String getIdentifierParentConceptId(String refSetId);

	/**
	 * Creates a reference set instance.
	 * <p>
	 * The default implementation picks a type according to a sample referenced
	 * component, subclasses should override to set the type, or do something
	 * completely different.
	 */
	protected SnomedRefSet createRefSet(final String identifierConceptId, final String referencedComponentId) {
		
		createIdentifierConceptIfNotExists(identifierConceptId);
		final SnomedRefSet refSet = createUninitializedRefSet(identifierConceptId);
		refSet.setIdentifierId(identifierConceptId);
		refSet.setReferencedComponentType(SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(referencedComponentId));
		refSet.setType(getRefSetType());
		initRefSet(refSet, referencedComponentId);
		return refSet;
	}

	protected SnomedRefSet createUninitializedRefSet(final String identifierConceptId) {
		return SnomedRefSetFactory.eINSTANCE.createSnomedRegularRefSet();
	}

	protected void initRefSet(final SnomedRefSet refSet, final String referencedComponentId) {
		// Subclasses should override
	}

	protected M getOrCreateMember(final UUID uuid) {
		
		@SuppressWarnings("unchecked")
		M member = (M) getImportContext().getRefSetMemberLookup().getMember(uuid);
			
		if (null == member) {
			member = createRefSetMember();
			member.setUuid(uuid.toString());
			getImportContext().getRefSetMemberLookup().addNewMember(member);
		}
				
		return member;
	}

	protected abstract M createRefSetMember();
	
	protected SnomedRefSet getOrCreateRefSet(final String refSetSctId, final String referencedComponentId) {
		
		SnomedRefSet refSet = refSetMap.get(refSetSctId);
		
		if (null == refSet) {
			refSet = getImportContext().getRefSetLookup().getComponent(refSetSctId);
		}
		
		if (null == refSet) {
			refSet = createRefSet(refSetSctId, referencedComponentId);
			refSetMap.put(refSetSctId, refSet);
			getImportContext().getRefSetLookup().addNewComponent(refSet, refSetSctId);
			getRefSetEditingContext().add(refSet);
		}
		
		return refSet;
	}
	
	private void clearRefSetMap() {
		refSetMap.clear();
	}
	
	@Override
	protected ImportAction commit(final SubMonitor subMonitor, final String formattedEffectiveTime) {
		final ImportAction result = super.commit(subMonitor, formattedEffectiveTime);
		clearRefSetMap();
		getImportContext().getRefSetMemberLookup().registerNewMembers();
		getImportContext().getRefSetLookup().registerNewComponents();
		getImportContext().getComponentLookup().registerNewComponents();
		return result;
	}

	protected abstract SnomedRefSetType getRefSetType();
}