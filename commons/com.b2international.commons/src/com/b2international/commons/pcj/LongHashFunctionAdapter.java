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
package com.b2international.commons.pcj;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import bak.pcj.hash.LongHashFunction;

import com.google.common.hash.HashFunction;

/**
 * Class for adapting a {@link HashFunction} to the PCJ specific {@link LongHashFunction}.
 *
 */
public class LongHashFunctionAdapter implements LongHashFunction, Serializable {

	private static final long serialVersionUID = 3956134011263483379L;
	private HashFunction function;

	/**Factory method for creating a new {@link LongHashFunction} from a {@link HashFunction} instance.*/
	public static LongHashFunction hashOf(final HashFunction function) {
		return new LongHashFunctionAdapter(checkNotNull(function, "function"));
	}
	
	public LongHashFunctionAdapter(final HashFunction function) {
		this.function = checkNotNull(function, "function");
	}
	
	@Override
	public int hash(final long v) {
		return function.hashLong(v).asInt();
	}

}