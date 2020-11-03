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
package com.b2international.commons.tree;

/**
 * Visitor for trees. Nodes are visited by entering/visit/leaving.
 * 
 *
 * @param <T>
 */
public interface TreeVisitor<T> {

	/**
	 * Visits a node.
	 * @param node
	 * @return true if the node is visited.
	 */
	public boolean visit(T node);
	
	/**
	 * Enters a tree node
	 * @param node
	 * @return true is the node is entered.
	 */
	public boolean entering(T node);
	
	/**
	 * Leaves a node.
	 * @param node
	 * @return true if the node is left after visiting.
	 */
	public boolean leaving(T node);
}