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
 * Walker for tree structures.
 *
 * @param <T>
 */
public class TreeWalker<T> {

	public interface ChildProvider<C> {
		public Iterable<C> getChildIterable(C node);
	}
	
	private TreeVisitor<T> visitor;
	private ChildProvider<T> childProvider;
	

	public TreeWalker(TreeVisitor<T> visitor, ChildProvider<T> childProvider) {
		this.visitor = visitor;
		this.childProvider = childProvider;
	}

	/**
	 * Walks a node.
	 */
	public boolean walk(T root) {
		return doWalk(root);
	}
	
	/**
	 * Recursively walks a tree
	 * @param root
	 * @return true if the node is recursively walked.
	 */
	protected boolean doWalk(T root) {
		
		if(visitor.visit(root)) {
			Iterable<T> childIterable = childProvider.getChildIterable(root);
			if(childIterable != null && visitor.entering(root)) {
					for(T child: childIterable) {
						if(!doWalk(child)) {
							return false;
						}
					}
					return visitor.leaving(root);
			}
			return true;
		} else {
			return false;
		}
	}

}