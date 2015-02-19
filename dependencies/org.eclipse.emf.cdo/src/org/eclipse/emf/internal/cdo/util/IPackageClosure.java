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

import org.eclipse.emf.ecore.EPackage;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IPackageClosure
{
  public static final Set<EPackage> EMPTY_CLOSURE = Collections.emptySet();

  public Set<EPackage> calculate(Collection<EPackage> ePackages);

  public Set<EPackage> calculate(EPackage ePackage);
}
