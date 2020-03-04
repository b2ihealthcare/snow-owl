/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.util;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static springfox.documentation.schema.Collections.collectionElementType;
import static springfox.documentation.schema.Collections.isContainerType;
import static springfox.documentation.schema.Types.typeNameFor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.ClassUtils;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.primitives.Ints;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.Maps;
import springfox.documentation.schema.Types;
import springfox.documentation.schema.property.bean.AccessorsProvider;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spring.web.readers.parameter.ExpansionContext;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeField;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterMetadataAccessor;

/**
 * Overriden {@link ModelAttributeParameterExpander} to provide consistent ordering for query parameters.
 * 
 * @since 6.16
 */
public class ModelAttributeParameterExpanderExt extends ModelAttributeParameterExpander {

	private static final Logger LOG = LoggerFactory.getLogger(ModelAttributeParameterExpander.class);

	private final FieldProvider fieldProvider;
	private final EnumTypeDeterminer enumTypeDeterminer;

	public ModelAttributeParameterExpanderExt(FieldProvider fields, AccessorsProvider accessorsProvider, EnumTypeDeterminer enumTypeDeterminer) {
		super(fields, accessorsProvider, enumTypeDeterminer);
		this.fieldProvider = fields;
		this.enumTypeDeterminer = enumTypeDeterminer;
	}

	@Override
	public List<Parameter> expand(ExpansionContext context) {
		List<Parameter> parameters = newArrayList();
		Set<String> beanPropNames = getBeanPropertyNames(context.getParamType().getErasedType());
		AlternateTypeProvider alternateTypeProvider = context.getDocumentationContext().getAlternateTypeProvider();
		FluentIterable<ResolvedField> fields = FluentIterable.from(fieldProvider.in(context.getParamType())).filter(onlyBeanProperties(beanPropNames));
		LOG.debug("Expanding parameter type: {}", context.getParamType());

		FluentIterable<ModelAttributeField> modelAttributes = from(fields).transform(toModelAttributeField(alternateTypeProvider));

		FluentIterable<ModelAttributeField> expendables = modelAttributes.filter(not(simpleType())).filter(not(recursiveType(context)));
		for (ModelAttributeField each : expendables) {
			LOG.debug("Attempting to expand expandable field: {}", each.getFieldType());
			parameters.addAll(expand(context.childContext(nestedParentName(context.getParentName(), each), each.getFieldType(),
					context.getOperationContext())));
		}

		FluentIterable<ModelAttributeField> collectionTypes = modelAttributes
				.filter(and(isCollection(), not(recursiveCollectionItemType(context.getParamType()))));
		for (ModelAttributeField each : collectionTypes) {
			LOG.debug("Attempting to expand collection/array field: {}", each.getName());

			ResolvedType itemType = collectionElementType(each.getFieldType());
			if (Types.isBaseType(itemType) || enumTypeDeterminer.isEnum(itemType.getErasedType())) {
				parameters.add(simpleFields(context.getParentName(), context, each));
			} else {
				parameters.addAll(expand(context.childContext(nestedParentName(context.getParentName(), each), itemType,
						context.getOperationContext())));
			}
		}

		FluentIterable<ModelAttributeField> simpleFields = modelAttributes.filter(simpleType());
		for (ModelAttributeField each : simpleFields) {
			parameters.add(simpleFields(context.getParentName(), context, each));
		}
		
		List<String> fieldNamesInOrder = fields.transform(ResolvedField::getName).toList();
		return FluentIterable.from(parameters)
				.filter(not(hiddenParameters()))
				.toSortedList((p1, p2) -> Ints.compare(fieldNamesInOrder.indexOf(p1.getName()), fieldNamesInOrder.indexOf(p2.getName())));
	}

	private Predicate<ModelAttributeField> recursiveCollectionItemType(final ResolvedType paramType) {
		return new Predicate<ModelAttributeField>() {
			@Override
			public boolean apply(ModelAttributeField input) {
				return equal(collectionElementType(input.getFieldType()), paramType);
			}
		};
	}

	private Predicate<Parameter> hiddenParameters() {
		return new Predicate<Parameter>() {
			@Override
			public boolean apply(Parameter input) {
				return input.isHidden();
			}
		};
	}

	private Parameter simpleFields(String parentName, ExpansionContext context, ModelAttributeField each) {
		LOG.debug("Attempting to expand field: {}", each);
		String dataTypeName = Optional.fromNullable(typeNameFor(each.getFieldType().getErasedType()))
				.or(each.getFieldType().getErasedType().getSimpleName());
		LOG.debug("Building parameter for field: {}, with type: {}", each, each.getFieldType());
		ParameterExpansionContext parameterExpansionContext = new ParameterExpansionContext(
				dataTypeName, 
				parentName, 
				determineScalarParameterType(
		            context.getOperationContext().consumes(),
		            context.getOperationContext().httpMethod()),
				new ModelAttributeParameterMetadataAccessor(
			            each.annotatedElements(),
			            each.getFieldType(),
			            each.getName()),
				context.getDocumentationContext().getDocumentationType(), 
				new ParameterBuilder());
		return pluginsManager.expandParameter(parameterExpansionContext);
	}
	
