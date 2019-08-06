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
package com.b2international.snowowl.snomed.cis.gen;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import com.b2international.snowowl.core.terminology.ComponentCategory;

/**
 * An item identifier generation strategy that always "generates" the same item identifier. Should only be used
 * for testing.
 * 
 * @since 4.0
 */
public class SingleItemIdGenerationStrategy implements ItemIdGenerationStrategy {

	private String itemId;

	public SingleItemIdGenerationStrategy(String itemId) {
		this.itemId = checkNotNull(itemId);
	}
	
	@Override
	public Set<String> generateItemIds(String namespace, ComponentCategory category, int quantity, int attempt) {
		checkArgument(quantity == 1, "Only a single item identifier can be requested at once from this service");
		return Collections.singleton(itemId);
	}
}
