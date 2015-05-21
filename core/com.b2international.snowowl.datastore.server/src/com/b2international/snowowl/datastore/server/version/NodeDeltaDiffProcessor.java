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
import static com.b2international.snowowl.core.ApplicationContext.handleErrorStatus;
import static com.b2international.snowowl.datastore.index.diff.NodeDelta.NULL_IMPL;
import static com.b2international.snowowl.datastore.server.DatastoreServerActivator.PLUGIN_ID;
import static com.b2international.snowowl.datastore.server.DatastoreServerActivator.getContext;
import static com.b2international.snowowl.emf.compare.diff.processor.StringDiffProcessor.STRING_PROCESSOR;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptySet;
import static org.eclipse.core.runtime.IStatus.WARNING;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;

import com.b2international.commons.emf.NsUriProvider;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.index.diff.NodeDelta;
import com.b2international.snowowl.emf.compare.diff.AttributeDiff;
import com.b2international.snowowl.emf.compare.diff.Diff;
import com.b2international.snowowl.emf.compare.diff.ReferenceDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueAttributeDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueReferenceDiff;
import com.b2international.snowowl.emf.compare.diff.processor.DiffProcessor;

/**
 * Abstract {@link DiffProcessor} implementation. Produces {@link NodeDelta}s from the processed 
 * {@link Diff differences}. Also responsible for gracefully handling and logging the unexpected changes.
 *
 */
public abstract class NodeDeltaDiffProcessor extends DiffTransformer implements DiffProcessor<NodeDelta> {

	private static final Logger LOGGER = getLogger(NodeDeltaDiffProcessor.class);

	@Override
	public NodeDelta processSingleValueAttributeChange(final SingleValueAttributeDiff diff) {
		return handleUnexpectedDiff(checkNotNull(diff, "diff"));
	}

	@Override
	public NodeDelta processManyValueAttributeChange(final AttributeDiff diff) {
		return handleUnexpectedDiff(checkNotNull(diff, "diff"));
	}

	@Override
	public NodeDelta processSingleValueReferenceChange(final SingleValueReferenceDiff diff) {
		return handleUnexpectedDiff(checkNotNull(diff, "diff"));
	}

	@Override
	public NodeDelta processManyValueReferenceChange(final ReferenceDiff diff) {
		return handleUnexpectedDiff(checkNotNull(diff, "diff"));
	}
	
	@Override
	public Collection<EStructuralFeature> getExcludedFeatures() {
		return emptySet();
	}
	
	@Override
	public String toString(final Notifier notifier) {
		return String.valueOf(notifier);
	}
	
	@Override
	public NsUriProvider getNsUriProvider() {
		return getRepositoryManager().getNsUriProvider(getRepositoryUuid());
	}

	/**Returns with the UUID of the associated repository.*/
	protected abstract String getRepositoryUuid();

	/**Handles unexpected {@link Diff} changes.*/
	protected NodeDelta handleUnexpectedDiff(final Diff<?, ?> diff) {
		
		final String diffMessage;
		final String message;
		
		if (diff instanceof SingleValueAttributeDiff) {
			message = "Unexpected single value attribute change.";
			diffMessage = STRING_PROCESSOR.processSingleValueAttributeChange((SingleValueAttributeDiff) diff);
		} else if (diff instanceof AttributeDiff) {
			message = "Unexpected many value attribute change.";
			diffMessage = STRING_PROCESSOR.processManyValueAttributeChange((AttributeDiff) diff);
		} else if (diff instanceof SingleValueReferenceDiff) {
			message = "Unexpected single value reference change.";
			diffMessage = STRING_PROCESSOR.processSingleValueReferenceChange((SingleValueReferenceDiff) diff);
		} else if (diff instanceof ReferenceDiff) {
			message = "Unexpected many value reference change.";
			diffMessage = STRING_PROCESSOR.processManyValueReferenceChange((ReferenceDiff) diff);
		} else {
			message = "Unexpected difference.";
			diffMessage = diff.toString();
		}
		
		logWarning(message, diffMessage);
		
		return NULL_IMPL;
	}

	private void logWarning(final String message, final String diffMessage) {
		LOGGER.warn(message);
		LOGGER.warn(diffMessage);
		final Bundle bundle = getContext().getBundle();
		final StringBuilder sb = new StringBuilder(message)
			.append("\n")
			.append(diffMessage);
		handleErrorStatus(bundle, new Status(WARNING, PLUGIN_ID, sb.toString()));
	}
	
	private ICDORepositoryManager getRepositoryManager() {
		return getServiceForClass(ICDORepositoryManager.class);
	}
	
}