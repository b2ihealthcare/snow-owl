/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.util;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.linuxforhealth.fhir.model.format.Format;
import org.linuxforhealth.fhir.model.generator.exception.FHIRGeneratorException;
import org.linuxforhealth.fhir.model.r5.generator.FHIRGenerator;
import org.linuxforhealth.fhir.model.r5.resource.Resource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/*
 * Modifications:
 * 
 * - Use Jackson for low-level JSON node manipulation
 */

public final class JsonSupport {
	
    public static final JsonFactory JSON_FACTORY = JsonFactory.builder()
		.enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
		.build();
    
    public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper(JSON_FACTORY);

    private static final Map<Class<?>, Set<String>> ELEMENT_NAME_MAP = buildElementNameMap(false);
    private static final Map<Class<?>, Set<String>> REQUIRED_ELEMENT_NAME_MAP = buildElementNameMap(true);
    private static final Map<Class<?>, Set<String>> SUMMARY_ELEMENT_NAME_MAP = buildSummaryElementNameMap();
    private static final Map<Class<?>, Set<String>> SUMMARY_DATA_ELEMENT_NAME_MAP = new LinkedHashMap<>();

    private JsonSupport() { }

    /**
     * Calling this method allows us to load/initialize this class during startup.
     */
    public static void init() { }

    private static Map<Class<?>, Set<String>> buildElementNameMap(boolean required) {
        Map<Class<?>, Set<String>> elementNameMap = new LinkedHashMap<>();
        for (Class<?> modelClass : ModelSupport.getModelClasses()) {
            if (ModelSupport.isPrimitiveType(modelClass)) {
                continue;
            }
            Set<String> elementNames = new LinkedHashSet<>();
            for (String elementName : ModelSupport.getElementNames(modelClass)) {
                if (required && !ModelSupport.isRequiredElement(modelClass, elementName)) {
                    continue;
                }
                if (ModelSupport.isChoiceElement(modelClass, elementName)) {
                    for (Class<?> choiceElementType : ModelSupport.getChoiceElementTypes(modelClass, elementName)) {
                        String choiceElementName = ModelSupport.getChoiceElementName(elementName, choiceElementType);
                        elementNames.add(choiceElementName);
                        if (ModelSupport.isPrimitiveType(choiceElementType)) {
                            elementNames.add("_" + choiceElementName);
                        }
                    }
                } else {
                    elementNames.add(elementName);
                    Class<?> elementType = ModelSupport.getElementType(modelClass, elementName);
                    if (ModelSupport.isPrimitiveType(elementType)) {
                        elementNames.add("_" + elementName);
                    }
                }
            }
            elementNameMap.put(modelClass, Collections.unmodifiableSet(elementNames));
        }
        return Collections.unmodifiableMap(elementNameMap);
    }


    private static Map<Class<?>, Set<String>> buildSummaryElementNameMap() {
        Map<Class<?>, Set<String>> summaryElementNameMap = new LinkedHashMap<>();
        for (Class<?> modelClass : ModelSupport.getModelClasses()) {
            if (ModelSupport.isPrimitiveType(modelClass)) {
                continue;
            }
            Set<String> elementNames = new LinkedHashSet<>();
            for (String elementName : ModelSupport.getElementNames(modelClass)) {
                if (!ModelSupport.isSummaryElement(modelClass, elementName)) {
                    continue;
                }
                elementNames.add(elementName);
            }
            summaryElementNameMap.put(modelClass, Collections.unmodifiableSet(elementNames));
        }
        return Collections.unmodifiableMap(summaryElementNameMap);
    }

    public static Set<String> getElementNames(Class<?> type) {
        return ELEMENT_NAME_MAP.getOrDefault(type, Collections.emptySet());
    }

    public static Set<String> getSummaryElementNames(Class<?> type) {
        return Collections.unmodifiableSet(SUMMARY_ELEMENT_NAME_MAP.getOrDefault(type, Collections.emptySet()));
    }

