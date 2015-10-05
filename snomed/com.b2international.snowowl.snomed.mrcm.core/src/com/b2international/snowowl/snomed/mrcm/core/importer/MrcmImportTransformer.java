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
package com.b2international.snowowl.snomed.mrcm.core.importer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.ihtsdo.sct.CmConstraint;
import org.ihtsdo.sct.CmDependencyMember;
import org.ihtsdo.sct.CmTestCardinality;
import org.ihtsdo.sct.CmTestDependency;
import org.ihtsdo.sct.CmTestRelationship;
import org.ihtsdo.sct.SifChange;
import org.ihtsdo.sct.SifChange.After;
import org.ihtsdo.sct.SifChangeSet;
import org.ihtsdo.sct.SifChanges;
import org.ihtsdo.sct.SifRefSetMember;
import org.ihtsdo.sct.SifRefSetMrcm;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConstraintBase;
import com.b2international.snowowl.snomed.mrcm.ConstraintForm;
import com.b2international.snowowl.snomed.mrcm.ConstraintStrength;
import com.b2international.snowowl.snomed.mrcm.DependencyOperator;
import com.b2international.snowowl.snomed.mrcm.DependencyPredicate;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.b2international.snowowl.snomed.mrcm.core.PersistenceUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Transforms the JAXB unmarshalled IHTSDO MRCM file into our internal representation.
 * 
 */
public class MrcmImportTransformer {

	// constraint strength constants
	private static final String MANDATORY_CM_STRENGTH = "0";
	private static final String RECOMMENDED_CM_STRENGTH = "1";
	private static final String ADVISORY_CM_STRENGTH = "2";
	private static final String MANDATORY_PC_STRENGTH = "3";
	private static final String INFORMATION_MODEL_PC_STRENGTH = "4";
	private static final String USE_CASE_SPECIFIC_PC_STRENGTH = "5";
	private static final String IMPLEMENTATION_SPECIFIC_PC_STRENGTH = "6";
	
	// constraint form constants
	private static final String ALL_FORMS = "0";
	private static final String DISTRIBUTION_FORM = "1";
	private static final String STATED_FORM = "2";
	private static final String CLOSE_TO_USER_FORM = "3";
	private static final String LONG_NORMAL_FORM = "4";
	private static final String SHORT_NORMAL_FORM = "5";
	
	// constraint status constants
	private static final short ACTIVE_STATUS = 1;
	private static final short INACTIVE_STATUS = 0;
	
	// ref set member tag code constants
	private static final String SELF_TAG_CODE = "1"; 
	private static final String DESCENDANT_TAG_CODE = "2"; 
	private static final String SELF_OR_DESCENDANT_TAG_CODE = "3";
	
	// group rule constants
	private static final String UNSPECIFIED_GROUP_RULE = "0";
	private static final String UNGROUPED_RULE = "1";
	private static final String SINGLE_GROUP_RULE = "2";
	private static final String ALL_GROUPS_RULE = "3";
	private static final String MULTIPLE_GROUPS_RULE = "4";
	
	// dependency operator constants
	private static final String ONE_DEPENDENCY_OPERATOR = "1";
	private static final String SOME_DEPENDENCY_OPERATOR = "2";
	private static final String ALL_DEPENDENCY_OPERATOR = "3";
	
	private final Set<ConstraintBase> transformedConstraints = Sets.newHashSet();
	private final Map<String, ConceptModelPredicate> predicateMap = Maps.newHashMap();
	private final Multimap<String, AttributeConstraint> predicateUuidMultimap = ArrayListMultimap.create();
	private final Map<String, CompositeConceptSetDefinition> topLevelConceptSetDefinitionMap = Maps.newHashMap();
	
	// constraint counter used for naming imported constraints
	private int constraintCounter = 0;
	
	public Set<ConstraintBase> getConstraints() {
		return ImmutableSet.copyOf(transformedConstraints);
	}
	