	private static String determineScalarParameterType(Set<? extends MediaType> consumes, HttpMethod method) {
	    String parameterType = "query";

	    if (consumes.contains(MediaType.APPLICATION_FORM_URLENCODED)
	        && method == HttpMethod.POST) {
	      parameterType = "form";
	    } else if (consumes.contains(MediaType.MULTIPART_FORM_DATA)
	        && method == HttpMethod.POST) {
	      parameterType = "formData";
	    }

	    return parameterType;
	  }

	private Predicate<ModelAttributeField> recursiveType(final ExpansionContext context) {
		return new Predicate<ModelAttributeField>() {
			@Override
			public boolean apply(ModelAttributeField input) {
				return context.hasSeenType(input.getFieldType());
			}
		};
	}

	private Predicate<ModelAttributeField> simpleType() {
		return and(not(isCollection()), not(isMap()), or(belongsToJavaPackage(), isBaseType(), isEnum()));
	}

	private Predicate<ModelAttributeField> isCollection() {
		return new Predicate<ModelAttributeField>() {
			@Override
			public boolean apply(ModelAttributeField input) {
				return isContainerType(input.getFieldType());
			}
		};
	}

	private Predicate<ModelAttributeField> isMap() {
		return new Predicate<ModelAttributeField>() {
			@Override
			public boolean apply(ModelAttributeField input) {
				return Maps.isMapType(input.getFieldType());
			}
		};
	}

	private Predicate<ModelAttributeField> isEnum() {
		return new Predicate<ModelAttributeField>() {
			@Override
			public boolean apply(ModelAttributeField input) {
				return enumTypeDeterminer.isEnum(input.getFieldType().getErasedType());
			}
		};
	}

	private Predicate<ModelAttributeField> belongsToJavaPackage() {
		return new Predicate<ModelAttributeField>() {
			@Override
			public boolean apply(ModelAttributeField input) {
				return ClassUtils.getPackageName(input.getFieldType().getErasedType()).startsWith("java.lang");
			}
		};
	}

	private Predicate<ModelAttributeField> isBaseType() {
		return new Predicate<ModelAttributeField>() {
			@Override
			public boolean apply(ModelAttributeField input) {
				return Types.isBaseType(input.getFieldType()) || input.getFieldType().isPrimitive();
			}
		};
	}

	private Function<ResolvedField, ModelAttributeField> toModelAttributeField(final AlternateTypeProvider alternateTypeProvider) {
		return new Function<ResolvedField, ModelAttributeField>() {
			@Override
			public ModelAttributeField apply(ResolvedField input) {
				return new ModelAttributeField(fieldType(alternateTypeProvider, input), input.getName(), input, null);
			}
		};
	}

	private Predicate<ResolvedField> onlyBeanProperties(final Set<String> beanPropNames) {
		return new Predicate<ResolvedField>() {
			@Override
			public boolean apply(ResolvedField input) {
				return beanPropNames.contains(input.getName());
			}
		};
	}

	private String nestedParentName(String parentName, ModelAttributeField field) {
		String name = field.getName();
		ResolvedType fieldType = field.getFieldType();
		if (isContainerType(fieldType) && !Types.isBaseType(collectionElementType(fieldType))) {
			name += "[0]";
		}

		if (isNullOrEmpty(parentName)) {
			return name;
		}
		return String.format("%s.%s", parentName, name);
	}

	private ResolvedType fieldType(AlternateTypeProvider alternateTypeProvider, ResolvedField field) {
		return alternateTypeProvider.alternateFor(field.getType());
	}

	private Set<String> getBeanPropertyNames(final Class<?> clazz) {

		try {
			Set<String> beanProps = new HashSet<String>();
			PropertyDescriptor[] propDescriptors = getBeanInfo(clazz).getPropertyDescriptors();

			for (PropertyDescriptor propDescriptor : propDescriptors) {

				if (propDescriptor.getReadMethod() != null) {
					beanProps.add(propDescriptor.getName());
				}
			}

			return beanProps;

		} catch (IntrospectionException e) {
			LOG.warn(String.format("Failed to get bean properties on (%s)", clazz), e);
		}
		return newHashSet();
	}

	@VisibleForTesting
	BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
		return Introspector.getBeanInfo(clazz);
	}

}
