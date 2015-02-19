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
package com.b2international.snowowl.core.claml;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 *
 */
public class LabelXmlToHtmlConverter {
	
	public String convert(String xml) {
		StringBuffer buffer = new StringBuffer();
		try {
			XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserFactory.newPullParser();
			parser.setInput(new StringReader(xml));
			buffer = parseNextTag(parser, buffer);
		} catch (XmlPullParserException e) {
			throw new RuntimeException("Error while parsing XML.", e);
		} catch (IOException e) {
			throw new RuntimeException("Error while parsing XML.", e);			
		}
		return buffer.toString();
	}
	
	private StringBuffer parseNextTag(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		while (true) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				String tagName = parser.getName();
				if ("Reference".equals(tagName)) {
					parseReference(parser, buffer);
				} else if ("Fragment".equals(tagName)) {
					parseFragment(parser, buffer);
				} else if ("Label".equals(tagName)) {
					parseLabel(parser, buffer);
				} else if ("Para".equals(tagName)) {
					parsePara(parser, buffer);
				} else if ("List".equals(tagName)) {
					parseList(parser, buffer);
				} else if ("ListItem".equals(tagName)) {
					parseListItem(parser, buffer);
				} else if ("Table".equals(tagName)) {
					parseTable(parser, buffer);
				} else if ("THead".equals(tagName)) {
					parseTHead(parser, buffer);
				} else if ("TBody".equals(tagName)) {
					parseTBody(parser, buffer);
				} else if ("TFoot".equals(tagName)) {
					parseTFoot(parser, buffer);
				} else if ("Row".equals(tagName)) {
					parseRow(parser, buffer);
				} else if ("Cell".equals(tagName)) {
					parseCell(parser, buffer);
				} else if ("Term".equals(tagName)) {
					parseTerm(parser, buffer);
				} else {
					throw new IllegalArgumentException("Unexpected element: " + tagName);
				}
			} else if (eventType == XmlPullParser.TEXT) {
				String text = parser.getText();
				buffer.append(text);
			} else if (eventType == XmlPullParser.END_TAG) {
				break;
			} else if (eventType == XmlPullParser.END_DOCUMENT) {
				break;
			}
		}
		return buffer;
	}
	
	private StringBuffer parseLabel(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		return parseNextTag(parser, buffer);
	}
	
	private StringBuffer parseReference(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		String code = parser.nextText();
		return buffer.append(" (<a href=\"icd://" + code + "\">").append(code).append("</a>)");
	}
	
	private StringBuffer parseFragment(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		return parseNextTag(parser, buffer);
	}
	
	private StringBuffer parsePara(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		buffer.append("<p>");
		return parseNextTag(parser, buffer).append("</p>");
	}
	
	private StringBuffer parseList(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		buffer.append("<ul>");
		return parseNextTag(parser, buffer).append("</ul>");
	}
	
	private StringBuffer parseListItem(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		buffer.append("<li>");
		return parseNextTag(parser, buffer).append("</li>");
	}
	
	private StringBuffer parseTable(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		return parseNextTag(parser, buffer);
	}
	
	private StringBuffer parseTHead(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		return parseNextTag(parser, buffer);
	}
	
	private StringBuffer parseTBody(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		return parseNextTag(parser, buffer);
	}
	
	private StringBuffer parseTFoot(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		return parseNextTag(parser, buffer);
	}
	
	private StringBuffer parseRow(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		return parseNextTag(parser, buffer);
	}
	
	private StringBuffer parseCell(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		return parseNextTag(parser, buffer);
	}
	
	private StringBuffer parseTerm(XmlPullParser parser, StringBuffer buffer) throws XmlPullParserException, IOException {
		return buffer;
	}
	
	public static void main(String[] args) {
		LabelXmlToHtmlConverter converter = new LabelXmlToHtmlConverter();
		String result = converter.convert("<Label xml:lang=\"en\" xml:space=\"default\">" +
				"<Para>para0</Para>" +
				"<List>" +
					"<ListItem>" +
						"<Para>para1</Para>" +
					"</ListItem>" +
					"<ListItem>" +
						"<Para>para2_0<Reference>ref</Reference>para2_1</Para>" +
					"</ListItem>" +
				"</List>" +
				"<Para>para3</Para>" +
			"</Label>");
	}
}