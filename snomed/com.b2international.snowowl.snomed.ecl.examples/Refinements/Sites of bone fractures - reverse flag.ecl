/* In most cases, an attribute refinement is satisfied by those concepts, which 
 * are the source concept of a defining relationship whose destination concept 
 * matches the attribute value. In some cases, however, it may be necessary to 
 * select the destination concept of a relationship and constrain the source 
 * concept to a given attribute value. To achieve this, an expression constraint 
 * indicates that an attribute is to be constrained in the reverse order using a 
 * 'reverse flag'. In the brief syntax, the reverse flag is represented by preceding
 * the name of the attribute with a capital letter 'R'.
 *
 * For example, the expression constraint below finds the set of anatomical 
 * structures, which are the finding site of a type of bone fracture (e.g. 
 * 85050009|Humerus|, 71341001|Femur|).
 */
 
<91723000|Anatomical structure|: 
	R 363698007|Finding site| = <125605004 |Fracture of bone|
