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
package com.b2international.snowowl.core.claml;

import java.util.Iterator;
import java.util.List;

/**
 * Collection of utility methods related to ClaML processing.
 * 
 */
public final class ClamlUtil {
	private ClamlUtil() {}
	
	public static void checkAndVisitAll(List<?> collection, ClamlModelVisitor visitor) {
		if (collection == null)
			return;
		Iterator<?> iterator = collection.iterator();
		while (iterator.hasNext()) {
			Object next = iterator.next();
			if (!(next instanceof VisitableClamlModelElement))
				throw new IllegalArgumentException("Unexpected collection item: " + next);
			((VisitableClamlModelElement) next).accept(visitor);
		}
	}
	
	public static String extractTextFromLabelXml(String xmlText) {
		return xmlText.replaceAll("<Label.*?>", "")
				.replaceAll("</Label>", "")
				.replaceAll("<Term.*?>", "")
				.replaceAll("</Term>", "")
				.replaceAll("<Para.*?>", "")
				.replaceAll("</Para>", "")
				.replaceAll("<Fragment.*?>", "")
				.replaceAll("</Fragment>", "")
				.replaceAll("<Reference.*?>.*</Reference>", ""); // XXX: this line removes the tags AND the text content
	}
	
	/**
	 * Appends the usage character to the specified {@link StringBuilder} based on the usage string.
	 * @param usageString
	 * @param builder
	 */
	public static StringBuilder appendUsageSymbol(String usageString, StringBuilder builder) {
		if ("aster".equalsIgnoreCase(usageString)) {
			builder.append('*');
		} else if ("dagger".equalsIgnoreCase(usageString)) {
			builder.append('+');
		}
		
		return builder;
	}
}