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
package com.b2international.snowowl.snomed.exporter.server.refset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.server.importer.AbstractTerminologyExporter;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import bak.pcj.set.LongSet;

/**
 * Exporter class to export simple type reference sets to Excel format where the 
 * referenced component is description or relationship.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public class SnomedSimpleTypeRefSetExcelExporter extends AbstractTerminologyExporter {
	
	private final static String FONT_STYLE = "Sarif";

	private final String refSetId;
	private final short referencedComponentType;
	private final SnomedEditingContext context;
	private final Workbook workbook;
	private final ISnomedComponentService service;
	private final SnomedRefSetBrowser browser;
	private final SnomedConceptLookupService lookupService;
	private final SnomedRefSetMembershipLookupService memberLookupService;
	
	private final CellStyle DEFAULT_STYLE;
	private final CellStyle BOLD_STYLE;

	public SnomedSimpleTypeRefSetExcelExporter(final String userId, final IBranchPath branchPath, final String refSetId, final short referencedComponentType) {
		super(userId, branchPath);
		
		this.refSetId = refSetId;
		this.referencedComponentType = referencedComponentType;
		this.context = new SnomedEditingContext(branchPath);
		this.workbook = new XSSFWorkbook();
		this.service = ApplicationContext.getInstance().getService(ISnomedComponentService.class);
		this.browser = ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class);
		this.lookupService = new SnomedConceptLookupService();
		this.memberLookupService = new SnomedRefSetMembershipLookupService();
		
		final Font headerFont = workbook.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setFontName(FONT_STYLE);
		
		final Font defaultFont = workbook.createFont();
		defaultFont.setFontName(FONT_STYLE);
		
		BOLD_STYLE = workbook.createCellStyle();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		BOLD_STYLE.setAlignment(CellStyle.ALIGN_LEFT);
		BOLD_STYLE.setFont(headerFont);
		
		DEFAULT_STYLE = workbook.createCellStyle();
		DEFAULT_STYLE.setFont(defaultFont);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.importer.AbstractTerminologyExporter#getTerminologyName()
	 */
	@Override
	protected String getTerminologyName() {
		switch (referencedComponentType) {
		case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
			return "description";
		case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
			return "relationship";
		default: 
			return "Unknown terminology name";
		}
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.importer.AbstractTerminologyExporter#getEditingContext()
	 */
	@Override
	protected CDOEditingContext getEditingContext() {
		return context;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.importer.AbstractTerminologyExporter#exportTerminology(java.lang.String)
	 */
	@Override
	protected File exportTerminology(final String exportFilePath, OMMonitor monitor) throws IOException {
		
		File file;
		FileOutputStream outputStream = null;
		
		try {
			
			logExportActivity(MessageFormat.format("Exporting {0}s to Excel started. Server-side file: {1}", getTerminologyName(), exportFilePath));
			
			file = new File(exportFilePath);
			outputStream = new FileOutputStream(file);
			
			exportTerminologyComponents(monitor);
			
			workbook.write(outputStream);
			
			logExportActivity(MessageFormat.format("Finished exporting {0}s to Excel.", getTerminologyName()));
			
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != getEditingContext()) {
				getEditingContext().close();
			}
			
			if (null != outputStream) {
				outputStream.close();
			}
		}
		
		return file;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.importer.AbstractTerminologyExporter#exportTerminologyComponents()
	 */
	@Override
	protected void exportTerminologyComponents(final OMMonitor monitor) {

		Async async = null;
		OMMonitor componentsMonitor = null;
		
		try {
			
			final String refSetLabel = formatSheetName(ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(getBranchPath(), refSetId));
			final Sheet sheet = workbook.createSheet(refSetLabel);
			
			async = monitor.forkAsync(70);
			
			final LongSet componentStorageKeys = service.getComponentByRefSetIdAndReferencedComponent(getBranchPath(), refSetId, referencedComponentType);
			final List<CDOID> cdoIds = CDOIDUtils.getIds(componentStorageKeys);
			
			final Set<CDOObject> components = Sets.newHashSet();
			
			for (final CDOID cdoid : cdoIds) {
				components.add(context.getTransaction().getObject(cdoid));
			}
			
			async.stop();
			componentsMonitor = monitor.fork(15);
			componentsMonitor.begin(browser.getMemberCount(getBranchPath(), refSetId));
			
			if (referencedComponentType == SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER) {
				cretaDescriptionHeader(sheet);
				exportDescriptions(components, sheet, componentsMonitor);
			} else if (referencedComponentType == SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER) {
				createRelationshipHeader(sheet);
				exportRelationships(components, sheet, componentsMonitor);
			} else {
				throw new IllegalStateException(MessageFormat.format("Invalid referenced component type: ", referencedComponentType));
			}
			
			for (int i = 0; i <= getColumnNumber(); i++) {
				sheet.autoSizeColumn(i);
			}
		} finally {
			if (null != async) {
				async.stop();
			}
			
			if (null != componentsMonitor) {
				componentsMonitor.done();
			}
		}
	}

	private void cretaDescriptionHeader(final Sheet sheet) {
		final Row row = sheet.createRow(0);
		
		createCell(row, "Description ID", 0, BOLD_STYLE);
		createCell(row, "Description Term", 1, BOLD_STYLE);
		createCell(row, "Acceptability", 2, BOLD_STYLE);
		createCell(row, "Concept ID", 3, BOLD_STYLE);
		createCell(row, "Concept Preferred Term", 4, BOLD_STYLE);
		createCell(row, "Status", 5, BOLD_STYLE);
		createCell(row, "Effective Time", 6, BOLD_STYLE);
		createCell(row, "Module ID", 7, BOLD_STYLE);
		createCell(row, "Module Preferred Term", 8, BOLD_STYLE);
	}
	
	private void createRelationshipHeader(final Sheet sheet) {
		final Row row = sheet.createRow(0);
		
		createCell(row, "Relationship ID", 0, BOLD_STYLE);
		createCell(row, "Source Concept ID", 1, BOLD_STYLE);
		createCell(row, "Source Concept Preferred Term", 2, BOLD_STYLE);
		createCell(row, "Relationship Type ID", 3, BOLD_STYLE);
		createCell(row, "Relationship Type Preferred Term", 4, BOLD_STYLE);
		createCell(row, "Destination Concept ID", 5, BOLD_STYLE);
		createCell(row, "Destination Concept Preferred Term", 6, BOLD_STYLE);
		createCell(row, "Status", 7, BOLD_STYLE);
		createCell(row, "Effective Time", 8, BOLD_STYLE);
		createCell(row, "Module ID", 9, BOLD_STYLE);
		createCell(row, "Module Preferred Term", 10, BOLD_STYLE);
	}

	private void exportDescriptions(final Set<CDOObject> components, final Sheet sheet, final OMMonitor monitor) {
		
		int rowNum = 1;
		
		for (final CDOObject cdoObject : components) {
			final Description description = (Description) cdoObject;
			
			final Row row = sheet.createRow(rowNum);
			
			final String acceptabilityId = getAcceptablilityId(description);
			
			if (null == acceptabilityId) {
				monitor.worked(1);
				continue;
			}
			
			createCell(row, description.getId(), 0, DEFAULT_STYLE);
			createCell(row, description.getTerm(), 1, DEFAULT_STYLE);
			createCell(row, acceptabilityId, 2, DEFAULT_STYLE);
			createCell(row, description.getConcept().getId(), 3, DEFAULT_STYLE);
			createCell(row, getPrefferedTermByConcept(description.getConcept()), 4, DEFAULT_STYLE);
			createCell(row, description.isActive() ? "Active" : "Inactive", 5, DEFAULT_STYLE);
			createCell(row, getExportedEffectiveTime(description.getEffectiveTime()), 6, DEFAULT_STYLE);
			createCell(row, description.getModule().getId(), 7, DEFAULT_STYLE);
			createCell(row, getPrefferedTermByConcept(description.getModule()), 8, DEFAULT_STYLE);
			
			rowNum++;
			monitor.worked(1);
		}
	}
	
	private void exportRelationships(final Set<CDOObject> components, final Sheet sheet, final OMMonitor monitor) {
		
		int rowNum = 1;
		
		for (final CDOObject cdoObject : components) {
			final Relationship relationship = (Relationship) cdoObject;
			
			final Row row = sheet.createRow(rowNum);
			
			createCell(row, relationship.getId(), 0, DEFAULT_STYLE);
			createCell(row, relationship.getSource().getId(), 1, DEFAULT_STYLE);
			createCell(row, getPrefferedTermByConcept(relationship.getSource()), 2, DEFAULT_STYLE);
			createCell(row, relationship.getType().getId(), 3, DEFAULT_STYLE);
			createCell(row, getPrefferedTermByConcept(relationship.getType()), 4, DEFAULT_STYLE);
			createCell(row, relationship.getDestination().getId(), 5, DEFAULT_STYLE);
			createCell(row, getPrefferedTermByConcept(relationship.getDestination()), 6, DEFAULT_STYLE);
			createCell(row, relationship.isActive() ? "Active" : "Inactive", 7, DEFAULT_STYLE);
			createCell(row, getExportedEffectiveTime(relationship.getEffectiveTime()), 8, DEFAULT_STYLE);
			createCell(row, relationship.getModule().getId(), 9, DEFAULT_STYLE);
			createCell(row, getPrefferedTermByConcept(relationship.getModule()), 10, DEFAULT_STYLE);
			
			rowNum++;
			monitor.worked(1);
		}
	}
	
	private void createCell(final Row row, final String value, final int columnNumber, final CellStyle style) {
		final Cell cell = row.createCell(columnNumber);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}
	
	private int getColumnNumber() {
		switch (referencedComponentType) {
		case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
			return 9;
		case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
			return 11;
		default: 
			return 0;
		}
	}

	private String getPrefferedTermByConcept(final Concept concept) {
		return ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(getBranchPath(), concept.getId());
	}

	private String getAcceptablilityId(final Description description) {
		final Collection<SnomedRefSetMemberIndexEntry> membersForType = memberLookupService.getMembersForType(SnomedTerminologyComponentConstants.DESCRIPTION, 
				Sets.newHashSet(SnomedRefSetType.LANGUAGE),	Sets.newHashSet(description.getId()));
		
		if (membersForType.isEmpty()) {
			return null;
		} else {
			final SnomedRefSetMemberIndexEntry entry = Lists.newArrayList(membersForType).get(0);
			return entry.getAcceptabilityId();
		}
	}
	
	private String formatSheetName(final String refSetLabel) {
		if (refSetLabel.length() < 32) {
			return refSetLabel;
		} else {
			return refSetLabel.substring(0, 31);
		}
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.importer.AbstractTerminologyExporter#getFileExtension()
	 */
	@Override
	protected String getFileExtension() {
		return ".xlsx";
	}

}