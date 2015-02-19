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
package com.b2international.snowowl.datastore.version;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Iterator;

import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.NodeDiffImpl;
import com.google.common.base.Predicate;

/**
 * Default {@link NodeDiffFilter} implementation.
 *
 */
public class DefaultNodeDiffFilter implements NodeDiffFilter {

	public static final NodeDiffFilter DEFAULT = new DefaultNodeDiffFilter();
	
	@Override
	public <T extends NodeDiff> Iterable<T> filter(final Iterable<T> unfiltered, final Predicate<T> predicate) {
		checkNotNull(unfiltered, "unfiltered");
		checkNotNull(predicate, "predicate");
		
		for (final Iterator<T> itr = unfiltered.iterator(); itr.hasNext(); /**/) {
			
			final T diff = itr.next();
			if (predicate.apply(diff)) {
				if (diff instanceof NodeDiffImpl) {
					final NodeDiffImpl diffImpl = (NodeDiffImpl) diff;
					final NodeDiffImpl parenDiff = (NodeDiffImpl) diff.getParent();
					final Collection<NodeDiff> diffChildren = diffImpl.getChildren();
					parenDiff.removeChild(diffImpl);
					parenDiff.addChildren(diffChildren);
					
					for (final NodeDiff child : diff.getChildren()) {
						((NodeDiffImpl) child).setParent(parenDiff);
					}
					itr.remove();
				}
			}
			
		}
		
		return newArrayList(postProcess(unfiltered));
	}
	
	/**
	 * Performs any additional post processing on the given iterable of {@link NodeDiff}s.
	 * <p>Default implementation does nothing but returns with the given argument. Clients
	 * may override to add any arbitrary behavior. 
	 * @param nodeDiffs an iterable of the node differences to post process.
	 * @return an iterable of node differences after the post processing.
	 */
	protected <T extends NodeDiff> Iterable<T> postProcess(final Iterable<T> nodeDiffs) {
		return checkNotNull(nodeDiffs, "nodeDiffs");
	}
	
	private DefaultNodeDiffFilter() { /*private*/ }

}