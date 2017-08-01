/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id.gen;

import java.util.Random;

import com.b2international.snowowl.core.terminology.ComponentCategory;

/**
 * An item identifier generation strategy that assigns item identifiers to components randomly, without
 * using the namespace or component category arguments.
 * 
 * @since 4.0
 */
public class RandomItemIdGenerationStrategy implements ItemIdGenerationStrategy {

	@Override
	public String generateItemId(String namespace, ComponentCategory category, int attempt) {
		// nextInt excludes top value, add 1 to make it inclusive
		return Integer.toString(new Random().nextInt(9999_9999 - 100 + 1) + 100);
	}

}
