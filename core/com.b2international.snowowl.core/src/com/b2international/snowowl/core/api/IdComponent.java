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
 * @since 4.4
 */
public class IdComponent implements IComponent<String>, ITerminologyComponentIdProvider {

	private static final long serialVersionUID = -2016802041403195516L;
	
	private final String id;
	private final String terminologyComponentId;

	private IdComponent(String id, String terminologyComponentId) {
		this.id = id;
		this.terminologyComponentId = terminologyComponentId;
	}
	
	@Override
	public String getLabel() {
		return id;
	}

	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getTerminologyComponentId() {
		return terminologyComponentId;
	}
	
	public static final IComponent<String> of(String id, String terminologyComponentId) {
		return new IdComponent(id, terminologyComponentId);
	}

}
