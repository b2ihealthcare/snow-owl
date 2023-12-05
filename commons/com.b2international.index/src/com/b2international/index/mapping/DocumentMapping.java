/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveCollection;
import com.b2international.collections.PrimitiveSet;
import com.b2international.commons.CompareUtils;
import com.b2international.index.*;
import com.b2international.index.migrate.SchemaRevision;
import com.b2international.index.revision.Revision;
import com.b2international.index.util.NumericClassUtils;
import com.b2international.index.util.Reflections;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.primitives.Primitives;

/**
 * @since 4.7
 */
public final class DocumentMapping {

	private static final Logger LOG = LoggerFactory.getLogger(DocumentMapping.class);
	
	public static final String _DOC = "_doc";
	
	/**
	 * Elasticsearch document mapping's _meta field that can be used to store application specific mapping related information.
	 * @see https://www.elastic.co/guide/en/elasticsearch/reference/7.17/mapping-meta-field.html 
	 */
	public static final String _META = "_meta";
	
	private static final Map<Class<?>, String> DOC_TYPE_CACHE = new MapMaker().makeMap();
	
	// type path delimiter to differentiate between same nested types in different contexts
	public static final String DELIMITER = ".";
	private static final Joiner DELIMITER_JOINER = Joiner.on(DELIMITER);

	/**
	 * Represents available fields in the _meta part of the document mapping in an Elasticsearch index.
	 * 
	 * @since 9.0
	 */
	public static final class Meta {
		
		/**
		 * Stores a single number representing the current model version of the given Java type.
		 */
		public static final String VERSION = "version";
		
	}

	private final Class<?> type;
	private final String typeAsString;
	private final Map<String, Field> fieldMap;
	private final Map<Class<?>, DocumentMapping> nestedTypes;
	private final TreeMap<String, FieldAlias> fieldAliases;
	private final DocumentMapping parent;
	private final Map<String, Script> scripts;
	private final SortedSet<String> trackedRevisionFields;
	private final String idField;
	private final boolean autoGeneratedId;
	private final String defaultSortField;
	private final SortedSet<String> selectableFields;

	private SortedMap<Long, SchemaRevision> schemaRevisionsByVersion;

	public DocumentMapping(Class<?> type) {
		this(type, false);
	}
	
	public DocumentMapping(Class<?> type, boolean nested) {
		this(type, null, nested);
	}
		
