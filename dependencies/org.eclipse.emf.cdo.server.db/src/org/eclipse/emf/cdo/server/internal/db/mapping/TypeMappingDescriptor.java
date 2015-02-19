/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;

import org.eclipse.net4j.db.DBType;

import org.eclipse.emf.ecore.EClassifier;

/**
 * @author Stefan Winkler
 */
public class TypeMappingDescriptor implements ITypeMapping.Descriptor
{
  private String id;

  private String factoryType;

  private EClassifier eClassifier;

  private DBType dbType;

  public TypeMappingDescriptor(String id, String factoryType, EClassifier eClassifier, DBType dbType)
  {
    this.id = id;
    this.factoryType = factoryType;
    this.eClassifier = eClassifier;
    this.dbType = dbType;
  }

  public String getID()
  {
    return id;
  }

  public String getFactoryType()
  {
    return factoryType;
  }

  public EClassifier getEClassifier()
  {
    return eClassifier;
  }

  public DBType getDBType()
  {
    return dbType;
  }

  @Override
  public String toString()
  {
    return "TypeMappingDescriptor [" + factoryType + "]";
  }
}
