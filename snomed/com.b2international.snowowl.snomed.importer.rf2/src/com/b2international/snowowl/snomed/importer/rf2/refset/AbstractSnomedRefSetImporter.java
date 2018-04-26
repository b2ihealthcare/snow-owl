/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.core.runtime.SubMonitor;

import com.b2international.index.query.Expression;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.importer.ImportAction;
import com.b2international.snowowl.snomed.importer.ImportException;
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
import com.google.common.collect.Iterables;

public abstract class AbstractSnomedRefSetImporter<T extends AbstractRefSetRow, M extends SnomedRefSetMember> extends AbstractSnomedImporter<T, M> {

	public AbstractSnomedRefSetImporter(final SnomedImportConfiguration<T> importConfiguration, final SnomedImportContext importContext, 
			final InputStream releaseFileStream, final String releaseFileIdentifier) {
		
		super(importConfiguration, importContext, releaseFileStream, releaseFileIdentifier);
	}

	protected final SnomedRefSetEditingContext getRefSetEditingContext() {
		return getImportContext().getEditingContext().getRefSetEditingContext();
	}

	@Override
	protected final Expression getAvailableComponentQuery() {
		return SnomedRefSetMemberIndexEntry.Expressions.refSetTypes(Collections.singleton(getRefSetType()));
	}
	
	@Override
	protected final Class<? extends SnomedDocument> getType() {
		return SnomedRefSetMemberIndexEntry.class;
	}
	
	@Override
	protected final Function<T, String> getRowIdMapper() {
		return row -> row.getUuid().toString();
	}
	
	@Override
	protected final Function<M, String> getComponentIdMapper() {
		return SnomedRefSetMember::getUuid;
	}
	
	@Override
	protected final Predicate<T> getRowFilter() {
		return row -> !getImportContext().isRefSetIgnored(row.getRefSetId());
	}
	
	@Override
	protected final Date getComponentEffectiveTime(M editedComponent) {
		return editedComponent.getEffectiveTime();
	}
	
	@Override
	protected M getNewComponent(String componentId) {
		return getImportContext().getRefSetMemberLookup().getNewMember(componentId);
	}
	
	@Override
	protected void attach(Collection<M> componentsToAttach) {
		for (M member : componentsToAttach) {
			if (member.getRefSet() instanceof SnomedRegularRefSet) {
				SnomedRegularRefSet refSet = (SnomedRegularRefSet) member.getRefSet();
				refSet.getMembers().add(member);
			} else {
				throw new IllegalStateException("Trying to attach structural member, but couldn't find its proper place: " + member.getRefSet().getType() + " | " + member.getUuid());
			}
		}
	}
	
	@Override
	protected final Collection<M> loadComponents(Set<String> componentIds) {
		return getImportContext().getRefSetMemberLookup().getMembers(componentIds);
	}
	
	protected void createIdentifierConceptIfNotExists(final String identifierId) throws ImportException {
		
		Concept identifierConcept = (Concept) Iterables.getOnlyElement(getComponents(Collections.singleton(identifierId)), null);
		
		if (identifierConcept == null) {
			
			final Concept identiferParentConcept = (Concept) Iterables.getOnlyElement(getComponents(Collections.singleton(getIdentifierParentConceptId(identifierId))), null);
			
			if (identiferParentConcept == null) {
				String message = MessageFormat.format("Reference set parent concept ''{0}'' not found in database.", 
						getIdentifierParentConceptId(identifierId));
				log("SNOMED CT import failed. {}", message);
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
			final SnomedRefSet languageRefSet = editingContext.lookup(Concepts.REFSET_LANGUAGE_TYPE_UK, SnomedRefSet.class);
			
			if (languageRefSet instanceof SnomedStructuralRefSet) {
				
				for (final Description description : identifierConcept.getDescriptions()) {
					//here we assume active descriptions.
					//one FSN and one synonym as PT
					
					final String referencedComponentId = description.getId();
					final String acceptabilityId = Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED;
					final String moduleId = identifierConcept.getModule().getId();
					
					//set language type reference set member to the description
					description.getLanguageRefSetMembers().add(editingContext.getRefSetEditingContext().createLanguageRefSetMember(
							referencedComponentId, 
							acceptabilityId, 
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
	protected final SnomedRefSet createRefSet(final String identifierConceptId, final String referencedComponentId) {
		
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
	
	@Override
	protected void registerNewComponent(M component) {
		getImportContext().getRefSetMemberLookup().addNewMember(component);
	}

	@Override
	protected final M createComponent(String memberId) {
		final M member = createMember();
		member.setUuid(memberId);
		return member;
	}
	
	protected abstract M createMember();
	
	protected SnomedRefSet getOrCreateRefSet(final String refSetSctId, final String referencedComponentId) {
		
		SnomedRefSet refSet = Iterables.getOnlyElement(getImportContext().getRefSetLookup().getComponents(Collections.singleton(refSetSctId)), null);
		
		if (null == refSet) {
			refSet = createRefSet(refSetSctId, referencedComponentId);
			getImportContext().getRefSetLookup().addNewComponent(refSet, refSetSctId);
			getRefSetEditingContext().add(refSet);
		}
		
		return refSet;
	}
	
	@Override
	protected ImportAction commit(final SubMonitor subMonitor, final String formattedEffectiveTime) {
		final ImportAction result = super.commit(subMonitor, formattedEffectiveTime);
		getImportContext().getRefSetMemberLookup().registerNewMemberStorageKeys();
		getImportContext().getRefSetLookup().registerNewComponentStorageKeys();
		getImportContext().getComponentLookup().registerNewComponentStorageKeys();
		return result;
	}

	protected abstract SnomedRefSetType getRefSetType();
}