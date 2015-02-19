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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class CDOFeatureAnalyzerModelBased extends CDOAbstractFeatureRuleAnalyzer
{
  CDOAnalyzerFeatureInfo featureInfos = new CDOAnalyzerFeatureInfo();

  public CDOFeatureAnalyzerModelBased()
  {
  }

  @Override
  public void doPreTraverseFeature(CDOObject cdoObject, EStructuralFeature feature, int index)
  {
  }

  @Override
  public void doPostTraverseFeature(CDOObject cdoObject, EStructuralFeature feature, int index, Object value)
  {
    if (didFetch())
    {
      featureInfos.activate(cdoObject.eClass(), feature);
    }
  }

  public CDOID getContext()
  {
    return CDOID.NULL;
  }

  public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
  {
    fetchData();
    List<CDOFetchRule> rules = new ArrayList<CDOFetchRule>();
    rules.addAll(featureInfos.getRules(lastTraverseCDOObject.eClass(), lastTraverseFeature));
    return rules;
  }
}
