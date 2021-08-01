package com.b2international.snowowl.fhir.core.model.typedproperty;

import java.io.IOException;

import com.b2international.snowowl.fhir.core.model.structuredefinition.ElementDefinition;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class TypedPropertyDeserializer extends StdDeserializer<ElementDefinition> {
	
	private static final long serialVersionUID = 1L;
	
	private static final String VALUE_PREFIX = "value";
	
	protected TypedPropertyDeserializer() {
		super(ElementDefinition.class);
	}

	@Override
	public ElementDefinition deserialize(JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		
		ObjectCodec oc = parser.getCodec();
	    JsonNode node = oc.readTree(parser);
	    ElementDefinition elementDefinition = null;

	    DeserializationConfig config = ctxt.getConfig();
	    JavaType type = TypeFactory.defaultInstance().constructType(ElementDefinition.class);
	    JsonDeserializer<Object> defaultDeserializer = BeanDeserializerFactory.instance.buildBeanDeserializer(ctxt, type, config.introspect(type));

	    
	    if (defaultDeserializer instanceof ResolvableDeserializer) {
	        ((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
	    }

	    JsonParser treeParser = oc.treeAsTokens(node);
	    config.initialize(treeParser);

	    if (treeParser.getCurrentToken() == null) {
	        treeParser.nextToken();
	    }

	    elementDefinition = (ElementDefinition) defaultDeserializer.deserialize(treeParser, ctxt);
	    
	    return elementDefinition;
		
//		TreeNode node = parser.readValueAsTree();
//		ObjectCodec objectCodec = parser.getCodec();
//		
//		Iterator<String> fieldNames = node.fieldNames();
//		
//		String valueFieldName = null;
//		
//		while (fieldNames.hasNext()) {
//			String fieldName = (String) fieldNames.next();
//			if (fieldName.startsWith(VALUE_PREFIX)) {
//				valueFieldName = fieldName.replace(VALUE_PREFIX, "");
//				break;
//			}
//		}
//
//		if (valueFieldName == null) {
//			throw new IllegalArgumentException("Invalid parameter type with null value.");
//		}
//		
//		switch (valueFieldName) {
//		case "String":
//			return objectCodec.treeToValue(node, StringProperty.class);
//		default:
//			return objectCodec.treeToValue(node, StringProperty.class);
//			//throw new IllegalArgumentException("Unsupported useage context type '" + valueFieldName + "'.");
//		}
	}
}