/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.claml;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Simple POJO that models the ClaML XML schema. A populated model can be used to simply generate a XML file that conforms to the ClaML schema.
 * The class has been created based on the <code>claml.dtd</code> file, but it does not contain all of the possible elements of the schema, 
 * only the currently necessary ones. 
 */
@XStreamAlias("ClaML")
public class ClamlModel implements VisitableClamlModelElement {
	
	public static final String CLAML_VERSION = "2.0.0";
	
	@XStreamAsAttribute
	private final String version = CLAML_VERSION;
	
	@XStreamConverter(TitleConverter.class)
	@XStreamAlias("Title")
	private Title title;

	@XStreamAlias("Identifier")
	private Identifier identifier;
	
	@XStreamAlias("ClassKinds")
	private final List<ClassKind> classKinds = new ArrayList<ClassKind>();
	
	@XStreamAlias("RubricKinds")
	private final List<RubricKind> rubricKinds = new ArrayList<RubricKind>();
	
	@XStreamAlias("UsageKinds")
	private final List<UsageKind> usageKinds = new ArrayList<UsageKind>();
	
	@XStreamImplicit
	@XStreamAlias("Class")
	private final List<ClamlClass> classes = new ArrayList<ClamlClass>();
	
	@XStreamImplicit
	@XStreamAlias("Meta")
	private final List<Meta> metas = new ArrayList<Meta>();
	
	@XStreamImplicit
	@XStreamAlias("Modifier")
	private final List<Modifier> modifiers = new ArrayList<Modifier>();
	
	@XStreamImplicit
	@XStreamAlias("ModifierClass")
	private final List<ModifierClass> modifierClasses = new ArrayList<ModifierClass>();
	
	public ClamlModel() {
		
	}
	
	public void addClassKind(ClassKind classKind) {
		classKinds.add(classKind);
	}
	
	public void addRubricKind(RubricKind rubricKind){
		rubricKinds.add(rubricKind);
	}
	
	public void addClass(ClamlClass clamlClass) {
		classes.add(clamlClass);
	}

	public String getVersion() {
		return version;
	}
	
	public List<ClassKind> getClassKinds() {
		return classKinds;
	}
	
	public List<RubricKind> getRubricKinds() {
		return rubricKinds;
	}
	
	public List<ClamlClass> getClasses() {
		return classes;
	}
	
	public List<Meta> getMetas() {
		return metas;
	}
	
	public List<Modifier> getModifiers() {
		return modifiers;
	}
	
	public List<ModifierClass> getModifierClasses() {
		return modifierClasses;
	}
	
	public List<UsageKind> getUsageKinds() {
		return usageKinds;
	}
	
	public void setTitle(Title title) {
		this.title = title;
	}
	
	public Title getTitle() {
		return title;
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}
	
	
	/* **************************************** */
	/* *********** CLASSES ******************** */
	/* **************************************** */
	
	/**
	 * <!ELEMENT Title (#PCDATA)>
     * <!ATTLIST Title
	 * name NMTOKEN #REQUIRED
	 * version CDATA #IMPLIED
	 * date CDATA #IMPLIED
     * >
     * 
	 */
	@XStreamAlias("Title")
	public static class Title {
		
		@XStreamAsAttribute
		private final String name;
		
		@XStreamAsAttribute
		private String version;
		
		@XStreamAsAttribute
		private Date date;
		
		private final String title;

		public Title(String name, String title) {
			this.name = name;
			this.title = title;
		}

		public Title(String name, String version, Date date, String title) {
			this(name, title);
			this.version = version;
			this.date = date;
		}

		public String getName() {
			return name;
		}

		public String getVersion() {
			return version;
		}

		public Date getDate() {
			return date;
		}

		public String getTitle() {
			return title;
		}
		
		public void setDate(Date date) {
			this.date = date;
		}
		
		public void setVersion(String version) {
			this.version = version;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("name", name)
					.add("title", title)
					.add("version", version)
					.add("date", date)
					.toString();
		}
	}
	
	
	/**
	 * <!ELEMENT ClassKind (Display*)>
	 *	<!ATTLIST ClassKind
	 *	name ID #REQUIRED 
	 * >
	 *
	 *
	 */
	@XStreamAlias("ClassKind")
	public static class ClassKind {
		
