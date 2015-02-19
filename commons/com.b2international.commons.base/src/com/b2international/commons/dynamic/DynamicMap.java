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
package com.b2international.commons.dynamic;

import java.util.Collection;
import java.util.Set;

/**
 * Represents an immutable map that associates {@link String}s with
 * {@link DynamicValue}s.
 * 
 */
public interface DynamicMap extends DynamicValue {

	/**
	 * Stores mapped property-value pairs. Properties are expressed in dot
	 * notation, and may span multiple lists and maps (ex.: "<code>a.b.arr.0.item</code>").
	 * 
	 * @see DynamicMap#getProperty(String)
	 */
	public interface Entry {

		/**
		 * @return the property key for this mapping entry
		 */
		String getPropertyKey();
		
		/**
		 * @return the stored value for this mapping entry
		 */
		DynamicValue getValue();
	}
	
	/**
	 * @return the number of mapped values
	 */
	int size();
	
	/**
	 * @return <code>true</code> if there are no associated items in this
	 *         map, <code>false</code> otherwise
	 */
	boolean isEmpty();
	
	/**
	 * @return a set of {@link String} keys for which a corresponding
	 *         {@link DynamicValue} is present in this map
	 */
	Set<String> keySet();
	
	/**
	 * @return a collection of all mapped {@link DynamicValue}s 
	 */
	Collection<DynamicValue> values();
	
	/**
	 * Checks if this map contains a mapping for the specified key.
	 * 
	 * @param key
	 *            the key to test
	 *            
	 * @return <code>true</code> if a mapping is present for this key (meaning a
	 *         {@link #get(String)} call will return a non-<code>null</code>
	 *         value), <code>false</code> otherwise
	 */
	boolean containsKey(String key);
	
	/**
	 * Returns the mapped value for the specified key, or a value wrapping
	 * <code>null</code> if no such mapping exists.
	 * 
	 * @param key
	 *            the requested key
	 * 
	 * @return the associated value, or {@link DynamicValue#MISSING} 
	 */
	DynamicValue get(String key);
	
	/**
	 * Returns the mapped value for the specified key, or a value wrapping
	 * <code>defaulValue</code> if no such mapping exists.
	 * 
	 * @param key
	 *            the requested key
	 * 
	 * @param defaultValue
	 *            the value to wrap if no mapping exists
	 * 
	 * @return the associated value, or <code>defaultValue</code>
	 */
	DynamicValue get(String key, Object defaultValue);
	
	/**
	 * Returns a collection of matching mapping for the specified property.
	 * Properties are expressed using dot notation.
	 * <p>
	 * Given the following structure, where
	 * <code>{ a: 5, b: [3, "a", 1.0] }</code> represents a dynamic map with
	 * keys "a" and "b", the value of "a" is 5 and the value of "b" is a dynamic
	 * list with the items 3, "a" and 1.0:
	 * 
	 * <pre>
	 * {
	 *     name: "John",
	 *     age: 32,
	 *     score: [ 6, 10, { medal: "gold", points: 10 } ]
	 * }
	 * </pre>
	 * 
	 * The following entries will be returned for the properties below:
	 * <p>
	 * <table border="1">
	 * <tr>
	 * <td><b>Property</b></td>
	 * <td><b>Property key</b></td>
	 * <td><b>PropertyValue</b> (wrapped in a DynamicValue instance)</td>
	 * <td><b>Note</b></td>
	 * </tr>
	 * <tr>
	 * <td>name</td>
	 * <td>"name"</td>
	 * <td>"John"</td>
	 * <td>&nbsp;</td>
	 * </tr>
	 * <tr>
	 * <td>score</td>
	 * <td>"score"</td>
	 * <td>[ 6, 10, { medal: "gold", points: 10 } ]</td>
	 * <td>Returns the array itself</td>
	 * </tr>
	 * <tr>
	 * <td rowspan="3">score.*</td>
	 * <td>"score.0"</td>
	 * <td>6</td>
	 * <td rowspan="3">Returns the contents of the array</td>
	 * </tr>
	 * <tr>
	 * <td>"score.1"</td>
	 * <td>10</td>
	 * </tr>
	 * <tr>
	 * <td>"score.2"</td>
	 * <td>{ medal: "gold", points: 10 }</td>
	 * </tr>
	 * <tr>
	 * <td>score.0</td>
	 * <td>"score.0"</td>
	 * <td>6</td>
	 * <td>Returns a single indexed item</td>
	 * </tr>
	 * <tr>
	 * <td>score.2.medal</td>
	 * <td>"score.2.medal"</td>
	 * <td>"gold"</td>
	 * <td>Returns a subproperty of an indexed item</td>
	 * </tr>
	 * <tr>
	 * <td>score.medal</td>
	 * <td>"score.2.medal"</td>
	 * <td>"gold"</td>
	 * <td>Returns a subproperty for all indexed items that have it</td>
	 * </tr>
	 * </table>
	 * <p>
	 * If there are no matching properties, an empty collection is returned.
	 * 
	 * @param property
	 *            the property to test
	 *            
	 * @return matching entries carrying properties and values
	 */
	Collection<Entry> getProperty(String property);
	
	/**
	 * Checks if any of the parts of the specified property chain is associated
	 * with a list, with the exception of the last part. If the result is
	 * <code>false</code>, the collection returned from
	 * {@link #getProperty(String)} for the same property can have at most one
	 * entry.
	 * 
	 * @param property
	 *            the property to test
	 * 
	 * @return <code>true</code> if any part of the property chain corresponds
	 *         to a list, <code>false</code> otherwise
	 */
	boolean propertyContainsList(String property);
	
	/**
	 * Returns the first matching property value for the given property, or a
	 * value wrapping <code>null</code> if no match could be found.
	 * 
	 * @param property
	 *            the property to test
	 *            
	 * @return the value of the first match, or {@link DynamicValue#MISSING}
	 */
	DynamicValue getFirstPropertyValue(String property);
	
	/**
	 * Returns the first matching property value for the given property, or a
	 * value wrapping <code>defaultValue</code> if no match could be found.
	 * 
	 * @param property
	 *            the property to test
	 *            
	 * @param defaultValue
	 *            the value to return if there are no matching properties
	 *            
	 * @return the value of the first match, or <code>defaultValue</code>
	 */
	DynamicValue getFirstPropertyValue(String property, Object defaultValue);
}