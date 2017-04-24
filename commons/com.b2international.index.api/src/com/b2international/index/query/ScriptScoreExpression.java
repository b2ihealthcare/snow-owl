/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.query;

/**
 * @since 5.0
 */
public class ScriptScoreExpression implements Expression {

	private final String scriptName;
	private final Expression expression;
	private final boolean strict;

	ScriptScoreExpression(Expression expression, String scriptName, boolean strict) {
		this.expression = expression;
		this.scriptName = scriptName;
		this.strict = strict;
	}
	
	public Expression expression() {
		return expression;
	}

	public String scriptName() {
		return scriptName;
	}
	
	public boolean isStrict() {
		return strict;
	}
	
	@Override
	public String toString() {
		return String.format("SCRIPT SCORE(%s)", expression);
	}
	
}
