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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.domain.DelegatingBranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponentBuilder;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.core.store.SnomedMemberBuilder;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 6.0.0
 */
final class Rf2TransactionContext extends DelegatingBranchContext implements TransactionContext {

	private final Map<Pair<String, Class<? extends EObject>>, CDOObject> newComponents = newHashMap();
	
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
		CDOEditingContext context = service(CDOEditingContext.class);
		try {
			if (context.isDirty()) {
				System.err.println("Pushing changes: " + commitComment);
				return getDelegate().commit(userId, commitComment, parentContextDescription);
			} else {
				return -1L;
			}
		} finally {
			context.clearCache();
		} 
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
		// add concepts to tx context
		for (Pair<String, Class<? extends EObject>> key : newComponents.keySet()) {
			if (Concept.class.isAssignableFrom(key.getSecond())) {
				final Concept newConcept = (Concept) newComponents.get(key);
				add(newConcept);
			}
		}
		
		newComponents.clear();
		
		// XXX do NOT invoke preCommit on the delegate, RF2 import does not check ID uniqueness and module dependencies the standard way
	}
	
	@Override
	public <T extends EObject> T lookup(String componentId, Class<T> type) throws ComponentNotFoundException {
		final Pair<String, Class<? extends EObject>> key = createComponentKey(componentId, type);
		if (newComponents.containsKey(key)) {
			return type.cast(newComponents.get(key));
		} else {
			return getDelegate().lookup(componentId, type);
		}
	}
	
	@Override
	public <T extends EObject> Map<String, T> lookup(Collection<String> componentIds, Class<T> type) {
		final Map<String, T> resolvedComponentById = newHashMap();
		Set<String> unresolvedComponentIds = newHashSet();
		
		for (String componentId : componentIds) {
			final Pair<String, Class<? extends EObject>> key = createComponentKey(componentId, type);
			if (newComponents.containsKey(key)) {
				resolvedComponentById.put(componentId, type.cast(newComponents.get(key)));
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
	
	private Pair<String, Class<? extends EObject>> createComponentKey(final String componentId, Class<? extends EObject> type) {
		return Tuples.pair(componentId, type);
	}
	
	void add(Collection<SnomedComponent> componentChanges, Multimap<Class<? extends CDOObject>, String> dependenciesByType) {
		final Multimap<Class<? extends CDOObject>, SnomedComponent> componentChangesByType = Multimaps.index(componentChanges, this::getCdoType);
		final List<Class<? extends CDOObject>> typeToImportInOrder = ImmutableList.of(Concept.class, Description.class, Relationship.class, SnomedRefSetMember.class);
		for (Class<? extends CDOObject> type : typeToImportInOrder) {
			final Collection<SnomedComponent> rf2Components = componentChangesByType.get(type);
			final Set<String> componentsToFetch = rf2Components.stream().map(IComponent::getId).collect(Collectors.toSet());
			// add all dependencies with the same type
			componentsToFetch.addAll(dependenciesByType.get(type));
			
			final Map<String, ? extends CDOObject> existingComponents = lookup(componentsToFetch, type);
			final Map<String, ? extends SnomedRefSet> existingRefSets;
			if (SnomedRefSetMember.class == type) {
				existingRefSets = lookup(rf2Components.stream().map(member -> ((SnomedReferenceSetMember) member).getReferenceSetId()).collect(Collectors.toSet()), SnomedRefSet.class);
			} else {
				existingRefSets = Collections.emptyMap();
			}

			// seed missing component before applying row changes
			// and check for existing components with the same or greater effective time and skip them
			final Collection<SnomedComponent> componentsToImport = newArrayList();
			for (SnomedComponent rf2Component : rf2Components) {
				CDOObject existingObject = existingComponents.get(rf2Component.getId());
				if (existingObject == null) {
					// new component, add to new components and apply row
					if (rf2Component instanceof SnomedCoreComponent) {
						existingObject = createCoreComponent(rf2Component.getId(), type);
					} else if (rf2Component instanceof SnomedReferenceSetMember) {
						final SnomedReferenceSetMember member = (SnomedReferenceSetMember) rf2Component;
						existingObject = createMember(rf2Component.getId(), member.type());
						// seed the refset if missing
						Pair<String, Class<? extends EObject>> refSetKey = createComponentKey(member.getReferenceSetId(), SnomedRefSet.class);
						if (!existingRefSets.containsKey(member.getReferenceSetId()) && !newComponents.containsKey(refSetKey)) {
							final String referencedComponentType = SnomedTerminologyComponentConstants.getTerminologyComponentId(member.getReferencedComponent().getId());
							String mapTargetComponentType = CoreTerminologyBroker.UNSPECIFIED;
							try {
								mapTargetComponentType = SnomedTerminologyComponentConstants.getTerminologyComponentId((String) member.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET));
							} catch (IllegalArgumentException e) {
								// ignored
							}
							SnomedRequests.prepareNewRefSet()
								.setIdentifierId(member.getReferenceSetId())
								.setType(member.type())
								.setReferencedComponentType(referencedComponentType)
								.setMapTargetComponentType(mapTargetComponentType)
								.build()
								.execute(this);
							
							newComponents.put(refSetKey, lookup(member.getReferenceSetId(), SnomedRefSet.class));
						}
					} else {
						throw new UnsupportedOperationException("Unsupported component: " + rf2Component);
					}
					newComponents.put(createComponentKey(rf2Component.getId(), type), existingObject);
					componentsToImport.add(rf2Component);
				} else if (existingObject instanceof Component && rf2Component instanceof SnomedCoreComponent) {
					final SnomedCoreComponent rf2Row = (SnomedCoreComponent) rf2Component;
					final Component existingRow = (Component) existingObject;
					if (rf2Row.getEffectiveTime().after(existingRow.getEffectiveTime())) {
						componentsToImport.add(rf2Component);
					}
				} else if (existingObject instanceof SnomedRefSetMember && rf2Component instanceof SnomedReferenceSetMember) {
					final SnomedReferenceSetMember rf2Row = (SnomedReferenceSetMember) rf2Component;
					final SnomedRefSetMember existingRow = (SnomedRefSetMember) existingObject;
					if (rf2Row.getEffectiveTime().after(existingRow.getEffectiveTime())) {
						componentsToImport.add(rf2Component);
					}
				}
			}
			
			// apply row changes
			for (SnomedComponent rf2Component : componentsToImport) {
				CDOObject existingObject = existingComponents.get(rf2Component.getId());
				if (existingObject == null) {
					existingObject = newComponents.get(createComponentKey(rf2Component.getId(), type));
				}
				final SnomedComponentBuilder builder;
				if (rf2Component instanceof SnomedCoreComponent) {
					builder = prepareCoreComponent(rf2Component);
				} else if (rf2Component instanceof SnomedReferenceSetMember) {
					builder = prepareMember((SnomedReferenceSetMember) rf2Component);
				} else {
					throw new UnsupportedOperationException("Unsupported component: " + rf2Component);
				}
				builder.init(existingObject, this);
				if (builder instanceof SnomedMemberBuilder) {
					((SnomedMemberBuilder) builder).addTo(this);
				}
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
		} else if (component instanceof SnomedReferenceSetMember) {
			return SnomedRefSetMember.class;
		}
		throw new UnsupportedOperationException("Unsupported component: " + component.getClass().getSimpleName());
	}
	
	private CDOObject createCoreComponent(String componentId, Class<?> type) {
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
	
	private SnomedComponentBuilder<?, ?> prepareCoreComponent(SnomedComponent component) {
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
			throw new UnsupportedOperationException("Cannot prepare unknown core component: " + component);
		}
	}
	
	private SnomedComponentBuilder<?, ?> prepareMember(SnomedReferenceSetMember rf2Component) {
		final Map<String, Object> properties = rf2Component.getProperties();
		SnomedMemberBuilder<?, ?> builder;
		switch (rf2Component.type()) {
		case ASSOCIATION: 
			builder = SnomedComponents.newAssociationMember()
					.withTargetComponentId((String) properties.get(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID));
			break;
		case ATTRIBUTE_VALUE:
			builder = SnomedComponents.newAttributeValueMember()
					.withValueId((String) properties.get(SnomedRf2Headers.FIELD_VALUE_ID));
			break;
		case DESCRIPTION_TYPE: 
			builder = SnomedComponents.newDescriptionTypeMember()
					.withDescriptionFormatId((String) properties.get(SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT))
					.withDescriptionLength((Integer) properties.get(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH));
			break;
		case COMPLEX_MAP:
		case EXTENDED_MAP:
			builder = SnomedComponents.newComplexMapMember()
					.withGroup((Integer) properties.get(SnomedRf2Headers.FIELD_MAP_GROUP))
					.withPriority((Integer) properties.get(SnomedRf2Headers.FIELD_MAP_PRIORITY))
					.withMapAdvice((String) properties.get(SnomedRf2Headers.FIELD_MAP_ADVICE))
					.withCorrelationId((String) properties.get(SnomedRf2Headers.FIELD_CORRELATION_ID))
					.withMapCategoryId((String) properties.get(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID))
					.withMapRule((String) properties.get(SnomedRf2Headers.FIELD_MAP_RULE))
					.withMapTargetId((String) properties.get(SnomedRf2Headers.FIELD_MAP_TARGET))
					.withMapTargetDescription((String) properties.get(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION));
			break;
		case LANGUAGE: 
			builder = SnomedComponents.newLanguageMember()
					.withAcceptability(Acceptability.getByConceptId((String) properties.get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID)));
			break;
		case SIMPLE_MAP: 
			builder = SnomedComponents.newSimpleMapMember()
					.withMapTargetId((String) properties.get(SnomedRf2Headers.FIELD_MAP_TARGET))
					.withMapTargetDescription((String) properties.get(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION));
			break;
		case MODULE_DEPENDENCY:
			builder = SnomedComponents.newModuleDependencyMember()
					.withSourceEffectiveTime((Date) properties.get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME))
					.withTargetEffectiveTime((Date) properties.get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
			break;
		case SIMPLE:
			builder = SnomedComponents.newSimpleMember();
			break;
		default: throw new UnsupportedOperationException("Unknown refset member type: " + rf2Component.type());
		}
		return builder
				.withId(rf2Component.getId())
				.withActive(rf2Component.isActive())
				.withEffectiveTime(rf2Component.getEffectiveTime())
				.withModule(rf2Component.getModuleId())
				.withReferencedComponent(rf2Component.getReferencedComponent().getId())
				.withRefSet(rf2Component.getReferenceSetId());
	}
	
}
