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
package com.b2international.snowowl.snomed.core.domain;

import java.util.Set;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Predicates regarding {@link CharacteristicType}s.
 * @author endre
 * 
 */
public class CharacteristicTypePredicates {

	private static ImmutableList<String> MANUALLY_ALLOWED_CHARACTERISTIC_TYPES = ImmutableList.<String> of(CharacteristicType.ADDITIONAL_RELATIONSHIP.getConceptId(), CharacteristicType.STATED_RELATIONSHIP.getConceptId());
	private static boolean inferredEditingEnabled = ApplicationContext.getInstance().getServiceChecked(SnomedCoreConfiguration.class).isInferredEditingEnabled();
	
	/**
	 * Predicate for {@link CharacteristicType} IDs.
	 */
	public static class CharacteristicTypeByIdPredicate implements Predicate<String> {

		private Set<String> ids = Sets.newHashSet();

		public CharacteristicTypeByIdPredicate(String... characteristicTypesIDs) {
			for (String id : characteristicTypesIDs) {
				ids.add(id);
			}
			
			//manual override
			if (inferredEditingEnabled) {
				ids.add(CharacteristicType.INFERRED_RELATIONSHIP.getConceptId());
			}
		}
		
		public CharacteristicTypeByIdPredicate(CharacteristicType... characteristicTypes) {
			for (CharacteristicType characteristicType : characteristicTypes) {
				ids.add(characteristicType.getConceptId());
			}
			//manual override
			if (inferredEditingEnabled) {
				ids.add(CharacteristicType.INFERRED_RELATIONSHIP.getConceptId());
			}
		}

		@Override
		public boolean apply(String input) {
			return ids.contains(input);
		}

	}
	
	/**
	 * Predicate for {@link CharacteristicType} {@link IComponent}s 
	 */
	public static class CharacteristicTypeIComponentPredicate implements Predicate<IComponent<String>> {
		
		private Set<String> ids = Sets.newHashSet();
		
		public CharacteristicTypeIComponentPredicate(String... characteristicTypeIDs) {
			for (String id : characteristicTypeIDs) {
				ids.add(id);
			}
			//manual override
			if (inferredEditingEnabled) {
				ids.add(CharacteristicType.INFERRED_RELATIONSHIP.getConceptId());
			}
		}
		
		public CharacteristicTypeIComponentPredicate(CharacteristicType... characteristicTypes) {
			for (CharacteristicType characteristicType : characteristicTypes) {
				ids.add(characteristicType.getConceptId());
			}
			//manual override
			if (inferredEditingEnabled) {
				ids.add(CharacteristicType.INFERRED_RELATIONSHIP.getConceptId());
			}
		}
		
		@Override
		public boolean apply(IComponent<String> input) {
			return ids.contains(input.getId());
		}
	}
	
	
	/**
	 * Returns a {@link Predicate} that selects only those {@link CharacteristicType}s that are allowed to be created by the user.
	 * @return
	 */
	public static Predicate<IComponent<String>> manuallyCreatableCharacteristicTypesIComponentPredicate() {
		return new CharacteristicTypeIComponentPredicate(MANUALLY_ALLOWED_CHARACTERISTIC_TYPES.toArray(new String[0]));
	}
	
	
	/**
	 * Returns a {@link Predicate} that selects only those {@link CharacteristicType}s that are allowed to be created by the user.
	 * @return
	 */
	public static Predicate<String> manuallyCreatableCharacteristicTypesIDsPredicate() {
		return new CharacteristicTypeByIdPredicate(MANUALLY_ALLOWED_CHARACTERISTIC_TYPES.toArray(new String[0]));
	}

}
