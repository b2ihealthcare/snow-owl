/* Two consecutive 'less than' signs (i.e. "<<") indicates that the expression
 * constraint is satisfied by all descendants of the specified concept plus the 
 * specified concept itself. The expression constraint below evaluates to the set
 * of descendants of 73211009|Diabetes mellitus|, plus the concept 
 * 73211009|Diabetes mellitus| itself.
 *
 * << is primarily used for attribute values, which refer to a specific clinical 
 * value (e.g. 73211009|Diabetes mellitus|, 73761001|Colonoscopy|, 
 * 385055001|Tablet dose form|), but any specialization of this value is also acceptable.
 */
 
<<73211009|Diabetes mellitus|