	/**
	 * Processes the specified element, which should be one of the classes generated by JAXB from the MRCM schema.
	 * 
	 * @param element
	 */
	public void visit(Object element) {
		checkNotNull(element, "Element to visit must not be null.");
		if (element instanceof SifChangeSet) {
			SifChangeSet changeSet = (SifChangeSet) element;
			visitChangeSet(changeSet);
		} else if (element instanceof SifChanges) {
			SifChanges changes = (SifChanges) element;
			visitChanges(changes);
		} else if (element instanceof SifChange) {
			SifChange change = (SifChange) element;
			visitChange(change);
		} else if (element instanceof After) {
			After after = (After) element;
			visitAfter(after);
		} else if (element instanceof CmConstraint) {
			CmConstraint constraint = (CmConstraint) element;
			visitConstraint(constraint);
		} else if (element instanceof SifRefSetMember) {
			SifRefSetMember refSetMember = (SifRefSetMember) element;
			visitRefSetMember(refSetMember);
		} else if (element instanceof SifRefSetMrcm) {
			SifRefSetMrcm refSet = (SifRefSetMrcm) element;
			visitRefSet(refSet);
		} else if (element instanceof CmTestRelationship) {
			CmTestRelationship testRelationship = (CmTestRelationship) element;
			visitTestRelationship(testRelationship);
		} else if (element instanceof CmTestCardinality) {
			CmTestCardinality testCardinality = (CmTestCardinality) element;
			visitTestCardinality(testCardinality);
		} else if (element instanceof CmTestDependency) {
			CmTestDependency testDependency = (CmTestDependency) element;
			visitTestDependency(testDependency);
		} else if (element instanceof CmDependencyMember) {
			CmDependencyMember dependencyMember = (CmDependencyMember) element;
			visitCmDependencyMember(dependencyMember);
		} else if (element instanceof JAXBElement<?>) {
			JAXBElement<?> jaxbElement = (JAXBElement<?>)element;
			visit(jaxbElement.getValue());
		} else {
//			System.out.println("****** Unknown: " + element);
		}
	}

	private void visitCmDependencyMember(CmDependencyMember dependencyMember) {
		System.out.println("MrcmImportTransformer.visitCmDependencyMember(uid=" + dependencyMember.getUid() + ", dependencyPredicate=" + dependencyMember.getRefDependencyUid() + ")");
		if (predicateMap.containsKey(dependencyMember.getRefDependencyUid()) && predicateMap.containsKey(dependencyMember.getRefTestUid())) {
			ConceptModelPredicate parentPredicate = predicateMap.get(dependencyMember.getRefDependencyUid());
			ConceptModelPredicate childPredicate = predicateMap.get(dependencyMember.getRefTestUid());
			if (parentPredicate instanceof DependencyPredicate) {
				DependencyPredicate dependencyPredicate = (DependencyPredicate) parentPredicate;
				dependencyPredicate.getChildren().add(childPredicate);
			} else {
				System.err.println("Referenced predicate was not a DependencyPredicate.");
			}
		} else {
			System.err.println("Predicate not found when processing dependency member: " + dependencyMember.getUid());
		}
	}

	private void visitTestDependency(CmTestDependency testDependency) {
		System.out.println("MrcmImportTransformer.visitTestDependency(" +  testDependency.getUid() + ")");
		String uuid = testDependency.getUid();
		boolean status = mapStatus(testDependency.getStatus());
		DependencyOperator operator = mapOperator(testDependency.getOperator());
		GroupRule groupRule = mapGroupRule(testDependency.getGroupRule());
		DependencyPredicate dependencyPredicate = MrcmFactory.eINSTANCE.createDependencyPredicate();
		dependencyPredicate.setUuid(uuid);
		dependencyPredicate.setActive(status);
		dependencyPredicate.setOperator(operator);
		dependencyPredicate.setGroupRule(groupRule);
		addPredicateToMap(dependencyPredicate);
	}

