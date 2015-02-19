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
package org.apache.poi.xssf.eventusermodel.ext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.ext.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Class for processing and processing of a sheet#.xml sheet part of a XSSF .xlsx file.
 *
 */
public class XSSFParser {

	public static void parseExcel(final File file, final XSSFParserCallback callback) throws XSSFParseException {

		if (null == file) {
			throw new NullPointerException("file");
		}
		
		if (null == callback) {
			throw new NullPointerException("callback"); 
		}
		
		OPCPackage container = null;
		
		try {
			
			container = OPCPackage.open(file.getAbsolutePath());
			final ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(container);
			final XSSFReader xssfReader = new XSSFReader(container);
			final StylesTable styles = xssfReader.getStylesTable();
			final XSSFReader.SheetIterator itr = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
			
			while (itr.hasNext()) {
				try (InputStream is = itr.next();) {
					if (callback.getSheetNamesToProcess().contains(itr.getSheetName())) {
						processSheet(styles, strings, is, callback);
					}
				}
			}
			
		} catch (final InvalidFormatException e) {
			throw new XSSFParseException("Invalid format.", e);
		} catch (final SAXException e) {
			throw new XSSFParseException("SAX error.", e);
		} catch (final OpenXML4JException e) {
			throw new XSSFParseException("Error while parsing sheet.", e);
		} catch (final IOException e) {
			throw new XSSFParseException("IO error occurred while parsing sheet.", e);
		} finally {
			if (null != container) {
				try {
					container.close();
				} catch (final IOException e) {
					try {
						container.close();
					} catch (final IOException e1) {
						e.addSuppressed(e1);
					}
					throw new XSSFParseException("Error while closing resources.", e);
				}
			}
 		}

	}
	
	private static void processSheet(final StylesTable styles, final ReadOnlySharedStringsTable strings, 
			final InputStream sheetInputStream, final XSSFParserCallback callback) throws IOException, SAXException, XSSFParseException {

		final InputSource sheetSource = new InputSource(sheetInputStream);
		final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		
		try {
			
			final SAXParser saxParser = saxFactory.newSAXParser();
			final XMLReader sheetParser = saxParser.getXMLReader();
			
			final AtomicReference<List<String>> values = new AtomicReference<List<String>>(new ArrayList<String>());
			
			final ContentHandler handler = new XSSFSheetXMLHandler(styles, strings, new SheetContentsHandler() {
				
				private int rowCounter = 0;
				private int coumnCounter = 0;
				
				@Override
				public void startRow(final int rowNum) {
					rowCounter = rowNum;
					coumnCounter = 0;
					values.set(new ArrayList<String>());
				}
				
				@Override
				public void headerFooter(final String text, final boolean isHeader, final String tagName) {
					//ignored
				}
				
				@Override
				public void endRow() {
					callback.handleNewLine(rowCounter, values.get());
				}
				
				@Override
				public void cell(final String cellReference, final String formattedValue) {
					if (coumnCounter++ <= callback.getRelevanCoumnCount()) {
						values.get().add(formattedValue);
					}
				}
			}, false);
			
			sheetParser.setContentHandler(handler);
			sheetParser.parse(sheetSource);
			
		} catch (final ParserConfigurationException e) {
			throw new XSSFParseException("SAX parser appears to be broken - " + e.getMessage(), e);
		}
	}
	
	private XSSFParser() {
		//suppress instantiation.
	}
	
}