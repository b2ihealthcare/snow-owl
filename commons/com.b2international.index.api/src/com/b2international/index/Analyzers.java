/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * Supported analyzers are listed here.
 * 
 * @since 5.0
 */
public enum Analyzers {

	/**
	 * The default term analyzer, by default escapes all whitespaces and any character from the default delimiters list and includes a leading and
	 * trailing marker from the Unicode Private Use Area characters to mark the beginning and end of a text.
	 */
	DEFAULT,

	/**
	 * Indexes the term as is for exact match searches with ASCII folding and in lower-case form.
	 * "exact_match": {
        	"tokenizer": "keyword",
        	"filter": [
        	  "asciifolding",
        	  "lowercase"
        	]
        }
	 */
	EXACT,

	
	/**
	 * "tokenized": {
	       "tokenizer": "whitespace",
	       "filter": [
	          "asciifolding",
	          "lowercase",
	          "possessive",
	          "word_splitter",
	          "unique_token"
	       ]
	    }
	    "word_splitter": {
            "type": "word_delimiter",
            "split_on_case_change": "false",
            "preserve_original": "true",
            "stem_english_possessive": "false",
            "type_table": [", => DIGIT", ". => DIGIT"]
        }
	 */
	TOKENIZED,

	/**
	 * "stemming": {
        	"tokenizer" : "whitespace",
        	"filter" : [
				"asciifolding",
				"lowercase",
				"possessive",
				"word_splitter",
				"unique_token",
				"english_stemmer"
        	]
        }
	 */
	STEMMING,

	/**
	 * "search_stemming": {
        	"tokenizer" : "whitespace",
        	"filter" : [
				"asciifolding",
				"lowercase",
				"possessive",
				"unique_token",
				"english_stemmer"
        	]
        }
	 */
	SEARCH_STEMMING,

	/**
	 * "prefix": {
           "tokenizer": "whitespace",
           "filter": [
              "asciifolding",
              "lowercase",
              "possessive",
              "word_splitter",
              "unique_token",
              "edge_ngram"
           ]
        }
	 */
	PREFIX,

	/**
	 * "case_sensitive": {
        	"tokenizer" : "whitespace",
        	"filter" : [
        		"possessive",
        		"word_splitter",
        		"unique_token"
        	]
        }
	 */
	CASE_SENSITIVE,
	
	/**
	 * "case_sensitive_prefix": {
           "tokenizer": "whitespace",
           "filter": [
              "asciifolding",
              "possessive",
              "word_splitter",
              "unique_token",
              "edge_ngram"
           ]
        }
	 */
	CASE_SENSITIVE_PREFIX,
	
	/**
	 * "case_sensitive_ascii_folding": {
        	"tokenizer" : "whitespace",
        	"filter" : [
				"asciifolding",
				"possessive",
				"word_splitter",
				"unique_token"
        	]
        }
	 */
	CASE_SENSITIVE_ASCII,
	
	/**
	 * "case_sensitive_ascii_folding_exact_match": {
        	"tokenizer" : "keyword",
        	"filter" : [
				"asciifolding"
        	]
        }
	 */
	CASE_SENSITIVE_ASCII_EXACT, 
	
	/**
	 * Use the same analyzer that is configured for the {@link Analyzed#analyzer()} field. 
	 */
	INDEX
	
}
