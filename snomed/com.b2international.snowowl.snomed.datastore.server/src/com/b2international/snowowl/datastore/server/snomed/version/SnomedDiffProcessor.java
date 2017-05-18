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
package com.b2international.snowowl.datastore.server.snomed.version;

import static com.b2international.commons.StringUtils.capitalizeFirstLetter;
import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.commons.StringUtils.lowerCaseFirstLetter;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.index.diff.NodeDelta.NULL_IMPL;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.SnomedPackage.eINSTANCE;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.datastore.DataTypeUtils.BIG_DECIMAL_EDIT_PATTERN;
import static com.b2international.snowowl.snomed.datastore.DataTypeUtils.INTEGER_EDIT_PATTERN;
import static com.b2international.snowowl.snomed.datastore.DataTypeUtils.getDefaultDataTypeLabel;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.ASSOCIATION_REFSETS;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.deserializeValue;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Boolean.parseBoolean;
import static java.lang.String.valueOf;
import static java.util.Collections.unmodifiableSet;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.diff.FeatureChange;
import com.b2international.snowowl.datastore.index.diff.NodeDelta;
import com.b2international.snowowl.datastore.server.version.NodeDeltaDiffProcessor;
import com.b2international.snowowl.emf.compare.diff.AttributeDiff;
import com.b2international.snowowl.emf.compare.diff.ReferenceDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueAttributeDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueReferenceDiff;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.collect.Sets;

/**
 * Difference processor implementation for SNOMED&nbsp;CT.
 *
 */
public class SnomedDiffProcessor extends NodeDeltaDiffProcessor {
	
	private static final EAttribute COMPONENT_STATUS_ATTRIBUTE = eINSTANCE.getComponent_Active();
	private static final EAttribute COMPONENT_ET_ATTRIBUTE = eINSTANCE.getComponent_EffectiveTime();
	private static final EAttribute COMPONENT_RELEASED_ATTRIBUTE = eINSTANCE.getComponent_Released();
	private static final EReference COMPONENT_MODULE_REFERENCE = eINSTANCE.getComponent_Module();
	private static final EReference ANNOTATABLE_CDT_REFERENCE = eINSTANCE.getAnnotatable_ConcreteDomainRefSetMembers();
	private static final EReference INACTIVATABLE_ASSOCIATION_REFERENCE = eINSTANCE.getInactivatable_AssociationRefSetMembers();
	private static final EReference INACTIVATABLE_INACTIVATION_REFERENCE = eINSTANCE.getInactivatable_InactivationIndicatorRefSetMembers();
	
	private static final EAttribute CONCEPT_SUBCLASS_DEFINITION_ATTRIBUTE = eINSTANCE.getConcept_Exhaustive();
	private static final EReference CONCEPT_DEFINITION_STATUS_REFERENCE = eINSTANCE.getConcept_DefinitionStatus();
	private static final EReference CONCEPT_OUTBOUND_RELATIONSHIP_REFERENCE = eINSTANCE.getConcept_OutboundRelationships();
	private static final EReference CONCEPT_DESCRIPTIONS_REFERENCE = eINSTANCE.getConcept_Descriptions();
	
	private static final EAttribute DESCRIPTION_TERM_ATTRIBUTE = eINSTANCE.getDescription_Term();
	private static final EReference DESCRIPTION_TYPE_REFERENCE = eINSTANCE.getDescription_Type();
	private static final EReference DESCRIPTION_CASE_SIGNIFICANCE_REFERENCE = eINSTANCE.getDescription_CaseSignificance();
	private static final EReference DESCRIPTION_LANGUAGE_MEMBER_REFERENCE = eINSTANCE.getDescription_LanguageRefSetMembers();
	
	private static final EAttribute RELATIONSHIP_GROUP_ATTRIBUTE = eINSTANCE.getRelationship_Group();
	private static final EReference RELATIONSHIP_CHARACTERISTIC_TYPE_REFERENCE = eINSTANCE.getRelationship_CharacteristicType();
	
	private static final EReference REFSET_MEMBERS_REFERENCE = SnomedRefSetPackage.eINSTANCE.getSnomedRegularRefSet_Members();
	private static final EAttribute REFSET_MEMBER_ACTIVE_ATTRIBUTE = SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_Active();
	private static final EAttribute REFSET_MEMBER_ET_ATTRIBUTE = SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_EffectiveTime();
	
