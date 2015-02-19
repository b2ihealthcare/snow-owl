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
package com.b2international.snowowl.datastore.server.snomed;

/**
 * Enumeration of initialization states.
 *
 */
public enum InitializationState {
	
	/**
	 * Represents the uninitialized state.
	 */
	UNINITIALIZED {
		@Override
		public InitializationState nextState() {
			return BUILDING;
		}
	},
	
	/**
	 * Represents the building state.
	 */
	BUILDING {
		@Override
		public InitializationState nextState() {
			return INITIALIZED;
		}
	},
	
	/**
	 * Initialized state representation.
	 */
	INITIALIZED {
		@Override
		public InitializationState nextState() {
			throw new UnsupportedOperationException("Implementation error.");
		}
	};
	
	/**
	 * Flips the current state to the next one.
	 * @return the next state.
	 */
	public abstract InitializationState nextState();
	
	/**
	 * Returns with {@code true} if the argument equals with 
	 * {@link InitializationState#INITIALIZED}.
	 * @param state the state to check.
	 * @return {@code true} if initialized, otherwise {@code false}.
	 */
	public static boolean isInitialized(final InitializationState state) {
		return InitializationState.INITIALIZED.equals(state);
	}
	
	/**
	 * Returns with {@code true} if the argument equals with 
	 * {@link InitializationState#UNINITIALIZED}.
	 * @param state the state to check.
	 * @return {@code true} if uninitialized, otherwise {@code false}.
	 */
	public static boolean isUninitialized(final InitializationState state) {
		return InitializationState.UNINITIALIZED.equals(state);
	}
	
}