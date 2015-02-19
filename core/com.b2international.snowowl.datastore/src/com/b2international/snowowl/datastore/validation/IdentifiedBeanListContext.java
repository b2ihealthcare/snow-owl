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
package com.b2international.snowowl.datastore.validation;

import java.util.List;

import com.b2international.snowowl.datastore.editor.bean.IdentifiedBean;

/**
 * Validation context implementation containing a list of {@link IdentifiedBean}s.
 * 
 */
public class IdentifiedBeanListContext<B extends IdentifiedBean> implements IValidationContext<List<B>> {

	private final List<B> contents;
	
	public IdentifiedBeanListContext(List<B> contents) {
		this.contents = contents;
	}

	@Override
	public List<B> getContents() {
		return contents;
	}

}