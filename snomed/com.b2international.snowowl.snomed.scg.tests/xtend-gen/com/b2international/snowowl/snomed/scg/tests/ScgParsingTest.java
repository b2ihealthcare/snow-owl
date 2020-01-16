/**
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.scg.tests;

import com.b2international.snowowl.snomed.scg.scg.Attribute;
import com.b2international.snowowl.snomed.scg.scg.AttributeGroup;
import com.b2international.snowowl.snomed.scg.scg.AttributeValue;
import com.b2international.snowowl.snomed.scg.scg.ConceptReference;
import com.b2international.snowowl.snomed.scg.scg.DecimalValue;
import com.b2international.snowowl.snomed.scg.scg.Expression;
import com.b2international.snowowl.snomed.scg.scg.IntegerValue;
import com.b2international.snowowl.snomed.scg.scg.StringValue;
import com.b2international.snowowl.snomed.scg.scg.SubExpression;
import com.b2international.snowowl.snomed.scg.tests.ScgInjectorProvider;
import com.google.inject.Inject;
import java.math.BigDecimal;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(ScgInjectorProvider.class)
@SuppressWarnings("all")
public class ScgParsingTest {
  @Inject
  @Extension
  private ParseHelper<Expression> _parseHelper;
  
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
  public void test_simple_expression_1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("73211009 |diabetes mellitus|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "73211009", "diabetes mellitus");
      Assert.assertNull(expression.getExpression().getRefinement());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_simple_expression_2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("73211009");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      Assert.assertEquals("73211009", IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()).getId());
      Assert.assertNull(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()).getTerm());
      Assert.assertNull(expression.getExpression().getRefinement());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_multiple_focus_concepts_1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("421720008 |spray dose form| + 7946007 |drug suspension|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertEquals(2, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "421720008", "spray dose form");
      this.assertConceptReference(IterableExtensions.<ConceptReference>last(expression.getExpression().getFocusConcepts()), "7946007", "drug suspension");
      Assert.assertNull(expression.getExpression().getRefinement());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_multiple_focus_concepts_2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("421720008 + 7946007 |drug suspension|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertEquals(2, expression.getExpression().getFocusConcepts().size());
      Assert.assertEquals("421720008", IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()).getId());
      Assert.assertNull(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()).getTerm());
      this.assertConceptReference(IterableExtensions.<ConceptReference>last(expression.getExpression().getFocusConcepts()), "7946007", "drug suspension");
      Assert.assertNull(expression.getExpression().getRefinement());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_multiple_focus_concepts_3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("421720008");
      _builder.newLine();
      _builder.append("+ 7946007");
      _builder.newLine();
      _builder.append("|drug suspension|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertEquals(2, expression.getExpression().getFocusConcepts().size());
      Assert.assertEquals("421720008", IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()).getId());
      Assert.assertNull(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()).getTerm());
      this.assertConceptReference(IterableExtensions.<ConceptReference>last(expression.getExpression().getFocusConcepts()), "7946007", "drug suspension");
      Assert.assertNull(expression.getExpression().getRefinement());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_definition_type_1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("===  46866001 |fracture of lower limb| + 428881005 |injury of tibia|:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("116676008 |associated morphology| = 72704001 |fracture|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("363698007 |finding site| = 12611008 |bone structure of tibia|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(2, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "46866001", "fracture of lower limb");
      this.assertConceptReference(IterableExtensions.<ConceptReference>last(expression.getExpression().getFocusConcepts()), "428881005", "injury of tibia");
      Assert.assertEquals(2, expression.getExpression().getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes()), "116676008", "associated morphology", "72704001", "fracture");
      this.assertSimpleAttribute(IterableExtensions.<Attribute>last(expression.getExpression().getRefinement().getAttributes()), "363698007", "finding site", "12611008", "bone structure of tibia");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_definition_type_2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("<<< 73211009 |diabetes mellitus|: 363698007 |finding site| = 113331007 |endocrine system|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertTrue(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "73211009", "diabetes mellitus");
      Assert.assertEquals(1, expression.getExpression().getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes()), "363698007", "finding site", "113331007", "endocrine system");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_attribute_group_1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("71388002 |procedure|:");
      _builder.newLine();
      _builder.append("{");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("260686004 |method| = 129304002 |excision - action|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("405813007 |procedure site - direct| = 15497006 |ovarian structure|");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      _builder.append("{");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("260686004 |method| = 129304002 |excision - action|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("405813007 |procedure site - direct| = 31435000 |fallopian tube structure|");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "71388002", "procedure");
      Assert.assertTrue(expression.getExpression().getRefinement().getAttributes().isEmpty());
      Assert.assertEquals(2, expression.getExpression().getRefinement().getGroups().size());
      final AttributeGroup group1 = IterableExtensions.<AttributeGroup>head(expression.getExpression().getRefinement().getGroups());
      Assert.assertEquals(2, group1.getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(group1.getAttributes()), "260686004", "method", "129304002", "excision - action");
      this.assertSimpleAttribute(IterableExtensions.<Attribute>last(group1.getAttributes()), "405813007", "procedure site - direct", "15497006", "ovarian structure");
      final AttributeGroup group2 = IterableExtensions.<AttributeGroup>last(expression.getExpression().getRefinement().getGroups());
      Assert.assertEquals(2, group2.getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(group2.getAttributes()), "260686004", "method", "129304002", "excision - action");
      this.assertSimpleAttribute(IterableExtensions.<Attribute>last(group2.getAttributes()), "405813007", "procedure site - direct", "31435000", "fallopian tube structure");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_attribute_group_2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("71388002 |procedure|:");
      _builder.newLine();
      _builder.append("{");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("260686004 |method| = 129304002 |excision - action|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("405813007 |procedure site - direct| = 20837000 |structure of right ovary|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("424226004 |using device| = 122456005 |laser device|");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      _builder.append("{");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("260686004 |method| = 261519002 |diathermy excision - action|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("405813007 |procedure site - direct| = 113293009 |structure of left fallopian tube|");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "71388002", "procedure");
      Assert.assertTrue(expression.getExpression().getRefinement().getAttributes().isEmpty());
      Assert.assertEquals(2, expression.getExpression().getRefinement().getGroups().size());
      final AttributeGroup group1 = IterableExtensions.<AttributeGroup>head(expression.getExpression().getRefinement().getGroups());
      Assert.assertEquals(3, group1.getAttributes().size());
      this.assertSimpleAttribute(group1.getAttributes().get(0), "260686004", "method", "129304002", "excision - action");
      this.assertSimpleAttribute(group1.getAttributes().get(1), "405813007", "procedure site - direct", "20837000", "structure of right ovary");
      this.assertSimpleAttribute(group1.getAttributes().get(2), "424226004", "using device", "122456005", "laser device");
      final AttributeGroup group2 = IterableExtensions.<AttributeGroup>last(expression.getExpression().getRefinement().getGroups());
      Assert.assertEquals(2, group2.getAttributes().size());
      this.assertSimpleAttribute(group2.getAttributes().get(0), "260686004", "method", "261519002", "diathermy excision - action");
      this.assertSimpleAttribute(group2.getAttributes().get(1), "405813007", "procedure site - direct", "113293009", "structure of left fallopian tube");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_concrete_value_1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("373873005 |pharmaceutical / biologic product|:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("411116001 |has dose form| = 385049006 |capsule|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("111115 |active ingredient count| = #1,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("{");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("127489000 |has active ingredient| = 96068000 |amoxicillin trihydrate|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |has reference basis of strength| = 372687004 |amoxicillin|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |strength magnitude equal to| = #500,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |strength unit| = 258684004 |mg|");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "373873005", "pharmaceutical / biologic product");
      Assert.assertEquals(2, expression.getExpression().getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes()), "411116001", "has dose form", "385049006", "capsule");
      this.assertIntegerAttribute(IterableExtensions.<Attribute>last(expression.getExpression().getRefinement().getAttributes()), "111115", "active ingredient count", 1);
      Assert.assertEquals(1, expression.getExpression().getRefinement().getGroups().size());
      final AttributeGroup group = IterableExtensions.<AttributeGroup>head(expression.getExpression().getRefinement().getGroups());
      Assert.assertEquals(4, group.getAttributes().size());
      this.assertSimpleAttribute(group.getAttributes().get(0), "127489000", "has active ingredient", "96068000", "amoxicillin trihydrate");
      this.assertSimpleAttribute(group.getAttributes().get(1), "111115", "has reference basis of strength", "372687004", "amoxicillin");
      this.assertIntegerAttribute(group.getAttributes().get(2), "111115", "strength magnitude equal to", 500);
      this.assertSimpleAttribute(group.getAttributes().get(3), "111115", "strength unit", "258684004", "mg");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_concrete_value_2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("373873005 |pharmaceutical / biologic product|:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("411116001 |has dose form| = 385023001 |oral solution|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("111115 |active ingredient count| = #1,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("{");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("127489000 |has active ingredient| = 372897005 |albuterol|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |has reference basis of strength| = 372897005 |albuterol|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |strength magnitude equal to| = #0.083,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |strength unit| = 118582008 |%|");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "373873005", "pharmaceutical / biologic product");
      Assert.assertEquals(2, expression.getExpression().getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes()), "411116001", "has dose form", "385023001", "oral solution");
      this.assertIntegerAttribute(IterableExtensions.<Attribute>last(expression.getExpression().getRefinement().getAttributes()), "111115", "active ingredient count", 1);
      Assert.assertEquals(1, expression.getExpression().getRefinement().getGroups().size());
      final AttributeGroup group = IterableExtensions.<AttributeGroup>head(expression.getExpression().getRefinement().getGroups());
      Assert.assertEquals(4, group.getAttributes().size());
      this.assertSimpleAttribute(group.getAttributes().get(0), "127489000", "has active ingredient", "372897005", "albuterol");
      this.assertSimpleAttribute(group.getAttributes().get(1), "111115", "has reference basis of strength", "372897005", "albuterol");
      this.assertDecimalAttribute(group.getAttributes().get(2), "111115", "strength magnitude equal to", new BigDecimal("0.083"));
      this.assertSimpleAttribute(group.getAttributes().get(3), "111115", "strength unit", "118582008", "%");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_concrete_value_3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("322236009 |paracetamol 500 mg tablet|: 111115 |trade name| = \"PANADOL\"");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "322236009", "paracetamol 500 mg tablet");
      Assert.assertEquals(1, expression.getExpression().getRefinement().getAttributes().size());
      this.assertStringAttribute(IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes()), "111115", "trade name", "PANADOL");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_concrete_value_4() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("373873005 |pharmaceutical / biologic product|:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("411116001 |has dose form| = 385218009 |injection|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("111115 |active ingredient count| = #2,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("{");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("127489000 |has active ingredient| = 428126001 |diphtheria toxoid|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |has reference basis of strength| = 428126001 |diphtheria toxoid|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |strength magnitude minimum| = #4,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |strength unit| = 259002007 |IU/mL|");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("{");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("127489000 |has active ingredient| = 412375000 |tetanus toxoid|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |has reference basis of strength| = 412375000 |tetanus toxoid|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |strength magnitude equal to| = #40,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("111115 |strength unit| = 259002007 |IU/mL|");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "373873005", "pharmaceutical / biologic product");
      Assert.assertEquals(2, expression.getExpression().getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes()), "411116001", "has dose form", "385218009", "injection");
      this.assertIntegerAttribute(IterableExtensions.<Attribute>last(expression.getExpression().getRefinement().getAttributes()), "111115", "active ingredient count", 2);
      Assert.assertEquals(2, expression.getExpression().getRefinement().getGroups().size());
      final AttributeGroup group1 = IterableExtensions.<AttributeGroup>head(expression.getExpression().getRefinement().getGroups());
      Assert.assertEquals(4, group1.getAttributes().size());
      this.assertSimpleAttribute(group1.getAttributes().get(0), "127489000", "has active ingredient", "428126001", "diphtheria toxoid");
      this.assertSimpleAttribute(group1.getAttributes().get(1), "111115", "has reference basis of strength", "428126001", "diphtheria toxoid");
      this.assertIntegerAttribute(group1.getAttributes().get(2), "111115", "strength magnitude minimum", 4);
      this.assertSimpleAttribute(group1.getAttributes().get(3), "111115", "strength unit", "259002007", "IU/mL");
      final AttributeGroup group2 = IterableExtensions.<AttributeGroup>last(expression.getExpression().getRefinement().getGroups());
      Assert.assertEquals(4, group2.getAttributes().size());
      this.assertSimpleAttribute(group2.getAttributes().get(0), "127489000", "has active ingredient", "412375000", "tetanus toxoid");
      this.assertSimpleAttribute(group2.getAttributes().get(1), "111115", "has reference basis of strength", "412375000", "tetanus toxoid");
      this.assertIntegerAttribute(group2.getAttributes().get(2), "111115", "strength magnitude equal to", 40);
      this.assertSimpleAttribute(group2.getAttributes().get(3), "111115", "strength unit", "259002007", "IU/mL");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_refinement_1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("83152002 |oophorectomy|:");
      _builder.newLine();
      _builder.append("405815000|procedure device| = 122456005 |laser device|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "83152002", "oophorectomy");
      Assert.assertTrue(expression.getExpression().getRefinement().getGroups().isEmpty());
      Assert.assertEquals(1, expression.getExpression().getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes()), "405815000", "procedure device", "122456005", "laser device");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_refinement_2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("182201002 |hip joint|:");
      _builder.newLine();
      _builder.append("272741003 |laterality| = 24028007 |right|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "182201002", "hip joint");
      Assert.assertTrue(expression.getExpression().getRefinement().getGroups().isEmpty());
      Assert.assertEquals(1, expression.getExpression().getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes()), "272741003", "laterality", "24028007", "right");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_refinement_3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("71388002 |procedure|:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("405815000|procedure device| = 122456005 |laser device|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("260686004 |method| = 129304002 |excision - action|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("405813007 |procedure site - direct| = 15497006 |ovarian structure|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "71388002", "procedure");
      Assert.assertTrue(expression.getExpression().getRefinement().getGroups().isEmpty());
      Assert.assertEquals(3, expression.getExpression().getRefinement().getAttributes().size());
      this.assertSimpleAttribute(expression.getExpression().getRefinement().getAttributes().get(0), "405815000", "procedure device", "122456005", "laser device");
      this.assertSimpleAttribute(expression.getExpression().getRefinement().getAttributes().get(1), "260686004", "method", "129304002", "excision - action");
      this.assertSimpleAttribute(expression.getExpression().getRefinement().getAttributes().get(2), "405813007", "procedure site - direct", "15497006", "ovarian structure");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_refinement_4() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("65801008 |excision|:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("405813007 |procedure site - direct| = 66754008 |appendix structure|,");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("260870009 |priority| = 25876001 |emergency|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "65801008", "excision");
      Assert.assertTrue(expression.getExpression().getRefinement().getGroups().isEmpty());
      Assert.assertEquals(2, expression.getExpression().getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes()), "405813007", "procedure site - direct", "66754008", "appendix structure");
      this.assertSimpleAttribute(IterableExtensions.<Attribute>last(expression.getExpression().getRefinement().getAttributes()), "260870009", "priority", "25876001", "emergency");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_refinement_5() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("313056006 |epiphysis of ulna|: 272741003 |laterality| = 7771000 |left|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "313056006", "epiphysis of ulna");
      Assert.assertTrue(expression.getExpression().getRefinement().getGroups().isEmpty());
      Assert.assertEquals(1, expression.getExpression().getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes()), "272741003", "laterality", "7771000", "left");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_refinement_6() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("119189000 |ulna part| + 312845000 |epiphysis of upper limb|:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("272741003 |laterality| = 7771000 |left|");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertEquals(2, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "119189000", "ulna part");
      this.assertConceptReference(IterableExtensions.<ConceptReference>last(expression.getExpression().getFocusConcepts()), "312845000", "epiphysis of upper limb");
      Assert.assertTrue(expression.getExpression().getRefinement().getGroups().isEmpty());
      Assert.assertEquals(1, expression.getExpression().getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes()), "272741003", "laterality", "7771000", "left");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_nested_refinement_1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("373873005 |pharmaceutical / biologic product|:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("411116001 |has dose form| =\t(421720008 |spray dose form| + 7946007 |drug suspension|)");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "373873005", "pharmaceutical / biologic product");
      Assert.assertEquals(1, expression.getExpression().getRefinement().getAttributes().size());
      final Attribute attribute = IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes());
      this.assertConceptReference(attribute.getName(), "411116001", "has dose form");
      AttributeValue _value = attribute.getValue();
      Assert.assertTrue((_value instanceof SubExpression));
      AttributeValue _value_1 = attribute.getValue();
      final SubExpression subExpression = ((SubExpression) _value_1);
      Assert.assertEquals(2, subExpression.getFocusConcepts().size());
      Assert.assertNull(subExpression.getRefinement());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(subExpression.getFocusConcepts()), "421720008", "spray dose form");
      this.assertConceptReference(IterableExtensions.<ConceptReference>last(subExpression.getFocusConcepts()), "7946007", "drug suspension");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_nested_refinement_2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("397956004 |prosthetic arthroplasty of the hip|:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("363704007 |procedure site| = (24136001 |hip joint structure|: 272741003 |laterality| = 7771000 |left|)");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "397956004", "prosthetic arthroplasty of the hip");
      Assert.assertEquals(1, expression.getExpression().getRefinement().getAttributes().size());
      final Attribute attribute = IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes());
      this.assertConceptReference(attribute.getName(), "363704007", "procedure site");
      AttributeValue _value = attribute.getValue();
      Assert.assertTrue((_value instanceof SubExpression));
      AttributeValue _value_1 = attribute.getValue();
      final SubExpression subExpression = ((SubExpression) _value_1);
      Assert.assertEquals(1, subExpression.getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(subExpression.getFocusConcepts()), "24136001", "hip joint structure");
      Assert.assertEquals(1, subExpression.getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(subExpression.getRefinement().getAttributes()), "272741003", "laterality", "7771000", "left");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_nested_refinement_3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("397956004 |prosthetic arthroplasty of the hip|: ");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("363704007 |procedure site| = (24136001 |hip joint structure|: 272741003 |laterality| = 7771000 |left|),");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("{");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("363699004 |direct device| = 304120007 |total hip replacement prosthesis|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("260686004 |method| = 257867005 |insertion - action|");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "397956004", "prosthetic arthroplasty of the hip");
      Assert.assertEquals(1, expression.getExpression().getRefinement().getAttributes().size());
      final Attribute attribute = IterableExtensions.<Attribute>head(expression.getExpression().getRefinement().getAttributes());
      this.assertConceptReference(attribute.getName(), "363704007", "procedure site");
      AttributeValue _value = attribute.getValue();
      Assert.assertTrue((_value instanceof SubExpression));
      AttributeValue _value_1 = attribute.getValue();
      final SubExpression subExpression = ((SubExpression) _value_1);
      Assert.assertEquals(1, subExpression.getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(subExpression.getFocusConcepts()), "24136001", "hip joint structure");
      Assert.assertEquals(1, subExpression.getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(subExpression.getRefinement().getAttributes()), "272741003", "laterality", "7771000", "left");
      Assert.assertEquals(1, expression.getExpression().getRefinement().getGroups().size());
      final AttributeGroup group = IterableExtensions.<AttributeGroup>head(expression.getExpression().getRefinement().getGroups());
      Assert.assertEquals(2, group.getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(group.getAttributes()), "363699004", "direct device", "304120007", "total hip replacement prosthesis");
      this.assertSimpleAttribute(IterableExtensions.<Attribute>last(group.getAttributes()), "260686004", "method", "257867005", "insertion - action");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test_expression_with_nested_refinement_4() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("243796009 |situation with explicit context|: ");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("{");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("408730004 |procedure context| = 385658003 |done|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("408731000 |temporal context| = 410512000 |current or specified|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("408732007 |subject relationship context| = 410604004 |subject of record|,");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("363589002 |associated procedure| = (");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("397956004 |prosthetic arthroplasty of the hip|:");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("363704007 |procedure site| = (24136001 |hip joint structure|: 272741003 |laterality| = 7771000 |left|)");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("{");
      _builder.newLine();
      _builder.append("\t\t\t\t\t");
      _builder.append("363699004 |direct device| = 304120007 |total hip replacement prosthesis|,");
      _builder.newLine();
      _builder.append("\t\t\t\t\t");
      _builder.append("260686004 |method| = 257867005 |insertion - action|");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append(") ");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      final Expression expression = this.assertNoErrors(_builder);
      Assert.assertFalse(expression.isPrimitive());
      Assert.assertEquals(1, expression.getExpression().getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(expression.getExpression().getFocusConcepts()), "243796009", "situation with explicit context");
      Assert.assertTrue(expression.getExpression().getRefinement().getAttributes().isEmpty());
      Assert.assertEquals(1, expression.getExpression().getRefinement().getGroups().size());
      final AttributeGroup group = IterableExtensions.<AttributeGroup>head(expression.getExpression().getRefinement().getGroups());
      Assert.assertEquals(4, group.getAttributes().size());
      this.assertSimpleAttribute(group.getAttributes().get(0), "408730004", "procedure context", "385658003", "done");
      this.assertSimpleAttribute(group.getAttributes().get(1), "408731000", "temporal context", "410512000", "current or specified");
      this.assertSimpleAttribute(group.getAttributes().get(2), "408732007", "subject relationship context", "410604004", "subject of record");
      final Attribute attribute = group.getAttributes().get(3);
      this.assertConceptReference(attribute.getName(), "363589002", "associated procedure");
      AttributeValue _value = attribute.getValue();
      Assert.assertTrue((_value instanceof SubExpression));
      AttributeValue _value_1 = attribute.getValue();
      final SubExpression subExpression = ((SubExpression) _value_1);
      Assert.assertEquals(1, subExpression.getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(subExpression.getFocusConcepts()), "397956004", "prosthetic arthroplasty of the hip");
      Assert.assertEquals(1, subExpression.getRefinement().getAttributes().size());
      this.assertConceptReference(IterableExtensions.<Attribute>head(subExpression.getRefinement().getAttributes()).getName(), "363704007", "procedure site");
      AttributeValue _value_2 = IterableExtensions.<Attribute>head(subExpression.getRefinement().getAttributes()).getValue();
      Assert.assertTrue((_value_2 instanceof SubExpression));
      AttributeValue _value_3 = IterableExtensions.<Attribute>head(subExpression.getRefinement().getAttributes()).getValue();
      final SubExpression subExpression2 = ((SubExpression) _value_3);
      Assert.assertEquals(1, subExpression2.getFocusConcepts().size());
      this.assertConceptReference(IterableExtensions.<ConceptReference>head(subExpression2.getFocusConcepts()), "24136001", "hip joint structure");
      Assert.assertEquals(1, subExpression2.getRefinement().getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(subExpression2.getRefinement().getAttributes()), "272741003", "laterality", "7771000", "left");
      Assert.assertEquals(1, subExpression.getRefinement().getGroups().size());
      final AttributeGroup subGroup = IterableExtensions.<AttributeGroup>head(subExpression.getRefinement().getGroups());
      Assert.assertEquals(2, subGroup.getAttributes().size());
      this.assertSimpleAttribute(IterableExtensions.<Attribute>head(subGroup.getAttributes()), "363699004", "direct device", "304120007", "total hip replacement prosthesis");
      this.assertSimpleAttribute(IterableExtensions.<Attribute>last(subGroup.getAttributes()), "260686004", "method", "257867005", "insertion - action");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private void assertSimpleAttribute(final Attribute attribute, final String nameId, final String nameTerm, final String valueId, final String valueTerm) {
    this.assertConceptReference(attribute.getName(), nameId, nameTerm);
    AttributeValue _value = attribute.getValue();
    Assert.assertTrue((_value instanceof ConceptReference));
    AttributeValue _value_1 = attribute.getValue();
    this.assertConceptReference(((ConceptReference) _value_1), valueId, valueTerm);
  }
  
  private void assertIntegerAttribute(final Attribute attribute, final String nameId, final String nameTerm, final int intValue) {
    this.assertConceptReference(attribute.getName(), nameId, nameTerm);
    AttributeValue _value = attribute.getValue();
    Assert.assertTrue((_value instanceof IntegerValue));
    AttributeValue _value_1 = attribute.getValue();
    Assert.assertEquals(intValue, ((IntegerValue) _value_1).getValue());
  }
  
  private void assertDecimalAttribute(final Attribute attribute, final String nameId, final String nameTerm, final BigDecimal decimalValue) {
    this.assertConceptReference(attribute.getName(), nameId, nameTerm);
    AttributeValue _value = attribute.getValue();
    Assert.assertTrue((_value instanceof DecimalValue));
    AttributeValue _value_1 = attribute.getValue();
    Assert.assertTrue(decimalValue.equals(((DecimalValue) _value_1).getValue()));
  }
  
  private void assertStringAttribute(final Attribute attribute, final String nameId, final String nameTerm, final String stringValue) {
    this.assertConceptReference(attribute.getName(), nameId, nameTerm);
    AttributeValue _value = attribute.getValue();
    Assert.assertTrue((_value instanceof StringValue));
    AttributeValue _value_1 = attribute.getValue();
    Assert.assertEquals(stringValue, ((StringValue) _value_1).getValue());
  }
  
  private void assertConceptReference(final ConceptReference reference, final String id, final String term) {
    Assert.assertEquals(id, reference.getId());
    Assert.assertEquals((("|" + term) + "|"), reference.getTerm());
  }
  
  private Expression assertNoErrors(final CharSequence it) throws Exception {
    final Expression expression = this._parseHelper.parse(it);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Cannot parse expression: ");
    _builder.append(it);
    _builder.append(".");
    Assert.assertNotNull(_builder.toString(), expression);
    this._validationTestHelper.assertNoErrors(expression);
    return expression;
  }
}
