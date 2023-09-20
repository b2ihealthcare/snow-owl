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
package com.b2international.commons.jackson;

import com.b2international.commons.StringUtils;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Adds support for &commat;XmlIgnore annotations that allows properties to be
 * skipped in the XML output only, and looks for namespace annotation hints on
 * declaring classes and packages.
 * 
 * @since 9.0
 */
public class CustomXmlAnnotationIntrospector extends JacksonXmlAnnotationIntrospector {

	private static final long serialVersionUID = 1L;

	@Override
	public PropertyName findRootName(AnnotatedClass annotatedClass) {
		/*
		 * Check if an JacksonXmlRootElement or JsonRootName annotation is present that
		 * provides a non-empty namespace
		 */
		final PropertyName propertyName = super.findRootName(annotatedClass);
		
		if (propertyName != null && !StringUtils.isEmpty(propertyName.getNamespace())) {
			return propertyName;
		}
		
		final Class<?> rawClass = annotatedClass.getAnnotated();
		final Package rawPackage = rawClass.getPackage();
		final String packageNamespace = getPackageNamespace(rawPackage);
		
		if (StringUtils.isEmpty(packageNamespace)) {
			return propertyName;
		}

		// Modify the default instance if we didn't have any information on the local name previously
		if (propertyName == null) {
			return PropertyName.USE_DEFAULT.withNamespace(packageNamespace);
		} else {
			return propertyName.withNamespace(packageNamespace);
		}
	}
	
	@Override
	public String findNamespace(MapperConfig<?> config, Annotated annotated) {
		/*
		 * See if there is some information on the annotated element in the form of a
		 * JsonProperty or JacksonXmlProperty annotation
		 */
		String namespace = super.findNamespace(config, annotated);
		
		if (!StringUtils.isEmpty(namespace) || (!(annotated instanceof AnnotatedMember member))) {
			return namespace;
		}
		
		// Check the declaring class as well for namespace clues
		final Class<?> rawClass = member.getDeclaringClass();
	
		/*
		 * This Class should be turned back into an AnnotatedClass so that findRootName
		 * above can be called on it; for now we replicate the functionality contained
		 * in superclasses using raw introspection.
		 */
		final JacksonXmlRootElement xmlRootElement = rawClass.getAnnotation(JacksonXmlRootElement.class);
		if (xmlRootElement != null && !StringUtils.isEmpty(xmlRootElement.namespace())) {
			return xmlRootElement.namespace();
		}

		final JsonRootName jsonRootName = rawClass.getAnnotation(JsonRootName.class);
		if (jsonRootName != null && !StringUtils.isEmpty(jsonRootName.namespace())) {
			return jsonRootName.namespace();
		}

		// Finally, see if the package has our custom annotation
		final Package rawPackage = rawClass.getPackage();
		
		final String packageNamespace = getPackageNamespace(rawPackage);
		if (!StringUtils.isEmpty(packageNamespace)) {
			return packageNamespace;
		}
		
		return namespace;
	}

	private String getPackageNamespace(final Package thePackage) {
		final XmlNamespace xmlNamespace = thePackage.getAnnotation(XmlNamespace.class);
		
		if (xmlNamespace != null) {
			return xmlNamespace.namespace();
		} else {
			return null;
		}
	}
	
	@Override
	public boolean hasIgnoreMarker(AnnotatedMember m) {
		return m.hasAnnotation(XmlIgnore.class) || super.hasIgnoreMarker(m);
	}
}
