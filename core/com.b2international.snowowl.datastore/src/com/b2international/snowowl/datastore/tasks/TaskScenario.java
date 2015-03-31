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
package com.b2international.snowowl.datastore.tasks;

import javax.annotation.Nullable;

public enum TaskScenario {
	
	SINGLE_AUTHOR_WITH_SINGLE_REVIEWER(AuthorArity.SINGLE,ReviewerArity.SINGLE_REVIEWER, AuthoringType.SINGLE_AUTHORING) {
		@Override
		public String toString() {
			return "Single author with single reviewer";
		}
	},
	
	DUAL_AUTHORS_WITH_SINGLE_REVIEWER_DUAL_AUTHORING(AuthorArity.DUAL, ReviewerArity.SINGLE_REVIEWER, AuthoringType.DUAL_AUTHORING) {
		@Override
		public String toString() {
			return "Dual authors with single reviewer – Dual authoring";
		}
	},
	
	DUAL_AUTHORS_WITH_SINGLE_REVIEWER_DUAL_BLIND_AUTHORING(AuthorArity.DUAL, ReviewerArity.SINGLE_REVIEWER, AuthoringType.DUAL_BLIND_AUTHORING) {
		@Override
		public String toString() {
			return "Dual authors with single reviewer – Dual blind authoring";
		}
	},
	
	DUAL_AUTHORS_WITH_DUAL_REVIEWERS_DUAL_AUTHORING(AuthorArity.DUAL, ReviewerArity.DUAL_REVIEWER, AuthoringType.DUAL_AUTHORING) {
		@Override
		public String toString() {
			return "Dual authors with dual reviewers – Dual authoring";
		}
	},
	
	DUAL_AUTHORS_WITH_DUAL_REVIEWERS_DUAL_BLIND_AUTHORING(AuthorArity.DUAL, ReviewerArity.DUAL_REVIEWER, AuthoringType.DUAL_BLIND_AUTHORING) {
		@Override
		public String toString() {
			return "Dual authors with dual reviewers – Dual blind authoring";
		}
	};
	
	
	private final ReviewerArity typeOfReviewing;
	
	private final AuthoringType typeOfAuthoring;

	private final AuthorArity authorArity;
	
	private TaskScenario(AuthorArity authorArity, ReviewerArity typeOfReviewing,  AuthoringType typeOfAuthoring) {
		this.authorArity = authorArity;
		this.typeOfReviewing = typeOfReviewing;
		this.typeOfAuthoring = typeOfAuthoring;
	}
	
	public boolean isDualReviewer() {
		return ReviewerArity.DUAL_REVIEWER == typeOfReviewing;
	}
	
	public boolean isDualBlind() {
		return AuthoringType.DUAL_BLIND_AUTHORING == typeOfAuthoring;
	}
	
	public static final TaskScenario scenarioFor(String string) {
		TaskScenario[] scenarios = values();
		for (TaskScenario scenario : scenarios) {
			if(scenario.toString().equals(string))
				return scenario;
		}
		return null;
	}
	
	/**
	 * Returns with the {@link TaskScenario} instance, specified by it's unique ordinal.
	 * @param ordinal the unique ordinal for the type.
	 * @return the {@link TaskScenario} type. Could return with {@code null} if no 
	 * matching scenario can be found for the given ordinal.
	 */
	@Nullable 
	public static TaskScenario get(final int ordinal) {
		for (final TaskScenario type : values()) {
			if (ordinal == type.ordinal())
				return type;
		}
		return null;
	}
	
	public enum AuthorArity {
		SINGLE, DUAL;
	}
	
	public enum ReviewerArity {
		SINGLE_REVIEWER, DUAL_REVIEWER;
	}
	
	public enum AuthoringType {
		SINGLE_AUTHORING, DUAL_AUTHORING, DUAL_BLIND_AUTHORING;
	}
	
	public AuthorArity getAuthorArity() {
		return authorArity;
	}
	
	public ReviewerArity getTypeOfReviewing() {
		return typeOfReviewing;
	}
	
	public AuthoringType getTypeOfAuthoring() {
		return typeOfAuthoring;
	}

}