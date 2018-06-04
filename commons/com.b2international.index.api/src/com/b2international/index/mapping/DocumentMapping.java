/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Sets.newHashSet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import com.b2international.index.Analyzers;
import com.b2international.index.Doc;
import com.b2international.index.RevisionHash;
import com.b2international.index.Keyword;
import com.b2international.index.Script;
import com.b2international.index.Text;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.util.Reflections;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;

/**
 * @since 4.7
 */
public final class DocumentMapping {

	// type path delimiter to differentiate between same nested types in different contexts
	public static final String DELIMITER = ".";
	private static final Joiner DELIMITER_JOINER = Joiner.on(DELIMITER);
	
	public static final String _ID = "_id";
	public static final String _UID = "_uid";
	public static final String _TYPE = "_type";
	public static final String _HASH = "_hash";

	private static final Function<? super Field, String> GET_NAME = new Function<Field, String>() {
		@Override
		public String apply(Field field) {
			return field.getName();
		}
	};
	
	private final Class<?> type;
	private final String typeAsString;
	private final Map<String, Field> fieldMap;
	private final Map<Class<?>, DocumentMapping> nestedTypes;
	private final TreeMap<String, Text> textFields;
	private final TreeMap<String, Keyword> keywordFields;
	private final DocumentMapping parent;
	private final Map<String, Script> scripts;
	private final Set<String> hashedFields;

	public DocumentMapping(Class<?> type) {
		this(null, type);
	}
		
	public DocumentMapping(DocumentMapping parent, Class<?> type) {
		this.parent = parent;
		this.type = type;
		final String typeAsString = getType(type);
		this.typeAsString = parent == null ? typeAsString : parent.typeAsString() + DELIMITER + typeAsString;
		this.fieldMap = FluentIterable.from(Reflections.getFields(type))
			.filter(new Predicate<Field>() {
				@Override
				public boolean apply(Field field) {
					return !Modifier.isStatic(field.getModifiers());
				}
			}).uniqueIndex(GET_NAME);
		
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

		// @RevisionHash should be directly present, not inherited
		final RevisionHash revisionHash = type.getDeclaredAnnotation(RevisionHash.class);
		if (revisionHash != null) {
			this.hashedFields = ImmutableSortedSet.copyOf(revisionHash.value());
		} else {
			this.hashedFields = ImmutableSortedSet.of();
		}
				
		this.nestedTypes = FluentIterable.from(getFields())
			.transform(new Function<Field, Class<?>>() {
				@Override
				public Class<?> apply(Field field) {
					if (Reflections.isMapType(field)) {
						return Map.class;
					} else {
						return Reflections.getType(field);
					}
				}
			})
			.filter(new Predicate<Class<?>>() {
				@Override
				public boolean apply(Class<?> fieldType) {
					return isNestedDoc(fieldType);
				}
			})
			.toMap(new Function<Class<?>, DocumentMapping>() {
				@Override
				public DocumentMapping apply(Class<?> input) {
					return new DocumentMapping(DocumentMapping.this.parent == null ? DocumentMapping.this : DocumentMapping.this.parent, input);
				}
			});
		
		this.scripts = Maps.uniqueIndex(getScripts(type), Script::name);
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
	
	public Field getField(String name) {
		checkArgument(fieldMap.containsKey(name), "Missing field '%s' on mapping of '%s'", name, type);
		return fieldMap.get(name);
	}
	
	public Class<?> getFieldType(String key) {
		// XXX: _hash can be retrieved via field selection, but has not corresponding entry in the mapping
		if (DocumentMapping._HASH.equals(key)) {
			return String.class;
		}
		return getField(key).getType();
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
	
	public Map<String, Text> getTextFields() {
		return textFields;
	}
	
	public Map<String, Keyword> getKeywordFields() {
		return keywordFields;
	}
	
	public Set<String> getHashedFields() {
		return hashedFields;
	}

	public Class<?> type() {
		return type;
	}
	
	public String typeAsString() {
		return typeAsString;
	}
	
	public Expression matchType() {
		return Expressions.exactMatch(_TYPE, typeAsString);
	}
	
	public String toUid(String key) {
		return String.format("%s#%s", typeAsString, key);
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
	
	public static Expression matchId(String id) {
		return Expressions.exactMatch(_ID, id);
	}
	
	public static String getType(Class<?> type) {
		final Doc annotation = getDocAnnotation(type);
		checkArgument(annotation != null, "Doc annotation must be present on type '%s' or on its class hierarchy", type);
		final String docType = Strings.isNullOrEmpty(annotation.type()) ? type.getSimpleName().toLowerCase() : annotation.type();
		checkArgument(!Strings.isNullOrEmpty(docType), "Document type should not be null or empty on class %s", type.getName());
		return docType;
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

}
