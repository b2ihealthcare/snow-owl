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
package com.b2international.index.query;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;

/**
 * Represents a binary operator.
 * 
 * @since 5.0
 */
abstract public class BinaryOperator implements Expression {
	private final Expression left;
	private final Expression right;

	public BinaryOperator(Expression left, Expression right) {
		this.left = checkNotNull(left, "left");
		this.right = checkNotNull(right, "right");
	}

	public BinaryOperator(Expression left) {
		this.left = checkNotNull(left, "left");
		this.right = null;
	}
	
	public Expression getLeft() {
		return left;
	}

	@JsonIgnore
	public Optional<Expression> getRight() {
		return Optional.fromNullable(right);
	}

}