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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_OR", "RULE_AND", "RULE_MINUS", "RULE_LT_EM", "RULE_LT", "RULE_DBL_LT", "RULE_GT_EM", "RULE_CARET", "RULE_PIPE", "RULE_WILDCARD", "RULE_ROUND_OPEN", "RULE_ROUND_CLOSE", "RULE_DIGIT_NONZERO", "RULE_ZERO", "RULE_WS", "RULE_GT", "RULE_DBL_GT", "RULE_NOT", "RULE_LETTER", "RULE_EQUAL", "RULE_PLUS", "RULE_CURLY_OPEN", "RULE_CURLY_CLOSE", "RULE_SQUARE_OPEN", "RULE_SQUARE_CLOSE", "RULE_DOT", "RULE_COLON", "RULE_COMMA", "RULE_OTHER_CHARACTER", "RULE_ML_COMMENT", "RULE_SL_COMMENT"
    };
    public static final int RULE_DIGIT_NONZERO=16;
    public static final int RULE_CURLY_OPEN=25;
    public static final int RULE_ROUND_CLOSE=15;
    public static final int RULE_DBL_GT=20;
    public static final int RULE_GT=19;
    public static final int RULE_NOT=21;
    public static final int RULE_AND=5;
    public static final int RULE_SL_COMMENT=34;
    public static final int RULE_ROUND_OPEN=14;
    public static final int RULE_OTHER_CHARACTER=32;
    public static final int RULE_DBL_LT=9;
    public static final int RULE_PLUS=24;
    public static final int RULE_OR=4;
    public static final int RULE_DOT=29;
    public static final int EOF=-1;
    public static final int RULE_SQUARE_CLOSE=28;
    public static final int RULE_EQUAL=23;
    public static final int RULE_SQUARE_OPEN=27;
    public static final int RULE_LT_EM=7;
    public static final int RULE_GT_EM=10;
    public static final int RULE_WS=18;
    public static final int RULE_COMMA=31;
    public static final int RULE_CURLY_CLOSE=26;
    public static final int RULE_ZERO=17;
    public static final int RULE_COLON=30;
    public static final int RULE_MINUS=6;
    public static final int RULE_LETTER=22;
    public static final int RULE_LT=8;
    public static final int RULE_CARET=11;
    public static final int RULE_PIPE=12;
    public static final int RULE_ML_COMMENT=33;
    public static final int RULE_WILDCARD=13;

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:82:1: ruleExpressionConstraint returns [EObject current=null] : this_OrExpressionConstraint_0= ruleOrExpressionConstraint ;
    public final EObject ruleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject this_OrExpressionConstraint_0 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens("RULE_WS", "RULE_SL_COMMENT", "RULE_ML_COMMENT");
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:86:28: (this_OrExpressionConstraint_0= ruleOrExpressionConstraint )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:88:5: this_OrExpressionConstraint_0= ruleOrExpressionConstraint
            {
             
                    newCompositeNode(grammarAccess.getExpressionConstraintAccess().getOrExpressionConstraintParserRuleCall()); 
                
            pushFollow(FOLLOW_ruleOrExpressionConstraint_in_ruleExpressionConstraint141);
            this_OrExpressionConstraint_0=ruleOrExpressionConstraint();

            state._fsp--;

             
                    current = this_OrExpressionConstraint_0; 
                    afterParserOrEnumRuleCall();
                

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


    // $ANTLR start "entryRuleOrExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:107:1: entryRuleOrExpressionConstraint returns [EObject current=null] : iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF ;
    public final EObject entryRuleOrExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrExpressionConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:108:2: (iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:109:2: iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getOrExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleOrExpressionConstraint_in_entryRuleOrExpressionConstraint179);
            iv_ruleOrExpressionConstraint=ruleOrExpressionConstraint();

            state._fsp--;

             current =iv_ruleOrExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOrExpressionConstraint189); 

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
    // $ANTLR end "entryRuleOrExpressionConstraint"


    // $ANTLR start "ruleOrExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:116:1: ruleOrExpressionConstraint returns [EObject current=null] : (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* ) ;
    public final EObject ruleOrExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_OR_2=null;
        EObject this_AndExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:119:28: ( (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:120:1: (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:120:1: (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:121:5: this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )*
            {
             
                    newCompositeNode(grammarAccess.getOrExpressionConstraintAccess().getAndExpressionConstraintParserRuleCall_0()); 
                
            pushFollow(FOLLOW_ruleAndExpressionConstraint_in_ruleOrExpressionConstraint236);
            this_AndExpressionConstraint_0=ruleAndExpressionConstraint();

            state._fsp--;

             
                    current = this_AndExpressionConstraint_0; 
                    afterParserOrEnumRuleCall();
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:129:1: ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==RULE_OR) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:129:2: () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndExpressionConstraint ) )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:129:2: ()
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:130:5: 
            	    {

            	            current = forceCreateModelElementAndSet(
            	                grammarAccess.getOrExpressionConstraintAccess().getOrExpressionConstraintLeftAction_1_0(),
            	                current);
            	        

            	    }

            	    this_OR_2=(Token)match(input,RULE_OR,FOLLOW_RULE_OR_in_ruleOrExpressionConstraint256); 
            	     
            	        newLeafNode(this_OR_2, grammarAccess.getOrExpressionConstraintAccess().getORTerminalRuleCall_1_1()); 
            	        
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:139:1: ( (lv_right_3_0= ruleAndExpressionConstraint ) )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:140:1: (lv_right_3_0= ruleAndExpressionConstraint )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:140:1: (lv_right_3_0= ruleAndExpressionConstraint )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:141:3: lv_right_3_0= ruleAndExpressionConstraint
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOrExpressionConstraintAccess().getRightAndExpressionConstraintParserRuleCall_1_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleAndExpressionConstraint_in_ruleOrExpressionConstraint276);
            	    lv_right_3_0=ruleAndExpressionConstraint();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOrExpressionConstraintRule());
            	    	        }
            	           		set(
            	           			current, 
            	           			"right",
            	            		lv_right_3_0, 
            	            		"AndExpressionConstraint");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
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
        }
        return current;
    }
    // $ANTLR end "ruleOrExpressionConstraint"


    // $ANTLR start "entryRuleAndExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:165:1: entryRuleAndExpressionConstraint returns [EObject current=null] : iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF ;
    public final EObject entryRuleAndExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndExpressionConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:166:2: (iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:167:2: iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getAndExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleAndExpressionConstraint_in_entryRuleAndExpressionConstraint314);
            iv_ruleAndExpressionConstraint=ruleAndExpressionConstraint();

            state._fsp--;

             current =iv_ruleAndExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAndExpressionConstraint324); 

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
    // $ANTLR end "entryRuleAndExpressionConstraint"


    // $ANTLR start "ruleAndExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:174:1: ruleAndExpressionConstraint returns [EObject current=null] : (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () this_AND_2= RULE_AND ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )* ) ;
    public final EObject ruleAndExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_AND_2=null;
        EObject this_ExclusionExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:177:28: ( (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () this_AND_2= RULE_AND ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:178:1: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () this_AND_2= RULE_AND ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:178:1: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () this_AND_2= RULE_AND ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:179:5: this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () this_AND_2= RULE_AND ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )*
            {
             
                    newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getExclusionExpressionConstraintParserRuleCall_0()); 
                
            pushFollow(FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint371);
            this_ExclusionExpressionConstraint_0=ruleExclusionExpressionConstraint();

            state._fsp--;

             
                    current = this_ExclusionExpressionConstraint_0; 
                    afterParserOrEnumRuleCall();
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:187:1: ( () this_AND_2= RULE_AND ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==RULE_AND) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:187:2: () this_AND_2= RULE_AND ( (lv_right_3_0= ruleExclusionExpressionConstraint ) )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:187:2: ()
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:188:5: 
            	    {

            	            current = forceCreateModelElementAndSet(
            	                grammarAccess.getAndExpressionConstraintAccess().getAndExpressionConstraintLeftAction_1_0(),
            	                current);
            	        

            	    }

            	    this_AND_2=(Token)match(input,RULE_AND,FOLLOW_RULE_AND_in_ruleAndExpressionConstraint391); 
            	     
            	        newLeafNode(this_AND_2, grammarAccess.getAndExpressionConstraintAccess().getANDTerminalRuleCall_1_1()); 
            	        
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:197:1: ( (lv_right_3_0= ruleExclusionExpressionConstraint ) )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:198:1: (lv_right_3_0= ruleExclusionExpressionConstraint )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:198:1: (lv_right_3_0= ruleExclusionExpressionConstraint )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:199:3: lv_right_3_0= ruleExclusionExpressionConstraint
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getRightExclusionExpressionConstraintParserRuleCall_1_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint411);
            	    lv_right_3_0=ruleExclusionExpressionConstraint();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAndExpressionConstraintRule());
            	    	        }
            	           		set(
            	           			current, 
            	           			"right",
            	            		lv_right_3_0, 
            	            		"ExclusionExpressionConstraint");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
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
        }
        return current;
    }
    // $ANTLR end "ruleAndExpressionConstraint"


    // $ANTLR start "entryRuleExclusionExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:223:1: entryRuleExclusionExpressionConstraint returns [EObject current=null] : iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF ;
    public final EObject entryRuleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExclusionExpressionConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:224:2: (iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:225:2: iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getExclusionExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleExclusionExpressionConstraint_in_entryRuleExclusionExpressionConstraint449);
            iv_ruleExclusionExpressionConstraint=ruleExclusionExpressionConstraint();

            state._fsp--;

             current =iv_ruleExclusionExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleExclusionExpressionConstraint459); 

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
    // $ANTLR end "entryRuleExclusionExpressionConstraint"


    // $ANTLR start "ruleExclusionExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:232:1: ruleExclusionExpressionConstraint returns [EObject current=null] : (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleSimpleExpressionConstraint ) ) )? ) ;
    public final EObject ruleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_MINUS_2=null;
        EObject this_SimpleExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:235:28: ( (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleSimpleExpressionConstraint ) ) )? ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:236:1: (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleSimpleExpressionConstraint ) ) )? )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:236:1: (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleSimpleExpressionConstraint ) ) )? )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:237:5: this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleSimpleExpressionConstraint ) ) )?
            {
             
                    newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getSimpleExpressionConstraintParserRuleCall_0()); 
                
            pushFollow(FOLLOW_ruleSimpleExpressionConstraint_in_ruleExclusionExpressionConstraint506);
            this_SimpleExpressionConstraint_0=ruleSimpleExpressionConstraint();

            state._fsp--;

             
                    current = this_SimpleExpressionConstraint_0; 
                    afterParserOrEnumRuleCall();
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:245:1: ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleSimpleExpressionConstraint ) ) )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==RULE_MINUS) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:245:2: () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleSimpleExpressionConstraint ) )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:245:2: ()
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:246:5: 
                    {

                            current = forceCreateModelElementAndSet(
                                grammarAccess.getExclusionExpressionConstraintAccess().getExclusionExpressionConstraintLeftAction_1_0(),
                                current);
                        

                    }

                    this_MINUS_2=(Token)match(input,RULE_MINUS,FOLLOW_RULE_MINUS_in_ruleExclusionExpressionConstraint526); 
                     
                        newLeafNode(this_MINUS_2, grammarAccess.getExclusionExpressionConstraintAccess().getMINUSTerminalRuleCall_1_1()); 
                        
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:255:1: ( (lv_right_3_0= ruleSimpleExpressionConstraint ) )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:256:1: (lv_right_3_0= ruleSimpleExpressionConstraint )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:256:1: (lv_right_3_0= ruleSimpleExpressionConstraint )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:257:3: lv_right_3_0= ruleSimpleExpressionConstraint
                    {
                     
                    	        newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRightSimpleExpressionConstraintParserRuleCall_1_2_0()); 
                    	    
                    pushFollow(FOLLOW_ruleSimpleExpressionConstraint_in_ruleExclusionExpressionConstraint546);
                    lv_right_3_0=ruleSimpleExpressionConstraint();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getExclusionExpressionConstraintRule());
                    	        }
                           		set(
                           			current, 
                           			"right",
                            		lv_right_3_0, 
                            		"SimpleExpressionConstraint");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


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
    // $ANTLR end "ruleExclusionExpressionConstraint"


    // $ANTLR start "entryRuleSimpleExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:281:1: entryRuleSimpleExpressionConstraint returns [EObject current=null] : iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF ;
    public final EObject entryRuleSimpleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleExpressionConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:282:2: (iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:283:2: iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getSimpleExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleSimpleExpressionConstraint_in_entryRuleSimpleExpressionConstraint584);
            iv_ruleSimpleExpressionConstraint=ruleSimpleExpressionConstraint();

            state._fsp--;

             current =iv_ruleSimpleExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSimpleExpressionConstraint594); 

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
    // $ANTLR end "entryRuleSimpleExpressionConstraint"


    // $ANTLR start "ruleSimpleExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:290:1: ruleSimpleExpressionConstraint returns [EObject current=null] : (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_FocusConcept_4= ruleFocusConcept ) ;
    public final EObject ruleSimpleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject this_ChildOf_0 = null;

        EObject this_DescendantOf_1 = null;

        EObject this_DescendantOrSelfOf_2 = null;

        EObject this_ParentOf_3 = null;

        EObject this_FocusConcept_4 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:293:28: ( (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_FocusConcept_4= ruleFocusConcept ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:294:1: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_FocusConcept_4= ruleFocusConcept )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:294:1: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_FocusConcept_4= ruleFocusConcept )
            int alt4=5;
            switch ( input.LA(1) ) {
            case RULE_LT_EM:
                {
                alt4=1;
                }
                break;
            case RULE_LT:
                {
                alt4=2;
                }
                break;
            case RULE_DBL_LT:
                {
                alt4=3;
                }
                break;
            case RULE_GT_EM:
                {
                alt4=4;
                }
                break;
            case RULE_CARET:
            case RULE_WILDCARD:
            case RULE_DIGIT_NONZERO:
                {
                alt4=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:295:5: this_ChildOf_0= ruleChildOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getChildOfParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleChildOf_in_ruleSimpleExpressionConstraint641);
                    this_ChildOf_0=ruleChildOf();

                    state._fsp--;

                     
                            current = this_ChildOf_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:305:5: this_DescendantOf_1= ruleDescendantOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getDescendantOfParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleDescendantOf_in_ruleSimpleExpressionConstraint668);
                    this_DescendantOf_1=ruleDescendantOf();

                    state._fsp--;

                     
                            current = this_DescendantOf_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:315:5: this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getDescendantOrSelfOfParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleDescendantOrSelfOf_in_ruleSimpleExpressionConstraint695);
                    this_DescendantOrSelfOf_2=ruleDescendantOrSelfOf();

                    state._fsp--;

                     
                            current = this_DescendantOrSelfOf_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:325:5: this_ParentOf_3= ruleParentOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getParentOfParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_ruleParentOf_in_ruleSimpleExpressionConstraint722);
                    this_ParentOf_3=ruleParentOf();

                    state._fsp--;

                     
                            current = this_ParentOf_3; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 5 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:335:5: this_FocusConcept_4= ruleFocusConcept
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getFocusConceptParserRuleCall_4()); 
                        
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleSimpleExpressionConstraint749);
                    this_FocusConcept_4=ruleFocusConcept();

                    state._fsp--;

                     
                            current = this_FocusConcept_4; 
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
    // $ANTLR end "ruleSimpleExpressionConstraint"


    // $ANTLR start "entryRuleFocusConcept"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:351:1: entryRuleFocusConcept returns [EObject current=null] : iv_ruleFocusConcept= ruleFocusConcept EOF ;
    public final EObject entryRuleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFocusConcept = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:352:2: (iv_ruleFocusConcept= ruleFocusConcept EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:353:2: iv_ruleFocusConcept= ruleFocusConcept EOF
            {
             newCompositeNode(grammarAccess.getFocusConceptRule()); 
            pushFollow(FOLLOW_ruleFocusConcept_in_entryRuleFocusConcept784);
            iv_ruleFocusConcept=ruleFocusConcept();

            state._fsp--;

             current =iv_ruleFocusConcept; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFocusConcept794); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:360:1: ruleFocusConcept returns [EObject current=null] : (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny ) ;
    public final EObject ruleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject this_MemberOf_0 = null;

        EObject this_ConceptReference_1 = null;

        EObject this_Any_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:363:28: ( (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:364:1: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:364:1: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny )
            int alt5=3;
            switch ( input.LA(1) ) {
            case RULE_CARET:
                {
                alt5=1;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt5=2;
                }
                break;
            case RULE_WILDCARD:
                {
                alt5=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:365:5: this_MemberOf_0= ruleMemberOf
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getMemberOfParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleMemberOf_in_ruleFocusConcept841);
                    this_MemberOf_0=ruleMemberOf();

                    state._fsp--;

                     
                            current = this_MemberOf_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:375:5: this_ConceptReference_1= ruleConceptReference
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getConceptReferenceParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleFocusConcept868);
                    this_ConceptReference_1=ruleConceptReference();

                    state._fsp--;

                     
                            current = this_ConceptReference_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:385:5: this_Any_2= ruleAny
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getAnyParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleAny_in_ruleFocusConcept895);
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


    // $ANTLR start "entryRuleChildOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:401:1: entryRuleChildOf returns [EObject current=null] : iv_ruleChildOf= ruleChildOf EOF ;
    public final EObject entryRuleChildOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleChildOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:402:2: (iv_ruleChildOf= ruleChildOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:403:2: iv_ruleChildOf= ruleChildOf EOF
            {
             newCompositeNode(grammarAccess.getChildOfRule()); 
            pushFollow(FOLLOW_ruleChildOf_in_entryRuleChildOf930);
            iv_ruleChildOf=ruleChildOf();

            state._fsp--;

             current =iv_ruleChildOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleChildOf940); 

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
    // $ANTLR end "entryRuleChildOf"


    // $ANTLR start "ruleChildOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:410:1: ruleChildOf returns [EObject current=null] : (this_LT_EM_0= RULE_LT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) ;
    public final EObject ruleChildOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_EM_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:413:28: ( (this_LT_EM_0= RULE_LT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:414:1: (this_LT_EM_0= RULE_LT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:414:1: (this_LT_EM_0= RULE_LT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:414:2: this_LT_EM_0= RULE_LT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            {
            this_LT_EM_0=(Token)match(input,RULE_LT_EM,FOLLOW_RULE_LT_EM_in_ruleChildOf976); 
             
                newLeafNode(this_LT_EM_0, grammarAccess.getChildOfAccess().getLT_EMTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:418:1: ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:419:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:419:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:420:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:420:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==RULE_CARET||LA6_0==RULE_WILDCARD||LA6_0==RULE_DIGIT_NONZERO) ) {
                alt6=1;
            }
            else if ( (LA6_0==RULE_ROUND_OPEN) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:421:3: lv_constraint_1_1= ruleFocusConcept
                    {
                     
                    	        newCompositeNode(grammarAccess.getChildOfAccess().getConstraintFocusConceptParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleChildOf998);
                    lv_constraint_1_1=ruleFocusConcept();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getChildOfRule());
                    	        }
                           		set(
                           			current, 
                           			"constraint",
                            		lv_constraint_1_1, 
                            		"FocusConcept");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:436:8: lv_constraint_1_2= ruleNestableExpression
                    {
                     
                    	        newCompositeNode(grammarAccess.getChildOfAccess().getConstraintNestableExpressionParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleNestableExpression_in_ruleChildOf1017);
                    lv_constraint_1_2=ruleNestableExpression();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getChildOfRule());
                    	        }
                           		set(
                           			current, 
                           			"constraint",
                            		lv_constraint_1_2, 
                            		"NestableExpression");
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
    // $ANTLR end "ruleChildOf"


    // $ANTLR start "entryRuleDescendantOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:462:1: entryRuleDescendantOf returns [EObject current=null] : iv_ruleDescendantOf= ruleDescendantOf EOF ;
    public final EObject entryRuleDescendantOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:463:2: (iv_ruleDescendantOf= ruleDescendantOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:464:2: iv_ruleDescendantOf= ruleDescendantOf EOF
            {
             newCompositeNode(grammarAccess.getDescendantOfRule()); 
            pushFollow(FOLLOW_ruleDescendantOf_in_entryRuleDescendantOf1056);
            iv_ruleDescendantOf=ruleDescendantOf();

            state._fsp--;

             current =iv_ruleDescendantOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDescendantOf1066); 

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
    // $ANTLR end "entryRuleDescendantOf"


    // $ANTLR start "ruleDescendantOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:471:1: ruleDescendantOf returns [EObject current=null] : (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) ;
    public final EObject ruleDescendantOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:474:28: ( (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:475:1: (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:475:1: (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:475:2: this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FOLLOW_RULE_LT_in_ruleDescendantOf1102); 
             
                newLeafNode(this_LT_0, grammarAccess.getDescendantOfAccess().getLTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:479:1: ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:480:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:480:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:481:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:481:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==RULE_CARET||LA7_0==RULE_WILDCARD||LA7_0==RULE_DIGIT_NONZERO) ) {
                alt7=1;
            }
            else if ( (LA7_0==RULE_ROUND_OPEN) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:482:3: lv_constraint_1_1= ruleFocusConcept
                    {
                     
                    	        newCompositeNode(grammarAccess.getDescendantOfAccess().getConstraintFocusConceptParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleDescendantOf1124);
                    lv_constraint_1_1=ruleFocusConcept();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getDescendantOfRule());
                    	        }
                           		set(
                           			current, 
                           			"constraint",
                            		lv_constraint_1_1, 
                            		"FocusConcept");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:497:8: lv_constraint_1_2= ruleNestableExpression
                    {
                     
                    	        newCompositeNode(grammarAccess.getDescendantOfAccess().getConstraintNestableExpressionParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleNestableExpression_in_ruleDescendantOf1143);
                    lv_constraint_1_2=ruleNestableExpression();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getDescendantOfRule());
                    	        }
                           		set(
                           			current, 
                           			"constraint",
                            		lv_constraint_1_2, 
                            		"NestableExpression");
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
    // $ANTLR end "ruleDescendantOf"


    // $ANTLR start "entryRuleDescendantOrSelfOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:523:1: entryRuleDescendantOrSelfOf returns [EObject current=null] : iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF ;
    public final EObject entryRuleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOrSelfOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:524:2: (iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:525:2: iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF
            {
             newCompositeNode(grammarAccess.getDescendantOrSelfOfRule()); 
            pushFollow(FOLLOW_ruleDescendantOrSelfOf_in_entryRuleDescendantOrSelfOf1182);
            iv_ruleDescendantOrSelfOf=ruleDescendantOrSelfOf();

            state._fsp--;

             current =iv_ruleDescendantOrSelfOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDescendantOrSelfOf1192); 

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
    // $ANTLR end "entryRuleDescendantOrSelfOf"


    // $ANTLR start "ruleDescendantOrSelfOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:532:1: ruleDescendantOrSelfOf returns [EObject current=null] : (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) ;
    public final EObject ruleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_LT_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:535:28: ( (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:536:1: (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:536:1: (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:536:2: this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            {
            this_DBL_LT_0=(Token)match(input,RULE_DBL_LT,FOLLOW_RULE_DBL_LT_in_ruleDescendantOrSelfOf1228); 
             
                newLeafNode(this_DBL_LT_0, grammarAccess.getDescendantOrSelfOfAccess().getDBL_LTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:540:1: ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:541:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:541:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:542:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:542:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==RULE_CARET||LA8_0==RULE_WILDCARD||LA8_0==RULE_DIGIT_NONZERO) ) {
                alt8=1;
            }
            else if ( (LA8_0==RULE_ROUND_OPEN) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:543:3: lv_constraint_1_1= ruleFocusConcept
                    {
                     
                    	        newCompositeNode(grammarAccess.getDescendantOrSelfOfAccess().getConstraintFocusConceptParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleDescendantOrSelfOf1250);
                    lv_constraint_1_1=ruleFocusConcept();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getDescendantOrSelfOfRule());
                    	        }
                           		set(
                           			current, 
                           			"constraint",
                            		lv_constraint_1_1, 
                            		"FocusConcept");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:558:8: lv_constraint_1_2= ruleNestableExpression
                    {
                     
                    	        newCompositeNode(grammarAccess.getDescendantOrSelfOfAccess().getConstraintNestableExpressionParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleNestableExpression_in_ruleDescendantOrSelfOf1269);
                    lv_constraint_1_2=ruleNestableExpression();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getDescendantOrSelfOfRule());
                    	        }
                           		set(
                           			current, 
                           			"constraint",
                            		lv_constraint_1_2, 
                            		"NestableExpression");
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
    // $ANTLR end "ruleDescendantOrSelfOf"


    // $ANTLR start "entryRuleParentOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:584:1: entryRuleParentOf returns [EObject current=null] : iv_ruleParentOf= ruleParentOf EOF ;
    public final EObject entryRuleParentOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParentOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:585:2: (iv_ruleParentOf= ruleParentOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:586:2: iv_ruleParentOf= ruleParentOf EOF
            {
             newCompositeNode(grammarAccess.getParentOfRule()); 
            pushFollow(FOLLOW_ruleParentOf_in_entryRuleParentOf1308);
            iv_ruleParentOf=ruleParentOf();

            state._fsp--;

             current =iv_ruleParentOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleParentOf1318); 

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
    // $ANTLR end "entryRuleParentOf"


    // $ANTLR start "ruleParentOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:593:1: ruleParentOf returns [EObject current=null] : (this_GT_EM_0= RULE_GT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) ;
    public final EObject ruleParentOf() throws RecognitionException {
        EObject current = null;

        Token this_GT_EM_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:596:28: ( (this_GT_EM_0= RULE_GT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:597:1: (this_GT_EM_0= RULE_GT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:597:1: (this_GT_EM_0= RULE_GT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:597:2: this_GT_EM_0= RULE_GT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            {
            this_GT_EM_0=(Token)match(input,RULE_GT_EM,FOLLOW_RULE_GT_EM_in_ruleParentOf1354); 
             
                newLeafNode(this_GT_EM_0, grammarAccess.getParentOfAccess().getGT_EMTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:601:1: ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:602:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:602:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:603:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:603:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==RULE_CARET||LA9_0==RULE_WILDCARD||LA9_0==RULE_DIGIT_NONZERO) ) {
                alt9=1;
            }
            else if ( (LA9_0==RULE_ROUND_OPEN) ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:604:3: lv_constraint_1_1= ruleFocusConcept
                    {
                     
                    	        newCompositeNode(grammarAccess.getParentOfAccess().getConstraintFocusConceptParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleParentOf1376);
                    lv_constraint_1_1=ruleFocusConcept();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getParentOfRule());
                    	        }
                           		set(
                           			current, 
                           			"constraint",
                            		lv_constraint_1_1, 
                            		"FocusConcept");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:619:8: lv_constraint_1_2= ruleNestableExpression
                    {
                     
                    	        newCompositeNode(grammarAccess.getParentOfAccess().getConstraintNestableExpressionParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleNestableExpression_in_ruleParentOf1395);
                    lv_constraint_1_2=ruleNestableExpression();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getParentOfRule());
                    	        }
                           		set(
                           			current, 
                           			"constraint",
                            		lv_constraint_1_2, 
                            		"NestableExpression");
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
    // $ANTLR end "ruleParentOf"


    // $ANTLR start "entryRuleMemberOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:645:1: entryRuleMemberOf returns [EObject current=null] : iv_ruleMemberOf= ruleMemberOf EOF ;
    public final EObject entryRuleMemberOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMemberOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:646:2: (iv_ruleMemberOf= ruleMemberOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:647:2: iv_ruleMemberOf= ruleMemberOf EOF
            {
             newCompositeNode(grammarAccess.getMemberOfRule()); 
            pushFollow(FOLLOW_ruleMemberOf_in_entryRuleMemberOf1434);
            iv_ruleMemberOf=ruleMemberOf();

            state._fsp--;

             current =iv_ruleMemberOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleMemberOf1444); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:654:1: ruleMemberOf returns [EObject current=null] : (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) ;
    public final EObject ruleMemberOf() throws RecognitionException {
        EObject current = null;

        Token this_CARET_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:657:28: ( (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:658:1: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:658:1: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:658:2: this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            {
            this_CARET_0=(Token)match(input,RULE_CARET,FOLLOW_RULE_CARET_in_ruleMemberOf1480); 
             
                newLeafNode(this_CARET_0, grammarAccess.getMemberOfAccess().getCARETTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:662:1: ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:663:1: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:663:1: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:664:1: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:664:1: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==RULE_DIGIT_NONZERO) ) {
                alt10=1;
            }
            else if ( (LA10_0==RULE_WILDCARD) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:665:3: lv_constraint_1_1= ruleConceptReference
                    {
                     
                    	        newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintConceptReferenceParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleMemberOf1502);
                    lv_constraint_1_1=ruleConceptReference();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getMemberOfRule());
                    	        }
                           		set(
                           			current, 
                           			"constraint",
                            		lv_constraint_1_1, 
                            		"ConceptReference");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:680:8: lv_constraint_1_2= ruleAny
                    {
                     
                    	        newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintAnyParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleAny_in_ruleMemberOf1521);
                    lv_constraint_1_2=ruleAny();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getMemberOfRule());
                    	        }
                           		set(
                           			current, 
                           			"constraint",
                            		lv_constraint_1_2, 
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:706:1: entryRuleConceptReference returns [EObject current=null] : iv_ruleConceptReference= ruleConceptReference EOF ;
    public final EObject entryRuleConceptReference() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptReference = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:707:2: (iv_ruleConceptReference= ruleConceptReference EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:708:2: iv_ruleConceptReference= ruleConceptReference EOF
            {
             newCompositeNode(grammarAccess.getConceptReferenceRule()); 
            pushFollow(FOLLOW_ruleConceptReference_in_entryRuleConceptReference1560);
            iv_ruleConceptReference=ruleConceptReference();

            state._fsp--;

             current =iv_ruleConceptReference; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleConceptReference1570); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:715:1: ruleConceptReference returns [EObject current=null] : ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? ) ;
    public final EObject ruleConceptReference() throws RecognitionException {
        EObject current = null;

        Token this_PIPE_1=null;
        Token this_PIPE_3=null;
        AntlrDatatypeRuleToken lv_id_0_0 = null;

        AntlrDatatypeRuleToken lv_term_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:718:28: ( ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:719:1: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:719:1: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:719:2: ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )?
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:719:2: ( (lv_id_0_0= ruleSnomedIdentifier ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:720:1: (lv_id_0_0= ruleSnomedIdentifier )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:720:1: (lv_id_0_0= ruleSnomedIdentifier )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:721:3: lv_id_0_0= ruleSnomedIdentifier
            {
             
            	        newCompositeNode(grammarAccess.getConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleSnomedIdentifier_in_ruleConceptReference1616);
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

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:737:2: (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==RULE_PIPE) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:737:3: this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE
                    {
                    this_PIPE_1=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConceptReference1628); 
                     
                        newLeafNode(this_PIPE_1, grammarAccess.getConceptReferenceAccess().getPIPETerminalRuleCall_1_0()); 
                        
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:741:1: ( (lv_term_2_0= ruleTerm ) )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:742:1: (lv_term_2_0= ruleTerm )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:742:1: (lv_term_2_0= ruleTerm )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:743:3: lv_term_2_0= ruleTerm
                    {
                     
                    	        newCompositeNode(grammarAccess.getConceptReferenceAccess().getTermTermParserRuleCall_1_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleTerm_in_ruleConceptReference1648);
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

                    this_PIPE_3=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConceptReference1659); 
                     
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:771:1: entryRuleAny returns [EObject current=null] : iv_ruleAny= ruleAny EOF ;
    public final EObject entryRuleAny() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAny = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:772:2: (iv_ruleAny= ruleAny EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:773:2: iv_ruleAny= ruleAny EOF
            {
             newCompositeNode(grammarAccess.getAnyRule()); 
            pushFollow(FOLLOW_ruleAny_in_entryRuleAny1696);
            iv_ruleAny=ruleAny();

            state._fsp--;

             current =iv_ruleAny; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAny1706); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:780:1: ruleAny returns [EObject current=null] : (this_WILDCARD_0= RULE_WILDCARD () ) ;
    public final EObject ruleAny() throws RecognitionException {
        EObject current = null;

        Token this_WILDCARD_0=null;

         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:783:28: ( (this_WILDCARD_0= RULE_WILDCARD () ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:784:1: (this_WILDCARD_0= RULE_WILDCARD () )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:784:1: (this_WILDCARD_0= RULE_WILDCARD () )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:784:2: this_WILDCARD_0= RULE_WILDCARD ()
            {
            this_WILDCARD_0=(Token)match(input,RULE_WILDCARD,FOLLOW_RULE_WILDCARD_in_ruleAny1742); 
             
                newLeafNode(this_WILDCARD_0, grammarAccess.getAnyAccess().getWILDCARDTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:788:1: ()
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:789:5: 
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


    // $ANTLR start "entryRuleNestableExpression"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:802:1: entryRuleNestableExpression returns [EObject current=null] : iv_ruleNestableExpression= ruleNestableExpression EOF ;
    public final EObject entryRuleNestableExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestableExpression = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:803:2: (iv_ruleNestableExpression= ruleNestableExpression EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:804:2: iv_ruleNestableExpression= ruleNestableExpression EOF
            {
             newCompositeNode(grammarAccess.getNestableExpressionRule()); 
            pushFollow(FOLLOW_ruleNestableExpression_in_entryRuleNestableExpression1786);
            iv_ruleNestableExpression=ruleNestableExpression();

            state._fsp--;

             current =iv_ruleNestableExpression; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNestableExpression1796); 

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
    // $ANTLR end "entryRuleNestableExpression"


    // $ANTLR start "ruleNestableExpression"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:811:1: ruleNestableExpression returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN this_ExpressionConstraint_1= ruleExpressionConstraint this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestableExpression() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject this_ExpressionConstraint_1 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:814:28: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN this_ExpressionConstraint_1= ruleExpressionConstraint this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:815:1: (this_ROUND_OPEN_0= RULE_ROUND_OPEN this_ExpressionConstraint_1= ruleExpressionConstraint this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:815:1: (this_ROUND_OPEN_0= RULE_ROUND_OPEN this_ExpressionConstraint_1= ruleExpressionConstraint this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:815:2: this_ROUND_OPEN_0= RULE_ROUND_OPEN this_ExpressionConstraint_1= ruleExpressionConstraint this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FOLLOW_RULE_ROUND_OPEN_in_ruleNestableExpression1832); 
             
                newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestableExpressionAccess().getROUND_OPENTerminalRuleCall_0()); 
                
             
                    newCompositeNode(grammarAccess.getNestableExpressionAccess().getExpressionConstraintParserRuleCall_1()); 
                
            pushFollow(FOLLOW_ruleExpressionConstraint_in_ruleNestableExpression1853);
            this_ExpressionConstraint_1=ruleExpressionConstraint();

            state._fsp--;

             
                    current = this_ExpressionConstraint_1; 
                    afterParserOrEnumRuleCall();
                
            this_ROUND_CLOSE_2=(Token)match(input,RULE_ROUND_CLOSE,FOLLOW_RULE_ROUND_CLOSE_in_ruleNestableExpression1863); 
             
                newLeafNode(this_ROUND_CLOSE_2, grammarAccess.getNestableExpressionAccess().getROUND_CLOSETerminalRuleCall_2()); 
                

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
    // $ANTLR end "ruleNestableExpression"


    // $ANTLR start "entryRuleSnomedIdentifier"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:840:1: entryRuleSnomedIdentifier returns [String current=null] : iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF ;
    public final String entryRuleSnomedIdentifier() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSnomedIdentifier = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:844:2: (iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:845:2: iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF
            {
             newCompositeNode(grammarAccess.getSnomedIdentifierRule()); 
            pushFollow(FOLLOW_ruleSnomedIdentifier_in_entryRuleSnomedIdentifier1905);
            iv_ruleSnomedIdentifier=ruleSnomedIdentifier();

            state._fsp--;

             current =iv_ruleSnomedIdentifier.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSnomedIdentifier1916); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:855:1: ruleSnomedIdentifier returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) ;
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
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:859:28: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:860:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:860:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:860:6: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            {
            this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier1960); 

            		current.merge(this_DIGIT_NONZERO_0);
                
             
                newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:867:1: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==RULE_DIGIT_NONZERO) ) {
                alt12=1;
            }
            else if ( (LA12_0==RULE_ZERO) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:867:6: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier1981); 

                    		current.merge(this_DIGIT_NONZERO_1);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_1_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:875:10: this_ZERO_2= RULE_ZERO
                    {
                    this_ZERO_2=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier2007); 

                    		current.merge(this_ZERO_2);
                        
                     
                        newLeafNode(this_ZERO_2, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_1_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:882:2: (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==RULE_DIGIT_NONZERO) ) {
                alt13=1;
            }
            else if ( (LA13_0==RULE_ZERO) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:882:7: this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_3=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2029); 

                    		current.merge(this_DIGIT_NONZERO_3);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_2_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:890:10: this_ZERO_4= RULE_ZERO
                    {
                    this_ZERO_4=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier2055); 

                    		current.merge(this_ZERO_4);
                        
                     
                        newLeafNode(this_ZERO_4, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_2_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:897:2: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_DIGIT_NONZERO) ) {
                alt14=1;
            }
            else if ( (LA14_0==RULE_ZERO) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:897:7: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2077); 

                    		current.merge(this_DIGIT_NONZERO_5);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_3_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:905:10: this_ZERO_6= RULE_ZERO
                    {
                    this_ZERO_6=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier2103); 

                    		current.merge(this_ZERO_6);
                        
                     
                        newLeafNode(this_ZERO_6, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_3_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:912:2: (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==RULE_DIGIT_NONZERO) ) {
                alt15=1;
            }
            else if ( (LA15_0==RULE_ZERO) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:912:7: this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_7=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2125); 

                    		current.merge(this_DIGIT_NONZERO_7);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_4_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:920:10: this_ZERO_8= RULE_ZERO
                    {
                    this_ZERO_8=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier2151); 

                    		current.merge(this_ZERO_8);
                        
                     
                        newLeafNode(this_ZERO_8, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_4_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:927:2: (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            int cnt16=0;
            loop16:
            do {
                int alt16=3;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==RULE_DIGIT_NONZERO) ) {
                    alt16=1;
                }
                else if ( (LA16_0==RULE_ZERO) ) {
                    alt16=2;
                }


                switch (alt16) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:927:7: this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_9=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2173); 

            	    		current.merge(this_DIGIT_NONZERO_9);
            	        
            	     
            	        newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_5_0()); 
            	        

            	    }
            	    break;
            	case 2 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:935:10: this_ZERO_10= RULE_ZERO
            	    {
            	    this_ZERO_10=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier2199); 

            	    		current.merge(this_ZERO_10);
            	        
            	     
            	        newLeafNode(this_ZERO_10, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_5_1()); 
            	        

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:953:1: entryRuleTerm returns [String current=null] : iv_ruleTerm= ruleTerm EOF ;
    public final String entryRuleTerm() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTerm = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:957:2: (iv_ruleTerm= ruleTerm EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:958:2: iv_ruleTerm= ruleTerm EOF
            {
             newCompositeNode(grammarAccess.getTermRule()); 
            pushFollow(FOLLOW_ruleTerm_in_entryRuleTerm2257);
            iv_ruleTerm=ruleTerm();

            state._fsp--;

             current =iv_ruleTerm.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTerm2268); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:968:1: ruleTerm returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) ;
    public final AntlrDatatypeRuleToken ruleTerm() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WS_1=null;
        AntlrDatatypeRuleToken this_TermCharacter_0 = null;

        AntlrDatatypeRuleToken this_TermCharacter_2 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:972:28: ( ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:973:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:973:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:973:2: (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:973:2: (this_TermCharacter_0= ruleTermCharacter )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0>=RULE_OR && LA17_0<=RULE_AND)||(LA17_0>=RULE_LT && LA17_0<=RULE_DBL_LT)||LA17_0==RULE_CARET||(LA17_0>=RULE_ROUND_OPEN && LA17_0<=RULE_ZERO)||(LA17_0>=RULE_GT && LA17_0<=RULE_OTHER_CHARACTER)) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:974:5: this_TermCharacter_0= ruleTermCharacter
            	    {
            	     
            	            newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_0()); 
            	        
            	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm2320);
            	    this_TermCharacter_0=ruleTermCharacter();

            	    state._fsp--;


            	    		current.merge(this_TermCharacter_0);
            	        
            	     
            	            afterParserOrEnumRuleCall();
            	        

            	    }
            	    break;

            	default :
            	    if ( cnt17 >= 1 ) break loop17;
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:984:3: ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==RULE_WS) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:984:4: (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:984:4: (this_WS_1= RULE_WS )+
            	    int cnt18=0;
            	    loop18:
            	    do {
            	        int alt18=2;
            	        int LA18_0 = input.LA(1);

            	        if ( (LA18_0==RULE_WS) ) {
            	            alt18=1;
            	        }


            	        switch (alt18) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:984:9: this_WS_1= RULE_WS
            	    	    {
            	    	    this_WS_1=(Token)match(input,RULE_WS,FOLLOW_RULE_WS_in_ruleTerm2344); 

            	    	    		current.merge(this_WS_1);
            	    	        
            	    	     
            	    	        newLeafNode(this_WS_1, grammarAccess.getTermAccess().getWSTerminalRuleCall_1_0()); 
            	    	        

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt18 >= 1 ) break loop18;
            	                EarlyExitException eee =
            	                    new EarlyExitException(18, input);
            	                throw eee;
            	        }
            	        cnt18++;
            	    } while (true);

            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:991:3: (this_TermCharacter_2= ruleTermCharacter )+
            	    int cnt19=0;
            	    loop19:
            	    do {
            	        int alt19=2;
            	        int LA19_0 = input.LA(1);

            	        if ( ((LA19_0>=RULE_OR && LA19_0<=RULE_AND)||(LA19_0>=RULE_LT && LA19_0<=RULE_DBL_LT)||LA19_0==RULE_CARET||(LA19_0>=RULE_ROUND_OPEN && LA19_0<=RULE_ZERO)||(LA19_0>=RULE_GT && LA19_0<=RULE_OTHER_CHARACTER)) ) {
            	            alt19=1;
            	        }


            	        switch (alt19) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:992:5: this_TermCharacter_2= ruleTermCharacter
            	    	    {
            	    	     
            	    	            newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_1_1()); 
            	    	        
            	    	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm2374);
            	    	    this_TermCharacter_2=ruleTermCharacter();

            	    	    state._fsp--;


            	    	    		current.merge(this_TermCharacter_2);
            	    	        
            	    	     
            	    	            afterParserOrEnumRuleCall();
            	    	        

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt19 >= 1 ) break loop19;
            	                EarlyExitException eee =
            	                    new EarlyExitException(19, input);
            	                throw eee;
            	        }
            	        cnt19++;
            	    } while (true);


            	    }
            	    break;

            	default :
            	    break loop20;
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1013:1: entryRuleTermCharacter returns [String current=null] : iv_ruleTermCharacter= ruleTermCharacter EOF ;
    public final String entryRuleTermCharacter() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTermCharacter = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1017:2: (iv_ruleTermCharacter= ruleTermCharacter EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1018:2: iv_ruleTermCharacter= ruleTermCharacter EOF
            {
             newCompositeNode(grammarAccess.getTermCharacterRule()); 
            pushFollow(FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter2434);
            iv_ruleTermCharacter=ruleTermCharacter();

            state._fsp--;

             current =iv_ruleTermCharacter.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTermCharacter2445); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1028:1: ruleTermCharacter returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_AND_4= RULE_AND | this_OR_5= RULE_OR | this_NOT_6= RULE_NOT | this_ZERO_7= RULE_ZERO | this_DIGIT_NONZERO_8= RULE_DIGIT_NONZERO | this_LETTER_9= RULE_LETTER | this_CARET_10= RULE_CARET | this_EQUAL_11= RULE_EQUAL | this_PLUS_12= RULE_PLUS | this_CURLY_OPEN_13= RULE_CURLY_OPEN | this_CURLY_CLOSE_14= RULE_CURLY_CLOSE | this_ROUND_OPEN_15= RULE_ROUND_OPEN | this_ROUND_CLOSE_16= RULE_ROUND_CLOSE | this_SQUARE_OPEN_17= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_18= RULE_SQUARE_CLOSE | this_DOT_19= RULE_DOT | this_COLON_20= RULE_COLON | this_COMMA_21= RULE_COMMA | this_OTHER_CHARACTER_22= RULE_OTHER_CHARACTER ) ;
    public final AntlrDatatypeRuleToken ruleTermCharacter() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_LT_0=null;
        Token this_GT_1=null;
        Token this_DBL_LT_2=null;
        Token this_DBL_GT_3=null;
        Token this_AND_4=null;
        Token this_OR_5=null;
        Token this_NOT_6=null;
        Token this_ZERO_7=null;
        Token this_DIGIT_NONZERO_8=null;
        Token this_LETTER_9=null;
        Token this_CARET_10=null;
        Token this_EQUAL_11=null;
        Token this_PLUS_12=null;
        Token this_CURLY_OPEN_13=null;
        Token this_CURLY_CLOSE_14=null;
        Token this_ROUND_OPEN_15=null;
        Token this_ROUND_CLOSE_16=null;
        Token this_SQUARE_OPEN_17=null;
        Token this_SQUARE_CLOSE_18=null;
        Token this_DOT_19=null;
        Token this_COLON_20=null;
        Token this_COMMA_21=null;
        Token this_OTHER_CHARACTER_22=null;

         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1032:28: ( (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_AND_4= RULE_AND | this_OR_5= RULE_OR | this_NOT_6= RULE_NOT | this_ZERO_7= RULE_ZERO | this_DIGIT_NONZERO_8= RULE_DIGIT_NONZERO | this_LETTER_9= RULE_LETTER | this_CARET_10= RULE_CARET | this_EQUAL_11= RULE_EQUAL | this_PLUS_12= RULE_PLUS | this_CURLY_OPEN_13= RULE_CURLY_OPEN | this_CURLY_CLOSE_14= RULE_CURLY_CLOSE | this_ROUND_OPEN_15= RULE_ROUND_OPEN | this_ROUND_CLOSE_16= RULE_ROUND_CLOSE | this_SQUARE_OPEN_17= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_18= RULE_SQUARE_CLOSE | this_DOT_19= RULE_DOT | this_COLON_20= RULE_COLON | this_COMMA_21= RULE_COMMA | this_OTHER_CHARACTER_22= RULE_OTHER_CHARACTER ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1033:1: (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_AND_4= RULE_AND | this_OR_5= RULE_OR | this_NOT_6= RULE_NOT | this_ZERO_7= RULE_ZERO | this_DIGIT_NONZERO_8= RULE_DIGIT_NONZERO | this_LETTER_9= RULE_LETTER | this_CARET_10= RULE_CARET | this_EQUAL_11= RULE_EQUAL | this_PLUS_12= RULE_PLUS | this_CURLY_OPEN_13= RULE_CURLY_OPEN | this_CURLY_CLOSE_14= RULE_CURLY_CLOSE | this_ROUND_OPEN_15= RULE_ROUND_OPEN | this_ROUND_CLOSE_16= RULE_ROUND_CLOSE | this_SQUARE_OPEN_17= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_18= RULE_SQUARE_CLOSE | this_DOT_19= RULE_DOT | this_COLON_20= RULE_COLON | this_COMMA_21= RULE_COMMA | this_OTHER_CHARACTER_22= RULE_OTHER_CHARACTER )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1033:1: (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_AND_4= RULE_AND | this_OR_5= RULE_OR | this_NOT_6= RULE_NOT | this_ZERO_7= RULE_ZERO | this_DIGIT_NONZERO_8= RULE_DIGIT_NONZERO | this_LETTER_9= RULE_LETTER | this_CARET_10= RULE_CARET | this_EQUAL_11= RULE_EQUAL | this_PLUS_12= RULE_PLUS | this_CURLY_OPEN_13= RULE_CURLY_OPEN | this_CURLY_CLOSE_14= RULE_CURLY_CLOSE | this_ROUND_OPEN_15= RULE_ROUND_OPEN | this_ROUND_CLOSE_16= RULE_ROUND_CLOSE | this_SQUARE_OPEN_17= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_18= RULE_SQUARE_CLOSE | this_DOT_19= RULE_DOT | this_COLON_20= RULE_COLON | this_COMMA_21= RULE_COMMA | this_OTHER_CHARACTER_22= RULE_OTHER_CHARACTER )
            int alt21=23;
            switch ( input.LA(1) ) {
            case RULE_LT:
                {
                alt21=1;
                }
                break;
            case RULE_GT:
                {
                alt21=2;
                }
                break;
            case RULE_DBL_LT:
                {
                alt21=3;
                }
                break;
            case RULE_DBL_GT:
                {
                alt21=4;
                }
                break;
            case RULE_AND:
                {
                alt21=5;
                }
                break;
            case RULE_OR:
                {
                alt21=6;
                }
                break;
            case RULE_NOT:
                {
                alt21=7;
                }
                break;
            case RULE_ZERO:
                {
                alt21=8;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt21=9;
                }
                break;
            case RULE_LETTER:
                {
                alt21=10;
                }
                break;
            case RULE_CARET:
                {
                alt21=11;
                }
                break;
            case RULE_EQUAL:
                {
                alt21=12;
                }
                break;
            case RULE_PLUS:
                {
                alt21=13;
                }
                break;
            case RULE_CURLY_OPEN:
                {
                alt21=14;
                }
                break;
            case RULE_CURLY_CLOSE:
                {
                alt21=15;
                }
                break;
            case RULE_ROUND_OPEN:
                {
                alt21=16;
                }
                break;
            case RULE_ROUND_CLOSE:
                {
                alt21=17;
                }
                break;
            case RULE_SQUARE_OPEN:
                {
                alt21=18;
                }
                break;
            case RULE_SQUARE_CLOSE:
                {
                alt21=19;
                }
                break;
            case RULE_DOT:
                {
                alt21=20;
                }
                break;
            case RULE_COLON:
                {
                alt21=21;
                }
                break;
            case RULE_COMMA:
                {
                alt21=22;
                }
                break;
            case RULE_OTHER_CHARACTER:
                {
                alt21=23;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1033:6: this_LT_0= RULE_LT
                    {
                    this_LT_0=(Token)match(input,RULE_LT,FOLLOW_RULE_LT_in_ruleTermCharacter2489); 

                    		current.merge(this_LT_0);
                        
                     
                        newLeafNode(this_LT_0, grammarAccess.getTermCharacterAccess().getLTTerminalRuleCall_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1041:10: this_GT_1= RULE_GT
                    {
                    this_GT_1=(Token)match(input,RULE_GT,FOLLOW_RULE_GT_in_ruleTermCharacter2515); 

                    		current.merge(this_GT_1);
                        
                     
                        newLeafNode(this_GT_1, grammarAccess.getTermCharacterAccess().getGTTerminalRuleCall_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1049:10: this_DBL_LT_2= RULE_DBL_LT
                    {
                    this_DBL_LT_2=(Token)match(input,RULE_DBL_LT,FOLLOW_RULE_DBL_LT_in_ruleTermCharacter2541); 

                    		current.merge(this_DBL_LT_2);
                        
                     
                        newLeafNode(this_DBL_LT_2, grammarAccess.getTermCharacterAccess().getDBL_LTTerminalRuleCall_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1057:10: this_DBL_GT_3= RULE_DBL_GT
                    {
                    this_DBL_GT_3=(Token)match(input,RULE_DBL_GT,FOLLOW_RULE_DBL_GT_in_ruleTermCharacter2567); 

                    		current.merge(this_DBL_GT_3);
                        
                     
                        newLeafNode(this_DBL_GT_3, grammarAccess.getTermCharacterAccess().getDBL_GTTerminalRuleCall_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1065:10: this_AND_4= RULE_AND
                    {
                    this_AND_4=(Token)match(input,RULE_AND,FOLLOW_RULE_AND_in_ruleTermCharacter2593); 

                    		current.merge(this_AND_4);
                        
                     
                        newLeafNode(this_AND_4, grammarAccess.getTermCharacterAccess().getANDTerminalRuleCall_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1073:10: this_OR_5= RULE_OR
                    {
                    this_OR_5=(Token)match(input,RULE_OR,FOLLOW_RULE_OR_in_ruleTermCharacter2619); 

                    		current.merge(this_OR_5);
                        
                     
                        newLeafNode(this_OR_5, grammarAccess.getTermCharacterAccess().getORTerminalRuleCall_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1081:10: this_NOT_6= RULE_NOT
                    {
                    this_NOT_6=(Token)match(input,RULE_NOT,FOLLOW_RULE_NOT_in_ruleTermCharacter2645); 

                    		current.merge(this_NOT_6);
                        
                     
                        newLeafNode(this_NOT_6, grammarAccess.getTermCharacterAccess().getNOTTerminalRuleCall_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1089:10: this_ZERO_7= RULE_ZERO
                    {
                    this_ZERO_7=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleTermCharacter2671); 

                    		current.merge(this_ZERO_7);
                        
                     
                        newLeafNode(this_ZERO_7, grammarAccess.getTermCharacterAccess().getZEROTerminalRuleCall_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1097:10: this_DIGIT_NONZERO_8= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_8=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter2697); 

                    		current.merge(this_DIGIT_NONZERO_8);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_8, grammarAccess.getTermCharacterAccess().getDIGIT_NONZEROTerminalRuleCall_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1105:10: this_LETTER_9= RULE_LETTER
                    {
                    this_LETTER_9=(Token)match(input,RULE_LETTER,FOLLOW_RULE_LETTER_in_ruleTermCharacter2723); 

                    		current.merge(this_LETTER_9);
                        
                     
                        newLeafNode(this_LETTER_9, grammarAccess.getTermCharacterAccess().getLETTERTerminalRuleCall_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1113:10: this_CARET_10= RULE_CARET
                    {
                    this_CARET_10=(Token)match(input,RULE_CARET,FOLLOW_RULE_CARET_in_ruleTermCharacter2749); 

                    		current.merge(this_CARET_10);
                        
                     
                        newLeafNode(this_CARET_10, grammarAccess.getTermCharacterAccess().getCARETTerminalRuleCall_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1121:10: this_EQUAL_11= RULE_EQUAL
                    {
                    this_EQUAL_11=(Token)match(input,RULE_EQUAL,FOLLOW_RULE_EQUAL_in_ruleTermCharacter2775); 

                    		current.merge(this_EQUAL_11);
                        
                     
                        newLeafNode(this_EQUAL_11, grammarAccess.getTermCharacterAccess().getEQUALTerminalRuleCall_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1129:10: this_PLUS_12= RULE_PLUS
                    {
                    this_PLUS_12=(Token)match(input,RULE_PLUS,FOLLOW_RULE_PLUS_in_ruleTermCharacter2801); 

                    		current.merge(this_PLUS_12);
                        
                     
                        newLeafNode(this_PLUS_12, grammarAccess.getTermCharacterAccess().getPLUSTerminalRuleCall_12()); 
                        

                    }
                    break;
                case 14 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1137:10: this_CURLY_OPEN_13= RULE_CURLY_OPEN
                    {
                    this_CURLY_OPEN_13=(Token)match(input,RULE_CURLY_OPEN,FOLLOW_RULE_CURLY_OPEN_in_ruleTermCharacter2827); 

                    		current.merge(this_CURLY_OPEN_13);
                        
                     
                        newLeafNode(this_CURLY_OPEN_13, grammarAccess.getTermCharacterAccess().getCURLY_OPENTerminalRuleCall_13()); 
                        

                    }
                    break;
                case 15 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1145:10: this_CURLY_CLOSE_14= RULE_CURLY_CLOSE
                    {
                    this_CURLY_CLOSE_14=(Token)match(input,RULE_CURLY_CLOSE,FOLLOW_RULE_CURLY_CLOSE_in_ruleTermCharacter2853); 

                    		current.merge(this_CURLY_CLOSE_14);
                        
                     
                        newLeafNode(this_CURLY_CLOSE_14, grammarAccess.getTermCharacterAccess().getCURLY_CLOSETerminalRuleCall_14()); 
                        

                    }
                    break;
                case 16 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1153:10: this_ROUND_OPEN_15= RULE_ROUND_OPEN
                    {
                    this_ROUND_OPEN_15=(Token)match(input,RULE_ROUND_OPEN,FOLLOW_RULE_ROUND_OPEN_in_ruleTermCharacter2879); 

                    		current.merge(this_ROUND_OPEN_15);
                        
                     
                        newLeafNode(this_ROUND_OPEN_15, grammarAccess.getTermCharacterAccess().getROUND_OPENTerminalRuleCall_15()); 
                        

                    }
                    break;
                case 17 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1161:10: this_ROUND_CLOSE_16= RULE_ROUND_CLOSE
                    {
                    this_ROUND_CLOSE_16=(Token)match(input,RULE_ROUND_CLOSE,FOLLOW_RULE_ROUND_CLOSE_in_ruleTermCharacter2905); 

                    		current.merge(this_ROUND_CLOSE_16);
                        
                     
                        newLeafNode(this_ROUND_CLOSE_16, grammarAccess.getTermCharacterAccess().getROUND_CLOSETerminalRuleCall_16()); 
                        

                    }
                    break;
                case 18 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1169:10: this_SQUARE_OPEN_17= RULE_SQUARE_OPEN
                    {
                    this_SQUARE_OPEN_17=(Token)match(input,RULE_SQUARE_OPEN,FOLLOW_RULE_SQUARE_OPEN_in_ruleTermCharacter2931); 

                    		current.merge(this_SQUARE_OPEN_17);
                        
                     
                        newLeafNode(this_SQUARE_OPEN_17, grammarAccess.getTermCharacterAccess().getSQUARE_OPENTerminalRuleCall_17()); 
                        

                    }
                    break;
                case 19 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1177:10: this_SQUARE_CLOSE_18= RULE_SQUARE_CLOSE
                    {
                    this_SQUARE_CLOSE_18=(Token)match(input,RULE_SQUARE_CLOSE,FOLLOW_RULE_SQUARE_CLOSE_in_ruleTermCharacter2957); 

                    		current.merge(this_SQUARE_CLOSE_18);
                        
                     
                        newLeafNode(this_SQUARE_CLOSE_18, grammarAccess.getTermCharacterAccess().getSQUARE_CLOSETerminalRuleCall_18()); 
                        

                    }
                    break;
                case 20 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1185:10: this_DOT_19= RULE_DOT
                    {
                    this_DOT_19=(Token)match(input,RULE_DOT,FOLLOW_RULE_DOT_in_ruleTermCharacter2983); 

                    		current.merge(this_DOT_19);
                        
                     
                        newLeafNode(this_DOT_19, grammarAccess.getTermCharacterAccess().getDOTTerminalRuleCall_19()); 
                        

                    }
                    break;
                case 21 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1193:10: this_COLON_20= RULE_COLON
                    {
                    this_COLON_20=(Token)match(input,RULE_COLON,FOLLOW_RULE_COLON_in_ruleTermCharacter3009); 

                    		current.merge(this_COLON_20);
                        
                     
                        newLeafNode(this_COLON_20, grammarAccess.getTermCharacterAccess().getCOLONTerminalRuleCall_20()); 
                        

                    }
                    break;
                case 22 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1201:10: this_COMMA_21= RULE_COMMA
                    {
                    this_COMMA_21=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleTermCharacter3035); 

                    		current.merge(this_COMMA_21);
                        
                     
                        newLeafNode(this_COMMA_21, grammarAccess.getTermCharacterAccess().getCOMMATerminalRuleCall_21()); 
                        

                    }
                    break;
                case 23 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1209:10: this_OTHER_CHARACTER_22= RULE_OTHER_CHARACTER
                    {
                    this_OTHER_CHARACTER_22=(Token)match(input,RULE_OTHER_CHARACTER,FOLLOW_RULE_OTHER_CHARACTER_in_ruleTermCharacter3061); 

                    		current.merge(this_OTHER_CHARACTER_22);
                        
                     
                        newLeafNode(this_OTHER_CHARACTER_22, grammarAccess.getTermCharacterAccess().getOTHER_CHARACTERTerminalRuleCall_22()); 
                        

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
    public static final BitSet FOLLOW_ruleOrExpressionConstraint_in_ruleExpressionConstraint141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOrExpressionConstraint_in_entryRuleOrExpressionConstraint179 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOrExpressionConstraint189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAndExpressionConstraint_in_ruleOrExpressionConstraint236 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_RULE_OR_in_ruleOrExpressionConstraint256 = new BitSet(new long[]{0x0000000000012F80L});
    public static final BitSet FOLLOW_ruleAndExpressionConstraint_in_ruleOrExpressionConstraint276 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_ruleAndExpressionConstraint_in_entryRuleAndExpressionConstraint314 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAndExpressionConstraint324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint371 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_RULE_AND_in_ruleAndExpressionConstraint391 = new BitSet(new long[]{0x0000000000012F80L});
    public static final BitSet FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint411 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_ruleExclusionExpressionConstraint_in_entryRuleExclusionExpressionConstraint449 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleExclusionExpressionConstraint459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSimpleExpressionConstraint_in_ruleExclusionExpressionConstraint506 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_RULE_MINUS_in_ruleExclusionExpressionConstraint526 = new BitSet(new long[]{0x0000000000012F80L});
    public static final BitSet FOLLOW_ruleSimpleExpressionConstraint_in_ruleExclusionExpressionConstraint546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSimpleExpressionConstraint_in_entryRuleSimpleExpressionConstraint584 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSimpleExpressionConstraint594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleChildOf_in_ruleSimpleExpressionConstraint641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOf_in_ruleSimpleExpressionConstraint668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOrSelfOf_in_ruleSimpleExpressionConstraint695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleParentOf_in_ruleSimpleExpressionConstraint722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleSimpleExpressionConstraint749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_entryRuleFocusConcept784 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFocusConcept794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberOf_in_ruleFocusConcept841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleFocusConcept868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_ruleFocusConcept895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleChildOf_in_entryRuleChildOf930 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleChildOf940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LT_EM_in_ruleChildOf976 = new BitSet(new long[]{0x0000000000016F80L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleChildOf998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_ruleChildOf1017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOf_in_entryRuleDescendantOf1056 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDescendantOf1066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LT_in_ruleDescendantOf1102 = new BitSet(new long[]{0x0000000000016F80L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleDescendantOf1124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_ruleDescendantOf1143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOrSelfOf_in_entryRuleDescendantOrSelfOf1182 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDescendantOrSelfOf1192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_LT_in_ruleDescendantOrSelfOf1228 = new BitSet(new long[]{0x0000000000016F80L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleDescendantOrSelfOf1250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_ruleDescendantOrSelfOf1269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleParentOf_in_entryRuleParentOf1308 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleParentOf1318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_GT_EM_in_ruleParentOf1354 = new BitSet(new long[]{0x0000000000016F80L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleParentOf1376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_ruleParentOf1395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberOf_in_entryRuleMemberOf1434 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMemberOf1444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CARET_in_ruleMemberOf1480 = new BitSet(new long[]{0x0000000000012F80L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleMemberOf1502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_ruleMemberOf1521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptReference_in_entryRuleConceptReference1560 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleConceptReference1570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSnomedIdentifier_in_ruleConceptReference1616 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConceptReference1628 = new BitSet(new long[]{0x00000001FFFBCB30L});
    public static final BitSet FOLLOW_ruleTerm_in_ruleConceptReference1648 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConceptReference1659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_entryRuleAny1696 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAny1706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WILDCARD_in_ruleAny1742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_entryRuleNestableExpression1786 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNestableExpression1796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_OPEN_in_ruleNestableExpression1832 = new BitSet(new long[]{0x0000000000012F80L});
    public static final BitSet FOLLOW_ruleExpressionConstraint_in_ruleNestableExpression1853 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RULE_ROUND_CLOSE_in_ruleNestableExpression1863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSnomedIdentifier_in_entryRuleSnomedIdentifier1905 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSnomedIdentifier1916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier1960 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier1981 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier2007 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2029 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier2055 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2077 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier2103 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2125 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier2151 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2173 = new BitSet(new long[]{0x0000000000030002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier2199 = new BitSet(new long[]{0x0000000000030002L});
    public static final BitSet FOLLOW_ruleTerm_in_entryRuleTerm2257 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTerm2268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm2320 = new BitSet(new long[]{0x00000001FFFFCB32L});
    public static final BitSet FOLLOW_RULE_WS_in_ruleTerm2344 = new BitSet(new long[]{0x00000001FFFFCB30L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm2374 = new BitSet(new long[]{0x00000001FFFFCB32L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter2434 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTermCharacter2445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LT_in_ruleTermCharacter2489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_GT_in_ruleTermCharacter2515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_LT_in_ruleTermCharacter2541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_GT_in_ruleTermCharacter2567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_AND_in_ruleTermCharacter2593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OR_in_ruleTermCharacter2619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_in_ruleTermCharacter2645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleTermCharacter2671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter2697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LETTER_in_ruleTermCharacter2723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CARET_in_ruleTermCharacter2749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_EQUAL_in_ruleTermCharacter2775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_PLUS_in_ruleTermCharacter2801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CURLY_OPEN_in_ruleTermCharacter2827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CURLY_CLOSE_in_ruleTermCharacter2853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_OPEN_in_ruleTermCharacter2879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_CLOSE_in_ruleTermCharacter2905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SQUARE_OPEN_in_ruleTermCharacter2931 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SQUARE_CLOSE_in_ruleTermCharacter2957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_in_ruleTermCharacter2983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COLON_in_ruleTermCharacter3009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleTermCharacter3035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OTHER_CHARACTER_in_ruleTermCharacter3061 = new BitSet(new long[]{0x0000000000000002L});

}