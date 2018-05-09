/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.util.Random;
import java.util.Set;

import com.b2international.snowowl.core.terminology.ComponentCategory;

/**
 * An item identifier generation strategy that assigns item identifiers to components randomly, without
 * using the namespace or component category arguments.
 * 
 * @since 4.0
 */
public class RandomItemIdGenerationStrategy implements ItemIdGenerationStrategy {

	@Override
	public Set<String> generateItemIds(String namespace, ComponentCategory category, int quantity, int attempt) {
		// nextInt excludes top value, add 1 to make it inclusive
		final Set<String> generatedItemIds = newHashSetWithExpectedSize(quantity);
		while (quantity > 0) {
			final String itemId = Integer.toString(new Random().nextInt(9999_9999 - 100 + 1) + 100);
			if (generatedItemIds.add(itemId)) {
				quantity--;
			}
		}
		return generatedItemIds;
	}

}