	private void visitTestCardinality(CmTestCardinality testCardinality) {
		String uuid = testCardinality.getUid();
		boolean status = mapStatus(testCardinality.getStatus());
		int min = Integer.parseInt(testCardinality.getMinOccurs());
		int max = mapMaxCardinality(testCardinality.getMaxOccurs());

		CardinalityPredicate cardinalityPredicate = MrcmFactory.eINSTANCE.createCardinalityPredicate();
		cardinalityPredicate.setUuid(uuid);
		cardinalityPredicate.setActive(status);
		cardinalityPredicate.setMinCardinality(min);
		cardinalityPredicate.setMaxCardinality(max);
		cardinalityPredicate.setGroupRule(mapGroupRule(Integer.toString(testCardinality.getGroupRule())));

		if (predicateMap.containsKey(testCardinality.getRefTestUid())) {
			ConceptModelPredicate childPredicate = predicateMap.get(testCardinality.getRefTestUid());
			cardinalityPredicate.setPredicate(childPredicate);
		} else {
			System.err.println("Child test not found: " + testCardinality.getRefTestUid());
		}
		addPredicateToMap(cardinalityPredicate);
	}

	private void visitTestRelationship(CmTestRelationship testRelationship) {
		String uuid = testRelationship.getUid();
		boolean status = mapStatus(testRelationship.getStatus());
		RelationshipPredicate predicate = MrcmFactory.eINSTANCE.createRelationshipPredicate();
		predicate.setUuid(uuid);
		predicate.setActive(status);
		// default to 'defining' characteristic type
		predicate.setCharacteristicTypeConceptId(Concepts.STATED_RELATIONSHIP);
		if (topLevelConceptSetDefinitionMap.containsKey(testRelationship.getRefAttributeUid())) {
			predicate.setAttribute(topLevelConceptSetDefinitionMap.get(testRelationship.getRefAttributeUid()));
		}
		if (topLevelConceptSetDefinitionMap.containsKey(testRelationship.getRefRangeUid())) {
			predicate.setRange(topLevelConceptSetDefinitionMap.get(testRelationship.getRefRangeUid()));
		}
		addPredicateToMap(predicate);
	}

	private void visitRefSet(SifRefSetMrcm refSet) {
		String uuid = refSet.getUid();
		boolean status = mapStatus(refSet.getStatus());
		CompositeConceptSetDefinition conceptSetDefinition = MrcmFactory.eINSTANCE.createCompositeConceptSetDefinition();
		conceptSetDefinition.setUuid(uuid);
		conceptSetDefinition.setActive(status);
		topLevelConceptSetDefinitionMap.put(uuid, conceptSetDefinition);
	}

	private void visitRefSetMember(SifRefSetMember refSetMember) {
		String uuid = refSetMember.getUid();
		String tagCode = refSetMember.getTagCode();
		HierarchyInclusionType inclusionType = mapTagCode(tagCode);
		boolean status = mapStatus(refSetMember.getStatus());
		String conceptId = refSetMember.getComponentId();

		HierarchyConceptSetDefinition conceptSetDefinition = MrcmFactory.eINSTANCE.createHierarchyConceptSetDefinition();
		conceptSetDefinition.setUuid(uuid);
		conceptSetDefinition.setActive(status);
		conceptSetDefinition.setConceptId(conceptId);
		conceptSetDefinition.setInclusionType(inclusionType);
		
		if (topLevelConceptSetDefinitionMap.containsKey(refSetMember.getRefSetId())) {
			CompositeConceptSetDefinition compositeConceptSetDefinition = topLevelConceptSetDefinitionMap.get(refSetMember.getRefSetId());
			compositeConceptSetDefinition.addChild(conceptSetDefinition);
		} else {
			System.err.println("No ref set found for member: " + refSetMember.getUid());
		}
	}

