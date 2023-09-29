/*
 * (C) Copyright IBM Corp. 2018, 2021
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r4b.util;

import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Streams;

/*
 * Modifications:
 * 
 * - Use Guava and Jackson for low-level JSON node manipulation
 */

public class ElementFilter implements Function<JsonNode, JsonNode> {
    private static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.withExactBigDecimals(false);
    private static final List<String> REQUIRED_ELEMENTS = Arrays.asList("resourceType", "id", "meta");
    
    private Set<String> includeElements = new HashSet<>();

    public ElementFilter(Class<?> resourceType) {
        includeElements.addAll(REQUIRED_ELEMENTS);
        includeElements.addAll(JsonSupport.getRequiredElementNames(resourceType));
    }

    public ElementFilter(Class<?> resourceType, Collection<String> elements) {
        this(resourceType);
        includeElements.addAll(elements);
    }

    public void addElements(Collection<String> elements) {
        includeElements.addAll(elements);
    }

    @Override
    public JsonNode apply(JsonNode jsonNode) {
    	ObjectNode result = new ObjectNode(NODE_FACTORY);
        
    	Streams.stream(jsonNode.fields())
        	.filter(e -> includeElements.contains(e.getKey()))
            .forEach(e -> result.set(e.getKey(), e.getValue()));
        
        return result;
    }
}
