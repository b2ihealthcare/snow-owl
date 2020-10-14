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
package com.b2international.snowowl.core.emf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;

import com.b2international.commons.tree.TreeVisitor;
import com.b2international.commons.tree.TreeWalker;

/**
 *  A TreeWalker that can walk an EObject tree, traversing it's structural features. Only contained features and attributes are traversed,
 *  this guaranteeing a tree.
 */
public final class EObjectWalker extends TreeWalker<EObjectTreeNode> {

	public static EObjectWalker createContainmentWalker(TreeVisitor<EObjectTreeNode> visitor) {
		return new EObjectWalker(visitor, new EObjectContainmentTreeNodeProvider());
	}
	
	public static EObjectWalker createUnfilteredWalker(TreeVisitor<EObjectTreeNode> visitor) {
		return new EObjectWalker(visitor, new EObjectTreeNodeProvider());
	}
	
	/**
	 * Constructs an EObjectWalker with the specified visitor
	 * @param visitor
	 * @param childProvider 
	 */
	public EObjectWalker(TreeVisitor<EObjectTreeNode> visitor, ChildProvider<EObjectTreeNode> childProvider) {
		super(visitor, childProvider);
	}

	/**
	 *  Utility class that provides the "child" {@link EObjectTreeNode}s for recursive validation.
	 *  For each EObject value in the hierarchy a node is created that contains that EObject value,
	 *  and additionally a node is created for each feature of that value. Null or Non-EObject values
	 *  are skipped.
	 */
	public static class EObjectContainmentTreeNodeProvider implements ChildProvider<EObjectTreeNode> {

		public Iterable<EObjectTreeNode> getChildIterable(EObjectTreeNode node) {

			List<EObjectTreeNode> nodes = new ArrayList<EObjectTreeNode>();
			

			// don't yield nodes again when traversing their features
			if(node.getFeature() == null) {

				// this is supposed to be the same as EObject.eContents()
				// see org.eclipse.emf.ecore.util.EContentsEList.EContentsEList(EObject)
				EStructuralFeature[] containments = ((EClassImpl.FeatureSubsetSupplier) node.getEObject().eClass().getEAllStructuralFeatures()).containments();
				
				// add eObject - feature pairs
				if(containments != null) {
					addFeatures(node, Arrays.asList(containments), nodes);
				}
				addFeatures(node, node.getEObject().eClass().getEAllAttributes(), nodes);
				
			}
			
			return nodes;
		}
		
		protected void addFeatures(EObjectTreeNode node, Iterable<? extends EStructuralFeature> features, List<EObjectTreeNode> nodes) {
			
			if(features != null) {
				for(EStructuralFeature feature: features) {
					String subPath = node.getPath() + "." + feature.getName(); 
					nodes.add(new EObjectTreeNode(subPath, node.getEObject(), feature));
					
					// add feature values
					Object value = node.getEObject().eGet(feature);
					if(isObjectDescendedInto(value)) {
						nodes.add(new EObjectTreeNode(subPath, (EObject) value, null));
						
					// add collection feature
					} else if (value instanceof Iterable<?>) {
						int i = 0;
						for(Object item: (Iterable<?>) value) {
							if(isObjectDescendedInto(item)) {
								nodes.add(new EObjectTreeNode(subPath + "[" + (i++) + "]", (EObject) item, null));
							}
						}
					}			
				}
			}
		}
		
		protected boolean isObjectDescendedInto(Object object) {
			return object instanceof EObject && !(object instanceof EEnumLiteral);
		}
	}
	
	/**
	 *  Utility class that provides the "child" {@link EObjectTreeNode}s for recursive validation.
	 *  For each EObject value in the hierarchy a node is created that contains that EObject value,
	 *  and additionally a node is created for each feature of that value. Null or Non-EObject values
	 *  are skipped.
	 */
	public static class EObjectTreeNodeProvider implements ChildProvider<EObjectTreeNode> {
		
		public Iterable<EObjectTreeNode> getChildIterable(EObjectTreeNode node) {
			
			List<EObjectTreeNode> nodes = new ArrayList<EObjectTreeNode>();
			
			
			// don't yield nodes again when traversing their features
			if(node.getFeature() == null) {
				
				// this is supposed to be the same as EObject.eContents()
				// see org.eclipse.emf.ecore.util.EContentsEList.EContentsEList(EObject)
				EList<EStructuralFeature> features = node.getEObject().eClass().getEAllStructuralFeatures();
				
				// add eObject - feature pairs
				if(!features.isEmpty()) {
					addFeatures(node, features, nodes);
				}
				addFeatures(node, node.getEObject().eClass().getEAllAttributes(), nodes);
				
			}
			
			return nodes;
		}
		
		protected void addFeatures(EObjectTreeNode node, Iterable<? extends EStructuralFeature> features, List<EObjectTreeNode> nodes) {
			
			if(features != null) {
				for(EStructuralFeature feature: features) {
					String subPath = node.getPath() + "." + feature.getName(); 
					nodes.add(new EObjectTreeNode(subPath, node.getEObject(), feature));
					
					// add feature values
					Object value = node.getEObject().eGet(feature);
					if(isObjectDescendedInto(value)) {
						nodes.add(new EObjectTreeNode(subPath, (EObject) value, null));
						
						// add collection feature
					} else if (value instanceof Iterable<?>) {
						int i = 0;
						for(Object item: (Iterable<?>) value) {
							if(isObjectDescendedInto(item)) {
								nodes.add(new EObjectTreeNode(subPath + "[" + (i++) + "]", (EObject) item, null));
							}
						}
					}			
				}
			}
		}
		
		protected boolean isObjectDescendedInto(Object object) {
			return object instanceof EObject && !(object instanceof EEnumLiteral);
		}
	}
	
	public boolean walk(EObject eObject) {
		return walk(new EObjectTreeNode("", eObject, null));
	}
}