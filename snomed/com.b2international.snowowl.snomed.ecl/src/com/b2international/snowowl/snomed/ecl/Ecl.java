/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ecl;

import java.util.Collection;

import com.google.common.base.Joiner;

/**
 * @since 5.4
 */
public final class Ecl {

	public static final String ANY = "*";
	public static final int MAX_CARDINALITY = -1;
	public static final Joiner OR_JOINER = Joiner.on(" OR ");
	public static final Joiner AND_JOINER = Joiner.on(",");
	
	private Ecl() {}

	public static String or(String...eclExpressions) {
		return OR_JOINER.join(eclExpressions);
	}
	
	public static String or(Collection<String> eclExpressions) {
		return OR_JOINER.join(eclExpressions);
	}
	
	public static String and(String...eclExpressions) {
		return AND_JOINER.join(eclExpressions);
	}
	
	public static String and(Collection<String> eclExpressions) {
		return AND_JOINER.join(eclExpressions);
	}

	public static String exclude(String from, String exclusion) {
		return String.format("(%s) MINUS (%s)", from, exclusion);
	}

}
