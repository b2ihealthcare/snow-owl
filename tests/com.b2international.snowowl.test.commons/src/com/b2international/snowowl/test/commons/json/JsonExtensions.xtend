/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons.json

import java.util.Collection
import java.util.List
import java.util.Map
import java.util.Map.Entry

/**
 * Useful Xtend extensions when working with JSON data.
 * 
 * @since 1.0 
 */
class JsonExtensions {
	
	def dispatch static String asJson(Map<String, Object> properties) {
		if (properties.empty) {
			return "{}"
		} else {
			return properties.entrySet.join("{", ",", "}") [asJson]
		}
	}
	
	def dispatch static String asJson(Collection<Pair<String, Object>> properties) {
		properties.join("{", ",", "}") [asJson]
	}
	
	def dispatch static String asJson(List<?> properties) {
		properties.join("[", ",", "]") [asJson]
	}
	
	def dispatch static String asJson(Object it) '''«it»'''
	
	def dispatch static String asJson(Entry<String, Object>  it) '''"«key»":«value.asJson»'''
	
	def dispatch static String asJson(Pair<String, Object> it) '''"«key»":«value.asJson»'''
	
	def dispatch static String asJson(String it) '''"«it»"'''
}