/*******************************************************************************
 * Copyright (c) 2018 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.validation.snomed;


import static com.b2international.snowowl.validation.snomed.util.DocumentBuilders.relationship;
import static com.b2international.snowowl.validation.snomed.util.DocumentBuilders.description;
import static com.b2international.snowowl.validation.snomed.util.DocumentBuilders.concept;
import static com.b2international.snowowl.validation.snomed.util.RandomSnomedIdentiferGenerator.generateConceptId;
import static com.b2international.snowowl.validation.snomed.util.RandomSnomedIdentiferGenerator.generateDescriptionId;


import org.junit.Test;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * 
 * @since 6.4
 */
public class GenericValidationRuleTest extends BaseGenericValidationRuleTest {
	
	@Test
	public void ruleSnomedCommon1() throws Exception {
		final String ruleId = "snomed-common-1";
		indexRule(ruleId);
		
		SnomedConceptDocument inactiveDestinationConcept = concept(generateConceptId()).active(false).build();
		indexRevision(MAIN, nextStorageKey(), inactiveDestinationConcept);
		
		SnomedConceptDocument inactiveSourceConcept = concept(generateConceptId()).active(false).build();
		indexRevision(MAIN, nextStorageKey(), inactiveSourceConcept);

		SnomedConceptDocument inactiveTypeConcept = concept(generateConceptId()).active(false).build();
		indexRevision(MAIN, nextStorageKey(), inactiveTypeConcept);
		
		SnomedConceptDocument activeConcept = concept(generateConceptId()).build();
		indexRevision(MAIN, nextStorageKey(), activeConcept);
		
		SnomedRelationshipIndexEntry invalidSourceRelationship = relationship(inactiveSourceConcept.getId(), Concepts.IS_A, activeConcept.getId()).build();
		indexRevision(MAIN, nextStorageKey(), invalidSourceRelationship);
		
		SnomedRelationshipIndexEntry invalidDestinationRelationship = relationship(activeConcept.getId(), Concepts.IS_A, inactiveDestinationConcept.getId()).build();
		indexRevision(MAIN, nextStorageKey(), invalidDestinationRelationship);
		
		SnomedRelationshipIndexEntry invalidTypeRelationship = relationship(activeConcept.getId(), inactiveTypeConcept.getId(), Concepts.FINDING_SITE).build();
		indexRevision(MAIN, nextStorageKey(), invalidTypeRelationship);
		
		SnomedRelationshipIndexEntry validRelationship = relationship(activeConcept.getId(), Concepts.IS_A, Concepts.FINDING_SITE).build();
		indexRevision(MAIN, nextStorageKey(), validRelationship);
		
		ValidationIssues validationIssues = validate(ruleId);
		
		assertAffectedComponents(validationIssues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, invalidSourceRelationship.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, invalidDestinationRelationship.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, invalidTypeRelationship.getId()));
		
		
	}
	
	@Test
	public void ruleSnomedCommon2() throws Exception {
		final String ruleId = "snomed-common-2";
		indexRule(ruleId);

		// index three concepts
		SnomedConceptDocument c1 = concept(generateConceptId()).build();
		indexRevision(MAIN, nextStorageKey(), c1);
		SnomedDescriptionIndexEntry d1 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Hello World!").conceptId(c1.getId())
				.build();
		indexRevision(MAIN, nextStorageKey(), d1);

		SnomedConceptDocument c2 = concept(generateConceptId()).build();
		indexRevision(MAIN, nextStorageKey(), c2);
		SnomedDescriptionIndexEntry d2 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Hello World!").conceptId(c2.getId())
				.build();
		indexRevision(MAIN, nextStorageKey(), d2);

		SnomedConceptDocument c3 = concept(generateConceptId()).build();
		indexRevision(MAIN, nextStorageKey(), c3);
		SnomedDescriptionIndexEntry d3 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Hello Cruel World!")
				.conceptId(c3.getId()).build();
		indexRevision(MAIN, nextStorageKey(), d3);

		ValidationIssues issues = validate(ruleId);

		assertAffectedComponents(issues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, d1.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, d2.getId()));
	}
	
}
