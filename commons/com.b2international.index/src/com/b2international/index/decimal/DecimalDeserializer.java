package com.b2international.index.decimal;

import java.io.IOException;
import java.math.BigDecimal;

import com.b2international.index.util.DecimalUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * {@link JsonDeserializer} implementation that deserializes ordered Base 64
 * encoded {@link String}s to {@link BigDecimal} values.
 * 
 * @since 5.10
 */
public final class DecimalDeserializer extends JsonDeserializer<BigDecimal> {

	@Override
	public BigDecimal deserialize(JsonParser parser, DeserializationContext ctx)
			throws IOException, JsonProcessingException {
		return DecimalUtils.decode(parser.getValueAsString());
	}

}