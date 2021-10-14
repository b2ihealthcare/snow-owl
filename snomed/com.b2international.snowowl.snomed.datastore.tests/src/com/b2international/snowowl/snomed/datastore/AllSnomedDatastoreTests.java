/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.snomed.core.ecl.*;
import com.b2international.snowowl.snomed.core.tree.TerminologyTreeTest;
import com.b2international.snowowl.snomed.datastore.id.memory.DefaultSnomedIdentifierServiceRegressionTest;
import com.b2international.snowowl.snomed.datastore.id.memory.DefaultSnomedIdentifierServiceTest;
import com.b2international.snowowl.snomed.datastore.index.change.*;
import com.b2international.snowowl.snomed.datastore.index.entry.*;
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
	// ID service test cases
	SnomedIdentifierTest.class,
	SnomedIdentifierReservationServiceImplTest.class,
	DefaultSnomedIdentifierServiceTest.class,
	DefaultSnomedIdentifierServiceRegressionTest.class,
	// index document test cases 
	SnomedConceptDocumentSerializationTest.class,
	SnomedRelationshipIndexEntrySerializationTest.class,
	SnomedDescriptionIndexEntrySerializationTest.class,
	SnomedRefSetMemberDocumentSerializationTest.class,
	SnomedConceptDocumentTermSortTest.class,
	// change proc test cases
	DescriptionChangeProcessorTest.class,
	RelationshipChangeProcessorTest.class,
	TaxonomyPreCommitHookTest.class,
	PreferredDescriptionPreCommitHookTest.class,
	ConceptChangeProcessorAxiomTest.class,
	ConceptIconIdUpdaterTest.class,
	// ECL test cases
	SnomedEclEvaluationRequestTest.class,
	SnomedEclEvaluationRequestPropertyFilterTest.class,
	SnomedStatedEclEvaluationTest.class,
	SnomedEclRewriterTest.class,
	SnomedEclLabelerRequestTest.class,
	SnomedEclShortcutTest.class,
	SnomedDescriptionUtilsTest.class,
	// Validate Rule Evaluators
	SnomedQueryValidationRuleEvaluatorTest.class,
})
public class AllSnomedDatastoreTests {

}
