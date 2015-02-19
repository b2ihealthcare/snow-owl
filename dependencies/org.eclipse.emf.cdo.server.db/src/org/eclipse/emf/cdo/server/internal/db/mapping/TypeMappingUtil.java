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
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.messages.Messages;

import org.eclipse.net4j.db.DBType;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Winkler
 */
public class TypeMappingUtil
{
  private static final Pattern FACTORY_DESCRIPTOR_PATTERN = Pattern.compile("(.+);(.+)#(.+)->(.+)");

  /**
   * Utility class - no instantiation.
   */
  private TypeMappingUtil()
  {
  }

  public static ITypeMapping.Descriptor createDescriptor(String id, EClassifier eClassifier, DBType dbType)
  {
    String factoryType = createFactoryType(id, eClassifier, dbType);
    return new TypeMappingDescriptor(id, factoryType, eClassifier, dbType);
  }

  public static String createFactoryType(String id, EClassifier eClassifier, DBType dbType)
  {
    StringBuilder builder = new StringBuilder();

    // id
    builder.append(id);
    builder.append(";");

    // classifier
    builder.append(eClassifier.getEPackage().getNsURI());
    builder.append("#");
    builder.append(eClassifier.getName());
    builder.append("->");

    // dbtype
    builder.append(dbType.getKeyword());

    return builder.toString();
  }

  public static ITypeMapping.Descriptor descriptorFromFactoryType(String factoryType) throws FactoryTypeParserException
  {
    Matcher matcher = FACTORY_DESCRIPTOR_PATTERN.matcher(factoryType);

    if (!matcher.matches())
    {
      throw new FactoryTypeParserException(MessageFormat.format(Messages.getString("FactoryTypeParserException.1"),
          factoryType));
    }

    String id = matcher.group(1);
    String packageUri = matcher.group(2);
    String classifierName = matcher.group(3);
    String typeKeyword = matcher.group(4);

    EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageUri);
    if (ePackage == null)
    {
      throw new FactoryTypeParserException(MessageFormat.format(Messages.getString("FactoryTypeParserException.2"),
          packageUri, factoryType));
    }

    EClassifier eClassifier = ePackage.getEClassifier(classifierName);
    if (eClassifier == null)
    {
      throw new FactoryTypeParserException(MessageFormat.format(Messages.getString("FactoryTypeParserException.3"),
          classifierName, factoryType));
    }

    DBType dbType = DBType.getTypeByKeyword(typeKeyword);
    if (dbType == null)
    {
      throw new FactoryTypeParserException(MessageFormat.format(Messages.getString("FactoryTypeParserException.4"),
          dbType, factoryType));
    }

    return new TypeMappingDescriptor(id, factoryType, eClassifier, dbType);
  }

  public static class FactoryTypeParserException extends Exception
  {
    private static final long serialVersionUID = 1L;

    public FactoryTypeParserException(String desc)
    {
      super(desc);
    }
  }
}