	public DocumentMapping(Class<?> type, DocumentMapping parent, boolean nested) {
		this.type = type;
		this.parent = parent;
		final String typeAsString = getDocType(type);
		this.typeAsString = parent == null ? typeAsString : parent.typeAsString() + DELIMITER + typeAsString;
		this.fieldMap = FluentIterable.from(Reflections.getFields(type)).filter(DocumentMapping::isValidField).uniqueIndex(Field::getName);

		final Collection<Field> idFields = fieldMap.values().stream()
				.filter(f -> f.isAnnotationPresent(ID.class))
				.collect(Collectors.toList());
		
		this.autoGeneratedId = hasAutoGenerateIDAnnotation(type);
		
		if (idFields.size() > 1) {
			throw new IllegalArgumentException(String.format("'%s' defines more than one ID annotated field: '%s'", type.getName(), idFields));
		} else if (idFields.size() == 1) {
			this.idField = Iterables.getOnlyElement(idFields).getName();
			checkArgument(getFieldType(this.idField) == String.class, "Only String ID annotated fields are supported");
		} else {
			if (!autoGeneratedId && !nested) {
				// report root documents if they does not have proper ID fields or automatic ID generation configured
				throw new IllegalArgumentException(String.format("'%s' does not define a mandatory ID annotated field nor request id fields to be auto-generated via AutoGeneratedId annotation.", type.getName()));
			} else {
				this.idField = null;
			}
		}
		
		final Collection<Field> defaultSortFields = fieldMap.values().stream()
				.filter(f -> f.isAnnotationPresent(com.b2international.index.mapping.Field.class))
				.filter(f -> f.getAnnotation(com.b2international.index.mapping.Field.class).defaultSortBy())
				.collect(Collectors.toList());
		
		if (defaultSortFields.size() > 1) {
			throw new IllegalArgumentException(String.format("'%s' defines more than one default sort field: '%s'", type.getName(), defaultSortFields));
		} else {
			this.defaultSortField = defaultSortFields.stream().findFirst().map(Field::getName)
					.or(() -> Optional.ofNullable(idField))
					.orElse(_DOC);
		}
		
		final Set<String> fieldAliasNames = new HashSet<>();
		final Builder<String, FieldAlias> aliasFields = ImmutableSortedMap.naturalOrder();
		for (Field field : getFields()) {
			if (field.isAnnotationPresent(com.b2international.index.mapping.Field.class)) {
				final String fieldName = field.getName();
				com.b2international.index.mapping.Field fieldAnnotation = field.getAnnotation(com.b2international.index.mapping.Field.class);
				Class<?> fieldType = NumericClassUtils.unwrapCollectionType(field);
				checkArgument(String.class == fieldType || fieldAnnotation.aliases().length == 0, "Field aliases are not supported on non-String fields: '%s/%s'.", type.getName(), fieldName);
				for (FieldAlias alias : fieldAnnotation.aliases()) {
					final String fieldAliasName = DELIMITER_JOINER.join(fieldName, alias.name());
					checkState(fieldAliasNames.add(fieldAliasName), "Multiple FieldAlias annotations have been found using the same alias on field '%s/%s'.", type.getName(), fieldAliasName);
					
					// check text/keyword analyzer/normalizer consistency
					switch (alias.type()) {
					case KEYWORD:
						checkArgument(alias.analyzer() == Analyzers.DEFAULT && alias.searchAnalyzer() == Analyzers.INDEX, "Invalid alias configuration on field '%s/%s'. Keyword aliases support only text normalization via normalizer. Analyzer and searchAnalyzer should be set to their default values or unset.", type.getName(), fieldAliasName);
						break;
					case TEXT:
						checkArgument(alias.normalizer() == Normalizers.NONE, "Invalid alias configuration on field '%s/%s'. Text aliases does not support text normalization via normalizer. Normalizer should be set to its default value or unset.", type.getName(), fieldAliasName);
						break;
					default: throw new UnsupportedOperationException("Unknown alias type: " + alias.type());
					}
					
					aliasFields.put(fieldAliasName, alias);
				}
			}
		}
		this.fieldAliases = new TreeMap<>(aliasFields.build());
		
		final Doc doc = getDocAnnotation(type);
		if (doc != null) {
			this.trackedRevisionFields = ImmutableSortedSet.copyOf(doc.revisionHash());
		} else {
			this.trackedRevisionFields = ImmutableSortedSet.of();
		}
		checkRevisionType();
				
		this.nestedTypes = getFields().stream()
			.map(field -> {
				if (Reflections.isMapType(field)) {
					return Map.class;
				} else {
					return Reflections.getType(field);
				}
			})
			.filter(fieldType -> isNestedDoc(fieldType))
			.distinct()
			.collect(Collectors.toMap(k -> k, k -> new DocumentMapping(k, DocumentMapping.this.parent == null ? DocumentMapping.this : DocumentMapping.this.parent, true)));
		
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
		
		final ImmutableSortedSet.Builder<String> selectableFields = ImmutableSortedSet.naturalOrder();

		for (Entry<String, Field> field : fieldMap.entrySet()) {
			final String fieldName = field.getKey();
			
			// exclude internal Revision fields
			if (Revision.class.isAssignableFrom(this.type) 
					&& Revision.Fields.CREATED.equals(fieldName)
					&& Revision.Fields.REVISED.equals(fieldName)) {
				continue;
			}
			
			selectableFields.add(fieldName);
		}
		
		this.selectableFields = selectableFields.build();
	}

	private boolean hasAutoGenerateIDAnnotation(Class<?> type) {
		return type.isAnnotationPresent(AutoGenerateID.class) || (type.getSuperclass() != null && hasAutoGenerateIDAnnotation(type.getSuperclass()));
	}