	private void visitConstraint(CmConstraint constraint) {
		String uuid = constraint.getUid();
		boolean status = mapStatus(constraint.getStatus());
		ConstraintForm form = mapForm(constraint.getForm());
		ConstraintStrength strength = mapStrength(constraint.getStrength());
		
		AttributeConstraint attributeConstraint = MrcmFactory.eINSTANCE.createAttributeConstraint();
		attributeConstraint.setUuid(uuid);
		attributeConstraint.setActive(status);
		attributeConstraint.setStrength(strength);
		attributeConstraint.setForm(form);
		attributeConstraint.setDescription("Imported from IHTSDO MRCM XML.");
		
		if (topLevelConceptSetDefinitionMap.containsKey(constraint.getRefDomainUid())) {
			attributeConstraint.setDomain(topLevelConceptSetDefinitionMap.get(constraint.getRefDomainUid()));
		}
		
		if (predicateMap.containsKey(constraint.getRefTestUid())) {
			ConceptModelPredicate predicate = predicateMap.get(constraint.getRefTestUid());
			if (predicate instanceof CardinalityPredicate) {
				attributeConstraint.setPredicate(predicate);
			} else {
				// surround predicate with 0...* cardinality predicate
				System.out.println("Added constraint predicate wrapper to " + attributeConstraint);
				CardinalityPredicate cardinalityPredicate = MrcmFactory.eINSTANCE.createCardinalityPredicate();
				cardinalityPredicate.setUuid(UUID.randomUUID().toString());
				cardinalityPredicate.setActive(true);
				cardinalityPredicate.setMinCardinality(0);
				cardinalityPredicate.setMaxCardinality(-1);
				cardinalityPredicate.setGroupRule(GroupRule.ALL_GROUPS);
				cardinalityPredicate.setPredicate(predicate);
				attributeConstraint.setPredicate(cardinalityPredicate);
			}
		} else {
			System.err.println("Predicate not found: " + constraint.getRefTestUid());
		}
		
		transformedConstraints.add(attributeConstraint);
	}

	private void visitAfter(After after) {
		if (after.getComponent() != null)
			visit(after.getComponent());
	}

	private void visitChange(SifChange change) {
		visitAfter(change.getAfter());
	}

	private void visitChanges(SifChanges changes) {
		for (SifChange change : changes.getChange()) {
			visitChange(change);
		}
	}

	private void visitChangeSet(SifChangeSet changeSet) {
		for (SifChanges changes : changeSet.getChanges()) {
			visitChanges(changes);
		}
	}
	
	private void addPredicateToMap(ConceptModelPredicate predicate) {
		ConceptModelPredicate previous = predicateMap.put(predicate.getUuid(), predicate);
		if (previous != null)
			System.err.println("Previously added predicate: " + previous);
	}

	private HierarchyInclusionType mapTagCode(String tagCode) {
		if (SELF_TAG_CODE.equals(tagCode)) {
			return HierarchyInclusionType.SELF;
		} else if (SELF_OR_DESCENDANT_TAG_CODE.equals(tagCode)) {
			return HierarchyInclusionType.SELF_OR_DESCENDANT;
		} else if (DESCENDANT_TAG_CODE.equals(tagCode)) {
			return HierarchyInclusionType.DESCENDANT;
		}
		System.err.println("Unexpected tag code: " + tagCode);
		return HierarchyInclusionType.SELF;
	}

	private ConstraintForm mapForm(String form) {
		checkNotNull(form, "Constraint form must not be null.");
		if (ALL_FORMS.equals(form)) {
			return ConstraintForm.ALL_FORMS;
		} else if (DISTRIBUTION_FORM.equals(form)) {
			return ConstraintForm.DISTRIBUTION_FORM;
		} else if (STATED_FORM.equals(form)) {
			return ConstraintForm.STATED_FORM;
		} else if (CLOSE_TO_USER_FORM.equals(form)) {
			return ConstraintForm.CLOSE_TO_USER_FORM;
		} else if (LONG_NORMAL_FORM.equals(form)) {
			return ConstraintForm.LONG_NORMAL_FORM;
		} else if (SHORT_NORMAL_FORM.equals(form)) {
			return ConstraintForm.SHORT_NORMAL_FORM;
		}
		throw new IllegalArgumentException("Unexpected constraint form: " + form);
	}
	
