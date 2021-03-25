/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.mapping;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveCollection;
import com.b2international.index.*;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.util.Reflections;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @since 4.7
 */
public final class DocumentMapping {

	private static final Logger LOG = LoggerFactory.getLogger(DocumentMapping.class);
	
	private static final BiMap<Class<?>, String> DOC_TYPE_CACHE = HashBiMap.create();
	
	// type path delimiter to differentiate between same nested types in different contexts
	public static final String DELIMITER = ".";
	private static final Joiner DELIMITER_JOINER = Joiner.on(DELIMITER);
	
	/**
	 * @deprecated - use an explicit {@link ID} field instead of relying on the default _id metadata field
	 */
	public static final String _ID = "_id";

	private static final Function<? super Field, String> GET_NAME = Field::getName;
	
	private final Class<?> type;
	private final String typeAsString;
	private final Map<String, Field> fieldMap;
	private final Map<Class<?>, DocumentMapping> nestedTypes;
	private final TreeMap<String, Text> textFields;
	private final TreeMap<String, Keyword> keywordFields;
	private final DocumentMapping parent;
	private final Map<String, Script> scripts;
	private final Set<String> hashedFields;
	private final String idField;

	public DocumentMapping(Class<?> type) {
		this(null, type);
	}
		
	public DocumentMapping(DocumentMapping parent, Class<?> type) {
		this.parent = parent;
		this.type = type;
		final String typeAsString = getType(type);
		this.typeAsString = parent == null ? typeAsString : parent.typeAsString() + DELIMITER + typeAsString;
		this.fieldMap = FluentIterable.from(Reflections.getFields(type)).filter(DocumentMapping::isValidField).uniqueIndex(GET_NAME);

		final Collection<Field> idFields = fieldMap.values().stream()
				.filter(f -> f.isAnnotationPresent(ID.class))
				.collect(Collectors.toList());
		
		if (idFields.size() > 1) {
			throw new IllegalArgumentException("Document classes require a single field to be annotated with the ID annotation: " + type.getName());
		} else if (idFields.size() == 1) {
			this.idField = Iterables.getOnlyElement(idFields).getName();
		} else {
			LOG.warn("'{}' does not define an ID annotated field, falling back to the deprecated '_id', but keep in mind that support will be removed in 8.0", type.getName());
			this.idField = _ID;
		}
		
		
		final Builder<String, Text> textFields = ImmutableSortedMap.naturalOrder();
		final Builder<String, Keyword> keywordFields = ImmutableSortedMap.naturalOrder();

		for (Field field : getFields()) {
			for (Text analyzer : field.getAnnotationsByType(Text.class)) {
				if (Strings.isNullOrEmpty(analyzer.alias())) {
					textFields.put(field.getName(), analyzer);
				} else {
					textFields.put(DELIMITER_JOINER.join(field.getName(), analyzer.alias()), analyzer);
				}
			}
			for (Keyword analyzer : field.getAnnotationsByType(Keyword.class)) {
				if (Strings.isNullOrEmpty(analyzer.alias())) {
					keywordFields.put(field.getName(), analyzer);
				} else {
					keywordFields.put(DELIMITER_JOINER.join(field.getName(), analyzer.alias()), analyzer);
				}
			}
		}
		
		this.textFields = new TreeMap<>(textFields.build());
		this.keywordFields = new TreeMap<>(keywordFields.build());

		final Doc doc = getDocAnnotation(type);
		if (doc != null) {
			this.hashedFields = ImmutableSortedSet.copyOf(doc.revisionHash());
		} else {
			this.hashedFields = ImmutableSortedSet.of();
		}
				
		this.nestedTypes = FluentIterable.from(getFields())
			.transform(field -> {
				if (Reflections.isMapType(field)) {
					return Map.class;
				} else {
					return Reflections.getType(field);
				}
			})
			.filter(fieldType -> isNestedDoc(fieldType))
			.toMap(new Function<Class<?>, DocumentMapping>() {
				@Override
				public DocumentMapping apply(Class<?> input) {
					return new DocumentMapping(DocumentMapping.this.parent == null ? DocumentMapping.this : DocumentMapping.this.parent, input);
				}
			});
		
		this.scripts = new HashMap<>();
		getScripts(type).forEach(script -> this.scripts.put(script.name(), script));
		// add default scripts
		if (!this.scripts.containsKey("normalizeWithOffset")) {
			// add generic score normalizer to all mappings
			this.scripts.put("normalizeWithOffset", new Script() {
				@Override
				public Class<? extends Annotation> annotationType() {
					return Script.class;
				}
				
				@Override
				public String script() {
					return "(_score / (_score + 1.0f)) + params.offset";
				}
				
				@Override
				public String name() {
					return "normalizeWithOffset";
				}
			});
		}
	}

	private Collection<Script> getScripts(Class<?> type) {
		final Set<Script> scripts = newHashSet();
		for (Script script : type.getAnnotationsByType(Script.class)) {
			scripts.add(script);
		}
		// check superclass and superinterfaces
		if (type.getSuperclass() != null) {
			scripts.addAll(getScripts(type.getSuperclass()));
		}
		for (Class<?> iface : type.getInterfaces()) {
			scripts.addAll(getScripts(iface));
		}
		return scripts;
	}

	public DocumentMapping getParent() {
		return parent;
	}
	
	public Script getScript(String name) {
		return scripts.get(name);
	}
	
	public Collection<DocumentMapping> getNestedMappings() {
		return ImmutableList.copyOf(nestedTypes.values());
	}
	
