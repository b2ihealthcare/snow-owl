/* Exclusion is supported in the SNOMED CT Expression Constraint Language by 
 * the binary operator 'MINUS'. When used within a simple expression, exclusion
 * works in a similar manner to mathematical subtraction. For example, the
 * following expression constraint returns the set of lung disorders which are
 * not a descendant or self of edema of the trunk.
 */
 
<<19829001|Disorder of lung| MINUS <<301867009|Edema of trunk|

/* Logically, this expression constraint takes the set of descendants of 
 * |disorder of lung| and subtracts the set of descendants of |edema of trunk|.
 * Please note that the keyword 'MINUS' is case insensitive.
 */
