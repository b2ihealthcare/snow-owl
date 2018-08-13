/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.dsv;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult.Result;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRefSetDSVExportModel;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

/**
 * This class receives requests from client side and depending the user request
 * executes exports correspondingly. The response is a zipped archive containing
 * the export file of the reference set. The zipped archive and the working
 * directory can be found during the export in your system dependent temporary
 * folder. After finishing the export and uploading the zipped file to the
 * client the working directory and the zipped archive are deleted.
 * 
 */
public class SnomedRefSetDSVExportServerIndication extends IndicationWithMonitoring {
	
	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SnomedRefSetDSVExportServerIndication.class);

	private SnomedRefSetDSVExportModel exportSetting;

	private String userId;
	private IBranchPath branchPath;

	public SnomedRefSetDSVExportServerIndication(SignalProtocol<?> protocol) {
		super(protocol, Net4jProtocolConstants.REFSET_TO_DSV_SIGNAL);
		exportSetting = new SnomedRefSetDSVExportModel();
	}

	@Override
	protected int getIndicatingWorkPercent() {
		return 0;
	}

	@Override
	protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception {
		// the file path does not equals to the path given by the user it is for
		// the temporary file on the server side.
		exportSetting.setExportPath(Files.createTempDirectory("dsv-export-temp-dir").toFile().getAbsolutePath());
		userId = in.readUTF();
		exportSetting.setRefSetId(in.readUTF());
		exportSetting.setIncludeDescriptionId(in.readBoolean());
		exportSetting.setIncludeRelationshipTargetId(in.readBoolean());
		exportSetting.setIncludeInactiveMembers(in.readBoolean());
		int exportItemsSize = in.readInt();
		for (int i = 0; i < exportItemsSize; i++) {
			// TODO supplying MAIN here, as the selected branch read later from the stream
			exportSetting.addExportItem(AbstractSnomedDsvExportItem.createFromInputStream(in));
		}
		
		int extendedLocaleSize = in.readInt();
		List<ExtendedLocale> locales = Lists.newArrayList();
		for (int i = 0; i < extendedLocaleSize; i++) {
			locales.add(ExtendedLocale.valueOf(in.readString()));
		}
		exportSetting.setLocales(locales);
		exportSetting.setDelimiter(in.readUTF());
		exportSetting.setBranchBase(in.readLong());
		exportSetting.setBranchPath(in.readUTF());
		branchPath = BranchPathUtils.createPath(exportSetting.getBranchPath());
	}

	@Override
	protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws SnowowlServiceException {
	}
	
}