/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 331619 - Support cross-referencing (XRef) for abstract classes and class hierarchies
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.State;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.QueryHandlerFactory;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public class XRefsQueryHandler implements IQueryHandler
{
  public XRefsQueryHandler()
  {
  }

  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    QueryContext xrefsContext = new QueryContext(info, context);
    accessor.queryXRefs(xrefsContext);

    CDOBranchPoint branchPoint = context;
    CDOBranch branch = branchPoint.getBranch();
    while (!branch.isMainBranch() && context.getResultCount() < info.getMaxResults())
    {
      branchPoint = branch.getBase();
      branch = branchPoint.getBranch();

      xrefsContext.setBranchPoint(branchPoint);
      accessor.queryXRefs(xrefsContext);
    }
  }

  public static void collectSourceCandidates(IView view, Collection<EClass> concreteTypes,
      Map<EClass, List<EReference>> sourceCandidates)
  {
    InternalRepository repository = (InternalRepository)view.getRepository();
    CDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);

    for (CDOPackageInfo packageInfo : packageRegistry.getPackageInfos())
    {
      // System.out.println();
      // System.out.println();
      // System.out.println(packageInfo);
      collectSourceCandidates(packageInfo, concreteTypes, sourceCandidates);
      // for (Entry<EClass, List<EReference>> entry : sourceCandidates.entrySet())
      // {
      // System.out.println(" ---> " + entry.getKey().getName());
      // for (EReference eReference : entry.getValue())
      // {
      // System.out.println("      ---> " + eReference.getName());
      // }
      // }
      //
      // System.out.println();
      // System.out.println();
    }
  }

  public static void collectSourceCandidates(CDOPackageInfo packageInfo, Collection<EClass> concreteTypes,
      Map<EClass, List<EReference>> sourceCandidates)
  {
    State state = packageInfo.getPackageUnit().getState();
    if (state == CDOPackageUnit.State.LOADED || state == CDOPackageUnit.State.PROXY)
    {
      EPackage ePackage = packageInfo.getEPackage();
      for (EClassifier eClassifier : ePackage.getEClassifiers())
      {
        if (eClassifier instanceof EClass)
        {
          collectSourceCandidates((EClass)eClassifier, concreteTypes, sourceCandidates);
        }
      }
    }
  }

  public static void collectSourceCandidates(EClass eClass, Collection<EClass> concreteTypes,
      Map<EClass, List<EReference>> sourceCandidates)
  {
    if (!eClass.isAbstract() && !eClass.isInterface())
    {
      for (EStructuralFeature eStructuralFeature : eClass.getEAllStructuralFeatures())
      {
        if (eStructuralFeature instanceof EReference && EMFUtil.isPersistent(eStructuralFeature))
        {
          collectSourceCandidates(eClass, (EReference)eStructuralFeature, concreteTypes, sourceCandidates);
        }
      }
    }
  }

  public static void collectSourceCandidates(EReference eReference, Collection<EClass> concreteTypes,
      Map<EClass, List<EReference>> sourceCandidates, CDOPackageRegistry packageRegistry)
  {
    EClass rootClass = eReference.getEContainingClass();
    collectSourceCandidates(rootClass, eReference, concreteTypes, sourceCandidates);

    Collection<EClass> descendentClasses = packageRegistry.getSubTypes().get(rootClass);
    if (descendentClasses != null)
    {
      for (EClass candidateClass : descendentClasses)
      {
        collectSourceCandidates(candidateClass, eReference, concreteTypes, sourceCandidates);
      }
    }
  }

  public static void collectSourceCandidates(EClass eClass, EReference eReference, Collection<EClass> concreteTypes,
      Map<EClass, List<EReference>> sourceCandidates)
  {
    if (!eClass.isAbstract() && !eClass.isInterface())
    {
      if (!eReference.isContainer() && !eReference.isContainment())
      {
        if (canReference(eReference.getEReferenceType(), concreteTypes))
        {
          List<EReference> list = sourceCandidates.get(eClass);
          if (list == null)
          {
            list = new ArrayList<EReference>();
            sourceCandidates.put(eClass, list);
          }

          list.add(eReference);
        }
      }
    }
  }

  private static boolean canReference(EClass declaredType, Collection<EClass> concreteTypes)
  {
    for (EClass concreteType : concreteTypes)
    {
      if (declaredType.isSuperTypeOf(concreteType))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  private static final class QueryContext implements IStoreAccessor.QueryXRefsContext
  {
    private CDOQueryInfo info;

    private IQueryContext context;

    private CDOBranchPoint branchPoint;

    private Map<CDOID, EClass> targetObjects;

    private Map<EClass, List<EReference>> sourceCandidates;

    private EReference[] sourceReferences;

    public QueryContext(CDOQueryInfo info, IQueryContext context)
    {
      this.info = info;
      this.context = context;
      branchPoint = context;
    }

    public void setBranchPoint(CDOBranchPoint branchPoint)
    {
      this.branchPoint = branchPoint;
    }

    public CDOBranch getBranch()
    {
      return branchPoint.getBranch();
    }

    public long getTimeStamp()
    {
      return branchPoint.getTimeStamp();
    }

    public Map<CDOID, EClass> getTargetObjects()
    {
      if (targetObjects == null)
      {
        IRepository repository = context.getView().getRepository();
        IStore store = repository.getStore();
        CDOPackageRegistry packageRegistry = repository.getPackageRegistry();

        targetObjects = new HashMap<CDOID, EClass>();
        StringTokenizer tokenizer = new StringTokenizer(info.getQueryString(), "|");
        while (tokenizer.hasMoreTokens())
        {
          String val = tokenizer.nextToken();
          CDOID id = store.createObjectID(val);

          CDOClassifierRef classifierRef;
          if (id instanceof CDOClassifierRef.Provider)
          {
            classifierRef = ((CDOClassifierRef.Provider)id).getClassifierRef();
          }
          else
          {
            val = tokenizer.nextToken();
            classifierRef = new CDOClassifierRef(val);
          }

          EClass eClass = (EClass)classifierRef.resolve(packageRegistry);
          targetObjects.put(id, eClass);
        }
      }

      return targetObjects;
    }

    public EReference[] getSourceReferences()
    {
      if (sourceReferences == null)
      {
        sourceReferences = parseSourceReferences();
      }

      return sourceReferences;
    }

    private EReference[] parseSourceReferences()
    {
      List<EReference> result = new ArrayList<EReference>();
      CDOPackageRegistry packageRegistry = context.getView().getRepository().getPackageRegistry();

      String params = (String)info.getParameters().get(CDOProtocolConstants.QUERY_LANGUAGE_XREFS_SOURCE_REFERENCES);
      if (params == null)
      {
        return new EReference[0];
      }

      StringTokenizer tokenizer = new StringTokenizer(params, "|");
      while (tokenizer.hasMoreTokens())
      {
        String className = tokenizer.nextToken();
        CDOClassifierRef classifierRef = new CDOClassifierRef(className);
        EClass eClass = (EClass)classifierRef.resolve(packageRegistry);

        String featureName = tokenizer.nextToken();
        EReference sourceReference = (EReference)eClass.getEStructuralFeature(featureName);
        result.add(sourceReference);
      }

      return result.toArray(new EReference[result.size()]);
    }

    public Map<EClass, List<EReference>> getSourceCandidates()
    {
      if (sourceCandidates == null)
      {
        sourceCandidates = new HashMap<EClass, List<EReference>>();
        Collection<EClass> concreteTypes = getTargetObjects().values();
        EReference[] sourceReferences = getSourceReferences();

        if (sourceReferences.length != 0)
        {
          InternalRepository repository = (InternalRepository)context.getView().getRepository();
          InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);
          for (EReference eReference : sourceReferences)
          {
            collectSourceCandidates(eReference, concreteTypes, sourceCandidates, packageRegistry);
          }
        }
        else
        {
          collectSourceCandidates(context.getView(), concreteTypes, sourceCandidates);
        }
      }

      return sourceCandidates;
    }

    public int getMaxResults()
    {
      return info.getMaxResults();
    }

    public boolean addXRef(CDOID targetID, CDOID sourceID, EReference sourceReference, int sourceIndex)
    {
      if (CDOIDUtil.isNull(targetID))
      {
        return true;
      }

      return context.addResult(new CDOIDReference(targetID, sourceID, sourceReference, sourceIndex));
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends QueryHandlerFactory
  {
    public Factory()
    {
      super(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES);
    }

    @Override
    public XRefsQueryHandler create(String description) throws ProductCreationException
    {
      return new XRefsQueryHandler();
    }
  }
}