	private static final EAttribute MODULE_SOURCE_ET_ATTRIBUTE = SnomedRefSetPackage.eINSTANCE.getSnomedModuleDependencyRefSetMember_SourceEffectiveTime();
	private static final EAttribute MODULE_TARGET_ET_ATTRIBUTE = SnomedRefSetPackage.eINSTANCE.getSnomedModuleDependencyRefSetMember_TargetEffectiveTime();
	private static final EAttribute MEMBER_RELEASED_ATTRIBUTE = SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_Released();
	
	private static final EAttribute LANGUAGE_ACCEPTABILITY_ID_ATTRIBUTE = SnomedRefSetPackage.eINSTANCE.getSnomedLanguageRefSetMember_AcceptabilityId();
	private static final EAttribute ATTRIBUTE_VALUE_ID_ATTRIBUTE = SnomedRefSetPackage.eINSTANCE.getSnomedAttributeValueRefSetMember_ValueId();
	private static final EAttribute TARGET_COMPONENT_ID = SnomedRefSetPackage.eINSTANCE.getSnomedAssociationRefSetMember_TargetComponentId();
	
	private static final Collection<EStructuralFeature> EXCLUDED_FEATURES = unmodifiableSet(Sets.<EStructuralFeature>newHashSet(
			SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_EffectiveTime(),
			SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_ModuleId(),
			SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_ReferencedComponentId(),
			SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_ReferencedComponentType(),
			SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_RefSet(),
			SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_RefSetIdentifierId()
		));

	private static final ThreadLocal<DecimalFormat> LONG_FORMAT = new ThreadLocal<DecimalFormat>() {
		@Override public DecimalFormat get() { return new DecimalFormat(BIG_DECIMAL_EDIT_PATTERN); }
		@Override public void set(final DecimalFormat value) { /*ignored*/ };
	};
	
	private static final ThreadLocal<DecimalFormat> INTEGER_FORMAT = new ThreadLocal<DecimalFormat>() {
		@Override public DecimalFormat get() { return new DecimalFormat(INTEGER_EDIT_PATTERN); }
		@Override public void set(final DecimalFormat value) { /*ignored*/ };
	};
	
	private final CDOView sourceView;
	private final CDOView targetView;
	
	public SnomedDiffProcessor(final CDOView sourceView, final CDOView targetView) {
		this.sourceView = checkNotNull(sourceView, "sourceView");
		this.targetView = checkNotNull(targetView, "targetView");
	}
	
	@Override
	public Collection<EStructuralFeature> getExcludedFeatures() {
		return EXCLUDED_FEATURES;
	}
	
	@Override
	public NodeDelta processSingleValueAttributeChange(final SingleValueAttributeDiff diff) {
		
		final EAttribute changedFeature = checkNotNull(diff, "diff").getChangedFeature();
		
		if (COMPONENT_STATUS_ATTRIBUTE.equals(changedFeature)) {
			return createComponentStatusChangedDelta(diff);
		}
		
		if (COMPONENT_ET_ATTRIBUTE.equals(changedFeature)) {
			return createComponentEffectiveTimeChangedDelta(diff);
		}
		
		if (COMPONENT_RELEASED_ATTRIBUTE.equals(changedFeature)) {
			return createComponentReleasedChangedDelta(diff);
		}
		
		if (CONCEPT_SUBCLASS_DEFINITION_ATTRIBUTE.equals(changedFeature)) {
			return createConceptSubclassDefinitionChangedDelta(diff);
		}
		
		if (DESCRIPTION_TERM_ATTRIBUTE.equals(changedFeature)) {
			return createDescriptionTermChangedDelta(diff);
		}
		
		if (MODULE_TARGET_ET_ATTRIBUTE.equals(changedFeature)) {
			return createModuleTargetEffectiveTimeChangedDelta(diff);
		}
		
		if (MODULE_SOURCE_ET_ATTRIBUTE.equals(changedFeature)) {
			return createModuleSourceEffectiveTimeChangedDelta(diff);
		}
		
		if (REFSET_MEMBER_ET_ATTRIBUTE.equals(changedFeature)) {
			return createMemberEffectiveTimeChangedDelta(diff);
		}
		
		if (REFSET_MEMBER_ACTIVE_ATTRIBUTE.equals(changedFeature)) {
			return createMemberStatusChangedDelta(diff);
		}
		
		if (LANGUAGE_ACCEPTABILITY_ID_ATTRIBUTE.equals(changedFeature)) {
			return createLanguageAcceptabilityChangedDelta(diff);
		}
		
		if (ATTRIBUTE_VALUE_ID_ATTRIBUTE.equals(changedFeature)) {
			return createAttributeValueChangeDelta(diff);
		}
		
		if (TARGET_COMPONENT_ID.equals(changedFeature)) {
			return createTargetComponentIdChangeDelta(diff);
		}
		
		if (MEMBER_RELEASED_ATTRIBUTE.equals(changedFeature)) {
			return createMemberReleasedChangedDelta(diff);
		}
		
		if (RELATIONSHIP_GROUP_ATTRIBUTE.equals(changedFeature)) {
			return createRelationshipGroupChangedDelta(diff);
		}
		
		return super.processSingleValueAttributeChange(diff);
	}
	
