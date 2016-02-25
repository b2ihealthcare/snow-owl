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
package com.b2international.snowowl.datastore.server.version;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.getObjectIfExists;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.index.diff.NodeChange;
import com.b2international.snowowl.datastore.index.diff.NodeChangeImpl;
import com.b2international.snowowl.datastore.index.diff.NodeDelta;
import com.b2international.snowowl.datastore.index.diff.NodeDeltaComparator;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.NodeTransformer;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;

/**
 * Basic {@link NodeTransformer} implementation.
 *
 */
public abstract class NodeTransformerImpl extends DiffTransformer implements NodeTransformer {

	protected static final Logger LOGGER = LoggerFactory.getLogger(NodeTransformerImpl.class);
	
	@Override
	public NodeChange transform(final VersionCompareConfiguration configuration, final NodeDiff diff) {
		checkNotNull(configuration, "configuration");
		checkNotNull(diff, "diff");

		final ICDOConnection connection = getConnection(configuration);

		CDOView sourceView = null;
		CDOView targetView = null;

		try {
			final IBranchPath sourceBranchPath = getBranchPath(configuration.isSourcePatched(), configuration.getSourcePath());
			final IBranchPath targetBranchPath = getBranchPath(configuration.isTargetPatched(), configuration.getTargetPath());

			final boolean threeWayUpdate = configuration.isThreeWay() && isUpdate(diff);
			sourceView = connection.createView(threeWayUpdate ? createPath(configuration.getSourcePath()) : sourceBranchPath);
			targetView = connection.createView(targetBranchPath);

			if (!diff.hasChanged()) {
				return createNodeChange(diff, sourceView, targetView, Collections.<NodeDelta> emptyList());
			}

			return trySort(doTransform(sourceView, targetView, diff));
		} finally {
			LifecycleUtil.deactivate(sourceView);
			LifecycleUtil.deactivate(targetView);
		}
	}

	private IBranchPath getBranchPath(final boolean patched, final IBranchPath branchPath) {
		return patched ? branchPath : BranchPathUtils.isMain(branchPath) ? branchPath : BranchPathUtils.convertIntoBasePath(branchPath);
	}

	/**
	 * Transforms the given node diffs into {@link NodeChange} with the given {@link CDOView views}. 
	 * @param sourceView the source view.
	 * @param targetView the target view.
	 * @param diff the node to transform.
	 * @return the transformed {@link NodeChange}.
	 */
	protected abstract NodeChange doTransform(final CDOView sourceView, final CDOView targetView, final NodeDiff diff);

	/**Loads the {@link CDOObject} represented as a {@link NodeDiff} from the {@link CDOView} argument.*/
	protected CDOObject loadObject(final CDOView view, final NodeDiff diff) {
		return getObjectIfExists(checkNotNull(view, "view"), checkNotNull(diff, "diff").getStorageKey());
	}
	

	private NodeChange trySort(final NodeChange change) {
		if (change instanceof NodeChangeImpl) {
			((NodeChangeImpl) change).sort(NodeDeltaComparator.INSTANCE);
		}
		return change;
	}
	
	private ICDOConnection getConnection(final VersionCompareConfiguration configuration) {
		return getServiceForClass(ICDOConnectionManager.class).getByUuid(checkNotNull(configuration, "configuration").getRepositoryUuid());
	}

}