/* Conjunction and disjunction can also be applied to attribute values. The 
 * example below is satisfied only by members of the adverse drug reactions 
 * reference set for GP/FP health issue, which have a causative agent that is 
 * either a subtype of pharmaceutical / biologic product or a subtype of substance.
 */
 
^450990004|Adverse drug reactions reference set for GP/FP health issue| :
    246075003|Causative agent| = (<373873005|Pharmaceutical / biologic product| OR
                                  <105590001|Substance|)