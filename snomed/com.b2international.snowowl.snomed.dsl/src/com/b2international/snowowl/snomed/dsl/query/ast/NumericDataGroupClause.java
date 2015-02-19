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

public class NumericDataGroupClause extends RValue {
	
	private RValue concepts;
	private NumericDataClause numericData;
	private RValue substances;

	public NumericDataGroupClause(RValue concepts, NumericDataClause numericData, RValue substances) {
		super();
		this.concepts = concepts;
		this.numericData = numericData;
		this.substances = substances;
	}
	
	public RValue getConcepts() {
		return concepts;
	}
	
	public void setConcepts(RValue concepts) {
		this.concepts = concepts;
	}

	public NumericDataClause getNumericData() {
		return numericData;
	}
	
	public void setNumericData(NumericDataClause numericData) {
		this.numericData = numericData;
	}
	
	public RValue getSubstances() {
		return substances;
	}
	
	public void setSubstances(RValue substances) {
		this.substances = substances;
	}

	@Override
	public StringBuilder toString(StringBuilder buf) {
		buf.append("NumericDataGroup: ");
		buf.append(" ");
		buf.append(substances);
		buf.append(" -> ");
		buf.append(numericData);
		return buf;
	}

}