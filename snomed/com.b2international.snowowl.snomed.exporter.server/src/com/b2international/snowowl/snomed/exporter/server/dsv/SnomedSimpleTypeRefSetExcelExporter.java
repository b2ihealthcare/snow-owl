/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.exporter.server.dsv;

import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.server.importer.AbstractTerminologyExporter;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.label.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;

/**
 * Exporter class to export simple type reference sets to Excel format where the 
 * referenced component is description or relationship.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public class SnomedSimpleTypeRefSetExcelExporter extends AbstractTerminologyExporter {
	
	private final static String FONT_STYLE = "Sarif";

	private final SnomedReferenceSet refSet;
	private final SnomedEditingContext context;
	private final Workbook workbook;
	
	private final CellStyle DEFAULT_STYLE;
	private final CellStyle BOLD_STYLE;

	public SnomedSimpleTypeRefSetExcelExporter(final String userId, final IBranchPath branchPath, final String refSetId) {
		super(userId, branchPath);
		
		this.refSet = SnomedRequests.prepareGetReferenceSet(refSetId).build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath()).execute(getBus()).getSync();
		this.context = new SnomedEditingContext(branchPath);
		this.workbook = new XSSFWorkbook();
		
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

	private IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

	@Override
	protected String getTerminologyName() {
		switch (refSet.getReferencedComponentType()) {
		case SnomedTerminologyComponentConstants.DESCRIPTION:
			return "description";
		case SnomedTerminologyComponentConstants.RELATIONSHIP:
			return "relationship";
		default: 
			return "Unknown terminology name";
		}
	}

	protected CDOEditingContext getEditingContext() {
		return context;
	}

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

	@Override
	protected void exportTerminologyComponents(final OMMonitor monitor) {

		Async async = null;
		final OMMonitor componentsMonitor = monitor.fork(15);
		
		try {
			
			final String refSetLabel = formatSheetName(new SnomedConceptNameProvider(ApplicationContext.getServiceForClass(IEventBus.class), ApplicationContext.getServiceForClass(LanguageSetting.class)).getComponentLabel(getBranchPath(), refSet.getId()));
			final Sheet sheet = workbook.createSheet(refSetLabel);
			
			async = monitor.forkAsync(70);

			final Collection<SnomedCoreComponent> components = getComponents(refSet);
			
			async.stop();
			
			componentsMonitor.begin(components.size());
			
			switch (refSet.getReferencedComponentType()) {
			case SnomedTerminologyComponentConstants.DESCRIPTION:
				cretaDescriptionHeader(sheet);
				exportDescriptions(components, sheet, componentsMonitor);
				break;
			case SnomedTerminologyComponentConstants.RELATIONSHIP:
				createRelationshipHeader(sheet);
				exportRelationships(components, sheet, componentsMonitor);
				break;
			default:
				throw new IllegalStateException(MessageFormat.format("Invalid referenced component type: ", refSet.getReferencedComponentType()));
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

	private Collection<SnomedCoreComponent> getComponents(SnomedReferenceSet refSet) {
		return SnomedRequests.prepareSearchMember()
				.all()
				.filterByRefSet(refSet.getId())
				.setExpand(getExpand(refSet))
				.setLocales(getLocales())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, context.getBranch())
				.execute(getBus())
				.then(new Function<SnomedReferenceSetMembers, Collection<SnomedCoreComponent>>() {
					@Override
					public Collection<SnomedCoreComponent> apply(SnomedReferenceSetMembers input) {
						final Collection<SnomedCoreComponent> components = newHashSet();
						
						for (SnomedReferenceSetMember member : input) {
							components.add(member.getReferencedComponent());
						}
						
						return components;
					}
				})
				.getSync();
	}

	private List<ExtendedLocale> getLocales() {
		return ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference();
	}
	
	private String getExpand(SnomedReferenceSet refSet) {
		switch (refSet.getReferencedComponentType()) {
		case SnomedTerminologyComponentConstants.DESCRIPTION:
			return "";
		case SnomedTerminologyComponentConstants.RELATIONSHIP:
			return "source(expand(pt())),type(expand(pt())),destination(expand(pt()))";
		default: return "";
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

	private void exportDescriptions(final Collection<SnomedCoreComponent> descriptions, final Sheet sheet, final OMMonitor monitor) {
		
		int rowNum = 1;
		
		for (final SnomedCoreComponent component : descriptions) {
			final SnomedDescription description = (SnomedDescription) component;
			
			final Row row = sheet.createRow(rowNum);
			
			// TODO all acceptability values should be printed out
			final Acceptability acceptability = description.getAcceptabilityMap().get(Concepts.REFSET_LANGUAGE_TYPE_UK);
			
			if (null == acceptability) {
				monitor.worked(1);
				continue;
			}
			
			createCell(row, description.getId(), 0, DEFAULT_STYLE);
			createCell(row, description.getTerm(), 1, DEFAULT_STYLE);
			createCell(row, acceptability.getConceptId(), 2, DEFAULT_STYLE);
			createCell(row, description.getConceptId(), 3, DEFAULT_STYLE);
			// TODO support description concept label again
//			createCell(row, getPtOrId(description.getConceptId()), 4, DEFAULT_STYLE);
			createCell(row, description.isActive() ? "Active" : "Inactive", 5, DEFAULT_STYLE);
			createCell(row, getExportedEffectiveTime(description.getEffectiveTime()), 6, DEFAULT_STYLE);
			createCell(row, description.getModuleId(), 7, DEFAULT_STYLE);

			// TODO support module labels again
//			createCell(row, getPrefferedTermByConcept(description.getModule()), 8, DEFAULT_STYLE);
			
			rowNum++;
			monitor.worked(1);
		}
	}
	
	private void exportRelationships(final Collection<SnomedCoreComponent> relationships, final Sheet sheet, final OMMonitor monitor) {
		
		int rowNum = 1;
		
		for (final SnomedCoreComponent component : relationships) {
			final SnomedRelationship relationship = (SnomedRelationship) component;
			
			final Row row = sheet.createRow(rowNum);
			
			createCell(row, relationship.getId(), 0, DEFAULT_STYLE);
			createCell(row, relationship.getSource().getId(), 1, DEFAULT_STYLE);
			createCell(row, getPtOrId(relationship.getSource()), 2, DEFAULT_STYLE);
			createCell(row, relationship.getType().getId(), 3, DEFAULT_STYLE);
			createCell(row, getPtOrId(relationship.getType()), 4, DEFAULT_STYLE);
			createCell(row, relationship.getDestination().getId(), 5, DEFAULT_STYLE);
			createCell(row, getPtOrId(relationship.getDestination()), 6, DEFAULT_STYLE);
			createCell(row, relationship.isActive() ? "Active" : "Inactive", 7, DEFAULT_STYLE);
			createCell(row, getExportedEffectiveTime(relationship.getEffectiveTime()), 8, DEFAULT_STYLE);
			createCell(row, relationship.getModuleId(), 9, DEFAULT_STYLE);
			// TODO support module labels again
//			createCell(row, getPrefferedTermByConcept(relationship.getModule()), 10, DEFAULT_STYLE);
			
			rowNum++;
			monitor.worked(1);
		}
	}
	
	private String getPtOrId(SnomedConcept concept) {
		return concept.getPt() == null ? concept.getId() : concept.getPt().getTerm();
	}

	private void createCell(final Row row, final String value, final int columnNumber, final CellStyle style) {
		final Cell cell = row.createCell(columnNumber);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}
	
	private int getColumnNumber() {
		switch (refSet.getReferencedComponentType()) {
		case SnomedTerminologyComponentConstants.DESCRIPTION:
			return 9;
		case SnomedTerminologyComponentConstants.RELATIONSHIP:
			return 11;
		default: 
			return 0;
		}
	}

	private String formatSheetName(final String refSetLabel) {
		if (refSetLabel.length() < 32) {
			return refSetLabel;
		} else {
			return refSetLabel.substring(0, 31);
		}
	}

	@Override
	protected String getFileExtension() {
		return ".xlsx";
	}

}