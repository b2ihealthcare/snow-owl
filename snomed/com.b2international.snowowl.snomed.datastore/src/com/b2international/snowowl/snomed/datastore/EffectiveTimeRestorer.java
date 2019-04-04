/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.UnexpectedTypeException;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedOWLExpressionRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Longs;

/**
 * @since 6.14
 */
public final class EffectiveTimeRestorer {
	
	public void restoreEffectiveTimes(Iterable<CDOObject> componentsToRestore, String branchPath) {
		final Multimap<Class<?>, EObject> componentsByType = ArrayListMultimap.create();
		componentsToRestore
		.forEach(object -> {
			if (object instanceof Component && ((Component) object).isReleased() && !((Component) object).isSetEffectiveTime()) {
				componentsByType.put(object.eClass().getInstanceClass(), object);
			}
			if (object instanceof SnomedRefSetMember && ((SnomedRefSetMember) object).isReleased() && !((SnomedRefSetMember) object).isSetEffectiveTime()) {
				componentsByType.put(SnomedRefSetMember.class, object);
			}
		});
		if (componentsByType.isEmpty())	{
			return;
		}
		
		final List<String> branchesForPreviousVersion = getAvailableVersionPaths(branchPath);
		if (branchesForPreviousVersion.isEmpty()) {
			return;
		}
		
		for (String branch : branchesForPreviousVersion) {
			for (Class<?> componentType : ImmutableSet.copyOf(componentsByType.keySet())) {
				final Set<String> componentIds = componentsByType.get(componentType).stream().map(object -> getId(object)).collect(Collectors.toSet());
				final Map<String, ? extends IComponent> previousVersions = Maps.uniqueIndex(fetchPreviousVersions(componentIds, componentType, branch), IComponent::getId);
				for (EObject object : ImmutableList.copyOf(componentsByType.get(componentType))) {
					final String id = getId(object);
					final IComponent previousVersion = previousVersions.get(id);
					if (previousVersion != null) {
						if (canRestoreEffectiveTime(object, previousVersion)) {
							restoreEffectiveTime(object, previousVersion);
						}
						componentsByType.remove(componentType, object);
					}
				}
			}
		}
		
		if (!componentsByType.isEmpty()) {
			throw new IllegalStateException("There were components which could not be restored: " + componentsByType.toString());
		}
	}
	
