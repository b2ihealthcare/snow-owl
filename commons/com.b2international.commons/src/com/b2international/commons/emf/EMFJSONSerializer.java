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
package com.b2international.commons.emf;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emfjson.jackson.module.EMFModule;
import org.emfjson.jackson.resource.JsonResourceFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

/**
 * @since 3.3
 */
public class EMFJSONSerializer {

	private ObjectMapper objectMapper;

	public EMFJSONSerializer() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new EMFModule());

		if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("json")) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("json", new JsonResourceFactory());
		}
	}

	/**
	 * Serializes the given {@link EObject} model to JSON format.
	 *
	 * @param model
	 * @return
	 */
	public String toJSON(EObject model) {
		final EObject copy = EcoreUtil.copy(checkNotNull(model, "model"));
		boolean withoutResource = copy.eResource() == null;
		try {
			if (withoutResource) {
				createResource().getContents().add(copy);
			}
			return objectMapper.writeValueAsString(copy);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} finally {
			if (withoutResource) {
				EcoreUtil.remove(copy);
			}
		}
	}

	/**
	 * Deserializes the given JSON string to a {@link EObject} model subclass instance.
	 *
	 * @param json
	 * @param type
	 * @return
	 * @throws ConversionException - if something happens during the conversion from json to type
	 */
	public <T extends EObject> T toEObject(String json, Class<T> type) throws ConversionException {
		checkArgument(!Strings.isNullOrEmpty(json), "Given JSON should not be null or empty");
		checkNotNull(type, "type");
		try {
			return type.cast(objectMapper.readValue(json, EObject.class));
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	}

	private Resource createResource() {
		final ResourceSet set = new ResourceSetImpl();
		return set.createResource(URI.createURI("file:/tmp.json"));
	}

	/**
	 * @since 3.3
	 */
	public static class ConversionException extends RuntimeException {

		private static final long serialVersionUID = 8593430470128660362L;

		public ConversionException(Throwable cause) {
			super(cause);
		}

	}

}