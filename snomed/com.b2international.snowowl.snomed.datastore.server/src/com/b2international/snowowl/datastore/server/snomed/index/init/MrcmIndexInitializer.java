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
package com.b2international.snowowl.datastore.server.snomed.index.init;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.snowowl.core.SimpleFamilyJob;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.snomed.datastore.MrcmEditingContext;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.update.ConstraintUpdater;
import com.b2international.snowowl.snomed.datastore.snor.ConstraintFormIsApplicableForValidationPredicate;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Index initializer for MRCM.
 */
public class MrcmIndexInitializer extends SimpleFamilyJob {
	
	private final Multimap<Long, String> componentIdToConstraints = HashMultimap.create();
	private final SnomedIndexServerService index;
	private final IBranchPath branchPath;

	/**
	 * @param indexService
	 * @param branchPath
	 */
	public MrcmIndexInitializer(final SnomedIndexServerService indexService, final IBranchPath branchPath) {
		super("Processing MRCM", new Object());
		this.index = indexService;
		this.branchPath = branchPath;
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) {
		final SubMonitor subMonitor = SubMonitor.convert(monitor);
		subMonitor.subTask(getName());
		try (MrcmEditingContext context = new MrcmEditingContext()) {
			ConceptModel conceptModel = context.getConceptModel();
			final Iterable<AttributeConstraint> constraints = FluentIterable.from(conceptModel.getConstraints())
					.filter(new ConstraintFormIsApplicableForValidationPredicate()).filter(AttributeConstraint.class);
			for (final AttributeConstraint constraint : constraints) {
				final long storageKey = CDOIDUtil.getLong(constraint.cdoID());
				for (final ConstraintDomain constraintDomain : PredicateUtils.processConstraintDomain(storageKey, constraint.getDomain())) {
					componentIdToConstraints.put(constraintDomain.getComponentId(), constraintDomain.getPredicateKey());
				}
				index.update(branchPath, storageKey, new ConstraintUpdater(constraint), new SnomedDocumentBuilder.Factory());
			}
		}
		return Status.OK_STATUS;
	}

	public Multimap<Long, String> getComponentIdToPredicateMap() {
		return componentIdToConstraints;
	}

}