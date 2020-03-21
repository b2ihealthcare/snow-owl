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
package com.b2international.snowowl.datastore.request;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.request.BaseResourceConverter;
import com.b2international.snowowl.datastore.index.RevisionDocument;

/**
 * @since 5.2
 */
public abstract class BaseRevisionResourceConverter<T extends RevisionDocument, R extends IComponent, CR extends CollectionResource<R>>
		extends BaseResourceConverter<T, R, CR> {

	protected BaseRevisionResourceConverter(final BranchContext context, final Options expand, final List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}
	
	protected final BranchContext context() {
		return (BranchContext) super.context();
	}

}
