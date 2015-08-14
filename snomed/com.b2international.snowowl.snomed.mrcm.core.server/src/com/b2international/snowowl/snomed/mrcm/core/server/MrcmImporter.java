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
package com.b2international.snowowl.snomed.mrcm.core.server;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.AlphaNumericComparator;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.cdo.CDOTransactionAggregator;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDOTransactionAggregator;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.server.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.index.IndexServerServiceManager;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.MrcmEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConstraintBase;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * MRCM importer singleton.
 *
 */
public enum MrcmImporter {

	INSTANCE;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MrcmImporter.class);
	
	/**
	 * Imports the given MRCM file impersonating a user given with the user name argument.
	 * If the {@code importForAllVersions} is set to {@code true} then the importer will import the
	 * MRCM rules to all available version branches. If {@code false} the content will be imported only 
	 * onto the MAIN branch.
	 * @param userName the user name.
	 * @param mrcmFile the MRCM file to process and import.
	 */
	public void doImport(final String userName, final File mrcmFile, final boolean importForAllVersions, @Nullable ICDOTransactionAggregator aggregator) {
		
		if (!mrcmFile.exists() || !mrcmFile.isFile()) {
			LOGGER.warn("MRCM import file cannot be found. MRCM import is aborting.");
			return;
		}
		
		if (!mrcmFile.canRead()) {
			LOGGER.warn("Cannot read MRCM import file content. MRCM import is aborting.");
			return;
		}
		
		LogUtils.logImportActivity(LOGGER, userName, BranchPathUtils.createMainPath(), "Importing MRCM rules...");
		
		final List<IBranchPath> paths = Lists.newArrayList();

		if (importForAllVersions) {
			//convert version to branch paths
			final Collection<ICodeSystemVersion> versions = getServiceForClass(CodeSystemService.class).getAllTags(SnomedPackage.eINSTANCE);
			paths.addAll(Sets.newHashSet(Iterables.transform(versions, new Function<ICodeSystemVersion, IBranchPath>() {
				@Override public IBranchPath apply(final ICodeSystemVersion version) {
					return BranchPathUtils.createPath(BranchPathUtils.createMainPath(), version.getVersionId());
				}
			})));
			
			final AlphaNumericComparator delegate = new AlphaNumericComparator();
			Collections.sort(paths, new Comparator<IBranchPath>() {
				@Override public int compare(final IBranchPath o1, final IBranchPath o2) {
					return delegate.compare(o1.getPath(), o2.getPath());
				}
			});
		}
		
		final Collection<CDOTransaction> transactions = newHashSet();
		paths.add(BranchPathUtils.createMainPath());
		
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final String repositoryUuid = connectionManager.get(SnomedPackage.eINSTANCE).getUuid();
		
		for (int i = 0; i < paths.size(); i++) {

			final IBranchPath path = paths.get(i);
			
			//prepare index server service here instead of change processor
			//since both transaction commit context and index service initialization runs SQL query, that could end up in deadlock.
			IndexServerServiceManager.INSTANCE.getByUuid(repositoryUuid).prepare(path);
			
			final String version = BranchPathUtils.isMain(path) ? "'HEAD'" : ("'" + path.lastSegment() + "' version");
			final String progressMessage = "Processing MRCM rules to " + version + "... [" + (((i + 1) *100) / paths.size()) + "%]"; 
			LogUtils.logImportActivity(LOGGER, userName, path, progressMessage);
			
			MrcmEditingContext context = null;
			
				try {
				
					context = new MrcmEditingContext(path);
					
					if (null == aggregator) {
						aggregator = CDOTransactionAggregator.create(context.getTransaction());	
					} else {
						transactions.addAll(Lists.newArrayList(((CDOTransactionAggregator) aggregator).iterator()));
						for (final CDOTransaction transaction : transactions) {
							((CDOTransactionAggregator) aggregator).remove(transaction);
						}
						aggregator.add(context.getTransaction());
					}
					
					
					final URI uri = URI.createFileURI(mrcmFile.getAbsolutePath());
					
					final ResourceSet resourceSet = new ResourceSetImpl();
					resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
					final Resource resource = resourceSet.createResource(uri);
					ConceptModel model = null;
					resource.load(null);
					model = (ConceptModel) resource.getContents().get(0);
					final Collection<ConstraintBase> invalidConstraints = new ConceptModelSemanticValidator(new SnomedConceptLookupService()).validate(path, model);
					
					context.clearContents();
		
					if (!importForAllVersions) {
						new CDOServerCommitBuilder(userName, "Imported MRCM rules.", aggregator)
						.sendCommitNotification(false)
						.parentContextDescription(DatastoreLockContextDescriptions.IMPORT)
						.commit();
					} else {
						CDOServerUtils.commit(aggregator, userName, "Imported MRCM rules.", true, null);
					}
					
					
					model.getConstraints().removeAll(invalidConstraints);
					
					context.add(model);
		
					if (!importForAllVersions) {
						new CDOServerCommitBuilder(userName, "Imported MRCM rules.", aggregator)
						.sendCommitNotification(false)
						.parentContextDescription(DatastoreLockContextDescriptions.IMPORT)
						.commit();
					} else {
						CDOServerUtils.commit(aggregator, userName, "Imported MRCM rules.", true, null);
					}

					LogUtils.logImportActivity(LOGGER, userName, path, "MRCM rule import to " + version + " successfully finished.");
					
				} catch (final Throwable t) {
					LogUtils.logImportActivity(LOGGER, userName, path, "Failed to import MRCM rules to " + version + ".");
					throw new SnowowlRuntimeException(t);
				} finally {
					
					if (null != context) {
						context.close();
					}
					
					if (null != aggregator) {
						((CDOTransactionAggregator) aggregator).dispose();
						for (final CDOTransaction transaction : transactions) {
							((CDOTransactionAggregator) aggregator).add(transaction);
						}
					}
					
					
				}
			
		}
		
	}
	
}