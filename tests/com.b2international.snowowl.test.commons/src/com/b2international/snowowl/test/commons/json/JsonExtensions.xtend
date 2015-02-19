/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
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
		properties.entrySet.join("{", ",", "}") [asJson]
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