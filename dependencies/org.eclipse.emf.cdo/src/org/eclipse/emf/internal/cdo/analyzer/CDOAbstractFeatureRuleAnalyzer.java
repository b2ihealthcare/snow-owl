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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 */
public abstract class CDOAbstractFeatureRuleAnalyzer implements CDOFeatureAnalyzer, CDOFetchRuleManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOAbstractFeatureRuleAnalyzer.class);

  protected EStructuralFeature lastTraverseFeature;

  protected int lastTraverseIndex;

  protected long lastAccessTime;

  protected long lastElapseTimeBetweenOperations;

  protected CDOObject lastTraverseCDOObject;

  protected long lastLatencyTime;

  protected CDOCollectionLoadingPolicy loadCollectionPolicy;

  private boolean didFetch;

  private int fetchCount;

  public CDOAbstractFeatureRuleAnalyzer()
  {
  }

  public int getFetchCount()
  {
    return fetchCount;
  }

  public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
  {
    return loadCollectionPolicy;
  }

  public void preTraverseFeature(CDOObject cdoObject, EStructuralFeature feature, int index)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("preTraverseFeature : {0}.{1}", cdoObject.eClass(), feature.getName()); //$NON-NLS-1$
    }

    loadCollectionPolicy = cdoObject.cdoView().getSession().options().getCollectionLoadingPolicy();
    lastTraverseFeature = feature;
    lastTraverseCDOObject = cdoObject;
    lastTraverseIndex = index;
    lastElapseTimeBetweenOperations = System.currentTimeMillis() - lastAccessTime;
    lastAccessTime = System.currentTimeMillis();
    didFetch = false;

    CDOFetchRuleManagerThreadLocal.join(this);
    doPreTraverseFeature(cdoObject, feature, index);
  }

  public void postTraverseFeature(CDOObject cdoObject, EStructuralFeature feature, int index, Object value)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("postTraverseFeature : {0}.{1}", cdoObject.eClass(), feature.getName()); //$NON-NLS-1$
    }

    try
    {
      doPostTraverseFeature(cdoObject, feature, index, value);
    }
    finally
    {
      CDOFetchRuleManagerThreadLocal.leave();
      lastAccessTime = System.currentTimeMillis();
    }
  }

  protected void doPreTraverseFeature(CDOObject cdoObject, EStructuralFeature feature, int index)
  {
  }

  protected void doPostTraverseFeature(CDOObject cdoObject, EStructuralFeature feature, int index, Object value)
  {
  }

  protected void fetchData()
  {
    didFetch = true;
    fetchCount++;
  }

  protected boolean didFetch()
  {
    return didFetch;
  }
}
