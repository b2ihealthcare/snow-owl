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
package com.b2international.snowowl.snomed.ql.tests

import com.b2international.snowowl.snomed.ql.ql.Query
import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.runner.RunWith

import static org.junit.Assert.assertNotNull
import org.junit.Test

@RunWith(XtextRunner)
@InjectWith(QLInjectorProvider)
class QLParsingTest {

	@Inject extension ParseHelper<Query>
	@Inject extension ValidationTestHelper
	
	@Test
	def void test_empty() {
		''.assertNoErrors;
	}
	
	@Test
	def void test_whitespaces() {
		' \n \t'.assertNoErrors;
	}
	
	@Test
	def void test_any() {
		'*'.assertNoErrors;
	}
	
	@Test
	def void test_concept_reference() {
		'138875005|SNOMED CT Root|'.assertNoErrors;
	}
	
	@Test
	def void test_active_only() {
		'* {{ active=true }}'.assertNoErrors;
	}
	
	@Test
	def void test_inactive_only() {
		'* {{ active=false }}'.assertNoErrors;
	}
	
	@Test
	def void test_module() {
		'* {{ moduleId= 900000000000207008 }}'.assertNoErrors;
	}
	
	@Test
	def void test_term_filter() {
		'* {{ term = "Clin find" }}'.assertNoErrors;
	}
	
	@Test
	def void test_active_and_module() {
		'* {{ active=true, moduleId = 900000000000207008 }}'.assertNoErrors;
	}
	
	@Test
	def void test_active_or_module() {
		'* {{ active=true OR moduleId = 900000000000207008 }}'.assertNoErrors;
	}
	
	@Test
	def void test_active_minus_module() {
		'* {{ active=true MINUS moduleId = 900000000000207008 }}'.assertNoErrors;
	}
	
	@Test
	def void test_type_filter() {
		'* {{ typeId = 900000000000550004 }}'.assertNoErrors;
	}

	@Test
	def void test_multi_domain_query_and() {
		'* {{ active=false }} AND * {{ term="clin find" }}'.assertNoErrors;
	}
	
	@Test
	def void test_multi_domain_query_or() {
		'* {{ active=false }} OR * {{ term="clin find" }}'.assertNoErrors;
	}
	
	@Test
	def void test_multi_domain_query_minus() {
		'* {{ active=false }} MINUS * {{ term="clin find" }}'.assertNoErrors;
	}
	
	@Test
	def void test_query_conjuction() {
		'* {{ active = false }} AND * {{ Description.active = true }}'.assertNoErrors;
	}
	
	@Test
	def void test_query_disjunction() {
		'* {{ active = false }} OR * {{ Description.active = true }}'.assertNoErrors;
	}
	
	@Test
	def void test_query_disjunction_w_parenthesis() {
		'* {{ active = false }} OR (* {{ Description.active = true }})'.assertNoErrors;
	}
	
	@Test
	def void test_preferredIn_filter() {
		'* {{ preferredIn = 900000000000550004 }}'.assertNoErrors;
	}
	
	@Test
	def void test_acceptableIn_filter() {
		'* {{ acceptableIn = 900000000000550004 }}'.assertNoErrors;
	}
	
	@Test
	def void test_languageRefset_filter() {
		'* {{ languageRefSetId = 900000000000550004 }}'.assertNoErrors;
	}
	
	@Test
	def void test_languageCode_filter() {
		'* {{ languageCode = "en" }}'.assertNoErrors;
	}
	
	@Test
	def void test_caseSignificance_filter() {
		'* {{ caseSignificanceId = 900000000000448009 }}'.assertNoErrors;
	}
	
	private def void assertNoErrors(CharSequence it) throws Exception {
		val query = parse;
		assertNotNull('''Cannot parse expression: «it».''', query);
		query.assertNoErrors;
	}

}
