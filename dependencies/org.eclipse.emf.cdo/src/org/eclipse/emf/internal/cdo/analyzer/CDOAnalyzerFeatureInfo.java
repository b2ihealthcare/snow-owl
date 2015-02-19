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

import org.eclipse.emf.cdo.common.util.CDOFetchRule;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class CDOAnalyzerFeatureInfo
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOAnalyzerFeatureInfo.class);

  private Map<CDOFetchFeatureInfo, CDOFetchFeatureInfo> featureStats = new HashMap<CDOFetchFeatureInfo, CDOFetchFeatureInfo>();

  private Map<EClass, CDOFetchRule> fetchRules = new HashMap<EClass, CDOFetchRule>();

  public CDOAnalyzerFeatureInfo()
  {
  }

  public Collection<CDOFetchRule> getRules(EClass eClass, EStructuralFeature feature)
  {
    return fetchRules.values();
  }

  public synchronized CDOFetchFeatureInfo getFeatureStat(EClass eClass, EStructuralFeature feature)
  {
    CDOFetchFeatureInfo search = new CDOFetchFeatureInfo(eClass, feature);
    CDOFetchFeatureInfo featureRule = featureStats.get(search);
    if (featureRule == null)
    {
      featureRule = search;
      featureStats.put(search, featureRule);
    }

    return featureRule;
  }

  public boolean isActive(EClass eClass, EStructuralFeature feature)
  {
    CDOFetchFeatureInfo search = new CDOFetchFeatureInfo(eClass, feature);
    CDOFetchFeatureInfo featureRule = featureStats.get(search);
    return featureRule != null && featureRule.isActive();
  }

  public void activate(EClass eClass, EStructuralFeature feature)
  {
    CDOFetchFeatureInfo info = getFeatureStat(eClass, feature);
    if (!info.isActive())
    {
      info.setActive(true);
      addRule(eClass, feature);
    }
  }

  public void deactivate(EClass eClass, EStructuralFeature feature)
  {
    CDOFetchFeatureInfo info = getFeatureStat(eClass, feature);
    if (info.isActive())
    {
      info.setActive(false);
      removeRule(eClass, feature);
    }
  }

  private void addRule(EClass eClass, EStructuralFeature feature)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding rule : {0}.{1}", eClass.getName(), feature.getName()); //$NON-NLS-1$
    }

    CDOFetchRule fetchRule = fetchRules.get(eClass);
    if (fetchRule == null)
    {
      fetchRule = new CDOFetchRule(eClass);
      fetchRules.put(eClass, fetchRule);
    }

    fetchRule.addFeature(feature);
  }

  private void removeRule(EClass eClass, EStructuralFeature feature)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Removing rule : {0}.{1}", eClass.getName(), feature.getName()); //$NON-NLS-1$
    }

    CDOFetchRule fetchRule = fetchRules.get(eClass);
    if (fetchRule == null)
    {
      return;
    }

    fetchRule.removeFeature(feature);
  }
}
