/**
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the \"License\");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.ql.validation;

import com.b2international.snowowl.snomed.ql.ql.Conjunction;
import com.b2international.snowowl.snomed.ql.ql.Constraint;
import com.b2international.snowowl.snomed.ql.ql.Disjunction;
import com.b2international.snowowl.snomed.ql.ql.Exclusion;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;
import com.b2international.snowowl.snomed.ql.validation.AbstractQLValidator;
import com.google.common.base.Objects;
import org.eclipse.xtext.validation.Check;

/**
 * This class contains custom validation rules.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
@SuppressWarnings("all")
public class QLValidator extends AbstractQLValidator {
  private static String AMBIGUOUS_MESSAGE = "Ambiguous binary operator, use parenthesis to disambiguate the meaning of the expression";
  
  private static String AMBIGUOUS_CODE = "binaryoperator.ambiguous";
  
  @Check
  public void checkAmbiguity(final Disjunction it) {
    boolean _isAmbiguous = this.isAmbiguous(it, it.getLeft());
    if (_isAmbiguous) {
      this.error(QLValidator.AMBIGUOUS_MESSAGE, it, QlPackage.Literals.DISJUNCTION__LEFT, QLValidator.AMBIGUOUS_CODE);
    } else {
      boolean _isAmbiguous_1 = this.isAmbiguous(it, it.getRight());
      if (_isAmbiguous_1) {
        this.error(QLValidator.AMBIGUOUS_MESSAGE, it, QlPackage.Literals.DISJUNCTION__RIGHT, QLValidator.AMBIGUOUS_CODE);
      }
    }
  }
  
  @Check
  public void checkAmbiguity(final Conjunction it) {
    boolean _isAmbiguous = this.isAmbiguous(it, it.getLeft());
    if (_isAmbiguous) {
      this.error(QLValidator.AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__LEFT, QLValidator.AMBIGUOUS_CODE);
    } else {
      boolean _isAmbiguous_1 = this.isAmbiguous(it, it.getRight());
      if (_isAmbiguous_1) {
        this.error(QLValidator.AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__RIGHT, QLValidator.AMBIGUOUS_CODE);
      }
    }
  }
  
  @Check
  public void checkAmbiguity(final Exclusion it) {
    boolean _isAmbiguous = this.isAmbiguous(it, it.getLeft());
    if (_isAmbiguous) {
      this.error(QLValidator.AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__LEFT, QLValidator.AMBIGUOUS_CODE);
    } else {
      boolean _isAmbiguous_1 = this.isAmbiguous(it, it.getRight());
      if (_isAmbiguous_1) {
        this.error(QLValidator.AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__RIGHT, QLValidator.AMBIGUOUS_CODE);
      }
    }
  }
  
  private boolean isAmbiguous(final Constraint parent, final Constraint child) {
    return ((!Objects.equal(parent.getClass(), child.getClass())) && (((child instanceof Disjunction) || (child instanceof Conjunction)) || (child instanceof Exclusion)));
  }
}
