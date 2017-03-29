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
package com.b2international.snowowl.snomed.exporter.server.net4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

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
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRefSetDSVExportModel;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult.Result;
import com.b2international.snowowl.snomed.exporter.server.dsv.IRefSetDSVExporter;
import com.b2international.snowowl.snomed.exporter.server.dsv.MapTypeRefSetDSVExporter;
import com.b2international.snowowl.snomed.exporter.server.dsv.SnomedSimpleTypeRefSetDSVExporter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

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
		exportSetting.setExportPath(System.getProperty("java.io.tmpdir") + File.separatorChar + "DSV_export" + System.currentTimeMillis());
		userId = in.readUTF();
		exportSetting.setRefSetId(in.readUTF());
		exportSetting.setDescriptionIdExpected(in.readBoolean());
		exportSetting.setRelationshipTargetExpected(in.readBoolean());
		int exportItemsSize = in.readInt();
		for (int i = 0; i < exportItemsSize; i++) {
			// TODO supplying MAIN here, as the selected branch read later from the stream
			exportSetting.addExportItem(AbstractSnomedDsvExportItem.createFromInputStream(in));
		}
		exportSetting.setLanguageConfigurationId(in.readLong());
		exportSetting.setDelimiter(in.readUTF());
		exportSetting.setBranchID(in.readInt());
		exportSetting.setBranchBase(in.readLong());
		exportSetting.setBranchPath(in.readUTF());
		
		branchPath = BranchPathUtils.createPath(exportSetting.getBranchPath());
	}

	@Override
	protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws SnowowlServiceException {
		File response = null;
		SnomedExportResult result = new SnomedExportResult();
		IRefSetDSVExporter exporter = getRefSetExporter();
		
		try {
			response = exporter.executeDSVExport(monitor);
		} catch (SnowowlServiceException e) {
			final String reason = null != e.getMessage() ? " Reason: '" + e.getMessage() + "'" : "";
			LogUtils.logExportActivity(LOGGER, userId, branchPath, "Caught exception while exporting SNOMED CT terminology to DSV format." + reason);
			
			LOGGER.error("Error while exporting DSV.", e);
			result.setResultAndMessage(Result.EXCEPTION, "An error occurred while exporting SNOMED CT components to delimiter separated files.");
		}
		sendResult(out, result, response);
		
		if (null != response) {
			response.delete();
		}
	}
	
	private IRefSetDSVExporter getRefSetExporter() {
		IBranchPath branchPath = BranchPathUtils.createPath(exportSetting.getBranchPath());
		
		RepositoryManager repositoryManager = ApplicationContext.getInstance().getService(RepositoryManager.class);
		RevisionIndex revisionIndex = repositoryManager.get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
		
		QueryBuilder<SnomedConceptDocument> builder = Query.select(SnomedConceptDocument.class);

		final Query<SnomedConceptDocument> query = builder.where(SnomedConceptDocument.Expressions.id(exportSetting.getRefSetId())).build();
		
		
		SnomedConceptDocument refsetConcept = revisionIndex.read(branchPath.getPath(), new RevisionIndexRead<SnomedConceptDocument>() {

			@Override
			public SnomedConceptDocument execute(RevisionSearcher searcher) throws IOException {
				
				Hits<SnomedConceptDocument> snomedConceptDocuments = searcher.search(query);
				Optional<SnomedConceptDocument> first = FluentIterable.<SnomedConceptDocument>from(snomedConceptDocuments).first();
				if (first.isPresent()) {
					return first.get();
				} else {
					throw new IllegalArgumentException("Could not find reference set with id: " + exportSetting.getRefSetId());
				}
			}
		});
		
		IRefSetDSVExporter exporter = null;
		if (SnomedRefSetType.SIMPLE.equals(refsetConcept.getRefSetType())) {
			exporter = new SnomedSimpleTypeRefSetDSVExporter(exportSetting);
		} else if (SnomedRefSetUtil.isMapping(refsetConcept.getRefSetType())) {
			exporter = new MapTypeRefSetDSVExporter(exportSetting);
		}
		return exporter;
	}

	private void sendResult(final ExtendedDataOutputStream out, final SnomedExportResult result, final File tempexportfile) throws SnowowlServiceException {
		BufferedInputStream in = null;
		
		try {
			out.writeObject(result);
			
			if (Result.SUCCESSFUL != result.getResult()) {
				return;
			}
			
			long size = tempexportfile.length();
			out.writeLong(size);
			
			in = new BufferedInputStream(new FileInputStream(tempexportfile));
			while (size != 0L) {
				int chunk = Net4jProtocolConstants.BUFFER_SIZE;
				if (size < Net4jProtocolConstants.BUFFER_SIZE) {
					chunk = (int) size;
				}

				byte[] buffer = new byte[chunk];
				in.read(buffer);
				out.writeByteArray(buffer);

				size -= chunk;
			}
		} catch (FileNotFoundException e) {
			throw new SnowowlServiceException(e);
		} catch (IOException e) {
			throw new SnowowlServiceException(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				throw new SnowowlServiceException(e);
			}
		}
	}

}