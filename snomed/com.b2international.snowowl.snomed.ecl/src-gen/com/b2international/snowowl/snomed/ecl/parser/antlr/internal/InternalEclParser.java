package com.b2international.snowowl.snomed.ecl.parser.antlr.internal; 

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import com.b2international.snowowl.snomed.ecl.services.EclGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalEclParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_CARET", "RULE_PIPE", "RULE_WILDCARD", "RULE_DIGIT_NONZERO", "RULE_ZERO", "RULE_WS", "RULE_DESCENDANT_OF", "RULE_DESCENDANT_OR_SELF_OF", "RULE_AND", "RULE_OR", "RULE_NOT", "RULE_LETTER", "RULE_EQUAL", "RULE_PLUS", "RULE_CURLY_OPEN", "RULE_CURLY_CLOSE", "RULE_ROUND_OPEN", "RULE_ROUND_CLOSE", "RULE_SQUARE_OPEN", "RULE_SQUARE_CLOSE", "RULE_DOT", "RULE_COLON", "RULE_COMMA", "RULE_OTHER_CHARACTER", "RULE_ML_COMMENT", "RULE_SL_COMMENT"
    };
    public static final int RULE_DIGIT_NONZERO=7;
    public static final int RULE_CURLY_OPEN=18;
    public static final int RULE_ROUND_CLOSE=21;
    public static final int RULE_DESCENDANT_OR_SELF_OF=11;
    public static final int RULE_NOT=14;
    public static final int RULE_AND=12;
    public static final int RULE_SL_COMMENT=29;
    public static final int RULE_DESCENDANT_OF=10;
    public static final int RULE_ROUND_OPEN=20;
    public static final int RULE_OTHER_CHARACTER=27;
    public static final int RULE_PLUS=17;
    public static final int RULE_OR=13;
    public static final int RULE_DOT=24;
    public static final int EOF=-1;
    public static final int RULE_SQUARE_CLOSE=23;
    public static final int RULE_EQUAL=16;
    public static final int RULE_SQUARE_OPEN=22;
    public static final int RULE_WS=9;
    public static final int RULE_COMMA=26;
    public static final int RULE_CURLY_CLOSE=19;
    public static final int RULE_ZERO=8;
    public static final int RULE_COLON=25;
    public static final int RULE_LETTER=15;
    public static final int RULE_CARET=4;
    public static final int RULE_PIPE=5;
    public static final int RULE_ML_COMMENT=28;
    public static final int RULE_WILDCARD=6;

    // delegates
    // delegators


        public InternalEclParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalEclParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalEclParser.tokenNames; }
    public String getGrammarFileName() { return "../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g"; }



     	private EclGrammarAccess grammarAccess;
     	
        public InternalEclParser(TokenStream input, EclGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "ExpressionConstraint";	
       	}
       	
       	@Override
       	protected EclGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start "entryRuleExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:67:1: entryRuleExpressionConstraint returns [EObject current=null] : iv_ruleExpressionConstraint= ruleExpressionConstraint EOF ;
    public final EObject entryRuleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpressionConstraint = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens("RULE_WS", "RULE_SL_COMMENT", "RULE_ML_COMMENT");
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:71:2: (iv_ruleExpressionConstraint= ruleExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:72:2: iv_ruleExpressionConstraint= ruleExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleExpressionConstraint_in_entryRuleExpressionConstraint81);
            iv_ruleExpressionConstraint=ruleExpressionConstraint();

            state._fsp--;

             current =iv_ruleExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleExpressionConstraint91); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "entryRuleExpressionConstraint"


    // $ANTLR start "ruleExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:82:1: ruleExpressionConstraint returns [EObject current=null] : ( (lv_expression_0_0= ruleFocusConcept ) ) ;
    public final EObject ruleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject lv_expression_0_0 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens("RULE_WS", "RULE_SL_COMMENT", "RULE_ML_COMMENT");
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:86:28: ( ( (lv_expression_0_0= ruleFocusConcept ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:87:1: ( (lv_expression_0_0= ruleFocusConcept ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:87:1: ( (lv_expression_0_0= ruleFocusConcept ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:88:1: (lv_expression_0_0= ruleFocusConcept )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:88:1: (lv_expression_0_0= ruleFocusConcept )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:89:3: lv_expression_0_0= ruleFocusConcept
            {
             
            	        newCompositeNode(grammarAccess.getExpressionConstraintAccess().getExpressionFocusConceptParserRuleCall_0()); 
            	    
            pushFollow(FOLLOW_ruleFocusConcept_in_ruleExpressionConstraint140);
            lv_expression_0_0=ruleFocusConcept();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getExpressionConstraintRule());
            	        }
                   		set(
                   			current, 
                   			"expression",
                    		lv_expression_0_0, 
                    		"FocusConcept");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "ruleExpressionConstraint"


    // $ANTLR start "entryRuleFocusConcept"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:116:1: entryRuleFocusConcept returns [EObject current=null] : iv_ruleFocusConcept= ruleFocusConcept EOF ;
    public final EObject entryRuleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFocusConcept = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:117:2: (iv_ruleFocusConcept= ruleFocusConcept EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:118:2: iv_ruleFocusConcept= ruleFocusConcept EOF
            {
             newCompositeNode(grammarAccess.getFocusConceptRule()); 
            pushFollow(FOLLOW_ruleFocusConcept_in_entryRuleFocusConcept179);
            iv_ruleFocusConcept=ruleFocusConcept();

            state._fsp--;

             current =iv_ruleFocusConcept; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFocusConcept189); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleFocusConcept"


    // $ANTLR start "ruleFocusConcept"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:125:1: ruleFocusConcept returns [EObject current=null] : (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny ) ;
    public final EObject ruleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject this_MemberOf_0 = null;

        EObject this_ConceptReference_1 = null;

        EObject this_Any_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:128:28: ( (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:129:1: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:129:1: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny )
            int alt1=3;
            switch ( input.LA(1) ) {
            case RULE_CARET:
                {
                alt1=1;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt1=2;
                }
                break;
            case RULE_WILDCARD:
                {
                alt1=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:130:5: this_MemberOf_0= ruleMemberOf
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getMemberOfParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleMemberOf_in_ruleFocusConcept236);
                    this_MemberOf_0=ruleMemberOf();

                    state._fsp--;

                     
                            current = this_MemberOf_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:140:5: this_ConceptReference_1= ruleConceptReference
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getConceptReferenceParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleFocusConcept263);
                    this_ConceptReference_1=ruleConceptReference();

                    state._fsp--;

                     
                            current = this_ConceptReference_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:150:5: this_Any_2= ruleAny
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getAnyParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleAny_in_ruleFocusConcept290);
                    this_Any_2=ruleAny();

                    state._fsp--;

                     
                            current = this_Any_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleFocusConcept"


    // $ANTLR start "entryRuleMemberOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:166:1: entryRuleMemberOf returns [EObject current=null] : iv_ruleMemberOf= ruleMemberOf EOF ;
    public final EObject entryRuleMemberOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMemberOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:167:2: (iv_ruleMemberOf= ruleMemberOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:168:2: iv_ruleMemberOf= ruleMemberOf EOF
            {
             newCompositeNode(grammarAccess.getMemberOfRule()); 
            pushFollow(FOLLOW_ruleMemberOf_in_entryRuleMemberOf325);
            iv_ruleMemberOf=ruleMemberOf();

            state._fsp--;

             current =iv_ruleMemberOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleMemberOf335); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleMemberOf"


    // $ANTLR start "ruleMemberOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:175:1: ruleMemberOf returns [EObject current=null] : (this_CARET_0= RULE_CARET ( ( (lv_concept_1_1= ruleConceptReference | lv_concept_1_2= ruleAny ) ) ) ) ;
    public final EObject ruleMemberOf() throws RecognitionException {
        EObject current = null;

        Token this_CARET_0=null;
        EObject lv_concept_1_1 = null;

        EObject lv_concept_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:178:28: ( (this_CARET_0= RULE_CARET ( ( (lv_concept_1_1= ruleConceptReference | lv_concept_1_2= ruleAny ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:179:1: (this_CARET_0= RULE_CARET ( ( (lv_concept_1_1= ruleConceptReference | lv_concept_1_2= ruleAny ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:179:1: (this_CARET_0= RULE_CARET ( ( (lv_concept_1_1= ruleConceptReference | lv_concept_1_2= ruleAny ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:179:2: this_CARET_0= RULE_CARET ( ( (lv_concept_1_1= ruleConceptReference | lv_concept_1_2= ruleAny ) ) )
            {
            this_CARET_0=(Token)match(input,RULE_CARET,FOLLOW_RULE_CARET_in_ruleMemberOf371); 
             
                newLeafNode(this_CARET_0, grammarAccess.getMemberOfAccess().getCARETTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:183:1: ( ( (lv_concept_1_1= ruleConceptReference | lv_concept_1_2= ruleAny ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:184:1: ( (lv_concept_1_1= ruleConceptReference | lv_concept_1_2= ruleAny ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:184:1: ( (lv_concept_1_1= ruleConceptReference | lv_concept_1_2= ruleAny ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:185:1: (lv_concept_1_1= ruleConceptReference | lv_concept_1_2= ruleAny )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:185:1: (lv_concept_1_1= ruleConceptReference | lv_concept_1_2= ruleAny )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==RULE_DIGIT_NONZERO) ) {
                alt2=1;
            }
            else if ( (LA2_0==RULE_WILDCARD) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:186:3: lv_concept_1_1= ruleConceptReference
                    {
                     
                    	        newCompositeNode(grammarAccess.getMemberOfAccess().getConceptConceptReferenceParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleMemberOf393);
                    lv_concept_1_1=ruleConceptReference();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getMemberOfRule());
                    	        }
                           		set(
                           			current, 
                           			"concept",
                            		lv_concept_1_1, 
                            		"ConceptReference");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:201:8: lv_concept_1_2= ruleAny
                    {
                     
                    	        newCompositeNode(grammarAccess.getMemberOfAccess().getConceptAnyParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleAny_in_ruleMemberOf412);
                    lv_concept_1_2=ruleAny();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getMemberOfRule());
                    	        }
                           		set(
                           			current, 
                           			"concept",
                            		lv_concept_1_2, 
                            		"Any");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;

            }


            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleMemberOf"


    // $ANTLR start "entryRuleConceptReference"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:227:1: entryRuleConceptReference returns [EObject current=null] : iv_ruleConceptReference= ruleConceptReference EOF ;
    public final EObject entryRuleConceptReference() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptReference = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:228:2: (iv_ruleConceptReference= ruleConceptReference EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:229:2: iv_ruleConceptReference= ruleConceptReference EOF
            {
             newCompositeNode(grammarAccess.getConceptReferenceRule()); 
            pushFollow(FOLLOW_ruleConceptReference_in_entryRuleConceptReference451);
            iv_ruleConceptReference=ruleConceptReference();

            state._fsp--;

             current =iv_ruleConceptReference; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleConceptReference461); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleConceptReference"


    // $ANTLR start "ruleConceptReference"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:236:1: ruleConceptReference returns [EObject current=null] : ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? ) ;
    public final EObject ruleConceptReference() throws RecognitionException {
        EObject current = null;

        Token this_PIPE_1=null;
        Token this_PIPE_3=null;
        AntlrDatatypeRuleToken lv_id_0_0 = null;

        AntlrDatatypeRuleToken lv_term_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:239:28: ( ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:240:1: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:240:1: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:240:2: ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )?
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:240:2: ( (lv_id_0_0= ruleSnomedIdentifier ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:241:1: (lv_id_0_0= ruleSnomedIdentifier )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:241:1: (lv_id_0_0= ruleSnomedIdentifier )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:242:3: lv_id_0_0= ruleSnomedIdentifier
            {
             
            	        newCompositeNode(grammarAccess.getConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleSnomedIdentifier_in_ruleConceptReference507);
            lv_id_0_0=ruleSnomedIdentifier();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getConceptReferenceRule());
            	        }
                   		set(
                   			current, 
                   			"id",
                    		lv_id_0_0, 
                    		"SnomedIdentifier");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:258:2: (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==RULE_PIPE) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:258:3: this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE
                    {
                    this_PIPE_1=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConceptReference519); 
                     
                        newLeafNode(this_PIPE_1, grammarAccess.getConceptReferenceAccess().getPIPETerminalRuleCall_1_0()); 
                        
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:262:1: ( (lv_term_2_0= ruleTerm ) )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:263:1: (lv_term_2_0= ruleTerm )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:263:1: (lv_term_2_0= ruleTerm )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:264:3: lv_term_2_0= ruleTerm
                    {
                     
                    	        newCompositeNode(grammarAccess.getConceptReferenceAccess().getTermTermParserRuleCall_1_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleTerm_in_ruleConceptReference539);
                    lv_term_2_0=ruleTerm();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getConceptReferenceRule());
                    	        }
                           		set(
                           			current, 
                           			"term",
                            		lv_term_2_0, 
                            		"Term");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }

                    this_PIPE_3=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConceptReference550); 
                     
                        newLeafNode(this_PIPE_3, grammarAccess.getConceptReferenceAccess().getPIPETerminalRuleCall_1_2()); 
                        

                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleConceptReference"


    // $ANTLR start "entryRuleAny"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:292:1: entryRuleAny returns [EObject current=null] : iv_ruleAny= ruleAny EOF ;
    public final EObject entryRuleAny() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAny = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:293:2: (iv_ruleAny= ruleAny EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:294:2: iv_ruleAny= ruleAny EOF
            {
             newCompositeNode(grammarAccess.getAnyRule()); 
            pushFollow(FOLLOW_ruleAny_in_entryRuleAny587);
            iv_ruleAny=ruleAny();

            state._fsp--;

             current =iv_ruleAny; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAny597); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAny"


    // $ANTLR start "ruleAny"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:301:1: ruleAny returns [EObject current=null] : (this_WILDCARD_0= RULE_WILDCARD () ) ;
    public final EObject ruleAny() throws RecognitionException {
        EObject current = null;

        Token this_WILDCARD_0=null;

         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:304:28: ( (this_WILDCARD_0= RULE_WILDCARD () ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:305:1: (this_WILDCARD_0= RULE_WILDCARD () )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:305:1: (this_WILDCARD_0= RULE_WILDCARD () )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:305:2: this_WILDCARD_0= RULE_WILDCARD ()
            {
            this_WILDCARD_0=(Token)match(input,RULE_WILDCARD,FOLLOW_RULE_WILDCARD_in_ruleAny633); 
             
                newLeafNode(this_WILDCARD_0, grammarAccess.getAnyAccess().getWILDCARDTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:309:1: ()
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:310:5: 
            {

                    current = forceCreateModelElement(
                        grammarAccess.getAnyAccess().getAnyAction_1(),
                        current);
                

            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAny"


    // $ANTLR start "entryRuleSnomedIdentifier"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:323:1: entryRuleSnomedIdentifier returns [String current=null] : iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF ;
    public final String entryRuleSnomedIdentifier() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSnomedIdentifier = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:327:2: (iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:328:2: iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF
            {
             newCompositeNode(grammarAccess.getSnomedIdentifierRule()); 
            pushFollow(FOLLOW_ruleSnomedIdentifier_in_entryRuleSnomedIdentifier684);
            iv_ruleSnomedIdentifier=ruleSnomedIdentifier();

            state._fsp--;

             current =iv_ruleSnomedIdentifier.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSnomedIdentifier695); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "entryRuleSnomedIdentifier"


    // $ANTLR start "ruleSnomedIdentifier"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:338:1: ruleSnomedIdentifier returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) ;
    public final AntlrDatatypeRuleToken ruleSnomedIdentifier() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_DIGIT_NONZERO_0=null;
        Token this_DIGIT_NONZERO_1=null;
        Token this_ZERO_2=null;
        Token this_DIGIT_NONZERO_3=null;
        Token this_ZERO_4=null;
        Token this_DIGIT_NONZERO_5=null;
        Token this_ZERO_6=null;
        Token this_DIGIT_NONZERO_7=null;
        Token this_ZERO_8=null;
        Token this_DIGIT_NONZERO_9=null;
        Token this_ZERO_10=null;

         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:342:28: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:343:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:343:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:343:6: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            {
            this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier739); 

            		current.merge(this_DIGIT_NONZERO_0);
                
             
                newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:350:1: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==RULE_DIGIT_NONZERO) ) {
                alt4=1;
            }
            else if ( (LA4_0==RULE_ZERO) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:350:6: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier760); 

                    		current.merge(this_DIGIT_NONZERO_1);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_1_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:358:10: this_ZERO_2= RULE_ZERO
                    {
                    this_ZERO_2=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier786); 

                    		current.merge(this_ZERO_2);
                        
                     
                        newLeafNode(this_ZERO_2, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_1_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:365:2: (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==RULE_DIGIT_NONZERO) ) {
                alt5=1;
            }
            else if ( (LA5_0==RULE_ZERO) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:365:7: this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_3=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier808); 

                    		current.merge(this_DIGIT_NONZERO_3);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_2_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:373:10: this_ZERO_4= RULE_ZERO
                    {
                    this_ZERO_4=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier834); 

                    		current.merge(this_ZERO_4);
                        
                     
                        newLeafNode(this_ZERO_4, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_2_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:380:2: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==RULE_DIGIT_NONZERO) ) {
                alt6=1;
            }
            else if ( (LA6_0==RULE_ZERO) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:380:7: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier856); 

                    		current.merge(this_DIGIT_NONZERO_5);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_3_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:388:10: this_ZERO_6= RULE_ZERO
                    {
                    this_ZERO_6=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier882); 

                    		current.merge(this_ZERO_6);
                        
                     
                        newLeafNode(this_ZERO_6, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_3_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:395:2: (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==RULE_DIGIT_NONZERO) ) {
                alt7=1;
            }
            else if ( (LA7_0==RULE_ZERO) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:395:7: this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_7=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier904); 

                    		current.merge(this_DIGIT_NONZERO_7);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_4_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:403:10: this_ZERO_8= RULE_ZERO
                    {
                    this_ZERO_8=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier930); 

                    		current.merge(this_ZERO_8);
                        
                     
                        newLeafNode(this_ZERO_8, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_4_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:410:2: (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            int cnt8=0;
            loop8:
            do {
                int alt8=3;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==RULE_DIGIT_NONZERO) ) {
                    alt8=1;
                }
                else if ( (LA8_0==RULE_ZERO) ) {
                    alt8=2;
                }


                switch (alt8) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:410:7: this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_9=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier952); 

            	    		current.merge(this_DIGIT_NONZERO_9);
            	        
            	     
            	        newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_5_0()); 
            	        

            	    }
            	    break;
            	case 2 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:418:10: this_ZERO_10= RULE_ZERO
            	    {
            	    this_ZERO_10=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier978); 

            	    		current.merge(this_ZERO_10);
            	        
            	     
            	        newLeafNode(this_ZERO_10, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_5_1()); 
            	        

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "ruleSnomedIdentifier"


    // $ANTLR start "entryRuleTerm"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:436:1: entryRuleTerm returns [String current=null] : iv_ruleTerm= ruleTerm EOF ;
    public final String entryRuleTerm() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTerm = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:440:2: (iv_ruleTerm= ruleTerm EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:441:2: iv_ruleTerm= ruleTerm EOF
            {
             newCompositeNode(grammarAccess.getTermRule()); 
            pushFollow(FOLLOW_ruleTerm_in_entryRuleTerm1036);
            iv_ruleTerm=ruleTerm();

            state._fsp--;

             current =iv_ruleTerm.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTerm1047); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "entryRuleTerm"


    // $ANTLR start "ruleTerm"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:451:1: ruleTerm returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) ;
    public final AntlrDatatypeRuleToken ruleTerm() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WS_1=null;
        AntlrDatatypeRuleToken this_TermCharacter_0 = null;

        AntlrDatatypeRuleToken this_TermCharacter_2 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:455:28: ( ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:456:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:456:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:456:2: (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:456:2: (this_TermCharacter_0= ruleTermCharacter )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==RULE_CARET||(LA9_0>=RULE_DIGIT_NONZERO && LA9_0<=RULE_ZERO)||(LA9_0>=RULE_DESCENDANT_OF && LA9_0<=RULE_OTHER_CHARACTER)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:457:5: this_TermCharacter_0= ruleTermCharacter
            	    {
            	     
            	            newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_0()); 
            	        
            	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm1099);
            	    this_TermCharacter_0=ruleTermCharacter();

            	    state._fsp--;


            	    		current.merge(this_TermCharacter_0);
            	        
            	     
            	            afterParserOrEnumRuleCall();
            	        

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:467:3: ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==RULE_WS) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:467:4: (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:467:4: (this_WS_1= RULE_WS )+
            	    int cnt10=0;
            	    loop10:
            	    do {
            	        int alt10=2;
            	        int LA10_0 = input.LA(1);

            	        if ( (LA10_0==RULE_WS) ) {
            	            alt10=1;
            	        }


            	        switch (alt10) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:467:9: this_WS_1= RULE_WS
            	    	    {
            	    	    this_WS_1=(Token)match(input,RULE_WS,FOLLOW_RULE_WS_in_ruleTerm1123); 

            	    	    		current.merge(this_WS_1);
            	    	        
            	    	     
            	    	        newLeafNode(this_WS_1, grammarAccess.getTermAccess().getWSTerminalRuleCall_1_0()); 
            	    	        

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt10 >= 1 ) break loop10;
            	                EarlyExitException eee =
            	                    new EarlyExitException(10, input);
            	                throw eee;
            	        }
            	        cnt10++;
            	    } while (true);

            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:474:3: (this_TermCharacter_2= ruleTermCharacter )+
            	    int cnt11=0;
            	    loop11:
            	    do {
            	        int alt11=2;
            	        int LA11_0 = input.LA(1);

            	        if ( (LA11_0==RULE_CARET||(LA11_0>=RULE_DIGIT_NONZERO && LA11_0<=RULE_ZERO)||(LA11_0>=RULE_DESCENDANT_OF && LA11_0<=RULE_OTHER_CHARACTER)) ) {
            	            alt11=1;
            	        }


            	        switch (alt11) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:475:5: this_TermCharacter_2= ruleTermCharacter
            	    	    {
            	    	     
            	    	            newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_1_1()); 
            	    	        
            	    	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm1153);
            	    	    this_TermCharacter_2=ruleTermCharacter();

            	    	    state._fsp--;


            	    	    		current.merge(this_TermCharacter_2);
            	    	        
            	    	     
            	    	            afterParserOrEnumRuleCall();
            	    	        

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt11 >= 1 ) break loop11;
            	                EarlyExitException eee =
            	                    new EarlyExitException(11, input);
            	                throw eee;
            	        }
            	        cnt11++;
            	    } while (true);


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "ruleTerm"


    // $ANTLR start "entryRuleTermCharacter"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:496:1: entryRuleTermCharacter returns [String current=null] : iv_ruleTermCharacter= ruleTermCharacter EOF ;
    public final String entryRuleTermCharacter() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTermCharacter = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:500:2: (iv_ruleTermCharacter= ruleTermCharacter EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:501:2: iv_ruleTermCharacter= ruleTermCharacter EOF
            {
             newCompositeNode(grammarAccess.getTermCharacterRule()); 
            pushFollow(FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter1213);
            iv_ruleTermCharacter=ruleTermCharacter();

            state._fsp--;

             current =iv_ruleTermCharacter.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTermCharacter1224); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "entryRuleTermCharacter"


    // $ANTLR start "ruleTermCharacter"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:511:1: ruleTermCharacter returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DESCENDANT_OF_0= RULE_DESCENDANT_OF | this_DESCENDANT_OR_SELF_OF_1= RULE_DESCENDANT_OR_SELF_OF | this_AND_2= RULE_AND | this_OR_3= RULE_OR | this_NOT_4= RULE_NOT | this_ZERO_5= RULE_ZERO | this_DIGIT_NONZERO_6= RULE_DIGIT_NONZERO | this_LETTER_7= RULE_LETTER | this_CARET_8= RULE_CARET | this_EQUAL_9= RULE_EQUAL | this_PLUS_10= RULE_PLUS | this_CURLY_OPEN_11= RULE_CURLY_OPEN | this_CURLY_CLOSE_12= RULE_CURLY_CLOSE | this_ROUND_OPEN_13= RULE_ROUND_OPEN | this_ROUND_CLOSE_14= RULE_ROUND_CLOSE | this_SQUARE_OPEN_15= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_16= RULE_SQUARE_CLOSE | this_DOT_17= RULE_DOT | this_COLON_18= RULE_COLON | this_COMMA_19= RULE_COMMA | this_OTHER_CHARACTER_20= RULE_OTHER_CHARACTER ) ;
    public final AntlrDatatypeRuleToken ruleTermCharacter() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_DESCENDANT_OF_0=null;
        Token this_DESCENDANT_OR_SELF_OF_1=null;
        Token this_AND_2=null;
        Token this_OR_3=null;
        Token this_NOT_4=null;
        Token this_ZERO_5=null;
        Token this_DIGIT_NONZERO_6=null;
        Token this_LETTER_7=null;
        Token this_CARET_8=null;
        Token this_EQUAL_9=null;
        Token this_PLUS_10=null;
        Token this_CURLY_OPEN_11=null;
        Token this_CURLY_CLOSE_12=null;
        Token this_ROUND_OPEN_13=null;
        Token this_ROUND_CLOSE_14=null;
        Token this_SQUARE_OPEN_15=null;
        Token this_SQUARE_CLOSE_16=null;
        Token this_DOT_17=null;
        Token this_COLON_18=null;
        Token this_COMMA_19=null;
        Token this_OTHER_CHARACTER_20=null;

         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:515:28: ( (this_DESCENDANT_OF_0= RULE_DESCENDANT_OF | this_DESCENDANT_OR_SELF_OF_1= RULE_DESCENDANT_OR_SELF_OF | this_AND_2= RULE_AND | this_OR_3= RULE_OR | this_NOT_4= RULE_NOT | this_ZERO_5= RULE_ZERO | this_DIGIT_NONZERO_6= RULE_DIGIT_NONZERO | this_LETTER_7= RULE_LETTER | this_CARET_8= RULE_CARET | this_EQUAL_9= RULE_EQUAL | this_PLUS_10= RULE_PLUS | this_CURLY_OPEN_11= RULE_CURLY_OPEN | this_CURLY_CLOSE_12= RULE_CURLY_CLOSE | this_ROUND_OPEN_13= RULE_ROUND_OPEN | this_ROUND_CLOSE_14= RULE_ROUND_CLOSE | this_SQUARE_OPEN_15= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_16= RULE_SQUARE_CLOSE | this_DOT_17= RULE_DOT | this_COLON_18= RULE_COLON | this_COMMA_19= RULE_COMMA | this_OTHER_CHARACTER_20= RULE_OTHER_CHARACTER ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:516:1: (this_DESCENDANT_OF_0= RULE_DESCENDANT_OF | this_DESCENDANT_OR_SELF_OF_1= RULE_DESCENDANT_OR_SELF_OF | this_AND_2= RULE_AND | this_OR_3= RULE_OR | this_NOT_4= RULE_NOT | this_ZERO_5= RULE_ZERO | this_DIGIT_NONZERO_6= RULE_DIGIT_NONZERO | this_LETTER_7= RULE_LETTER | this_CARET_8= RULE_CARET | this_EQUAL_9= RULE_EQUAL | this_PLUS_10= RULE_PLUS | this_CURLY_OPEN_11= RULE_CURLY_OPEN | this_CURLY_CLOSE_12= RULE_CURLY_CLOSE | this_ROUND_OPEN_13= RULE_ROUND_OPEN | this_ROUND_CLOSE_14= RULE_ROUND_CLOSE | this_SQUARE_OPEN_15= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_16= RULE_SQUARE_CLOSE | this_DOT_17= RULE_DOT | this_COLON_18= RULE_COLON | this_COMMA_19= RULE_COMMA | this_OTHER_CHARACTER_20= RULE_OTHER_CHARACTER )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:516:1: (this_DESCENDANT_OF_0= RULE_DESCENDANT_OF | this_DESCENDANT_OR_SELF_OF_1= RULE_DESCENDANT_OR_SELF_OF | this_AND_2= RULE_AND | this_OR_3= RULE_OR | this_NOT_4= RULE_NOT | this_ZERO_5= RULE_ZERO | this_DIGIT_NONZERO_6= RULE_DIGIT_NONZERO | this_LETTER_7= RULE_LETTER | this_CARET_8= RULE_CARET | this_EQUAL_9= RULE_EQUAL | this_PLUS_10= RULE_PLUS | this_CURLY_OPEN_11= RULE_CURLY_OPEN | this_CURLY_CLOSE_12= RULE_CURLY_CLOSE | this_ROUND_OPEN_13= RULE_ROUND_OPEN | this_ROUND_CLOSE_14= RULE_ROUND_CLOSE | this_SQUARE_OPEN_15= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_16= RULE_SQUARE_CLOSE | this_DOT_17= RULE_DOT | this_COLON_18= RULE_COLON | this_COMMA_19= RULE_COMMA | this_OTHER_CHARACTER_20= RULE_OTHER_CHARACTER )
            int alt13=21;
            switch ( input.LA(1) ) {
            case RULE_DESCENDANT_OF:
                {
                alt13=1;
                }
                break;
            case RULE_DESCENDANT_OR_SELF_OF:
                {
                alt13=2;
                }
                break;
            case RULE_AND:
                {
                alt13=3;
                }
                break;
            case RULE_OR:
                {
                alt13=4;
                }
                break;
            case RULE_NOT:
                {
                alt13=5;
                }
                break;
            case RULE_ZERO:
                {
                alt13=6;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt13=7;
                }
                break;
            case RULE_LETTER:
                {
                alt13=8;
                }
                break;
            case RULE_CARET:
                {
                alt13=9;
                }
                break;
            case RULE_EQUAL:
                {
                alt13=10;
                }
                break;
            case RULE_PLUS:
                {
                alt13=11;
                }
                break;
            case RULE_CURLY_OPEN:
                {
                alt13=12;
                }
                break;
            case RULE_CURLY_CLOSE:
                {
                alt13=13;
                }
                break;
            case RULE_ROUND_OPEN:
                {
                alt13=14;
                }
                break;
            case RULE_ROUND_CLOSE:
                {
                alt13=15;
                }
                break;
            case RULE_SQUARE_OPEN:
                {
                alt13=16;
                }
                break;
            case RULE_SQUARE_CLOSE:
                {
                alt13=17;
                }
                break;
            case RULE_DOT:
                {
                alt13=18;
                }
                break;
            case RULE_COLON:
                {
                alt13=19;
                }
                break;
            case RULE_COMMA:
                {
                alt13=20;
                }
                break;
            case RULE_OTHER_CHARACTER:
                {
                alt13=21;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:516:6: this_DESCENDANT_OF_0= RULE_DESCENDANT_OF
                    {
                    this_DESCENDANT_OF_0=(Token)match(input,RULE_DESCENDANT_OF,FOLLOW_RULE_DESCENDANT_OF_in_ruleTermCharacter1268); 

                    		current.merge(this_DESCENDANT_OF_0);
                        
                     
                        newLeafNode(this_DESCENDANT_OF_0, grammarAccess.getTermCharacterAccess().getDESCENDANT_OFTerminalRuleCall_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:524:10: this_DESCENDANT_OR_SELF_OF_1= RULE_DESCENDANT_OR_SELF_OF
                    {
                    this_DESCENDANT_OR_SELF_OF_1=(Token)match(input,RULE_DESCENDANT_OR_SELF_OF,FOLLOW_RULE_DESCENDANT_OR_SELF_OF_in_ruleTermCharacter1294); 

                    		current.merge(this_DESCENDANT_OR_SELF_OF_1);
                        
                     
                        newLeafNode(this_DESCENDANT_OR_SELF_OF_1, grammarAccess.getTermCharacterAccess().getDESCENDANT_OR_SELF_OFTerminalRuleCall_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:532:10: this_AND_2= RULE_AND
                    {
                    this_AND_2=(Token)match(input,RULE_AND,FOLLOW_RULE_AND_in_ruleTermCharacter1320); 

                    		current.merge(this_AND_2);
                        
                     
                        newLeafNode(this_AND_2, grammarAccess.getTermCharacterAccess().getANDTerminalRuleCall_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:540:10: this_OR_3= RULE_OR
                    {
                    this_OR_3=(Token)match(input,RULE_OR,FOLLOW_RULE_OR_in_ruleTermCharacter1346); 

                    		current.merge(this_OR_3);
                        
                     
                        newLeafNode(this_OR_3, grammarAccess.getTermCharacterAccess().getORTerminalRuleCall_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:548:10: this_NOT_4= RULE_NOT
                    {
                    this_NOT_4=(Token)match(input,RULE_NOT,FOLLOW_RULE_NOT_in_ruleTermCharacter1372); 

                    		current.merge(this_NOT_4);
                        
                     
                        newLeafNode(this_NOT_4, grammarAccess.getTermCharacterAccess().getNOTTerminalRuleCall_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:556:10: this_ZERO_5= RULE_ZERO
                    {
                    this_ZERO_5=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleTermCharacter1398); 

                    		current.merge(this_ZERO_5);
                        
                     
                        newLeafNode(this_ZERO_5, grammarAccess.getTermCharacterAccess().getZEROTerminalRuleCall_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:564:10: this_DIGIT_NONZERO_6= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_6=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter1424); 

                    		current.merge(this_DIGIT_NONZERO_6);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_6, grammarAccess.getTermCharacterAccess().getDIGIT_NONZEROTerminalRuleCall_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:572:10: this_LETTER_7= RULE_LETTER
                    {
                    this_LETTER_7=(Token)match(input,RULE_LETTER,FOLLOW_RULE_LETTER_in_ruleTermCharacter1450); 

                    		current.merge(this_LETTER_7);
                        
                     
                        newLeafNode(this_LETTER_7, grammarAccess.getTermCharacterAccess().getLETTERTerminalRuleCall_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:580:10: this_CARET_8= RULE_CARET
                    {
                    this_CARET_8=(Token)match(input,RULE_CARET,FOLLOW_RULE_CARET_in_ruleTermCharacter1476); 

                    		current.merge(this_CARET_8);
                        
                     
                        newLeafNode(this_CARET_8, grammarAccess.getTermCharacterAccess().getCARETTerminalRuleCall_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:588:10: this_EQUAL_9= RULE_EQUAL
                    {
                    this_EQUAL_9=(Token)match(input,RULE_EQUAL,FOLLOW_RULE_EQUAL_in_ruleTermCharacter1502); 

                    		current.merge(this_EQUAL_9);
                        
                     
                        newLeafNode(this_EQUAL_9, grammarAccess.getTermCharacterAccess().getEQUALTerminalRuleCall_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:596:10: this_PLUS_10= RULE_PLUS
                    {
                    this_PLUS_10=(Token)match(input,RULE_PLUS,FOLLOW_RULE_PLUS_in_ruleTermCharacter1528); 

                    		current.merge(this_PLUS_10);
                        
                     
                        newLeafNode(this_PLUS_10, grammarAccess.getTermCharacterAccess().getPLUSTerminalRuleCall_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:604:10: this_CURLY_OPEN_11= RULE_CURLY_OPEN
                    {
                    this_CURLY_OPEN_11=(Token)match(input,RULE_CURLY_OPEN,FOLLOW_RULE_CURLY_OPEN_in_ruleTermCharacter1554); 

                    		current.merge(this_CURLY_OPEN_11);
                        
                     
                        newLeafNode(this_CURLY_OPEN_11, grammarAccess.getTermCharacterAccess().getCURLY_OPENTerminalRuleCall_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:612:10: this_CURLY_CLOSE_12= RULE_CURLY_CLOSE
                    {
                    this_CURLY_CLOSE_12=(Token)match(input,RULE_CURLY_CLOSE,FOLLOW_RULE_CURLY_CLOSE_in_ruleTermCharacter1580); 

                    		current.merge(this_CURLY_CLOSE_12);
                        
                     
                        newLeafNode(this_CURLY_CLOSE_12, grammarAccess.getTermCharacterAccess().getCURLY_CLOSETerminalRuleCall_12()); 
                        

                    }
                    break;
                case 14 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:620:10: this_ROUND_OPEN_13= RULE_ROUND_OPEN
                    {
                    this_ROUND_OPEN_13=(Token)match(input,RULE_ROUND_OPEN,FOLLOW_RULE_ROUND_OPEN_in_ruleTermCharacter1606); 

                    		current.merge(this_ROUND_OPEN_13);
                        
                     
                        newLeafNode(this_ROUND_OPEN_13, grammarAccess.getTermCharacterAccess().getROUND_OPENTerminalRuleCall_13()); 
                        

                    }
                    break;
                case 15 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:628:10: this_ROUND_CLOSE_14= RULE_ROUND_CLOSE
                    {
                    this_ROUND_CLOSE_14=(Token)match(input,RULE_ROUND_CLOSE,FOLLOW_RULE_ROUND_CLOSE_in_ruleTermCharacter1632); 

                    		current.merge(this_ROUND_CLOSE_14);
                        
                     
                        newLeafNode(this_ROUND_CLOSE_14, grammarAccess.getTermCharacterAccess().getROUND_CLOSETerminalRuleCall_14()); 
                        

                    }
                    break;
                case 16 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:636:10: this_SQUARE_OPEN_15= RULE_SQUARE_OPEN
                    {
                    this_SQUARE_OPEN_15=(Token)match(input,RULE_SQUARE_OPEN,FOLLOW_RULE_SQUARE_OPEN_in_ruleTermCharacter1658); 

                    		current.merge(this_SQUARE_OPEN_15);
                        
                     
                        newLeafNode(this_SQUARE_OPEN_15, grammarAccess.getTermCharacterAccess().getSQUARE_OPENTerminalRuleCall_15()); 
                        

                    }
                    break;
                case 17 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:644:10: this_SQUARE_CLOSE_16= RULE_SQUARE_CLOSE
                    {
                    this_SQUARE_CLOSE_16=(Token)match(input,RULE_SQUARE_CLOSE,FOLLOW_RULE_SQUARE_CLOSE_in_ruleTermCharacter1684); 

                    		current.merge(this_SQUARE_CLOSE_16);
                        
                     
                        newLeafNode(this_SQUARE_CLOSE_16, grammarAccess.getTermCharacterAccess().getSQUARE_CLOSETerminalRuleCall_16()); 
                        

                    }
                    break;
                case 18 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:652:10: this_DOT_17= RULE_DOT
                    {
                    this_DOT_17=(Token)match(input,RULE_DOT,FOLLOW_RULE_DOT_in_ruleTermCharacter1710); 

                    		current.merge(this_DOT_17);
                        
                     
                        newLeafNode(this_DOT_17, grammarAccess.getTermCharacterAccess().getDOTTerminalRuleCall_17()); 
                        

                    }
                    break;
                case 19 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:660:10: this_COLON_18= RULE_COLON
                    {
                    this_COLON_18=(Token)match(input,RULE_COLON,FOLLOW_RULE_COLON_in_ruleTermCharacter1736); 

                    		current.merge(this_COLON_18);
                        
                     
                        newLeafNode(this_COLON_18, grammarAccess.getTermCharacterAccess().getCOLONTerminalRuleCall_18()); 
                        

                    }
                    break;
                case 20 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:668:10: this_COMMA_19= RULE_COMMA
                    {
                    this_COMMA_19=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleTermCharacter1762); 

                    		current.merge(this_COMMA_19);
                        
                     
                        newLeafNode(this_COMMA_19, grammarAccess.getTermCharacterAccess().getCOMMATerminalRuleCall_19()); 
                        

                    }
                    break;
                case 21 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:676:10: this_OTHER_CHARACTER_20= RULE_OTHER_CHARACTER
                    {
                    this_OTHER_CHARACTER_20=(Token)match(input,RULE_OTHER_CHARACTER,FOLLOW_RULE_OTHER_CHARACTER_in_ruleTermCharacter1788); 

                    		current.merge(this_OTHER_CHARACTER_20);
                        
                     
                        newLeafNode(this_OTHER_CHARACTER_20, grammarAccess.getTermCharacterAccess().getOTHER_CHARACTERTerminalRuleCall_20()); 
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "ruleTermCharacter"

    // Delegated rules


 

    public static final BitSet FOLLOW_ruleExpressionConstraint_in_entryRuleExpressionConstraint81 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleExpressionConstraint91 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleExpressionConstraint140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_entryRuleFocusConcept179 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFocusConcept189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberOf_in_ruleFocusConcept236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleFocusConcept263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_ruleFocusConcept290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberOf_in_entryRuleMemberOf325 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMemberOf335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CARET_in_ruleMemberOf371 = new BitSet(new long[]{0x00000000000000C0L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleMemberOf393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_ruleMemberOf412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptReference_in_entryRuleConceptReference451 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleConceptReference461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSnomedIdentifier_in_ruleConceptReference507 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConceptReference519 = new BitSet(new long[]{0x000000000FFFFD90L});
    public static final BitSet FOLLOW_ruleTerm_in_ruleConceptReference539 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConceptReference550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_entryRuleAny587 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAny597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WILDCARD_in_ruleAny633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSnomedIdentifier_in_entryRuleSnomedIdentifier684 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSnomedIdentifier695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier739 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier760 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier786 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier808 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier834 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier856 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier882 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier904 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier930 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier952 = new BitSet(new long[]{0x0000000000000182L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier978 = new BitSet(new long[]{0x0000000000000182L});
    public static final BitSet FOLLOW_ruleTerm_in_entryRuleTerm1036 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTerm1047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm1099 = new BitSet(new long[]{0x000000000FFFFF92L});
    public static final BitSet FOLLOW_RULE_WS_in_ruleTerm1123 = new BitSet(new long[]{0x000000000FFFFF90L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm1153 = new BitSet(new long[]{0x000000000FFFFF92L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter1213 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTermCharacter1224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DESCENDANT_OF_in_ruleTermCharacter1268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DESCENDANT_OR_SELF_OF_in_ruleTermCharacter1294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_AND_in_ruleTermCharacter1320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OR_in_ruleTermCharacter1346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_in_ruleTermCharacter1372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleTermCharacter1398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter1424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LETTER_in_ruleTermCharacter1450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CARET_in_ruleTermCharacter1476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_EQUAL_in_ruleTermCharacter1502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_PLUS_in_ruleTermCharacter1528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CURLY_OPEN_in_ruleTermCharacter1554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CURLY_CLOSE_in_ruleTermCharacter1580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_OPEN_in_ruleTermCharacter1606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_CLOSE_in_ruleTermCharacter1632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SQUARE_OPEN_in_ruleTermCharacter1658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SQUARE_CLOSE_in_ruleTermCharacter1684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_in_ruleTermCharacter1710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COLON_in_ruleTermCharacter1736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleTermCharacter1762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OTHER_CHARACTER_in_ruleTermCharacter1788 = new BitSet(new long[]{0x0000000000000002L});

}