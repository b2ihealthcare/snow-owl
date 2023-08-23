/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.collection;

import java.util.TreeSet;
import java.util.stream.Collectors;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ResourceTypeConverter;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.b2international.snowowl.core.request.resource.BaseTerminologyResourceCreateRequest;

/**
 * @since 9.0
 */
final class TerminologyResourceCollectionCreateRequest extends BaseTerminologyResourceCreateRequest {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String childResourceType;

	void setChildResourceType(String childResourceType) {
		this.childResourceType = childResourceType;
	}
	
	@Override
	protected String getResourceType() {
		return TerminologyResourceCollection.RESOURCE_TYPE;
	}
	
	@Override
	protected Builder completeResource(Builder builder) {
		return super.completeResource(builder)
				.childResourceType(childResourceType);
	}
	
//	@Override
//	protected void preExecute(TransactionContext context) {
//		var supportedChildResourceTypes = context.service(ResourceTypeConverter.Registry.class).getResourceTypeConverters()
//				.values()
//				.stream()
//				.filter(ResourceTypeConverter::canBeContainedByCollection)
//				.map(ResourceTypeConverter::getResourceType)
//				.collect(Collectors.toCollection(TreeSet::new));
//		
//		if (!supportedChildResourceTypes.contains(childResourceType)) {
//			throw new BadRequestException("'%s' is not supported as child resource type in collections. Select one from the following supported resource types: '%s'", childResourceType, supportedChildResourceTypes);
//		}
//		
//		// FIXME call preExecute after validating child resource type as the method has a side effect on creating a new branch, which is not rolled back in case of an error 
//		super.preExecute(context);
//	}
	
}
