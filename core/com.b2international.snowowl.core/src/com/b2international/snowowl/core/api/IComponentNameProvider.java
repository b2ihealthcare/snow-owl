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
package com.b2international.snowowl.core.api;

/**
 * Provides a human-readable label for a terminology component.
 */
public interface IComponentNameProvider {

	/**
	 * Returns with the human readable label of a terminology independent component identified by its unique ID
	 * from the given branch. This method may return with {@code null} if the component cannot be found on the 
	 * specified branch with the given component ID.
	 * @param branchPath the branch path uniquely identifying the branch where the lookup has to be performed.
	 * @param componentId the terminology specific unique ID of the component.
	 * @return the name/label of the component. Or {@code null} if the component cannot be found.
	 */
	String getComponentLabel(IBranchPath branchPath, String componentId);

	/**
	 * Returns with the terminology dependent unique ID and the human readable label of a component specified by its unique storage key.
	 * <br>This method could return with {@code null} if the component does not exist in the store on the specified branch.  
	 * @param branchPath the branch path.
	 * @param storageKey the primary storage key of the component
	 * @return the {@link ComponentIdAndLabel ID and label pair} of a component. May return with {@code null} if the component does not exist in store.
	 */
	ComponentIdAndLabel getComponentIdAndLabel(IBranchPath branchPath, long storageKey);
	
	/**
	 * No-operation name provider that always behaves as {@link String#valueOf(Object)} for any input.
	 */
	public static IComponentNameProvider NOOP_NAME_PROVIDER = new IComponentNameProvider() {
		@Override public String getComponentLabel(final IBranchPath branchPath, final String componentId) { return String.valueOf(componentId);	}
		@Override public ComponentIdAndLabel getComponentIdAndLabel(final IBranchPath branchPath, final long storageKey) { return null; }
	}; 
}
