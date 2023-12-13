/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.validation;

/**
 * Enumerates possible actions to resolve a validation issue.
 *
 * @since 9.0
 */
public enum SuggestedAction {

	/** Remove the offending component or a part of it */
	REMOVE,
	
	/** Replace the offending component (or a part of it) with the suggested replacement */
	REPLACE,
	
	/** Offer the author a series of replacements for the offending component (or a part of it) */
	REPLACE_INTERACTIVE;
}
