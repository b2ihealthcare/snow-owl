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
package com.b2international.commons.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

/**
 * Utility class to manage namespaces.
 *
 */
public class NameSpaceContextMap implements NamespaceContext {

	private Map<String, String> prefixMap = new HashMap<String, String>();
	private Map<String, String> uriMap = new HashMap<String, String>();
	
	public String getNamespaceURI(String prefix) {
		return uriMap.get(prefix);
	}

	public String getPrefix(String namespaceURI) {
		return prefixMap.get(namespaceURI);
	}

	public Iterator<String> getPrefixes(String namespaceURI) {
		return Collections.singletonList(prefixMap.get(namespaceURI)).iterator();
	}
	
	public void add(String prefix, String nameSpace) {
		uriMap.put(prefix, nameSpace);
		prefixMap.put(nameSpace, prefix);
	}

}