		@XStreamAsAttribute
		private final String name;

		public ClassKind(final String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("name", name).toString();
		}
	}
	
	/**
	 * <!ELEMENT RubricKind (Display*)>
	 *  <!ATTLIST RubricKind
	 *  name ID #REQUIRED
	 *  inherited (true|false) "true"
	 *  >
	 * 
	 *
	 */
	@XStreamAlias("RubricKind")
	public static class RubricKind {
		
		@XStreamAsAttribute
		private final String name;

		public RubricKind(final String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("name", name).toString();
		}
	}
	
	/**
	 * <!ELEMENT UsageKind EMPTY>
	 * <!ATTLIST UsageKind
	 * name ID #REQUIRED
	 * mark CDATA #REQUIRED
	 * >
	 * 
	 *
	 */
	@XStreamAlias("UsageKind")
	public static class UsageKind {
		
		@XStreamAsAttribute
		private final String name;
		
		@XStreamAsAttribute
		private final String mark;
		
		public UsageKind(final String name, final String mark) {
			this.name = name;
			this.mark = mark;
		}
		
		public String getName() {
			return name;
		}
		
		public String getMark() {
			return mark;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("name", name).add("mark", mark).toString();
		}

	}
	
	
	public static abstract class AbstractClamlClass implements VisitableClamlModelElement {
		
		@XStreamAsAttribute
		private final String code;

		public AbstractClamlClass(final String code) {
			this.code = code;
		}
		
		public String getCode() {
			return code;
		}
	}
	
	/**
	 * <!ELEMENT Meta EMPTY>
	 * <!ATTLIST Meta
	 * name CDATA #REQUIRED
	 * value CDATA #REQUIRED
	 * variants IDREFS #IMPLIED
	 * >
	 */
	@XStreamAlias("Meta")
	public static class Meta {
		
		@XStreamAsAttribute
		private final String name;
		
		@XStreamAsAttribute
		private final String value;
		
		public Meta(final String name, final String value) {
			this.name = name;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		
		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("name", name).add("value", value).toString(); 
		}
	}
	
	/**
	 * <!ELEMENT Identifier EMPTY>
	 * <!ATTLIST Identifier
	 * authority NMTOKEN #IMPLIED
	 * uid CDATA #REQUIRED
	 * >
	 */
	@XStreamAlias("Identifier")
	public static class Identifier {
		
		@XStreamAsAttribute
		private final String authority;
		
		@XStreamAsAttribute
		private final String uid;

		public Identifier(final String authority, final String uid) {
			this.authority = authority;
			this.uid = uid;
		}
		
		public String getAuthority() {
			return authority;
		}
		
		public String getUid() {
			return uid;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("uid", uid)
					.add("authority", authority)
					.toString();
		}
	}
	
	/**
	 * <!ELEMENT Modifier (
	 * Meta*,
	 * SubClass*,
	 * Rubric*,
	 * History*)
	 * >
	 * <!ATTLIST Modifier
	 * code NMTOKEN #REQUIRED
	 * variants IDREFS #IMPLIED
	 * > 
	 */
	@XStreamAlias("Modifier")
	public static class Modifier extends AbstractClamlClass implements VisitableClamlModelElement {
		
		@XStreamImplicit
		@XStreamAlias("Meta")
		private final List<Meta> metas = new ArrayList<Meta>();
		
		@XStreamImplicit
		@XStreamAlias("SubClass")
		private final List<SubClass> subClasses = new ArrayList<SubClass>();
		
		@XStreamImplicit
		@XStreamAlias("Rubric")
		private final List<Rubric> rubrics = new ArrayList<Rubric>();
		
		public Modifier(final String code) {
			super(code);
		}
		
		public List<Meta> getMetas() {
			return metas;
		}
		
		public List<Rubric> getRubrics() {
			return rubrics;
		}
		
		public List<SubClass> getSubClasses() {
			return subClasses;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("code", getCode())
					.toString();
		}

		@Override
		public void accept(ClamlModelVisitor visitor) {
			visitor.visit(this);
			ClamlUtil.checkAndVisitAll(getSubClasses(), visitor);
			ClamlUtil.checkAndVisitAll(getRubrics(), visitor);
		}
	}
	
