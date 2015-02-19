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
package com.b2international.snowowl.snomed.dsl.query.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NumericDataClause extends RValue {

	public enum Operator {
		EQUALS("=="), 
		LESS_THAN("<"), 
		GREATER_THAN(">"), 
		LESS_EQUALS_TO("<="), 
		GREATER_EQUALS_TO(">="), 
		NOT_EQUALS("!=");
		
		private static final Operator[] VALUES_ARRAY = new Operator[] { 
			EQUALS,
			LESS_THAN, 
			GREATER_THAN, 
			LESS_EQUALS_TO, 
			GREATER_EQUALS_TO,
			NOT_EQUALS, };

		public static final List<Operator> VALUES = Collections
				.unmodifiableList(Arrays.asList(VALUES_ARRAY));

		public static Operator get(String literal) {
			for (int i = 0; i < VALUES_ARRAY.length; ++i) {
				Operator result = VALUES_ARRAY[i];
				if (result.getLiteral().equals(literal)) {
					return result;
				}
			}
			return null;
		}
		
		private String literal;
		
		private Operator(String literal) {
			this.literal = literal;
		}
		
		public String getLiteral() {
			return literal;
		}
	}	
	
	private RValue concepts;
	private NumericDataClause.Operator operator;
	private double value;
	private String unit;
	
	public NumericDataClause(RValue concepts, Operator operator, double value, String unitType) {
		this.concepts = concepts;
		this.operator = operator;
		this.value = value;
		this.unit = unitType;
	}
	
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public double getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public RValue getConcepts() {
		return concepts;
	}
	
	public void setConcepts(RValue concepts) {
		this.concepts = concepts;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public StringBuilder toString(StringBuilder buf) {
		buf.append("HAS STRENGTH");
		buf.append(" ");
		buf.append(operator.getLiteral());
		buf.append(" ");
		buf.append(value);
		buf.append(" ");
		buf.append(unit);
		return buf;
	}


}