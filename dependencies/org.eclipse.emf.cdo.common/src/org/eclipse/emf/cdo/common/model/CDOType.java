/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Erdal Karaca - added support for MAP Type
 */
package org.eclipse.emf.cdo.common.model;

import java.io.IOException;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Provides access to all CDO-supported data types.
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 * @apiviz.landmark
 */
public interface CDOType
{
  public static final CDOType OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.OBJECT;

  public static final CDOType BOOLEAN = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BOOLEAN;

  public static final CDOType BOOLEAN_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BOOLEAN_OBJECT;

  public static final CDOType BYTE = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BYTE;

  public static final CDOType BYTE_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BYTE_OBJECT;

  public static final CDOType CHAR = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.CHAR;

  public static final CDOType CHARACTER_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.CHARACTER_OBJECT;

  public static final CDOType DATE = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.DATE;

  public static final CDOType DOUBLE = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.DOUBLE;

  public static final CDOType DOUBLE_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.DOUBLE_OBJECT;

  public static final CDOType FLOAT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.FLOAT;

  public static final CDOType FLOAT_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.FLOAT_OBJECT;

  public static final CDOType INT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.INT;

  public static final CDOType INTEGER_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.INTEGER_OBJECT;

  public static final CDOType LONG = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.LONG;

  public static final CDOType LONG_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.LONG_OBJECT;

  public static final CDOType SHORT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.SHORT;

  public static final CDOType SHORT_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.SHORT_OBJECT;

  public static final CDOType STRING = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.STRING;

  public static final CDOType BYTE_ARRAY = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BYTE_ARRAY;

  /**
   * @since 3.0
   */
  public static final CDOType OBJECT_ARRAY = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.OBJECT_ARRAY;

  /**
   * @since 4.0
   */
  public static final CDOType MAP = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.MAP;

  /**
   * @since 4.1
   */
  public static final CDOType SET = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.SET;

  /**
   * @since 4.1
   */
  public static final CDOType LIST = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.LIST;

  /**
   * @since 2.0
   */
  public static final CDOType BIG_DECIMAL = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BIG_DECIMAL;

  /**
   * @since 2.0
   */
  public static final CDOType BIG_INTEGER = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BIG_INTEGER;

  /**
   * @since 3.0
   */
  public static final CDOType ENUM_ORDINAL = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.ENUM_ORDINAL;

  /**
   * @since 3.0
   */
  public static final CDOType ENUM_LITERAL = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.ENUM_LITERAL;

  /**
   * @since 4.0
   */
  public static final CDOType BLOB = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BLOB;

  /**
   * @since 4.0
   */
  public static final CDOType CLOB = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.CLOB;

  public static final CDOType CUSTOM = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.CUSTOM;

  /**
   * @since 2.0
   */
  public static final CDOType FEATURE_MAP_ENTRY = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.FEATURE_MAP_ENTRY;

  public String getName();

  /**
   * @since 4.0
   */
  public byte getTypeID();

  public boolean canBeNull();

  public Object getDefaultValue();

  public Object copyValue(Object value);

  /**
   * @since 4.0
   */
  public Object adjustReferences(CDOReferenceAdjuster adjuster, Object value, EStructuralFeature feature, int index);

  /**
   * @since 3.0
   */
  public Object readValue(CDODataInput in) throws IOException;

  /**
   * @since 3.0
   */
  public void writeValue(CDODataOutput out, Object value) throws IOException;

  /**
   * @since 2.0
   */
  public Object convertToEMF(EClassifier feature, Object value);

  /**
   * @since 2.0
   */
  public Object convertToCDO(EClassifier feature, Object value);
}
