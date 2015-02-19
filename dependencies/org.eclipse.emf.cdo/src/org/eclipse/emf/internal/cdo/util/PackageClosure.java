/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class PackageClosure implements IPackageClosure
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_MODEL, PackageClosure.class);

  protected PackageClosure()
  {
  }

  public Set<EPackage> calculate(EPackage ePackage)
  {
    return calculate(Collections.singletonList(ePackage));
  }

  public Set<EPackage> calculate(Collection<EPackage> ePackages)
  {
    // Optimize no packages
    if (ePackages.isEmpty())
    {
      return EMPTY_CLOSURE;
    }

    Set<EPackage> visited = new HashSet<EPackage>();
    for (EPackage ePackage : ePackages)
    {
      doCollectContents(ePackage, visited);
    }

    return visited;
  }

  private void doCollectContents(EPackage ePackage, Set<EPackage> visited)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Package closure for " + ePackage.getNsURI()); //$NON-NLS-1$
    }

    handleEPackage(ePackage, visited);
    for (EPackage subPackage : ePackage.getESubpackages())
    {
      doCollectContents(subPackage, visited);
    }
  }

  protected abstract void handleEPackage(EPackage ePackage, Set<EPackage> visited);
}