    public static Set<String> getSummaryDataElementNames(Class<?> type) {
        if (SUMMARY_DATA_ELEMENT_NAME_MAP.get(type) != null) {
            return Collections.unmodifiableSet(SUMMARY_DATA_ELEMENT_NAME_MAP.get(type));
        } else {
            Set<String> summaryData = ELEMENT_NAME_MAP.getOrDefault(type, Collections.emptySet())
                .stream().filter(e -> !"text".equals(e)).collect(Collectors.toSet());
            SUMMARY_DATA_ELEMENT_NAME_MAP.put(type, summaryData);
            return summaryData;
        }
    }

    public static Set<String> getRequiredElementNames(Class<?> type) {
        return REQUIRED_ELEMENT_NAME_MAP.getOrDefault(type, Collections.emptySet());
    }

    public static ArrayNode getArrayNode(JsonNode jsonNode, String key) {
        return getArrayNode(jsonNode, key, false);
    }

    public static ArrayNode getArrayNode(JsonNode jsonNode, String key, boolean primitive) {
        ArrayNode jsonArray = getJsonNode(jsonNode, key, ArrayNode.class);
        if (primitive) {
            if (jsonArray == null) {
                ArrayNode _jsonArray = getArrayNode(jsonNode, "_" + key);
                if (_jsonArray != null) {
                    throw new IllegalArgumentException("Found array with key '_" + key + "' but could not find matching array with key: '" + key + "'");
                }
            }
        }
        return jsonArray;
    }

    public static JsonNode getJsonNode(ArrayNode jsonArray, int index) {
        if (jsonArray != null) {
            if (index >= 0 && index < jsonArray.size()) {
                return jsonArray.get(index);
            } else {
                throw new IllegalArgumentException("Could not find element at index: " + index);
            }
        }
        return null;
    }

    public static <T extends JsonNode> T getJsonNode(JsonNode jsonNode, String key, Class<T> expectedType) {
        JsonNode jsonValue = jsonNode.get(key);
        if (jsonValue != null && !expectedType.isInstance(jsonValue)) {
            throw new IllegalArgumentException("Expected: " + expectedType.getSimpleName() + " but found: " + jsonValue.getNodeType()
                                                + " for element: " + key);
        }
        return expectedType.cast(jsonValue);
    }

    // TODO: replace this method with a class that converts Resource to JsonNode directly
    public static JsonNode toJsonNode(Resource resource) throws FHIRGeneratorException {
        StringWriter writer = new StringWriter();
        FHIRGenerator.generator(Format.JSON).generate(resource, writer);
        try {
			return JSON_OBJECT_MAPPER.readTree(new StringReader(writer.toString()));
		} catch (IOException e) {
			throw new FHIRGeneratorException("Caugh exception while converting resource to JsonNode", null, e);
		}
    }

    public static Reader nonClosingReader(Reader reader) {
        return new FilterReader(reader) {
            @Override
            public void close() {
                // do nothing
            }
        };
    }

    public static InputStream nonClosingInputStream(InputStream in) {
        return new FilterInputStream(in) {
            @Override
            public void close() {
                // do nothing
            }
        };
    }

    public static Writer nonClosingWriter(Writer writer) {
        return new FilterWriter(writer) {
            @Override
            public void close() {
                // do nothing
            }
        };
    }

    public static OutputStream nonClosingOutputStream(OutputStream out) {
        return new FilterOutputStream(out) {
            @Override
            public void close() {
                // do nothing
            }
        };
    }

    public static void checkForUnrecognizedElements(Class<?> type, JsonNode jsonNode) {
        Set<java.lang.String> elementNames = JsonSupport.getElementNames(type);
        Iterable<java.lang.String> fieldNames = () -> jsonNode.fieldNames();
        for (java.lang.String key : fieldNames) {
            if (!elementNames.contains(key) && !"resourceType".equals(key) && !"fhir_comments".equals(key)) {
                throw new IllegalArgumentException("Unrecognized element: '" + key + "'");
            }
        }
    }

    public static Class<?> getResourceType(JsonNode jsonNode) {
        JsonNode resourceTypeString = jsonNode.get("resourceType");
        if (resourceTypeString == null) {
            throw new IllegalArgumentException("Missing required element: 'resourceType'");
        }
        String resourceTypeName = resourceTypeString.asText();
        Class<?> resourceType = ModelSupport.getResourceType(resourceTypeName);
        if (resourceType == null) {
            throw new IllegalArgumentException("Invalid resource type: '" + resourceTypeName + "'");
        }
        return resourceType;
    }
}
