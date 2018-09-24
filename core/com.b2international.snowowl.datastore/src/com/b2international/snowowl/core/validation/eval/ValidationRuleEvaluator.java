/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.eval;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.google.common.collect.ImmutableSet;

/**
 * Evaluates {@link ValidationRule}s.
 * 
 * @since 6.0
 * @see ValidationRule#getType()
 */
public interface ValidationRuleEvaluator {

	/**
	 * Registry that holds references to all validation rule evaluator implementations.
	 * 
	 * @since 6.0
	 */
	enum Registry {
		INSTANCE;

		private final Map<String, ValidationRuleEvaluator> evaluators = newHashMap();

		/**
		 * @param evaluator
		 */
		public static void register(ValidationRuleEvaluator evaluator) {
			checkArgument(!INSTANCE.evaluators.containsKey(evaluator.type()), "Rule Evaluator '%s' is already registered", evaluator.type());
			INSTANCE.evaluators.put(evaluator.type(), evaluator);
		}
		
		/**
		 * Returns the available evaluator for the given type or <code>null</code> if there is not evaluator registered for that type.
		 * 
		 * @param type
		 * @return
		 */
		public static ValidationRuleEvaluator get(String type) {
			return INSTANCE.evaluators.get(type);
		}

		/**
		 * Returns the available evaluator types in this registry.
		 * 
		 * @return
		 */
		public static Set<String> types() {
			return ImmutableSet.copyOf(INSTANCE.evaluators.keySet());
		}

	}

	/**
	 * Evaluate the given rule
	 * @param rule - the rule to evaluate
	 * @param params contains parameters for the rule evaluators (BranchContext should always be included)
	 * @return
	 * @throws Exception
	 */
	List<ComponentIdentifier> eval(BranchContext context, ValidationRule rule, Map<String, Object> params) throws Exception;

	/**
	 * Unique type identifier of this validation rule evaluator. The type should represent the kind of rules that this evaluator can evaluate using
	 * the {@link #eval(BranchContext, ValidationRule)} method.
	 * 
	 * @return
	 */
	String type();

}
