/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Objects;

import org.elasticsearch.common.collect.List;
import org.junit.Test;

import com.b2international.index.mapping.Field;
import com.b2international.index.mapping.FieldAlias;
import com.b2international.index.mapping.FieldAlias.FieldAliasType;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.11
 */
public class AnalyzerTest extends BaseIndexTest {

	@Doc
	private static final class DataWithTokenizedText {
		
		@ID
		private String id;
		
		@Field(
			aliases = {
				@FieldAlias(name = "tokenized", type = FieldAliasType.TEXT, analyzer = Analyzers.TOKENIZED)
			}
		)
		private String text;
		
		@JsonCreator
		public DataWithTokenizedText(@JsonProperty("id") String id, @JsonProperty("text") String text) {
			this.id = id;
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(id, text);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			DataWithTokenizedText other = (DataWithTokenizedText) obj;
			return Objects.equals(id, other.id) 
					&& Objects.equals(text, other.text);
		}
		
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(DataWithTokenizedText.class);
	}
	
	@Test
	public void tokenizedIgnoreStopwords() throws Exception {
		DataWithTokenizedText withStopwords = new DataWithTokenizedText(KEY1, "a quick fox jumps over the lazy dog and cat");
		indexDocument(withStopwords);
		DataWithTokenizedText withoutStopwords = new DataWithTokenizedText(KEY2, "quick fox jumps over lazy dog cat");
		indexDocument(withoutStopwords);
		
		// search with stopwords enabled
		Hits<DataWithTokenizedText> hits = search(Query.select(DataWithTokenizedText.class)
				.where(Expressions.matchTextAll("text.tokenized", "a quick fox jumps over the lazy dog and cat"))
				.build());
		// should return the document where the text has the stopwords
		assertThat(hits).containsOnly(withStopwords);
		
		// search with stopwords filtered
		hits = search(Query.select(DataWithTokenizedText.class)
				.where(Expressions.matchTextAll("text.tokenized", "a quick fox jumps over the lazy dog and cat").withAnalyzer(Analyzers.TOKENIZED_IGNORE_STOPWORDS))
				.build());
		// should return both document
		assertThat(hits).containsOnly(withStopwords, withoutStopwords);
	}
	
}
