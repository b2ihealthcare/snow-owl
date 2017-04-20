/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain;

import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @since 4.6
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "id", visible = true)
@JsonTypeIdResolver(SnomedCoreComponent.SnomedComponentCategoryResolver.class)
public abstract class SnomedCoreComponent extends SnomedComponent {

	private SnomedReferenceSetMembers members;

	public void setMembers(SnomedReferenceSetMembers members) {
		this.members = members;
	}

	/**
	 * Returns the expanded reference set members if any, otherwise it returns a <code>null</code> {@link SnomedReferenceSetMembers}.
	 * @return
	 */
	public SnomedReferenceSetMembers getMembers() {
		return members;
	}
	
	/**
	 * @since 5.5
	 */
	static class SnomedComponentCategoryResolver implements TypeIdResolver {

		private JavaType baseType;

		@Override
		public void init(JavaType bt) {
			baseType = bt;
		}
		
		@Override
		public Id getMechanism() {
			return Id.CUSTOM;
		}

		@Override
		public String idFromValue(Object value) {
			return idFromValueAndType(value, value.getClass());
		}

		@Override
		public String idFromBaseType() {
			throw new UnsupportedOperationException();
		}
		
	    @Override
	    public String idFromValueAndType(Object value, Class<?> clazz) {
	    	if (value instanceof SnomedCoreComponent) {
	    		return ((SnomedCoreComponent) value).getId();
	    	}
	    	throw new IllegalArgumentException("Unsupported value: " + value);
	    }

	    @Override
		public JavaType typeFromId(DatabindContext context, String type) {
	    	return typeFromId(type);
	    }

	    @Override
	    public JavaType typeFromId(String type) {
	    	final Class<? extends SnomedCoreComponent> clazz;
	    	switch (SnomedIdentifiers.getComponentCategory(type)) {
	    	case CONCEPT:
	    		clazz = SnomedConcept.class;
	    		break;
	    	case DESCRIPTION:
	    		clazz = SnomedDescription.class;
	    		break;
	    	case RELATIONSHIP:
	    		clazz = SnomedRelationship.class;
	    		break;
	    	default: throw new IllegalArgumentException("Unknown core component type: " + type);
	    	}
	    	return TypeFactory.defaultInstance().constructSpecializedType(baseType, clazz);
	    }
	    
	}
	
}
