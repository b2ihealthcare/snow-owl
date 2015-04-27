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
package com.b2international.snowowl.api.domain;

/**
 * The common interface of component-creating input models, pointing to the code system, version and task in which the
 * component should be created.
 */
public interface IComponentInput extends IBranchAwareConfig {

	/**
	 * Returns the code system short name, eg. "{@code SNOMEDCT}"
	 * 
	 * @return the code system short name
	 */
	String getCodeSystemShortName();

}
