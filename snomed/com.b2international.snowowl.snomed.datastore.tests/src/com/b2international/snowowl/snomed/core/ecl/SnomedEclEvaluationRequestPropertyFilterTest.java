/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ecl;

import static com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator.generateDescriptionId;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.SyntaxException;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;

/**
 * @since 8.0
 */
public class SnomedEclEvaluationRequestPropertyFilterTest extends BaseSnomedEclEvaluationRequestTest {

	public SnomedEclEvaluationRequestPropertyFilterTest(String expressionForm, boolean statementsWithValue) {
		super(expressionForm, statementsWithValue);
	}

	@Test
	public void activeOnly() throws Exception {
		final Expression actual = eval("* {{ c active=true }}");
		final Expression expected = SnomedDocument.Expressions.active();
		assertEquals(expected, actual);
	}
	
	@Test
	public void inactiveOnly() throws Exception {
		final Expression actual = eval("* {{ c active=false }}");
		final Expression expected = SnomedDocument.Expressions.inactive();
		assertEquals(expected, actual);
	}
	
	@Test
	public void moduleId() throws Exception {
		final Expression actual = eval("* {{ c moduleId= " + Concepts.MODULE_SCT_CORE + " }}");
		final Expression expected = SnomedDocument.Expressions.modules(List.of(Concepts.MODULE_SCT_CORE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void term() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
			.id(generateDescriptionId())
			.active(true)
			.moduleId(Concepts.MODULE_SCT_CORE)
			.term("Clinical finding")
			.conceptId(Concepts.ROOT_CONCEPT)
			.typeId(Concepts.SYNONYM)
			.build());
		
		final Expression actual = eval("* {{ term = \"Clin find\" }}");
		final Expression expected = SnomedDocument.Expressions.ids(List.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void termLessThanTwoChars() throws Exception {
		eval("* {{ term = \"C\" }}");
	}
	
	@Test
	public void conjunctionActiveAndModuleId() throws Exception {
		final Expression actual = eval("* {{ c active = true, moduleId = " + Concepts.MODULE_SCT_CORE + " }}");
		final Expression expected = Expressions.builder()
			.filter(SnomedDocument.Expressions.active())
			.filter(SnomedDocument.Expressions.modules(List.of(Concepts.MODULE_SCT_CORE)))
			.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void termDisjunction() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Compressed natural gas")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.SYNONYM)
				.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Endocarditis")
				.conceptId(Concepts.MODULE_SCT_CORE)
				.typeId(Concepts.SYNONYM)
				.build());
			
		final Expression actual = eval("* {{ term = (match:\"gas\" wild:\"*itis\")}}");
		final Expression expected = SnomedDocument.Expressions.ids(List.of(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void disjunctionActiveAndModuleId() throws Exception {
		final Expression actual = eval("* {{ c active = true OR moduleId = " + Concepts.MODULE_SCT_CORE + " }}");
		final Expression expected = Expressions.builder()
			.should(SnomedDocument.Expressions.active())
			.should(SnomedDocument.Expressions.modules(List.of(Concepts.MODULE_SCT_CORE)))
			.build();
		assertEquals(expected, actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void conjunctionDomainInconsistency() throws Exception {
		eval("* {{ active=true AND definitionStatusId = "+ Concepts.MODULE_SCT_CORE +" }}");
	}
	
	@Test(expected = BadRequestException.class)
	public void disjunctionDomainInconsistency() throws Exception {
		eval("* {{ active=true OR definitionStatusId = "+ Concepts.MODULE_SCT_CORE +" }}");
	}
	
	@Test
	public void descriptionType() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
			.id(generateDescriptionId())
			.active(true)
			.moduleId(Concepts.MODULE_SCT_CORE)
			.term("Clinical finding")
			.conceptId(Concepts.ROOT_CONCEPT)
			.typeId(Concepts.TEXT_DEFINITION)
			.build());
		
		final Expression actual = eval("* {{ typeId = " + Concepts.TEXT_DEFINITION + " }}");
		final Expression expected = SnomedDocument.Expressions.ids(List.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void conjunctionAmbiguity() throws Exception {
		eval("* {{ active=true AND moduleId = " + Concepts.MODULE_SCT_CORE + " OR term=\"clinical finding\" }}");
	}
	
	@Test(expected = BadRequestException.class)
	public void disjunctionAmbiguity() throws Exception {
		eval("* {{ active=true OR moduleId = " + Concepts.MODULE_SCT_CORE + " AND term=\"clinical finding\" }}");
	}
	
	@Test(expected = BadRequestException.class)
	public void exclusionAmbiguity() throws Exception {
		eval("* {{ active=true OR moduleId = " + Concepts.MODULE_SCT_CORE +" MINUS term=\"clinical finding\" }}");
	}
	
	@Test
	public void multiDomainQueryAnd() throws Exception {
		Expression actual = eval("* {{ c active=false }} AND * {{ d term=\"clin find\" }}");
		Expression expected = Expressions.builder()
			.filter(SnomedDocument.Expressions.inactive())
			.filter(SnomedDocument.Expressions.ids(Collections.emptySet()))
			.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void multiDomainQueryOr() throws Exception {
		Expression actual = eval("* {{ c active=false }} OR * {{ d term=\"clin find\" }}");
		Expression expected = Expressions.builder()
			.should(SnomedDocument.Expressions.inactive())
			.should(SnomedDocument.Expressions.ids(Collections.emptySet()))
			.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void multiDomainQueryExclusion() throws Exception {
		Expression actual = eval("* {{ c active=false }} MINUS * {{ d term=\"clin find\" }}");
		Expression expected = Expressions.builder()
			.filter(SnomedDocument.Expressions.inactive())
			.mustNot(SnomedDocument.Expressions.ids(Collections.emptySet()))
			.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void preferredIn() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
			.id(generateDescriptionId())
			.active(true)
			.moduleId(Concepts.MODULE_SCT_CORE)
			.term("Clinical finding")
			.conceptId(Concepts.ROOT_CONCEPT)
			.typeId(Concepts.TEXT_DEFINITION)
			.preferredIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
			.acceptableIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_US))
			.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
			.id(generateDescriptionId())
			.active(true)
			.moduleId(Concepts.MODULE_SCT_CORE)
			.term("Clinical finding")
			.conceptId(Concepts.SUBSTANCE)
			.typeId(Concepts.TEXT_DEFINITION)
			.preferredIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_US))
			.acceptableIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
			.build());
		
		final Expression actual = eval("* {{ preferredIn = " + Concepts.REFSET_LANGUAGE_TYPE_UK + " }}");
		final Expression expected = SnomedDocument.Expressions.ids(List.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void acceptableIn() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
			.id(generateDescriptionId())
			.active(true)
			.moduleId(Concepts.MODULE_SCT_CORE)
			.term("Clinical finding")
			.conceptId(Concepts.ROOT_CONCEPT)
			.typeId(Concepts.TEXT_DEFINITION)
			.preferredIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
			.acceptableIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_US))
			.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
			.id(generateDescriptionId())
			.active(true)
			.moduleId(Concepts.MODULE_SCT_CORE)
			.term("Clinical finding")
			.conceptId(Concepts.SUBSTANCE)
			.typeId(Concepts.TEXT_DEFINITION)
			.preferredIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_US))
			.acceptableIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
			.build());
		
		final Expression actual = eval("* {{ acceptableIn = " + Concepts.REFSET_LANGUAGE_TYPE_UK + " }}");
		final Expression expected = SnomedDocument.Expressions.ids(List.of(Concepts.SUBSTANCE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void languageRefSet() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.acceptableIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.SUBSTANCE)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.acceptableIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.build());
		
		final Expression actual = eval("* {{ languageRefSetId = " + Concepts.REFSET_LANGUAGE_TYPE_UK + " }}");
		final Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void semanticTag() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding (finding)")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.acceptableIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding (clinical)")
				.conceptId(Concepts.SUBSTANCE)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.acceptableIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding (disorder)")
				.conceptId(Concepts.ATTRIBUTE)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.acceptableIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.build());
		
		
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.SUBSTANCE));
		Expression actual = eval("* {{ semanticTag = \"clinical\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.SUBSTANCE, Concepts.ATTRIBUTE));
		actual = eval("* {{ semanticTag != \"finding\" }}");
		assertEquals(expected, actual);
	}
	
	@Test
	public void descriptionEffectiveTime() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.released(true)
				.effectiveTime(EffectiveTimes.getEffectiveTime("20210731", DateFormats.SHORT))
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding (finding)")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.acceptableIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.released(true)
				.effectiveTime(EffectiveTimes.getEffectiveTime("20020131", DateFormats.SHORT))
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding (clinical)")
				.conceptId(Concepts.SUBSTANCE)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.acceptableIn(Set.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.build());
		
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		Expression actual = eval("* {{ effectiveTime = \"20210731\" }}");
		assertEquals(expected, actual);

		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		actual = eval("* {{ effectiveTime > \"20210605\" }}");
		assertEquals(expected, actual);

		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.SUBSTANCE));
		actual = eval("* {{ effectiveTime < \"20020201\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ effectiveTime >= \"20020131\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ effectiveTime >= \"20010731\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ effectiveTime <= \"20210731\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ effectiveTime <= \"20211030\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ effectiveTime != \"20211030\" }}");
		assertEquals(expected, actual);
	}
	
	@Test
	public void conceptEffectiveTime() throws Exception {
		indexRevision(MAIN, SnomedConceptDocument.builder()
				.id(Concepts.FINDING_SITE)
				.active(true)
				.released(true)
				.effectiveTime(EffectiveTimes.getEffectiveTime("20210731", DateFormats.SHORT))
				.moduleId(Concepts.MODULE_SCT_CORE)
				.build());
		
		indexRevision(MAIN, SnomedConceptDocument.builder()
				.id(Concepts.HAS_ACTIVE_INGREDIENT)
				.active(true)
				.released(true)
				.effectiveTime(EffectiveTimes.getEffectiveTime("20020131", DateFormats.SHORT))
				.moduleId(Concepts.MODULE_SCT_CORE)
				.build());
		
		Expression expected = SnomedDocument.Expressions.effectiveTime(EffectiveTimes.getEffectiveTime("20210731", DateFormats.SHORT));
		Expression actual = eval("* {{ c effectiveTime = \"20210731\" }}");
		assertEquals(expected, actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void invalidLanguageCode() throws Exception {
		eval("* {{ language = \"en-sg\" }}");
	}
	
	@Test
	public void languageCode() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.TEXT_DEFINITION)
				.languageCode("en")
				.build());
		
		Expression actual = eval("* {{ language = en }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void caseSignificanceId() throws Exception {
		generatePreferredDescription(Concepts.ROOT_CONCEPT);
		
		Expression actual = eval("* {{ caseSignificanceId = " + Concepts.ENTIRE_TERM_CASE_INSENSITIVE + " }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}

	@Test
	public void dialectAnyAcceptability() throws Exception {
		generatePreferredDescription(Concepts.ROOT_CONCEPT);
		generateAcceptableDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialect = en-gb }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectAnyAcceptabilityNotEquals() throws Exception {
		generatePreferredDescription(Concepts.ROOT_CONCEPT);
		generateAcceptableDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialect != en-gb }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of());
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectPreferred() throws Exception {
		generatePreferredDescription(Concepts.ROOT_CONCEPT);
		// extra acceptable description on another concept to demonstrate that it won't match
		generateAcceptableDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialect = en-gb (prefer) }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectPreferredNotEquals() throws Exception {
		generatePreferredDescription(Concepts.ROOT_CONCEPT);
		// extra acceptable description on another concept to demonstrate that it won't match
		generateAcceptableDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialect != en-gb (prefer) }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectAcceptable() throws Exception {
		generateAcceptableDescription(Concepts.ROOT_CONCEPT);
		// extra preferred description on another concept to demonstrate that it won't match
		generatePreferredDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialect = en-gb (accept) }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectAcceptableNotEquals() throws Exception {
		generateAcceptableDescription(Concepts.ROOT_CONCEPT);
		// extra preferred description on another concept to demonstrate that it won't match
		generatePreferredDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialect != en-gb (accept) }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectIdAnyAcceptability() throws Exception {
		generatePreferredDescription(Concepts.ROOT_CONCEPT);
		generateAcceptableDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialectId = " + Concepts.REFSET_LANGUAGE_TYPE_UK + " }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectIdAnyAcceptabilityNotEquals() throws Exception {
		generatePreferredDescription(Concepts.ROOT_CONCEPT);
		generateAcceptableDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialectId != " + Concepts.REFSET_LANGUAGE_TYPE_UK + " }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of());
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectIdPreferred() throws Exception {
		generatePreferredDescription(Concepts.ROOT_CONCEPT);
		// extra acceptable description on another concept to demonstrate that it won't match
		generateAcceptableDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialectId = " + Concepts.REFSET_LANGUAGE_TYPE_UK + " (prefer) }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectIdPreferredNotEquals() throws Exception {
		generatePreferredDescription(Concepts.ROOT_CONCEPT);
		// extra acceptable description on another concept to demonstrate that it won't match
		generateAcceptableDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialectId != " + Concepts.REFSET_LANGUAGE_TYPE_UK + " (prefer) }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectIdAcceptable() throws Exception {
		generateAcceptableDescription(Concepts.ROOT_CONCEPT);
		// extra preferred description on another concept to demonstrate that it won't match
		generatePreferredDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialectId = " + Concepts.REFSET_LANGUAGE_TYPE_UK + " (accept) }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectIdAcceptableNotEquals() throws Exception {
		generateAcceptableDescription(Concepts.ROOT_CONCEPT);
		// extra preferred description on another concept to demonstrate that it won't match
		generatePreferredDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialectId != " + Concepts.REFSET_LANGUAGE_TYPE_UK + " (accept) }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dialectIdAcceptableId() throws Exception {
		generateAcceptableDescription(Concepts.ROOT_CONCEPT);
		// extra preferred description on another concept to demonstrate that it won't match
		generatePreferredDescription(Concepts.MODULE_ROOT);
		
		Expression actual = eval("* {{ dialectId != " + Concepts.REFSET_LANGUAGE_TYPE_UK + " (" + Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE + ") }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test(expected = SyntaxException.class)
	public void dialectUnsupportedOperator() throws Exception {
		eval("* {{ dialect > en-gb (preferred) }}");
	}
	
	@Test(expected = SyntaxException.class)
	public void dialectUnknownAcceptability() throws Exception {
		eval("* {{ dialect = en-gb (unknown) }}");
	}
	
	@Test
	public void dialectUnknownAlias() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.TEXT_DEFINITION)
				.languageCode("en")
				.caseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
				.acceptabilityMap(Map.of(
					Concepts.REFSET_LANGUAGE_TYPE_SG, Acceptability.ACCEPTABLE
				))
				.build());
		
		Expression actual = eval("* {{ dialect = en-sg }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of());
		assertEquals(expected, actual);
	}
	
	@Test
	public void definitionStatus() throws Exception {
		Expression actual1 = eval("* {{ c definitionStatusId = 900000000000073002 }}");
		Expression actual2 = eval("* {{ c definitionStatus = defined }}");
		Expression expected = SnomedConceptDocument.Expressions.definitionStatusIds(Set.of(Concepts.FULLY_DEFINED));
		assertEquals(expected, actual1);
		assertEquals(expected, actual2);
	}
	
	@Test
	public void definitionStatusNotEquals() throws Exception {
		Expression actual1 = eval("* {{ c definitionStatusId != 900000000000074008 }}");
		Expression actual2 = eval("* {{ c definitionStatus != primitive }}");
		Expression expected = Expressions.builder()
			.mustNot(SnomedConceptDocument.Expressions.definitionStatusIds(Set.of(Concepts.PRIMITIVE)))
			.build();
		
		assertEquals(expected, actual1);
		assertEquals(expected, actual2);
	}
	
	private void generatePreferredDescription(String conceptId) {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(conceptId)
				.typeId(Concepts.TEXT_DEFINITION)
				.languageCode("en")
				.caseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
				.acceptabilityMap(Map.of(
					Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED
				))
				.build());
	}
	
	private void generateAcceptableDescription(String conceptId) {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(conceptId)
				.typeId(Concepts.TEXT_DEFINITION)
				.languageCode("en")
				.caseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
				.acceptabilityMap(Map.of(
					Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE
				))
				.build());
	}
	
}
