/*
 * Copyright 2011-2018 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.commons.options;


/**
 * @since 4.1
 */
public abstract class MetadataHolderImpl implements MetadataHolder {

	private final Metadata metadata;
	
	public MetadataHolderImpl(Metadata metadata) {
		this.metadata = metadata;
	}
	
	@Override
	public Metadata metadata() {
		return metadata;
	}
	
}
