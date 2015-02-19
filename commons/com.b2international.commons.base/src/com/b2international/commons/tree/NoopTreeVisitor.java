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
 * Tree visitor implementation that accepts all nodes.
 */
public class NoopTreeVisitor<T> implements TreeVisitor<T> {

	/* (non-Javadoc)
	 * @see com.b2international.commons.tree.TreeVisitor#visit(java.lang.Object)
	 */
	@Override
	public boolean visit(final T node) {
		doVisit(node);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.tree.TreeVisitor#entering(java.lang.Object)
	 */
	@Override
	public boolean entering(final T node) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.tree.TreeVisitor#leaving(java.lang.Object)
	 */
	@Override
	public boolean leaving(final T node) {
		return true;
	}

	protected void doVisit(final T node) {
		//does nothing by default
	}
	
}