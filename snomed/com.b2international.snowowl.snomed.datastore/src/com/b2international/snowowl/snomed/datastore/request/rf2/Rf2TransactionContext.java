/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.domain.DelegatingBranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponentBuilder;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.impl.ConceptImpl;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;

/**
 * @since 6.0.0
 */
final class Rf2TransactionContext extends DelegatingBranchContext implements TransactionContext {

	private final Map<String, CDOObject> newComponents = newHashMap();
	
	Rf2TransactionContext(TransactionContext context) {
		super(context);
		((SnomedEditingContext) context.service(CDOEditingContext.class)).setUniquenessCheckEnabled(false);
	}

	@Override
	protected TransactionContext getDelegate() {
		return (TransactionContext) super.getDelegate();
	}
	
	@Override
	public void add(EObject o) {
		getDelegate().add(o);
	}
	
	@Override
	public void clearContents() {
		getDelegate().clearContents();
	}
	
	@Override
	public long commit(String userId, String commitComment, String parentContextDescription) {
		return getDelegate().commit(userId, commitComment, parentContextDescription);
	}
	
	@Override
	public void delete(EObject o) {
		getDelegate().delete(o);
	}
	
	@Override
	public void delete(EObject o, boolean force) {
		getDelegate().delete(o, force);
	}
	
	@Override
	public void rollback() {
		getDelegate().rollback();
	}
	
	@Override
	public void close() throws Exception {
		getDelegate().close();
	}

	@Override
	public <T extends EObject> T lookupIfExists(String componentId, Class<T> type) {
		return getDelegate().lookupIfExists(componentId, type);
	}
	
	@Override
	public void preCommit() {
		Multimap<Class<? extends CDOObject>, String> resolvableComponentsByType = HashMultimap.create();
		newComponents.forEach((id, component) -> resolvableComponentsByType.put(component.getClass(), id));

		// add concepts to 
		for (String conceptId : resolvableComponentsByType.get(ConceptImpl.class)) {
			final Concept newConcept = (Concept) newComponents.get(conceptId);
			add(newConcept);
		}
		
		newComponents.clear();

		getDelegate().preCommit();
	}
	
	@Override
	public <T extends EObject> T lookup(String componentId, Class<T> type) throws ComponentNotFoundException {
		if (newComponents.containsKey(componentId)) {
			return type.cast(newComponents.get(componentId));
		} else {
			return getDelegate().lookup(componentId, type);
		}
	}
	
	@Override
	public <T extends EObject> Map<String, T> lookup(Collection<String> componentIds, Class<T> type) {
		final Map<String, T> resolvedComponentById = newHashMap();
		Set<String> unresolvedComponentIds = newHashSet();
		
		for (String componentId : componentIds) {
			if (newComponents.containsKey(componentId)) {
				resolvedComponentById.put(componentId, type.cast(newComponents.get(componentId)));
			} else {
				unresolvedComponentIds.add(componentId);
			}
		}
		
		if (!unresolvedComponentIds.isEmpty()) {
			// load any unresolved components via lookup
			resolvedComponentById.putAll(getDelegate().lookup(unresolvedComponentIds, type));
		}
		
		return resolvedComponentById;
	}
	
	void add(Collection<SnomedComponent> componentChanges) {
		final Multimap<Class<? extends CDOObject>, SnomedComponent> componentChangesByType = Multimaps.index(componentChanges, this::getCdoType);
		final List<Class<? extends CDOObject>> typeToImportInOrder = Ordering.natural()
				.<Class<? extends CDOObject>>onResultOf(this::getTypeRank)
				.sortedCopy(componentChangesByType.keySet());
		for (Class<? extends CDOObject> type : typeToImportInOrder) {
			Collection<SnomedComponent> rf2Components = componentChangesByType.get(type);
			final Set<String> componentIds = rf2Components.stream().map(IComponent::getId).collect(Collectors.toSet());
			final Map<String, ? extends CDOObject> existingComponents = lookup(componentIds, type);

			// seed missing component before applying row changes
			for (SnomedComponent rf2Component : rf2Components) {
				CDOObject existingObject = existingComponents.get(rf2Component.getId());
				if (existingObject == null) {
					// new component, add to new components and apply row
					if (rf2Component instanceof SnomedCoreComponent) {
						existingObject = createCoreComponent(rf2Component.getId(), type);
					} else if (rf2Component instanceof SnomedReferenceSetMember) {
						existingObject = createMember(rf2Component.getId(), ((SnomedReferenceSetMember) rf2Component).type());
					}
					newComponents.put(rf2Component.getId(), existingObject);
				}
			}
			
			for (SnomedComponent rf2Component : rf2Components) {
				CDOObject existingObject = existingComponents.get(rf2Component.getId());
				if (existingObject == null) {
					existingObject = newComponents.get(rf2Component.getId());
				}
				final SnomedComponentBuilder builder = prepareComponent(rf2Component);
				builder.init(existingObject, this);
			}
		}
	}

