/**
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
package com.b2international.snowowl.snomed.ql.tests;

import com.b2international.snowowl.snomed.ql.ql.Query;
import com.b2international.snowowl.snomed.ql.tests.QLInjectorProvider;
import com.google.inject.Inject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(QLInjectorProvider.class)
@SuppressWarnings("all")
public class QLParsingTest {
  @Inject
  @Extension
  private ParseHelper<Query> _parseHelper;
  
  @Inject
  @Extension
  private ValidationTestHelper _validationTestHelper;
  
  @Test
  public void test_empty() {
    try {
      this.assertNoErrors("");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_whitespaces() {
    try {
      this.assertNoErrors(" \n \t");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_any() {
    try {
      this.assertNoErrors("*");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_concept_reference() {
    try {
      this.assertNoErrors("138875005|SNOMED CT Root|");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_active_only() {
    try {
      this.assertNoErrors("* {{ active=true }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_inactive_only() {
    try {
      this.assertNoErrors("* {{ active=false }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_module() {
    try {
      this.assertNoErrors("* {{ moduleId= 900000000000207008 }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_term_filter() {
    try {
      this.assertNoErrors("* {{ term = \"Clin find\" }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_active_and_module() {
    try {
      this.assertNoErrors("* {{ active=true, moduleId = 900000000000207008 }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_active_or_module() {
    try {
      this.assertNoErrors("* {{ active=true OR moduleId = 900000000000207008 }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_active_minus_module() {
    try {
      this.assertNoErrors("* {{ active=true MINUS moduleId = 900000000000207008 }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_type_filter() {
    try {
      this.assertNoErrors("* {{ typeId = 900000000000550004 }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_multi_domain_query_and() {
    try {
      this.assertNoErrors("* {{ active=false }} AND * {{ term=\"clin find\" }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_multi_domain_query_or() {
    try {
      this.assertNoErrors("* {{ active=false }} OR * {{ term=\"clin find\" }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_multi_domain_query_minus() {
    try {
      this.assertNoErrors("* {{ active=false }} MINUS * {{ term=\"clin find\" }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_query_conjuction() {
    try {
      this.assertNoErrors("* {{ active = false }} AND * {{ Description.active = true }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_query_disjunction() {
    try {
      this.assertNoErrors("* {{ active = false }} OR * {{ Description.active = true }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_query_disjunction_w_parenthesis() {
    try {
      this.assertNoErrors("* {{ active = false }} OR (* {{ Description.active = true }})");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_preferredIn_filter() {
    try {
      this.assertNoErrors("* {{ preferredIn = 900000000000550004 }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_acceptableIn_filter() {
    try {
      this.assertNoErrors("* {{ acceptableIn = 900000000000550004 }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_languageRefset_filter() {
    try {
      this.assertNoErrors("* {{ languageRefSetId = 900000000000550004 }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_languageCode_filter() {
    try {
      this.assertNoErrors("* {{ languageCode = \"en\" }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_caseSignificance_filter() {
    try {
      this.assertNoErrors("* {{ caseSignificanceId = 900000000000448009 }}");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private void assertNoErrors(final CharSequence it) throws Exception {
    final Query query = this._parseHelper.parse(it);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Cannot parse expression: ");
    _builder.append(it);
    _builder.append(".");
    Assert.assertNotNull(_builder.toString(), query);
    this._validationTestHelper.assertNoErrors(query);
  }
}
