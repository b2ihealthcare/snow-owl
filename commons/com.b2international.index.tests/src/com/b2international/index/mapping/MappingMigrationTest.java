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
package com.b2international.index.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import org.elasticsearch.common.collect.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.*;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.16.1
 */
public class MappingMigrationTest extends BaseIndexTest {

	@Doc(type = "schema") // this will ensure the same index name will be used for all subclasses and versions
	static class Schema {
		
		@ID
		private String id;
		private String field;
		
		@JsonCreator
		public Schema(@JsonProperty("id") String id, @JsonProperty("field") String field) {
			this.id = id;
			this.field = field;
		}
		
		public String getId() {
			return id;
		}
		
		public String getField() {
			return field;
		}
		
	}
	
	@Doc(type = "schema")
	static class SchemaWithNewField extends Schema {

		private String newField;
		
		@JsonCreator
		public SchemaWithNewField(@JsonProperty("id") String id, @JsonProperty("field") String field, @JsonProperty("newField") String newField) {
			super(id, field);
			this.newField = newField;
		}
		
		public String getNewField() {
			return newField;
		}
		
	}
	
	@Doc(type = "schema")
	static class SchemaWithNewTextField {

		@ID
		private String id;
		
		@Keyword
		@Text(alias = "text", analyzer = Analyzers.TOKENIZED)
		private String field;
		
		@JsonCreator
		public SchemaWithNewTextField(@JsonProperty("id") String id, @JsonProperty("field") String field) {
			this.id = id;
			this.field = field;
		}
		
		public String getId() {
			return id;
		}
		
		public String getField() {
			return field;
		}
		
	}
	
	@Doc(type = "schema")
	static class SchemaWithNewKeywordField {

		@ID
		private String id;
		
		@Keyword
		@Keyword(alias = "exact", normalizer = Normalizers.LOWER_ASCII)
		private String field;
		
		@JsonCreator
		public SchemaWithNewKeywordField(@JsonProperty("id") String id, @JsonProperty("field") String field) {
			this.id = id;
			this.field = field;
		}
		
		public String getId() {
			return id;
		}
		
		public String getField() {
			return field;
		}
		
	}
	
	@Doc(type = "schema")
	static class SchemaWithTextFieldAndNewKeywordField {

		@ID
		private String id;
		
		@Keyword
		@Text(alias = "text", analyzer = Analyzers.TOKENIZED)
		@Keyword(alias = "exact", normalizer = Normalizers.LOWER_ASCII)
		private String field;
		
		@JsonCreator
		public SchemaWithTextFieldAndNewKeywordField(@JsonProperty("id") String id, @JsonProperty("field") String field) {
			this.id = id;
			this.field = field;
		}
		
		public String getId() {
			return id;
		}
		
		public String getField() {
			return field;
		}
		
	}

	private Schema existingDoc1;
	private Schema existingDoc2;
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(); // no types at the beginning
	}
	
	@Before
	public void setup() {
		// enable class overrides for test(s) in this class
		DocumentMappingRegistry.INSTANCE.enableRuntimeMappingOverrides = true;
		// make sure we always start with the basic Schema
		admin().updateMappings(new Mappings(Schema.class));
		admin().create();
		// index two documents with the existing schema
		existingDoc1 = new Schema(KEY1, "Existing Field One");
		existingDoc2 = new Schema(KEY2, "Existing Field Two");
		indexDocuments(existingDoc1, existingDoc2);
	}

	@Test
	public void migrateNewField() throws Exception {
		// update Mappings to new schema
		admin().updateMappings(new Mappings(SchemaWithNewField.class));
		// then recreate indices using the new mappings (and to perform potential migration as well)
		admin().create();

		assertDocEquals(existingDoc1, getDocument(SchemaWithNewField.class, KEY1));
		assertDocEquals(existingDoc2, getDocument(SchemaWithNewField.class, KEY2));
	}
	
	@Test
	public void migrateNewTextField() throws Exception {
		// update Mappings to new schema
		admin().updateMappings(new Mappings(SchemaWithNewTextField.class));
		// then recreate indices using the new mappings (and to perform potential migration as well)
		admin().create();

		assertDocEquals(existingDoc1, getDocument(SchemaWithNewTextField.class, KEY1));
		assertDocEquals(existingDoc2, getDocument(SchemaWithNewTextField.class, KEY2));
		
		Hits<SchemaWithNewTextField> hits = search(Query.select(SchemaWithNewTextField.class)
				.where(Expressions.matchTextAll("field.text", "one"))
				.build());
		assertThat(hits).hasSize(1);
	}
	
	@Test
	public void migrateNewKeywordField() throws Exception {
		// update Mappings to new schema
		admin().updateMappings(new Mappings(SchemaWithNewKeywordField.class));
		// then recreate indices using the new mappings (and to perform potential migration as well)
		admin().create();

		assertDocEquals(existingDoc1, getDocument(SchemaWithNewKeywordField.class, KEY1));
		assertDocEquals(existingDoc2, getDocument(SchemaWithNewKeywordField.class, KEY2));
		
		Hits<SchemaWithNewKeywordField> hits = search(Query.select(SchemaWithNewKeywordField.class)
				.where(Expressions.exactMatch("field.exact", "existing field one"))
				.build());
		assertThat(hits).hasSize(1);
	}
	
	@Test
	public void migrateNewKeywordFieldOnExistingKeywordAndTextField() throws Exception {
		// update Mapping to perform the first migration
		admin().updateMappings(new Mappings(SchemaWithNewTextField.class));
		admin().create();

		// then apply the actual test migration to the extra keyword field alias
		admin().updateMappings(new Mappings(SchemaWithTextFieldAndNewKeywordField.class));
		admin().create();

		assertDocEquals(existingDoc1, getDocument(SchemaWithTextFieldAndNewKeywordField.class, KEY1));
		assertDocEquals(existingDoc2, getDocument(SchemaWithTextFieldAndNewKeywordField.class, KEY2));
		
		assertThat(
			search(Query.select(SchemaWithTextFieldAndNewKeywordField.class)
				.where(Expressions.matchTextAll("field.text", "one"))
				.build())
		).hasSize(1);
		
		assertThat(
			search(Query.select(SchemaWithTextFieldAndNewKeywordField.class)
				.where(Expressions.exactMatch("field.exact", "existing field one"))
				.build())
		).hasSize(1);
	}
	
	@After
	public void teardown() {
		// clear the custom indices
		admin().updateMappings(new Mappings(Schema.class));
		admin().clear(List.of(Schema.class));
		// disable class overrides after running the test(s)
		DocumentMappingRegistry.INSTANCE.enableRuntimeMappingOverrides = false;
	}
	
	/*
	 * Helper method to access the IndexAdmin for the revision index and not for the underlying raw index.
	 */
	private IndexAdmin admin() {
		return index.getRevisionIndex().admin();
	}
	
}
