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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_OR", "RULE_MINUS", "RULE_COLON", "RULE_DOT", "RULE_LT_EM", "RULE_LT", "RULE_DBL_LT", "RULE_GT_EM", "RULE_GT", "RULE_DBL_GT", "RULE_CARET", "RULE_PIPE", "RULE_WILDCARD", "RULE_REVERSED", "RULE_SQUARE_OPEN", "RULE_TO", "RULE_SQUARE_CLOSE", "RULE_EQUAL", "RULE_NOT_EQUAL", "RULE_ROUND_OPEN", "RULE_ROUND_CLOSE", "RULE_DIGIT_NONZERO", "RULE_ZERO", "RULE_WS", "RULE_AND", "RULE_NOT", "RULE_LETTER", "RULE_PLUS", "RULE_CURLY_OPEN", "RULE_CURLY_CLOSE", "RULE_COMMA", "RULE_OTHER_CHARACTER", "RULE_ML_COMMENT", "RULE_SL_COMMENT"
    };
    public static final int RULE_DIGIT_NONZERO=25;
    public static final int RULE_CURLY_OPEN=32;
    public static final int RULE_DBL_GT=13;
    public static final int RULE_TO=19;
    public static final int RULE_ROUND_CLOSE=24;
    public static final int RULE_GT=12;
    public static final int RULE_NOT=29;
    public static final int RULE_REVERSED=17;
    public static final int RULE_AND=28;
    public static final int RULE_SL_COMMENT=37;
    public static final int RULE_ROUND_OPEN=23;
    public static final int RULE_OTHER_CHARACTER=35;
    public static final int RULE_DBL_LT=10;
    public static final int RULE_PLUS=31;
    public static final int RULE_NOT_EQUAL=22;
    public static final int RULE_OR=4;
    public static final int RULE_DOT=7;
    public static final int EOF=-1;
    public static final int RULE_SQUARE_CLOSE=20;
    public static final int RULE_SQUARE_OPEN=18;
    public static final int RULE_EQUAL=21;
    public static final int RULE_LT_EM=8;
    public static final int RULE_GT_EM=11;
    public static final int RULE_WS=27;
    public static final int RULE_COMMA=34;
    public static final int RULE_CURLY_CLOSE=33;
    public static final int RULE_ZERO=26;
    public static final int RULE_COLON=6;
    public static final int RULE_MINUS=5;
    public static final int RULE_LETTER=30;
    public static final int RULE_LT=9;
    public static final int RULE_CARET=14;
    public static final int RULE_PIPE=15;
    public static final int RULE_ML_COMMENT=36;
    public static final int RULE_WILDCARD=16;

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:174:1: ruleAndExpressionConstraint returns [EObject current=null] : (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () ruleAndOperator ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )* ) ;
    public final EObject ruleAndExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject this_ExclusionExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:177:28: ( (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () ruleAndOperator ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:178:1: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () ruleAndOperator ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:178:1: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () ruleAndOperator ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:179:5: this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () ruleAndOperator ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )*
            {
             
                    newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getExclusionExpressionConstraintParserRuleCall_0()); 
                
            pushFollow(FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint371);
            this_ExclusionExpressionConstraint_0=ruleExclusionExpressionConstraint();

            state._fsp--;

             
                    current = this_ExclusionExpressionConstraint_0; 
                    afterParserOrEnumRuleCall();
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:187:1: ( () ruleAndOperator ( (lv_right_3_0= ruleExclusionExpressionConstraint ) ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==RULE_AND||LA2_0==RULE_COMMA) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:187:2: () ruleAndOperator ( (lv_right_3_0= ruleExclusionExpressionConstraint ) )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:187:2: ()
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:188:5: 
            	    {

            	            current = forceCreateModelElementAndSet(
            	                grammarAccess.getAndExpressionConstraintAccess().getAndExpressionConstraintLeftAction_1_0(),
            	                current);
            	        

            	    }

            	     
            	            newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getAndOperatorParserRuleCall_1_1()); 
            	        
            	    pushFollow(FOLLOW_ruleAndOperator_in_ruleAndExpressionConstraint396);
            	    ruleAndOperator();

            	    state._fsp--;

            	     
            	            afterParserOrEnumRuleCall();
            	        
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:201:1: ( (lv_right_3_0= ruleExclusionExpressionConstraint ) )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:202:1: (lv_right_3_0= ruleExclusionExpressionConstraint )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:202:1: (lv_right_3_0= ruleExclusionExpressionConstraint )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:203:3: lv_right_3_0= ruleExclusionExpressionConstraint
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getRightExclusionExpressionConstraintParserRuleCall_1_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint416);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:227:1: entryRuleExclusionExpressionConstraint returns [EObject current=null] : iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF ;
    public final EObject entryRuleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExclusionExpressionConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:228:2: (iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:229:2: iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getExclusionExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleExclusionExpressionConstraint_in_entryRuleExclusionExpressionConstraint454);
            iv_ruleExclusionExpressionConstraint=ruleExclusionExpressionConstraint();

            state._fsp--;

             current =iv_ruleExclusionExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleExclusionExpressionConstraint464); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:236:1: ruleExclusionExpressionConstraint returns [EObject current=null] : (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? ) ;
    public final EObject ruleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_MINUS_2=null;
        EObject this_RefinedExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:239:28: ( (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:240:1: (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:240:1: (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:241:5: this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )?
            {
             
                    newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRefinedExpressionConstraintParserRuleCall_0()); 
                
            pushFollow(FOLLOW_ruleRefinedExpressionConstraint_in_ruleExclusionExpressionConstraint511);
            this_RefinedExpressionConstraint_0=ruleRefinedExpressionConstraint();

            state._fsp--;

             
                    current = this_RefinedExpressionConstraint_0; 
                    afterParserOrEnumRuleCall();
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:249:1: ( () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==RULE_MINUS) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:249:2: () this_MINUS_2= RULE_MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:249:2: ()
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:250:5: 
                    {

                            current = forceCreateModelElementAndSet(
                                grammarAccess.getExclusionExpressionConstraintAccess().getExclusionExpressionConstraintLeftAction_1_0(),
                                current);
                        

                    }

                    this_MINUS_2=(Token)match(input,RULE_MINUS,FOLLOW_RULE_MINUS_in_ruleExclusionExpressionConstraint531); 
                     
                        newLeafNode(this_MINUS_2, grammarAccess.getExclusionExpressionConstraintAccess().getMINUSTerminalRuleCall_1_1()); 
                        
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:259:1: ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:260:1: (lv_right_3_0= ruleRefinedExpressionConstraint )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:260:1: (lv_right_3_0= ruleRefinedExpressionConstraint )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:261:3: lv_right_3_0= ruleRefinedExpressionConstraint
                    {
                     
                    	        newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRightRefinedExpressionConstraintParserRuleCall_1_2_0()); 
                    	    
                    pushFollow(FOLLOW_ruleRefinedExpressionConstraint_in_ruleExclusionExpressionConstraint551);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:285:1: entryRuleRefinedExpressionConstraint returns [EObject current=null] : iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF ;
    public final EObject entryRuleRefinedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinedExpressionConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:286:2: (iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:287:2: iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getRefinedExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleRefinedExpressionConstraint_in_entryRuleRefinedExpressionConstraint589);
            iv_ruleRefinedExpressionConstraint=ruleRefinedExpressionConstraint();

            state._fsp--;

             current =iv_ruleRefinedExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRefinedExpressionConstraint599); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:294:1: ruleRefinedExpressionConstraint returns [EObject current=null] : (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? ) ;
    public final EObject ruleRefinedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_COLON_2=null;
        EObject this_DottedExpressionConstraint_0 = null;

        EObject lv_refinement_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:297:28: ( (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:298:1: (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:298:1: (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:299:5: this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )?
            {
             
                    newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getDottedExpressionConstraintParserRuleCall_0()); 
                
            pushFollow(FOLLOW_ruleDottedExpressionConstraint_in_ruleRefinedExpressionConstraint646);
            this_DottedExpressionConstraint_0=ruleDottedExpressionConstraint();

            state._fsp--;

             
                    current = this_DottedExpressionConstraint_0; 
                    afterParserOrEnumRuleCall();
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:307:1: ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==RULE_COLON) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:307:2: () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:307:2: ()
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:308:5: 
                    {

                            current = forceCreateModelElementAndSet(
                                grammarAccess.getRefinedExpressionConstraintAccess().getRefinedExpressionConstraintConstraintAction_1_0(),
                                current);
                        

                    }

                    this_COLON_2=(Token)match(input,RULE_COLON,FOLLOW_RULE_COLON_in_ruleRefinedExpressionConstraint666); 
                     
                        newLeafNode(this_COLON_2, grammarAccess.getRefinedExpressionConstraintAccess().getCOLONTerminalRuleCall_1_1()); 
                        
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:317:1: ( (lv_refinement_3_0= ruleRefinement ) )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:318:1: (lv_refinement_3_0= ruleRefinement )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:318:1: (lv_refinement_3_0= ruleRefinement )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:319:3: lv_refinement_3_0= ruleRefinement
                    {
                     
                    	        newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getRefinementRefinementParserRuleCall_1_2_0()); 
                    	    
                    pushFollow(FOLLOW_ruleRefinement_in_ruleRefinedExpressionConstraint686);
                    lv_refinement_3_0=ruleRefinement();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getRefinedExpressionConstraintRule());
                    	        }
                           		set(
                           			current, 
                           			"refinement",
                            		lv_refinement_3_0, 
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


    // $ANTLR start "entryRuleDottedExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:343:1: entryRuleDottedExpressionConstraint returns [EObject current=null] : iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF ;
    public final EObject entryRuleDottedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDottedExpressionConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:344:2: (iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:345:2: iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getDottedExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleDottedExpressionConstraint_in_entryRuleDottedExpressionConstraint724);
            iv_ruleDottedExpressionConstraint=ruleDottedExpressionConstraint();

            state._fsp--;

             current =iv_ruleDottedExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDottedExpressionConstraint734); 

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
    // $ANTLR end "entryRuleDottedExpressionConstraint"


    // $ANTLR start "ruleDottedExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:352:1: ruleDottedExpressionConstraint returns [EObject current=null] : (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )* ) ;
    public final EObject ruleDottedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_DOT_2=null;
        EObject this_SimpleExpressionConstraint_0 = null;

        EObject lv_attribute_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:355:28: ( (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:356:1: (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:356:1: (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:357:5: this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )*
            {
             
                    newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getSimpleExpressionConstraintParserRuleCall_0()); 
                
            pushFollow(FOLLOW_ruleSimpleExpressionConstraint_in_ruleDottedExpressionConstraint781);
            this_SimpleExpressionConstraint_0=ruleSimpleExpressionConstraint();

            state._fsp--;

             
                    current = this_SimpleExpressionConstraint_0; 
                    afterParserOrEnumRuleCall();
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:365:1: ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==RULE_DOT) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:365:2: () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:365:2: ()
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:366:5: 
            	    {

            	            current = forceCreateModelElementAndSet(
            	                grammarAccess.getDottedExpressionConstraintAccess().getDottedExpressionConstraintConstraintAction_1_0(),
            	                current);
            	        

            	    }

            	    this_DOT_2=(Token)match(input,RULE_DOT,FOLLOW_RULE_DOT_in_ruleDottedExpressionConstraint801); 
            	     
            	        newLeafNode(this_DOT_2, grammarAccess.getDottedExpressionConstraintAccess().getDOTTerminalRuleCall_1_1()); 
            	        
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:375:1: ( (lv_attribute_3_0= ruleAttribute ) )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:376:1: (lv_attribute_3_0= ruleAttribute )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:376:1: (lv_attribute_3_0= ruleAttribute )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:377:3: lv_attribute_3_0= ruleAttribute
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getAttributeAttributeParserRuleCall_1_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleAttribute_in_ruleDottedExpressionConstraint821);
            	    lv_attribute_3_0=ruleAttribute();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getDottedExpressionConstraintRule());
            	    	        }
            	           		set(
            	           			current, 
            	           			"attribute",
            	            		lv_attribute_3_0, 
            	            		"Attribute");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop5;
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
    // $ANTLR end "ruleDottedExpressionConstraint"


    // $ANTLR start "entryRuleSimpleExpressionConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:401:1: entryRuleSimpleExpressionConstraint returns [EObject current=null] : iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF ;
    public final EObject entryRuleSimpleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleExpressionConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:402:2: (iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:403:2: iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF
            {
             newCompositeNode(grammarAccess.getSimpleExpressionConstraintRule()); 
            pushFollow(FOLLOW_ruleSimpleExpressionConstraint_in_entryRuleSimpleExpressionConstraint859);
            iv_ruleSimpleExpressionConstraint=ruleSimpleExpressionConstraint();

            state._fsp--;

             current =iv_ruleSimpleExpressionConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSimpleExpressionConstraint869); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:410:1: ruleSimpleExpressionConstraint returns [EObject current=null] : (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept ) ;
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
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:413:28: ( (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:414:1: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:414:1: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept )
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
            case RULE_ROUND_OPEN:
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:415:5: this_ChildOf_0= ruleChildOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getChildOfParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleChildOf_in_ruleSimpleExpressionConstraint916);
                    this_ChildOf_0=ruleChildOf();

                    state._fsp--;

                     
                            current = this_ChildOf_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:425:5: this_DescendantOf_1= ruleDescendantOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getDescendantOfParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleDescendantOf_in_ruleSimpleExpressionConstraint943);
                    this_DescendantOf_1=ruleDescendantOf();

                    state._fsp--;

                     
                            current = this_DescendantOf_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:435:5: this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getDescendantOrSelfOfParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleDescendantOrSelfOf_in_ruleSimpleExpressionConstraint970);
                    this_DescendantOrSelfOf_2=ruleDescendantOrSelfOf();

                    state._fsp--;

                     
                            current = this_DescendantOrSelfOf_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:445:5: this_ParentOf_3= ruleParentOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getParentOfParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_ruleParentOf_in_ruleSimpleExpressionConstraint997);
                    this_ParentOf_3=ruleParentOf();

                    state._fsp--;

                     
                            current = this_ParentOf_3; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 5 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:455:5: this_AncestorOf_4= ruleAncestorOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getAncestorOfParserRuleCall_4()); 
                        
                    pushFollow(FOLLOW_ruleAncestorOf_in_ruleSimpleExpressionConstraint1024);
                    this_AncestorOf_4=ruleAncestorOf();

                    state._fsp--;

                     
                            current = this_AncestorOf_4; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 6 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:465:5: this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getAncestorOrSelfOfParserRuleCall_5()); 
                        
                    pushFollow(FOLLOW_ruleAncestorOrSelfOf_in_ruleSimpleExpressionConstraint1051);
                    this_AncestorOrSelfOf_5=ruleAncestorOrSelfOf();

                    state._fsp--;

                     
                            current = this_AncestorOrSelfOf_5; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 7 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:475:5: this_FocusConcept_6= ruleFocusConcept
                    {
                     
                            newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getFocusConceptParserRuleCall_6()); 
                        
                    pushFollow(FOLLOW_ruleFocusConcept_in_ruleSimpleExpressionConstraint1078);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:491:1: entryRuleFocusConcept returns [EObject current=null] : iv_ruleFocusConcept= ruleFocusConcept EOF ;
    public final EObject entryRuleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFocusConcept = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:492:2: (iv_ruleFocusConcept= ruleFocusConcept EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:493:2: iv_ruleFocusConcept= ruleFocusConcept EOF
            {
             newCompositeNode(grammarAccess.getFocusConceptRule()); 
            pushFollow(FOLLOW_ruleFocusConcept_in_entryRuleFocusConcept1113);
            iv_ruleFocusConcept=ruleFocusConcept();

            state._fsp--;

             current =iv_ruleFocusConcept; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFocusConcept1123); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:500:1: ruleFocusConcept returns [EObject current=null] : (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression ) ;
    public final EObject ruleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject this_MemberOf_0 = null;

        EObject this_ConceptReference_1 = null;

        EObject this_Any_2 = null;

        EObject this_NestedExpression_3 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:503:28: ( (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:504:1: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:504:1: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression )
            int alt7=4;
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
            case RULE_ROUND_OPEN:
                {
                alt7=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:505:5: this_MemberOf_0= ruleMemberOf
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getMemberOfParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleMemberOf_in_ruleFocusConcept1170);
                    this_MemberOf_0=ruleMemberOf();

                    state._fsp--;

                     
                            current = this_MemberOf_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:515:5: this_ConceptReference_1= ruleConceptReference
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getConceptReferenceParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleFocusConcept1197);
                    this_ConceptReference_1=ruleConceptReference();

                    state._fsp--;

                     
                            current = this_ConceptReference_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:525:5: this_Any_2= ruleAny
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getAnyParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleAny_in_ruleFocusConcept1224);
                    this_Any_2=ruleAny();

                    state._fsp--;

                     
                            current = this_Any_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:535:5: this_NestedExpression_3= ruleNestedExpression
                    {
                     
                            newCompositeNode(grammarAccess.getFocusConceptAccess().getNestedExpressionParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_ruleNestedExpression_in_ruleFocusConcept1251);
                    this_NestedExpression_3=ruleNestedExpression();

                    state._fsp--;

                     
                            current = this_NestedExpression_3; 
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:551:1: entryRuleChildOf returns [EObject current=null] : iv_ruleChildOf= ruleChildOf EOF ;
    public final EObject entryRuleChildOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleChildOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:552:2: (iv_ruleChildOf= ruleChildOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:553:2: iv_ruleChildOf= ruleChildOf EOF
            {
             newCompositeNode(grammarAccess.getChildOfRule()); 
            pushFollow(FOLLOW_ruleChildOf_in_entryRuleChildOf1286);
            iv_ruleChildOf=ruleChildOf();

            state._fsp--;

             current =iv_ruleChildOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleChildOf1296); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:560:1: ruleChildOf returns [EObject current=null] : (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleChildOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_EM_0=null;
        EObject lv_constraint_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:563:28: ( (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:564:1: (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:564:1: (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:564:2: this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_LT_EM_0=(Token)match(input,RULE_LT_EM,FOLLOW_RULE_LT_EM_in_ruleChildOf1332); 
             
                newLeafNode(this_LT_EM_0, grammarAccess.getChildOfAccess().getLT_EMTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:568:1: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:569:1: (lv_constraint_1_0= ruleFocusConcept )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:569:1: (lv_constraint_1_0= ruleFocusConcept )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:570:3: lv_constraint_1_0= ruleFocusConcept
            {
             
            	        newCompositeNode(grammarAccess.getChildOfAccess().getConstraintFocusConceptParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleFocusConcept_in_ruleChildOf1352);
            lv_constraint_1_0=ruleFocusConcept();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getChildOfRule());
            	        }
                   		set(
                   			current, 
                   			"constraint",
                    		lv_constraint_1_0, 
                    		"FocusConcept");
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
    // $ANTLR end "ruleChildOf"


    // $ANTLR start "entryRuleDescendantOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:594:1: entryRuleDescendantOf returns [EObject current=null] : iv_ruleDescendantOf= ruleDescendantOf EOF ;
    public final EObject entryRuleDescendantOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:595:2: (iv_ruleDescendantOf= ruleDescendantOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:596:2: iv_ruleDescendantOf= ruleDescendantOf EOF
            {
             newCompositeNode(grammarAccess.getDescendantOfRule()); 
            pushFollow(FOLLOW_ruleDescendantOf_in_entryRuleDescendantOf1388);
            iv_ruleDescendantOf=ruleDescendantOf();

            state._fsp--;

             current =iv_ruleDescendantOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDescendantOf1398); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:603:1: ruleDescendantOf returns [EObject current=null] : (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleDescendantOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        EObject lv_constraint_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:606:28: ( (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:607:1: (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:607:1: (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:607:2: this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FOLLOW_RULE_LT_in_ruleDescendantOf1434); 
             
                newLeafNode(this_LT_0, grammarAccess.getDescendantOfAccess().getLTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:611:1: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:612:1: (lv_constraint_1_0= ruleFocusConcept )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:612:1: (lv_constraint_1_0= ruleFocusConcept )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:613:3: lv_constraint_1_0= ruleFocusConcept
            {
             
            	        newCompositeNode(grammarAccess.getDescendantOfAccess().getConstraintFocusConceptParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleFocusConcept_in_ruleDescendantOf1454);
            lv_constraint_1_0=ruleFocusConcept();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getDescendantOfRule());
            	        }
                   		set(
                   			current, 
                   			"constraint",
                    		lv_constraint_1_0, 
                    		"FocusConcept");
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
    // $ANTLR end "ruleDescendantOf"


    // $ANTLR start "entryRuleDescendantOrSelfOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:637:1: entryRuleDescendantOrSelfOf returns [EObject current=null] : iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF ;
    public final EObject entryRuleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOrSelfOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:638:2: (iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:639:2: iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF
            {
             newCompositeNode(grammarAccess.getDescendantOrSelfOfRule()); 
            pushFollow(FOLLOW_ruleDescendantOrSelfOf_in_entryRuleDescendantOrSelfOf1490);
            iv_ruleDescendantOrSelfOf=ruleDescendantOrSelfOf();

            state._fsp--;

             current =iv_ruleDescendantOrSelfOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDescendantOrSelfOf1500); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:646:1: ruleDescendantOrSelfOf returns [EObject current=null] : (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_LT_0=null;
        EObject lv_constraint_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:649:28: ( (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:650:1: (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:650:1: (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:650:2: this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_DBL_LT_0=(Token)match(input,RULE_DBL_LT,FOLLOW_RULE_DBL_LT_in_ruleDescendantOrSelfOf1536); 
             
                newLeafNode(this_DBL_LT_0, grammarAccess.getDescendantOrSelfOfAccess().getDBL_LTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:654:1: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:655:1: (lv_constraint_1_0= ruleFocusConcept )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:655:1: (lv_constraint_1_0= ruleFocusConcept )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:656:3: lv_constraint_1_0= ruleFocusConcept
            {
             
            	        newCompositeNode(grammarAccess.getDescendantOrSelfOfAccess().getConstraintFocusConceptParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleFocusConcept_in_ruleDescendantOrSelfOf1556);
            lv_constraint_1_0=ruleFocusConcept();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getDescendantOrSelfOfRule());
            	        }
                   		set(
                   			current, 
                   			"constraint",
                    		lv_constraint_1_0, 
                    		"FocusConcept");
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
    // $ANTLR end "ruleDescendantOrSelfOf"


    // $ANTLR start "entryRuleParentOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:680:1: entryRuleParentOf returns [EObject current=null] : iv_ruleParentOf= ruleParentOf EOF ;
    public final EObject entryRuleParentOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParentOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:681:2: (iv_ruleParentOf= ruleParentOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:682:2: iv_ruleParentOf= ruleParentOf EOF
            {
             newCompositeNode(grammarAccess.getParentOfRule()); 
            pushFollow(FOLLOW_ruleParentOf_in_entryRuleParentOf1592);
            iv_ruleParentOf=ruleParentOf();

            state._fsp--;

             current =iv_ruleParentOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleParentOf1602); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:689:1: ruleParentOf returns [EObject current=null] : (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleParentOf() throws RecognitionException {
        EObject current = null;

        Token this_GT_EM_0=null;
        EObject lv_constraint_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:692:28: ( (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:693:1: (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:693:1: (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:693:2: this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_GT_EM_0=(Token)match(input,RULE_GT_EM,FOLLOW_RULE_GT_EM_in_ruleParentOf1638); 
             
                newLeafNode(this_GT_EM_0, grammarAccess.getParentOfAccess().getGT_EMTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:697:1: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:698:1: (lv_constraint_1_0= ruleFocusConcept )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:698:1: (lv_constraint_1_0= ruleFocusConcept )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:699:3: lv_constraint_1_0= ruleFocusConcept
            {
             
            	        newCompositeNode(grammarAccess.getParentOfAccess().getConstraintFocusConceptParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleFocusConcept_in_ruleParentOf1658);
            lv_constraint_1_0=ruleFocusConcept();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getParentOfRule());
            	        }
                   		set(
                   			current, 
                   			"constraint",
                    		lv_constraint_1_0, 
                    		"FocusConcept");
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
    // $ANTLR end "ruleParentOf"


    // $ANTLR start "entryRuleAncestorOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:723:1: entryRuleAncestorOf returns [EObject current=null] : iv_ruleAncestorOf= ruleAncestorOf EOF ;
    public final EObject entryRuleAncestorOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAncestorOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:724:2: (iv_ruleAncestorOf= ruleAncestorOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:725:2: iv_ruleAncestorOf= ruleAncestorOf EOF
            {
             newCompositeNode(grammarAccess.getAncestorOfRule()); 
            pushFollow(FOLLOW_ruleAncestorOf_in_entryRuleAncestorOf1694);
            iv_ruleAncestorOf=ruleAncestorOf();

            state._fsp--;

             current =iv_ruleAncestorOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAncestorOf1704); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:732:1: ruleAncestorOf returns [EObject current=null] : (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleAncestorOf() throws RecognitionException {
        EObject current = null;

        Token this_GT_0=null;
        EObject lv_constraint_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:735:28: ( (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:736:1: (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:736:1: (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:736:2: this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_GT_0=(Token)match(input,RULE_GT,FOLLOW_RULE_GT_in_ruleAncestorOf1740); 
             
                newLeafNode(this_GT_0, grammarAccess.getAncestorOfAccess().getGTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:740:1: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:741:1: (lv_constraint_1_0= ruleFocusConcept )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:741:1: (lv_constraint_1_0= ruleFocusConcept )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:742:3: lv_constraint_1_0= ruleFocusConcept
            {
             
            	        newCompositeNode(grammarAccess.getAncestorOfAccess().getConstraintFocusConceptParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleFocusConcept_in_ruleAncestorOf1760);
            lv_constraint_1_0=ruleFocusConcept();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAncestorOfRule());
            	        }
                   		set(
                   			current, 
                   			"constraint",
                    		lv_constraint_1_0, 
                    		"FocusConcept");
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
    // $ANTLR end "ruleAncestorOf"


    // $ANTLR start "entryRuleAncestorOrSelfOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:766:1: entryRuleAncestorOrSelfOf returns [EObject current=null] : iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF ;
    public final EObject entryRuleAncestorOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAncestorOrSelfOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:767:2: (iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:768:2: iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF
            {
             newCompositeNode(grammarAccess.getAncestorOrSelfOfRule()); 
            pushFollow(FOLLOW_ruleAncestorOrSelfOf_in_entryRuleAncestorOrSelfOf1796);
            iv_ruleAncestorOrSelfOf=ruleAncestorOrSelfOf();

            state._fsp--;

             current =iv_ruleAncestorOrSelfOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAncestorOrSelfOf1806); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:775:1: ruleAncestorOrSelfOf returns [EObject current=null] : (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleAncestorOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_GT_0=null;
        EObject lv_constraint_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:778:28: ( (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:779:1: (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:779:1: (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:779:2: this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_DBL_GT_0=(Token)match(input,RULE_DBL_GT,FOLLOW_RULE_DBL_GT_in_ruleAncestorOrSelfOf1842); 
             
                newLeafNode(this_DBL_GT_0, grammarAccess.getAncestorOrSelfOfAccess().getDBL_GTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:783:1: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:784:1: (lv_constraint_1_0= ruleFocusConcept )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:784:1: (lv_constraint_1_0= ruleFocusConcept )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:785:3: lv_constraint_1_0= ruleFocusConcept
            {
             
            	        newCompositeNode(grammarAccess.getAncestorOrSelfOfAccess().getConstraintFocusConceptParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleFocusConcept_in_ruleAncestorOrSelfOf1862);
            lv_constraint_1_0=ruleFocusConcept();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAncestorOrSelfOfRule());
            	        }
                   		set(
                   			current, 
                   			"constraint",
                    		lv_constraint_1_0, 
                    		"FocusConcept");
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
    // $ANTLR end "ruleAncestorOrSelfOf"


    // $ANTLR start "entryRuleMemberOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:809:1: entryRuleMemberOf returns [EObject current=null] : iv_ruleMemberOf= ruleMemberOf EOF ;
    public final EObject entryRuleMemberOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMemberOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:810:2: (iv_ruleMemberOf= ruleMemberOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:811:2: iv_ruleMemberOf= ruleMemberOf EOF
            {
             newCompositeNode(grammarAccess.getMemberOfRule()); 
            pushFollow(FOLLOW_ruleMemberOf_in_entryRuleMemberOf1898);
            iv_ruleMemberOf=ruleMemberOf();

            state._fsp--;

             current =iv_ruleMemberOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleMemberOf1908); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:818:1: ruleMemberOf returns [EObject current=null] : (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) ;
    public final EObject ruleMemberOf() throws RecognitionException {
        EObject current = null;

        Token this_CARET_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:821:28: ( (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:822:1: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:822:1: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:822:2: this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            {
            this_CARET_0=(Token)match(input,RULE_CARET,FOLLOW_RULE_CARET_in_ruleMemberOf1944); 
             
                newLeafNode(this_CARET_0, grammarAccess.getMemberOfAccess().getCARETTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:826:1: ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:827:1: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:827:1: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:828:1: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:828:1: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==RULE_DIGIT_NONZERO) ) {
                alt8=1;
            }
            else if ( (LA8_0==RULE_WILDCARD) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:829:3: lv_constraint_1_1= ruleConceptReference
                    {
                     
                    	        newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintConceptReferenceParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleMemberOf1966);
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:844:8: lv_constraint_1_2= ruleAny
                    {
                     
                    	        newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintAnyParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleAny_in_ruleMemberOf1985);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:870:1: entryRuleConceptReference returns [EObject current=null] : iv_ruleConceptReference= ruleConceptReference EOF ;
    public final EObject entryRuleConceptReference() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptReference = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:871:2: (iv_ruleConceptReference= ruleConceptReference EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:872:2: iv_ruleConceptReference= ruleConceptReference EOF
            {
             newCompositeNode(grammarAccess.getConceptReferenceRule()); 
            pushFollow(FOLLOW_ruleConceptReference_in_entryRuleConceptReference2024);
            iv_ruleConceptReference=ruleConceptReference();

            state._fsp--;

             current =iv_ruleConceptReference; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleConceptReference2034); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:879:1: ruleConceptReference returns [EObject current=null] : ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? ) ;
    public final EObject ruleConceptReference() throws RecognitionException {
        EObject current = null;

        Token this_PIPE_1=null;
        Token this_PIPE_3=null;
        AntlrDatatypeRuleToken lv_id_0_0 = null;

        AntlrDatatypeRuleToken lv_term_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:882:28: ( ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:883:1: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:883:1: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )? )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:883:2: ( (lv_id_0_0= ruleSnomedIdentifier ) ) (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )?
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:883:2: ( (lv_id_0_0= ruleSnomedIdentifier ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:884:1: (lv_id_0_0= ruleSnomedIdentifier )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:884:1: (lv_id_0_0= ruleSnomedIdentifier )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:885:3: lv_id_0_0= ruleSnomedIdentifier
            {
             
            	        newCompositeNode(grammarAccess.getConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleSnomedIdentifier_in_ruleConceptReference2080);
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

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:901:2: (this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==RULE_PIPE) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:901:3: this_PIPE_1= RULE_PIPE ( (lv_term_2_0= ruleTerm ) ) this_PIPE_3= RULE_PIPE
                    {
                    this_PIPE_1=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConceptReference2092); 
                     
                        newLeafNode(this_PIPE_1, grammarAccess.getConceptReferenceAccess().getPIPETerminalRuleCall_1_0()); 
                        
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:905:1: ( (lv_term_2_0= ruleTerm ) )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:906:1: (lv_term_2_0= ruleTerm )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:906:1: (lv_term_2_0= ruleTerm )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:907:3: lv_term_2_0= ruleTerm
                    {
                     
                    	        newCompositeNode(grammarAccess.getConceptReferenceAccess().getTermTermParserRuleCall_1_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleTerm_in_ruleConceptReference2112);
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

                    this_PIPE_3=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConceptReference2123); 
                     
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:935:1: entryRuleAny returns [EObject current=null] : iv_ruleAny= ruleAny EOF ;
    public final EObject entryRuleAny() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAny = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:936:2: (iv_ruleAny= ruleAny EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:937:2: iv_ruleAny= ruleAny EOF
            {
             newCompositeNode(grammarAccess.getAnyRule()); 
            pushFollow(FOLLOW_ruleAny_in_entryRuleAny2160);
            iv_ruleAny=ruleAny();

            state._fsp--;

             current =iv_ruleAny; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAny2170); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:944:1: ruleAny returns [EObject current=null] : (this_WILDCARD_0= RULE_WILDCARD () ) ;
    public final EObject ruleAny() throws RecognitionException {
        EObject current = null;

        Token this_WILDCARD_0=null;

         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:947:28: ( (this_WILDCARD_0= RULE_WILDCARD () ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:948:1: (this_WILDCARD_0= RULE_WILDCARD () )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:948:1: (this_WILDCARD_0= RULE_WILDCARD () )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:948:2: this_WILDCARD_0= RULE_WILDCARD ()
            {
            this_WILDCARD_0=(Token)match(input,RULE_WILDCARD,FOLLOW_RULE_WILDCARD_in_ruleAny2206); 
             
                newLeafNode(this_WILDCARD_0, grammarAccess.getAnyAccess().getWILDCARDTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:952:1: ()
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:953:5: 
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:966:1: entryRuleRefinement returns [EObject current=null] : iv_ruleRefinement= ruleRefinement EOF ;
    public final EObject entryRuleRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinement = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:967:2: (iv_ruleRefinement= ruleRefinement EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:968:2: iv_ruleRefinement= ruleRefinement EOF
            {
             newCompositeNode(grammarAccess.getRefinementRule()); 
            pushFollow(FOLLOW_ruleRefinement_in_entryRuleRefinement2250);
            iv_ruleRefinement=ruleRefinement();

            state._fsp--;

             current =iv_ruleRefinement; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRefinement2260); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:975:1: ruleRefinement returns [EObject current=null] : this_OrRefinement_0= ruleOrRefinement ;
    public final EObject ruleRefinement() throws RecognitionException {
        EObject current = null;

        EObject this_OrRefinement_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:978:28: (this_OrRefinement_0= ruleOrRefinement )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:980:5: this_OrRefinement_0= ruleOrRefinement
            {
             
                    newCompositeNode(grammarAccess.getRefinementAccess().getOrRefinementParserRuleCall()); 
                
            pushFollow(FOLLOW_ruleOrRefinement_in_ruleRefinement2306);
            this_OrRefinement_0=ruleOrRefinement();

            state._fsp--;

             
                    current = this_OrRefinement_0; 
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


    // $ANTLR start "entryRuleOrRefinement"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:996:1: entryRuleOrRefinement returns [EObject current=null] : iv_ruleOrRefinement= ruleOrRefinement EOF ;
    public final EObject entryRuleOrRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrRefinement = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:997:2: (iv_ruleOrRefinement= ruleOrRefinement EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:998:2: iv_ruleOrRefinement= ruleOrRefinement EOF
            {
             newCompositeNode(grammarAccess.getOrRefinementRule()); 
            pushFollow(FOLLOW_ruleOrRefinement_in_entryRuleOrRefinement2340);
            iv_ruleOrRefinement=ruleOrRefinement();

            state._fsp--;

             current =iv_ruleOrRefinement; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOrRefinement2350); 

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
    // $ANTLR end "entryRuleOrRefinement"


    // $ANTLR start "ruleOrRefinement"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1005:1: ruleOrRefinement returns [EObject current=null] : (this_AndRefinement_0= ruleAndRefinement ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndRefinement ) ) )* ) ;
    public final EObject ruleOrRefinement() throws RecognitionException {
        EObject current = null;

        Token this_OR_2=null;
        EObject this_AndRefinement_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1008:28: ( (this_AndRefinement_0= ruleAndRefinement ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndRefinement ) ) )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1009:1: (this_AndRefinement_0= ruleAndRefinement ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndRefinement ) ) )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1009:1: (this_AndRefinement_0= ruleAndRefinement ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndRefinement ) ) )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1010:5: this_AndRefinement_0= ruleAndRefinement ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndRefinement ) ) )*
            {
             
                    newCompositeNode(grammarAccess.getOrRefinementAccess().getAndRefinementParserRuleCall_0()); 
                
            pushFollow(FOLLOW_ruleAndRefinement_in_ruleOrRefinement2397);
            this_AndRefinement_0=ruleAndRefinement();

            state._fsp--;

             
                    current = this_AndRefinement_0; 
                    afterParserOrEnumRuleCall();
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1018:1: ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndRefinement ) ) )*
            loop10:
            do {
                int alt10=2;
                alt10 = dfa10.predict(input);
                switch (alt10) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1018:2: () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndRefinement ) )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1018:2: ()
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1019:5: 
            	    {

            	            current = forceCreateModelElementAndSet(
            	                grammarAccess.getOrRefinementAccess().getOrRefinementLeftAction_1_0(),
            	                current);
            	        

            	    }

            	    this_OR_2=(Token)match(input,RULE_OR,FOLLOW_RULE_OR_in_ruleOrRefinement2417); 
            	     
            	        newLeafNode(this_OR_2, grammarAccess.getOrRefinementAccess().getORTerminalRuleCall_1_1()); 
            	        
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1028:1: ( (lv_right_3_0= ruleAndRefinement ) )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1029:1: (lv_right_3_0= ruleAndRefinement )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1029:1: (lv_right_3_0= ruleAndRefinement )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1030:3: lv_right_3_0= ruleAndRefinement
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOrRefinementAccess().getRightAndRefinementParserRuleCall_1_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleAndRefinement_in_ruleOrRefinement2437);
            	    lv_right_3_0=ruleAndRefinement();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOrRefinementRule());
            	    	        }
            	           		set(
            	           			current, 
            	           			"right",
            	            		lv_right_3_0, 
            	            		"AndRefinement");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


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
        }
        return current;
    }
    // $ANTLR end "ruleOrRefinement"


    // $ANTLR start "entryRuleAndRefinement"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1054:1: entryRuleAndRefinement returns [EObject current=null] : iv_ruleAndRefinement= ruleAndRefinement EOF ;
    public final EObject entryRuleAndRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndRefinement = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1055:2: (iv_ruleAndRefinement= ruleAndRefinement EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1056:2: iv_ruleAndRefinement= ruleAndRefinement EOF
            {
             newCompositeNode(grammarAccess.getAndRefinementRule()); 
            pushFollow(FOLLOW_ruleAndRefinement_in_entryRuleAndRefinement2475);
            iv_ruleAndRefinement=ruleAndRefinement();

            state._fsp--;

             current =iv_ruleAndRefinement; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAndRefinement2485); 

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
    // $ANTLR end "entryRuleAndRefinement"


    // $ANTLR start "ruleAndRefinement"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1063:1: ruleAndRefinement returns [EObject current=null] : (this_AttributeConstraint_0= ruleAttributeConstraint ( () ruleAndOperator ( (lv_right_3_0= ruleAttributeConstraint ) ) )* ) ;
    public final EObject ruleAndRefinement() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeConstraint_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1066:28: ( (this_AttributeConstraint_0= ruleAttributeConstraint ( () ruleAndOperator ( (lv_right_3_0= ruleAttributeConstraint ) ) )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1067:1: (this_AttributeConstraint_0= ruleAttributeConstraint ( () ruleAndOperator ( (lv_right_3_0= ruleAttributeConstraint ) ) )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1067:1: (this_AttributeConstraint_0= ruleAttributeConstraint ( () ruleAndOperator ( (lv_right_3_0= ruleAttributeConstraint ) ) )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1068:5: this_AttributeConstraint_0= ruleAttributeConstraint ( () ruleAndOperator ( (lv_right_3_0= ruleAttributeConstraint ) ) )*
            {
             
                    newCompositeNode(grammarAccess.getAndRefinementAccess().getAttributeConstraintParserRuleCall_0()); 
                
            pushFollow(FOLLOW_ruleAttributeConstraint_in_ruleAndRefinement2532);
            this_AttributeConstraint_0=ruleAttributeConstraint();

            state._fsp--;

             
                    current = this_AttributeConstraint_0; 
                    afterParserOrEnumRuleCall();
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1076:1: ( () ruleAndOperator ( (lv_right_3_0= ruleAttributeConstraint ) ) )*
            loop11:
            do {
                int alt11=2;
                alt11 = dfa11.predict(input);
                switch (alt11) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1076:2: () ruleAndOperator ( (lv_right_3_0= ruleAttributeConstraint ) )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1076:2: ()
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1077:5: 
            	    {

            	            current = forceCreateModelElementAndSet(
            	                grammarAccess.getAndRefinementAccess().getAndRefinementLeftAction_1_0(),
            	                current);
            	        

            	    }

            	     
            	            newCompositeNode(grammarAccess.getAndRefinementAccess().getAndOperatorParserRuleCall_1_1()); 
            	        
            	    pushFollow(FOLLOW_ruleAndOperator_in_ruleAndRefinement2557);
            	    ruleAndOperator();

            	    state._fsp--;

            	     
            	            afterParserOrEnumRuleCall();
            	        
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1090:1: ( (lv_right_3_0= ruleAttributeConstraint ) )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1091:1: (lv_right_3_0= ruleAttributeConstraint )
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1091:1: (lv_right_3_0= ruleAttributeConstraint )
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1092:3: lv_right_3_0= ruleAttributeConstraint
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAndRefinementAccess().getRightAttributeConstraintParserRuleCall_1_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleAttributeConstraint_in_ruleAndRefinement2577);
            	    lv_right_3_0=ruleAttributeConstraint();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAndRefinementRule());
            	    	        }
            	           		set(
            	           			current, 
            	           			"right",
            	            		lv_right_3_0, 
            	            		"AttributeConstraint");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop11;
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
    // $ANTLR end "ruleAndRefinement"


    // $ANTLR start "entryRuleAttributeConstraint"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1116:1: entryRuleAttributeConstraint returns [EObject current=null] : iv_ruleAttributeConstraint= ruleAttributeConstraint EOF ;
    public final EObject entryRuleAttributeConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeConstraint = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1117:2: (iv_ruleAttributeConstraint= ruleAttributeConstraint EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1118:2: iv_ruleAttributeConstraint= ruleAttributeConstraint EOF
            {
             newCompositeNode(grammarAccess.getAttributeConstraintRule()); 
            pushFollow(FOLLOW_ruleAttributeConstraint_in_entryRuleAttributeConstraint2615);
            iv_ruleAttributeConstraint=ruleAttributeConstraint();

            state._fsp--;

             current =iv_ruleAttributeConstraint; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeConstraint2625); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1125:1: ruleAttributeConstraint returns [EObject current=null] : ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleAttribute ) ) ( (lv_comparison_3_0= ruleComparison ) ) ) ;
    public final EObject ruleAttributeConstraint() throws RecognitionException {
        EObject current = null;

        Token lv_reversed_1_0=null;
        EObject lv_cardinality_0_0 = null;

        EObject lv_attribute_2_0 = null;

        EObject lv_comparison_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1128:28: ( ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleAttribute ) ) ( (lv_comparison_3_0= ruleComparison ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1129:1: ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleAttribute ) ) ( (lv_comparison_3_0= ruleComparison ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1129:1: ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleAttribute ) ) ( (lv_comparison_3_0= ruleComparison ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1129:2: ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleAttribute ) ) ( (lv_comparison_3_0= ruleComparison ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1129:2: ( (lv_cardinality_0_0= ruleCardinality ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==RULE_SQUARE_OPEN) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1130:1: (lv_cardinality_0_0= ruleCardinality )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1130:1: (lv_cardinality_0_0= ruleCardinality )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1131:3: lv_cardinality_0_0= ruleCardinality
                    {
                     
                    	        newCompositeNode(grammarAccess.getAttributeConstraintAccess().getCardinalityCardinalityParserRuleCall_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleCardinality_in_ruleAttributeConstraint2671);
                    lv_cardinality_0_0=ruleCardinality();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
                    	        }
                           		set(
                           			current, 
                           			"cardinality",
                            		lv_cardinality_0_0, 
                            		"Cardinality");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1147:3: ( (lv_reversed_1_0= RULE_REVERSED ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==RULE_REVERSED) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1148:1: (lv_reversed_1_0= RULE_REVERSED )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1148:1: (lv_reversed_1_0= RULE_REVERSED )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1149:3: lv_reversed_1_0= RULE_REVERSED
                    {
                    lv_reversed_1_0=(Token)match(input,RULE_REVERSED,FOLLOW_RULE_REVERSED_in_ruleAttributeConstraint2689); 

                    			newLeafNode(lv_reversed_1_0, grammarAccess.getAttributeConstraintAccess().getReversedREVERSEDTerminalRuleCall_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getAttributeConstraintRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"reversed",
                            		true, 
                            		"REVERSED");
                    	    

                    }


                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1165:3: ( (lv_attribute_2_0= ruleAttribute ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1166:1: (lv_attribute_2_0= ruleAttribute )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1166:1: (lv_attribute_2_0= ruleAttribute )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1167:3: lv_attribute_2_0= ruleAttribute
            {
             
            	        newCompositeNode(grammarAccess.getAttributeConstraintAccess().getAttributeAttributeParserRuleCall_2_0()); 
            	    
            pushFollow(FOLLOW_ruleAttribute_in_ruleAttributeConstraint2716);
            lv_attribute_2_0=ruleAttribute();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
            	        }
                   		set(
                   			current, 
                   			"attribute",
                    		lv_attribute_2_0, 
                    		"Attribute");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1183:2: ( (lv_comparison_3_0= ruleComparison ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1184:1: (lv_comparison_3_0= ruleComparison )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1184:1: (lv_comparison_3_0= ruleComparison )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1185:3: lv_comparison_3_0= ruleComparison
            {
             
            	        newCompositeNode(grammarAccess.getAttributeConstraintAccess().getComparisonComparisonParserRuleCall_3_0()); 
            	    
            pushFollow(FOLLOW_ruleComparison_in_ruleAttributeConstraint2737);
            lv_comparison_3_0=ruleComparison();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
            	        }
                   		set(
                   			current, 
                   			"comparison",
                    		lv_comparison_3_0, 
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


    // $ANTLR start "entryRuleAttribute"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1209:1: entryRuleAttribute returns [EObject current=null] : iv_ruleAttribute= ruleAttribute EOF ;
    public final EObject entryRuleAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttribute = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1210:2: (iv_ruleAttribute= ruleAttribute EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1211:2: iv_ruleAttribute= ruleAttribute EOF
            {
             newCompositeNode(grammarAccess.getAttributeRule()); 
            pushFollow(FOLLOW_ruleAttribute_in_entryRuleAttribute2773);
            iv_ruleAttribute=ruleAttribute();

            state._fsp--;

             current =iv_ruleAttribute; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttribute2783); 

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
    // $ANTLR end "entryRuleAttribute"


    // $ANTLR start "ruleAttribute"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1218:1: ruleAttribute returns [EObject current=null] : (this_AttributeDescendantOf_0= ruleAttributeDescendantOf | this_AttributeDescendantOrSelfOf_1= ruleAttributeDescendantOrSelfOf | this_ConceptReference_2= ruleConceptReference | this_Any_3= ruleAny ) ;
    public final EObject ruleAttribute() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeDescendantOf_0 = null;

        EObject this_AttributeDescendantOrSelfOf_1 = null;

        EObject this_ConceptReference_2 = null;

        EObject this_Any_3 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1221:28: ( (this_AttributeDescendantOf_0= ruleAttributeDescendantOf | this_AttributeDescendantOrSelfOf_1= ruleAttributeDescendantOrSelfOf | this_ConceptReference_2= ruleConceptReference | this_Any_3= ruleAny ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1222:1: (this_AttributeDescendantOf_0= ruleAttributeDescendantOf | this_AttributeDescendantOrSelfOf_1= ruleAttributeDescendantOrSelfOf | this_ConceptReference_2= ruleConceptReference | this_Any_3= ruleAny )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1222:1: (this_AttributeDescendantOf_0= ruleAttributeDescendantOf | this_AttributeDescendantOrSelfOf_1= ruleAttributeDescendantOrSelfOf | this_ConceptReference_2= ruleConceptReference | this_Any_3= ruleAny )
            int alt14=4;
            switch ( input.LA(1) ) {
            case RULE_LT:
                {
                alt14=1;
                }
                break;
            case RULE_DBL_LT:
                {
                alt14=2;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt14=3;
                }
                break;
            case RULE_WILDCARD:
                {
                alt14=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1223:5: this_AttributeDescendantOf_0= ruleAttributeDescendantOf
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeAccess().getAttributeDescendantOfParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleAttributeDescendantOf_in_ruleAttribute2830);
                    this_AttributeDescendantOf_0=ruleAttributeDescendantOf();

                    state._fsp--;

                     
                            current = this_AttributeDescendantOf_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1233:5: this_AttributeDescendantOrSelfOf_1= ruleAttributeDescendantOrSelfOf
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeAccess().getAttributeDescendantOrSelfOfParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleAttributeDescendantOrSelfOf_in_ruleAttribute2857);
                    this_AttributeDescendantOrSelfOf_1=ruleAttributeDescendantOrSelfOf();

                    state._fsp--;

                     
                            current = this_AttributeDescendantOrSelfOf_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1243:5: this_ConceptReference_2= ruleConceptReference
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeAccess().getConceptReferenceParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleAttribute2884);
                    this_ConceptReference_2=ruleConceptReference();

                    state._fsp--;

                     
                            current = this_ConceptReference_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1253:5: this_Any_3= ruleAny
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeAccess().getAnyParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_ruleAny_in_ruleAttribute2911);
                    this_Any_3=ruleAny();

                    state._fsp--;

                     
                            current = this_Any_3; 
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
    // $ANTLR end "ruleAttribute"


    // $ANTLR start "entryRuleAttributeDescendantOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1269:1: entryRuleAttributeDescendantOf returns [EObject current=null] : iv_ruleAttributeDescendantOf= ruleAttributeDescendantOf EOF ;
    public final EObject entryRuleAttributeDescendantOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeDescendantOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1270:2: (iv_ruleAttributeDescendantOf= ruleAttributeDescendantOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1271:2: iv_ruleAttributeDescendantOf= ruleAttributeDescendantOf EOF
            {
             newCompositeNode(grammarAccess.getAttributeDescendantOfRule()); 
            pushFollow(FOLLOW_ruleAttributeDescendantOf_in_entryRuleAttributeDescendantOf2946);
            iv_ruleAttributeDescendantOf=ruleAttributeDescendantOf();

            state._fsp--;

             current =iv_ruleAttributeDescendantOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeDescendantOf2956); 

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
    // $ANTLR end "entryRuleAttributeDescendantOf"


    // $ANTLR start "ruleAttributeDescendantOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1278:1: ruleAttributeDescendantOf returns [EObject current=null] : (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) ;
    public final EObject ruleAttributeDescendantOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1281:28: ( (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1282:1: (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1282:1: (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1282:2: this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FOLLOW_RULE_LT_in_ruleAttributeDescendantOf2992); 
             
                newLeafNode(this_LT_0, grammarAccess.getAttributeDescendantOfAccess().getLTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1286:1: ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1287:1: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1287:1: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1288:1: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1288:1: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==RULE_DIGIT_NONZERO) ) {
                alt15=1;
            }
            else if ( (LA15_0==RULE_WILDCARD) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1289:3: lv_constraint_1_1= ruleConceptReference
                    {
                     
                    	        newCompositeNode(grammarAccess.getAttributeDescendantOfAccess().getConstraintConceptReferenceParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleAttributeDescendantOf3014);
                    lv_constraint_1_1=ruleConceptReference();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAttributeDescendantOfRule());
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1304:8: lv_constraint_1_2= ruleAny
                    {
                     
                    	        newCompositeNode(grammarAccess.getAttributeDescendantOfAccess().getConstraintAnyParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleAny_in_ruleAttributeDescendantOf3033);
                    lv_constraint_1_2=ruleAny();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAttributeDescendantOfRule());
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
    // $ANTLR end "ruleAttributeDescendantOf"


    // $ANTLR start "entryRuleAttributeDescendantOrSelfOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1330:1: entryRuleAttributeDescendantOrSelfOf returns [EObject current=null] : iv_ruleAttributeDescendantOrSelfOf= ruleAttributeDescendantOrSelfOf EOF ;
    public final EObject entryRuleAttributeDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeDescendantOrSelfOf = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1331:2: (iv_ruleAttributeDescendantOrSelfOf= ruleAttributeDescendantOrSelfOf EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1332:2: iv_ruleAttributeDescendantOrSelfOf= ruleAttributeDescendantOrSelfOf EOF
            {
             newCompositeNode(grammarAccess.getAttributeDescendantOrSelfOfRule()); 
            pushFollow(FOLLOW_ruleAttributeDescendantOrSelfOf_in_entryRuleAttributeDescendantOrSelfOf3072);
            iv_ruleAttributeDescendantOrSelfOf=ruleAttributeDescendantOrSelfOf();

            state._fsp--;

             current =iv_ruleAttributeDescendantOrSelfOf; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeDescendantOrSelfOf3082); 

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
    // $ANTLR end "entryRuleAttributeDescendantOrSelfOf"


    // $ANTLR start "ruleAttributeDescendantOrSelfOf"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1339:1: ruleAttributeDescendantOrSelfOf returns [EObject current=null] : (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) ;
    public final EObject ruleAttributeDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_LT_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1342:28: ( (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1343:1: (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1343:1: (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1343:2: this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            {
            this_DBL_LT_0=(Token)match(input,RULE_DBL_LT,FOLLOW_RULE_DBL_LT_in_ruleAttributeDescendantOrSelfOf3118); 
             
                newLeafNode(this_DBL_LT_0, grammarAccess.getAttributeDescendantOrSelfOfAccess().getDBL_LTTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1347:1: ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1348:1: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1348:1: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1349:1: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1349:1: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1350:3: lv_constraint_1_1= ruleConceptReference
                    {
                     
                    	        newCompositeNode(grammarAccess.getAttributeDescendantOrSelfOfAccess().getConstraintConceptReferenceParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleConceptReference_in_ruleAttributeDescendantOrSelfOf3140);
                    lv_constraint_1_1=ruleConceptReference();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAttributeDescendantOrSelfOfRule());
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1365:8: lv_constraint_1_2= ruleAny
                    {
                     
                    	        newCompositeNode(grammarAccess.getAttributeDescendantOrSelfOfAccess().getConstraintAnyParserRuleCall_1_0_1()); 
                    	    
                    pushFollow(FOLLOW_ruleAny_in_ruleAttributeDescendantOrSelfOf3159);
                    lv_constraint_1_2=ruleAny();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAttributeDescendantOrSelfOfRule());
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
    // $ANTLR end "ruleAttributeDescendantOrSelfOf"


    // $ANTLR start "entryRuleCardinality"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1391:1: entryRuleCardinality returns [EObject current=null] : iv_ruleCardinality= ruleCardinality EOF ;
    public final EObject entryRuleCardinality() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCardinality = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1392:2: (iv_ruleCardinality= ruleCardinality EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1393:2: iv_ruleCardinality= ruleCardinality EOF
            {
             newCompositeNode(grammarAccess.getCardinalityRule()); 
            pushFollow(FOLLOW_ruleCardinality_in_entryRuleCardinality3198);
            iv_ruleCardinality=ruleCardinality();

            state._fsp--;

             current =iv_ruleCardinality; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCardinality3208); 

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
    // $ANTLR end "entryRuleCardinality"


    // $ANTLR start "ruleCardinality"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1400:1: ruleCardinality returns [EObject current=null] : (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE ) ;
    public final EObject ruleCardinality() throws RecognitionException {
        EObject current = null;

        Token this_SQUARE_OPEN_0=null;
        Token this_TO_2=null;
        Token this_SQUARE_CLOSE_4=null;
        AntlrDatatypeRuleToken lv_min_1_0 = null;

        AntlrDatatypeRuleToken lv_max_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1403:28: ( (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1404:1: (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1404:1: (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1404:2: this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE
            {
            this_SQUARE_OPEN_0=(Token)match(input,RULE_SQUARE_OPEN,FOLLOW_RULE_SQUARE_OPEN_in_ruleCardinality3244); 
             
                newLeafNode(this_SQUARE_OPEN_0, grammarAccess.getCardinalityAccess().getSQUARE_OPENTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1408:1: ( (lv_min_1_0= ruleNonNegativeInteger ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1409:1: (lv_min_1_0= ruleNonNegativeInteger )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1409:1: (lv_min_1_0= ruleNonNegativeInteger )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1410:3: lv_min_1_0= ruleNonNegativeInteger
            {
             
            	        newCompositeNode(grammarAccess.getCardinalityAccess().getMinNonNegativeIntegerParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleNonNegativeInteger_in_ruleCardinality3264);
            lv_min_1_0=ruleNonNegativeInteger();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCardinalityRule());
            	        }
                   		set(
                   			current, 
                   			"min",
                    		lv_min_1_0, 
                    		"NonNegativeInteger");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            this_TO_2=(Token)match(input,RULE_TO,FOLLOW_RULE_TO_in_ruleCardinality3275); 
             
                newLeafNode(this_TO_2, grammarAccess.getCardinalityAccess().getTOTerminalRuleCall_2()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1430:1: ( (lv_max_3_0= ruleMaxValue ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1431:1: (lv_max_3_0= ruleMaxValue )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1431:1: (lv_max_3_0= ruleMaxValue )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1432:3: lv_max_3_0= ruleMaxValue
            {
             
            	        newCompositeNode(grammarAccess.getCardinalityAccess().getMaxMaxValueParserRuleCall_3_0()); 
            	    
            pushFollow(FOLLOW_ruleMaxValue_in_ruleCardinality3295);
            lv_max_3_0=ruleMaxValue();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCardinalityRule());
            	        }
                   		set(
                   			current, 
                   			"max",
                    		lv_max_3_0, 
                    		"MaxValue");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            this_SQUARE_CLOSE_4=(Token)match(input,RULE_SQUARE_CLOSE,FOLLOW_RULE_SQUARE_CLOSE_in_ruleCardinality3306); 
             
                newLeafNode(this_SQUARE_CLOSE_4, grammarAccess.getCardinalityAccess().getSQUARE_CLOSETerminalRuleCall_4()); 
                

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
    // $ANTLR end "ruleCardinality"


    // $ANTLR start "entryRuleComparison"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1460:1: entryRuleComparison returns [EObject current=null] : iv_ruleComparison= ruleComparison EOF ;
    public final EObject entryRuleComparison() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComparison = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1461:2: (iv_ruleComparison= ruleComparison EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1462:2: iv_ruleComparison= ruleComparison EOF
            {
             newCompositeNode(grammarAccess.getComparisonRule()); 
            pushFollow(FOLLOW_ruleComparison_in_entryRuleComparison3341);
            iv_ruleComparison=ruleComparison();

            state._fsp--;

             current =iv_ruleComparison; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleComparison3351); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1469:1: ruleComparison returns [EObject current=null] : (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals ) ;
    public final EObject ruleComparison() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeValueEquals_0 = null;

        EObject this_AttributeValueNotEquals_1 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1472:28: ( (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1473:1: (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1473:1: (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals )
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1474:5: this_AttributeValueEquals_0= ruleAttributeValueEquals
                    {
                     
                            newCompositeNode(grammarAccess.getComparisonAccess().getAttributeValueEqualsParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleAttributeValueEquals_in_ruleComparison3398);
                    this_AttributeValueEquals_0=ruleAttributeValueEquals();

                    state._fsp--;

                     
                            current = this_AttributeValueEquals_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1484:5: this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals
                    {
                     
                            newCompositeNode(grammarAccess.getComparisonAccess().getAttributeValueNotEqualsParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleAttributeValueNotEquals_in_ruleComparison3425);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1500:1: entryRuleAttributeValueEquals returns [EObject current=null] : iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF ;
    public final EObject entryRuleAttributeValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValueEquals = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1501:2: (iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1502:2: iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF
            {
             newCompositeNode(grammarAccess.getAttributeValueEqualsRule()); 
            pushFollow(FOLLOW_ruleAttributeValueEquals_in_entryRuleAttributeValueEquals3460);
            iv_ruleAttributeValueEquals=ruleAttributeValueEquals();

            state._fsp--;

             current =iv_ruleAttributeValueEquals; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeValueEquals3470); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1509:1: ruleAttributeValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) ;
    public final EObject ruleAttributeValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        EObject lv_constraint_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1512:28: ( (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1513:1: (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1513:1: (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1513:2: this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FOLLOW_RULE_EQUAL_in_ruleAttributeValueEquals3506); 
             
                newLeafNode(this_EQUAL_0, grammarAccess.getAttributeValueEqualsAccess().getEQUALTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1517:1: ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1518:1: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1518:1: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1519:3: lv_constraint_1_0= ruleSimpleExpressionConstraint
            {
             
            	        newCompositeNode(grammarAccess.getAttributeValueEqualsAccess().getConstraintSimpleExpressionConstraintParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSimpleExpressionConstraint_in_ruleAttributeValueEquals3526);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1543:1: entryRuleAttributeValueNotEquals returns [EObject current=null] : iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF ;
    public final EObject entryRuleAttributeValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValueNotEquals = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1544:2: (iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1545:2: iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF
            {
             newCompositeNode(grammarAccess.getAttributeValueNotEqualsRule()); 
            pushFollow(FOLLOW_ruleAttributeValueNotEquals_in_entryRuleAttributeValueNotEquals3562);
            iv_ruleAttributeValueNotEquals=ruleAttributeValueNotEquals();

            state._fsp--;

             current =iv_ruleAttributeValueNotEquals; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeValueNotEquals3572); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1552:1: ruleAttributeValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) ;
    public final EObject ruleAttributeValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        EObject lv_constraint_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1555:28: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1556:1: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1556:1: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1556:2: this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FOLLOW_RULE_NOT_EQUAL_in_ruleAttributeValueNotEquals3608); 
             
                newLeafNode(this_NOT_EQUAL_0, grammarAccess.getAttributeValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1560:1: ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1561:1: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1561:1: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1562:3: lv_constraint_1_0= ruleSimpleExpressionConstraint
            {
             
            	        newCompositeNode(grammarAccess.getAttributeValueNotEqualsAccess().getConstraintSimpleExpressionConstraintParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSimpleExpressionConstraint_in_ruleAttributeValueNotEquals3628);
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


    // $ANTLR start "entryRuleNestedExpression"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1586:1: entryRuleNestedExpression returns [EObject current=null] : iv_ruleNestedExpression= ruleNestedExpression EOF ;
    public final EObject entryRuleNestedExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedExpression = null;


        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1587:2: (iv_ruleNestedExpression= ruleNestedExpression EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1588:2: iv_ruleNestedExpression= ruleNestedExpression EOF
            {
             newCompositeNode(grammarAccess.getNestedExpressionRule()); 
            pushFollow(FOLLOW_ruleNestedExpression_in_entryRuleNestedExpression3664);
            iv_ruleNestedExpression=ruleNestedExpression();

            state._fsp--;

             current =iv_ruleNestedExpression; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNestedExpression3674); 

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
    // $ANTLR end "entryRuleNestedExpression"


    // $ANTLR start "ruleNestedExpression"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1595:1: ruleNestedExpression returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestedExpression() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject lv_nested_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1598:28: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1599:1: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1599:1: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1599:2: this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FOLLOW_RULE_ROUND_OPEN_in_ruleNestedExpression3710); 
             
                newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedExpressionAccess().getROUND_OPENTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1603:1: ( (lv_nested_1_0= ruleExpressionConstraint ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1604:1: (lv_nested_1_0= ruleExpressionConstraint )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1604:1: (lv_nested_1_0= ruleExpressionConstraint )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1605:3: lv_nested_1_0= ruleExpressionConstraint
            {
             
            	        newCompositeNode(grammarAccess.getNestedExpressionAccess().getNestedExpressionConstraintParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleExpressionConstraint_in_ruleNestedExpression3730);
            lv_nested_1_0=ruleExpressionConstraint();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getNestedExpressionRule());
            	        }
                   		set(
                   			current, 
                   			"nested",
                    		lv_nested_1_0, 
                    		"ExpressionConstraint");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            this_ROUND_CLOSE_2=(Token)match(input,RULE_ROUND_CLOSE,FOLLOW_RULE_ROUND_CLOSE_in_ruleNestedExpression3741); 
             
                newLeafNode(this_ROUND_CLOSE_2, grammarAccess.getNestedExpressionAccess().getROUND_CLOSETerminalRuleCall_2()); 
                

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
    // $ANTLR end "ruleNestedExpression"


    // $ANTLR start "entryRuleSnomedIdentifier"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1633:1: entryRuleSnomedIdentifier returns [String current=null] : iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF ;
    public final String entryRuleSnomedIdentifier() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSnomedIdentifier = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1637:2: (iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1638:2: iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF
            {
             newCompositeNode(grammarAccess.getSnomedIdentifierRule()); 
            pushFollow(FOLLOW_ruleSnomedIdentifier_in_entryRuleSnomedIdentifier3783);
            iv_ruleSnomedIdentifier=ruleSnomedIdentifier();

            state._fsp--;

             current =iv_ruleSnomedIdentifier.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSnomedIdentifier3794); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1648:1: ruleSnomedIdentifier returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) ;
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
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1652:28: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1653:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1653:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1653:6: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            {
            this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3838); 

            		current.merge(this_DIGIT_NONZERO_0);
                
             
                newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_0()); 
                
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1660:1: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO )
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1660:6: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3859); 

                    		current.merge(this_DIGIT_NONZERO_1);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_1_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1668:10: this_ZERO_2= RULE_ZERO
                    {
                    this_ZERO_2=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3885); 

                    		current.merge(this_ZERO_2);
                        
                     
                        newLeafNode(this_ZERO_2, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_1_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1675:2: (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO )
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1675:7: this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_3=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3907); 

                    		current.merge(this_DIGIT_NONZERO_3);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_2_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1683:10: this_ZERO_4= RULE_ZERO
                    {
                    this_ZERO_4=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3933); 

                    		current.merge(this_ZERO_4);
                        
                     
                        newLeafNode(this_ZERO_4, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_2_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1690:2: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1690:7: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3955); 

                    		current.merge(this_DIGIT_NONZERO_5);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_3_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1698:10: this_ZERO_6= RULE_ZERO
                    {
                    this_ZERO_6=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3981); 

                    		current.merge(this_ZERO_6);
                        
                     
                        newLeafNode(this_ZERO_6, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_3_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1705:2: (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO )
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
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1705:7: this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_7=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier4003); 

                    		current.merge(this_DIGIT_NONZERO_7);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_4_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1713:10: this_ZERO_8= RULE_ZERO
                    {
                    this_ZERO_8=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier4029); 

                    		current.merge(this_ZERO_8);
                        
                     
                        newLeafNode(this_ZERO_8, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_4_1()); 
                        

                    }
                    break;

            }

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1720:2: (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
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
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1720:7: this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_9=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier4051); 

            	    		current.merge(this_DIGIT_NONZERO_9);
            	        
            	     
            	        newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_5_0()); 
            	        

            	    }
            	    break;
            	case 2 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1728:10: this_ZERO_10= RULE_ZERO
            	    {
            	    this_ZERO_10=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier4077); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1746:1: entryRuleTerm returns [String current=null] : iv_ruleTerm= ruleTerm EOF ;
    public final String entryRuleTerm() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTerm = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1750:2: (iv_ruleTerm= ruleTerm EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1751:2: iv_ruleTerm= ruleTerm EOF
            {
             newCompositeNode(grammarAccess.getTermRule()); 
            pushFollow(FOLLOW_ruleTerm_in_entryRuleTerm4135);
            iv_ruleTerm=ruleTerm();

            state._fsp--;

             current =iv_ruleTerm.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTerm4146); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1761:1: ruleTerm returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) ;
    public final AntlrDatatypeRuleToken ruleTerm() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WS_1=null;
        AntlrDatatypeRuleToken this_TermCharacter_0 = null;

        AntlrDatatypeRuleToken this_TermCharacter_2 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1765:28: ( ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1766:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1766:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1766:2: (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1766:2: (this_TermCharacter_0= ruleTermCharacter )+
            int cnt23=0;
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( ((LA23_0>=RULE_OR && LA23_0<=RULE_CARET)||(LA23_0>=RULE_WILDCARD && LA23_0<=RULE_ZERO)||(LA23_0>=RULE_AND && LA23_0<=RULE_OTHER_CHARACTER)) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1767:5: this_TermCharacter_0= ruleTermCharacter
            	    {
            	     
            	            newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_0()); 
            	        
            	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm4198);
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

            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1777:3: ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==RULE_WS) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1777:4: (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+
            	    {
            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1777:4: (this_WS_1= RULE_WS )+
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
            	    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1777:9: this_WS_1= RULE_WS
            	    	    {
            	    	    this_WS_1=(Token)match(input,RULE_WS,FOLLOW_RULE_WS_in_ruleTerm4222); 

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

            	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1784:3: (this_TermCharacter_2= ruleTermCharacter )+
            	    int cnt25=0;
            	    loop25:
            	    do {
            	        int alt25=2;
            	        int LA25_0 = input.LA(1);

            	        if ( ((LA25_0>=RULE_OR && LA25_0<=RULE_CARET)||(LA25_0>=RULE_WILDCARD && LA25_0<=RULE_ZERO)||(LA25_0>=RULE_AND && LA25_0<=RULE_OTHER_CHARACTER)) ) {
            	            alt25=1;
            	        }


            	        switch (alt25) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1785:5: this_TermCharacter_2= ruleTermCharacter
            	    	    {
            	    	     
            	    	            newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_1_1()); 
            	    	        
            	    	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm4252);
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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1806:1: entryRuleTermCharacter returns [String current=null] : iv_ruleTermCharacter= ruleTermCharacter EOF ;
    public final String entryRuleTermCharacter() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTermCharacter = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1810:2: (iv_ruleTermCharacter= ruleTermCharacter EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1811:2: iv_ruleTermCharacter= ruleTermCharacter EOF
            {
             newCompositeNode(grammarAccess.getTermCharacterRule()); 
            pushFollow(FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter4312);
            iv_ruleTermCharacter=ruleTermCharacter();

            state._fsp--;

             current =iv_ruleTermCharacter.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTermCharacter4323); 

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
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1821:1: ruleTermCharacter returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_LT_EM_4= RULE_LT_EM | this_GT_EM_5= RULE_GT_EM | this_AND_6= RULE_AND | this_OR_7= RULE_OR | this_NOT_8= RULE_NOT | this_MINUS_9= RULE_MINUS | this_ZERO_10= RULE_ZERO | this_DIGIT_NONZERO_11= RULE_DIGIT_NONZERO | this_LETTER_12= RULE_LETTER | this_CARET_13= RULE_CARET | this_EQUAL_14= RULE_EQUAL | this_NOT_EQUAL_15= RULE_NOT_EQUAL | this_PLUS_16= RULE_PLUS | this_CURLY_OPEN_17= RULE_CURLY_OPEN | this_CURLY_CLOSE_18= RULE_CURLY_CLOSE | this_ROUND_OPEN_19= RULE_ROUND_OPEN | this_ROUND_CLOSE_20= RULE_ROUND_CLOSE | this_SQUARE_OPEN_21= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_22= RULE_SQUARE_CLOSE | this_DOT_23= RULE_DOT | this_COLON_24= RULE_COLON | this_COMMA_25= RULE_COMMA | this_REVERSED_26= RULE_REVERSED | this_TO_27= RULE_TO | this_WILDCARD_28= RULE_WILDCARD | this_OTHER_CHARACTER_29= RULE_OTHER_CHARACTER ) ;
    public final AntlrDatatypeRuleToken ruleTermCharacter() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_LT_0=null;
        Token this_GT_1=null;
        Token this_DBL_LT_2=null;
        Token this_DBL_GT_3=null;
        Token this_LT_EM_4=null;
        Token this_GT_EM_5=null;
        Token this_AND_6=null;
        Token this_OR_7=null;
        Token this_NOT_8=null;
        Token this_MINUS_9=null;
        Token this_ZERO_10=null;
        Token this_DIGIT_NONZERO_11=null;
        Token this_LETTER_12=null;
        Token this_CARET_13=null;
        Token this_EQUAL_14=null;
        Token this_NOT_EQUAL_15=null;
        Token this_PLUS_16=null;
        Token this_CURLY_OPEN_17=null;
        Token this_CURLY_CLOSE_18=null;
        Token this_ROUND_OPEN_19=null;
        Token this_ROUND_CLOSE_20=null;
        Token this_SQUARE_OPEN_21=null;
        Token this_SQUARE_CLOSE_22=null;
        Token this_DOT_23=null;
        Token this_COLON_24=null;
        Token this_COMMA_25=null;
        Token this_REVERSED_26=null;
        Token this_TO_27=null;
        Token this_WILDCARD_28=null;
        Token this_OTHER_CHARACTER_29=null;

         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1825:28: ( (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_LT_EM_4= RULE_LT_EM | this_GT_EM_5= RULE_GT_EM | this_AND_6= RULE_AND | this_OR_7= RULE_OR | this_NOT_8= RULE_NOT | this_MINUS_9= RULE_MINUS | this_ZERO_10= RULE_ZERO | this_DIGIT_NONZERO_11= RULE_DIGIT_NONZERO | this_LETTER_12= RULE_LETTER | this_CARET_13= RULE_CARET | this_EQUAL_14= RULE_EQUAL | this_NOT_EQUAL_15= RULE_NOT_EQUAL | this_PLUS_16= RULE_PLUS | this_CURLY_OPEN_17= RULE_CURLY_OPEN | this_CURLY_CLOSE_18= RULE_CURLY_CLOSE | this_ROUND_OPEN_19= RULE_ROUND_OPEN | this_ROUND_CLOSE_20= RULE_ROUND_CLOSE | this_SQUARE_OPEN_21= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_22= RULE_SQUARE_CLOSE | this_DOT_23= RULE_DOT | this_COLON_24= RULE_COLON | this_COMMA_25= RULE_COMMA | this_REVERSED_26= RULE_REVERSED | this_TO_27= RULE_TO | this_WILDCARD_28= RULE_WILDCARD | this_OTHER_CHARACTER_29= RULE_OTHER_CHARACTER ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1826:1: (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_LT_EM_4= RULE_LT_EM | this_GT_EM_5= RULE_GT_EM | this_AND_6= RULE_AND | this_OR_7= RULE_OR | this_NOT_8= RULE_NOT | this_MINUS_9= RULE_MINUS | this_ZERO_10= RULE_ZERO | this_DIGIT_NONZERO_11= RULE_DIGIT_NONZERO | this_LETTER_12= RULE_LETTER | this_CARET_13= RULE_CARET | this_EQUAL_14= RULE_EQUAL | this_NOT_EQUAL_15= RULE_NOT_EQUAL | this_PLUS_16= RULE_PLUS | this_CURLY_OPEN_17= RULE_CURLY_OPEN | this_CURLY_CLOSE_18= RULE_CURLY_CLOSE | this_ROUND_OPEN_19= RULE_ROUND_OPEN | this_ROUND_CLOSE_20= RULE_ROUND_CLOSE | this_SQUARE_OPEN_21= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_22= RULE_SQUARE_CLOSE | this_DOT_23= RULE_DOT | this_COLON_24= RULE_COLON | this_COMMA_25= RULE_COMMA | this_REVERSED_26= RULE_REVERSED | this_TO_27= RULE_TO | this_WILDCARD_28= RULE_WILDCARD | this_OTHER_CHARACTER_29= RULE_OTHER_CHARACTER )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1826:1: (this_LT_0= RULE_LT | this_GT_1= RULE_GT | this_DBL_LT_2= RULE_DBL_LT | this_DBL_GT_3= RULE_DBL_GT | this_LT_EM_4= RULE_LT_EM | this_GT_EM_5= RULE_GT_EM | this_AND_6= RULE_AND | this_OR_7= RULE_OR | this_NOT_8= RULE_NOT | this_MINUS_9= RULE_MINUS | this_ZERO_10= RULE_ZERO | this_DIGIT_NONZERO_11= RULE_DIGIT_NONZERO | this_LETTER_12= RULE_LETTER | this_CARET_13= RULE_CARET | this_EQUAL_14= RULE_EQUAL | this_NOT_EQUAL_15= RULE_NOT_EQUAL | this_PLUS_16= RULE_PLUS | this_CURLY_OPEN_17= RULE_CURLY_OPEN | this_CURLY_CLOSE_18= RULE_CURLY_CLOSE | this_ROUND_OPEN_19= RULE_ROUND_OPEN | this_ROUND_CLOSE_20= RULE_ROUND_CLOSE | this_SQUARE_OPEN_21= RULE_SQUARE_OPEN | this_SQUARE_CLOSE_22= RULE_SQUARE_CLOSE | this_DOT_23= RULE_DOT | this_COLON_24= RULE_COLON | this_COMMA_25= RULE_COMMA | this_REVERSED_26= RULE_REVERSED | this_TO_27= RULE_TO | this_WILDCARD_28= RULE_WILDCARD | this_OTHER_CHARACTER_29= RULE_OTHER_CHARACTER )
            int alt27=30;
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
            case RULE_LT_EM:
                {
                alt27=5;
                }
                break;
            case RULE_GT_EM:
                {
                alt27=6;
                }
                break;
            case RULE_AND:
                {
                alt27=7;
                }
                break;
            case RULE_OR:
                {
                alt27=8;
                }
                break;
            case RULE_NOT:
                {
                alt27=9;
                }
                break;
            case RULE_MINUS:
                {
                alt27=10;
                }
                break;
            case RULE_ZERO:
                {
                alt27=11;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt27=12;
                }
                break;
            case RULE_LETTER:
                {
                alt27=13;
                }
                break;
            case RULE_CARET:
                {
                alt27=14;
                }
                break;
            case RULE_EQUAL:
                {
                alt27=15;
                }
                break;
            case RULE_NOT_EQUAL:
                {
                alt27=16;
                }
                break;
            case RULE_PLUS:
                {
                alt27=17;
                }
                break;
            case RULE_CURLY_OPEN:
                {
                alt27=18;
                }
                break;
            case RULE_CURLY_CLOSE:
                {
                alt27=19;
                }
                break;
            case RULE_ROUND_OPEN:
                {
                alt27=20;
                }
                break;
            case RULE_ROUND_CLOSE:
                {
                alt27=21;
                }
                break;
            case RULE_SQUARE_OPEN:
                {
                alt27=22;
                }
                break;
            case RULE_SQUARE_CLOSE:
                {
                alt27=23;
                }
                break;
            case RULE_DOT:
                {
                alt27=24;
                }
                break;
            case RULE_COLON:
                {
                alt27=25;
                }
                break;
            case RULE_COMMA:
                {
                alt27=26;
                }
                break;
            case RULE_REVERSED:
                {
                alt27=27;
                }
                break;
            case RULE_TO:
                {
                alt27=28;
                }
                break;
            case RULE_WILDCARD:
                {
                alt27=29;
                }
                break;
            case RULE_OTHER_CHARACTER:
                {
                alt27=30;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1826:6: this_LT_0= RULE_LT
                    {
                    this_LT_0=(Token)match(input,RULE_LT,FOLLOW_RULE_LT_in_ruleTermCharacter4367); 

                    		current.merge(this_LT_0);
                        
                     
                        newLeafNode(this_LT_0, grammarAccess.getTermCharacterAccess().getLTTerminalRuleCall_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1834:10: this_GT_1= RULE_GT
                    {
                    this_GT_1=(Token)match(input,RULE_GT,FOLLOW_RULE_GT_in_ruleTermCharacter4393); 

                    		current.merge(this_GT_1);
                        
                     
                        newLeafNode(this_GT_1, grammarAccess.getTermCharacterAccess().getGTTerminalRuleCall_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1842:10: this_DBL_LT_2= RULE_DBL_LT
                    {
                    this_DBL_LT_2=(Token)match(input,RULE_DBL_LT,FOLLOW_RULE_DBL_LT_in_ruleTermCharacter4419); 

                    		current.merge(this_DBL_LT_2);
                        
                     
                        newLeafNode(this_DBL_LT_2, grammarAccess.getTermCharacterAccess().getDBL_LTTerminalRuleCall_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1850:10: this_DBL_GT_3= RULE_DBL_GT
                    {
                    this_DBL_GT_3=(Token)match(input,RULE_DBL_GT,FOLLOW_RULE_DBL_GT_in_ruleTermCharacter4445); 

                    		current.merge(this_DBL_GT_3);
                        
                     
                        newLeafNode(this_DBL_GT_3, grammarAccess.getTermCharacterAccess().getDBL_GTTerminalRuleCall_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1858:10: this_LT_EM_4= RULE_LT_EM
                    {
                    this_LT_EM_4=(Token)match(input,RULE_LT_EM,FOLLOW_RULE_LT_EM_in_ruleTermCharacter4471); 

                    		current.merge(this_LT_EM_4);
                        
                     
                        newLeafNode(this_LT_EM_4, grammarAccess.getTermCharacterAccess().getLT_EMTerminalRuleCall_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1866:10: this_GT_EM_5= RULE_GT_EM
                    {
                    this_GT_EM_5=(Token)match(input,RULE_GT_EM,FOLLOW_RULE_GT_EM_in_ruleTermCharacter4497); 

                    		current.merge(this_GT_EM_5);
                        
                     
                        newLeafNode(this_GT_EM_5, grammarAccess.getTermCharacterAccess().getGT_EMTerminalRuleCall_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1874:10: this_AND_6= RULE_AND
                    {
                    this_AND_6=(Token)match(input,RULE_AND,FOLLOW_RULE_AND_in_ruleTermCharacter4523); 

                    		current.merge(this_AND_6);
                        
                     
                        newLeafNode(this_AND_6, grammarAccess.getTermCharacterAccess().getANDTerminalRuleCall_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1882:10: this_OR_7= RULE_OR
                    {
                    this_OR_7=(Token)match(input,RULE_OR,FOLLOW_RULE_OR_in_ruleTermCharacter4549); 

                    		current.merge(this_OR_7);
                        
                     
                        newLeafNode(this_OR_7, grammarAccess.getTermCharacterAccess().getORTerminalRuleCall_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1890:10: this_NOT_8= RULE_NOT
                    {
                    this_NOT_8=(Token)match(input,RULE_NOT,FOLLOW_RULE_NOT_in_ruleTermCharacter4575); 

                    		current.merge(this_NOT_8);
                        
                     
                        newLeafNode(this_NOT_8, grammarAccess.getTermCharacterAccess().getNOTTerminalRuleCall_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1898:10: this_MINUS_9= RULE_MINUS
                    {
                    this_MINUS_9=(Token)match(input,RULE_MINUS,FOLLOW_RULE_MINUS_in_ruleTermCharacter4601); 

                    		current.merge(this_MINUS_9);
                        
                     
                        newLeafNode(this_MINUS_9, grammarAccess.getTermCharacterAccess().getMINUSTerminalRuleCall_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1906:10: this_ZERO_10= RULE_ZERO
                    {
                    this_ZERO_10=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleTermCharacter4627); 

                    		current.merge(this_ZERO_10);
                        
                     
                        newLeafNode(this_ZERO_10, grammarAccess.getTermCharacterAccess().getZEROTerminalRuleCall_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1914:10: this_DIGIT_NONZERO_11= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_11=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter4653); 

                    		current.merge(this_DIGIT_NONZERO_11);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_11, grammarAccess.getTermCharacterAccess().getDIGIT_NONZEROTerminalRuleCall_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1922:10: this_LETTER_12= RULE_LETTER
                    {
                    this_LETTER_12=(Token)match(input,RULE_LETTER,FOLLOW_RULE_LETTER_in_ruleTermCharacter4679); 

                    		current.merge(this_LETTER_12);
                        
                     
                        newLeafNode(this_LETTER_12, grammarAccess.getTermCharacterAccess().getLETTERTerminalRuleCall_12()); 
                        

                    }
                    break;
                case 14 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1930:10: this_CARET_13= RULE_CARET
                    {
                    this_CARET_13=(Token)match(input,RULE_CARET,FOLLOW_RULE_CARET_in_ruleTermCharacter4705); 

                    		current.merge(this_CARET_13);
                        
                     
                        newLeafNode(this_CARET_13, grammarAccess.getTermCharacterAccess().getCARETTerminalRuleCall_13()); 
                        

                    }
                    break;
                case 15 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1938:10: this_EQUAL_14= RULE_EQUAL
                    {
                    this_EQUAL_14=(Token)match(input,RULE_EQUAL,FOLLOW_RULE_EQUAL_in_ruleTermCharacter4731); 

                    		current.merge(this_EQUAL_14);
                        
                     
                        newLeafNode(this_EQUAL_14, grammarAccess.getTermCharacterAccess().getEQUALTerminalRuleCall_14()); 
                        

                    }
                    break;
                case 16 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1946:10: this_NOT_EQUAL_15= RULE_NOT_EQUAL
                    {
                    this_NOT_EQUAL_15=(Token)match(input,RULE_NOT_EQUAL,FOLLOW_RULE_NOT_EQUAL_in_ruleTermCharacter4757); 

                    		current.merge(this_NOT_EQUAL_15);
                        
                     
                        newLeafNode(this_NOT_EQUAL_15, grammarAccess.getTermCharacterAccess().getNOT_EQUALTerminalRuleCall_15()); 
                        

                    }
                    break;
                case 17 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1954:10: this_PLUS_16= RULE_PLUS
                    {
                    this_PLUS_16=(Token)match(input,RULE_PLUS,FOLLOW_RULE_PLUS_in_ruleTermCharacter4783); 

                    		current.merge(this_PLUS_16);
                        
                     
                        newLeafNode(this_PLUS_16, grammarAccess.getTermCharacterAccess().getPLUSTerminalRuleCall_16()); 
                        

                    }
                    break;
                case 18 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1962:10: this_CURLY_OPEN_17= RULE_CURLY_OPEN
                    {
                    this_CURLY_OPEN_17=(Token)match(input,RULE_CURLY_OPEN,FOLLOW_RULE_CURLY_OPEN_in_ruleTermCharacter4809); 

                    		current.merge(this_CURLY_OPEN_17);
                        
                     
                        newLeafNode(this_CURLY_OPEN_17, grammarAccess.getTermCharacterAccess().getCURLY_OPENTerminalRuleCall_17()); 
                        

                    }
                    break;
                case 19 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1970:10: this_CURLY_CLOSE_18= RULE_CURLY_CLOSE
                    {
                    this_CURLY_CLOSE_18=(Token)match(input,RULE_CURLY_CLOSE,FOLLOW_RULE_CURLY_CLOSE_in_ruleTermCharacter4835); 

                    		current.merge(this_CURLY_CLOSE_18);
                        
                     
                        newLeafNode(this_CURLY_CLOSE_18, grammarAccess.getTermCharacterAccess().getCURLY_CLOSETerminalRuleCall_18()); 
                        

                    }
                    break;
                case 20 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1978:10: this_ROUND_OPEN_19= RULE_ROUND_OPEN
                    {
                    this_ROUND_OPEN_19=(Token)match(input,RULE_ROUND_OPEN,FOLLOW_RULE_ROUND_OPEN_in_ruleTermCharacter4861); 

                    		current.merge(this_ROUND_OPEN_19);
                        
                     
                        newLeafNode(this_ROUND_OPEN_19, grammarAccess.getTermCharacterAccess().getROUND_OPENTerminalRuleCall_19()); 
                        

                    }
                    break;
                case 21 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1986:10: this_ROUND_CLOSE_20= RULE_ROUND_CLOSE
                    {
                    this_ROUND_CLOSE_20=(Token)match(input,RULE_ROUND_CLOSE,FOLLOW_RULE_ROUND_CLOSE_in_ruleTermCharacter4887); 

                    		current.merge(this_ROUND_CLOSE_20);
                        
                     
                        newLeafNode(this_ROUND_CLOSE_20, grammarAccess.getTermCharacterAccess().getROUND_CLOSETerminalRuleCall_20()); 
                        

                    }
                    break;
                case 22 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:1994:10: this_SQUARE_OPEN_21= RULE_SQUARE_OPEN
                    {
                    this_SQUARE_OPEN_21=(Token)match(input,RULE_SQUARE_OPEN,FOLLOW_RULE_SQUARE_OPEN_in_ruleTermCharacter4913); 

                    		current.merge(this_SQUARE_OPEN_21);
                        
                     
                        newLeafNode(this_SQUARE_OPEN_21, grammarAccess.getTermCharacterAccess().getSQUARE_OPENTerminalRuleCall_21()); 
                        

                    }
                    break;
                case 23 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2002:10: this_SQUARE_CLOSE_22= RULE_SQUARE_CLOSE
                    {
                    this_SQUARE_CLOSE_22=(Token)match(input,RULE_SQUARE_CLOSE,FOLLOW_RULE_SQUARE_CLOSE_in_ruleTermCharacter4939); 

                    		current.merge(this_SQUARE_CLOSE_22);
                        
                     
                        newLeafNode(this_SQUARE_CLOSE_22, grammarAccess.getTermCharacterAccess().getSQUARE_CLOSETerminalRuleCall_22()); 
                        

                    }
                    break;
                case 24 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2010:10: this_DOT_23= RULE_DOT
                    {
                    this_DOT_23=(Token)match(input,RULE_DOT,FOLLOW_RULE_DOT_in_ruleTermCharacter4965); 

                    		current.merge(this_DOT_23);
                        
                     
                        newLeafNode(this_DOT_23, grammarAccess.getTermCharacterAccess().getDOTTerminalRuleCall_23()); 
                        

                    }
                    break;
                case 25 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2018:10: this_COLON_24= RULE_COLON
                    {
                    this_COLON_24=(Token)match(input,RULE_COLON,FOLLOW_RULE_COLON_in_ruleTermCharacter4991); 

                    		current.merge(this_COLON_24);
                        
                     
                        newLeafNode(this_COLON_24, grammarAccess.getTermCharacterAccess().getCOLONTerminalRuleCall_24()); 
                        

                    }
                    break;
                case 26 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2026:10: this_COMMA_25= RULE_COMMA
                    {
                    this_COMMA_25=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleTermCharacter5017); 

                    		current.merge(this_COMMA_25);
                        
                     
                        newLeafNode(this_COMMA_25, grammarAccess.getTermCharacterAccess().getCOMMATerminalRuleCall_25()); 
                        

                    }
                    break;
                case 27 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2034:10: this_REVERSED_26= RULE_REVERSED
                    {
                    this_REVERSED_26=(Token)match(input,RULE_REVERSED,FOLLOW_RULE_REVERSED_in_ruleTermCharacter5043); 

                    		current.merge(this_REVERSED_26);
                        
                     
                        newLeafNode(this_REVERSED_26, grammarAccess.getTermCharacterAccess().getREVERSEDTerminalRuleCall_26()); 
                        

                    }
                    break;
                case 28 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2042:10: this_TO_27= RULE_TO
                    {
                    this_TO_27=(Token)match(input,RULE_TO,FOLLOW_RULE_TO_in_ruleTermCharacter5069); 

                    		current.merge(this_TO_27);
                        
                     
                        newLeafNode(this_TO_27, grammarAccess.getTermCharacterAccess().getTOTerminalRuleCall_27()); 
                        

                    }
                    break;
                case 29 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2050:10: this_WILDCARD_28= RULE_WILDCARD
                    {
                    this_WILDCARD_28=(Token)match(input,RULE_WILDCARD,FOLLOW_RULE_WILDCARD_in_ruleTermCharacter5095); 

                    		current.merge(this_WILDCARD_28);
                        
                     
                        newLeafNode(this_WILDCARD_28, grammarAccess.getTermCharacterAccess().getWILDCARDTerminalRuleCall_28()); 
                        

                    }
                    break;
                case 30 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2058:10: this_OTHER_CHARACTER_29= RULE_OTHER_CHARACTER
                    {
                    this_OTHER_CHARACTER_29=(Token)match(input,RULE_OTHER_CHARACTER,FOLLOW_RULE_OTHER_CHARACTER_in_ruleTermCharacter5121); 

                    		current.merge(this_OTHER_CHARACTER_29);
                        
                     
                        newLeafNode(this_OTHER_CHARACTER_29, grammarAccess.getTermCharacterAccess().getOTHER_CHARACTERTerminalRuleCall_29()); 
                        

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


    // $ANTLR start "entryRuleNonNegativeInteger"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2076:1: entryRuleNonNegativeInteger returns [String current=null] : iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF ;
    public final String entryRuleNonNegativeInteger() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNonNegativeInteger = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2080:2: (iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2081:2: iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF
            {
             newCompositeNode(grammarAccess.getNonNegativeIntegerRule()); 
            pushFollow(FOLLOW_ruleNonNegativeInteger_in_entryRuleNonNegativeInteger5177);
            iv_ruleNonNegativeInteger=ruleNonNegativeInteger();

            state._fsp--;

             current =iv_ruleNonNegativeInteger.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNonNegativeInteger5188); 

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
    // $ANTLR end "entryRuleNonNegativeInteger"


    // $ANTLR start "ruleNonNegativeInteger"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2091:1: ruleNonNegativeInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) ;
    public final AntlrDatatypeRuleToken ruleNonNegativeInteger() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ZERO_0=null;
        Token this_DIGIT_NONZERO_1=null;
        Token this_DIGIT_NONZERO_2=null;
        Token this_ZERO_3=null;

         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2095:28: ( (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2096:1: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2096:1: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==RULE_ZERO) ) {
                alt29=1;
            }
            else if ( (LA29_0==RULE_DIGIT_NONZERO) ) {
                alt29=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2096:6: this_ZERO_0= RULE_ZERO
                    {
                    this_ZERO_0=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleNonNegativeInteger5232); 

                    		current.merge(this_ZERO_0);
                        
                     
                        newLeafNode(this_ZERO_0, grammarAccess.getNonNegativeIntegerAccess().getZEROTerminalRuleCall_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2104:6: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    {
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2104:6: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2104:11: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleNonNegativeInteger5259); 

                    		current.merge(this_DIGIT_NONZERO_1);
                        
                     
                        newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_0()); 
                        
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2111:1: (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
                    loop28:
                    do {
                        int alt28=3;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0==RULE_DIGIT_NONZERO) ) {
                            alt28=1;
                        }
                        else if ( (LA28_0==RULE_ZERO) ) {
                            alt28=2;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2111:6: this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO
                    	    {
                    	    this_DIGIT_NONZERO_2=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleNonNegativeInteger5280); 

                    	    		current.merge(this_DIGIT_NONZERO_2);
                    	        
                    	     
                    	        newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_1_0()); 
                    	        

                    	    }
                    	    break;
                    	case 2 :
                    	    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2119:10: this_ZERO_3= RULE_ZERO
                    	    {
                    	    this_ZERO_3=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleNonNegativeInteger5306); 

                    	    		current.merge(this_ZERO_3);
                    	        
                    	     
                    	        newLeafNode(this_ZERO_3, grammarAccess.getNonNegativeIntegerAccess().getZEROTerminalRuleCall_1_1_1()); 
                    	        

                    	    }
                    	    break;

                    	default :
                    	    break loop28;
                        }
                    } while (true);


                    }


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
    // $ANTLR end "ruleNonNegativeInteger"


    // $ANTLR start "entryRuleMaxValue"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2137:1: entryRuleMaxValue returns [String current=null] : iv_ruleMaxValue= ruleMaxValue EOF ;
    public final String entryRuleMaxValue() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleMaxValue = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2141:2: (iv_ruleMaxValue= ruleMaxValue EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2142:2: iv_ruleMaxValue= ruleMaxValue EOF
            {
             newCompositeNode(grammarAccess.getMaxValueRule()); 
            pushFollow(FOLLOW_ruleMaxValue_in_entryRuleMaxValue5365);
            iv_ruleMaxValue=ruleMaxValue();

            state._fsp--;

             current =iv_ruleMaxValue.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleMaxValue5376); 

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
    // $ANTLR end "entryRuleMaxValue"


    // $ANTLR start "ruleMaxValue"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2152:1: ruleMaxValue returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD ) ;
    public final AntlrDatatypeRuleToken ruleMaxValue() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WILDCARD_1=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_0 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2156:28: ( (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2157:1: (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2157:1: (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( ((LA30_0>=RULE_DIGIT_NONZERO && LA30_0<=RULE_ZERO)) ) {
                alt30=1;
            }
            else if ( (LA30_0==RULE_WILDCARD) ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2158:5: this_NonNegativeInteger_0= ruleNonNegativeInteger
                    {
                     
                            newCompositeNode(grammarAccess.getMaxValueAccess().getNonNegativeIntegerParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleNonNegativeInteger_in_ruleMaxValue5427);
                    this_NonNegativeInteger_0=ruleNonNegativeInteger();

                    state._fsp--;


                    		current.merge(this_NonNegativeInteger_0);
                        
                     
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2169:10: this_WILDCARD_1= RULE_WILDCARD
                    {
                    this_WILDCARD_1=(Token)match(input,RULE_WILDCARD,FOLLOW_RULE_WILDCARD_in_ruleMaxValue5453); 

                    		current.merge(this_WILDCARD_1);
                        
                     
                        newLeafNode(this_WILDCARD_1, grammarAccess.getMaxValueAccess().getWILDCARDTerminalRuleCall_1()); 
                        

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
    // $ANTLR end "ruleMaxValue"


    // $ANTLR start "entryRuleAndOperator"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2187:1: entryRuleAndOperator returns [String current=null] : iv_ruleAndOperator= ruleAndOperator EOF ;
    public final String entryRuleAndOperator() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAndOperator = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2191:2: (iv_ruleAndOperator= ruleAndOperator EOF )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2192:2: iv_ruleAndOperator= ruleAndOperator EOF
            {
             newCompositeNode(grammarAccess.getAndOperatorRule()); 
            pushFollow(FOLLOW_ruleAndOperator_in_entryRuleAndOperator5509);
            iv_ruleAndOperator=ruleAndOperator();

            state._fsp--;

             current =iv_ruleAndOperator.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAndOperator5520); 

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
    // $ANTLR end "entryRuleAndOperator"


    // $ANTLR start "ruleAndOperator"
    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2202:1: ruleAndOperator returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_AND_0= RULE_AND | this_COMMA_1= RULE_COMMA ) ;
    public final AntlrDatatypeRuleToken ruleAndOperator() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_AND_0=null;
        Token this_COMMA_1=null;

         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2206:28: ( (this_AND_0= RULE_AND | this_COMMA_1= RULE_COMMA ) )
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2207:1: (this_AND_0= RULE_AND | this_COMMA_1= RULE_COMMA )
            {
            // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2207:1: (this_AND_0= RULE_AND | this_COMMA_1= RULE_COMMA )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==RULE_AND) ) {
                alt31=1;
            }
            else if ( (LA31_0==RULE_COMMA) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2207:6: this_AND_0= RULE_AND
                    {
                    this_AND_0=(Token)match(input,RULE_AND,FOLLOW_RULE_AND_in_ruleAndOperator5564); 

                    		current.merge(this_AND_0);
                        
                     
                        newLeafNode(this_AND_0, grammarAccess.getAndOperatorAccess().getANDTerminalRuleCall_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.snomed.ecl/src-gen/com/b2international/snowowl/snomed/ecl/parser/antlr/internal/InternalEcl.g:2215:10: this_COMMA_1= RULE_COMMA
                    {
                    this_COMMA_1=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleAndOperator5590); 

                    		current.merge(this_COMMA_1);
                        
                     
                        newLeafNode(this_COMMA_1, grammarAccess.getAndOperatorAccess().getCOMMATerminalRuleCall_1()); 
                        

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
    // $ANTLR end "ruleAndOperator"

    // Delegated rules


    protected DFA10 dfa10 = new DFA10(this);
    protected DFA11 dfa11 = new DFA11(this);
    static final String DFA10_eotS =
        "\u00e7\uffff";
    static final String DFA10_eofS =
        "\1\1\5\uffff\1\1\2\uffff\1\1\1\uffff\1\1\30\uffff\6\1\136\uffff\1\1\1\uffff\1\1\1\uffff\1\1\132\uffff";
    static final String DFA10_minS =
        "\1\4\1\uffff\1\10\2\16\1\31\1\4\1\uffff\1\31\1\4\1\31\1\4\30\31\u00c3\4";
    static final String DFA10_maxS =
        "\1\42\1\uffff\3\31\1\32\1\42\1\uffff\1\32\1\42\1\32\1\42\30\32\6\42\136\43\1\42\1\43\1\42\1\43\1\42\132\43";
    static final String DFA10_acceptS =
        "\1\uffff\1\2\5\uffff\1\1\u00df\uffff";
    static final String DFA10_specialS =
        "\u00e7\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\2\1\1\22\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "",
            "\1\1\1\3\1\4\4\1\1\uffff\1\6\2\7\4\uffff\1\1\1\uffff\1\5",
            "\1\1\1\uffff\1\11\6\uffff\1\1\1\uffff\1\10",
            "\1\1\1\uffff\1\13\6\uffff\1\1\1\uffff\1\12",
            "\1\14\1\15",
            "\4\1\15\uffff\2\7\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "",
            "\1\16\1\17",
            "\4\1\15\uffff\2\7\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "\1\20\1\21",
            "\4\1\15\uffff\2\7\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "\1\22\1\23",
            "\1\22\1\23",
            "\1\24\1\25",
            "\1\24\1\25",
            "\1\26\1\27",
            "\1\26\1\27",
            "\1\30\1\31",
            "\1\30\1\31",
            "\1\32\1\33",
            "\1\32\1\33",
            "\1\34\1\35",
            "\1\34\1\35",
            "\1\36\1\37",
            "\1\36\1\37",
            "\1\40\1\41",
            "\1\40\1\41",
            "\1\42\1\43",
            "\1\42\1\43",
            "\1\44\1\45",
            "\1\44\1\45",
            "\1\46\1\47",
            "\1\46\1\47",
            "\1\50\1\51",
            "\1\50\1\51",
            "\4\1\7\uffff\1\52\5\uffff\2\7\1\uffff\1\1\1\44\1\45\1\uffff\1\1\5\uffff\1\1",
            "\4\1\7\uffff\1\52\5\uffff\2\7\1\uffff\1\1\1\44\1\45\1\uffff\1\1\5\uffff\1\1",
            "\4\1\7\uffff\1\53\5\uffff\2\7\1\uffff\1\1\1\46\1\47\1\uffff\1\1\5\uffff\1\1",
            "\4\1\7\uffff\1\53\5\uffff\2\7\1\uffff\1\1\1\46\1\47\1\uffff\1\1\5\uffff\1\1",
            "\4\1\7\uffff\1\54\5\uffff\2\7\1\uffff\1\1\1\50\1\51\1\uffff\1\1\5\uffff\1\1",
            "\4\1\7\uffff\1\54\5\uffff\2\7\1\uffff\1\1\1\50\1\51\1\uffff\1\1\5\uffff\1\1",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\uffff\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\uffff\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\uffff\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\uffff\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\uffff\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\uffff\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\64\1\66\1\105\1\104\1\61\1\55\1\57\1\62\1\56\1\60\1\72\1\u0088\1\111\1\107\1\102\1\110\1\103\1\73\1\74\1\100\1\101\1\70\1\67\1\u0087\1\63\1\65\1\71\1\75\1\76\1\77\1\106\1\112",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\122\1\124\1\143\1\142\1\117\1\113\1\115\1\120\1\114\1\116\1\130\1\u008a\1\147\1\145\1\140\1\146\1\141\1\131\1\132\1\136\1\137\1\126\1\125\1\u0089\1\121\1\123\1\127\1\133\1\134\1\135\1\144\1\150",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\160\1\162\1\u0081\1\u0080\1\155\1\151\1\153\1\156\1\152\1\154\1\166\1\u008c\1\u0085\1\u0083\1\176\1\u0084\1\177\1\167\1\170\1\174\1\175\1\164\1\163\1\u008b\1\157\1\161\1\165\1\171\1\172\1\173\1\u0082\1\u0086",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\uffff\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\4\1\15\uffff\2\7\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\uffff\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\4\1\15\uffff\2\7\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\uffff\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\4\1\15\uffff\2\7\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u0094\1\u0096\1\u00a5\1\u00a4\1\u0091\1\u008d\1\u008f\1\u0092\1\u008e\1\u0090\1\u009a\1\u0088\1\u00a9\1\u00a7\1\u00a2\1\u00a8\1\u00a3\1\u009b\1\u009c\1\u00a0\1\u00a1\1\u0098\1\u0097\1\u0087\1\u0093\1\u0095\1\u0099\1\u009d\1\u009e\1\u009f\1\u00a6\1\u00aa",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00b2\1\u00b4\1\u00c3\1\u00c2\1\u00af\1\u00ab\1\u00ad\1\u00b0\1\u00ac\1\u00ae\1\u00b8\1\u008a\1\u00c7\1\u00c5\1\u00c0\1\u00c6\1\u00c1\1\u00b9\1\u00ba\1\u00be\1\u00bf\1\u00b6\1\u00b5\1\u0089\1\u00b1\1\u00b3\1\u00b7\1\u00bb\1\u00bc\1\u00bd\1\u00c4\1\u00c8",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6",
            "\1\u00d0\1\u00d2\1\u00e1\1\u00e0\1\u00cd\1\u00c9\1\u00cb\1\u00ce\1\u00ca\1\u00cc\1\u00d6\1\u008c\1\u00e5\1\u00e3\1\u00de\1\u00e4\1\u00df\1\u00d7\1\u00d8\1\u00dc\1\u00dd\1\u00d4\1\u00d3\1\u008b\1\u00cf\1\u00d1\1\u00d5\1\u00d9\1\u00da\1\u00db\1\u00e2\1\u00e6"
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "()* loopback of 1018:1: ( () this_OR_2= RULE_OR ( (lv_right_3_0= ruleAndRefinement ) ) )*";
        }
    }
    static final String DFA11_eotS =
        "\u00e8\uffff";
    static final String DFA11_eofS =
        "\1\1\7\uffff\1\1\1\uffff\1\1\1\uffff\1\1\30\uffff\6\1\136\uffff\1\1\1\uffff\1\1\1\uffff\1\1\132\uffff";
    static final String DFA11_minS =
        "\1\4\1\uffff\2\10\1\uffff\2\16\1\31\1\4\1\31\1\4\1\31\1\4\30\31\u00c3\4";
    static final String DFA11_maxS =
        "\1\42\1\uffff\2\31\1\uffff\2\31\1\32\1\42\1\32\1\42\1\32\1\42\30\32\6\42\136\43\1\42\1\43\1\42\1\43\1\42\132\43";
    static final String DFA11_acceptS =
        "\1\uffff\1\2\2\uffff\1\1\u00e3\uffff";
    static final String DFA11_specialS =
        "\u00e8\uffff}>";
    static final String[] DFA11_transitionS = {
            "\2\1\22\uffff\1\1\3\uffff\1\2\5\uffff\1\3",
            "",
            "\1\1\1\5\1\6\4\1\1\uffff\1\10\2\4\4\uffff\1\1\1\uffff\1\7",
            "\1\1\1\5\1\6\4\1\1\uffff\1\10\2\4\4\uffff\1\1\1\uffff\1\7",
            "",
            "\1\1\1\uffff\1\12\6\uffff\1\1\1\uffff\1\11",
            "\1\1\1\uffff\1\14\6\uffff\1\1\1\uffff\1\13",
            "\1\15\1\16",
            "\4\1\15\uffff\2\4\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "\1\17\1\20",
            "\4\1\15\uffff\2\4\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "\1\21\1\22",
            "\4\1\15\uffff\2\4\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "\1\23\1\24",
            "\1\23\1\24",
            "\1\25\1\26",
            "\1\25\1\26",
            "\1\27\1\30",
            "\1\27\1\30",
            "\1\31\1\32",
            "\1\31\1\32",
            "\1\33\1\34",
            "\1\33\1\34",
            "\1\35\1\36",
            "\1\35\1\36",
            "\1\37\1\40",
            "\1\37\1\40",
            "\1\41\1\42",
            "\1\41\1\42",
            "\1\43\1\44",
            "\1\43\1\44",
            "\1\45\1\46",
            "\1\45\1\46",
            "\1\47\1\50",
            "\1\47\1\50",
            "\1\51\1\52",
            "\1\51\1\52",
            "\4\1\7\uffff\1\53\5\uffff\2\4\1\uffff\1\1\1\45\1\46\1\uffff\1\1\5\uffff\1\1",
            "\4\1\7\uffff\1\53\5\uffff\2\4\1\uffff\1\1\1\45\1\46\1\uffff\1\1\5\uffff\1\1",
            "\4\1\7\uffff\1\54\5\uffff\2\4\1\uffff\1\1\1\47\1\50\1\uffff\1\1\5\uffff\1\1",
            "\4\1\7\uffff\1\54\5\uffff\2\4\1\uffff\1\1\1\47\1\50\1\uffff\1\1\5\uffff\1\1",
            "\4\1\7\uffff\1\55\5\uffff\2\4\1\uffff\1\1\1\51\1\52\1\uffff\1\1\5\uffff\1\1",
            "\4\1\7\uffff\1\55\5\uffff\2\4\1\uffff\1\1\1\51\1\52\1\uffff\1\1\5\uffff\1\1",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\uffff\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\uffff\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\uffff\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\uffff\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\uffff\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\uffff\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\65\1\67\1\106\1\105\1\62\1\56\1\60\1\63\1\57\1\61\1\73\1\u0089\1\112\1\110\1\103\1\111\1\104\1\74\1\75\1\101\1\102\1\71\1\70\1\u0088\1\64\1\66\1\72\1\76\1\77\1\100\1\107\1\113",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\123\1\125\1\144\1\143\1\120\1\114\1\116\1\121\1\115\1\117\1\131\1\u008b\1\150\1\146\1\141\1\147\1\142\1\132\1\133\1\137\1\140\1\127\1\126\1\u008a\1\122\1\124\1\130\1\134\1\135\1\136\1\145\1\151",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\161\1\163\1\u0082\1\u0081\1\156\1\152\1\154\1\157\1\153\1\155\1\167\1\u008d\1\u0086\1\u0084\1\177\1\u0085\1\u0080\1\170\1\171\1\175\1\176\1\165\1\164\1\u008c\1\160\1\162\1\166\1\172\1\173\1\174\1\u0083\1\u0087",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\uffff\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\4\1\15\uffff\2\4\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\uffff\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\4\1\15\uffff\2\4\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\uffff\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\4\1\15\uffff\2\4\1\uffff\1\1\3\uffff\1\1\5\uffff\1\1",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u0095\1\u0097\1\u00a6\1\u00a5\1\u0092\1\u008e\1\u0090\1\u0093\1\u008f\1\u0091\1\u009b\1\u0089\1\u00aa\1\u00a8\1\u00a3\1\u00a9\1\u00a4\1\u009c\1\u009d\1\u00a1\1\u00a2\1\u0099\1\u0098\1\u0088\1\u0094\1\u0096\1\u009a\1\u009e\1\u009f\1\u00a0\1\u00a7\1\u00ab",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00b3\1\u00b5\1\u00c4\1\u00c3\1\u00b0\1\u00ac\1\u00ae\1\u00b1\1\u00ad\1\u00af\1\u00b9\1\u008b\1\u00c8\1\u00c6\1\u00c1\1\u00c7\1\u00c2\1\u00ba\1\u00bb\1\u00bf\1\u00c0\1\u00b7\1\u00b6\1\u008a\1\u00b2\1\u00b4\1\u00b8\1\u00bc\1\u00bd\1\u00be\1\u00c5\1\u00c9",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7",
            "\1\u00d1\1\u00d3\1\u00e2\1\u00e1\1\u00ce\1\u00ca\1\u00cc\1\u00cf\1\u00cb\1\u00cd\1\u00d7\1\u008d\1\u00e6\1\u00e4\1\u00df\1\u00e5\1\u00e0\1\u00d8\1\u00d9\1\u00dd\1\u00de\1\u00d5\1\u00d4\1\u008c\1\u00d0\1\u00d2\1\u00d6\1\u00da\1\u00db\1\u00dc\1\u00e3\1\u00e7"
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "()* loopback of 1076:1: ( () ruleAndOperator ( (lv_right_3_0= ruleAttributeConstraint ) ) )*";
        }
    }
 

    public static final BitSet FOLLOW_ruleExpressionConstraint_in_entryRuleExpressionConstraint81 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleExpressionConstraint91 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOrExpressionConstraint_in_ruleExpressionConstraint141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOrExpressionConstraint_in_entryRuleOrExpressionConstraint179 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOrExpressionConstraint189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAndExpressionConstraint_in_ruleOrExpressionConstraint236 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_RULE_OR_in_ruleOrExpressionConstraint256 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleAndExpressionConstraint_in_ruleOrExpressionConstraint276 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_ruleAndExpressionConstraint_in_entryRuleAndExpressionConstraint314 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAndExpressionConstraint324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint371 = new BitSet(new long[]{0x0000000410000002L});
    public static final BitSet FOLLOW_ruleAndOperator_in_ruleAndExpressionConstraint396 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleExclusionExpressionConstraint_in_ruleAndExpressionConstraint416 = new BitSet(new long[]{0x0000000410000002L});
    public static final BitSet FOLLOW_ruleExclusionExpressionConstraint_in_entryRuleExclusionExpressionConstraint454 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleExclusionExpressionConstraint464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRefinedExpressionConstraint_in_ruleExclusionExpressionConstraint511 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_RULE_MINUS_in_ruleExclusionExpressionConstraint531 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleRefinedExpressionConstraint_in_ruleExclusionExpressionConstraint551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRefinedExpressionConstraint_in_entryRuleRefinedExpressionConstraint589 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRefinedExpressionConstraint599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDottedExpressionConstraint_in_ruleRefinedExpressionConstraint646 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_RULE_COLON_in_ruleRefinedExpressionConstraint666 = new BitSet(new long[]{0x0000000002070600L});
    public static final BitSet FOLLOW_ruleRefinement_in_ruleRefinedExpressionConstraint686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDottedExpressionConstraint_in_entryRuleDottedExpressionConstraint724 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDottedExpressionConstraint734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSimpleExpressionConstraint_in_ruleDottedExpressionConstraint781 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_RULE_DOT_in_ruleDottedExpressionConstraint801 = new BitSet(new long[]{0x0000000002070600L});
    public static final BitSet FOLLOW_ruleAttribute_in_ruleDottedExpressionConstraint821 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_ruleSimpleExpressionConstraint_in_entryRuleSimpleExpressionConstraint859 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSimpleExpressionConstraint869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleChildOf_in_ruleSimpleExpressionConstraint916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOf_in_ruleSimpleExpressionConstraint943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOrSelfOf_in_ruleSimpleExpressionConstraint970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleParentOf_in_ruleSimpleExpressionConstraint997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAncestorOf_in_ruleSimpleExpressionConstraint1024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAncestorOrSelfOf_in_ruleSimpleExpressionConstraint1051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleSimpleExpressionConstraint1078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_entryRuleFocusConcept1113 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFocusConcept1123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberOf_in_ruleFocusConcept1170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleFocusConcept1197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_ruleFocusConcept1224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestedExpression_in_ruleFocusConcept1251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleChildOf_in_entryRuleChildOf1286 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleChildOf1296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LT_EM_in_ruleChildOf1332 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleChildOf1352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOf_in_entryRuleDescendantOf1388 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDescendantOf1398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LT_in_ruleDescendantOf1434 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleDescendantOf1454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDescendantOrSelfOf_in_entryRuleDescendantOrSelfOf1490 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDescendantOrSelfOf1500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_LT_in_ruleDescendantOrSelfOf1536 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleDescendantOrSelfOf1556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleParentOf_in_entryRuleParentOf1592 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleParentOf1602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_GT_EM_in_ruleParentOf1638 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleParentOf1658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAncestorOf_in_entryRuleAncestorOf1694 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAncestorOf1704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_GT_in_ruleAncestorOf1740 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleAncestorOf1760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAncestorOrSelfOf_in_entryRuleAncestorOrSelfOf1796 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAncestorOrSelfOf1806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_GT_in_ruleAncestorOrSelfOf1842 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleFocusConcept_in_ruleAncestorOrSelfOf1862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberOf_in_entryRuleMemberOf1898 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMemberOf1908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CARET_in_ruleMemberOf1944 = new BitSet(new long[]{0x0000000002010000L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleMemberOf1966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_ruleMemberOf1985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptReference_in_entryRuleConceptReference2024 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleConceptReference2034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSnomedIdentifier_in_ruleConceptReference2080 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConceptReference2092 = new BitSet(new long[]{0x0000000FF7FF7FF0L});
    public static final BitSet FOLLOW_ruleTerm_in_ruleConceptReference2112 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConceptReference2123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_entryRuleAny2160 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAny2170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WILDCARD_in_ruleAny2206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRefinement_in_entryRuleRefinement2250 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRefinement2260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOrRefinement_in_ruleRefinement2306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOrRefinement_in_entryRuleOrRefinement2340 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOrRefinement2350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAndRefinement_in_ruleOrRefinement2397 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_RULE_OR_in_ruleOrRefinement2417 = new BitSet(new long[]{0x0000000002070600L});
    public static final BitSet FOLLOW_ruleAndRefinement_in_ruleOrRefinement2437 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_ruleAndRefinement_in_entryRuleAndRefinement2475 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAndRefinement2485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeConstraint_in_ruleAndRefinement2532 = new BitSet(new long[]{0x0000000410000002L});
    public static final BitSet FOLLOW_ruleAndOperator_in_ruleAndRefinement2557 = new BitSet(new long[]{0x0000000002070600L});
    public static final BitSet FOLLOW_ruleAttributeConstraint_in_ruleAndRefinement2577 = new BitSet(new long[]{0x0000000410000002L});
    public static final BitSet FOLLOW_ruleAttributeConstraint_in_entryRuleAttributeConstraint2615 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeConstraint2625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCardinality_in_ruleAttributeConstraint2671 = new BitSet(new long[]{0x0000000002070600L});
    public static final BitSet FOLLOW_RULE_REVERSED_in_ruleAttributeConstraint2689 = new BitSet(new long[]{0x0000000002070600L});
    public static final BitSet FOLLOW_ruleAttribute_in_ruleAttributeConstraint2716 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_ruleComparison_in_ruleAttributeConstraint2737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttribute_in_entryRuleAttribute2773 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttribute2783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeDescendantOf_in_ruleAttribute2830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeDescendantOrSelfOf_in_ruleAttribute2857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleAttribute2884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_ruleAttribute2911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeDescendantOf_in_entryRuleAttributeDescendantOf2946 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeDescendantOf2956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LT_in_ruleAttributeDescendantOf2992 = new BitSet(new long[]{0x0000000002010000L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleAttributeDescendantOf3014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_ruleAttributeDescendantOf3033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeDescendantOrSelfOf_in_entryRuleAttributeDescendantOrSelfOf3072 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeDescendantOrSelfOf3082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_LT_in_ruleAttributeDescendantOrSelfOf3118 = new BitSet(new long[]{0x0000000002010000L});
    public static final BitSet FOLLOW_ruleConceptReference_in_ruleAttributeDescendantOrSelfOf3140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAny_in_ruleAttributeDescendantOrSelfOf3159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCardinality_in_entryRuleCardinality3198 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCardinality3208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SQUARE_OPEN_in_ruleCardinality3244 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_ruleNonNegativeInteger_in_ruleCardinality3264 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_RULE_TO_in_ruleCardinality3275 = new BitSet(new long[]{0x0000000006010000L});
    public static final BitSet FOLLOW_ruleMaxValue_in_ruleCardinality3295 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_RULE_SQUARE_CLOSE_in_ruleCardinality3306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleComparison_in_entryRuleComparison3341 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleComparison3351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeValueEquals_in_ruleComparison3398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeValueNotEquals_in_ruleComparison3425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeValueEquals_in_entryRuleAttributeValueEquals3460 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeValueEquals3470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_EQUAL_in_ruleAttributeValueEquals3506 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleSimpleExpressionConstraint_in_ruleAttributeValueEquals3526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeValueNotEquals_in_entryRuleAttributeValueNotEquals3562 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeValueNotEquals3572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_EQUAL_in_ruleAttributeValueNotEquals3608 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleSimpleExpressionConstraint_in_ruleAttributeValueNotEquals3628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNestedExpression_in_entryRuleNestedExpression3664 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNestedExpression3674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_OPEN_in_ruleNestedExpression3710 = new BitSet(new long[]{0x0000000002817F00L});
    public static final BitSet FOLLOW_ruleExpressionConstraint_in_ruleNestedExpression3730 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RULE_ROUND_CLOSE_in_ruleNestedExpression3741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSnomedIdentifier_in_entryRuleSnomedIdentifier3783 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSnomedIdentifier3794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3838 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3859 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3885 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3907 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3933 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier3955 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier3981 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier4003 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier4029 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleSnomedIdentifier4051 = new BitSet(new long[]{0x0000000006000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleSnomedIdentifier4077 = new BitSet(new long[]{0x0000000006000002L});
    public static final BitSet FOLLOW_ruleTerm_in_entryRuleTerm4135 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTerm4146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm4198 = new BitSet(new long[]{0x0000000FFFFF7FF2L});
    public static final BitSet FOLLOW_RULE_WS_in_ruleTerm4222 = new BitSet(new long[]{0x0000000FFFFF7FF0L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm4252 = new BitSet(new long[]{0x0000000FFFFF7FF2L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter4312 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTermCharacter4323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LT_in_ruleTermCharacter4367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_GT_in_ruleTermCharacter4393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_LT_in_ruleTermCharacter4419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DBL_GT_in_ruleTermCharacter4445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LT_EM_in_ruleTermCharacter4471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_GT_EM_in_ruleTermCharacter4497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_AND_in_ruleTermCharacter4523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OR_in_ruleTermCharacter4549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_in_ruleTermCharacter4575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_MINUS_in_ruleTermCharacter4601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleTermCharacter4627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter4653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LETTER_in_ruleTermCharacter4679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CARET_in_ruleTermCharacter4705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_EQUAL_in_ruleTermCharacter4731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_EQUAL_in_ruleTermCharacter4757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_PLUS_in_ruleTermCharacter4783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CURLY_OPEN_in_ruleTermCharacter4809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CURLY_CLOSE_in_ruleTermCharacter4835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_OPEN_in_ruleTermCharacter4861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ROUND_CLOSE_in_ruleTermCharacter4887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SQUARE_OPEN_in_ruleTermCharacter4913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SQUARE_CLOSE_in_ruleTermCharacter4939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_in_ruleTermCharacter4965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COLON_in_ruleTermCharacter4991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleTermCharacter5017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REVERSED_in_ruleTermCharacter5043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_TO_in_ruleTermCharacter5069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WILDCARD_in_ruleTermCharacter5095 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OTHER_CHARACTER_in_ruleTermCharacter5121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNonNegativeInteger_in_entryRuleNonNegativeInteger5177 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNonNegativeInteger5188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleNonNegativeInteger5232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleNonNegativeInteger5259 = new BitSet(new long[]{0x0000000006000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleNonNegativeInteger5280 = new BitSet(new long[]{0x0000000006000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleNonNegativeInteger5306 = new BitSet(new long[]{0x0000000006000002L});
    public static final BitSet FOLLOW_ruleMaxValue_in_entryRuleMaxValue5365 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMaxValue5376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNonNegativeInteger_in_ruleMaxValue5427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WILDCARD_in_ruleMaxValue5453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAndOperator_in_entryRuleAndOperator5509 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAndOperator5520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_AND_in_ruleAndOperator5564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleAndOperator5590 = new BitSet(new long[]{0x0000000000000002L});

}