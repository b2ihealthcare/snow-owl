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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_OR", "RULE_AND", "RULE_COMMA", "RULE_MINUS", "RULE_COLON", "RULE_LT_EM", "RULE_LT", "RULE_DBL_LT", "RULE_GT_EM", "RULE_GT", "RULE_DBL_GT", "RULE_CARET", "RULE_PIPE", "RULE_WILDCARD", "RULE_EQUAL", "RULE_NOT_EQUAL", "RULE_ROUND_OPEN", "RULE_ROUND_CLOSE", "RULE_DIGIT_NONZERO", "RULE_ZERO", "RULE_WS", "RULE_NOT", "RULE_LETTER", "RULE_PLUS", "RULE_CURLY_OPEN", "RULE_CURLY_CLOSE", "RULE_SQUARE_OPEN", "RULE_SQUARE_CLOSE", "RULE_DOT", "RULE_OTHER_CHARACTER", "RULE_ML_COMMENT", "RULE_SL_COMMENT"
    };
    public static final int RULE_DIGIT_NONZERO=22;
    public static final int RULE_CURLY_OPEN=28;
    public static final int RULE_DBL_GT=14;
    public static final int RULE_ROUND_CLOSE=21;
    public static final int RULE_GT=13;
    public static final int RULE_NOT=25;
    public static final int RULE_AND=5;
    public static final int RULE_SL_COMMENT=35;
    public static final int RULE_ROUND_OPEN=20;
    public static final int RULE_OTHER_CHARACTER=33;
    public static final int RULE_DBL_LT=11;
    public static final int RULE_PLUS=27;
    public static final int RULE_NOT_EQUAL=19;
    public static final int RULE_OR=4;
    public static final int RULE_DOT=32;
    public static final int EOF=-1;
    public static final int RULE_SQUARE_CLOSE=31;
    public static final int RULE_EQUAL=18;
    public static final int RULE_SQUARE_OPEN=30;
    public static final int RULE_COMMA=6;
    public static final int RULE_LT_EM=9;
    public static final int RULE_GT_EM=12;
    public static final int RULE_WS=24;
    public static final int RULE_CURLY_CLOSE=29;
    public static final int RULE_ZERO=23;
    public static final int RULE_COLON=8;
    public static final int RULE_MINUS=7;
    public static final int RULE_LETTER=26;
    public static final int RULE_LT=10;
    public static final int RULE_CARET=15;
    public static final int RULE_PIPE=16;
    public static final int RULE_ML_COMMENT=34;
    public static final int RULE_WILDCARD=17;

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:174:1: ruleAndExpressionConstraint returns [EObject current=null] : (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (this_AND_2= RULE_AND | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* ) ;
    public final EObject ruleAndExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_AND_2=null;
        Token this_COMMA_3=null;
        EObject this_ExclusionExpressionConstraint_0 = null;

        EObject lv_right_4_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:177:28: ( (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (this_AND_2= RULE_AND | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:178:1: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (this_AND_2= RULE_AND | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:178:1: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (this_AND_2= RULE_AND | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:179:5: this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (this_AND_2= RULE_AND | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )*
            {
             
                    newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getExclusionExpressionConstraintParserRuleCall_0()); 
                
            pushFollow(FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint371);
            this_ExclusionExpressionConstraint_0=ruleExclusionExpressionConstraint();

            state._fsp--;

             
                    current = this_ExclusionExpressionConstraint_0; 
                    afterParserOrEnumRuleCall();
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:187:1: ( () (this_AND_2= RULE_AND | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>=RULE_AND && LA3_0<=RULE_COMMA)) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:187:2: () (this_AND_2= RULE_AND | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:187:2: ()
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:188:5: 
            	    {

            	            current = forceCreateModelElementAndSet(
            	                grammarAccess.getAndExpressionConstraintAccess().getAndExpressionConstraintLeftAction_1_0(),
            	                current);
            	        

            	    }

            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:193:2: (this_AND_2= RULE_AND | this_COMMA_3= RULE_COMMA )
            	    int alt2=2;
            	    int LA2_0 = input.LA(1);

            	    if ( (LA2_0==RULE_AND) ) {
            	        alt2=1;
            	    }
            	    else if ( (LA2_0==RULE_COMMA) ) {
            	        alt2=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 2, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt2) {
            	        case 1 :
            	            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:193:3: this_AND_2= RULE_AND
            	            {
            	            this_AND_2=(Token)match(input,RULE_AND,FOLLOW_RULE_AND_in_ruleAndExpressionConstraint392); 
            	             
            	                newLeafNode(this_AND_2, grammarAccess.getAndExpressionConstraintAccess().getANDTerminalRuleCall_1_1_0()); 
            	                

            	            }
            	            break;
            	        case 2 :
            	            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:198:6: this_COMMA_3= RULE_COMMA
            	            {
            	            this_COMMA_3=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleAndExpressionConstraint408); 
            	             
            	                newLeafNode(this_COMMA_3, grammarAccess.getAndExpressionConstraintAccess().getCOMMATerminalRuleCall_1_1_1()); 
            	                

            	            }
            	            break;

            	    }

            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:202:2: ( (lv_right_4_0= ruleExclusionExpressionConstraint ) )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:203:1: (lv_right_4_0= ruleExclusionExpressionConstraint )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:203:1: (lv_right_4_0= ruleExclusionExpressionConstraint )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:204:3: lv_right_4_0= ruleExclusionExpressionConstraint
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getRightExclusionExpressionConstraintParserRuleCall_1_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint429);
            	    lv_right_4_0=ruleExclusionExpressionConstraint();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAndExpressionConstraintRule());
            	    	        }
            	           		set(
            	           			current, 
            	           			"right",
            	            		lv_right_4_0, 
            	            		"ExclusionExpressionConstraint");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:228:1: entryRuleExclusionExpressionConstraint returns [EObject current=null] : iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF ;
    public final EObject entryRuleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExclusionExpressionConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:229:2: (iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:230:2: iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getExclusionExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleExclusionExpressionConstraint_in_entryRuleExclusionExpressionConstraint467);
            iv_ruleExclusionExpressionConstraint=ruleExclusionExpressionConstraint();

            state._fsp--;

             current =iv_ruleExclusionExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleExclusionExpressionConstraint477); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:237:1: ruleExclusionExpressionConstraint returns [EObject current=null] : (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? ) ;
    public final EObject ruleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_MINUS_2=null;
        EObject this_RefinedExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:240:28: ( (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:241:1: (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:241:1: (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:242:5: this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )?
            {
             
                    newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRefinedExpressionConstraintParserRuleCall_0()); 
                
            pushFollow(FOLLOW_ruleRefinedExpressionConstraint_in_ruleExclusionExpressionConstraint524);
            this_RefinedExpressionConstraint_0=ruleRefinedExpressionConstraint();

            state._fsp--;

             
                    current = this_RefinedExpressionConstraint_0; 
                    afterParserOrEnumRuleCall();
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:250:1: ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==RULE_MINUS) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:250:2: () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:250:2: ()
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:251:5: 
                    {

                            current = forceCreateModelElementAndSet(
                                grammarAccess.getExclusionExpressionConstraintAccess().getExclusionExpressionConstraintLeftAction_1_0(),
                                current);
                        

                    }

                    this_MINUS_2=(Token)match(input,RULE_MINUS,FOLLOW_RULE_MINUS_in_ruleExclusionExpressionConstraint544); 
                     
                        newLeafNode(this_MINUS_2, grammarAccess.getExclusionExpressionConstraintAccess().getMINUSTerminalRuleCall_1_1()); 
                        
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:260:1: ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:261:1: (lv_right_3_0= ruleRefinedExpressionConstraint )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:261:1: (lv_right_3_0= ruleRefinedExpressionConstraint )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:262:3: lv_right_3_0= ruleRefinedExpressionConstraint
                    {
                     
                    	        newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRightRefinedExpressionConstraintParserRuleCall_1_2_0()); 
                    	    
                    pushFollow(FOLLOW_ruleRefinedExpressionConstraint_in_ruleExclusionExpressionConstraint564);
                    lv_right_3_0=ruleRefinedExpressionConstraint();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getExclusionExpressionConstraintRule());
                    	        }
                           		set(
                           			current, 
                           			"right",
                            		lv_right_3_0, 
                            		"RefinedExpressionConstraint");
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


    // $ANTLR start "entryRuleRefinedExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:286:1: entryRuleRefinedExpressionConstraint returns [EObject current=null] : iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF ;
    public final EObject entryRuleRefinedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinedExpressionConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:287:2: (iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:288:2: iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getRefinedExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleRefinedExpressionConstraint_in_entryRuleRefinedExpressionConstraint602);
            iv_ruleRefinedExpressionConstraint=ruleRefinedExpressionConstraint();

            state._fsp--;

             current =iv_ruleRefinedExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRefinedExpressionConstraint612); 

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
    // $ANTLR end "entryRuleRefinedExpressionConstraint"


    // $ANTLR start "ruleRefinedExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:295:1: ruleRefinedExpressionConstraint returns [EObject current=null] : ( ( (lv_constraint_0_0= ruleSimpleExpressionConstraint ) ) (this_COLON_1= RULE_COLON ( (lv_refinement_2_0= ruleRefinement ) ) )? ) ;
    public final EObject ruleRefinedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_COLON_1=null;
        EObject lv_constraint_0_0 = null;

        EObject lv_refinement_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:298:28: ( ( ( (lv_constraint_0_0= ruleSimpleExpressionConstraint ) ) (this_COLON_1= RULE_COLON ( (lv_refinement_2_0= ruleRefinement ) ) )? ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:299:1: ( ( (lv_constraint_0_0= ruleSimpleExpressionConstraint ) ) (this_COLON_1= RULE_COLON ( (lv_refinement_2_0= ruleRefinement ) ) )? )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:299:1: ( ( (lv_constraint_0_0= ruleSimpleExpressionConstraint ) ) (this_COLON_1= RULE_COLON ( (lv_refinement_2_0= ruleRefinement ) ) )? )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:299:2: ( (lv_constraint_0_0= ruleSimpleExpressionConstraint ) ) (this_COLON_1= RULE_COLON ( (lv_refinement_2_0= ruleRefinement ) ) )?
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:299:2: ( (lv_constraint_0_0= ruleSimpleExpressionConstraint ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:300:1: (lv_constraint_0_0= ruleSimpleExpressionConstraint )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:300:1: (lv_constraint_0_0= ruleSimpleExpressionConstraint )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:301:3: lv_constraint_0_0= ruleSimpleExpressionConstraint
            {
             
            	        newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getConstraintSimpleExpressionConstraintParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleSimpleExpressionConstraint_in_ruleRefinedExpressionConstraint658);
            lv_constraint_0_0=ruleSimpleExpressionConstraint();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getRefinedExpressionConstraintRule());
            	        }
                   		set(
                   			current, 
                   			"constraint",
                    		lv_constraint_0_0, 
                    		"SimpleExpressionConstraint");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:317:2: (this_COLON_1= RULE_COLON ( (lv_refinement_2_0= ruleRefinement ) ) )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==RULE_COLON) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:317:3: this_COLON_1= RULE_COLON ( (lv_refinement_2_0= ruleRefinement ) )
                    {
                    this_COLON_1=(Token)match(input,RULE_COLON,FOLLOW_RULE_COLON_in_ruleRefinedExpressionConstraint670); 
                     
                        newLeafNode(this_COLON_1, grammarAccess.getRefinedExpressionConstraintAccess().getCOLONTerminalRuleCall_1_0()); 
                        
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:321:1: ( (lv_refinement_2_0= ruleRefinement ) )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:322:1: (lv_refinement_2_0= ruleRefinement )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:322:1: (lv_refinement_2_0= ruleRefinement )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:323:3: lv_refinement_2_0= ruleRefinement
                    {
                     
                    	        newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getRefinementRefinementParserRuleCall_1_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleRefinement_in_ruleRefinedExpressionConstraint690);
                    lv_refinement_2_0=ruleRefinement();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getRefinedExpressionConstraintRule());
                    	        }
                           		set(
                           			current, 
                           			"refinement",
                            		lv_refinement_2_0, 
                            		"Refinement");
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
    // $ANTLR end "ruleRefinedExpressionConstraint"


    // $ANTLR start "entryRuleSimpleExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:347:1: entryRuleSimpleExpressionConstraint returns [EObject current=null] : iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF ;
    public final EObject entryRuleSimpleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleExpressionConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:348:2: (iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:349:2: iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getSimpleExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleSimpleExpressionConstraint_in_entryRuleSimpleExpressionConstraint728);
            iv_ruleSimpleExpressionConstraint=ruleSimpleExpressionConstraint();

            state._fsp--;

             current =iv_ruleSimpleExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSimpleExpressionConstraint738); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:356:1: ruleSimpleExpressionConstraint returns [EObject current=null] : (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept ) ;
    public final EObject ruleSimpleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject this_ChildOf_0 = null;

        EObject this_DescendantOf_1 = null;

        EObject this_DescendantOrSelfOf_2 = null;

        EObject this_ParentOf_3 = null;

        EObject this_AncestorOf_4 = null;

        EObject this_AncestorOrSelfOf_5 = null;

        EObject this_FocusConcept_6 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:359:28: ( (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:360:1: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:360:1: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept )
            int alt6=7;
            switch ( input.LA(1) ) {
            case RULE_LT_EM:
                {
                alt6=1;
                }
                break;
            case RULE_LT:
                {
                alt6=2;
                }
                break;
            case RULE_DBL_LT:
                {
                alt6=3;
                }
                break;
            case RULE_GT_EM:
                {
                alt6=4;
                }
                break;
            case RULE_GT:
                {
                alt6=5;
                }
                break;
            case RULE_DBL_GT:
                {
                alt6=6;
                }
                break;
            case RULE_CARET:
            case RULE_WILDCARD:
            case RULE_DIGIT_NONZERO:
                {
                alt6=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:361:5: this_ChildOf_0= ruleChildOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getChildOfParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleChildOf_in_ruleSimpleExpressionConstraint785);
                    this_ChildOf_0=ruleChildOf();

                    state._fsp--;

                     
                            current = this_ChildOf_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:371:5: this_DescendantOf_1= ruleDescendantOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getDescendantOfParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleDescendantOf_in_ruleSimpleExpressionConstraint812);
                    this_DescendantOf_1=ruleDescendantOf();

                    state._fsp--;

                     
                            current = this_DescendantOf_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:381:5: this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getDescendantOrSelfOfParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleDescendantOrSelfOf_in_ruleSimpleExpressionConstraint839);
                    this_DescendantOrSelfOf_2=ruleDescendantOrSelfOf();

                    state._fsp--;

                     
                            current = this_DescendantOrSelfOf_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:391:5: this_ParentOf_3= ruleParentOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getParentOfParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_ruleParentOf_in_ruleSimpleExpressionConstraint866);
                    this_ParentOf_3=ruleParentOf();

                    state._fsp--;

                     
                            current = this_ParentOf_3; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 5 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:401:5: this_AncestorOf_4= ruleAncestorOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getAncestorOfParserRuleCall_4()); 
                        
                    pushFollow(FOLLOW_ruleAncestorOf_in_ruleSimpleExpressionConstraint893);
                    this_AncestorOf_4=ruleAncestorOf();

                    state._fsp--;

                     
                            current = this_AncestorOf_4; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 6 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:411:5: this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getAncestorOrSelfOfParserRuleCall_5()); 
                        
                    pushFollow(FOLLOW_ruleAncestorOrSelfOf_in_ruleSimpleExpressionConstraint920);
                    this_AncestorOrSelfOf_5=ruleAncestorOrSelfOf();

                    state._fsp--;

                     
                            current = this_AncestorOrSelfOf_5; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 7 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:421:5: this_FocusConcept_6= ruleFocusConcept
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getFocusConceptParserRuleCall_6()); 
                        
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleSimpleExpressionConstraint947);
                    this_FocusConcept_6=ruleFocusConcept();

                    state._fsp--;

                     
                            current = this_FocusConcept_6; 
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:437:1: entryRuleFocusConcept returns [EObject current=null] : iv_ruleFocusConcept= ruleFocusConcept EOF ;
    public final EObject entryRuleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFocusConcept = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:438:2: (iv_ruleFocusConcept= ruleFocusConcept EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:439:2: iv_ruleFocusConcept= ruleFocusConcept EOF
            {
             newCompositeNode(grammarAccess.getFocusConceptRule()); 
            pushFollow(FOLLOW_ruleFocusConcept_in_entryRuleFocusConcept982);
            iv_ruleFocusConcept=ruleFocusConcept();

            state._fsp--;

             current =iv_ruleFocusConcept; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFocusConcept992); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:446:1: ruleFocusConcept returns [EObject current=null] : (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny ) ;
    public final EObject ruleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject this_MemberOf_0 = null;

        EObject this_ConceptReference_1 = null;

        EObject this_Any_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:449:28: ( (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:450:1: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:450:1: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny )
            int alt7=3;
            switch ( input.LA(1) ) {
            case RULE_CARET:
                {
                alt7=1;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt7=2;
                }
                break;
            case RULE_WILDCARD:
                {
                alt7=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:451:5: this_MemberOf_0= ruleMemberOf
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getMemberOfParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleMemberOf_in_ruleFocusConcept1039);
                    this_MemberOf_0=ruleMemberOf();

                    state._fsp--;

                     
                            current = this_MemberOf_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:461:5: this_ConceptReference_1= ruleConceptReference
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getConceptReferenceParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleFocusConcept1066);
                    this_ConceptReference_1=ruleConceptReference();

                    state._fsp--;

                     
                            current = this_ConceptReference_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:471:5: this_Any_2= ruleAny
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getAnyParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleAny_in_ruleFocusConcept1093);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:487:1: entryRuleChildOf returns [EObject current=null] : iv_ruleChildOf= ruleChildOf EOF ;
    public final EObject entryRuleChildOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleChildOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:488:2: (iv_ruleChildOf= ruleChildOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:489:2: iv_ruleChildOf= ruleChildOf EOF
            {
             newCompositeNode(grammarAccess.getChildOfRule()); 
            pushFollow(FOLLOW_ruleChildOf_in_entryRuleChildOf1128);
            iv_ruleChildOf=ruleChildOf();

            state._fsp--;

             current =iv_ruleChildOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleChildOf1138); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:496:1: ruleChildOf returns [EObject current=null] : (this_LT_EM_0= RULE_LT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) ;
    public final EObject ruleChildOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_EM_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:499:28: ( (this_LT_EM_0= RULE_LT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:500:1: (this_LT_EM_0= RULE_LT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:500:1: (this_LT_EM_0= RULE_LT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:500:2: this_LT_EM_0= RULE_LT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            {
            this_LT_EM_0=(Token)match(input,RULE_LT_EM,FOLLOW_RULE_LT_EM_in_ruleChildOf1174); 
             
                newLeafNode(this_LT_EM_0, grammarAccess.getChildOfAccess().getLT_EMTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:504:1: ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:505:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:505:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:506:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:506:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:507:3: lv_constraint_1_1= ruleFocusConcept
                    {
                     
                    	        newCompositeNode(grammarAccess.getChildOfAccess().getConstraintFocusConceptParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleChildOf1196);
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:522:8: lv_constraint_1_2= ruleNestableExpression
                    {
                     
                    	        newCompositeNode(grammarAccess.getChildOfAccess().getConstraintNestableExpressionParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleNestableExpression_in_ruleChildOf1215);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:548:1: entryRuleDescendantOf returns [EObject current=null] : iv_ruleDescendantOf= ruleDescendantOf EOF ;
    public final EObject entryRuleDescendantOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:549:2: (iv_ruleDescendantOf= ruleDescendantOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:550:2: iv_ruleDescendantOf= ruleDescendantOf EOF
            {
             newCompositeNode(grammarAccess.getDescendantOfRule()); 
            pushFollow(FOLLOW_ruleDescendantOf_in_entryRuleDescendantOf1254);
            iv_ruleDescendantOf=ruleDescendantOf();

            state._fsp--;

             current =iv_ruleDescendantOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDescendantOf1264); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:557:1: ruleDescendantOf returns [EObject current=null] : (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) ;
    public final EObject ruleDescendantOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:560:28: ( (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:561:1: (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:561:1: (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:561:2: this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FOLLOW_RULE_LT_in_ruleDescendantOf1300); 
             
                newLeafNode(this_LT_0, grammarAccess.getDescendantOfAccess().getLTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:565:1: ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:566:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:566:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:567:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:567:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:568:3: lv_constraint_1_1= ruleFocusConcept
                    {
                     
                    	        newCompositeNode(grammarAccess.getDescendantOfAccess().getConstraintFocusConceptParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleDescendantOf1322);
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:583:8: lv_constraint_1_2= ruleNestableExpression
                    {
                     
                    	        newCompositeNode(grammarAccess.getDescendantOfAccess().getConstraintNestableExpressionParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleNestableExpression_in_ruleDescendantOf1341);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:609:1: entryRuleDescendantOrSelfOf returns [EObject current=null] : iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF ;
    public final EObject entryRuleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOrSelfOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:610:2: (iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:611:2: iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF
            {
             newCompositeNode(grammarAccess.getDescendantOrSelfOfRule()); 
            pushFollow(FOLLOW_ruleDescendantOrSelfOf_in_entryRuleDescendantOrSelfOf1380);
            iv_ruleDescendantOrSelfOf=ruleDescendantOrSelfOf();

            state._fsp--;

             current =iv_ruleDescendantOrSelfOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDescendantOrSelfOf1390); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:618:1: ruleDescendantOrSelfOf returns [EObject current=null] : (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) ;
    public final EObject ruleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_LT_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:621:28: ( (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:622:1: (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:622:1: (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:622:2: this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            {
            this_DBL_LT_0=(Token)match(input,RULE_DBL_LT,FOLLOW_RULE_DBL_LT_in_ruleDescendantOrSelfOf1426); 
             
                newLeafNode(this_DBL_LT_0, grammarAccess.getDescendantOrSelfOfAccess().getDBL_LTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:626:1: ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:627:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:627:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:628:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:628:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==RULE_CARET||LA10_0==RULE_WILDCARD||LA10_0==RULE_DIGIT_NONZERO) ) {
                alt10=1;
            }
            else if ( (LA10_0==RULE_ROUND_OPEN) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:629:3: lv_constraint_1_1= ruleFocusConcept
                    {
                     
                    	        newCompositeNode(grammarAccess.getDescendantOrSelfOfAccess().getConstraintFocusConceptParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleDescendantOrSelfOf1448);
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:644:8: lv_constraint_1_2= ruleNestableExpression
                    {
                     
                    	        newCompositeNode(grammarAccess.getDescendantOrSelfOfAccess().getConstraintNestableExpressionParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleNestableExpression_in_ruleDescendantOrSelfOf1467);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:670:1: entryRuleParentOf returns [EObject current=null] : iv_ruleParentOf= ruleParentOf EOF ;
    public final EObject entryRuleParentOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParentOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:671:2: (iv_ruleParentOf= ruleParentOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:672:2: iv_ruleParentOf= ruleParentOf EOF
            {
             newCompositeNode(grammarAccess.getParentOfRule()); 
            pushFollow(FOLLOW_ruleParentOf_in_entryRuleParentOf1506);
            iv_ruleParentOf=ruleParentOf();

            state._fsp--;

             current =iv_ruleParentOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleParentOf1516); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:679:1: ruleParentOf returns [EObject current=null] : (this_GT_EM_0= RULE_GT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) ;
    public final EObject ruleParentOf() throws RecognitionException {
        EObject current = null;

        Token this_GT_EM_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:682:28: ( (this_GT_EM_0= RULE_GT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:683:1: (this_GT_EM_0= RULE_GT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:683:1: (this_GT_EM_0= RULE_GT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:683:2: this_GT_EM_0= RULE_GT_EM ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            {
            this_GT_EM_0=(Token)match(input,RULE_GT_EM,FOLLOW_RULE_GT_EM_in_ruleParentOf1552); 
             
                newLeafNode(this_GT_EM_0, grammarAccess.getParentOfAccess().getGT_EMTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:687:1: ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:688:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:688:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:689:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:689:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==RULE_CARET||LA11_0==RULE_WILDCARD||LA11_0==RULE_DIGIT_NONZERO) ) {
                alt11=1;
            }
            else if ( (LA11_0==RULE_ROUND_OPEN) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:690:3: lv_constraint_1_1= ruleFocusConcept
                    {
                     
                    	        newCompositeNode(grammarAccess.getParentOfAccess().getConstraintFocusConceptParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleParentOf1574);
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:705:8: lv_constraint_1_2= ruleNestableExpression
                    {
                     
                    	        newCompositeNode(grammarAccess.getParentOfAccess().getConstraintNestableExpressionParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleNestableExpression_in_ruleParentOf1593);
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


    // $ANTLR start "entryRuleAncestorOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:731:1: entryRuleAncestorOf returns [EObject current=null] : iv_ruleAncestorOf= ruleAncestorOf EOF ;
    public final EObject entryRuleAncestorOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAncestorOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:732:2: (iv_ruleAncestorOf= ruleAncestorOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:733:2: iv_ruleAncestorOf= ruleAncestorOf EOF
            {
             newCompositeNode(grammarAccess.getAncestorOfRule()); 
            pushFollow(FOLLOW_ruleAncestorOf_in_entryRuleAncestorOf1632);
            iv_ruleAncestorOf=ruleAncestorOf();

            state._fsp--;

             current =iv_ruleAncestorOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAncestorOf1642); 

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
    // $ANTLR end "entryRuleAncestorOf"


    // $ANTLR start "ruleAncestorOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:740:1: ruleAncestorOf returns [EObject current=null] : (this_GT_0= RULE_GT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) ;
    public final EObject ruleAncestorOf() throws RecognitionException {
        EObject current = null;

        Token this_GT_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:743:28: ( (this_GT_0= RULE_GT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:744:1: (this_GT_0= RULE_GT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:744:1: (this_GT_0= RULE_GT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:744:2: this_GT_0= RULE_GT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            {
            this_GT_0=(Token)match(input,RULE_GT,FOLLOW_RULE_GT_in_ruleAncestorOf1678); 
             
                newLeafNode(this_GT_0, grammarAccess.getAncestorOfAccess().getGTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:748:1: ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:749:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:749:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:750:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:750:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==RULE_CARET||LA12_0==RULE_WILDCARD||LA12_0==RULE_DIGIT_NONZERO) ) {
                alt12=1;
            }
            else if ( (LA12_0==RULE_ROUND_OPEN) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:751:3: lv_constraint_1_1= ruleFocusConcept
                    {
                     
                    	        newCompositeNode(grammarAccess.getAncestorOfAccess().getConstraintFocusConceptParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleAncestorOf1700);
                    lv_constraint_1_1=ruleFocusConcept();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAncestorOfRule());
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:766:8: lv_constraint_1_2= ruleNestableExpression
                    {
                     
                    	        newCompositeNode(grammarAccess.getAncestorOfAccess().getConstraintNestableExpressionParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleNestableExpression_in_ruleAncestorOf1719);
                    lv_constraint_1_2=ruleNestableExpression();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAncestorOfRule());
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
    // $ANTLR end "ruleAncestorOf"


    // $ANTLR start "entryRuleAncestorOrSelfOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:792:1: entryRuleAncestorOrSelfOf returns [EObject current=null] : iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF ;
    public final EObject entryRuleAncestorOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAncestorOrSelfOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:793:2: (iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:794:2: iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF
            {
             newCompositeNode(grammarAccess.getAncestorOrSelfOfRule()); 
            pushFollow(FOLLOW_ruleAncestorOrSelfOf_in_entryRuleAncestorOrSelfOf1758);
            iv_ruleAncestorOrSelfOf=ruleAncestorOrSelfOf();

            state._fsp--;

             current =iv_ruleAncestorOrSelfOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAncestorOrSelfOf1768); 

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
    // $ANTLR end "entryRuleAncestorOrSelfOf"


    // $ANTLR start "ruleAncestorOrSelfOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:801:1: ruleAncestorOrSelfOf returns [EObject current=null] : (this_DBL_GT_0= RULE_DBL_GT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) ;
    public final EObject ruleAncestorOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_GT_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:804:28: ( (this_DBL_GT_0= RULE_DBL_GT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:805:1: (this_DBL_GT_0= RULE_DBL_GT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:805:1: (this_DBL_GT_0= RULE_DBL_GT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:805:2: this_DBL_GT_0= RULE_DBL_GT ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            {
            this_DBL_GT_0=(Token)match(input,RULE_DBL_GT,FOLLOW_RULE_DBL_GT_in_ruleAncestorOrSelfOf1804); 
             
                newLeafNode(this_DBL_GT_0, grammarAccess.getAncestorOrSelfOfAccess().getDBL_GTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:809:1: ( ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:810:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:810:1: ( (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:811:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:811:1: (lv_constraint_1_1= ruleFocusConcept | lv_constraint_1_2= ruleNestableExpression )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==RULE_CARET||LA13_0==RULE_WILDCARD||LA13_0==RULE_DIGIT_NONZERO) ) {
                alt13=1;
            }
            else if ( (LA13_0==RULE_ROUND_OPEN) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:812:3: lv_constraint_1_1= ruleFocusConcept
                    {
                     
                    	        newCompositeNode(grammarAccess.getAncestorOrSelfOfAccess().getConstraintFocusConceptParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleAncestorOrSelfOf1826);
                    lv_constraint_1_1=ruleFocusConcept();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAncestorOrSelfOfRule());
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:827:8: lv_constraint_1_2= ruleNestableExpression
                    {
                     
                    	        newCompositeNode(grammarAccess.getAncestorOrSelfOfAccess().getConstraintNestableExpressionParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleNestableExpression_in_ruleAncestorOrSelfOf1845);
                    lv_constraint_1_2=ruleNestableExpression();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAncestorOrSelfOfRule());
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
    // $ANTLR end "ruleAncestorOrSelfOf"


    // $ANTLR start "entryRuleMemberOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:853:1: entryRuleMemberOf returns [EObject current=null] : iv_ruleMemberOf= ruleMemberOf EOF ;
    public final EObject entryRuleMemberOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMemberOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:854:2: (iv_ruleMemberOf= ruleMemberOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:855:2: iv_ruleMemberOf= ruleMemberOf EOF
            {
             newCompositeNode(grammarAccess.getMemberOfRule()); 
            pushFollow(FOLLOW_ruleMemberOf_in_entryRuleMemberOf1884);
            iv_ruleMemberOf=ruleMemberOf();

            state._fsp--;

             current =iv_ruleMemberOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleMemberOf1894); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:862:1: ruleMemberOf returns [EObject current=null] : (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) ;
    public final EObject ruleMemberOf() throws RecognitionException {
        EObject current = null;

        Token this_CARET_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:865:28: ( (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:866:1: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:866:1: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:866:2: this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            {
            this_CARET_0=(Token)match(input,RULE_CARET,FOLLOW_RULE_CARET_in_ruleMemberOf1930); 
             
                newLeafNode(this_CARET_0, grammarAccess.getMemberOfAccess().getCARETTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:870:1: ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:871:1: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:871:1: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:872:1: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:872:1: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_DIGIT_NONZERO) ) {
                alt14=1;
            }
            else if ( (LA14_0==RULE_WILDCARD) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:873:3: lv_constraint_1_1= ruleConceptReference
                    {
                     
                    	        newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintConceptReferenceParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleMemberOf1952);
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:888:8: lv_constraint_1_2= ruleAny
                    {
                     
                    	        newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintAnyParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleAny_in_ruleMemberOf1971);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:914:1: entryRuleConceptReference returns [EObject current=null] : iv_ruleConceptReference= ruleConceptReference EOF ;
    public final EObject entryRuleConceptReference() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptReference = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:915:2: (iv_ruleConceptReference= ruleConceptReference EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:916:2: iv_ruleConceptReference= ruleConceptReference EOF
            {
             newCompositeNode(grammarAccess.getConceptReferenceRule()); 
            pushFollow(FOLLOW_ruleConceptReference_in_entryRuleConceptReference2010);
            iv_ruleConceptReference=ruleConceptReference();

            state._fsp--;

             current =iv_ruleConceptReference; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleConceptReference2020); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:923:1: ruleConceptReference returns [EObject current=null] : ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? ) ;
    public final EObject ruleConceptReference() throws RecognitionException {
        EObject current = null;

        Token this_PIPE_1=null;
        Token this_PIPE_3=null;
        AntlrDatatypeRuleToken lv_id_0_0 = null;

        AntlrDatatypeRuleToken lv_term_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:926:28: ( ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:927:1: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:927:1: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:927:2: ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )?
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:927:2: ( (lv_id_0_0= ruleSnomedIdentifier ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:928:1: (lv_id_0_0= ruleSnomedIdentifier )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:928:1: (lv_id_0_0= ruleSnomedIdentifier )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:929:3: lv_id_0_0= ruleSnomedIdentifier
            {
             
            	        newCompositeNode(grammarAccess.getConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleSnomedIdentifier_in_ruleConceptReference2066);
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

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:945:2: (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==RULE_PIPE) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:945:3: this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE
                    {
                    this_PIPE_1=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConceptReference2078); 
                     
                        newLeafNode(this_PIPE_1, grammarAccess.getConceptReferenceAccess().getPIPETerminalRuleCall_1_0()); 
                        
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:949:1: ( (lv_term_2_0= ruleTerm ) )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:950:1: (lv_term_2_0= ruleTerm )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:950:1: (lv_term_2_0= ruleTerm )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:951:3: lv_term_2_0= ruleTerm
                    {
                     
                    	        newCompositeNode(grammarAccess.getConceptReferenceAccess().getTermTermParserRuleCall_1_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleTerm_in_ruleConceptReference2098);
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

                    this_PIPE_3=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConceptReference2109); 
                     
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:979:1: entryRuleAny returns [EObject current=null] : iv_ruleAny= ruleAny EOF ;
    public final EObject entryRuleAny() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAny = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:980:2: (iv_ruleAny= ruleAny EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:981:2: iv_ruleAny= ruleAny EOF
            {
             newCompositeNode(grammarAccess.getAnyRule()); 
            pushFollow(FOLLOW_ruleAny_in_entryRuleAny2146);
            iv_ruleAny=ruleAny();

            state._fsp--;

             current =iv_ruleAny; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAny2156); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:988:1: ruleAny returns [EObject current=null] : (this_WILDCARD_0= RULE_WILDCARD () ) ;
    public final EObject ruleAny() throws RecognitionException {
        EObject current = null;

        Token this_WILDCARD_0=null;

         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:991:28: ( (this_WILDCARD_0= RULE_WILDCARD () ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:992:1: (this_WILDCARD_0= RULE_WILDCARD () )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:992:1: (this_WILDCARD_0= RULE_WILDCARD () )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:992:2: this_WILDCARD_0= RULE_WILDCARD ()
            {
            this_WILDCARD_0=(Token)match(input,RULE_WILDCARD,FOLLOW_RULE_WILDCARD_in_ruleAny2192); 
             
                newLeafNode(this_WILDCARD_0, grammarAccess.getAnyAccess().getWILDCARDTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:996:1: ()
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:997:5: 
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


    // $ANTLR start "entryRuleRefinement"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1010:1: entryRuleRefinement returns [EObject current=null] : iv_ruleRefinement= ruleRefinement EOF ;
    public final EObject entryRuleRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinement = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1011:2: (iv_ruleRefinement= ruleRefinement EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1012:2: iv_ruleRefinement= ruleRefinement EOF
            {
             newCompositeNode(grammarAccess.getRefinementRule()); 
            pushFollow(FOLLOW_ruleRefinement_in_entryRuleRefinement2236);
            iv_ruleRefinement=ruleRefinement();

            state._fsp--;

             current =iv_ruleRefinement; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRefinement2246); 

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
    // $ANTLR end "entryRuleRefinement"


    // $ANTLR start "ruleRefinement"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1019:1: ruleRefinement returns [EObject current=null] : this_AttributeConstraint_0= ruleAttributeConstraint ;
    public final EObject ruleRefinement() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeConstraint_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1022:28: (this_AttributeConstraint_0= ruleAttributeConstraint )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1024:5: this_AttributeConstraint_0= ruleAttributeConstraint
            {
             
                    newCompositeNode(grammarAccess.getRefinementAccess().getAttributeConstraintParserRuleCall()); 
                
            pushFollow(FOLLOW_ruleAttributeConstraint_in_ruleRefinement2292);
            this_AttributeConstraint_0=ruleAttributeConstraint();

            state._fsp--;

             
                    current = this_AttributeConstraint_0; 
                    afterParserOrEnumRuleCall();
                

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
    // $ANTLR end "ruleRefinement"


    // $ANTLR start "entryRuleAttributeConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1040:1: entryRuleAttributeConstraint returns [EObject current=null] : iv_ruleAttributeConstraint= ruleAttributeConstraint EOF ;
    public final EObject entryRuleAttributeConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1041:2: (iv_ruleAttributeConstraint= ruleAttributeConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1042:2: iv_ruleAttributeConstraint= ruleAttributeConstraint EOF
            {
             newCompositeNode(grammarAccess.getAttributeConstraintRule()); 
            pushFollow(FOLLOW_ruleAttributeConstraint_in_entryRuleAttributeConstraint2326);
            iv_ruleAttributeConstraint=ruleAttributeConstraint();

            state._fsp--;

             current =iv_ruleAttributeConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeConstraint2336); 

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
    // $ANTLR end "entryRuleAttributeConstraint"


    // $ANTLR start "ruleAttributeConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1049:1: ruleAttributeConstraint returns [EObject current=null] : ( ( ( (lv_attribute_0_1= ruleConceptReference | lv_attribute_0_2= ruleAny ) ) ) ( (lv_comparison_1_0= ruleComparison ) ) ) ;
    public final EObject ruleAttributeConstraint() throws RecognitionException {
        EObject current = null;

        EObject lv_attribute_0_1 = null;

        EObject lv_attribute_0_2 = null;

        EObject lv_comparison_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1052:28: ( ( ( ( (lv_attribute_0_1= ruleConceptReference | lv_attribute_0_2= ruleAny ) ) ) ( (lv_comparison_1_0= ruleComparison ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1053:1: ( ( ( (lv_attribute_0_1= ruleConceptReference | lv_attribute_0_2= ruleAny ) ) ) ( (lv_comparison_1_0= ruleComparison ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1053:1: ( ( ( (lv_attribute_0_1= ruleConceptReference | lv_attribute_0_2= ruleAny ) ) ) ( (lv_comparison_1_0= ruleComparison ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1053:2: ( ( (lv_attribute_0_1= ruleConceptReference | lv_attribute_0_2= ruleAny ) ) ) ( (lv_comparison_1_0= ruleComparison ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1053:2: ( ( (lv_attribute_0_1= ruleConceptReference | lv_attribute_0_2= ruleAny ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1054:1: ( (lv_attribute_0_1= ruleConceptReference | lv_attribute_0_2= ruleAny ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1054:1: ( (lv_attribute_0_1= ruleConceptReference | lv_attribute_0_2= ruleAny ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1055:1: (lv_attribute_0_1= ruleConceptReference | lv_attribute_0_2= ruleAny )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1055:1: (lv_attribute_0_1= ruleConceptReference | lv_attribute_0_2= ruleAny )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==RULE_DIGIT_NONZERO) ) {
                alt16=1;
            }
            else if ( (LA16_0==RULE_WILDCARD) ) {
                alt16=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1056:3: lv_attribute_0_1= ruleConceptReference
                    {
                     
                    	        newCompositeNode(grammarAccess.getAttributeConstraintAccess().getAttributeConceptReferenceParserRuleCall_0_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleAttributeConstraint2384);
                    lv_attribute_0_1=ruleConceptReference();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
                    	        }
                           		set(
                           			current, 
                           			"attribute",
                            		lv_attribute_0_1, 
                            		"ConceptReference");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1071:8: lv_attribute_0_2= ruleAny
                    {
                     
                    	        newCompositeNode(grammarAccess.getAttributeConstraintAccess().getAttributeAnyParserRuleCall_0_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleAny_in_ruleAttributeConstraint2403);
                    lv_attribute_0_2=ruleAny();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
                    	        }
                           		set(
                           			current, 
                           			"attribute",
                            		lv_attribute_0_2, 
                            		"Any");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;

            }


            }


            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1089:2: ( (lv_comparison_1_0= ruleComparison ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1090:1: (lv_comparison_1_0= ruleComparison )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1090:1: (lv_comparison_1_0= ruleComparison )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1091:3: lv_comparison_1_0= ruleComparison
            {
             
            	        newCompositeNode(grammarAccess.getAttributeConstraintAccess().getComparisonComparisonParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleComparison_in_ruleAttributeConstraint2427);
            lv_comparison_1_0=ruleComparison();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
            	        }
                   		set(
                   			current, 
                   			"comparison",
                    		lv_comparison_1_0, 
                    		"Comparison");
            	        afterParserOrEnumRuleCall();
            	    

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
    // $ANTLR end "ruleAttributeConstraint"


    // $ANTLR start "entryRuleComparison"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1115:1: entryRuleComparison returns [EObject current=null] : iv_ruleComparison= ruleComparison EOF ;
    public final EObject entryRuleComparison() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComparison = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1116:2: (iv_ruleComparison= ruleComparison EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1117:2: iv_ruleComparison= ruleComparison EOF
            {
             newCompositeNode(grammarAccess.getComparisonRule()); 
            pushFollow(FOLLOW_ruleComparison_in_entryRuleComparison2463);
            iv_ruleComparison=ruleComparison();

            state._fsp--;

             current =iv_ruleComparison; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleComparison2473); 

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
    // $ANTLR end "entryRuleComparison"


    // $ANTLR start "ruleComparison"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1124:1: ruleComparison returns [EObject current=null] : (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals ) ;
    public final EObject ruleComparison() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeValueEquals_0 = null;

        EObject this_AttributeValueNotEquals_1 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1127:28: ( (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1128:1: (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1128:1: (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==RULE_EQUAL) ) {
                alt17=1;
            }
            else if ( (LA17_0==RULE_NOT_EQUAL) ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1129:5: this_AttributeValueEquals_0= ruleAttributeValueEquals
                    {
                     
                            newCompositeNode(grammarAccess.getComparisonAccess().getAttributeValueEqualsParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleAttributeValueEquals_in_ruleComparison2520);
                    this_AttributeValueEquals_0=ruleAttributeValueEquals();

                    state._fsp--;

                     
                            current = this_AttributeValueEquals_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1139:5: this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals
                    {
                     
                            newCompositeNode(grammarAccess.getComparisonAccess().getAttributeValueNotEqualsParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleAttributeValueNotEquals_in_ruleComparison2547);
                    this_AttributeValueNotEquals_1=ruleAttributeValueNotEquals();

                    state._fsp--;

                     
                            current = this_AttributeValueNotEquals_1; 
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
    // $ANTLR end "ruleComparison"


    // $ANTLR start "entryRuleAttributeValueEquals"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1155:1: entryRuleAttributeValueEquals returns [EObject current=null] : iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF ;
    public final EObject entryRuleAttributeValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValueEquals = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1156:2: (iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1157:2: iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF
            {
             newCompositeNode(grammarAccess.getAttributeValueEqualsRule()); 
            pushFollow(FOLLOW_ruleAttributeValueEquals_in_entryRuleAttributeValueEquals2582);
            iv_ruleAttributeValueEquals=ruleAttributeValueEquals();

            state._fsp--;

             current =iv_ruleAttributeValueEquals; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeValueEquals2592); 

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
    // $ANTLR end "entryRuleAttributeValueEquals"


    // $ANTLR start "ruleAttributeValueEquals"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1164:1: ruleAttributeValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) ;
    public final EObject ruleAttributeValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        EObject lv_constraint_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1167:28: ( (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1168:1: (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1168:1: (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1168:2: this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FOLLOW_RULE_EQUAL_in_ruleAttributeValueEquals2628); 
             
                newLeafNode(this_EQUAL_0, grammarAccess.getAttributeValueEqualsAccess().getEQUALTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1172:1: ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1173:1: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1173:1: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1174:3: lv_constraint_1_0= ruleSimpleExpressionConstraint
            {
             
            	        newCompositeNode(grammarAccess.getAttributeValueEqualsAccess().getConstraintSimpleExpressionConstraintParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSimpleExpressionConstraint_in_ruleAttributeValueEquals2648);
            lv_constraint_1_0=ruleSimpleExpressionConstraint();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttributeValueEqualsRule());
            	        }
                   		set(
                   			current, 
                   			"constraint",
                    		lv_constraint_1_0, 
                    		"SimpleExpressionConstraint");
            	        afterParserOrEnumRuleCall();
            	    

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
    // $ANTLR end "ruleAttributeValueEquals"


    // $ANTLR start "entryRuleAttributeValueNotEquals"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1198:1: entryRuleAttributeValueNotEquals returns [EObject current=null] : iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF ;
    public final EObject entryRuleAttributeValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValueNotEquals = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1199:2: (iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1200:2: iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF
            {
             newCompositeNode(grammarAccess.getAttributeValueNotEqualsRule()); 
            pushFollow(FOLLOW_ruleAttributeValueNotEquals_in_entryRuleAttributeValueNotEquals2684);
            iv_ruleAttributeValueNotEquals=ruleAttributeValueNotEquals();

            state._fsp--;

             current =iv_ruleAttributeValueNotEquals; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeValueNotEquals2694); 

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
    // $ANTLR end "entryRuleAttributeValueNotEquals"


    // $ANTLR start "ruleAttributeValueNotEquals"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1207:1: ruleAttributeValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) ;
    public final EObject ruleAttributeValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        EObject lv_constraint_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1210:28: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1211:1: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1211:1: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1211:2: this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FOLLOW_RULE_NOT_EQUAL_in_ruleAttributeValueNotEquals2730); 
             
                newLeafNode(this_NOT_EQUAL_0, grammarAccess.getAttributeValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1215:1: ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1216:1: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1216:1: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1217:3: lv_constraint_1_0= ruleSimpleExpressionConstraint
            {
             
            	        newCompositeNode(grammarAccess.getAttributeValueNotEqualsAccess().getConstraintSimpleExpressionConstraintParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSimpleExpressionConstraint_in_ruleAttributeValueNotEquals2750);
            lv_constraint_1_0=ruleSimpleExpressionConstraint();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttributeValueNotEqualsRule());
            	        }
                   		set(
                   			current, 
                   			"constraint",
                    		lv_constraint_1_0, 
                    		"SimpleExpressionConstraint");
            	        afterParserOrEnumRuleCall();
            	    

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
    // $ANTLR end "ruleAttributeValueNotEquals"


    // $ANTLR start "entryRuleNestableExpression"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1241:1: entryRuleNestableExpression returns [EObject current=null] : iv_ruleNestableExpression= ruleNestableExpression EOF ;
    public final EObject entryRuleNestableExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestableExpression = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1242:2: (iv_ruleNestableExpression= ruleNestableExpression EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1243:2: iv_ruleNestableExpression= ruleNestableExpression EOF
            {
             newCompositeNode(grammarAccess.getNestableExpressionRule()); 
            pushFollow(FOLLOW_ruleNestableExpression_in_entryRuleNestableExpression2786);
            iv_ruleNestableExpression=ruleNestableExpression();

            state._fsp--;

             current =iv_ruleNestableExpression; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNestableExpression2796); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1250:1: ruleNestableExpression returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN this_ExpressionConstraint_1= ruleExpressionConstraint this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestableExpression() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject this_ExpressionConstraint_1 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1253:28: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN this_ExpressionConstraint_1= ruleExpressionConstraint this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1254:1: (this_ROUND_OPEN_0= RULE_ROUND_OPEN this_ExpressionConstraint_1= ruleExpressionConstraint this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1254:1: (this_ROUND_OPEN_0= RULE_ROUND_OPEN this_ExpressionConstraint_1= ruleExpressionConstraint this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1254:2: this_ROUND_OPEN_0= RULE_ROUND_OPEN this_ExpressionConstraint_1= ruleExpressionConstraint this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FOLLOW_RULE_ROUND_OPEN_in_ruleNestableExpression2832); 
             
                newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestableExpressionAccess().getROUND_OPENTerminalRuleCall_0()); 
                
             
                    newCompositeNode(grammarAccess.getNestableExpressionAccess().getExpressionConstraintParserRuleCall_1()); 
                
            pushFollow(FOLLOW_ruleExpressionConstraint_in_ruleNestableExpression2853);
            this_ExpressionConstraint_1=ruleExpressionConstraint();

            state._fsp--;

             
                    current = this_ExpressionConstraint_1; 
                    afterParserOrEnumRuleCall();
                
            this_ROUND_CLOSE_2=(Token)match(input,RULE_ROUND_CLOSE,FOLLOW_RULE_ROUND_CLOSE_in_ruleNestableExpression2863); 
             
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1279:1: entryRuleSnomedIdentifier returns [String current=null] : iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF ;
    public final String entryRuleSnomedIdentifier() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSnomedIdentifier = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1283:2: (iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1284:2: iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF
            {
             newCompositeNode(grammarAccess.getSnomedIdentifierRule()); 
            pushFollow(FOLLOW_ruleSnomedIdentifier_in_entryRuleSnomedIdentifier2905);
            iv_ruleSnomedIdentifier=ruleSnomedIdentifier();

            state._fsp--;

             current =iv_ruleSnomedIdentifier.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSnomedIdentifier2916); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1294:1: ruleSnomedIdentifier returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) ;
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
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1298:28: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1299:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1299:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1299:6: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            {
            this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2960); 

            		current.merge(this_DIGIT_NONZERO_0);
                
             
                newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1306:1: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==RULE_DIGIT_NONZERO) ) {
                alt18=1;
            }
            else if ( (LA18_0==RULE_ZERO) ) {
                alt18=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1306:6: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2981); 

                    		current.merge(this_DIGIT_NONZERO_1);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_1_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1314:10: this_ZERO_2= RULE_ZERO
                    {
                    this_ZERO_2=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3007); 

                    		current.merge(this_ZERO_2);
                        
                     
                        newLeafNode(this_ZERO_2, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_1_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1321:2: (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==RULE_DIGIT_NONZERO) ) {
                alt19=1;
            }
            else if ( (LA19_0==RULE_ZERO) ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1321:7: this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_3=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3029); 

                    		current.merge(this_DIGIT_NONZERO_3);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_2_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1329:10: this_ZERO_4= RULE_ZERO
                    {
                    this_ZERO_4=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3055); 

                    		current.merge(this_ZERO_4);
                        
                     
                        newLeafNode(this_ZERO_4, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_2_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1336:2: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==RULE_DIGIT_NONZERO) ) {
                alt20=1;
            }
            else if ( (LA20_0==RULE_ZERO) ) {
                alt20=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1336:7: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3077); 

                    		current.merge(this_DIGIT_NONZERO_5);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_3_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1344:10: this_ZERO_6= RULE_ZERO
                    {
                    this_ZERO_6=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3103); 

                    		current.merge(this_ZERO_6);
                        
                     
                        newLeafNode(this_ZERO_6, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_3_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1351:2: (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==RULE_DIGIT_NONZERO) ) {
                alt21=1;
            }
            else if ( (LA21_0==RULE_ZERO) ) {
                alt21=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1351:7: this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_7=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3125); 

                    		current.merge(this_DIGIT_NONZERO_7);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_4_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1359:10: this_ZERO_8= RULE_ZERO
                    {
                    this_ZERO_8=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3151); 

                    		current.merge(this_ZERO_8);
                        
                     
                        newLeafNode(this_ZERO_8, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_4_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1366:2: (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            int cnt22=0;
            loop22:
            do {
                int alt22=3;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==RULE_DIGIT_NONZERO) ) {
                    alt22=1;
                }
                else if ( (LA22_0==RULE_ZERO) ) {
                    alt22=2;
                }


                switch (alt22) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1366:7: this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_9=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3173); 

            	    		current.merge(this_DIGIT_NONZERO_9);
            	        
            	     
            	        newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_5_0()); 
            	        

            	    }
            	    break;
            	case 2 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1374:10: this_ZERO_10= RULE_ZERO
            	    {
            	    this_ZERO_10=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3199); 

            	    		current.merge(this_ZERO_10);
            	        
            	     
            	        newLeafNode(this_ZERO_10, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_5_1()); 
            	        

            	    }
            	    break;

            	default :
            	    if ( cnt22 >= 1 ) break loop22;
                        EarlyExitException eee =
                            new EarlyExitException(22, input);
                        throw eee;
                }
                cnt22++;
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1392:1: entryRuleTerm returns [String current=null] : iv_ruleTerm= ruleTerm EOF ;
    public final String entryRuleTerm() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTerm = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1396:2: (iv_ruleTerm= ruleTerm EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1397:2: iv_ruleTerm= ruleTerm EOF
            {
             newCompositeNode(grammarAccess.getTermRule()); 
            pushFollow(FOLLOW_ruleTerm_in_entryRuleTerm3257);
            iv_ruleTerm=ruleTerm();

            state._fsp--;

             current =iv_ruleTerm.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTerm3268); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1407:1: ruleTerm returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) ;
    public final AntlrDatatypeRuleToken ruleTerm() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WS_1=null;
        AntlrDatatypeRuleToken this_TermCharacter_0 = null;

        AntlrDatatypeRuleToken this_TermCharacter_2 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1411:28: ( ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1412:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1412:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1412:2: (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1412:2: (this_TermCharacter_0= ruleTermCharacter )+
            int cnt23=0;
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( ((LA23_0>=RULE_OR && LA23_0<=RULE_COMMA)||LA23_0==RULE_COLON||(LA23_0>=RULE_LT && LA23_0<=RULE_DBL_LT)||(LA23_0>=RULE_GT && LA23_0<=RULE_CARET)||LA23_0==RULE_EQUAL||(LA23_0>=RULE_ROUND_OPEN && LA23_0<=RULE_ZERO)||(LA23_0>=RULE_NOT && LA23_0<=RULE_OTHER_CHARACTER)) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1413:5: this_TermCharacter_0= ruleTermCharacter
            	    {
            	     
            	            newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_0()); 
            	        
            	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm3320);
            	    this_TermCharacter_0=ruleTermCharacter();

            	    state._fsp--;


            	    		current.merge(this_TermCharacter_0);
            	        
            	     
            	            afterParserOrEnumRuleCall();
            	        

            	    }
            	    break;

            	default :
            	    if ( cnt23 >= 1 ) break loop23;
                        EarlyExitException eee =
                            new EarlyExitException(23, input);
                        throw eee;
                }
                cnt23++;
            } while (true);

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1423:3: ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==RULE_WS) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1423:4: (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1423:4: (this_WS_1= RULE_WS )+
            	    int cnt24=0;
            	    loop24:
            	    do {
            	        int alt24=2;
            	        int LA24_0 = input.LA(1);

            	        if ( (LA24_0==RULE_WS) ) {
            	            alt24=1;
            	        }


            	        switch (alt24) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1423:9: this_WS_1= RULE_WS
            	    	    {
            	    	    this_WS_1=(Token)match(input,RULE_WS,FOLLOW_RULE_WS_in_ruleTerm3344); 

            	    	    		current.merge(this_WS_1);
            	    	        
            	    	     
            	    	        newLeafNode(this_WS_1, grammarAccess.getTermAccess().getWSTerminalRuleCall_1_0()); 
            	    	        

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt24 >= 1 ) break loop24;
            	                EarlyExitException eee =
            	                    new EarlyExitException(24, input);
            	                throw eee;
            	        }
            	        cnt24++;
            	    } while (true);

            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1430:3: (this_TermCharacter_2= ruleTermCharacter )+
            	    int cnt25=0;
            	    loop25:
            	    do {
            	        int alt25=2;
            	        int LA25_0 = input.LA(1);

            	        if ( ((LA25_0>=RULE_OR && LA25_0<=RULE_COMMA)||LA25_0==RULE_COLON||(LA25_0>=RULE_LT && LA25_0<=RULE_DBL_LT)||(LA25_0>=RULE_GT && LA25_0<=RULE_CARET)||LA25_0==RULE_EQUAL||(LA25_0>=RULE_ROUND_OPEN && LA25_0<=RULE_ZERO)||(LA25_0>=RULE_NOT && LA25_0<=RULE_OTHER_CHARACTER)) ) {
            	            alt25=1;
            	        }


            	        switch (alt25) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1431:5: this_TermCharacter_2= ruleTermCharacter
            	    	    {
            	    	     
            	    	            newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_1_1()); 
            	    	        
            	    	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm3374);
            	    	    this_TermCharacter_2=ruleTermCharacter();

            	    	    state._fsp--;


            	    	    		current.merge(this_TermCharacter_2);
            	    	        
            	    	     
            	    	            afterParserOrEnumRuleCall();
            	    	        

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt25 >= 1 ) break loop25;
            	                EarlyExitException eee =
            	                    new EarlyExitException(25, input);
            	                throw eee;
            	        }
            	        cnt25++;
            	    } while (true);


            	    }
            	    break;

            	default :
            	    break loop26;
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1452:1: entryRuleTermCharacter returns [String current=null] : iv_ruleTermCharacter= ruleTermCharacter EOF ;
    public final String entryRuleTermCharacter() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTermCharacter = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1456:2: (iv_ruleTermCharacter= ruleTermCharacter EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1457:2: iv_ruleTermCharacter= ruleTermCharacter EOF
            {
             newCompositeNode(grammarAccess.getTermCharacterRule()); 
            pushFollow(FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter3434);
            iv_ruleTermCharacter=ruleTermCharacter();

            state._fsp--;

             current =iv_ruleTermCharacter.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTermCharacter3445); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1467:1: ruleTermCharacter returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_AND_4= RULE_AND | this_OR_5= RULE_OR | this_NOT_6= RULE_NOT | this_ZERO_7= RULE_ZERO | this_DIGIT_NONZERO_8= RULE_DIGIT_NONZERO | this_LETTER_9= RULE_LETTER | this_CARET_10= RULE_CARET | this_EQUAL_11= RULE_EQUAL | this_PLUS_12= RULE_PLUS | this_CURLY_OPEN_13= RULE_CURLY_OPEN | this_CURLY_CLOSE_14= RULE_CURLY_CLOSE | this_ROUND_OPEN_15= RULE_ROUND_OPEN | this_ROUND_CLOSE_16= RULE_ROUND_CLOSE | this_SQUARE_OPEN_17= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_18= RULE_SQUARE_CLOSE | this_DOT_19= RULE_DOT | this_COLON_20= RULE_COLON | this_COMMA_21= RULE_COMMA | this_OTHER_CHARACTER_22= RULE_OTHER_CHARACTER ) ;
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
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1471:28: ( (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_AND_4= RULE_AND | this_OR_5= RULE_OR | this_NOT_6= RULE_NOT | this_ZERO_7= RULE_ZERO | this_DIGIT_NONZERO_8= RULE_DIGIT_NONZERO | this_LETTER_9= RULE_LETTER | this_CARET_10= RULE_CARET | this_EQUAL_11= RULE_EQUAL | this_PLUS_12= RULE_PLUS | this_CURLY_OPEN_13= RULE_CURLY_OPEN | this_CURLY_CLOSE_14= RULE_CURLY_CLOSE | this_ROUND_OPEN_15= RULE_ROUND_OPEN | this_ROUND_CLOSE_16= RULE_ROUND_CLOSE | this_SQUARE_OPEN_17= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_18= RULE_SQUARE_CLOSE | this_DOT_19= RULE_DOT | this_COLON_20= RULE_COLON | this_COMMA_21= RULE_COMMA | this_OTHER_CHARACTER_22= RULE_OTHER_CHARACTER ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1472:1: (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_AND_4= RULE_AND | this_OR_5= RULE_OR | this_NOT_6= RULE_NOT | this_ZERO_7= RULE_ZERO | this_DIGIT_NONZERO_8= RULE_DIGIT_NONZERO | this_LETTER_9= RULE_LETTER | this_CARET_10= RULE_CARET | this_EQUAL_11= RULE_EQUAL | this_PLUS_12= RULE_PLUS | this_CURLY_OPEN_13= RULE_CURLY_OPEN | this_CURLY_CLOSE_14= RULE_CURLY_CLOSE | this_ROUND_OPEN_15= RULE_ROUND_OPEN | this_ROUND_CLOSE_16= RULE_ROUND_CLOSE | this_SQUARE_OPEN_17= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_18= RULE_SQUARE_CLOSE | this_DOT_19= RULE_DOT | this_COLON_20= RULE_COLON | this_COMMA_21= RULE_COMMA | this_OTHER_CHARACTER_22= RULE_OTHER_CHARACTER )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1472:1: (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_AND_4= RULE_AND | this_OR_5= RULE_OR | this_NOT_6= RULE_NOT | this_ZERO_7= RULE_ZERO | this_DIGIT_NONZERO_8= RULE_DIGIT_NONZERO | this_LETTER_9= RULE_LETTER | this_CARET_10= RULE_CARET | this_EQUAL_11= RULE_EQUAL | this_PLUS_12= RULE_PLUS | this_CURLY_OPEN_13= RULE_CURLY_OPEN | this_CURLY_CLOSE_14= RULE_CURLY_CLOSE | this_ROUND_OPEN_15= RULE_ROUND_OPEN | this_ROUND_CLOSE_16= RULE_ROUND_CLOSE | this_SQUARE_OPEN_17= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_18= RULE_SQUARE_CLOSE | this_DOT_19= RULE_DOT | this_COLON_20= RULE_COLON | this_COMMA_21= RULE_COMMA | this_OTHER_CHARACTER_22= RULE_OTHER_CHARACTER )
            int alt27=23;
            switch ( input.LA(1) ) {
            case RULE_LT:
                {
                alt27=1;
                }
                break;
            case RULE_GT:
                {
                alt27=2;
                }
                break;
            case RULE_DBL_LT:
                {
                alt27=3;
                }
                break;
            case RULE_DBL_GT:
                {
                alt27=4;
                }
                break;
            case RULE_AND:
                {
                alt27=5;
                }
                break;
            case RULE_OR:
                {
                alt27=6;
                }
                break;
            case RULE_NOT:
                {
                alt27=7;
                }
                break;
            case RULE_ZERO:
                {
                alt27=8;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt27=9;
                }
                break;
            case RULE_LETTER:
                {
                alt27=10;
                }
                break;
            case RULE_CARET:
                {
                alt27=11;
                }
                break;
            case RULE_EQUAL:
                {
                alt27=12;
                }
                break;
            case RULE_PLUS:
                {
                alt27=13;
                }
                break;
            case RULE_CURLY_OPEN:
                {
                alt27=14;
                }
                break;
            case RULE_CURLY_CLOSE:
                {
                alt27=15;
                }
                break;
            case RULE_ROUND_OPEN:
                {
                alt27=16;
                }
                break;
            case RULE_ROUND_CLOSE:
                {
                alt27=17;
                }
                break;
            case RULE_SQUARE_OPEN:
                {
                alt27=18;
                }
                break;
            case RULE_SQUARE_CLOSE:
                {
                alt27=19;
                }
                break;
            case RULE_DOT:
                {
                alt27=20;
                }
                break;
            case RULE_COLON:
                {
                alt27=21;
                }
                break;
            case RULE_COMMA:
                {
                alt27=22;
                }
                break;
            case RULE_OTHER_CHARACTER:
                {
                alt27=23;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1472:6: this_LT_0= RULE_LT
                    {
                    this_LT_0=(Token)match(input,RULE_LT,FOLLOW_RULE_LT_in_ruleTermCharacter3489); 

                    		current.merge(this_LT_0);
                        
                     
                        newLeafNode(this_LT_0, grammarAccess.getTermCharacterAccess().getLTTerminalRuleCall_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1480:10: this_GT_1= RULE_GT
                    {
                    this_GT_1=(Token)match(input,RULE_GT,FOLLOW_RULE_GT_in_ruleTermCharacter3515); 

                    		current.merge(this_GT_1);
                        
                     
                        newLeafNode(this_GT_1, grammarAccess.getTermCharacterAccess().getGTTerminalRuleCall_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1488:10: this_DBL_LT_2= RULE_DBL_LT
                    {
                    this_DBL_LT_2=(Token)match(input,RULE_DBL_LT,FOLLOW_RULE_DBL_LT_in_ruleTermCharacter3541); 

                    		current.merge(this_DBL_LT_2);
                        
                     
                        newLeafNode(this_DBL_LT_2, grammarAccess.getTermCharacterAccess().getDBL_LTTerminalRuleCall_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1496:10: this_DBL_GT_3= RULE_DBL_GT
                    {
                    this_DBL_GT_3=(Token)match(input,RULE_DBL_GT,FOLLOW_RULE_DBL_GT_in_ruleTermCharacter3567); 

                    		current.merge(this_DBL_GT_3);
                        
                     
                        newLeafNode(this_DBL_GT_3, grammarAccess.getTermCharacterAccess().getDBL_GTTerminalRuleCall_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1504:10: this_AND_4= RULE_AND
                    {
                    this_AND_4=(Token)match(input,RULE_AND,FOLLOW_RULE_AND_in_ruleTermCharacter3593); 

                    		current.merge(this_AND_4);
                        
                     
                        newLeafNode(this_AND_4, grammarAccess.getTermCharacterAccess().getANDTerminalRuleCall_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1512:10: this_OR_5= RULE_OR
                    {
                    this_OR_5=(Token)match(input,RULE_OR,FOLLOW_RULE_OR_in_ruleTermCharacter3619); 

                    		current.merge(this_OR_5);
                        
                     
                        newLeafNode(this_OR_5, grammarAccess.getTermCharacterAccess().getORTerminalRuleCall_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1520:10: this_NOT_6= RULE_NOT
                    {
                    this_NOT_6=(Token)match(input,RULE_NOT,FOLLOW_RULE_NOT_in_ruleTermCharacter3645); 

                    		current.merge(this_NOT_6);
                        
                     
                        newLeafNode(this_NOT_6, grammarAccess.getTermCharacterAccess().getNOTTerminalRuleCall_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1528:10: this_ZERO_7= RULE_ZERO
                    {
                    this_ZERO_7=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleTermCharacter3671); 

                    		current.merge(this_ZERO_7);
                        
                     
                        newLeafNode(this_ZERO_7, grammarAccess.getTermCharacterAccess().getZEROTerminalRuleCall_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1536:10: this_DIGIT_NONZERO_8= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_8=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter3697); 

                    		current.merge(this_DIGIT_NONZERO_8);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_8, grammarAccess.getTermCharacterAccess().getDIGIT_NONZEROTerminalRuleCall_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1544:10: this_LETTER_9= RULE_LETTER
                    {
                    this_LETTER_9=(Token)match(input,RULE_LETTER,FOLLOW_RULE_LETTER_in_ruleTermCharacter3723); 

                    		current.merge(this_LETTER_9);
                        
                     
                        newLeafNode(this_LETTER_9, grammarAccess.getTermCharacterAccess().getLETTERTerminalRuleCall_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1552:10: this_CARET_10= RULE_CARET
                    {
                    this_CARET_10=(Token)match(input,RULE_CARET,FOLLOW_RULE_CARET_in_ruleTermCharacter3749); 

                    		current.merge(this_CARET_10);
                        
                     
                        newLeafNode(this_CARET_10, grammarAccess.getTermCharacterAccess().getCARETTerminalRuleCall_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1560:10: this_EQUAL_11= RULE_EQUAL
                    {
                    this_EQUAL_11=(Token)match(input,RULE_EQUAL,FOLLOW_RULE_EQUAL_in_ruleTermCharacter3775); 

                    		current.merge(this_EQUAL_11);
                        
                     
                        newLeafNode(this_EQUAL_11, grammarAccess.getTermCharacterAccess().getEQUALTerminalRuleCall_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1568:10: this_PLUS_12= RULE_PLUS
                    {
                    this_PLUS_12=(Token)match(input,RULE_PLUS,FOLLOW_RULE_PLUS_in_ruleTermCharacter3801); 

                    		current.merge(this_PLUS_12);
                        
                     
                        newLeafNode(this_PLUS_12, grammarAccess.getTermCharacterAccess().getPLUSTerminalRuleCall_12()); 
                        

                    }
                    break;
                case 14 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1576:10: this_CURLY_OPEN_13= RULE_CURLY_OPEN
                    {
                    this_CURLY_OPEN_13=(Token)match(input,RULE_CURLY_OPEN,FOLLOW_RULE_CURLY_OPEN_in_ruleTermCharacter3827); 

                    		current.merge(this_CURLY_OPEN_13);
                        
                     
                        newLeafNode(this_CURLY_OPEN_13, grammarAccess.getTermCharacterAccess().getCURLY_OPENTerminalRuleCall_13()); 
                        

                    }
                    break;
                case 15 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1584:10: this_CURLY_CLOSE_14= RULE_CURLY_CLOSE
                    {
                    this_CURLY_CLOSE_14=(Token)match(input,RULE_CURLY_CLOSE,FOLLOW_RULE_CURLY_CLOSE_in_ruleTermCharacter3853); 

                    		current.merge(this_CURLY_CLOSE_14);
                        
                     
                        newLeafNode(this_CURLY_CLOSE_14, grammarAccess.getTermCharacterAccess().getCURLY_CLOSETerminalRuleCall_14()); 
                        

                    }
                    break;
                case 16 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1592:10: this_ROUND_OPEN_15= RULE_ROUND_OPEN
                    {
                    this_ROUND_OPEN_15=(Token)match(input,RULE_ROUND_OPEN,FOLLOW_RULE_ROUND_OPEN_in_ruleTermCharacter3879); 

                    		current.merge(this_ROUND_OPEN_15);
                        
                     
                        newLeafNode(this_ROUND_OPEN_15, grammarAccess.getTermCharacterAccess().getROUND_OPENTerminalRuleCall_15()); 
                        

                    }
                    break;
                case 17 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1600:10: this_ROUND_CLOSE_16= RULE_ROUND_CLOSE
                    {
                    this_ROUND_CLOSE_16=(Token)match(input,RULE_ROUND_CLOSE,FOLLOW_RULE_ROUND_CLOSE_in_ruleTermCharacter3905); 

                    		current.merge(this_ROUND_CLOSE_16);
                        
                     
                        newLeafNode(this_ROUND_CLOSE_16, grammarAccess.getTermCharacterAccess().getROUND_CLOSETerminalRuleCall_16()); 
                        

                    }
                    break;
                case 18 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1608:10: this_SQUARE_OPEN_17= RULE_SQUARE_OPEN
                    {
                    this_SQUARE_OPEN_17=(Token)match(input,RULE_SQUARE_OPEN,FOLLOW_RULE_SQUARE_OPEN_in_ruleTermCharacter3931); 

                    		current.merge(this_SQUARE_OPEN_17);
                        
                     
                        newLeafNode(this_SQUARE_OPEN_17, grammarAccess.getTermCharacterAccess().getSQUARE_OPENTerminalRuleCall_17()); 
                        

                    }
                    break;
                case 19 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1616:10: this_SQUARE_CLOSE_18= RULE_SQUARE_CLOSE
                    {
                    this_SQUARE_CLOSE_18=(Token)match(input,RULE_SQUARE_CLOSE,FOLLOW_RULE_SQUARE_CLOSE_in_ruleTermCharacter3957); 

                    		current.merge(this_SQUARE_CLOSE_18);
                        
                     
                        newLeafNode(this_SQUARE_CLOSE_18, grammarAccess.getTermCharacterAccess().getSQUARE_CLOSETerminalRuleCall_18()); 
                        

                    }
                    break;
                case 20 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1624:10: this_DOT_19= RULE_DOT
                    {
                    this_DOT_19=(Token)match(input,RULE_DOT,FOLLOW_RULE_DOT_in_ruleTermCharacter3983); 

                    		current.merge(this_DOT_19);
                        
                     
                        newLeafNode(this_DOT_19, grammarAccess.getTermCharacterAccess().getDOTTerminalRuleCall_19()); 
                        

                    }
                    break;
                case 21 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1632:10: this_COLON_20= RULE_COLON
                    {
                    this_COLON_20=(Token)match(input,RULE_COLON,FOLLOW_RULE_COLON_in_ruleTermCharacter4009); 

                    		current.merge(this_COLON_20);
                        
                     
                        newLeafNode(this_COLON_20, grammarAccess.getTermCharacterAccess().getCOLONTerminalRuleCall_20()); 
                        

                    }
                    break;
                case 22 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1640:10: this_COMMA_21= RULE_COMMA
                    {
                    this_COMMA_21=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleTermCharacter4035); 

                    		current.merge(this_COMMA_21);
                        
                     
                        newLeafNode(this_COMMA_21, grammarAccess.getTermCharacterAccess().getCOMMATerminalRuleCall_21()); 
                        

                    }
                    break;
                case 23 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1648:10: this_OTHER_CHARACTER_22= RULE_OTHER_CHARACTER
                    {
                    this_OTHER_CHARACTER_22=(Token)match(input,RULE_OTHER_CHARACTER,FOLLOW_RULE_OTHER_CHARACTER_in_ruleTermCharacter4061); 

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
    public static final BitSet FOLLOW_RULE_OR_in_ruleOrExpressionConstraint256 = new BitSet(new long[]{0x000000000042FE00L});
    public static final BitSet FOLLOW_ruleAndExpressionConstraint_in_ruleOrExpressionConstraint276 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_ruleAndExpressionConstraint_in_entryRuleAndExpressionConstraint314 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAndExpressionConstraint324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint371 = new BitSet(new long[]{0x0000000000000062L});
    public static final BitSet FOLLOW_RULE_AND_in_ruleAndExpressionConstraint392 = new BitSet(new long[]{0x000000000042FE00L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleAndExpressionConstraint408 = new BitSet(new long[]{0x000000000042FE00L});
    public static final BitSet FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint429 = new BitSet(new long[]{0x0000000000000062L});
    public static final BitSet FOLLOW_ruleExclusionExpressionConstraint_in_entryRuleExclusionExpressionConstraint467 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleExclusionExpressionConstraint477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRefinedExpressionConstraint_in_ruleExclusionExpressionConstraint524 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_RULE_MINUS_in_ruleExclusionExpressionConstraint544 = new BitSet(new long[]{0x000000000042FE00L});
    public static final BitSet FOLLOW_ruleRefinedExpressionConstraint_in_ruleExclusionExpressionConstraint564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRefinedExpressionConstraint_in_entryRuleRefinedExpressionConstraint602 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRefinedExpressionConstraint612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSimpleExpressionConstraint_in_ruleRefinedExpressionConstraint658 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_RULE_COLON_in_ruleRefinedExpressionConstraint670 = new BitSet(new long[]{0x000000000042FE00L});
    public static final BitSet FOLLOW_ruleRefinement_in_ruleRefinedExpressionConstraint690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSimpleExpressionConstraint_in_entryRuleSimpleExpressionConstraint728 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSimpleExpressionConstraint738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleChildOf_in_ruleSimpleExpressionConstraint785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOf_in_ruleSimpleExpressionConstraint812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOrSelfOf_in_ruleSimpleExpressionConstraint839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleParentOf_in_ruleSimpleExpressionConstraint866 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAncestorOf_in_ruleSimpleExpressionConstraint893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAncestorOrSelfOf_in_ruleSimpleExpressionConstraint920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleSimpleExpressionConstraint947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_entryRuleFocusConcept982 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFocusConcept992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberOf_in_ruleFocusConcept1039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleFocusConcept1066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_ruleFocusConcept1093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleChildOf_in_entryRuleChildOf1128 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleChildOf1138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LT_EM_in_ruleChildOf1174 = new BitSet(new long[]{0x000000000052FE00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleChildOf1196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_ruleChildOf1215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOf_in_entryRuleDescendantOf1254 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDescendantOf1264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LT_in_ruleDescendantOf1300 = new BitSet(new long[]{0x000000000052FE00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleDescendantOf1322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_ruleDescendantOf1341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOrSelfOf_in_entryRuleDescendantOrSelfOf1380 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDescendantOrSelfOf1390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_LT_in_ruleDescendantOrSelfOf1426 = new BitSet(new long[]{0x000000000052FE00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleDescendantOrSelfOf1448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_ruleDescendantOrSelfOf1467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleParentOf_in_entryRuleParentOf1506 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleParentOf1516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_GT_EM_in_ruleParentOf1552 = new BitSet(new long[]{0x000000000052FE00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleParentOf1574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_ruleParentOf1593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAncestorOf_in_entryRuleAncestorOf1632 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAncestorOf1642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_GT_in_ruleAncestorOf1678 = new BitSet(new long[]{0x000000000052FE00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleAncestorOf1700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_ruleAncestorOf1719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAncestorOrSelfOf_in_entryRuleAncestorOrSelfOf1758 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAncestorOrSelfOf1768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_GT_in_ruleAncestorOrSelfOf1804 = new BitSet(new long[]{0x000000000052FE00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleAncestorOrSelfOf1826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_ruleAncestorOrSelfOf1845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberOf_in_entryRuleMemberOf1884 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMemberOf1894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CARET_in_ruleMemberOf1930 = new BitSet(new long[]{0x000000000042FE00L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleMemberOf1952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_ruleMemberOf1971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptReference_in_entryRuleConceptReference2010 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleConceptReference2020 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSnomedIdentifier_in_ruleConceptReference2066 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConceptReference2078 = new BitSet(new long[]{0x00000003FEF4ED70L});
    public static final BitSet FOLLOW_ruleTerm_in_ruleConceptReference2098 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConceptReference2109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_entryRuleAny2146 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAny2156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WILDCARD_in_ruleAny2192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRefinement_in_entryRuleRefinement2236 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRefinement2246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeConstraint_in_ruleRefinement2292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeConstraint_in_entryRuleAttributeConstraint2326 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeConstraint2336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleAttributeConstraint2384 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_ruleAny_in_ruleAttributeConstraint2403 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_ruleComparison_in_ruleAttributeConstraint2427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleComparison_in_entryRuleComparison2463 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleComparison2473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeValueEquals_in_ruleComparison2520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeValueNotEquals_in_ruleComparison2547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeValueEquals_in_entryRuleAttributeValueEquals2582 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeValueEquals2592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_EQUAL_in_ruleAttributeValueEquals2628 = new BitSet(new long[]{0x000000000042FE00L});
    public static final BitSet FOLLOW_ruleSimpleExpressionConstraint_in_ruleAttributeValueEquals2648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeValueNotEquals_in_entryRuleAttributeValueNotEquals2684 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeValueNotEquals2694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_EQUAL_in_ruleAttributeValueNotEquals2730 = new BitSet(new long[]{0x000000000042FE00L});
    public static final BitSet FOLLOW_ruleSimpleExpressionConstraint_in_ruleAttributeValueNotEquals2750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestableExpression_in_entryRuleNestableExpression2786 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNestableExpression2796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_OPEN_in_ruleNestableExpression2832 = new BitSet(new long[]{0x000000000042FE00L});
    public static final BitSet FOLLOW_ruleExpressionConstraint_in_ruleNestableExpression2853 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RULE_ROUND_CLOSE_in_ruleNestableExpression2863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSnomedIdentifier_in_entryRuleSnomedIdentifier2905 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSnomedIdentifier2916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2960 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier2981 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3007 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3029 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3055 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3077 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3103 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3125 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3151 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3173 = new BitSet(new long[]{0x0000000000C00002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3199 = new BitSet(new long[]{0x0000000000C00002L});
    public static final BitSet FOLLOW_ruleTerm_in_entryRuleTerm3257 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTerm3268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm3320 = new BitSet(new long[]{0x00000003FFF4ED72L});
    public static final BitSet FOLLOW_RULE_WS_in_ruleTerm3344 = new BitSet(new long[]{0x00000003FFF4ED70L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm3374 = new BitSet(new long[]{0x00000003FFF4ED72L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter3434 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTermCharacter3445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LT_in_ruleTermCharacter3489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_GT_in_ruleTermCharacter3515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_LT_in_ruleTermCharacter3541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_GT_in_ruleTermCharacter3567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_AND_in_ruleTermCharacter3593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OR_in_ruleTermCharacter3619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_in_ruleTermCharacter3645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleTermCharacter3671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter3697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LETTER_in_ruleTermCharacter3723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CARET_in_ruleTermCharacter3749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_EQUAL_in_ruleTermCharacter3775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_PLUS_in_ruleTermCharacter3801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CURLY_OPEN_in_ruleTermCharacter3827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CURLY_CLOSE_in_ruleTermCharacter3853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_OPEN_in_ruleTermCharacter3879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_CLOSE_in_ruleTermCharacter3905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SQUARE_OPEN_in_ruleTermCharacter3931 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SQUARE_CLOSE_in_ruleTermCharacter3957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_in_ruleTermCharacter3983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COLON_in_ruleTermCharacter4009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleTermCharacter4035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OTHER_CHARACTER_in_ruleTermCharacter4061 = new BitSet(new long[]{0x0000000000000002L});

}