	private ConstraintStrength mapStrength(String strength) {
		checkNotNull(strength, "Constraint strength must not be null.");
		if (MANDATORY_CM_STRENGTH.equals(strength)) {
			return ConstraintStrength.MANDATORY_CM;
		} else if (RECOMMENDED_CM_STRENGTH.equals(strength)) {
			return ConstraintStrength.RECOMMENDED_CM;
		} else if (ADVISORY_CM_STRENGTH.equals(strength)) {
			return ConstraintStrength.ADVISORY_CM;
		} else if (MANDATORY_PC_STRENGTH.equals(strength)) {
			return ConstraintStrength.MANDATORY_PC;
		} else if (INFORMATION_MODEL_PC_STRENGTH.equals(strength)) {
			return ConstraintStrength.INFORMATION_MODEL_PC;
		} else if (USE_CASE_SPECIFIC_PC_STRENGTH.equals(strength)) {
			return ConstraintStrength.USE_CASE_SPECIFIC_PC;
		} else if (IMPLEMENTATION_SPECIFIC_PC_STRENGTH.equals(strength)) {
			return ConstraintStrength.IMPLEMENTATION_SPECIFIC_PC;
		}
		throw new IllegalArgumentException("Unexpected constraint strength: " + strength);
	}
	
	private boolean mapStatus(short status) {
		if (ACTIVE_STATUS == status) {
			return true;
		} else if (INACTIVE_STATUS == status) {
			return false;
		}
		throw new IllegalArgumentException("Unexpected constraint status: " + status);
	}
	
	private GroupRule mapGroupRule(String groupRule) {
		checkNotNull(groupRule, "Group rule must not be null.");
		if (UNGROUPED_RULE.equals(groupRule)) {
			return GroupRule.UNGROUPED;
		} else if (UNSPECIFIED_GROUP_RULE.equals(groupRule)) {
			System.err.println("Unexpected group rule: " + groupRule + ", defaulting to " + GroupRule.ALL_GROUPS);
			return GroupRule.UNGROUPED;
		} else if (SINGLE_GROUP_RULE.equals(groupRule)) {
			return GroupRule.SINGLE_GROUP;
		} else if (MULTIPLE_GROUPS_RULE.equals(groupRule)) {
			return GroupRule.MULTIPLE_GROUPS;
		} else if (ALL_GROUPS_RULE.equals(groupRule)) {
			return GroupRule.ALL_GROUPS;
		}
		throw new IllegalArgumentException("Unexpected group rule: " + groupRule);
	}

	private DependencyOperator mapOperator(String operator) {
		checkNotNull(operator, "Operator must not be null.");
		if (ONE_DEPENDENCY_OPERATOR.equals(operator)) {
			return DependencyOperator.ONE;
		} else if (SOME_DEPENDENCY_OPERATOR.equals(operator)) {
			return DependencyOperator.SOME;
		} else if (ALL_DEPENDENCY_OPERATOR.equals(operator)) {
			return DependencyOperator.ALL;
		}
		throw new IllegalArgumentException("Unexpected group rule: " + operator);
	}
	
	private int mapMaxCardinality(String maxCardinality) {
		return "*".equals(maxCardinality) ? -1 : Integer.parseInt(maxCardinality);
	}

	/**
	 * For testing only.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 */
	public static void main(String[] args) throws FileNotFoundException, JAXBException {
		SifChangeSet sifChangeSet = PersistenceUtils.unmarshal(SifChangeSet.class, new FileInputStream(args[0]));
		MrcmImportTransformer visitor = new MrcmImportTransformer();
		visitor.visit(sifChangeSet);
		Set<ConstraintBase> transformedConstraints = visitor.getConstraints();
		Set<ConstraintBase> dependencyConstraints = Sets.filter(transformedConstraints, new Predicate<ConstraintBase>() {
			@Override
			public boolean apply(ConstraintBase input) {
				if (input instanceof AttributeConstraint) {
					AttributeConstraint attributeConstraint = (AttributeConstraint) input;
					if (attributeConstraint.getPredicate() instanceof DependencyPredicate)
						return true;
				}
				return false;
			}
		});
		System.out.println(dependencyConstraints.size());
		
		ConceptModel model = MrcmFactory.eINSTANCE.createConceptModel();
		model.getConstraints().addAll(transformedConstraints);
		
		PersistenceUtils.marshal(model, new FileOutputStream(args[1]));
		ConceptModel conceptModel = PersistenceUtils.unmarshal(ConceptModel.class, new FileInputStream(args[1]));
	}
}