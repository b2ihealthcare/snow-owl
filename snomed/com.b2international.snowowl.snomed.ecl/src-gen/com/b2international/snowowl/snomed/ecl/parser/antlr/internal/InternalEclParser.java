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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_PIPE", "RULE_DIGIT_NONZERO", "RULE_ZERO", "RULE_WS", "RULE_DESCENDANT_OF", "RULE_DESCENDANT_OR_SELF_OF", "RULE_AND", "RULE_OR", "RULE_NOT", "RULE_LETTER", "RULE_CARET", "RULE_EQUAL", "RULE_PLUS", "RULE_CURLY_OPEN", "RULE_CURLY_CLOSE", "RULE_ROUND_OPEN", "RULE_ROUND_CLOSE", "RULE_SQUARE_OPEN", "RULE_SQUARE_CLOSE", "RULE_DOT", "RULE_COLON", "RULE_COMMA", "RULE_OTHER_CHARACTER", "RULE_WILDCARD", "RULE_ML_COMMENT", "RULE_SL_COMMENT"
    };
    public static final int RULE_DIGIT_NONZERO=5;
    public static final int RULE_CURLY_OPEN=17;
    public static final int RULE_ROUND_CLOSE=20;
    public static final int RULE_DESCENDANT_OR_SELF_OF=9;
    public static final int RULE_NOT=12;
    public static final int RULE_AND=10;
    public static final int RULE_SL_COMMENT=29;
    public static final int RULE_DESCENDANT_OF=8;
    public static final int RULE_ROUND_OPEN=19;
    public static final int RULE_OTHER_CHARACTER=26;
    public static final int RULE_PLUS=16;
    public static final int RULE_OR=11;
    public static final int RULE_DOT=23;
    public static final int EOF=-1;
    public static final int RULE_SQUARE_CLOSE=22;
    public static final int RULE_EQUAL=15;
    public static final int RULE_SQUARE_OPEN=21;
    public static final int RULE_WS=7;
    public static final int RULE_COMMA=25;
    public static final int RULE_CURLY_CLOSE=18;
    public static final int RULE_ZERO=6;
    public static final int RULE_COLON=24;
    public static final int RULE_LETTER=13;
    public static final int RULE_CARET=14;
    public static final int RULE_PIPE=4;
    public static final int RULE_ML_COMMENT=28;
    public static final int RULE_WILDCARD=27;

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:82:1: ruleExpressionConstraint returns [EObject current=null] : ( (lv_expression_0_0= ruleConceptReference ) ) ;
    public final EObject ruleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject lv_expression_0_0 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens("RULE_WS", "RULE_SL_COMMENT", "RULE_ML_COMMENT");
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:86:28: ( ( (lv_expression_0_0= ruleConceptReference ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:87:1: ( (lv_expression_0_0= ruleConceptReference ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:87:1: ( (lv_expression_0_0= ruleConceptReference ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:88:1: (lv_expression_0_0= ruleConceptReference )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:88:1: (lv_expression_0_0= ruleConceptReference )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:89:3: lv_expression_0_0= ruleConceptReference
            {
             
            	        newCompositeNode(grammarAccess.getExpressionConstraintAccess().getExpressionConceptReferenceParserRuleCall_0()); 
            	    
            pushFollow(FOLLOW_ruleConceptReference_in_ruleExpressionConstraint140);
            lv_expression_0_0=ruleConceptReference();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getExpressionConstraintRule());
            	        }
                   		set(
                   			current, 
                   			"expression",
                    		lv_expression_0_0, 
                    		"ConceptReference");
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


    // $ANTLR start "entryRuleConceptReference"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:116:1: entryRuleConceptReference returns [EObject current=null] : iv_ruleConceptReference= ruleConceptReference EOF ;
    public final EObject entryRuleConceptReference() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptReference = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:117:2: (iv_ruleConceptReference= ruleConceptReference EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:118:2: iv_ruleConceptReference= ruleConceptReference EOF
            {
             newCompositeNode(grammarAccess.getConceptReferenceRule()); 
            pushFollow(FOLLOW_ruleConceptReference_in_entryRuleConceptReference179);
            iv_ruleConceptReference=ruleConceptReference();

            state._fsp--;

             current =iv_ruleConceptReference; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleConceptReference189); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:125:1: ruleConceptReference returns [EObject current=null] : ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? ) ;
    public final EObject ruleConceptReference() throws RecognitionException {
        EObject current = null;

        Token this_PIPE_1=null;
        Token this_PIPE_3=null;
        AntlrDatatypeRuleToken lv_id_0_0 = null;

        AntlrDatatypeRuleToken lv_term_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:128:28: ( ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:129:1: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:129:1: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:129:2: ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )?
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:129:2: ( (lv_id_0_0= ruleSnomedIdentifier ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:130:1: (lv_id_0_0= ruleSnomedIdentifier )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:130:1: (lv_id_0_0= ruleSnomedIdentifier )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:131:3: lv_id_0_0= ruleSnomedIdentifier
            {
             
            	        newCompositeNode(grammarAccess.getConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleSnomedIdentifier_in_ruleConceptReference235);
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

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:147:2: (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==RULE_PIPE) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:147:3: this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE
                    {
                    this_PIPE_1=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConceptReference247); 
                     
                        newLeafNode(this_PIPE_1, grammarAccess.getConceptReferenceAccess().getPIPETerminalRuleCall_1_0()); 
                        
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:151:1: ( (lv_term_2_0= ruleTerm ) )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:152:1: (lv_term_2_0= ruleTerm )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:152:1: (lv_term_2_0= ruleTerm )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:153:3: lv_term_2_0= ruleTerm
                    {
                     
                    	        newCompositeNode(grammarAccess.getConceptReferenceAccess().getTermTermParserRuleCall_1_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleTerm_in_ruleConceptReference267);
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

                    this_PIPE_3=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConceptReference278); 
                     
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


    // $ANTLR start "entryRuleSnomedIdentifier"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:181:1: entryRuleSnomedIdentifier returns [String current=null] : iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF ;
    public final String entryRuleSnomedIdentifier() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSnomedIdentifier = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:185:2: (iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:186:2: iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF
            {
             newCompositeNode(grammarAccess.getSnomedIdentifierRule()); 
            pushFollow(FOLLOW_ruleSnomedIdentifier_in_entryRuleSnomedIdentifier322);
            iv_ruleSnomedIdentifier=ruleSnomedIdentifier();

            state._fsp--;

             current =iv_ruleSnomedIdentifier.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSnomedIdentifier333); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:196:1: ruleSnomedIdentifier returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) ;
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
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:200:28: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:201:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:201:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:201:6: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            {
            this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier377); 

            		current.merge(this_DIGIT_NONZERO_0);
                
             
                newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:208:1: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==RULE_DIGIT_NONZERO) ) {
                alt2=1;
            }
            else if ( (LA2_0==RULE_ZERO) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:208:6: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier398); 

                    		current.merge(this_DIGIT_NONZERO_1);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_1_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:216:10: this_ZERO_2= RULE_ZERO
                    {
                    this_ZERO_2=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier424); 

                    		current.merge(this_ZERO_2);
                        
                     
                        newLeafNode(this_ZERO_2, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_1_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:223:2: (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==RULE_DIGIT_NONZERO) ) {
                alt3=1;
            }
            else if ( (LA3_0==RULE_ZERO) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:223:7: this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_3=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier446); 

                    		current.merge(this_DIGIT_NONZERO_3);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_2_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:231:10: this_ZERO_4= RULE_ZERO
                    {
                    this_ZERO_4=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier472); 

                    		current.merge(this_ZERO_4);
                        
                     
                        newLeafNode(this_ZERO_4, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_2_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:238:2: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:238:7: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier494); 

                    		current.merge(this_DIGIT_NONZERO_5);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_3_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:246:10: this_ZERO_6= RULE_ZERO
                    {
                    this_ZERO_6=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier520); 

                    		current.merge(this_ZERO_6);
                        
                     
                        newLeafNode(this_ZERO_6, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_3_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:253:2: (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO )
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:253:7: this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_7=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier542); 

                    		current.merge(this_DIGIT_NONZERO_7);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_4_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:261:10: this_ZERO_8= RULE_ZERO
                    {
                    this_ZERO_8=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier568); 

                    		current.merge(this_ZERO_8);
                        
                     
                        newLeafNode(this_ZERO_8, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_4_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:268:2: (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            int cnt6=0;
            loop6:
            do {
                int alt6=3;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==RULE_DIGIT_NONZERO) ) {
                    alt6=1;
                }
                else if ( (LA6_0==RULE_ZERO) ) {
                    alt6=2;
                }


                switch (alt6) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:268:7: this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_9=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier590); 

            	    		current.merge(this_DIGIT_NONZERO_9);
            	        
            	     
            	        newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_5_0()); 
            	        

            	    }
            	    break;
            	case 2 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:276:10: this_ZERO_10= RULE_ZERO
            	    {
            	    this_ZERO_10=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier616); 

            	    		current.merge(this_ZERO_10);
            	        
            	     
            	        newLeafNode(this_ZERO_10, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_5_1()); 
            	        

            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:294:1: entryRuleTerm returns [String current=null] : iv_ruleTerm= ruleTerm EOF ;
    public final String entryRuleTerm() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTerm = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:298:2: (iv_ruleTerm= ruleTerm EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:299:2: iv_ruleTerm= ruleTerm EOF
            {
             newCompositeNode(grammarAccess.getTermRule()); 
            pushFollow(FOLLOW_ruleTerm_in_entryRuleTerm674);
            iv_ruleTerm=ruleTerm();

            state._fsp--;

             current =iv_ruleTerm.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTerm685); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:309:1: ruleTerm returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) ;
    public final AntlrDatatypeRuleToken ruleTerm() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WS_1=null;
        AntlrDatatypeRuleToken this_TermCharacter_0 = null;

        AntlrDatatypeRuleToken this_TermCharacter_2 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:313:28: ( ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:314:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:314:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:314:2: (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:314:2: (this_TermCharacter_0= ruleTermCharacter )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>=RULE_DIGIT_NONZERO && LA7_0<=RULE_ZERO)||(LA7_0>=RULE_DESCENDANT_OF && LA7_0<=RULE_OTHER_CHARACTER)) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:315:5: this_TermCharacter_0= ruleTermCharacter
            	    {
            	     
            	            newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_0()); 
            	        
            	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm737);
            	    this_TermCharacter_0=ruleTermCharacter();

            	    state._fsp--;


            	    		current.merge(this_TermCharacter_0);
            	        
            	     
            	            afterParserOrEnumRuleCall();
            	        

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:325:3: ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==RULE_WS) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:325:4: (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:325:4: (this_WS_1= RULE_WS )+
            	    int cnt8=0;
            	    loop8:
            	    do {
            	        int alt8=2;
            	        int LA8_0 = input.LA(1);

            	        if ( (LA8_0==RULE_WS) ) {
            	            alt8=1;
            	        }


            	        switch (alt8) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:325:9: this_WS_1= RULE_WS
            	    	    {
            	    	    this_WS_1=(Token)match(input,RULE_WS,FOLLOW_RULE_WS_in_ruleTerm761); 

            	    	    		current.merge(this_WS_1);
            	    	        
            	    	     
            	    	        newLeafNode(this_WS_1, grammarAccess.getTermAccess().getWSTerminalRuleCall_1_0()); 
            	    	        

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

            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:332:3: (this_TermCharacter_2= ruleTermCharacter )+
            	    int cnt9=0;
            	    loop9:
            	    do {
            	        int alt9=2;
            	        int LA9_0 = input.LA(1);

            	        if ( ((LA9_0>=RULE_DIGIT_NONZERO && LA9_0<=RULE_ZERO)||(LA9_0>=RULE_DESCENDANT_OF && LA9_0<=RULE_OTHER_CHARACTER)) ) {
            	            alt9=1;
            	        }


            	        switch (alt9) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:333:5: this_TermCharacter_2= ruleTermCharacter
            	    	    {
            	    	     
            	    	            newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_1_1()); 
            	    	        
            	    	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm791);
            	    	    this_TermCharacter_2=ruleTermCharacter();

            	    	    state._fsp--;


            	    	    		current.merge(this_TermCharacter_2);
            	    	        
            	    	     
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


            	    }
            	    break;

            	default :
            	    break loop10;
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:354:1: entryRuleTermCharacter returns [String current=null] : iv_ruleTermCharacter= ruleTermCharacter EOF ;
    public final String entryRuleTermCharacter() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTermCharacter = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:358:2: (iv_ruleTermCharacter= ruleTermCharacter EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:359:2: iv_ruleTermCharacter= ruleTermCharacter EOF
            {
             newCompositeNode(grammarAccess.getTermCharacterRule()); 
            pushFollow(FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter851);
            iv_ruleTermCharacter=ruleTermCharacter();

            state._fsp--;

             current =iv_ruleTermCharacter.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTermCharacter862); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:369:1: ruleTermCharacter returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DESCENDANT_OF_0= RULE_DESCENDANT_OF | this_DESCENDANT_OR_SELF_OF_1= RULE_DESCENDANT_OR_SELF_OF | this_AND_2= RULE_AND | this_OR_3= RULE_OR | this_NOT_4= RULE_NOT | this_ZERO_5= RULE_ZERO | this_DIGIT_NONZERO_6= RULE_DIGIT_NONZERO | this_LETTER_7= RULE_LETTER | this_CARET_8= RULE_CARET | this_EQUAL_9= RULE_EQUAL | this_PLUS_10= RULE_PLUS | this_CURLY_OPEN_11= RULE_CURLY_OPEN | this_CURLY_CLOSE_12= RULE_CURLY_CLOSE | this_ROUND_OPEN_13= RULE_ROUND_OPEN | this_ROUND_CLOSE_14= RULE_ROUND_CLOSE | this_SQUARE_OPEN_15= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_16= RULE_SQUARE_CLOSE | this_DOT_17= RULE_DOT | this_COLON_18= RULE_COLON | this_COMMA_19= RULE_COMMA | this_OTHER_CHARACTER_20= RULE_OTHER_CHARACTER ) ;
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
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:373:28: ( (this_DESCENDANT_OF_0= RULE_DESCENDANT_OF | this_DESCENDANT_OR_SELF_OF_1= RULE_DESCENDANT_OR_SELF_OF | this_AND_2= RULE_AND | this_OR_3= RULE_OR | this_NOT_4= RULE_NOT | this_ZERO_5= RULE_ZERO | this_DIGIT_NONZERO_6= RULE_DIGIT_NONZERO | this_LETTER_7= RULE_LETTER | this_CARET_8= RULE_CARET | this_EQUAL_9= RULE_EQUAL | this_PLUS_10= RULE_PLUS | this_CURLY_OPEN_11= RULE_CURLY_OPEN | this_CURLY_CLOSE_12= RULE_CURLY_CLOSE | this_ROUND_OPEN_13= RULE_ROUND_OPEN | this_ROUND_CLOSE_14= RULE_ROUND_CLOSE | this_SQUARE_OPEN_15= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_16= RULE_SQUARE_CLOSE | this_DOT_17= RULE_DOT | this_COLON_18= RULE_COLON | this_COMMA_19= RULE_COMMA | this_OTHER_CHARACTER_20= RULE_OTHER_CHARACTER ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:374:1: (this_DESCENDANT_OF_0= RULE_DESCENDANT_OF | this_DESCENDANT_OR_SELF_OF_1= RULE_DESCENDANT_OR_SELF_OF | this_AND_2= RULE_AND | this_OR_3= RULE_OR | this_NOT_4= RULE_NOT | this_ZERO_5= RULE_ZERO | this_DIGIT_NONZERO_6= RULE_DIGIT_NONZERO | this_LETTER_7= RULE_LETTER | this_CARET_8= RULE_CARET | this_EQUAL_9= RULE_EQUAL | this_PLUS_10= RULE_PLUS | this_CURLY_OPEN_11= RULE_CURLY_OPEN | this_CURLY_CLOSE_12= RULE_CURLY_CLOSE | this_ROUND_OPEN_13= RULE_ROUND_OPEN | this_ROUND_CLOSE_14= RULE_ROUND_CLOSE | this_SQUARE_OPEN_15= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_16= RULE_SQUARE_CLOSE | this_DOT_17= RULE_DOT | this_COLON_18= RULE_COLON | this_COMMA_19= RULE_COMMA | this_OTHER_CHARACTER_20= RULE_OTHER_CHARACTER )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:374:1: (this_DESCENDANT_OF_0= RULE_DESCENDANT_OF | this_DESCENDANT_OR_SELF_OF_1= RULE_DESCENDANT_OR_SELF_OF | this_AND_2= RULE_AND | this_OR_3= RULE_OR | this_NOT_4= RULE_NOT | this_ZERO_5= RULE_ZERO | this_DIGIT_NONZERO_6= RULE_DIGIT_NONZERO | this_LETTER_7= RULE_LETTER | this_CARET_8= RULE_CARET | this_EQUAL_9= RULE_EQUAL | this_PLUS_10= RULE_PLUS | this_CURLY_OPEN_11= RULE_CURLY_OPEN | this_CURLY_CLOSE_12= RULE_CURLY_CLOSE | this_ROUND_OPEN_13= RULE_ROUND_OPEN | this_ROUND_CLOSE_14= RULE_ROUND_CLOSE | this_SQUARE_OPEN_15= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_16= RULE_SQUARE_CLOSE | this_DOT_17= RULE_DOT | this_COLON_18= RULE_COLON | this_COMMA_19= RULE_COMMA | this_OTHER_CHARACTER_20= RULE_OTHER_CHARACTER )
            int alt11=21;
            switch ( input.LA(1) ) {
            case RULE_DESCENDANT_OF:
                {
                alt11=1;
                }
                break;
            case RULE_DESCENDANT_OR_SELF_OF:
                {
                alt11=2;
                }
                break;
            case RULE_AND:
                {
                alt11=3;
                }
                break;
            case RULE_OR:
                {
                alt11=4;
                }
                break;
            case RULE_NOT:
                {
                alt11=5;
                }
                break;
            case RULE_ZERO:
                {
                alt11=6;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt11=7;
                }
                break;
            case RULE_LETTER:
                {
                alt11=8;
                }
                break;
            case RULE_CARET:
                {
                alt11=9;
                }
                break;
            case RULE_EQUAL:
                {
                alt11=10;
                }
                break;
            case RULE_PLUS:
                {
                alt11=11;
                }
                break;
            case RULE_CURLY_OPEN:
                {
                alt11=12;
                }
                break;
            case RULE_CURLY_CLOSE:
                {
                alt11=13;
                }
                break;
            case RULE_ROUND_OPEN:
                {
                alt11=14;
                }
                break;
            case RULE_ROUND_CLOSE:
                {
                alt11=15;
                }
                break;
            case RULE_SQUARE_OPEN:
                {
                alt11=16;
                }
                break;
            case RULE_SQUARE_CLOSE:
                {
                alt11=17;
                }
                break;
            case RULE_DOT:
                {
                alt11=18;
                }
                break;
            case RULE_COLON:
                {
                alt11=19;
                }
                break;
            case RULE_COMMA:
                {
                alt11=20;
                }
                break;
            case RULE_OTHER_CHARACTER:
                {
                alt11=21;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:374:6: this_DESCENDANT_OF_0= RULE_DESCENDANT_OF
                    {
                    this_DESCENDANT_OF_0=(Token)match(input,RULE_DESCENDANT_OF,FOLLOW_RULE_DESCENDANT_OF_in_ruleTermCharacter906); 

                    		current.merge(this_DESCENDANT_OF_0);
                        
                     
                        newLeafNode(this_DESCENDANT_OF_0, grammarAccess.getTermCharacterAccess().getDESCENDANT_OFTerminalRuleCall_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:382:10: this_DESCENDANT_OR_SELF_OF_1= RULE_DESCENDANT_OR_SELF_OF
                    {
                    this_DESCENDANT_OR_SELF_OF_1=(Token)match(input,RULE_DESCENDANT_OR_SELF_OF,FOLLOW_RULE_DESCENDANT_OR_SELF_OF_in_ruleTermCharacter932); 

                    		current.merge(this_DESCENDANT_OR_SELF_OF_1);
                        
                     
                        newLeafNode(this_DESCENDANT_OR_SELF_OF_1, grammarAccess.getTermCharacterAccess().getDESCENDANT_OR_SELF_OFTerminalRuleCall_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:390:10: this_AND_2= RULE_AND
                    {
                    this_AND_2=(Token)match(input,RULE_AND,FOLLOW_RULE_AND_in_ruleTermCharacter958); 

                    		current.merge(this_AND_2);
                        
                     
                        newLeafNode(this_AND_2, grammarAccess.getTermCharacterAccess().getANDTerminalRuleCall_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:398:10: this_OR_3= RULE_OR
                    {
                    this_OR_3=(Token)match(input,RULE_OR,FOLLOW_RULE_OR_in_ruleTermCharacter984); 

                    		current.merge(this_OR_3);
                        
                     
                        newLeafNode(this_OR_3, grammarAccess.getTermCharacterAccess().getORTerminalRuleCall_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:406:10: this_NOT_4= RULE_NOT
                    {
                    this_NOT_4=(Token)match(input,RULE_NOT,FOLLOW_RULE_NOT_in_ruleTermCharacter1010); 

                    		current.merge(this_NOT_4);
                        
                     
                        newLeafNode(this_NOT_4, grammarAccess.getTermCharacterAccess().getNOTTerminalRuleCall_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:414:10: this_ZERO_5= RULE_ZERO
                    {
                    this_ZERO_5=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleTermCharacter1036); 

                    		current.merge(this_ZERO_5);
                        
                     
                        newLeafNode(this_ZERO_5, grammarAccess.getTermCharacterAccess().getZEROTerminalRuleCall_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:422:10: this_DIGIT_NONZERO_6= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_6=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter1062); 

                    		current.merge(this_DIGIT_NONZERO_6);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_6, grammarAccess.getTermCharacterAccess().getDIGIT_NONZEROTerminalRuleCall_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:430:10: this_LETTER_7= RULE_LETTER
                    {
                    this_LETTER_7=(Token)match(input,RULE_LETTER,FOLLOW_RULE_LETTER_in_ruleTermCharacter1088); 

                    		current.merge(this_LETTER_7);
                        
                     
                        newLeafNode(this_LETTER_7, grammarAccess.getTermCharacterAccess().getLETTERTerminalRuleCall_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:438:10: this_CARET_8= RULE_CARET
                    {
                    this_CARET_8=(Token)match(input,RULE_CARET,FOLLOW_RULE_CARET_in_ruleTermCharacter1114); 

                    		current.merge(this_CARET_8);
                        
                     
                        newLeafNode(this_CARET_8, grammarAccess.getTermCharacterAccess().getCARETTerminalRuleCall_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:446:10: this_EQUAL_9= RULE_EQUAL
                    {
                    this_EQUAL_9=(Token)match(input,RULE_EQUAL,FOLLOW_RULE_EQUAL_in_ruleTermCharacter1140); 

                    		current.merge(this_EQUAL_9);
                        
                     
                        newLeafNode(this_EQUAL_9, grammarAccess.getTermCharacterAccess().getEQUALTerminalRuleCall_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:454:10: this_PLUS_10= RULE_PLUS
                    {
                    this_PLUS_10=(Token)match(input,RULE_PLUS,FOLLOW_RULE_PLUS_in_ruleTermCharacter1166); 

                    		current.merge(this_PLUS_10);
                        
                     
                        newLeafNode(this_PLUS_10, grammarAccess.getTermCharacterAccess().getPLUSTerminalRuleCall_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:462:10: this_CURLY_OPEN_11= RULE_CURLY_OPEN
                    {
                    this_CURLY_OPEN_11=(Token)match(input,RULE_CURLY_OPEN,FOLLOW_RULE_CURLY_OPEN_in_ruleTermCharacter1192); 

                    		current.merge(this_CURLY_OPEN_11);
                        
                     
                        newLeafNode(this_CURLY_OPEN_11, grammarAccess.getTermCharacterAccess().getCURLY_OPENTerminalRuleCall_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:470:10: this_CURLY_CLOSE_12= RULE_CURLY_CLOSE
                    {
                    this_CURLY_CLOSE_12=(Token)match(input,RULE_CURLY_CLOSE,FOLLOW_RULE_CURLY_CLOSE_in_ruleTermCharacter1218); 

                    		current.merge(this_CURLY_CLOSE_12);
                        
                     
                        newLeafNode(this_CURLY_CLOSE_12, grammarAccess.getTermCharacterAccess().getCURLY_CLOSETerminalRuleCall_12()); 
                        

                    }
                    break;
                case 14 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:478:10: this_ROUND_OPEN_13= RULE_ROUND_OPEN
                    {
                    this_ROUND_OPEN_13=(Token)match(input,RULE_ROUND_OPEN,FOLLOW_RULE_ROUND_OPEN_in_ruleTermCharacter1244); 

                    		current.merge(this_ROUND_OPEN_13);
                        
                     
                        newLeafNode(this_ROUND_OPEN_13, grammarAccess.getTermCharacterAccess().getROUND_OPENTerminalRuleCall_13()); 
                        

                    }
                    break;
                case 15 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:486:10: this_ROUND_CLOSE_14= RULE_ROUND_CLOSE
                    {
                    this_ROUND_CLOSE_14=(Token)match(input,RULE_ROUND_CLOSE,FOLLOW_RULE_ROUND_CLOSE_in_ruleTermCharacter1270); 

                    		current.merge(this_ROUND_CLOSE_14);
                        
                     
                        newLeafNode(this_ROUND_CLOSE_14, grammarAccess.getTermCharacterAccess().getROUND_CLOSETerminalRuleCall_14()); 
                        

                    }
                    break;
                case 16 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:494:10: this_SQUARE_OPEN_15= RULE_SQUARE_OPEN
                    {
                    this_SQUARE_OPEN_15=(Token)match(input,RULE_SQUARE_OPEN,FOLLOW_RULE_SQUARE_OPEN_in_ruleTermCharacter1296); 

                    		current.merge(this_SQUARE_OPEN_15);
                        
                     
                        newLeafNode(this_SQUARE_OPEN_15, grammarAccess.getTermCharacterAccess().getSQUARE_OPENTerminalRuleCall_15()); 
                        

                    }
                    break;
                case 17 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:502:10: this_SQUARE_CLOSE_16= RULE_SQUARE_CLOSE
                    {
                    this_SQUARE_CLOSE_16=(Token)match(input,RULE_SQUARE_CLOSE,FOLLOW_RULE_SQUARE_CLOSE_in_ruleTermCharacter1322); 

                    		current.merge(this_SQUARE_CLOSE_16);
                        
                     
                        newLeafNode(this_SQUARE_CLOSE_16, grammarAccess.getTermCharacterAccess().getSQUARE_CLOSETerminalRuleCall_16()); 
                        

                    }
                    break;
                case 18 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:510:10: this_DOT_17= RULE_DOT
                    {
                    this_DOT_17=(Token)match(input,RULE_DOT,FOLLOW_RULE_DOT_in_ruleTermCharacter1348); 

                    		current.merge(this_DOT_17);
                        
                     
                        newLeafNode(this_DOT_17, grammarAccess.getTermCharacterAccess().getDOTTerminalRuleCall_17()); 
                        

                    }
                    break;
                case 19 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:518:10: this_COLON_18= RULE_COLON
                    {
                    this_COLON_18=(Token)match(input,RULE_COLON,FOLLOW_RULE_COLON_in_ruleTermCharacter1374); 

                    		current.merge(this_COLON_18);
                        
                     
                        newLeafNode(this_COLON_18, grammarAccess.getTermCharacterAccess().getCOLONTerminalRuleCall_18()); 
                        

                    }
                    break;
                case 20 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:526:10: this_COMMA_19= RULE_COMMA
                    {
                    this_COMMA_19=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleTermCharacter1400); 

                    		current.merge(this_COMMA_19);
                        
                     
                        newLeafNode(this_COMMA_19, grammarAccess.getTermCharacterAccess().getCOMMATerminalRuleCall_19()); 
                        

                    }
                    break;
                case 21 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:534:10: this_OTHER_CHARACTER_20= RULE_OTHER_CHARACTER
                    {
                    this_OTHER_CHARACTER_20=(Token)match(input,RULE_OTHER_CHARACTER,FOLLOW_RULE_OTHER_CHARACTER_in_ruleTermCharacter1426); 

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
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleExpressionConstraint140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptReference_in_entryRuleConceptReference179 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleConceptReference189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSnomedIdentifier_in_ruleConceptReference235 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConceptReference247 = new BitSet(new long[]{0x0000000007FFFF60L});
    public static final BitSet FOLLOW_ruleTerm_in_ruleConceptReference267 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConceptReference278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSnomedIdentifier_in_entryRuleSnomedIdentifier322 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSnomedIdentifier333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier377 = new BitSet(new long[]{0x0000000000000060L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier398 = new BitSet(new long[]{0x0000000000000060L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier424 = new BitSet(new long[]{0x0000000000000060L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier446 = new BitSet(new long[]{0x0000000000000060L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier472 = new BitSet(new long[]{0x0000000000000060L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier494 = new BitSet(new long[]{0x0000000000000060L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier520 = new BitSet(new long[]{0x0000000000000060L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier542 = new BitSet(new long[]{0x0000000000000060L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier568 = new BitSet(new long[]{0x0000000000000060L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier590 = new BitSet(new long[]{0x0000000000000062L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier616 = new BitSet(new long[]{0x0000000000000062L});
    public static final BitSet FOLLOW_ruleTerm_in_entryRuleTerm674 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTerm685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm737 = new BitSet(new long[]{0x0000000007FFFFE2L});
    public static final BitSet FOLLOW_RULE_WS_in_ruleTerm761 = new BitSet(new long[]{0x0000000007FFFFE0L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm791 = new BitSet(new long[]{0x0000000007FFFFE2L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter851 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTermCharacter862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DESCENDANT_OF_in_ruleTermCharacter906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DESCENDANT_OR_SELF_OF_in_ruleTermCharacter932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_AND_in_ruleTermCharacter958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OR_in_ruleTermCharacter984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_in_ruleTermCharacter1010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleTermCharacter1036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter1062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LETTER_in_ruleTermCharacter1088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CARET_in_ruleTermCharacter1114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_EQUAL_in_ruleTermCharacter1140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_PLUS_in_ruleTermCharacter1166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CURLY_OPEN_in_ruleTermCharacter1192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CURLY_CLOSE_in_ruleTermCharacter1218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_OPEN_in_ruleTermCharacter1244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_CLOSE_in_ruleTermCharacter1270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SQUARE_OPEN_in_ruleTermCharacter1296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SQUARE_CLOSE_in_ruleTermCharacter1322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_in_ruleTermCharacter1348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COLON_in_ruleTermCharacter1374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleTermCharacter1400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OTHER_CHARACTER_in_ruleTermCharacter1426 = new BitSet(new long[]{0x0000000000000002L});

}