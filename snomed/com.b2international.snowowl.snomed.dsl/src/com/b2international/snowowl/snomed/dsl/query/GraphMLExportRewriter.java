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
package com.b2international.snowowl.snomed.dsl.query;

import java.io.IOException;
import java.io.Writer;

import com.b2international.snowowl.snomed.dsl.query.ast.AndClause;
import com.b2international.snowowl.snomed.dsl.query.ast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.ast.BinaryRValue;
import com.b2international.snowowl.snomed.dsl.query.ast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.ast.NotClause;
import com.b2international.snowowl.snomed.dsl.query.ast.OrClause;
import com.b2international.snowowl.snomed.dsl.query.ast.RefSet;
import com.b2international.snowowl.snomed.dsl.query.ast.SubExpression;
import com.b2international.snowowl.snomed.dsl.query.ast.UnaryRValue;

public class GraphMLExportRewriter implements QueryRewriter<RValue> {

	Writer writer;
	private int nodeCounter = 0;
	
	public GraphMLExportRewriter(Writer writer) {
		this.writer = writer;
	}

	@Override
	public RValue rewrite(RValue input) {
		
		writeHead();
		
		writeEdge(null, input, nodeCounter);
		
		writeTail();
		
		return input;
	}
	
	protected void writeEdge(RValue from, RValue to, int fromCounter) {
	
		int toCounter = writeNode(to);
		
		if(from != null) {
			writeEdge("node" + fromCounter, "node" + toCounter);
		}
	}

	protected int writeNode(RValue node) {
		
		int myCounter = ++nodeCounter;

		String label = node.getClass().getSimpleName();
		if(node instanceof ConceptRef) {
			String prefix = null;
			ConceptRef conceptRef = (ConceptRef) node;
			switch (conceptRef.getQuantifier()) {
			case SELF: prefix = ""; break;
			case ANY_SUBTYPE: prefix = "&lt;"; break;
			case SELF_AND_ANY_SUBTYPE: prefix = "&lt;&lt;"; break;
			}
			
			label = prefix + conceptRef.getConceptId() + "\n" + conceptRef.getLabel();
		} else if(node instanceof RefSet) {
			label = "^" + ((RefSet) node).getId();
		}
		
		writeNode("node" + myCounter, getColor(node), label);
		
		if(node instanceof UnaryRValue<?>) {
			UnaryRValue<?> unary = (UnaryRValue<?>) node;
			writeEdge(node, unary.getValue(), myCounter);

		} else if(node instanceof BinaryRValue<?, ?>) {
			BinaryRValue<?, ?> binary = (BinaryRValue<?, ?>) node;
			writeEdge(node, binary.getLeft(), myCounter);
			writeEdge(node, binary.getRight(), myCounter);
		}
		
		return myCounter;
	}
	
	protected String getColor(RValue node) {
		if (node instanceof ConceptRef || node instanceof AttributeClause) {
			return "#FFFF99";
		}
		if (node instanceof RefSet) {
			return "#FFCC66";
		}		
		if (node instanceof NotClause) {
			return "#FF6666";
		}		
		if (node instanceof AndClause) {
			return "#00FF66";
		}		
		if (node instanceof OrClause) {
			return "#33CCFF";
		}		
		if (node instanceof SubExpression) {
			return "#996699";
		}		
		return "#DDDDDD"; 
	}

	protected void writeHead() {
		writeln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writeln("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" xmlns:y=\"http://www.yworks.com/xml/graphml\">");
		writeln("<key id=\"label\" for=\"node\" yfiles.type=\"nodegraphics\"/>");
		writeln("<graph id=\"graph\" edgedefault=\"directed\">");
	}
	
	protected void writeNode(String id, String color, String label) {
		writeln(String.format("<node id=\"%s\"><data key=\"label\"><y:ShapeNode><y:Fill color=\"%s\" transparent=\"false\"/><y:Geometry height=\"30.0\" width=\"120.0\"/><y:NodeLabel>%s</y:NodeLabel></y:ShapeNode></data></node>", id, color, label));
	}

	protected void writeEdge(String from, String to) {
		writeln(String.format("<edge source=\"%s\" target=\"%s\" directed=\"true\"/>", from, to));
	}

	protected void writeTail() {
		writeln("</graph>");
		writeln("</graphml>");
		try {
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error while flushing output: ", e);
		}
	}
	
	protected void writeln(String line) {
		try {
			writer.write(line);
			writer.write("\n");
		} catch (IOException e) {
			throw new RuntimeException("Error while writing to output: ", e);
		}
	}
}