	private Class<? extends CDOObject> getCdoType(SnomedComponent component) {
		if (component instanceof SnomedConcept) {
			return Concept.class;
		} else if (component instanceof SnomedDescription) {
			return Description.class;
		} else if (component instanceof SnomedRelationship) {
			return Relationship.class;
		}
		throw new UnsupportedOperationException("Unsupported component: " + component.getClass().getSimpleName());
	}
	
	private int getTypeRank(Class<? extends CDOObject> type) {
		if (Concept.class == type) {
			return 0;
		} else if (Description.class == type) {
			return 1;
		} else if (Relationship.class == type) {
			return 2;
		} else if (SnomedRefSetMember.class == type) {
			return 3;
		} else {
			return 4;
		}
	}

	private <T extends EObject> CDOObject createCoreComponent(String componentId, Class<T> type) {
		if (type.isAssignableFrom(Concept.class)) {
			return SnomedFactory.eINSTANCE.createConcept();
		} else if (type.isAssignableFrom(Description.class)) {
			return SnomedFactory.eINSTANCE.createDescription();
		} else if (type.isAssignableFrom(Relationship.class)) {
			return SnomedFactory.eINSTANCE.createRelationship();
		} else {
			throw new UnsupportedOperationException("Unknown core component type: " + type);
		}
	}
	
	private SnomedRefSetMember createMember(String memberId, SnomedRefSetType type) {
		switch (type) {
		case ASSOCIATION: 
			return SnomedRefSetFactory.eINSTANCE.createSnomedAssociationRefSetMember();
		case ATTRIBUTE_VALUE: 
			return SnomedRefSetFactory.eINSTANCE.createSnomedAttributeValueRefSetMember();
		case DESCRIPTION_TYPE: 
			return SnomedRefSetFactory.eINSTANCE.createSnomedDescriptionTypeRefSetMember();
		case COMPLEX_MAP:
		case EXTENDED_MAP: 
			return SnomedRefSetFactory.eINSTANCE.createSnomedComplexMapRefSetMember();
		case LANGUAGE: 
			return SnomedRefSetFactory.eINSTANCE.createSnomedLanguageRefSetMember();
		case SIMPLE_MAP: 
			return SnomedRefSetFactory.eINSTANCE.createSnomedSimpleMapRefSetMember();
		case MODULE_DEPENDENCY:
			return SnomedRefSetFactory.eINSTANCE.createSnomedModuleDependencyRefSetMember();
		case SIMPLE:
			return SnomedRefSetFactory.eINSTANCE.createSnomedRefSetMember();
		default: throw new UnsupportedOperationException("Unknown refset member type: " + type);
		}
	}
	
	private SnomedComponentBuilder<?, ?> prepareComponent(SnomedComponent component) {
		if (component instanceof SnomedConcept) {
			SnomedConcept concept = (SnomedConcept) component;
			return SnomedComponents.newConcept()
					.withId(component.getId())
					.withActive(concept.isActive())
					.withEffectiveTime(concept.getEffectiveTime())
					.withModule(concept.getModuleId())
					.withDefinitionStatus(concept.getDefinitionStatus())
					.withExhaustive(concept.getSubclassDefinitionStatus().isExhaustive());
		} else if (component instanceof SnomedDescription) { 
			SnomedDescription description = (SnomedDescription) component;
			return SnomedComponents.newDescription()
					.withId(component.getId())
					.withActive(description.isActive())
					.withEffectiveTime(description.getEffectiveTime())
					.withModule(description.getModuleId())
					.withCaseSignificance(description.getCaseSignificance())
					.withLanguageCode(description.getLanguageCode())
					.withType(description.getTypeId())
					.withTerm(description.getTerm())
					.withConcept(description.getConceptId());
		} else if (component instanceof SnomedRelationship) {
			SnomedRelationship relationship = (SnomedRelationship) component;
			return SnomedComponents.newRelationship()
					.withId(component.getId())
					.withActive(relationship.isActive())
					.withEffectiveTime(relationship.getEffectiveTime())
					.withModule(relationship.getModuleId())
					.withSource(relationship.getSourceId())
					.withType(relationship.getTypeId())
					.withDestination(relationship.getDestinationId())
					.withCharacteristicType(relationship.getCharacteristicType())
					.withGroup(relationship.getGroup())
					.withUnionGroup(relationship.getUnionGroup())
					.withDestinationNegated(false)
					.withModifier(relationship.getModifier());
		} else {
			throw new UnsupportedOperationException("Cannot prepare unknown component: " + component);
		}
	}
	
}
