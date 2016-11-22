/* In some cases, an attribute concept has subtypes in the SNOMED CT hierarchy. 
 * Where this occurs, it is possible to indicate that an attribute condition may 
 * be satisfied by matching one of the subtypes of the given attribute. This is 
 * done using the < or << operator directly before the attribute name concept. 
 * For example, the expression constraint below will not only match clinical 
 * findings that are associated with edema, but also those that are due to, after
 * or caused by an edema. This result occurs because the 47429007|Associated with|
 * attribute concept has three subtypes: 255234002|After|, 246075003|Causative agent|,
 * and 42752001|Due to|.
 */
 
<<404684003|Clinical finding|:
	<<47429007|Associated with| = <<267038008|Edema|