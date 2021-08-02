/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.domain.structuredefinition;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.AggregationMode;
import com.b2international.snowowl.fhir.core.codesystems.DiscriminatorType;
import com.b2international.snowowl.fhir.core.codesystems.ReferenceVersionRules;
import com.b2international.snowowl.fhir.core.codesystems.SlicingRules;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.structuredefinition.*;
import com.b2international.snowowl.fhir.core.model.typedproperty.StringProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.TypedProperty;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link ElementDefinitionTest}
 * @since 8.0.0
 */
public class ElementDefinitionTest extends FhirTest {
	
	private ElementDefinition elementDefinition;

	@Before
	public void setup() throws Exception {
		
		elementDefinition = ElementDefinition.builder()
				.path("elementPath")
				.addAlias("alias")
				.addCondition(new Id("condition"))
				.isModifier(false)
				.slicing(Slicing.builder()
						.ordered(true)
						.rules(SlicingRules.OPEN)
						.addDiscriminator(Discriminator.builder()
								.id("id")
								.path("path")
								.type(DiscriminatorType.VALUE)
								.build())
						.build())
				.base(Base.builder()
					.id("id")
					.min(1)
					.max("2") //TODO: check
					.path("path")
					.build())
				.addCode(Coding.builder()
					.code("coding")
					.display("codingDisplay")
					.build())
				.addExample(Example.builder()
						.label("example")
						.value(new StringProperty("value"))
						.build())
				.addConstraint(Constraint.builder()
						.id("id")
						.key("key")
						.human("human")
						.severity("severity")
						.expression("expression")
						.xpath("xpath")
						.build())
				.addType(Type.builder()
						.addAggregation(AggregationMode.BUNDLED)
						.code("code")
						.versioning(ReferenceVersionRules.EITHER)
						.targetProfile("targetProfile")
						.build())
				.defaultValue(new StringProperty("defaultValue"))
				.minValue(new StringProperty("minValue"))
				.pattern(new StringProperty("pattern"))
				.fixed(new StringProperty("fixed"))
				.binding(Binding.builder()
						.id("bindingId")
						.description("bindingDescription")
						.strength("strength")
						.build())
				.addMapping(MappingElement.builder()
						.id("mappingElementId")
						.identity("identity")
						.map("map")
						.build())
				.build();
	}
	
	@Test
	public void build() throws Exception {
		printPrettyJson(elementDefinition);
		validate(elementDefinition);
	}
	
	private void validate(ElementDefinition elementDefinition) {
		assertEquals("elementPath", elementDefinition.getPath());
		TypedProperty<?> defaultValue = elementDefinition.getDefaultValue();
		assertTrue(defaultValue instanceof StringProperty);
		assertEquals("defaultValue", defaultValue.getValueString());
		assertEquals("alias", elementDefinition.getAliases().iterator().next());
		assertEquals(false, elementDefinition.getIsModifier());
		assertEquals("elementPath", elementDefinition.getPath());
		
		assertEquals("minValue", elementDefinition.getMinValue().getValueString());
		assertEquals("pattern", elementDefinition.getPattern().getValueString());
		assertEquals("fixed", elementDefinition.getFixed().getValueString());
		
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(elementDefinition);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(elementDefinition));
		assertThat(jsonPath.getString("defaultValueString"), equalTo("defaultValue"));
		assertThat(jsonPath.getString("patternString"), equalTo("pattern"));
		assertThat(jsonPath.getString("fixedString"), equalTo("fixed"));
		assertThat(jsonPath.getString("minValueString"), equalTo("minValue"));
		assertThat(jsonPath.getString("path"), equalTo("elementPath"));
		assertThat(jsonPath.getString("slicing.discriminator[0].id"), equalTo("id"));
		assertThat(jsonPath.getString("slicing.discriminator[0].type"), equalTo("value"));
		assertThat(jsonPath.getString("slicing.discriminator[0].path"), equalTo("path"));
	}
	
	@Test
	public void deserialize() throws Exception {
		ElementDefinition readElementDefinition = objectMapper.readValue(objectMapper.writeValueAsString(elementDefinition), ElementDefinition.class);
		validate(readElementDefinition);
	}

}
