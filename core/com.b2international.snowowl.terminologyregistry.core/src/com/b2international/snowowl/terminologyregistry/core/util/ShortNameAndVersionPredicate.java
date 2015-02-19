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
package com.b2international.snowowl.terminologyregistry.core.util;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.terminologyregistry.core.bean.CodeSystemShortNameProvider;
import com.b2international.snowowl.terminologyregistry.core.bean.CodeSystemVersionProvider;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

/**
 * @since 3.2.0
 */
public class ShortNameAndVersionPredicate<T extends CodeSystemShortNameProvider & CodeSystemVersionProvider> implements Predicate<T> {

	private final String shortName;
	private final String version;

	public ShortNameAndVersionPredicate(String shortName, String version) {
		Preconditions.checkArgument(!StringUtils.isEmpty(shortName));
		Preconditions.checkArgument(!StringUtils.isEmpty(version));
		this.shortName = shortName;
		this.version = version;
	}

	@Override
	public boolean apply(T input) {
		return shortName.equals(input.getCodeSystemShortName()) && version.equals(input.getCodeSystemVersion());
	}

}