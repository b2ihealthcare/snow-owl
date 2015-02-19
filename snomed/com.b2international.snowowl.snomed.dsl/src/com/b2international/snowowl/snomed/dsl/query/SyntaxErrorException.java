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
package com.b2international.snowowl.snomed.dsl.query;

import java.util.List;

import org.eclipse.xtext.nodemodel.INode;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class SyntaxErrorException extends RuntimeException {

	private static final long serialVersionUID = -109709128128071291L;
	
	private final List<INode> syntaxErrors;
	
	public SyntaxErrorException(final Iterable<INode> syntaxErrors) {
		super("Syntax error");
		this.syntaxErrors = Lists.newArrayList(syntaxErrors);
	}

	public List<INode> getSyntaxErrors() {
		return syntaxErrors;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return Joiner.on("\n").join(Iterables.transform(syntaxErrors, new Function<INode, String>() {
			@Override public String apply(final INode node) {
				return node.getSyntaxErrorMessage().getMessage();
			}
		}));
	}
}