	private void checkRevisionType() {
		if (!this.trackedRevisionFields.isEmpty() && !Revision.class.isAssignableFrom(this.type)) {
			LOG.warn("Tracked fields feature is only supported in subtypes of the Revision class. '{}' is not a subtype of Revision.", type.getName());
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
		return List.copyOf(nestedTypes.values());
	}
	
	public boolean isNestedMapping(String field) {
		final Class<?> nestedType = Reflections.getType(getField(field));
		return nestedTypes.containsKey(nestedType);
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
		return getField(field).getType();
	}
	
	public Collection<Field> getFields() {
		return List.copyOf(fieldMap.values());
	}
	
	public Set<String> getSelectableFields() {
		return selectableFields;
	}

	public TreeMap<String, FieldAlias> getFieldAliases() {
		return fieldAliases;
	}
	
	public boolean isCollection(String field) {
		return isCollection(getFieldType(field));
	}
	
	public boolean isSet(String field) {
		final Class<?> fieldType = getFieldType(field);
		return Set.class.isAssignableFrom(fieldType) || PrimitiveSet.class.isAssignableFrom(fieldType);
	}
	
	public boolean isMap(String field) {
		final Class<?> fieldType = getFieldType(field);
		return Map.class.isAssignableFrom(fieldType);
	}
	
	public boolean isDocValuesEnabled(String field) {
		Field f = getField(field);
		com.b2international.index.mapping.Field fieldAnnotation = f.getAnnotation(com.b2international.index.mapping.Field.class);
		return fieldAnnotation == null || fieldAnnotation.index() == true;
	}

	private static boolean isCollection(Class<?> fieldType) {
		return Iterable.class.isAssignableFrom(fieldType) || PrimitiveCollection.class.isAssignableFrom(fieldType) || fieldType.isArray();
	}
	
	public boolean isObject(String field) {
		return isObject(getFieldType(field));
	}

	private static boolean isObject(Class<?> fieldType) {
		return !fieldType.isPrimitive() 
				&& !String.class.equals(fieldType)
				&& !BigDecimal.class.equals(fieldType) 
				&& !Primitives.isWrapperType(fieldType)
				&& !isCollection(fieldType);
	}
	
	/**
	 * Fields that are being tracked in commits and will receive proper conflict detection during merges/rebases.
	 * 
	 * @return a non-<code>null</code> {@link SortedSet} containing all tracked revision fields declared in the {@link #type()}'s {@link Doc} annotation.
	 */
	public SortedSet<String> getTrackedRevisionFields() {
		return trackedRevisionFields;
	}

	public Class<?> type() {
		return type;
	}
	
	public String typeAsString() {
		return typeAsString;
	}
	
	public boolean isAutoGeneratedId() {
		return autoGeneratedId;
	}
	
	public String getIdField() {
		checkArgument(hasIdField(), "Document has an auto-generated ID field, direct ID field support is not available. The #hasIdField() can be used for special casing.");
		return idField;
	}
	
	public String getDefaultSortField() {
		return defaultSortField;
	}
	
	public boolean hasIdField() {
		return !CompareUtils.isEmpty(idField);
	}
	
	public Map<String, FieldAlias> getFieldAliases(String fieldName) {
		return fieldAliases.subMap(fieldName, fieldName + Character.MAX_VALUE);
	}
	
	public Analyzers getSearchAnalyzer(String fieldName) {
		final FieldAlias analyzed = getFieldAliases().get(fieldName);
		return analyzed.searchAnalyzer() == Analyzers.INDEX ? analyzed.analyzer() : analyzed.searchAnalyzer();
	}
	
	
	public String getIdFieldValue(Object object) {
		if (object instanceof JsonNode) {
			return ((JsonNode) object).get(getIdField()).asText();
		} else {
			return (String) Reflections.getValue(object, getField(getIdField()));
		}
	}
	
	/**
	 * @return a {@link List} of {@link SchemaRevision}s registered on the Java type via {@link Doc#revisions()}.
	 */
	public List<SchemaRevision> getSchemaRevisions() {
		return List.copyOf(getSchemaRevisionsByVersion().values());
	}
	
	/**
	 * Returns {@link SchemaRevision} instances that needs to be run to migrate schema to the latest available version from the given version.
	 * 
	 * @param versionFrom
	 * @return
	 */
	public List<SchemaRevision> getSchemaRevisionsFrom(long versionFrom) {
		return List.copyOf(getSchemaRevisionsByVersion().tailMap(versionFrom + 1).values());
	}
	
	private SortedMap<Long, SchemaRevision> getSchemaRevisionsByVersion() {
		if (this.schemaRevisionsByVersion == null) {
			this.schemaRevisionsByVersion = Stream.of(getDocAnnotation(type).revisions()).collect(ImmutableSortedMap.toImmutableSortedMap(Long::compare, SchemaRevision::version, m -> m));
		}
		return this.schemaRevisionsByVersion;
	}
	
	/**
	 * Generates the current _meta mapping content from the current annotation values.
	 * 
	 * @return a {@link Map} containing mapping metadata related information about the type, eg. version
	 */
	public Map<String, Object> getMeta() {
		// select the current max version from the schema revision list or fall back to the default version 1
		final long currentVersion = getSchemaRevisionsByVersion().isEmpty() ? 1L : getSchemaRevisionsByVersion().lastKey();
		return Map.of(
			Meta.VERSION, currentVersion
		);
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
	
	public static boolean isNestedDoc(Class<?> fieldType) {
		final Doc doc = getDocAnnotation(fieldType);
		return doc == null ? false : doc.nested();
	}

	public static boolean isValidField(Field field) {
		return !Modifier.isStatic(field.getModifiers()) && !field.isAnnotationPresent(JsonIgnore.class) && (!field.isAnnotationPresent(com.b2international.index.mapping.Field.class) || !field.getAnnotation(com.b2international.index.mapping.Field.class).ignore());
	}
	
	public static Doc getDocAnnotation(Class<?> type) {
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

	public static String getDocType(Class<?> type) {
		if (!DOC_TYPE_CACHE.containsKey(type)) {
			final Doc annotation = DocumentMapping.getDocAnnotation(type);
			checkArgument(annotation != null, "Doc annotation must be present on type '%s' or on its class hierarchy", type);
			final String docType = Strings.isNullOrEmpty(annotation.type()) ? type.getSimpleName().toLowerCase() : annotation.type();
			checkArgument(!Strings.isNullOrEmpty(docType), "Document type should not be null or empty on class %s", type.getName());
			DOC_TYPE_CACHE.put(type, docType);
		}
		return DOC_TYPE_CACHE.get(type);
	}

}
