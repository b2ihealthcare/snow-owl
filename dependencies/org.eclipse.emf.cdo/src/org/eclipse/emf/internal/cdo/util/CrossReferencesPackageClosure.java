/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CrossReferencesPackageClosure extends PackageClosure
{
  public CrossReferencesPackageClosure()
  {
  }

  @Override
  public Set<EPackage> calculate(EPackage ePackage)
  {
    Set<EPackage> visited = new HashSet<EPackage>();
    handleEPackage(ePackage, visited);
    return visited;
  }

  @Override
  protected void handleEPackage(EPackage ePackage, Set<EPackage> visited)
  {
    visited.add(ePackage);
    for (Iterator<EObject> it = ePackage.eAllContents(); it.hasNext();)
    {
      EObject content = it.next();
      collectCrossReferences(content, visited);
    }
  }

  protected void collectCrossReferences(EObject content, Set<EPackage> visited)
  {
    EList<EObject> crossReferences = content.eCrossReferences();
    for (EObject crossReference : crossReferences)
    {
      EPackage crossReferencePackage = crossReference.eClass().getEPackage();
      if (!visited.contains(crossReferencePackage))
      {
        handleEPackage(crossReferencePackage, visited);
      }
    }
  }
}
