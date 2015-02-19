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
package com.b2international.snowowl.snomed.dsl.query.walker;

import com.b2international.commons.tree.TreeVisitor;
import com.b2international.commons.tree.emf.EObjectTreeNode;
import com.b2international.commons.tree.emf.EObjectWalker;
import com.b2international.snowowl.snomed.dsl.query.QueryRewriter;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;

public class VisitingQueryRewriter implements QueryRewriter<RValue> {

	private TreeVisitor<EObjectTreeNode> visitor;
	
	@Override
	public RValue rewrite(RValue root) {
		EObjectWalker walker = EObjectWalker.createContainmentWalker(visitor);
		walker.walk(root);
		return root;
	}

}