	/**
	 * <!ELEMENT ModifierClass (
	 * Meta*,
	 * SuperClass,
	 * SubClass*,
	 * Rubric*,
	 * History*)
	 * >
	 * <!ATTLIST ModifierClass
	 * modifier NMTOKEN #REQUIRED
	 * code NMTOKEN #REQUIRED
	 * usage IDREF #IMPLIED
	 * variants IDREFS #IMPLIED
	 * > 
	 */
	@XStreamAlias("ModifierClass")
	public static class ModifierClass extends AbstractClamlClass implements VisitableClamlModelElement {
		
		@XStreamImplicit
		@XStreamAlias("Meta")
		private final List<Meta> metas = new ArrayList<Meta>();
		
		@XStreamAlias("SuperClass")
		private final SuperClass superClass;
		
		@XStreamImplicit
		@XStreamAlias("SubClass")
		private final List<SubClass> subClasses = new ArrayList<SubClass>();
		
		@XStreamImplicit
		@XStreamAlias("Rubric")
		private final List<Rubric> rubrics = new ArrayList<Rubric>();
		
		@XStreamAsAttribute
		private final String modifier;
		
		@XStreamAsAttribute
		private final String usage;

		public ModifierClass(final SuperClass superClass, final String code,
				final String modifier, final String usage) {
			super(code);
			this.superClass = superClass;
			this.modifier = modifier;
			this.usage = usage;
		}
		
		public List<Meta> getMetas() {
			return metas;
		}
		
		public String getModifier() {
			return modifier;
		}
		
		public List<Rubric> getRubrics() {
			return rubrics;
		}
		
		public List<SubClass> getSubClasses() {
			return subClasses;
		}
		
		public SuperClass getSuperClass() {
			return superClass;
		}
		
		public String getUsage() {
			return usage;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("code", getCode())
					.add("modifier", modifier)
					.add("usage", usage)
					.add("superClass", superClass)
					.add("subClasses", subClasses)
					.add("metas", metas)
					.add("rubrics", rubrics)
					.toString();
		}

		@Override
		public void accept(ClamlModelVisitor visitor) {
			visitor.visit(this);
			ClamlUtil.checkAndVisitAll(getRubrics(), visitor);
			ClamlUtil.checkAndVisitAll(getSubClasses(), visitor);
			getSuperClass().accept(visitor);
		}
	}
	
	/**
	 * <!ELEMENT ModifiedBy (
	 * Meta*,
	 * ValidModifierClass*)
	 * >
	 * <!ATTLIST ModifiedBy
	 * code NMTOKEN #REQUIRED
	 * all (true|false) "true"
	 * position CDATA #IMPLIED
	 * variants IDREFS #IMPLIED
	 * >
	 */
	@XStreamAlias("ModifiedBy")
	public static class ModifiedBy extends AbstractClamlClass implements VisitableClamlModelElement {
		@XStreamImplicit
		@XStreamAlias("Meta")
		private final List<Meta> metas = new ArrayList<Meta>();
		
		private final boolean all;
		
		private final Integer position;
		
		public ModifiedBy(final String code, final boolean all, final Integer position) {
			super(code);
			this.all = all;
			this.position = position;
		}
		
		public List<Meta> getMetas() {
			return metas;
		}
		
		public boolean isAll() {
			return all;
		}
		
		public Integer getPosition() {
			return position;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("code", getCode())
					.add("position", position)
					.add("all", all)
					.toString();
		}