	private boolean canRestoreEffectiveTime(EObject componentToRestore, Object previousVersion) {
		if (componentToRestore instanceof Component && previousVersion instanceof SnomedCoreComponent) {
			if (componentToRestore instanceof Concept && previousVersion instanceof SnomedConcept) {
				final Concept conceptToRestore = (Concept) componentToRestore;
				final SnomedConcept previousConcept = (SnomedConcept) previousVersion;
				return conceptToRestore.isActive() == previousConcept.isActive()
						&& conceptToRestore.getModule().getId().equals(previousConcept.getModuleId())
						&& conceptToRestore.getDefinitionStatus().getId().equals(previousConcept.getDefinitionStatus().getConceptId());
						
			} else if (componentToRestore instanceof Description && previousVersion instanceof SnomedDescription) {
				final Description descriptionToRestore = (Description) componentToRestore;
				final SnomedDescription previousDescription = (SnomedDescription) previousVersion;
				return descriptionToRestore.isActive() == previousDescription.isActive()
						&& descriptionToRestore.getModule().getId().equals(previousDescription.getModuleId()) 
						&& descriptionToRestore.getTerm().equals(previousDescription.getTerm()) 
						&& descriptionToRestore.getCaseSignificance().getId().equals(previousDescription.getCaseSignificance().getConceptId());
				
			} else if (componentToRestore instanceof Relationship && previousVersion instanceof SnomedRelationship) {
				final Relationship relationshipToRestore = (Relationship) componentToRestore;
				final SnomedRelationship previousRelationship = (SnomedRelationship) previousVersion;
						return relationshipToRestore.isActive() == previousRelationship.isActive()
						&& relationshipToRestore.getModule().getId().equals(previousRelationship.getModuleId())
						&& relationshipToRestore.getGroup() == previousRelationship.getGroup().intValue() 
						&& relationshipToRestore.getUnionGroup() == previousRelationship.getUnionGroup().intValue() 
						&& relationshipToRestore.getCharacteristicType().getId().equals(previousRelationship.getCharacteristicType().getConceptId())
						&& relationshipToRestore.getModifier().getId().equals(previousRelationship.getModifier().getConceptId());	
				
			} else {
				throw new UnexpectedTypeException("Unexpected component type '" + componentToRestore.getClass() + "'.");
			}
		} else if (componentToRestore instanceof SnomedRefSetMember && previousVersion instanceof SnomedReferenceSetMember) {
			final SnomedReferenceSetMember previousMember = (SnomedReferenceSetMember) previousVersion;
			if (componentToRestore instanceof SnomedAssociationRefSetMember) {
				final SnomedAssociationRefSetMember memberToRestore = (SnomedAssociationRefSetMember) componentToRestore;
				return memberToRestore.isActive() == previousMember.isActive()
						&& memberToRestore.getModuleId().equals(previousMember.getModuleId()) 
						&& memberToRestore.getTargetComponentId().equals(((SnomedConcept) previousMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_COMPONENT)).getId());
			} else if(componentToRestore instanceof SnomedAttributeValueRefSetMember) {
				final SnomedAttributeValueRefSetMember memberToRestore = (SnomedAttributeValueRefSetMember) componentToRestore;
						return memberToRestore.isActive() == previousMember.isActive()
						&& memberToRestore.getModuleId().equals(previousMember.getModuleId())
						&& memberToRestore.getValueId().equals((String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID));
			} else if (componentToRestore instanceof SnomedConcreteDataTypeRefSetMember) {
				final SnomedConcreteDataTypeRefSetMember memberToRestore = (SnomedConcreteDataTypeRefSetMember) componentToRestore;
				return memberToRestore.isActive() == previousMember.isActive() 
						&& memberToRestore.getModuleId().equals(previousMember.getModuleId())
						&& memberToRestore.getSerializedValue().equals(previousMember.getProperties().get(SnomedRf2Headers.FIELD_VALUE))
						&& memberToRestore.getCharacteristicTypeId().equals(previousMember.getProperties().get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID))
						&& memberToRestore.getTypeId().equals(previousMember.getProperties().get(SnomedRf2Headers.FIELD_TYPE_ID))
						&& memberToRestore.getGroup() == ((Integer )previousMember.getProperties().get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP)).intValue();
			} else if(componentToRestore instanceof SnomedDescriptionTypeRefSetMember) {
				final SnomedDescriptionTypeRefSetMember memberToRestore = (SnomedDescriptionTypeRefSetMember) componentToRestore;
				return memberToRestore.isActive() == previousMember.isActive() 
						&& memberToRestore.getModuleId().equals(previousMember.getModuleId())
						&& memberToRestore.getDescriptionFormat().equals(previousMember.getProperties().get(SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT))
						&& memberToRestore.getDescriptionLength() == ((Integer) previousMember.getProperties().get(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH)).intValue();
			} else if (componentToRestore instanceof SnomedLanguageRefSetMember) {
				final SnomedLanguageRefSetMember memberToRestore = (SnomedLanguageRefSetMember) componentToRestore;
				return memberToRestore.isActive() == previousMember.isActive()
						&& memberToRestore.getModuleId().equals(previousMember.getModuleId())
						&& memberToRestore.getAcceptabilityId().equals(previousMember.getProperties().get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID));
			} else if (componentToRestore instanceof SnomedModuleDependencyRefSetMember) {
				final SnomedModuleDependencyRefSetMember memberToRestore = (SnomedModuleDependencyRefSetMember) componentToRestore;
				
				if (previousMember.getProperties().containsKey(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME)) {
					final Date previousSourceEffectiveDate = (Date) previousMember.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME);

					if (!Objects.equals(previousSourceEffectiveDate, memberToRestore.getSourceEffectiveTime())) {
						return false;
					}
				}

				if (previousMember.getProperties().containsKey(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME)) {
					final Date previousTargetEffectiveDate = (Date) previousMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME);

					if (!Objects.equals(previousTargetEffectiveDate, memberToRestore.getTargetEffectiveTime())) {
						return false;
					}
				}

				return memberToRestore.isActive() == previousMember.isActive() && memberToRestore.getModuleId().equals(previousMember.getModuleId());
			} else if (componentToRestore instanceof SnomedMRCMAttributeDomainRefSetMember) {
				final SnomedMRCMAttributeDomainRefSetMember memberToRestore = (SnomedMRCMAttributeDomainRefSetMember) componentToRestore;
				final Boolean previousGrouped = ClassUtils.checkAndCast(previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_GROUPED), Boolean.class);		
				final String previousAttributeCardinality = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY);
				final String previousAttributeInGroupCardinality = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY);
				final String previousRuleStrengthId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID);
				final String previousContentTypeId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID);
				
				if (previousGrouped != null && previousGrouped.booleanValue() != memberToRestore.isGrouped()) {
					return false;
				}

				if (!Objects.equals(previousAttributeCardinality, memberToRestore.getAttributeCardinality())) {
					return false;
				}

				if (!Objects.equals(previousAttributeInGroupCardinality, memberToRestore.getAttributeInGroupCardinality())) {
					return false;
				}

				if (!Objects.equals(previousRuleStrengthId, memberToRestore.getRuleStrengthId())) {
					return false;
				}

				if (!Objects.equals(previousContentTypeId, memberToRestore.getContentTypeId())) {
					return false;
				}

				return memberToRestore.isActive() == previousMember.isActive() && memberToRestore.getModuleId().equals(previousMember.getModuleId());
			} else if (componentToRestore instanceof SnomedMRCMAttributeRangeRefSetMember) {
				final SnomedMRCMAttributeRangeRefSetMember memberToRestore = (SnomedMRCMAttributeRangeRefSetMember) componentToRestore;
				final String previousRangedConstraint = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT);
				final String previousAttributeRule = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE);
				final String previousRuleStrengthId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID);
				final String previousContentTypeId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID);

				if (!Objects.equals(previousRangedConstraint, memberToRestore.getRangeConstraint())) {
					return false;
				}

				if (!Objects.equals(previousAttributeRule, memberToRestore.getAttributeRule())) {
					return false;
				}

				if (!Objects.equals(previousRuleStrengthId, memberToRestore.getRuleStrengthId())) {
					return false;
				}

				if (!Objects.equals(previousContentTypeId, memberToRestore.getContentTypeId())) {
					return false;
				}

				return memberToRestore.isActive() == previousMember.isActive() && memberToRestore.getModuleId().equals(previousMember.getModuleId());
			} else if (componentToRestore instanceof SnomedMRCMDomainRefSetMember) {
				final SnomedMRCMDomainRefSetMember memberToRestore = (SnomedMRCMDomainRefSetMember) componentToRestore;
				final String previousDomainConstraint = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT);
				final String previousParentDomain = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN);
				final String previousProximalPrimitiveConstraint = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT);
				final String previousProximalPrimitiveRefinement = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT);
				final String previousDomainTemplateForPrecoordination = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION);
				final String previousDomainTemplateForPostcoordination = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION);
				final String previousEditorialGuideReference = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE);

				if (!Objects.equals(previousDomainConstraint, memberToRestore.getDomainConstraint())) {
					return false;
				}

				if (!Objects.equals(previousParentDomain, memberToRestore.getParentDomain())) {
					return false;
				}

				if (!Objects.equals(previousProximalPrimitiveConstraint, memberToRestore.getProximalPrimitiveConstraint())) {
					return false;
				}

				if (!Objects.equals(previousProximalPrimitiveRefinement, memberToRestore.getProximalPrimitiveRefinement())) {
					return false;
				}

				if (!Objects.equals(previousDomainTemplateForPrecoordination, memberToRestore.getDomainTemplateForPrecoordination())) {
					return false;
				}

				if (!Objects.equals(previousDomainTemplateForPostcoordination, memberToRestore.getDomainTemplateForPostcoordination())) {
					return false;
				}

				if (!Objects.equals(previousEditorialGuideReference, memberToRestore.getEditorialGuideReference())) {
					return false;
				}

				return memberToRestore.isActive() == previousMember.isActive() && memberToRestore.getModuleId().equals(previousMember.getModuleId());
			} else if (componentToRestore instanceof SnomedMRCMModuleScopeRefSetMember) {
				final SnomedMRCMModuleScopeRefSetMember memberToRestore = (SnomedMRCMModuleScopeRefSetMember) componentToRestore;
				
				return memberToRestore.isActive() == previousMember.isActive()
						&& memberToRestore.getModuleId().equals(previousMember.getModuleId())
						&& memberToRestore.getMrcmRuleRefsetId().equals(previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID));
			} else if (componentToRestore instanceof SnomedOWLExpressionRefSetMember) {
				final SnomedOWLExpressionRefSetMember memberToRestore = (SnomedOWLExpressionRefSetMember) componentToRestore;
				return memberToRestore.isActive() == previousMember.isActive()
						&& memberToRestore.getModuleId().equals(previousMember.getModuleId())
						&& memberToRestore.getOwlExpression().equals(previousMember.getProperties().get(SnomedRf2Headers.FIELD_OWL_EXPRESSION));
			} else if (componentToRestore instanceof SnomedQueryRefSetMember) {
				final SnomedQueryRefSetMember memberToRestore = (SnomedQueryRefSetMember) componentToRestore;
				return memberToRestore.isActive() == previousMember.isActive()
						&& memberToRestore.getModuleId().equals(previousMember.getModuleId())
						&& memberToRestore.getQuery().equals((String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_QUERY));
			} else if (componentToRestore instanceof SnomedComplexMapRefSetMember) {
				final SnomedComplexMapRefSetMember memberToRestore = (SnomedComplexMapRefSetMember) componentToRestore;
				final String previousMapTargetId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET);
				final Integer previousMapGroup = (Integer) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_GROUP);
				final Integer previousMapPriority = (Integer) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_PRIORITY);
				final String previousMapRule = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_RULE);
				final String previousMapAdvice = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_ADVICE);
				final String previousCorrelationId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_CORRELATION_ID);
				final String previousMapCategoryId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID);

				if (!Objects.equals(previousMapTargetId, memberToRestore.getMapTargetComponentId())) {
					return false;
				}

				if (previousMapGroup != null && previousMapGroup.intValue() != memberToRestore.getMapGroup()) {
					return false;
				}

				if (previousMapPriority != null && previousMapPriority.intValue() != memberToRestore.getMapPriority()) {
					return false;
				}

				if (!Objects.equals(previousMapRule, memberToRestore.getMapRule())) {
					return false;
				}

				if (!Objects.equals(previousMapAdvice, memberToRestore.getMapAdvice())) {
					return false;
				}

				if (!Objects.equals(previousCorrelationId, memberToRestore.getCorrelationId())) {
					return false;
				}
				
				// Handle extended map
				if (!Objects.equals(memberToRestore.getMapCategoryId(), previousMapCategoryId)) {
					return false;
				}
				
				return memberToRestore.isActive() == previousMember.isActive() && memberToRestore.getModuleId().equals(previousMember.getModuleId());
			} else if (componentToRestore instanceof SnomedSimpleMapRefSetMember) {
				final SnomedSimpleMapRefSetMember memberToRestore = (SnomedSimpleMapRefSetMember) componentToRestore;
				final String previousMapTargetDescription = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION);
				
				if (!Objects.equals(memberToRestore.getMapTargetComponentDescription(), previousMapTargetDescription)) {
					return false;
				}
				
				return memberToRestore.isActive() == previousMember.isActive()
						&& memberToRestore.getModuleId().equals(previousMember.getModuleId()) 
						&& memberToRestore.getMapTargetComponentId().equals(previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET));
						
			}
			if (componentToRestore.eClass().equals(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER)) {
				// Simple type reference set
				final SnomedRefSetMember memberToRestore = (SnomedRefSetMember) componentToRestore;
				return memberToRestore.isActive() == previousMember.isActive() && memberToRestore.getModuleId().equals(previousMember.getModuleId());
			} else {
				return false;
			}
		} else {
			throw new UnexpectedTypeException("Unexpected member type '" + componentToRestore.getClass() + "'.");
		}
	}
	
	private void restoreEffectiveTime(EObject componentToRestore, IComponent previousVersion) {
		if (componentToRestore instanceof Component && previousVersion instanceof SnomedComponent) {
			((Component) componentToRestore).setEffectiveTime(((SnomedComponent) previousVersion).getEffectiveTime());
		} else if (componentToRestore instanceof SnomedRefSetMember && previousVersion instanceof SnomedComponent) {
			((SnomedRefSetMember) componentToRestore).setEffectiveTime(((SnomedComponent) previousVersion).getEffectiveTime());
		}
	}
	
	private String getId(EObject object) {
		if (object instanceof Component) {
			final Component component = (Component) object;
			return component.getId();
		} else if (object instanceof SnomedRefSetMember) {
			final SnomedRefSetMember member = (SnomedRefSetMember) object;
			return member.getUuid();
		}
		
		// XXX: Should never happen
		throw new IllegalArgumentException("Object was neither instance of Component or SnomedRefSetMember");
	}
	
	private List<String> getAvailableVersionPaths(String branchPath) {
		final IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
		final CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(eventBus)
				.getSync();

		final Map<String, CodeSystemEntry> codeSystemsByMainBranch = Maps.uniqueIndex(codeSystems, CodeSystemEntry::getBranchPath);

		final List<CodeSystemEntry> relativeCodeSystems = Lists.newArrayList();

		final Iterator<IBranchPath> bottomToTop = BranchPathUtils.bottomToTopIterator(BranchPathUtils.createPath(branchPath));

		while (bottomToTop.hasNext()) {
			final IBranchPath candidate = bottomToTop.next();
			if (codeSystemsByMainBranch.containsKey(candidate.getPath())) {
				relativeCodeSystems.add(codeSystemsByMainBranch.get(candidate.getPath()));
			}
		}
		if (relativeCodeSystems.isEmpty()) {
			throw new IllegalStateException("No relative code system has been found for branch '" + branchPath + "'");
		}

		// the first code system in the list is the working codesystem
		final CodeSystemEntry workingCodeSystem = relativeCodeSystems.stream().findFirst().get();

		final Optional<CodeSystemVersionEntry> workingCodeSystemVersion = CodeSystemRequests.prepareSearchCodeSystemVersion()
				.one()
				.filterByCodeSystemShortName(workingCodeSystem.getShortName())
				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(eventBus)
				.getSync()
				.first();

		final List<CodeSystemVersionEntry> relativeCodeSystemVersions = Lists.newArrayList();

		if (workingCodeSystemVersion.isPresent() && !Strings.isNullOrEmpty(workingCodeSystemVersion.get().getPath())) {
			relativeCodeSystemVersions.add(workingCodeSystemVersion.get());
		}

		if (relativeCodeSystems.size() > 1) {

			relativeCodeSystems.stream().skip(1).forEach(codeSystem -> {

				final Map<String, CodeSystemVersionEntry> pathToVersionMap = CodeSystemRequests.prepareSearchCodeSystemVersion()
						.all()
						.filterByCodeSystemShortName(codeSystem.getShortName())
						.build(SnomedDatastoreActivator.REPOSITORY_UUID)
						.execute(eventBus)
						.getSync()
						.stream()
						.collect(Collectors.toMap(version -> version.getPath(), v -> v));

				final Iterator<IBranchPath> branchPathIterator = BranchPathUtils.bottomToTopIterator(BranchPathUtils.createPath(branchPath));

				while (branchPathIterator.hasNext()) {
					final IBranchPath candidate = branchPathIterator.next();
					if (pathToVersionMap.containsKey(candidate.getPath())) {
						relativeCodeSystemVersions.add(pathToVersionMap.get(candidate.getPath()));
						break;
					}
				}

			});

		}

		return relativeCodeSystemVersions.stream()
				// sort versions by effective date in reversed order
				.sorted((v1, v2) -> Longs.compare(v2.getEffectiveDate(), v1.getEffectiveDate()))
				.map(CodeSystemVersionEntry::getPath).collect(Collectors.toList());
	}
	
	private Iterable<? extends IComponent> fetchPreviousVersions(Set<String> ids, Class<?> componentType, String branch) {
		if (Concept.class.isAssignableFrom(componentType)) {
			return SnomedRequests.prepareSearchConcept()
					.all()
					.filterByIds(ids)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
					.get();
		} else if (Description.class.isAssignableFrom(componentType)) {
			return SnomedRequests.prepareSearchDescription()
					.all()
					.filterByIds(ids)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
					.get();
		} else if (Relationship.class.isAssignableFrom(componentType)) {
			return SnomedRequests.prepareSearchRelationship()
					.all()
					.filterByIds(ids)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
					.get();
		} else if (SnomedRefSetMember.class.isAssignableFrom(componentType)) {
			return SnomedRequests.prepareSearchMember()
					.all()
					.filterByIds(ids)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
					.get();
		}
		throw new UnsupportedOperationException("Cannot get components for" + componentType);
	}
	
}