	@Override
	public NodeDelta processManyValueAttributeChange(final AttributeDiff diff) {
		return super.processManyValueAttributeChange(diff);
	}

	@Override
	public NodeDelta processSingleValueReferenceChange(final SingleValueReferenceDiff diff) {
		
		final EReference changedFeature = checkNotNull(diff, "diff").getChangedFeature();
		
		if (COMPONENT_MODULE_REFERENCE.equals(changedFeature)) {
			return createComponentModuleChangedDelta(diff);
		}
		
		if (CONCEPT_DEFINITION_STATUS_REFERENCE.equals(changedFeature)) {
			return createConceptDefinitionStatusChangedDelta(diff);
		}
		
		if (DESCRIPTION_CASE_SIGNIFICANCE_REFERENCE.equals(changedFeature)) {
			return createDescriptionCaseSignificanceChangedDelta(diff);
		}
		
		if (DESCRIPTION_TYPE_REFERENCE.equals(changedFeature)) {
			return createDescriptionTypeChangedDelta(diff);
		}
		
		if (RELATIONSHIP_CHARACTERISTIC_TYPE_REFERENCE.equals(changedFeature)) {
			return createRelationshipCharacteristicTypeChangedDelta(diff);
		}
		
		return super.processSingleValueReferenceChange(diff);
	}

	@Override
	public NodeDelta processManyValueReferenceChange(final ReferenceDiff diff) {
		
		final EReference changedFeature = checkNotNull(diff, "diff").getChangedFeature();
		
		if (ANNOTATABLE_CDT_REFERENCE.equals(changedFeature)) {
			return createAnnotatableCdtChangedDelta(diff);
		}
		
		if (INACTIVATABLE_ASSOCIATION_REFERENCE.equals(changedFeature)) {
			return createInactivatableAssociationChangedDelta(diff);
		}
		
		if (INACTIVATABLE_INACTIVATION_REFERENCE.equals(changedFeature)) {
			return createInactivatableInactivationChangedDelta(diff);
		}
		
		if (CONCEPT_OUTBOUND_RELATIONSHIP_REFERENCE.equals(changedFeature)) {
			return createConceptOutboundRelationshipChangedDelta(diff);
		}
		
		if (CONCEPT_DESCRIPTIONS_REFERENCE.equals(changedFeature)) {
			return createConceptDescriptionChangedDelta(diff);
		}
		
		if (DESCRIPTION_LANGUAGE_MEMBER_REFERENCE.equals(changedFeature)) {
			return createDescriptionLanguageMemberChangedDelta(diff);
		}
		
		if (REFSET_MEMBERS_REFERENCE.equals(changedFeature)) {
			return createRefSetMemberChangedDelta(diff);
		}
			
		return super.processManyValueReferenceChange(diff);
	}