		@Override
		public void accept(ClamlModelVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	/**
	 * <!ELEMENT ExcludeModifier EMPTY>
	 * <!ATTLIST ExcludeModifier
	 * code NMTOKEN #REQUIRED
	 * variants IDREFS #IMPLIED
	 * >
	 */
	@XStreamAlias("ExcludeModifier")
	public static class ExcludeModifier extends AbstractClamlClass implements VisitableClamlModelElement {
		
		public ExcludeModifier(String code) {
			super(code);
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("code", getCode()).toString();
		}

		@Override
		public void accept(ClamlModelVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	/**
	 * 
	 * <!ELEMENT Class (
	 * Meta*,
	 * SuperClass*,
	 * SubClass*,
	 * ModifiedBy*,
	 * ExcludeModifier*,
	 * Rubric*,
     * History*)
     * >
     * <!ATTLIST Class
	 * code NMTOKEN #REQUIRED
	 * kind IDREF #REQUIRED
	 * usage IDREF #IMPLIED
	 * variants IDREFS #IMPLIED
     * >
	 * 
	 *
	 */
	@XStreamAlias("Class")
	public static class ClamlClass extends AbstractClamlClass implements VisitableClamlModelElement {
				
		@XStreamAsAttribute
		private final String kind;
		
		@XStreamAsAttribute
		private String usage;
		
		@XStreamImplicit
		@XStreamAlias("SuperClass")
		private final List<SuperClass> superClasses = new ArrayList<SuperClass>();
		
		@XStreamImplicit
		@XStreamAlias("SubClass")
		private final List<SubClass> subClasses = new ArrayList<SubClass>();
		
		@XStreamImplicit
		@XStreamAlias("Rubric")
		private final List<Rubric> rubrics = new ArrayList<Rubric>();
		
		@XStreamImplicit
		@XStreamAlias("Meta")
		private final List<Meta> metas = new ArrayList<Meta>();
		
		@XStreamImplicit
		@XStreamConverter(ModifiedByConverter.class)
		@XStreamAlias("ModifiedBy")
		private final List<ModifiedBy> modifiedBy = new ArrayList<ModifiedBy>();
		
		@XStreamImplicit
		@XStreamAlias("ExcludeModifier")
		private final List<ExcludeModifier> excludeModifiers = new ArrayList<ExcludeModifier>();

		public ClamlClass(final String code, final String kind) {
			super(code);
			this.kind = kind;
		}
		
		public void addSuperClass(final SuperClass superClass) {
			superClasses.add(superClass);
		}
		
		public void addSubClass(final SubClass subClass) {
			subClasses.add(subClass);
		}
		
		public void addRubric(final Rubric rubric){
			rubrics.add(rubric);
		}
		
		public String getKind() {
			return kind;
		}
		
		public List<SuperClass> getSuperClasses() {
			return superClasses;
		}
		
		public List<SubClass> getSubClasses() {
			return subClasses;
		}
		
		public List<Rubric> getRubrics() {
			return rubrics;
		}
		
		public List<Meta> getMetas() {
			return metas;
		}
		
		public List<ModifiedBy> getModifiedBy() {
			return modifiedBy;
		}
		
		public List<ExcludeModifier> getExcludeModifier() {
			return excludeModifiers;
		}
		
		public String getUsage() {
			return usage;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("code", getCode())
					.add("kind", kind)
					.toString();
		}

		@Override
		public void accept(ClamlModelVisitor visitor) {
			visitor.visit(this);
			ClamlUtil.checkAndVisitAll(getSubClasses(), visitor);
			ClamlUtil.checkAndVisitAll(getSuperClasses(), visitor);
			ClamlUtil.checkAndVisitAll(getRubrics(), visitor);
			ClamlUtil.checkAndVisitAll(getModifiedBy(), visitor);
			ClamlUtil.checkAndVisitAll(getExcludeModifier(), visitor);
		}

	}
	
	/**
	 * <!ELEMENT SubClass EMPTY>
     * <!ATTLIST SubClass
	 * code NMTOKEN #REQUIRED
	 * variants IDREFS #IMPLIED
     * >
     * 
	 *
	 */
	@XStreamAlias("SubClass")
	public static class SubClass extends AbstractClamlClass implements VisitableClamlModelElement {

		public SubClass(String code) {
			super(code);
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("code", getCode()).toString();
		}

		@Override
		public void accept(ClamlModelVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	
	/**
	 * <!ELEMENT SuperClass EMPTY>
     * <!ATTLIST SuperClass
	 * code NMTOKEN #REQUIRED
	 * variants IDREFS #IMPLIED
     * >
     * 
	 *
	 */
	@XStreamAlias("SuperClass")
	public static class SuperClass extends AbstractClamlClass implements VisitableClamlModelElement {

		public SuperClass(String code) {
			super(code);
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("code", getCode()).toString();
		}

		@Override
		public void accept(ClamlModelVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	/**
	 * <!ELEMENT Rubric (
	 * Label+,
	 * History*)
	 * >
	 * <!ATTLIST Rubric
	 * id ID #IMPLIED
	 * kind IDREF #REQUIRED
	 * usage IDREF #IMPLIED
	 * >
	 * 
	 *
	 */
	@XStreamAlias("Rubric")
	public static class Rubric implements VisitableClamlModelElement {
		
		@XStreamAsAttribute
		private final String id;
		
		@XStreamAsAttribute
		private final String kind;
		
		@XStreamAsAttribute
		private final String usage;
		
		@XStreamConverter(LabelConverter.class)
		@XStreamAlias("Label")
		private final FlatLabel label;

		public Rubric(final String id, final String kind, final String usage, final FlatLabel label) {
			this.id = id;
			this.kind = kind;
			this.usage = usage;
			this.label = label;
		}
		
		public String getKind() {
			return kind;
		}
		
		public FlatLabel getLabel() {
			return label;
		}
		
		public String getUsage() {
			return usage;
		}
		
		public String getId() {
			return id;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("id", id)
					.add("kind", kind)
					.toString();
		}

		@Override
		public void accept(ClamlModelVisitor visitor) {
			visitor.visit(this);
			getLabel().accept(visitor);
		}
	}
	
	/**
	 * <!ELEMENT Label (%rubric.complex;)*>
	 * <!ATTLIST Label
	 * xml:lang NMTOKEN #REQUIRED
	 * xml:space (default|preserve) "default"
	 * variants IDREFS #IMPLIED
	 * >
	 * 
	 *
	 */
	@XStreamAlias("Label")
	@XStreamConverter(LabelConverter.class)
	public static class FlatLabel implements VisitableClamlModelElement {
		
		private final String content;	
		
		@XStreamAsAttribute
		@XStreamAlias("xml:lang")
		private String language = "en";

		public FlatLabel(final String content) {
			this.content = content;
		}		
		
		public FlatLabel(final String value, final String language) {
			this(value);
			this.language = language;
		}

		public String getContent() {
			return content;
		}
		
		public String getLanguage() {
			return language;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("language", language)
					.add("content", content)
					.toString();
		}

		@Override
		public void accept(ClamlModelVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	/**
	 * <!ELEMENT Label (%rubric.complex;)*>
	 * <!ATTLIST Label
	 * xml:lang NMTOKEN #REQUIRED
	 * xml:space (default|preserve) "default"
	 * variants IDREFS #IMPLIED
	 * >
	 * 
	 */
	public static class StructuredLabel extends MixedContentContainer {
		public final String language;
		
		public StructuredLabel(String language) {
			super(null);
			this.language = language;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("language", language).add("mixedContent", getMixedContent()).toString();
		}
	}
	
	/**
	 * <!ELEMENT Para (%rubric.simple;)*>
	 * <!ATTLIST Para
	 * class CDATA #IMPLIED
	 * >
	 * 
	 */
	public static class Para extends MixedContentContainer {

		public Para() {
			super(null);
		}
	}
	
	/**
	 * <!ELEMENT Reference (#PCDATA)>
	 * <!ATTLIST Reference
	 * 	class CDATA #IMPLIED
	 * 	authority NMTOKEN #IMPLIED
	 * 	uid NMTOKEN #IMPLIED
	 * 	code NMTOKEN #IMPLIED
	 * 	usage IDREF #IMPLIED
	 * 	variants IDREFS #IMPLIED
	 * >
	 * 
	 */
	public static class Reference {
		private final String clazz;
		private final String code;
		private final String content;
		private final String usage;
		
		public Reference(String clazz, String code, String value, String usage) {
			this.clazz = clazz;
			this.code = code;
			this.content = value;
			this.usage = usage;
		}
		
		public String getClazz() {
			return clazz;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getValue() {
			return content;
		}
		
		public String getUsage() {
			return usage;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("class", clazz)
					.add("code", code)
					.add("usage", usage)
					.add("content", content).toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((code == null) ? 0 : code.hashCode());
			result = prime * result
					+ ((content == null) ? 0 : content.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Reference other = (Reference) obj;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			if (code == null) {
				if (other.code != null)
					return false;
			} else if (!code.equals(other.code))
				return false;
			if (content == null) {
				if (other.content != null)
					return false;
			} else if (!content.equals(other.content))
				return false;
			return true;
		}
	}
	
	/**
	 * <!ELEMENT Fragment (%rubric.simple;)*>
	 * <!ATTLIST Fragment
	 * class CDATA #IMPLIED
	 * usage IDREF #IMPLIED
	 * type (item | list) "item"
	 * >
	 * 
	 */
	public static class Fragment extends MixedContentContainer {
		private final String type;
		
		public Fragment(String type, String usage) {
			super(usage);
			this.type = type;
		}
		
		public String getType() {
			return type;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("type", type)
					.add("usageKind", getUsageKind())
					.add("mixedContent", getMixedContent())
					.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + ((getUsageKind() == null) ? 0 : getUsageKind().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			Fragment other = (Fragment) obj;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			if (getUsageKind() == null) {
				if (other.getUsageKind()!= null)
					return false;
			} else if (!getUsageKind().equals(other.getUsageKind()))
				return false;
			return true;
		}
	}

	/**
	 * <!ELEMENT List (ClamlListItem+)>
	 * <!ATTLIST List
	 * class CDATA #IMPLIED
	 * >
	 * 
	 */
	public static class ClamlList {
		private final List<ClamlListItem> listItems = Lists.newArrayList();
		private final String clazz;

		public ClamlList(String clazz) {
			this.clazz = clazz;
		}
		
		public List<ClamlListItem> getListItems() {
			return listItems;
		}
		
		public String getClazz() {
			return clazz;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("class", clazz)
					.add("items", listItems)
					.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result
					+ ((listItems == null) ? 0 : listItems.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ClamlList other = (ClamlList) obj;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			if (listItems == null) {
				if (other.listItems != null)
					return false;
			} else if (!listItems.equals(other.listItems))
				return false;
			return true;
		}
	}
	
	/**
	 * <!ELEMENT ClamlListItem (
	 * %rubric.simple;
	 * | Para
	 * | Include
	 * | List
	 * | Table)*
	 * >
	 * <!ATTLIST ClamlListItem
	 * class CDATA #IMPLIED
	 * >
	 * 
	 */
	public static class ClamlListItem extends MixedContentContainer {

		public ClamlListItem() {
			super(null);
		}

	}
	
	/**
	 * <!ELEMENT Table (
	 * Caption?,
	 * THead?,
	 * TBody?,
	 * TFoot?)
	 * >
	 * <!ATTLIST Table
	 * class CDATA #IMPLIED
	 * >
	 * 
	 */
	public static class Table {
		private THead head;
		private TBody body;
		private TFoot foot;

		public THead getHead() {
			return head;
		}
		
		public void setHead(THead head) {
			this.head = head;
		}
		
		public TBody getBody() {
			return body;
		}
		
		public void setBody(TBody body) {
			this.body = body;
		}

		public TFoot getFoot() {
			return foot;
		}
		
		public void setFoot(TFoot foot) {
			this.foot = foot;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("head", head)
					.add("body", body)
					.add("foot", foot)
					.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((body == null) ? 0 : body.hashCode());
			result = prime * result + ((foot == null) ? 0 : foot.hashCode());
			result = prime * result + ((head == null) ? 0 : head.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Table other = (Table) obj;
			if (body == null) {
				if (other.body != null)
					return false;
			} else if (!body.equals(other.body))
				return false;
			if (foot == null) {
				if (other.foot != null)
					return false;
			} else if (!foot.equals(other.foot))
				return false;
			if (head == null) {
				if (other.head != null)
					return false;
			} else if (!head.equals(other.head))
				return false;
			return true;
		}
	}

	public abstract static class RowContainer {
		private final List<Row> rows = Lists.newArrayList();
		
		public List<Row> getRows() {
			return rows;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("rows", rows)
					.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((rows == null) ? 0 : rows.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RowContainer other = (RowContainer) obj;
			if (rows == null) {
				if (other.rows != null)
					return false;
			} else if (!rows.equals(other.rows))
				return false;
			return true;
		}
	}
	
	/**
	 * <!ELEMENT THead (Row+)>
	 * <!ATTLIST THead
	 * class CDATA #IMPLIED
	 * >
	 * 
	 */
	public static class THead extends RowContainer {
	}

	/**
	 * <!ELEMENT TBody (Row+)>
	 * <!ATTLIST TBody
	 * class CDATA #IMPLIED
	 * >
	 * 
	 */
	public static class TBody extends RowContainer {
	}

	/**
	 * <!ELEMENT TFoot (Row+)>
	 * <!ATTLIST TFoot
	 * class CDATA #IMPLIED
	 * >
	 * 
	 */
	public static class TFoot extends RowContainer {
	}
	
	/**
	 * <!ELEMENT Row (Cell*)>
	 * <!ATTLIST Row
	 * class CDATA #IMPLIED
	 * >
	 * 
	 */
	public static class Row {
		private final List<Cell> cells = Lists.newArrayList();
		
		public List<Cell> getCells() {
			return cells;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("cells", cells)
					.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((cells == null) ? 0 : cells.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Row other = (Row) obj;
			if (cells == null) {
				if (other.cells != null)
					return false;
			} else if (!cells.equals(other.cells))
				return false;
			return true;
		}
	}

	/**
	 * <!ELEMENT Cell (
	 * %rubric.simple;
	 * | Para
	 * | Include
	 * | List
	 * | Table)*
	 * >
	 * <!ATTLIST Cell
	 * class CDATA #IMPLIED
	 * rowSpan CDATA #IMPLIED
	 * colSpan CDATA #IMPLIED
	 * >
	 * 
	 */
	public static class Cell extends MixedContentContainer {
		private final String rowSpan;
		private final String colSpan;
		
		public Cell(String rowSpan, String colSpan) {
			super(null);
			this.rowSpan = rowSpan;
			this.colSpan = colSpan;
		}

		public String getColSpan() {
			return colSpan;
		}
		
		public String getRowSpan() {
			return rowSpan;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("rowspan", rowSpan)
					.add("colspan", colSpan)
					.add("mixedContent", getMixedContent())
					.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result
					+ ((colSpan == null) ? 0 : colSpan.hashCode());
			result = prime * result
					+ ((rowSpan == null) ? 0 : rowSpan.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			Cell other = (Cell) obj;
			if (colSpan == null) {
				if (other.colSpan != null)
					return false;
			} else if (!colSpan.equals(other.colSpan))
				return false;
			if (rowSpan == null) {
				if (other.rowSpan != null)
					return false;
			} else if (!rowSpan.equals(other.rowSpan))
				return false;
			return true;
		}
	}
	
	/**
	 * <!ELEMENT Term (#PCDATA)>
	 * <!ATTLIST Term
	 * class CDATA #IMPLIED
	 * >
	 * 	 
	 */
	public static class Term {
		private final String clazz;
		private final String content;
		
		public Term(String clazz, String content) {
			this.clazz = clazz;
			this.content = content;
		}
		
		public String getClazz() {
			return clazz;
		}
		
		public String getContent() {
			return content;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("class", clazz)
					.add("content", content)
					.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result
					+ ((content == null) ? 0 : content.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Term other = (Term) obj;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			if (content == null) {
				if (other.content != null)
					return false;
			} else if (!content.equals(other.content))
				return false;
			return true;
		}
	}
	
	public static abstract class MixedContentContainer {
		private final List<Object> mixedContent = Lists.newArrayList();
		private final String usageKind;
		
		public MixedContentContainer(String usageKind) {
			this.usageKind = usageKind;
		}
		
		public void add(Object object) {
			Preconditions.checkNotNull(object, "Mixed content item must not be null.");
			mixedContent.add(object);
		}
		
		public List<Object> getMixedContent() {
			return mixedContent;
		}
		
		public String getUsageKind() {
			return usageKind;
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("mixedContent", mixedContent).add("usageKind", usageKind).toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((mixedContent == null) ? 0 : mixedContent.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MixedContentContainer other = (MixedContentContainer) obj;
			if (mixedContent == null) {
				if (other.mixedContent != null)
					return false;
			} else if (!mixedContent.equals(other.mixedContent))
				return false;
			return true;
		}
	}


	/* ******************************** */
	/* ********* CONVERTERS *********** */
	/* ******************************** */
	@SuppressWarnings("rawtypes")
	public static class TitleConverter implements Converter {
		
	    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
	    	Title title = (Title) source;
	    	writer.addAttribute("name", title.getName());
	    	
	    	if(title.getDate() != null){
	    		writer.addAttribute("date", Dates.formatByHostTimeZone(title.getDate(), DateFormats.SHORT));
	    	}
	    	
	    	if(title.getVersion() != null){
	    		writer.addAttribute("version", title.getVersion());
	    	}
	    	    	
	        writer.setValue(title.getTitle());
	        
	    }

	    public Object unmarshal(HierarchicalStreamReader reader,
	            UnmarshallingContext context) {
	        
	    	String name = reader.getAttribute("name");
	    	String version = reader.getAttribute("version");
	    	String dateString = reader.getAttribute("date");
	    	String titleText = reader.getValue();
	    	
	    	Title title = new Title(name, titleText);
	    	
	    	if(version!=null){
	    		title.setVersion(version);
	    	}
	    	
    		if(dateString!=null){
    			title.setDate(Dates.parse(dateString, DateFormats.SHORT));
    		}
			
			return title;
	    }

	    public boolean canConvert(Class type) {
	        return type.equals(Title.class);
	    }
	}

	@SuppressWarnings("rawtypes")
	public static class LabelConverter implements Converter {
		
		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
			FlatLabel label = (FlatLabel) source;
	    	writer.addAttribute("xml:lang", label.getLanguage());
	    	writer.setValue(label.getContent());
	        
	    }

	    public Object unmarshal(HierarchicalStreamReader reader,
	            UnmarshallingContext context) {
	        
	    	String language = reader.getAttribute("xml:lang");
	    	StringBuilder xmlTextBuilder = new StringBuilder();
	    	buildString(xmlTextBuilder, reader);
	    	
	    	return new FlatLabel(xmlTextBuilder.toString(), language);
	    	
	    }

		public boolean canConvert(Class type) {
	        return type.equals(FlatLabel.class);
	    }
	    
	    private void buildString(StringBuilder stringBuilder, HierarchicalStreamReader reader) {
	    	stringBuilder.append("<" + reader.getNodeName());
	    	Iterator attributeNameIterator = reader.getAttributeNames();
	    	while (attributeNameIterator.hasNext()) {
	    		String attributeName = (String) attributeNameIterator.next();
	    		String attributeValue = reader.getAttribute(attributeName);
	    		stringBuilder.append(" " + attributeName + "=\"" + attributeValue + "\"");
	    	}
	    	stringBuilder.append(">");
	    	stringBuilder.append(reader.getValue());
	    	while (reader.hasMoreChildren()) {
	    		reader.moveDown();
	    		buildString(stringBuilder, reader);
	    		reader.moveUp();
	    		stringBuilder.append(reader.getValue());
	    	}
	    	stringBuilder.append("</" + reader.getNodeName() + ">");
	    }
	}
	
	@SuppressWarnings("rawtypes")
	public static class ModifiedByConverter implements Converter {

		@Override
		public boolean canConvert(Class type) {
			return type.equals(ModifiedBy.class);
		}

		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			ModifiedBy modifiedBy = (ModifiedBy) source;
			writer.addAttribute("code", modifiedBy.getCode());
			writer.addAttribute("all", modifiedBy.isAll() ? "true" : "false");
			for (Meta meta : modifiedBy.getMetas()) {
				context.convertAnother(meta);
			}
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			String code = reader.getAttribute("code");
			String all = reader.getAttribute("all");
			String position = reader.getAttribute("position");
			
			return new ModifiedBy(code, Boolean.valueOf(all), Integer.valueOf(position));
		}
	}
	
	public static class MixedContentContainerConverter implements Converter {

		@Override
		public boolean canConvert(Class type) {
			return MixedContentContainer.class.isAssignableFrom(type);
		}

		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			List<Object> mixedContent = Lists.newArrayList();
			while (reader.hasMoreChildren()) {
				reader.moveDown();
//				context.convertAnother(reader., arg1)
				reader.moveUp();
			}
			return mixedContent;
		}
		
	}

	@Override
	public void accept(ClamlModelVisitor visitor) {
		visitor.visit(this);
		ClamlUtil.checkAndVisitAll(getModifiers(), visitor);
		ClamlUtil.checkAndVisitAll(getModifierClasses(), visitor);
		ClamlUtil.checkAndVisitAll(getClasses(), visitor);
	}
}