	public boolean isNestedMapping(Class<?> fieldType) {
		return nestedTypes.containsKey(fieldType);
	}
	
	public DocumentMapping getNestedMapping(String field) {
		return nestedTypes.get(getNestedType(field));
	}
	
	public DocumentMapping getNestedMapping(Class<?> nestedType) {
		if (nestedTypes.containsKey(nestedType)) {
			return nestedTypes.get(nestedType);
		} else {
			for (DocumentMapping nestedMapping : nestedTypes.values()) {
				try {
					return nestedMapping.getNestedMapping(nestedType);
				} catch (IllegalArgumentException ignored) {
					continue;
				}
			}
			throw new IllegalArgumentException(String.format("Missing nested type '%s' on mapping of '%s'", nestedType, type));
		}
	}
	
	private Class<?> getNestedType(String field) {
		final Class<?> nestedType = Reflections.getType(getField(field));
		checkArgument(nestedTypes.containsKey(nestedType), "Missing nested type '%s' on mapping of '%s'", field, type);
		return nestedType;
	}
	
	public Field getField(String field) {
		checkArgument(fieldMap.containsKey(field), "Missing field '%s' on mapping of '%s'", field, type);
		return fieldMap.get(field);
	}
	
	public Class<?> getFieldType(String field) {
		// XXX: _id can be retrieved via field selection, but has no corresponding entry in the mapping
		if (DocumentMapping._ID.equals(field)) {
			return String.class;
		}
		return getField(field).getType();
	}
	
	public Collection<Field> getFields() {
		return ImmutableList.copyOf(fieldMap.values());
	}
	
	public boolean isText(String field) {
		return textFields.containsKey(field);
	}
	
	public boolean isKeyword(String field) {
		return keywordFields.containsKey(field);
	}
	
	public boolean isCollection(String field) {
		Class<?> fieldType = getFieldType(field);
		return Iterable.class.isAssignableFrom(fieldType) || PrimitiveCollection.class.isAssignableFrom(fieldType) || fieldType.getClass().isArray();
	}
	
	public Map<String, Text> getTextFields() {
		return textFields;
	}
	
	public Map<String, Keyword> getKeywordFields() {
		return keywordFields;
	}
	
	public Set<String> getRevisionFields() {
		return hashedFields;
	}

	public Class<?> type() {
		return type;
	}
	
	public String typeAsString() {
		return typeAsString;
	}
	
	public String getIdField() {
		return idField;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, parent);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final DocumentMapping other = (DocumentMapping) obj;
		return Objects.equals(type, other.type) && Objects.equals(parent, other.parent);
	}
	
	// static helpers
	
	/**
	 * @param id
	 * @return
	 * @deprecated - use an explicit {@link ID} field instead of relying on the default _id
	 */
	public static Expression matchId(String id) {
		return Expressions.exactMatch(_ID, id);
	}
	
	public static String getType(Class<?> type) {
		if (!DOC_TYPE_CACHE.containsKey(type)) {
			final Doc annotation = getDocAnnotation(type);
			checkArgument(annotation != null, "Doc annotation must be present on type '%s' or on its class hierarchy", type);
			final String docType = Strings.isNullOrEmpty(annotation.type()) ? type.getSimpleName().toLowerCase() : annotation.type();
			checkArgument(!Strings.isNullOrEmpty(docType), "Document type should not be null or empty on class %s", type.getName());
			DOC_TYPE_CACHE.put(type, docType);
		}
		return DOC_TYPE_CACHE.get(type);
	}
	
	/**
	 * @return all types currently registered in doc type mapping
	 */
	public static Collection<Class<?>> getTypes() {
		return ImmutableSet.copyOf(DOC_TYPE_CACHE.keySet());
	}
	
	public static Class<?> getClass(String type) {
		return checkNotNull(DOC_TYPE_CACHE.inverse().get(type), "Missing doc class for key '%s'. Populate the doc type cache via #getType(Class<?>) method before using this method.", type);
	}
	
	private static Doc getDocAnnotation(Class<?> type) {
		if (type.isAnnotationPresent(Doc.class)) {
			return type.getAnnotation(Doc.class);
		} else {
			if (type.getSuperclass() != null) {
				final Doc doc = getDocAnnotation(type.getSuperclass());
				if (doc != null) {
					return doc;
				}
			}
			
			for (Class<?> iface : type.getInterfaces()) {
				final Doc doc = getDocAnnotation(iface);
				if (doc != null) {
					return doc;
				}
			}
			return null;
		}
	}

	public static boolean isNestedDoc(Class<?> fieldType) {
		final Doc doc = getDocAnnotation(fieldType);
		return doc == null ? false : doc.nested();
	}

	public Map<String, Text> getTextFields(String fieldName) {
		return textFields.subMap(fieldName, fieldName + Character.MAX_VALUE);
	}
	
	public Map<String, Keyword> getKeywordFields(String fieldName) {
		return keywordFields.subMap(fieldName, fieldName + Character.MAX_VALUE);
	}
	
	public Analyzers getSearchAnalyzer(String fieldName) {
		final Text analyzed = getTextFields().get(fieldName);
		return analyzed.searchAnalyzer() == Analyzers.INDEX ? analyzed.analyzer() : analyzed.searchAnalyzer();
	}
	
	public static boolean isValidField(Field field) {
		return !Modifier.isStatic(field.getModifiers()) && !field.isAnnotationPresent(JsonIgnore.class);
	}

}
