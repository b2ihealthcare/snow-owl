/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.test.commons;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import org.junit.rules.ExternalResource;

import com.b2international.commons.ConsoleProgressMonitor;
import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.api.impl.codesystem.domain.CodeSystem;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;

/**
 * JUnit test rule to import SNOMED CT content during automated tests.
 * 
 * @since 3.6
 */
public class SnomedContentRule extends ExternalResource {

	private final String branchPath;
	private final CodeSystem codeSystem;
	private final File importArchive;
	private final ContentSubType contentType;

	public SnomedContentRule(final CodeSystem codeSystem, final String importArchivePath, final ContentSubType contentType) {
		this(IBranchPath.MAIN_BRANCH, codeSystem, importArchivePath, contentType);
	}
	
	public SnomedContentRule(final String branchPath, final CodeSystem codeSystem, final String importArchivePath, final ContentSubType contentType) {
		this.branchPath = checkNotNull(branchPath, "branchPath");
		this.codeSystem = checkNotNull(codeSystem, "codeSystem");
		this.contentType = checkNotNull(contentType, "contentType");
		this.importArchive = new File(PlatformUtil.toAbsolutePathBundleEntry(SnomedContentRule.class, importArchivePath));
	}

	@Override
	protected void before() throws Throwable {
		checkBranch();
		new ImportUtil().doImport(codeSystem, "info@b2international.com", contentType, branchPath, importArchive, true,
				new ConsoleProgressMonitor());
	}

	private void checkBranch() {
		if (!IBranchPath.MAIN_BRANCH.equals(branchPath) && !BranchPathUtils.exists("snomedStore", branchPath)) {
			final IBranchPath parentBranchPath = BranchPathUtils.createPath(branchPath.substring(0, branchPath.lastIndexOf('/')));
			final IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
			
			SnomedRequests.branching().prepareCreate()
				.setParent(parentBranchPath.getPath())
				.setName(codeSystem.getShortName())
				.build()
				.executeSync(eventBus);
		}
	}

}
