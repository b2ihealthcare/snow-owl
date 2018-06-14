/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.datastore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.google.common.collect.ImmutableSet;

/**
 * POJO for storing information about the SNOMED&nbsp;CT map set used for RF1 cross map export.
 * 
 * @see MapSetType
 */
public class SnomedMapSetSetting implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Set<String> DEFAULT_IDS = ImmutableSet.of(Concepts.ICD_O_REFERENCE_SET_ID, 
			Concepts.ICD_9_CM_REFERENCE_SET_ID, 
			Concepts.ICD_10_REFERENCE_SET_ID);

	/**
	 * The reference set identifier concept ID.
	 */
	private String refSetId;

	/**
	 * Human readable name of the map set.	
	 */
	private String mapSetName;

	/**
	 * The unique OID of the map set scheme.
	 */
	private String mapSchemeId;

	/**
	 * The name of the map set scheme.
	 */
	private String mapSchemeName;

	/**
	 * The version of the map set scheme.
	 */
	private String mapSchemeVersion;

	/**
	 * Type of the map set.
	 */
	private MapSetType mapSetType;

	/**
	 * Flag for indicating whether the reference set is a complex type or not.
	 */
	private boolean complex;

	public static SnomedMapSetSetting read(final DataInputStream dis) throws IOException {
		return new SnomedMapSetSetting(
			dis.readUTF(),
			dis.readUTF(),
			dis.readUTF(),
			dis.readUTF(),
			dis.readUTF(),
			MapSetType.getByValue(dis.readInt()),
			dis.readBoolean()
		);
	}
	
	public static void write(final SnomedMapSetSetting setting, final DataOutputStream dos) throws IOException {
		dos.writeUTF(setting.getRefSetId());
		dos.writeUTF(setting.getMapSetName());
		dos.writeUTF(setting.getMapSchemeId());
		dos.writeUTF(setting.getMapSchemeName());
		dos.writeUTF(setting.getMapSchemeVersion());
		dos.writeInt(setting.getMapSetType().getValue());
		dos.writeBoolean(setting.isComplex());
	}
	
	/**
	 * Map set setting for ICD-O.
	 */
	public static final SnomedMapSetSetting ICD_O_SETTING = new SnomedMapSetSetting(
		Concepts.ICD_O_REFERENCE_SET_ID,
		"ICD-O-3",
		"2.16.840.1.113883.6.5.2.2",
		"International Classification of Diseases for Oncology, 3rd Edition",
		"2001",
		MapSetType.SINGLE,
		false
	);
	
	/**
	 * Map set setting for ICD-9-CM.
	 */
	public static final SnomedMapSetSetting ICD_9_CM_SETTING = new SnomedMapSetSetting(
		Concepts.ICD_9_CM_REFERENCE_SET_ID,
		"ICD-9-CM",
		"2.16.840.1.113883.6.5.2.1",
		"International Classification of Diseases and Related Health Problems, 9th Revision, Clinical Modifications.",
		"2012",
		MapSetType.MULTIPLE,
		true
	);
	
	/**
	 * Map set setting for ICD-10.
	 */
	public static final SnomedMapSetSetting ICD_10_SETTING = new SnomedMapSetSetting(
		Concepts.ICD_10_REFERENCE_SET_ID,
		"ICD-10",
		"2.16.840.1.113883.6.3",
		"International Classification of Diseases revision 10 (ICD-10)",
		"2012",
		MapSetType.MULTIPLE,
		true
	);
	
	public SnomedMapSetSetting(final String refSetId, 
			final String mapSetName, 
			final String mapSchemeId, 
			final String mapSchemeName, 
			final String mapSchemeVersion, 
			final MapSetType mapSetType, 
			final boolean complex) {
		
		this.refSetId = refSetId;
		this.mapSetName = mapSetName;
		this.mapSchemeId = mapSchemeId;
		this.mapSchemeName = mapSchemeName;
		this.mapSchemeVersion = mapSchemeVersion;
		this.mapSetType = mapSetType;
		this.complex = complex;
	}

	public String getRefSetId() {
		return refSetId;
	}

	public void setRefSetId(String refSetId) {
		this.refSetId = refSetId;
	}

	public String getMapSetName() {
		return mapSetName;
	}

	public void setMapSetName(String mapSetName) {
		this.mapSetName = mapSetName;
	}

	public String getMapSchemeId() {
		return mapSchemeId;
	}

	public void setMapSchemeId(String mapSchemeId) {
		this.mapSchemeId = mapSchemeId;
	}

	public String getMapSchemeName() {
		return mapSchemeName;
	}

	public void setMapSchemeName(String mapSchemeName) {
		this.mapSchemeName = mapSchemeName;
	}

	public String getMapSchemeVersion() {
		return mapSchemeVersion;
	}

	public void setMapSchemeVersion(String mapSchemeVersion) {
		this.mapSchemeVersion = mapSchemeVersion;
	}

	public MapSetType getMapSetType() {
		return mapSetType;
	}

	public void setMapSetType(MapSetType mapSetType) {
		this.mapSetType = mapSetType;
	}

	public boolean isComplex() {
		return complex;
	}

	public void setComplex(boolean complex) {
		this.complex = complex;
	}
}
