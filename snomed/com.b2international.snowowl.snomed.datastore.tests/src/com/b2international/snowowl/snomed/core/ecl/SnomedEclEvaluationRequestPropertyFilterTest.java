/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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
import static org.junit.Assert.*;

import java.util.*;

import org.junit.ClassRule;
import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.SyntaxException;
import com.b2international.index.SynonymsRule;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * @since 8.0
 */
public class SnomedEclEvaluationRequestPropertyFilterTest extends BaseSnomedEclEvaluationRequestTest {

	@ClassRule
	public static SynonymsRule synonyms = new SynonymsRule(
		"history,previous"
	);
	
	@Test
	public void concept_activeOnly() throws Exception {
		final Expression actual = eval("* {{ c active = true }}");
		final Expression expected = SnomedDocument.Expressions.active();
		assertEquals(expected, actual);
	}
	
	@Test
	public void concept_inactiveOnly() throws Exception {
		final Expression actual = eval("* {{ c active = false }}");
		final Expression expected = SnomedDocument.Expressions.inactive();
		assertEquals(expected, actual);
	}
	
	@Test
	public void concept_active_notequals() throws Exception {
		final Expression actual = eval("* {{ c active != false }}");
		final Expression expected = Expressions.bool().mustNot(SnomedDocument.Expressions.active(false)).build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void description_activeOnly() throws Exception {
		generateActiveAndInactiveDescription(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT);
		
		final Expression actual = eval("* {{ d active = true }}");
		final Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void description_inactiveOnly() throws Exception {
		generateActiveAndInactiveDescription(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT);
		
		final Expression actual = eval("* {{ d active = false }}");
		final Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void description_active_notequals() throws Exception {
		generateActiveAndInactiveDescription(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT);
		
		final Expression actual = eval("* {{ d active != false }}");
		final Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}

	@Test
	public void member_activeOnly() throws Exception {
		generateActiveAndInactiveSimpleTypeMember(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT);
		
		final Expression actual = eval("* {{ m active = true }}");
		final Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void member_inactiveOnly() throws Exception {
		generateActiveAndInactiveSimpleTypeMember(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT);
		
		final Expression actual = eval("* {{ m active = false }}");
		final Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void member_active_notequals() throws Exception {
		generateActiveAndInactiveSimpleTypeMember(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT);
		
		final Expression actual = eval("* {{ m active != false }}");
		final Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void member_moduleId() throws Exception {
		generateActiveAndInactiveSimpleTypeMember(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, Concepts.MODULE_ROOT, Concepts.MODULE_SCT_MODEL_COMPONENT);
		
		final Expression actual = eval("* {{ m moduleId = " + Concepts.MODULE_SCT_CORE + " }}");
		final Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void member_moduleId_notequals() throws Exception {
		generateActiveAndInactiveSimpleTypeMember(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, Concepts.MODULE_ROOT, Concepts.MODULE_SCT_MODEL_COMPONENT);
		
		final Expression actual = eval("* {{ m moduleId != " + Concepts.MODULE_SCT_CORE + " }}");
		final Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void moduleId() throws Exception {
		final Expression actual = eval("* {{ c moduleId = " + Concepts.MODULE_SCT_CORE + " }}");
		final Expression expected = SnomedDocument.Expressions.modules(List.of(Concepts.MODULE_SCT_CORE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void moduleId_notequals() throws Exception {
		final Expression actual = eval("* {{ c moduleId != " + Concepts.MODULE_SCT_CORE + " }}");
		final Expression expected = Expressions.bool().mustNot(SnomedDocument.Expressions.modules(List.of(Concepts.MODULE_SCT_CORE))).build();
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
		final Expression expected = Expressions.bool()
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
	public void termMatchSynonymsDisabled() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("History related concept")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.SYNONYM)
				.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Concept with previous word in it")
				.conceptId(Concepts.MODULE_SCT_CORE)
				.typeId(Concepts.SYNONYM)
				.build());
			
		final Expression actual = eval("* {{ term = \"history\" }}");
		final Expression expected = SnomedDocument.Expressions.ids(List.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void termWildCaseInsensitive() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("History related concept")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.SYNONYM)
				.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Concept with history")
				.conceptId(Concepts.MODULE_SCT_CORE)
				.typeId(Concepts.SYNONYM)
				.build());
			
		final Expression actual = eval("* {{ term = wild:\"*history*\" }}");
		final Expression expected = SnomedDocument.Expressions.ids(List.of(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void termWildElasticsearchOptionalFlagsShouldNotInterfere() throws Exception {
		// https://snowowl.atlassian.net/browse/SO-5906
		// optional ES specific regex operators should be disabled otherwise special characters in terms cannot be recognized
		// previously this thrown a low level search error, now it returns as intended
		final Expression actual = eval("* {{ term = wild:\"*random term with optional < ES regexp character\" }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of());
		assertEquals(expected, actual);
	}
	
	@Test
	public void termWildAnyCharacterShouldNotCreateQueryClause() throws Exception {
		final Expression actualTwo = eval("* {{ term = wild:\"**\" }}");
		final Expression actualThree = eval("* {{ term = wild:\"***\" }}");
		Expression expected = Expressions.matchAll();
		assertEquals(expected, actualTwo);
		assertEquals(expected, actualThree);
	}
	
	@Test
	public void termRegexAnyCharacterShouldNotCreateQueryClause() throws Exception {
		final Expression actualOne = eval("* {{ term = regex:\".*\" }}");
		final Expression actualTwo = eval("* {{ term = regex:\".*.*\" }}");
		final Expression actualThree = eval("* {{ term = regex:\".*.*.*\" }}");
		Expression expected = Expressions.matchAll();
		assertEquals(expected, actualOne);
		assertEquals(expected, actualTwo);
		assertEquals(expected, actualThree);
	}
	
	@Test
	public void termWildEscapedAnyCharacter() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding with * character")
				.conceptId(Concepts.ALL_SNOMEDCT_CONTENT)
				.typeId(Concepts.TEXT_DEFINITION)
				.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding without the any character")
				.conceptId(Concepts.ALL_PRECOORDINATED_CONTENT)
				.typeId(Concepts.TEXT_DEFINITION)
				.build());
		
		final Expression actual = eval("* {{ term = wild:\"*\\\\**\" }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ALL_SNOMEDCT_CONTENT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void termRegexEscapedAnyCharacter() throws Exception {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding with * character")
				.conceptId(Concepts.ALL_SNOMEDCT_CONTENT)
				.typeId(Concepts.TEXT_DEFINITION)
				.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding without the any character")
				.conceptId(Concepts.ALL_PRECOORDINATED_CONTENT)
				.typeId(Concepts.TEXT_DEFINITION)
				.build());
		
		final Expression actual = eval("* {{ term = regex:'.*[\\*].*\' }}");
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ALL_SNOMEDCT_CONTENT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void disjunctionActiveAndModuleId() throws Exception {
		final Expression actual = eval("* {{ c active = true OR moduleId = " + Concepts.MODULE_SCT_CORE + " }}");
		final Expression expected = Expressions.bool()
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
	
	@Test
	public void descriptionId() throws Exception {
		final String descriptionId = generateDescriptionId();

		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
			.id(descriptionId)
			.active(true)
			.moduleId(Concepts.MODULE_SCT_CORE)
			.term("Clinical finding")
			.conceptId(Concepts.ROOT_CONCEPT)
			.typeId(Concepts.TEXT_DEFINITION)
			.build());
		
		final Expression actual = eval("* {{ id = " + descriptionId + " }}");
		final Expression expected = SnomedDocument.Expressions.ids(List.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void conceptId() throws Exception {
		final String conceptId = Concepts.TEXT_DEFINITION;
		final Expression actual = eval("* {{ C id = " + conceptId + " }}");
		final Expression expected = SnomedDocument.Expressions.id(conceptId);
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
		Expression expected = Expressions.bool()
			.filter(SnomedDocument.Expressions.inactive())
			.filter(SnomedDocument.Expressions.ids(Collections.emptySet()))
			.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void multiDomainQueryOr() throws Exception {
		Expression actual = eval("* {{ c active=false }} OR * {{ d term=\"clin find\" }}");
		Expression expected = Expressions.bool()
			.should(SnomedDocument.Expressions.inactive())
			.should(SnomedDocument.Expressions.ids(Collections.emptySet()))
			.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void multiDomainQueryExclusion() throws Exception {
		Expression actual = eval("* {{ c active=false }} MINUS * {{ d term=\"clin find\" }}");
		Expression expected = Expressions.bool()
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
		
		// XXX injecting domain before effectiveTime field randomly to test default description domain and explicit domain cases
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		Expression actual = eval("* {{ effectiveTime = \"20210731\" }}");
		assertEquals(expected, actual);

		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		actual = eval("* {{ d effectiveTime > \"20210605\" }}");
		assertEquals(expected, actual);

		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.SUBSTANCE));
		actual = eval("* {{ effectiveTime < \"20020201\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ d effectiveTime >= \"20020131\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ effectiveTime >= \"20010731\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ d effectiveTime <= \"20210731\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ effectiveTime <= \"20211030\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ d effectiveTime != \"20211030\" }}");
		assertEquals(expected, actual);
	}
	
	@Test
	public void memberEffectiveTime() throws Exception {
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referenceSetType(SnomedRefSetType.SIMPLE)
				.refsetId(Concepts.REFSET_SIMPLE_TYPE)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.effectiveTime(EffectiveTimes.getEffectiveTime("20210731", DateFormats.SHORT))
				.released(true)
				.build());
		
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.SUBSTANCE)
				.referenceSetType(SnomedRefSetType.SIMPLE)
				.refsetId(Concepts.REFSET_SIMPLE_TYPE)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.effectiveTime(EffectiveTimes.getEffectiveTime("20020131", DateFormats.SHORT))
				.released(true)
				.build());
		
		// XXX injecting domain before effectiveTime field randomly to test default description domain and explicit domain cases
		Expression expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		Expression actual = eval("* {{ m effectiveTime = \"20210731\" }}");
		assertEquals(expected, actual);

		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT));
		actual = eval("* {{ m effectiveTime > \"20210605\" }}");
		assertEquals(expected, actual);

		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.SUBSTANCE));
		actual = eval("* {{ m effectiveTime < \"20020201\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ m effectiveTime >= \"20020131\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ m effectiveTime >= \"20010731\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ m effectiveTime <= \"20210731\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ m effectiveTime <= \"20211030\" }}");
		assertEquals(expected, actual);
		
		expected = SnomedDocument.Expressions.ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		actual = eval("* {{ m effectiveTime != \"20211030\" }}");
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
		Expression expected = Expressions.bool()
			.mustNot(SnomedConceptDocument.Expressions.definitionStatusIds(Set.of(Concepts.PRIMITIVE)))
			.build();
		
		assertEquals(expected, actual1);
		assertEquals(expected, actual2);
	}
	
	@Test
	public void member_referencedComponentId_filter_string_eq() throws Exception {
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.SUBSTANCE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.targetComponentId(Concepts.ROOT_CONCEPT)
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.targetComponentId(Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE)
				.build());
		
		Expression actual = eval("* {{ m referencedComponentId = " + Concepts.SUBSTANCE + " }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(Concepts.SUBSTANCE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void member_refsetFieldName_filter_string_eq() throws Exception {
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.SUBSTANCE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.targetComponentId(Concepts.ROOT_CONCEPT)
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.targetComponentId(Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE)
				.build());
		
		Expression actual = eval("* {{ m targetComponentId = " + Concepts.ROOT_CONCEPT + " }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(Concepts.SUBSTANCE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void member_refsetFieldName_filter_string_ne() throws Exception {
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.SUBSTANCE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.targetComponentId(Concepts.ROOT_CONCEPT)
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.targetComponentId(Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE)
				.build());
		
		Expression actual = eval("* {{ m targetComponentId != " + Concepts.ROOT_CONCEPT + " }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(Concepts.CONCEPT_MODEL_ATTRIBUTE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void member_refsetFieldName_filter_integer_eq() throws Exception {
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.SUBSTANCE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.mapGroup(1)
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.mapGroup(2)
				.build());
		
		Expression actual = eval("* {{ m mapGroup = #1 }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(Concepts.SUBSTANCE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void member_refsetFieldName_filter_integer_ne() throws Exception {
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.SUBSTANCE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.mapGroup(1)
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.mapGroup(2)
				.build());
		
		Expression actual = eval("* {{ m mapGroup != #1 }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(Concepts.CONCEPT_MODEL_ATTRIBUTE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void member_refsetFieldName_filter_boolean_eq() throws Exception {
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.SUBSTANCE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.grouped(true)
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.grouped(false)
				.build());
		
		Expression actual = eval("* {{ m grouped = true }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(Concepts.SUBSTANCE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void member_refsetFieldName_filter_boolean_ne() throws Exception {
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.SUBSTANCE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.grouped(true)
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.grouped(false)
				.build());
		
		Expression actual = eval("* {{ m grouped != true }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(Concepts.CONCEPT_MODEL_ATTRIBUTE));
		assertEquals(expected, actual);
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
	
	private void generateActiveAndInactiveDescription(String activeDescriptionConceptId, String inactiveDescriptionConceptId) {
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(activeDescriptionConceptId)
				.typeId(Concepts.TEXT_DEFINITION)
				.languageCode("en")
				.caseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
				.acceptabilityMap(Map.of(
					Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE
				))
				.build());
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(inactiveDescriptionConceptId)
				.typeId(Concepts.TEXT_DEFINITION)
				.languageCode("en")
				.caseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
				.acceptabilityMap(Map.of(
					Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE
				))
				.build());
	}
	
	
	private void generateActiveAndInactiveSimpleTypeMember(String activeMemberConceptId, String inactiveMemberConceptId) {
		generateActiveAndInactiveSimpleTypeMember(activeMemberConceptId, Concepts.MODULE_SCT_CORE, inactiveMemberConceptId, Concepts.MODULE_SCT_MODEL_COMPONENT);
	}
	
	private void generateActiveAndInactiveSimpleTypeMember(String activeMemberConceptId, String activeMemberModuleId, String inactiveMemberConceptId, String inactiveMemberModuleId) {
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.referencedComponentId(activeMemberConceptId)
				.referenceSetType(SnomedRefSetType.SIMPLE)
				.refsetId(Concepts.REFSET_SIMPLE_TYPE)
				.moduleId(activeMemberModuleId)
				.build());
		
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(false)
				.referencedComponentId(inactiveMemberConceptId)
				.referenceSetType(SnomedRefSetType.SIMPLE)
				.refsetId(Concepts.REFSET_SIMPLE_TYPE)
				.moduleId(inactiveMemberModuleId)
				.build());
	}
	
}
