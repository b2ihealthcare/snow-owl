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
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalAuditMappingStrategyWithRanges extends HorizontalAuditMappingStrategy
{
  public HorizontalAuditMappingStrategyWithRanges()
  {
  }

  @Override
  public boolean hasDeltaSupport()
  {
    return true;
  }

  @Override
  public IClassMapping doCreateClassMapping(EClass eClass)
  {
    return new HorizontalAuditClassMapping(this, eClass);
  }

  @Override
  public IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new AuditListTableMappingWithRanges(this, containingClass, feature);
  }

  @Override
  public IListMapping doCreateFeatureMapMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new AuditFeatureMapTableMappingWithRanges(this, containingClass, feature);
  }

  @Override
  protected String modifyListJoin(String attrTable, String listTable, String join)
  {
    join += " AND " + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_ADDED;
    join += "<=" + attrTable + "." + CDODBSchema.ATTRIBUTES_VERSION;
    join += " AND (" + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_REMOVED;
    join += " IS NULL OR " + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_REMOVED;
    join += ">" + attrTable + "." + CDODBSchema.ATTRIBUTES_VERSION + ")";
    return join;
  }
}
