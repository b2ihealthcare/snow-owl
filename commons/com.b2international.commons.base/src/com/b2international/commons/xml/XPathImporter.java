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
package com.b2international.commons.xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Imports an XML document portion based on an XPath expression.
 *
 */
public class XPathImporter {

	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private NameSpaceContextMap nsContext;	
	private XPath xPath;
	
	private Map<String, XPathExpression> expressionCache = new HashMap<String, XPathExpression>();
	
	public XPathImporter() throws ParserConfigurationException {
		
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilder = documentBuilderFactory.newDocumentBuilder();

		nsContext = new NameSpaceContextMap();
		
		XPathFactory factory = XPathFactory.newInstance();
		xPath = factory.newXPath();
		xPath.setNamespaceContext(nsContext);
	}
	
	public Document readDocument(File file) throws SAXException, IOException {
		return documentBuilder.parse(file);
	}
	
	public NameSpaceContextMap getNsContext() {
		return nsContext;
	}
	
	public Node getNode(Node node, String xpath) throws XPathExpressionException {
		XPathExpression expression = xPath.compile(xpath);
		Node subNode = (Node) expression.evaluate(node, XPathConstants.NODE);
		return subNode;
	}
	
	public NodeList getNodeList(Node node, String xpath) throws XPathExpressionException {
		XPathExpression expression = expressionCache.get(xpath);
		if(expression == null) {
			expression = xPath.compile(xpath);
			expressionCache.put(xpath, expression);
		}
		NodeList nodeList = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
		return nodeList;
	}
	
	public NodeList getChildElements(Element element, String namespace, String name) {
		return element.getElementsByTagNameNS(namespace, name);
	}
	
	public Element getChildElement(Element element, String namespace, String name) {
		NodeList childElements = getChildElements(element, namespace, name);
		if(childElements == null || childElements.getLength() == 0) {
			return null;
		}
		return (Element) childElements.item(0);
	}
	
	public String getAttributeValue(Node node, String namespace, String name) {
		if(node == null) {
			return null;
		}
		
		Node attribute = node.getAttributes().getNamedItemNS(namespace, name);
		if(attribute == null) {
			return null;
		}
		return attribute.getTextContent();
	}
	
	public String getTextConcent(Node node) {
		return node == null ? null : node.getTextContent();
	}
}