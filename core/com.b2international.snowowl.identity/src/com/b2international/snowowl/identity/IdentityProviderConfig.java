/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.identity;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @since 5.11
 */
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.CUSTOM)
@JsonTypeIdResolver(value=IdentityProviderConfig.IdentityProviderTypeIdResolver.class)
public interface IdentityProviderConfig {
	
	/**
	 * @since 5.11
	 */
	final class IdentityProviderTypeIdResolver implements TypeIdResolver {
		
		private final Map<String, Class<?>> subTypeCache;
		
		private JavaType baseType;

		public IdentityProviderTypeIdResolver() throws IOException {
			this.subTypeCache = IdentityProvider.Factory.getAvailableConfigClasses().stream() 
				.filter(this::isValid)
				.collect(Collectors.toMap(this::getType, Function.identity()));
		}
		
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
	    	if (isValid(clazz)) {
	    		return getType((Class<? extends IdentityProviderConfig>) clazz);
	    	}
	    	throw new IllegalArgumentException("Unsupported configuration class (must be instance of IdentityProviderConfig and must have JsonTypeName annotation): " + value);
	    }

	    @Override
		public JavaType typeFromId(DatabindContext context, String type) {
	    	return typeFromId(type);
	    }

	    @Override
	    public JavaType typeFromId(String type) {
	    	checkArgument(subTypeCache.containsKey(type), "Cannot resolve configuration object type: " + type);
	    	return TypeFactory.defaultInstance().constructSpecializedType(baseType, subTypeCache.get(type));
	    }
	    
		private boolean isValid(Class<?> clazz) {
			return IdentityProviderConfig.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(JsonTypeName.class);
		}
		
		private String getType(Class<? extends IdentityProviderConfig> clazz) {
			return clazz.getAnnotation(JsonTypeName.class).value();
		}
	}
	
}
