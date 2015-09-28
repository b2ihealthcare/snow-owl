/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.test.commons;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import org.junit.rules.ExternalResource;

import com.b2international.commons.ConsoleProgressMonitor;
import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;

/**
 * JUnit test rule to import SNOMED CT content during automated tests.
 * 
 * @since 3.6
 */
public class SnomedContentRule extends ExternalResource {

	private File importArchive;
	private String languageRefSetId;
	private ContentSubType contentType;
	
	public SnomedContentRule(String importArchivePath, String languageRefSetId, ContentSubType contentType) {
		this.contentType = checkNotNull(contentType, "contentType");
		this.importArchive = new File(PlatformUtil.toAbsolutePathBundleEntry(SnomedContentRule.class, importArchivePath));
		this.languageRefSetId = checkNotNull(languageRefSetId, "languageRefSetId");
	}

	@Override
	protected void before() throws Throwable {
		new ImportUtil().doImport("info@b2international.com", languageRefSetId, contentType, importArchive, new ConsoleProgressMonitor());
	}
	
}
