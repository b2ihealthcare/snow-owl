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
package com.b2international.snowowl.datastore.index.diff;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.index.diff.NodeChange.NULL_IMPL;
import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.snowowl.datastore.version.NodeTransformerService;

/**
 * Job for transforming a {@link NodeDiff} into a {@link NodeChange} by resolving the changes.
 *
 */
public class NodeDiffTransformationJob extends Job {

	private NodeDiff nodeDiff;
	private NodeChange nodeChange = NULL_IMPL;
	private CompareResult result;

	public NodeDiffTransformationJob(final NodeDiff nodeDiff, final CompareResult result) {
		super(NodeDiffTransformationJob.class.getName());
		this.result = checkNotNull(result, "result");
		this.nodeDiff = checkNotNull(nodeDiff, "nodeDiff");
		setUser(false);
		setSystem(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		nodeChange = doTransform();
		return Status.OK_STATUS;
	}

	/**
	 * Returns with the {@link NodeChange}.
	 * @return the node change.
	 */
	public NodeChange getNodeChange() {
		return nodeChange;
	}
	
	/**
	 * Returns with the {@link NodeDiff node} that has to be transformed into a node change.
	 * @return the node to transform.
	 */ 
	public NodeDiff getNodeDiff() {
		return nodeDiff;
	}
	
	/**
	 * Performs the transformation and returns with the node change.
	 * @return the node change.
	 */
	protected NodeChange doTransform() {
		return getServiceForClass(NodeTransformerService.class).transform(result.getConfiguration(), nodeDiff);
	}
	


}