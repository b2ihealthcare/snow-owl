/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type.code;

import org.linuxforhealth.fhir.model.annotation.System;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.String;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

@System("urn:ietf:bcp:47")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Language extends Code {
    /**
     * Arabic
     */
    public static final Language AR = Language.builder().value(Value.AR).build();

    /**
     * Bulgarian
     */
    public static final Language BG = Language.builder().value(Value.BG).build();

    /**
     * Bulgarian (Bulgaria)
     */
    public static final Language BG_BG = Language.builder().value(Value.BG_BG).build();

    /**
     * Bengali
     */
    public static final Language BN = Language.builder().value(Value.BN).build();

    /**
     * Czech
     */
    public static final Language CS = Language.builder().value(Value.CS).build();

    /**
     * Czech (Czechia)
     */
    public static final Language CS_CZ = Language.builder().value(Value.CS_CZ).build();

    /**
     * Bosnian
     */
    public static final Language BS = Language.builder().value(Value.BS).build();

    /**
     * Bosnian (Bosnia and Herzegovina)
     */
    public static final Language BS_BA = Language.builder().value(Value.BS_BA).build();

    /**
     * Danish
     */
    public static final Language DA = Language.builder().value(Value.DA).build();

    /**
     * Danish (Denmark)
     */
    public static final Language DA_DK = Language.builder().value(Value.DA_DK).build();

    /**
     * German
     */
    public static final Language DE = Language.builder().value(Value.DE).build();

    /**
     * German (Austria)
     */
    public static final Language DE_AT = Language.builder().value(Value.DE_AT).build();

    /**
     * German (Switzerland)
     */
    public static final Language DE_CH = Language.builder().value(Value.DE_CH).build();

    /**
     * German (Germany)
     */
    public static final Language DE_DE = Language.builder().value(Value.DE_DE).build();

    /**
     * Greek
     */
    public static final Language EL = Language.builder().value(Value.EL).build();

    /**
     * Greek (Greece)
     */
    public static final Language EL_GR = Language.builder().value(Value.EL_GR).build();

    /**
     * English
     */
    public static final Language EN = Language.builder().value(Value.EN).build();

    /**
     * English (Australia)
     */
    public static final Language EN_AU = Language.builder().value(Value.EN_AU).build();

    /**
     * English (Canada)
     */
    public static final Language EN_CA = Language.builder().value(Value.EN_CA).build();

    /**
     * English (Great Britain)
     */
    public static final Language EN_GB = Language.builder().value(Value.EN_GB).build();

    /**
     * English (India)
     */
    public static final Language EN_IN = Language.builder().value(Value.EN_IN).build();

    /**
     * English (New Zealand)
     */
    public static final Language EN_NZ = Language.builder().value(Value.EN_NZ).build();

    /**
     * English (Singapore)
     */
    public static final Language EN_SG = Language.builder().value(Value.EN_SG).build();

    /**
     * English (United States)
     */
    public static final Language EN_US = Language.builder().value(Value.EN_US).build();

    /**
     * Spanish
     */
    public static final Language ES = Language.builder().value(Value.ES).build();

    /**
     * Spanish (Argentina)
     */
    public static final Language ES_AR = Language.builder().value(Value.ES_AR).build();

    /**
     * Spanish (Spain)
     */
    public static final Language ES_ES = Language.builder().value(Value.ES_ES).build();

    /**
     * Spanish (Uruguay)
     */
    public static final Language ES_UY = Language.builder().value(Value.ES_UY).build();

    /**
     * Estonian
     */
    public static final Language ET = Language.builder().value(Value.ET).build();

    /**
     * Estonian (Estonia)
     */
    public static final Language ET_EE = Language.builder().value(Value.ET_EE).build();

    /**
     * Finnish
     */
    public static final Language FI = Language.builder().value(Value.FI).build();

    /**
     * French
     */
    public static final Language FR = Language.builder().value(Value.FR).build();

    /**
     * French (Belgium)
     */
    public static final Language FR_BE = Language.builder().value(Value.FR_BE).build();

    /**
     * French (Switzerland)
     */
    public static final Language FR_CH = Language.builder().value(Value.FR_CH).build();

    /**
     * French (France)
     */
    public static final Language FR_FR = Language.builder().value(Value.FR_FR).build();

    /**
     * Finnish (Finland)
     */
    public static final Language FI_FI = Language.builder().value(Value.FI_FI).build();

    /**
     * French (Canada)
     */
    public static final Language FR_CA = Language.builder().value(Value.FR_CA).build();

    /**
     * Frisian
     */
    public static final Language FY = Language.builder().value(Value.FY).build();

    /**
     * Frisian (Netherlands)
     */
    public static final Language FY_NL = Language.builder().value(Value.FY_NL).build();

    /**
     * Hindi
     */
    public static final Language HI = Language.builder().value(Value.HI).build();

    /**
     * Croatian
     */
    public static final Language HR = Language.builder().value(Value.HR).build();

    /**
     * Croatian (Croatia)
     */
    public static final Language HR_HR = Language.builder().value(Value.HR_HR).build();

    /**
     * Icelandic
     */
    public static final Language IS = Language.builder().value(Value.IS).build();

    /**
     * Icelandic (Iceland)
     */
    public static final Language IS_IS = Language.builder().value(Value.IS_IS).build();

    /**
     * Italian
     */
    public static final Language IT = Language.builder().value(Value.IT).build();

    /**
     * Italian (Switzerland)
     */
    public static final Language IT_CH = Language.builder().value(Value.IT_CH).build();

    /**
     * Italian (Italy)
     */
    public static final Language IT_IT = Language.builder().value(Value.IT_IT).build();

    /**
     * Japanese
     */
    public static final Language JA = Language.builder().value(Value.JA).build();

    /**
     * Korean
     */
    public static final Language KO = Language.builder().value(Value.KO).build();

    /**
     * Lithuanian
     */
    public static final Language LT = Language.builder().value(Value.LT).build();

    /**
     * Lithuanian (Lithuania)
     */
    public static final Language LT_LT = Language.builder().value(Value.LT_LT).build();

    /**
     * Latvian
     */
    public static final Language LV = Language.builder().value(Value.LV).build();

    /**
     * Latvian (Latvia)
     */
    public static final Language LV_LV = Language.builder().value(Value.LV_LV).build();

    /**
     * Dutch
     */
    public static final Language NL = Language.builder().value(Value.NL).build();

    /**
     * Dutch (Belgium)
     */
    public static final Language NL_BE = Language.builder().value(Value.NL_BE).build();

    /**
     * Dutch (Netherlands)
     */
    public static final Language NL_NL = Language.builder().value(Value.NL_NL).build();

    /**
     * Norwegian
     */
    public static final Language NO = Language.builder().value(Value.NO).build();

    /**
     * Norwegian (Norway)
     */
    public static final Language NO_NO = Language.builder().value(Value.NO_NO).build();

    /**
     * Punjabi
     */
    public static final Language PA = Language.builder().value(Value.PA).build();

    /**
     * Polish
     */
    public static final Language PL = Language.builder().value(Value.PL).build();

    /**
     * Polish (Poland)
     */
    public static final Language PL_PL = Language.builder().value(Value.PL_PL).build();

    /**
     * Portuguese
     */
    public static final Language PT = Language.builder().value(Value.PT).build();

    /**
     * Portuguese (Portugal)
     */
    public static final Language PT_PT = Language.builder().value(Value.PT_PT).build();

    /**
     * Portuguese (Brazil)
     */
    public static final Language PT_BR = Language.builder().value(Value.PT_BR).build();

    /**
     * Romanian
     */
    public static final Language RO = Language.builder().value(Value.RO).build();

    /**
     * Romanian (Romania)
     */
    public static final Language RO_RO = Language.builder().value(Value.RO_RO).build();

    /**
     * Russian
     */
    public static final Language RU = Language.builder().value(Value.RU).build();

    /**
     * Russian (Russia)
     */
    public static final Language RU_RU = Language.builder().value(Value.RU_RU).build();

    /**
     * Slovakian
     */
    public static final Language SK = Language.builder().value(Value.SK).build();

    /**
     * Slovakian (Slovakia)
     */
    public static final Language SK_SK = Language.builder().value(Value.SK_SK).build();

    /**
     * Slovenian
     */
    public static final Language SL = Language.builder().value(Value.SL).build();

    /**
     * Slovenian (Slovenia)
     */
    public static final Language SL_SI = Language.builder().value(Value.SL_SI).build();

    /**
     * Serbian
     */
    public static final Language SR = Language.builder().value(Value.SR).build();

    /**
     * Serbian (Serbia)
     */
    public static final Language SR_RS = Language.builder().value(Value.SR_RS).build();

    /**
     * Swedish
     */
    public static final Language SV = Language.builder().value(Value.SV).build();

    /**
     * Swedish (Sweden)
     */
    public static final Language SV_SE = Language.builder().value(Value.SV_SE).build();

    /**
     * Telugu
     */
    public static final Language TE = Language.builder().value(Value.TE).build();

    /**
     * Chinese
     */
    public static final Language ZH = Language.builder().value(Value.ZH).build();

    /**
     * Chinese (China)
     */
    public static final Language ZH_CN = Language.builder().value(Value.ZH_CN).build();

    /**
     * Chinese (Hong Kong)
     */
    public static final Language ZH_HK = Language.builder().value(Value.ZH_HK).build();

    /**
     * Chinese (Singapore)
     */
    public static final Language ZH_SG = Language.builder().value(Value.ZH_SG).build();

    /**
     * Chinese (Taiwan)
     */
    public static final Language ZH_TW = Language.builder().value(Value.ZH_TW).build();

    private volatile int hashCode;

    private Language(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this Language as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating Language objects from a passed enum value.
     */
    public static Language of(Value value) {
        switch (value) {
        case AR:
            return AR;
        case BG:
            return BG;
        case BG_BG:
            return BG_BG;
        case BN:
            return BN;
        case CS:
            return CS;
        case CS_CZ:
            return CS_CZ;
        case BS:
            return BS;
        case BS_BA:
            return BS_BA;
        case DA:
            return DA;
        case DA_DK:
            return DA_DK;
        case DE:
            return DE;
        case DE_AT:
            return DE_AT;
        case DE_CH:
            return DE_CH;
        case DE_DE:
            return DE_DE;
        case EL:
            return EL;
        case EL_GR:
            return EL_GR;
        case EN:
            return EN;
        case EN_AU:
            return EN_AU;
        case EN_CA:
            return EN_CA;
        case EN_GB:
            return EN_GB;
        case EN_IN:
            return EN_IN;
        case EN_NZ:
            return EN_NZ;
        case EN_SG:
            return EN_SG;
        case EN_US:
            return EN_US;
        case ES:
            return ES;
        case ES_AR:
            return ES_AR;
        case ES_ES:
            return ES_ES;
        case ES_UY:
            return ES_UY;
        case ET:
            return ET;
        case ET_EE:
            return ET_EE;
        case FI:
            return FI;
        case FR:
            return FR;
        case FR_BE:
            return FR_BE;
        case FR_CH:
            return FR_CH;
        case FR_FR:
            return FR_FR;
        case FI_FI:
            return FI_FI;
        case FR_CA:
            return FR_CA;
        case FY:
            return FY;
        case FY_NL:
            return FY_NL;
        case HI:
            return HI;
        case HR:
            return HR;
        case HR_HR:
            return HR_HR;
        case IS:
            return IS;
        case IS_IS:
            return IS_IS;
        case IT:
            return IT;
        case IT_CH:
            return IT_CH;
        case IT_IT:
            return IT_IT;
        case JA:
            return JA;
        case KO:
            return KO;
        case LT:
            return LT;
        case LT_LT:
            return LT_LT;
        case LV:
            return LV;
        case LV_LV:
            return LV_LV;
        case NL:
            return NL;
        case NL_BE:
            return NL_BE;
        case NL_NL:
            return NL_NL;
        case NO:
            return NO;
        case NO_NO:
            return NO_NO;
        case PA:
            return PA;
        case PL:
            return PL;
        case PL_PL:
            return PL_PL;
        case PT:
            return PT;
        case PT_PT:
            return PT_PT;
        case PT_BR:
            return PT_BR;
        case RO:
            return RO;
        case RO_RO:
            return RO_RO;
        case RU:
            return RU;
        case RU_RU:
            return RU_RU;
        case SK:
            return SK;
        case SK_SK:
            return SK_SK;
        case SL:
            return SL;
        case SL_SI:
            return SL_SI;
        case SR:
            return SR;
        case SR_RS:
            return SR_RS;
        case SV:
            return SV;
        case SV_SE:
            return SV_SE;
        case TE:
            return TE;
        case ZH:
            return ZH;
        case ZH_CN:
            return ZH_CN;
        case ZH_HK:
            return ZH_HK;
        case ZH_SG:
            return ZH_SG;
        case ZH_TW:
            return ZH_TW;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating Language objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static Language of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating Language objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static String string(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating Language objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static Code code(java.lang.String value) {
        return of(Value.from(value));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Language other = (Language) obj;
        return Objects.equals(id, other.id) && Objects.equals(extension, other.extension) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, extension, value);
            hashCode = result;
        }
        return result;
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Code.Builder {
        private Builder() {
            super();
        }

        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder value(java.lang.String value) {
            return (value != null) ? (Builder) super.value(Value.from(value).value()) : this;
        }

        /**
         * Primitive value for code
         * 
         * @param value
         *     An enum constant for Language
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public Language build() {
            Language language = new Language(this);
            if (validating) {
                validate(language);
            }
            return language;
        }

        protected void validate(Language language) {
            super.validate(language);
        }

        protected Builder from(Language language) {
            super.from(language);
            return this;
        }
    }

    public enum Value {
        /**
         * Arabic
         */
        AR("ar"),

        /**
         * Bulgarian
         */
        BG("bg"),

        /**
         * Bulgarian (Bulgaria)
         */
        BG_BG("bg-BG"),

        /**
         * Bengali
         */
        BN("bn"),

        /**
         * Czech
         */
        CS("cs"),

        /**
         * Czech (Czechia)
         */
        CS_CZ("cs-CZ"),

        /**
         * Bosnian
         */
        BS("bs"),

        /**
         * Bosnian (Bosnia and Herzegovina)
         */
        BS_BA("bs-BA"),

        /**
         * Danish
         */
        DA("da"),

        /**
         * Danish (Denmark)
         */
        DA_DK("da-DK"),

        /**
         * German
         */
        DE("de"),

        /**
         * German (Austria)
         */
        DE_AT("de-AT"),

        /**
         * German (Switzerland)
         */
        DE_CH("de-CH"),

        /**
         * German (Germany)
         */
        DE_DE("de-DE"),

        /**
         * Greek
         */
        EL("el"),

        /**
         * Greek (Greece)
         */
        EL_GR("el-GR"),

        /**
         * English
         */
        EN("en"),

        /**
         * English (Australia)
         */
        EN_AU("en-AU"),

        /**
         * English (Canada)
         */
        EN_CA("en-CA"),

        /**
         * English (Great Britain)
         */
        EN_GB("en-GB"),

        /**
         * English (India)
         */
        EN_IN("en-IN"),

        /**
         * English (New Zealand)
         */
        EN_NZ("en-NZ"),

        /**
         * English (Singapore)
         */
        EN_SG("en-SG"),

        /**
         * English (United States)
         */
        EN_US("en-US"),

        /**
         * Spanish
         */
        ES("es"),

        /**
         * Spanish (Argentina)
         */
        ES_AR("es-AR"),

        /**
         * Spanish (Spain)
         */
        ES_ES("es-ES"),

        /**
         * Spanish (Uruguay)
         */
        ES_UY("es-UY"),

        /**
         * Estonian
         */
        ET("et"),

        /**
         * Estonian (Estonia)
         */
        ET_EE("et-EE"),

        /**
         * Finnish
         */
        FI("fi"),

        /**
         * French
         */
        FR("fr"),

        /**
         * French (Belgium)
         */
        FR_BE("fr-BE"),

        /**
         * French (Switzerland)
         */
        FR_CH("fr-CH"),

        /**
         * French (France)
         */
        FR_FR("fr-FR"),

        /**
         * Finnish (Finland)
         */
        FI_FI("fi-FI"),

        /**
         * French (Canada)
         */
        FR_CA("fr-CA"),

        /**
         * Frisian
         */
        FY("fy"),

        /**
         * Frisian (Netherlands)
         */
        FY_NL("fy-NL"),

        /**
         * Hindi
         */
        HI("hi"),

        /**
         * Croatian
         */
        HR("hr"),

        /**
         * Croatian (Croatia)
         */
        HR_HR("hr-HR"),

        /**
         * Icelandic
         */
        IS("is"),

        /**
         * Icelandic (Iceland)
         */
        IS_IS("is-IS"),

        /**
         * Italian
         */
        IT("it"),

        /**
         * Italian (Switzerland)
         */
        IT_CH("it-CH"),

        /**
         * Italian (Italy)
         */
        IT_IT("it-IT"),

        /**
         * Japanese
         */
        JA("ja"),

        /**
         * Korean
         */
        KO("ko"),

        /**
         * Lithuanian
         */
        LT("lt"),

        /**
         * Lithuanian (Lithuania)
         */
        LT_LT("lt-LT"),

        /**
         * Latvian
         */
        LV("lv"),

        /**
         * Latvian (Latvia)
         */
        LV_LV("lv-LV"),

        /**
         * Dutch
         */
        NL("nl"),

        /**
         * Dutch (Belgium)
         */
        NL_BE("nl-BE"),

        /**
         * Dutch (Netherlands)
         */
        NL_NL("nl-NL"),

        /**
         * Norwegian
         */
        NO("no"),

        /**
         * Norwegian (Norway)
         */
        NO_NO("no-NO"),

        /**
         * Punjabi
         */
        PA("pa"),

        /**
         * Polish
         */
        PL("pl"),

        /**
         * Polish (Poland)
         */
        PL_PL("pl-PL"),

        /**
         * Portuguese
         */
        PT("pt"),

        /**
         * Portuguese (Portugal)
         */
        PT_PT("pt-PT"),

        /**
         * Portuguese (Brazil)
         */
        PT_BR("pt-BR"),

        /**
         * Romanian
         */
        RO("ro"),

        /**
         * Romanian (Romania)
         */
        RO_RO("ro-RO"),

        /**
         * Russian
         */
        RU("ru"),

        /**
         * Russian (Russia)
         */
        RU_RU("ru-RU"),

        /**
         * Slovakian
         */
        SK("sk"),

        /**
         * Slovakian (Slovakia)
         */
        SK_SK("sk-SK"),

        /**
         * Slovenian
         */
        SL("sl"),

        /**
         * Slovenian (Slovenia)
         */
        SL_SI("sl-SI"),

        /**
         * Serbian
         */
        SR("sr"),

        /**
         * Serbian (Serbia)
         */
        SR_RS("sr-RS"),

        /**
         * Swedish
         */
        SV("sv"),

        /**
         * Swedish (Sweden)
         */
        SV_SE("sv-SE"),

        /**
         * Telugu
         */
        TE("te"),

        /**
         * Chinese
         */
        ZH("zh"),

        /**
         * Chinese (China)
         */
        ZH_CN("zh-CN"),

        /**
         * Chinese (Hong Kong)
         */
        ZH_HK("zh-HK"),

        /**
         * Chinese (Singapore)
         */
        ZH_SG("zh-SG"),

        /**
         * Chinese (Taiwan)
         */
        ZH_TW("zh-TW");

        private final java.lang.String value;

        Value(java.lang.String value) {
            this.value = value;
        }

        /**
         * @return
         *     The java.lang.String value of the code represented by this enum
         */
        public java.lang.String value() {
            return value;
        }

        /**
         * Factory method for creating Language.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding Language.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "ar":
                return AR;
            case "bg":
                return BG;
            case "bg-BG":
                return BG_BG;
            case "bn":
                return BN;
            case "cs":
                return CS;
            case "cs-CZ":
                return CS_CZ;
            case "bs":
                return BS;
            case "bs-BA":
                return BS_BA;
            case "da":
                return DA;
            case "da-DK":
                return DA_DK;
            case "de":
                return DE;
            case "de-AT":
                return DE_AT;
            case "de-CH":
                return DE_CH;
            case "de-DE":
                return DE_DE;
            case "el":
                return EL;
            case "el-GR":
                return EL_GR;
            case "en":
                return EN;
            case "en-AU":
                return EN_AU;
            case "en-CA":
                return EN_CA;
            case "en-GB":
                return EN_GB;
            case "en-IN":
                return EN_IN;
            case "en-NZ":
                return EN_NZ;
            case "en-SG":
                return EN_SG;
            case "en-US":
                return EN_US;
            case "es":
                return ES;
            case "es-AR":
                return ES_AR;
            case "es-ES":
                return ES_ES;
            case "es-UY":
                return ES_UY;
            case "et":
                return ET;
            case "et-EE":
                return ET_EE;
            case "fi":
                return FI;
            case "fr":
                return FR;
            case "fr-BE":
                return FR_BE;
            case "fr-CH":
                return FR_CH;
            case "fr-FR":
                return FR_FR;
            case "fi-FI":
                return FI_FI;
            case "fr-CA":
                return FR_CA;
            case "fy":
                return FY;
            case "fy-NL":
                return FY_NL;
            case "hi":
                return HI;
            case "hr":
                return HR;
            case "hr-HR":
                return HR_HR;
            case "is":
                return IS;
            case "is-IS":
                return IS_IS;
            case "it":
                return IT;
            case "it-CH":
                return IT_CH;
            case "it-IT":
                return IT_IT;
            case "ja":
                return JA;
            case "ko":
                return KO;
            case "lt":
                return LT;
            case "lt-LT":
                return LT_LT;
            case "lv":
                return LV;
            case "lv-LV":
                return LV_LV;
            case "nl":
                return NL;
            case "nl-BE":
                return NL_BE;
            case "nl-NL":
                return NL_NL;
            case "no":
                return NO;
            case "no-NO":
                return NO_NO;
            case "pa":
                return PA;
            case "pl":
                return PL;
            case "pl-PL":
                return PL_PL;
            case "pt":
                return PT;
            case "pt-PT":
                return PT_PT;
            case "pt-BR":
                return PT_BR;
            case "ro":
                return RO;
            case "ro-RO":
                return RO_RO;
            case "ru":
                return RU;
            case "ru-RU":
                return RU_RU;
            case "sk":
                return SK;
            case "sk-SK":
                return SK_SK;
            case "sl":
                return SL;
            case "sl-SI":
                return SL_SI;
            case "sr":
                return SR;
            case "sr-RS":
                return SR_RS;
            case "sv":
                return SV;
            case "sv-SE":
                return SV_SE;
            case "te":
                return TE;
            case "zh":
                return ZH;
            case "zh-CN":
                return ZH_CN;
            case "zh-HK":
                return ZH_HK;
            case "zh-SG":
                return ZH_SG;
            case "zh-TW":
                return ZH_TW;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
