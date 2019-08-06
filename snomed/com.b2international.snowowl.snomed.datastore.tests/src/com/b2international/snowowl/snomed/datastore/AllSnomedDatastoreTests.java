/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.snowowl.snomed.core.ecl.SnomedEclEvaluationRequestTest;
import com.b2international.snowowl.snomed.core.ecl.SnomedEclRewriterTest;
import com.b2international.snowowl.snomed.core.ecl.SnomedStatedEclEvaluationTest;
import com.b2international.snowowl.snomed.core.ql.SnomedQueryEvaluationRequestTest;
import com.b2international.snowowl.snomed.core.tree.TerminologyTreeTest;
import com.b2international.snowowl.snomed.datastore.id.memory.DefaultSnomedIdentifierServiceRegressionTest;
import com.b2international.snowowl.snomed.datastore.id.memory.DefaultSnomedIdentifierServiceTest;
import com.b2international.snowowl.snomed.datastore.index.change.ConceptChangeProcessorAxiomTest;
import com.b2international.snowowl.snomed.datastore.index.change.ConceptChangeProcessorTest;
import com.b2international.snowowl.snomed.datastore.index.change.ConstraintChangeProcessorTest;
import com.b2international.snowowl.snomed.datastore.index.change.DescriptionChangeProcessorTest;
import com.b2international.snowowl.snomed.datastore.index.change.RefSetMemberChangeProcessorTest;
import com.b2international.snowowl.snomed.datastore.index.change.RelationshipChangeProcessorTest;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocumentTermSortTest;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntrySerializationTest;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConstraintDocumentSerializationTest;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntrySerializationTest;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberDocumentSerializationTest;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntrySerializationTest;
import com.b2international.snowowl.snomed.datastore.internal.id.SnomedIdentifierTest;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.ReservationImplTest;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImplTest;
import com.b2international.snowowl.snomed.validation.SnomedQueryValidationRuleEvaluatorTest;

/**
 * @since 4.0
 */
@RunWith(Suite.class)
@SuiteClasses({
	TerminologyTreeTest.class,
	ReservationImplTest.class,
	SnomedIdentifierTest.class,
	SnomedIdentifierReservationServiceImplTest.class,
	DefaultSnomedIdentifierServiceTest.class,
	DefaultSnomedIdentifierServiceRegressionTest.class,
	SnomedConceptIndexEntrySerializationTest.class,
	SnomedRelationshipIndexEntrySerializationTest.class,
	SnomedDescriptionIndexEntrySerializationTest.class,
	SnomedConstraintDocumentSerializationTest.class,
	SnomedRefSetMemberDocumentSerializationTest.class,
	// change proc test cases
	DescriptionChangeProcessorTest.class,
	RelationshipChangeProcessorTest.class,
	RefSetMemberChangeProcessorTest.class,
	ConstraintChangeProcessorTest.class,
	ConceptChangeProcessorTest.class,
	ConceptChangeProcessorAxiomTest.class,
	SnomedEclEvaluationRequestTest.class,
	SnomedStatedEclEvaluationTest.class,
	SnomedEclRewriterTest.class,
	SnomedQueryEvaluationRequestTest.class,
	SnomedQueryValidationRuleEvaluatorTest.class,
	SnomedConceptDocumentTermSortTest.class
})
public class AllSnomedDatastoreTests {

}