	@Override
	protected String getRepositoryUuid() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}
	
	private NodeDelta createIgnoredDelta() {
		return NULL_IMPL;
	}
	
	private NodeDelta createComponentStatusChangedDelta(final SingleValueAttributeDiff diff) {
		final EObject target = diff.getTarget();
		final String componentLabel = getComponentLabel(target);
		final FeatureChange featureChange = createFeatureChange("Status", toActiveInactive(diff.getOldValue()), toActiveInactive(diff.getValue()));
		final short terminologyComponentId = getTerminologyComponentId(target);
		return createDeltaForUpdate(componentLabel, featureChange, terminologyComponentId);
	}

	private NodeDelta createComponentEffectiveTimeChangedDelta(final SingleValueAttributeDiff diff) {
		final EObject target = diff.getTarget();
		final String componentLabel = getComponentLabel(target);
		final FeatureChange featureChange = createFeatureChange("Effective time", EffectiveTimes.format(diff.getOldValue()), EffectiveTimes.format(diff.getValue()));
		final short terminologyComponentId = getTerminologyComponentId(target);
		return createDeltaForUpdate(componentLabel, featureChange, terminologyComponentId);
	}

	private NodeDelta createComponentReleasedChangedDelta(final SingleValueAttributeDiff diff) {
		final EObject target = diff.getTarget();
		final String componentLabel = getComponentLabel(target);
		final FeatureChange featureChange = createFeatureChange("Released", toPublishedUnreleased(diff.getOldValue()), toPublishedUnreleased(diff.getValue()));
		final short terminologyComponentId = getTerminologyComponentId(target);
		return createDeltaForUpdate(componentLabel, featureChange, terminologyComponentId);
	}

	private NodeDelta createConceptDefinitionStatusChangedDelta(final SingleValueReferenceDiff diff) {
		final EObject target = diff.getTarget();
		final String conceptLabel = getComponentLabel(target);
		final String newDefinitionStatus = getComponentLabel(diff.getValue());
		final String oldDefinitionStatus = getComponentLabel(diff.getOldValue());
		final FeatureChange featureChange = createFeatureChange("Definition status", oldDefinitionStatus, newDefinitionStatus);
		return createDeltaForUpdate(conceptLabel, featureChange, CONCEPT_NUMBER);
	}
	
	private NodeDelta createConceptSubclassDefinitionChangedDelta(final SingleValueAttributeDiff diff) {
		final String conceptLabel = getComponentLabel(diff.getTarget());
		final String newSubclassDefinitionLabel = parseBoolean(String.valueOf(diff.getValue())) ? "Mutual disjoint" : "Non-disjoint";
		final String oldSubclassDefinitionLabel = parseBoolean(String.valueOf(diff.getOldValue())) ? "Mutual disjoint" : "Non-disjoint";
		final FeatureChange featureChange = createFeatureChange("Subclass definition", oldSubclassDefinitionLabel, newSubclassDefinitionLabel);
		return createDeltaForUpdate(conceptLabel, featureChange, CONCEPT_NUMBER);
	}

	private NodeDelta createRelationshipCharacteristicTypeChangedDelta(final SingleValueReferenceDiff diff) {
		final EObject target = diff.getTarget();
		final String relationshipLabel = getComponentLabel(target);
		final String newCharacteristicType = getComponentLabel(diff.getValue());
		final String oldCharacteristicType = getComponentLabel(diff.getOldValue());
		final FeatureChange featureChange = createFeatureChange("Characteristic type", oldCharacteristicType, newCharacteristicType);
		return createDeltaForUpdate(relationshipLabel, featureChange, RELATIONSHIP_NUMBER);
	}

	private NodeDelta createDescriptionTermChangedDelta(final SingleValueAttributeDiff diff) {
		final String descriptionLabel = String.valueOf(diff.getValue());
		final String oldDescriptionTerm = String.valueOf(diff.getOldValue());
		final FeatureChange featureChange = createFeatureChange("Term", oldDescriptionTerm, descriptionLabel);
		return createDeltaForUpdate(descriptionLabel, featureChange, DESCRIPTION_NUMBER);
	}

	private NodeDelta createComponentModuleChangedDelta(final SingleValueReferenceDiff diff) {
		final EObject target = diff.getTarget();
		final String componentLabel = getComponentLabel(target);
		final String newModule = getComponentLabel(diff.getValue());
		final String oldModule = getComponentLabel(diff.getOldValue());
		final short terminologyComponentId = getTerminologyComponentId(target);
		final FeatureChange featureChange = createFeatureChange("Module", oldModule, newModule);
		return createDeltaForUpdate(componentLabel, featureChange, terminologyComponentId);
	}

	private NodeDelta createDescriptionCaseSignificanceChangedDelta(final SingleValueReferenceDiff diff) {
		final EObject target = diff.getTarget();
		final String descriptionLabel = getComponentLabel(target);
		final String newCaseSignificance = getComponentLabel(diff.getValue());
		final String oldCaseSignificance = getComponentLabel(diff.getOldValue());
		final FeatureChange featureChange = createFeatureChange("Case significance", oldCaseSignificance, newCaseSignificance);
		return createDeltaForUpdate(descriptionLabel, featureChange, DESCRIPTION_NUMBER);
	}

	private NodeDelta createDescriptionTypeChangedDelta(final SingleValueReferenceDiff diff) {
		final EObject target = diff.getTarget();
		final String descriptionLabel = getComponentLabel(target);
		final String newType = getComponentLabel(diff.getValue());
		final String oldType = getComponentLabel(diff.getOldValue());
		final FeatureChange featureChange = createFeatureChange("Type", oldType, newType);
		return createDeltaForUpdate(descriptionLabel, featureChange, DESCRIPTION_NUMBER);
	}
	
	private NodeDelta createInactivatableAssociationChangedDelta(final SnomedAssociationRefSetMember member, final boolean addition) {
		if (CONCEPT_NUMBER == member.getReferencedComponentType()) {
			final String associationLabel = lowerCaseFirstLetter(ASSOCIATION_REFSETS.get(member.getRefSetIdentifierId()));
			final String associatedComponentLabel = getConceptLabel(getBranchPath(member), member.getTargetComponentId());
			final String conceptLabel = getConceptLabel(getBranchPath(member), member.getReferencedComponentId());
			final FeatureChange featureChange = addition 
					? createToFeatureChange("New " + associationLabel + " association", associatedComponentLabel)
					: createFromFeatureChange("Deleted " + associationLabel + " association", associatedComponentLabel);
			return createDeltaForUpdate(conceptLabel, featureChange, CONCEPT_NUMBER);
		} else {
			return createIgnoredDelta();
		}
		
	}
	
	private NodeDelta createInactivatableAssociationChangedDelta(final ReferenceDiff diff) {
		return createInactivatableAssociationChangedDelta((SnomedAssociationRefSetMember) diff.getValue(), isAddition(diff));
	}

	private NodeDelta createInactivatableInactivationChangedDelta(final ReferenceDiff diff) {
		return createInactivatableInactivationChangedDelta((SnomedAttributeValueRefSetMember) diff.getValue(), isAddition(diff));
	}
	
	private NodeDelta createInactivatableInactivationChangedDelta(final SnomedAttributeValueRefSetMember member, final boolean addition) {
		if (CONCEPT_NUMBER == member.getReferencedComponentType()) {
			final String inactivityStatusId = member.getValueId();
			final String inactivityStatusLabel = getConceptLabel(getBranchPath(member), inactivityStatusId);
			final String conceptLabel = getConceptLabel(getBranchPath(member), member.getReferencedComponentId());
			final FeatureChange featureChange = addition 
					? createToFeatureChange("New inactivity status", inactivityStatusLabel)
					: createFromFeatureChange("Deleted inactivity status", inactivityStatusLabel);
			return createDeltaForUpdate(conceptLabel, featureChange, CONCEPT_NUMBER);
		} else {
			return createIgnoredDelta();
		}
	}

	
	private NodeDelta createAnnotatableCdtChangedDelta(final ReferenceDiff diff) {
		return createAnnotatableCdtChangedDelta((SnomedConcreteDataTypeRefSetMember) diff.getValue(), isAddition(diff));
	}
	
	private NodeDelta createAnnotatableCdtChangedDelta(final SnomedConcreteDataTypeRefSetMember member, final boolean addition) {
		final String componentLabel = getComponentLabel(member.eContainer());
		final String dataTypeLabel = getDefaultDataTypeLabel(member.getLabel());
		final short terminologyComponentId = member.getReferencedComponentType();
		final String dataTypeType = getDataTypeType(member);
		final String dataTypeValueLabel = getDataTypeValue(member.getDataType(), member);
		final String dataTypeLabelWithValue = dataTypeLabel + " " + dataTypeValueLabel;
		final FeatureChange featureChange = addition 
				? createToFeatureChange("New " + capitalizeFirstLetter(dataTypeType) + " data type property", dataTypeLabelWithValue)
				: createFromFeatureChange("Deleted " + capitalizeFirstLetter(dataTypeType) + " data type property", dataTypeLabelWithValue);
		return createDeltaForUpdate(componentLabel, featureChange, terminologyComponentId);
	}

	private String getDataTypeValue(final DataType dataType, final SnomedConcreteDataTypeRefSetMember member) {
		final Object value = deserializeValue(dataType, member.getSerializedValue());
		switch (dataType) {
			case BOOLEAN: return Boolean.toString((boolean) value);
			case DATE: return Dates.formatByGmt(value);
			case DECIMAL: return LONG_FORMAT.get().format(((BigDecimal) value).doubleValue());
			case INTEGER: return INTEGER_FORMAT.get().format(value);
			case STRING: return valueOf(value);
			default: throw new IllegalArgumentException("Unknown data type: " + dataType);
		}
	}

	private NodeDelta createConceptOutboundRelationshipChangedDelta(final ReferenceDiff diff) {
		final String relationshipLabel = getComponentLabel(diff.getValue());
		return isDeletion(diff) 
				? createDeltaForDeletion(relationshipLabel, createEmptyFeatureChange(), RELATIONSHIP_NUMBER) 
				: createDeltaForAddition(relationshipLabel, createEmptyFeatureChange(), RELATIONSHIP_NUMBER);
	}

	private NodeDelta createModuleEffectiveTimeChangedDelta(final SingleValueAttributeDiff diff, final String moduleOriginLabel) {
		final SnomedModuleDependencyRefSetMember member = (SnomedModuleDependencyRefSetMember) diff.getTarget();
		final String newEffectiveTime = EffectiveTimes.format(diff.getValue());
		final String oldEffectiveTime = EffectiveTimes.format(diff.getOldValue());
		final String moduleConceptLabel = getConceptLabel(getBranchPath(member), member.getReferencedComponentId());
		final FeatureChange featureChange = createFeatureChange(moduleOriginLabel + " effective time", oldEffectiveTime, newEffectiveTime);
		return createDeltaForUpdate(moduleConceptLabel, featureChange, REFSET_MEMBER_NUMBER);
	}

	private NodeDelta createModuleSourceEffectiveTimeChangedDelta(final SingleValueAttributeDiff diff) {
		return createModuleEffectiveTimeChangedDelta(diff, "Source");
	}
	
	private NodeDelta createModuleTargetEffectiveTimeChangedDelta(final SingleValueAttributeDiff diff) {
		return createModuleEffectiveTimeChangedDelta(diff, "Target");
	}

	private NodeDelta createMemberEffectiveTimeChangedDelta(final SingleValueAttributeDiff diff) {
		final SnomedRefSetMember member = (SnomedRefSetMember) diff.getTarget();
		final String newEffectiveTime = EffectiveTimes.format(diff.getValue());
		final String oldEffectiveTime = EffectiveTimes.format(diff.getOldValue());
		final FeatureChange featureChange = createFeatureChange("Effective time", oldEffectiveTime, newEffectiveTime);
		return createDeltaForUpdate(getMemberLabel(member), featureChange, REFSET_MEMBER_NUMBER);
	}

	private NodeDelta createMemberReleasedChangedDelta(final SingleValueAttributeDiff diff) {
		final SnomedRefSetMember member = (SnomedRefSetMember) diff.getTarget();
		final FeatureChange featureChange = createFeatureChange("Released", toPublishedUnreleased(diff.getOldValue()), toPublishedUnreleased(diff.getValue()));
		return createDeltaForUpdate(getMemberLabel(member), featureChange, REFSET_MEMBER_NUMBER);
	}
	
	private NodeDelta createRelationshipGroupChangedDelta(final SingleValueAttributeDiff diff) {
		final String relationshipLabel = getComponentLabel(diff.getTarget());
		final FeatureChange featureChange = createFeatureChange("Group", String.valueOf(diff.getOldValue()), String.valueOf(diff.getValue()));
		return createDeltaForUpdate(relationshipLabel, featureChange, RELATIONSHIP_NUMBER); 
	}
	
	private NodeDelta createMemberStatusChangedDelta(final SingleValueAttributeDiff diff) {
		final SnomedRefSetMember member = (SnomedRefSetMember) diff.getTarget();
		
		if (member instanceof SnomedLanguageRefSetMember) {
			return createIgnoredDelta();
		}
		
		if (member instanceof SnomedConcreteDataTypeRefSetMember) {
			return createAnnotatableCdtChangedDelta((SnomedConcreteDataTypeRefSetMember) member, member.isActive());
		}
		
		if (member instanceof SnomedAssociationRefSetMember) {
			return createInactivatableAssociationChangedDelta((SnomedAssociationRefSetMember) member, member.isActive());
		}
		
		if (member instanceof SnomedAttributeValueRefSetMember && member.getRefSet() instanceof SnomedStructuralRefSet) {
			return createInactivatableInactivationChangedDelta((SnomedAttributeValueRefSetMember) member, member.isActive());
		}
		
		final String memberLabel = getMemberLabel(member);
		final FeatureChange featureChange = createFeatureChange("Status", toActiveInactive(diff.getOldValue()), toActiveInactive(diff.getValue()));
		return createDeltaForUpdate(memberLabel, featureChange, REFSET_MEMBER_NUMBER);
	}
	
	private NodeDelta createRefSetMemberChangedDelta(final ReferenceDiff diff) {
		final String memberLabel = getMemberLabel((SnomedRefSetMember) diff.getValue());
		return isDeletion(diff) 
				? createDeltaForDeletion(memberLabel, createEmptyFeatureChange(), REFSET_MEMBER_NUMBER) 
				: createDeltaForAddition(memberLabel, createEmptyFeatureChange(), REFSET_MEMBER_NUMBER);
	}
	
	private NodeDelta createConceptDescriptionChangedDelta(final ReferenceDiff diff) {
		final String descriptionLabel = getComponentLabel(diff.getValue());
		final String descriptionTypeLabel = getComponentLabel(((Description) diff.getValue()).getType());
		final String descriptionTypeWithTerm = descriptionTypeLabel + ": " + descriptionLabel;
		return isDeletion(diff) 
				? createDeltaForDeletion(descriptionTypeWithTerm, createEmptyFeatureChange(), DESCRIPTION_NUMBER) 
				: createDeltaForAddition(descriptionTypeWithTerm, createEmptyFeatureChange(), DESCRIPTION_NUMBER);
	}

	private String getDataTypeType(final SnomedConcreteDataTypeRefSetMember member) {
		switch (member.getDataType()) {
			case BOOLEAN: return "boolean";
			case DATE: return "datetime";
			case DECIMAL: return "decimal";
			case STRING: return "string";
			case INTEGER: return "integer";
			default: throw new IllegalArgumentException("Unknown data type: " + member.getDataType());
		}
	}

	private NodeDelta createLanguageAcceptabilityChangedDelta(final SingleValueAttributeDiff diff) {
		return createDescriptionLanguageMemberChangedDelta((SnomedLanguageRefSetMember) diff.getTarget());
	}
	
	private NodeDelta createDescriptionLanguageMemberChangedDelta(final ReferenceDiff diff) {
		return createDescriptionLanguageMemberChangedDelta(((SnomedLanguageRefSetMember) diff.getValue()));
	}
	
	private NodeDelta createDescriptionLanguageMemberChangedDelta(final SnomedLanguageRefSetMember member) {
		if (!member.isActive()) {
			return createIgnoredDelta();
		}
		
		final String[] descriptionProperties = getReferencedDescriptionProperties(member);
		final String descriptionTypeId = descriptionProperties[1];
		
		if (isFsn(descriptionTypeId)) {
			return createIgnoredDelta();
		}
		
		if (!couldBePreferredTerm(member, descriptionTypeId)) {
			return createIgnoredDelta();
		}
		
		final String conceptId = descriptionProperties[0];
		
		final String oldPt = getConceptLabel(getBranchPath(sourceView), conceptId);
		final String newPt = getConceptLabel(getBranchPath(targetView), conceptId);
		
		if (isEmpty(oldPt)) {
			return createIgnoredDelta();
		}
		
		if (oldPt.equals(newPt)) {
			return createIgnoredDelta();
		}
		
		final String languageRefSetLabel = capitalizeFirstLetter(getConceptLabel(getBranchPath(member), member.getRefSetIdentifierId()));
		final FeatureChange featureChange = createFeatureChange(languageRefSetLabel + " preferred term", oldPt, newPt);
		return createDeltaForUpdate(newPt, featureChange, CONCEPT_NUMBER);
	}

	private NodeDelta createAttributeValueChangeDelta(final SingleValueAttributeDiff diff) {
		final SnomedAttributeValueRefSetMember member = (SnomedAttributeValueRefSetMember) diff.getTarget();
		final String memberLabel = getMemberLabel(member);
		final String oldConceptLabel = getConceptLabel(getBranchPath(member), String.valueOf(diff.getOldValue()));
		final String newConceptLabel = getConceptLabel(getBranchPath(member), String.valueOf(diff.getValue()));
		final FeatureChange featureChange = createFeatureChange("Attribute value", oldConceptLabel, newConceptLabel);
		return createDeltaForUpdate(memberLabel, featureChange, REFSET_MEMBER_NUMBER);
	}
	
	private NodeDelta createTargetComponentIdChangeDelta(SingleValueAttributeDiff diff) {
		SnomedAssociationRefSetMember member = (SnomedAssociationRefSetMember) diff.getTarget();
		String memberLabel = getMemberLabel(member);
		String oldConceptLabel = getConceptLabel(getBranchPath(member), String.valueOf(diff.getOldValue()));
		String newConceptLabel = getConceptLabel(getBranchPath(member), String.valueOf(diff.getValue()));
		FeatureChange featureChange = createFeatureChange("Target component id", oldConceptLabel, newConceptLabel);
		return createDeltaForUpdate(memberLabel, featureChange, REFSET_MEMBER_NUMBER);
	}
	
	private boolean isFsn(final String descriptionTypeId) {
		return FULLY_SPECIFIED_NAME.equals(descriptionTypeId);
	}

	private String[] getReferencedDescriptionProperties(final SnomedLanguageRefSetMember member) {
		return getComponentService().getDescriptionProperties(getBranchPath(member), member.getReferencedComponentId());
	}

	private boolean couldBePreferredTerm(final SnomedLanguageRefSetMember member, final String descriptionTypeId) {
		return getAllPreferredDescriptionIds(member).contains(descriptionTypeId);
	}

	private Set<String> getAllPreferredDescriptionIds(final SnomedLanguageRefSetMember member) {
		return getComponentService().getAvailablePreferredTermIds(getBranchPath(member));
	}

	private String getComponentLabel(final EObject component) {
		if (component instanceof Description) {
			return ((Description) component).getTerm();
		} else if (component instanceof SnomedRefSet) {
			return getConceptLabel(getBranchPath(component), ((SnomedRefSet) component).getIdentifierId());
		} else if (component instanceof Concept) {
			return getConceptLabel(getBranchPath(component), ((Concept) component).getId());
		} else if (component instanceof Relationship) {
			final Relationship relationship = (Relationship) component;
			
			final String negation = relationship.isDestinationNegated() ? " NOT " : "";
			final String[] labels = getRelationshipLabels(component, relationship);
			final StringBuilder sb = new StringBuilder();
			sb.append(labels[0]);
			sb.append(" - ");
			sb.append(negation);
			sb.append(labels[1]);
			
			//indicate relationship characteristic type to distinguish between different types
			sb.append(" (");
			String characteristicTypeLabel = getComponentLabel(relationship.getCharacteristicType());
			sb.append(characteristicTypeLabel);
			sb.append(") ");
			sb.append(" - ");
			sb.append(labels[2]);
			return sb.toString();
		} else {
			throw new IllegalArgumentException("Unknown component type of: " + component);
		}
	}

	private String getConceptLabel(final IBranchPath branchPath, final String conceptId) {
		return getLabels(branchPath, conceptId)[0];
	}
	
	private String getMemberLabel(final SnomedRefSetMember member) {
		final Pair<String, String> labelPair = getComponentService().getMemberLabel(getBranchPath(member), member.getUuid());
		final StringBuffer sb = new StringBuffer();
		sb.append(labelPair.getA());
		if (!isEmpty(labelPair.getB())) {
			sb.append(" - ");
			sb.append(labelPair.getB());
		}
		return sb.toString();
	}
	
	private String[] getRelationshipLabels(final EObject component, final Relationship relationship) {
		return getLabels(getBranchPath(component), 
				relationship.getSource().getId(), 
				relationship.getType().getId(), 
				relationship.getDestination().getId());
	}
	
	private short getTerminologyComponentId(final EObject component) {
		if (component instanceof Concept) {
			return CONCEPT_NUMBER;
		} else if (component instanceof Description) {
			return DESCRIPTION_NUMBER;
		} else if (component instanceof Relationship) {
			return RELATIONSHIP_NUMBER;
		} else {
			throw new IllegalArgumentException("Unknown component type of: " + component);
		}
	}
	
	private String[] getLabels(final IBranchPath branchPath, final String... ids) {
		return getComponentService().getLabels(branchPath, ids);
	}
	
	private ISnomedComponentService getComponentService() {
		return getServiceForClass(ISnomedComponentService.class);
	}
	
	private IBranchPath getBranchPath(final CDOView view) {
		return createPath(view);
	}
	
	private IBranchPath getBranchPath(final EObject object) {
		return createPath((CDOObject) object);
	}
	
}