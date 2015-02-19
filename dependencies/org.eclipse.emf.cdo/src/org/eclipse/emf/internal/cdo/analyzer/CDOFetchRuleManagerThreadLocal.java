/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import java.util.Collection;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class CDOFetchRuleManagerThreadLocal implements CDOFetchRuleManager
{
  private static final ThreadLocal<CDOFetchRuleManager> threadLocal = new ThreadLocal<CDOFetchRuleManager>();

  public CDOFetchRuleManagerThreadLocal()
  {
  }

  public static CDOFetchRuleManager getCurrent()
  {
    return threadLocal.get();
  }

  public static void join(CDOFetchRuleManager fetchRulemanager)
  {
    threadLocal.set(fetchRulemanager);
  }

  public static void leave()
  {
    threadLocal.set(null);
  }

  public CDOID getContext()
  {
    CDOFetchRuleManager analyzer = CDOFetchRuleManagerThreadLocal.getCurrent();
    return analyzer != null ? analyzer.getContext() : null;
  }

  public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
  {
    CDOFetchRuleManager analyzer = CDOFetchRuleManagerThreadLocal.getCurrent();
    return analyzer != null ? analyzer.getFetchRules(ids) : null;
  }

  public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
  {
    CDOFetchRuleManager analyzer = CDOFetchRuleManagerThreadLocal.getCurrent();
    return analyzer != null ? analyzer.getCollectionLoadingPolicy() : null;
  }
}
