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

import javax.annotation.Nullable;


/**
 * Headless representation of a label provider. Provides a human readable 
 * name or label associated with a terminology independent component.
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link IComponentNameProvider#getText(Object) Get component label}</li>
 * </ul>
 * </p>
 * @see #NOOP_NAME_PROVIDER
 */
public interface IComponentNameProvider {

	/**
	 * Returns with a humane readable name or label of a terminology independent component specified by the argument.
	 * @param object the terminology independent component by default. Clients may add functionality to accept identifiers as well.
	 * @return the human readable label of the component. Or the String representation of the <b>object</b> argument.
	 * Clients must ensure that this method does not return with {@code null}.
	 * @see IComponentNameProvider
	 */
	String getText(final Object object);
	
	/**
	 * Returns with the human readable label of a terminology independent component identified by its unique ID
	 * from the given branch. This method may return with {@code null} if the component cannot be found on the 
	 * specified branch with the given component ID.
	 * @param branchPath the branch path uniquely identifying the branch where the lookup has to be performed.
	 * @param componentId the terminology specific unique ID of the component.
	 * @return the name/label of the component. Or {@code null} if the component cannot be found.
	 */
	@Nullable String getComponentLabel(final IBranchPath branchPath, final String componentId);
	
	/**
	 * No-operation name provider that always behaves as {@link String#valueOf(Object)} for any input.
	 * @see IComponentNameProvider 
	 */
	public static final IComponentNameProvider NOOP_NAME_PROVIDER = new IComponentNameProvider() {
		@Override public String getText(final Object object) { return String.valueOf(object); }
		@Override public String getComponentLabel(final IBranchPath branchPath, final String componentId) { return String.valueOf(componentId);	}
	}; 
	
}