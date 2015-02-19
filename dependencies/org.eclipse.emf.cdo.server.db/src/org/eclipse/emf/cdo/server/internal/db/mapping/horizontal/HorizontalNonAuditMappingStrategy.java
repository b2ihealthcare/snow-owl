/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalNonAuditMappingStrategy extends AbstractHorizontalMappingStrategy
{
  private boolean forceZeroBasedIndex;

  public HorizontalNonAuditMappingStrategy()
  {
  }

  public boolean hasAuditSupport()
  {
    return false;
  }

  public boolean hasBranchingSupport()
  {
    return false;
  }

  public boolean hasDeltaSupport()
  {
    return true;
  }

  public boolean shallForceZeroBasedIndex()
  {
    return forceZeroBasedIndex;
  }

  @Override
  public IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new NonAuditListTableMapping(this, containingClass, feature);
  }

  @Override
  public IListMapping doCreateFeatureMapMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new NonAuditFeatureMapTableMapping(this, containingClass, feature);
  }

  @Override
  protected IClassMapping doCreateClassMapping(EClass eClass)
  {
    return new HorizontalNonAuditClassMapping(this, eClass);
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();

    String value = getProperties().get(CDODBUtil.PROP_ZEROBASED_INDEX);
    forceZeroBasedIndex = value == null ? false : Boolean.valueOf(value);
  }
}
