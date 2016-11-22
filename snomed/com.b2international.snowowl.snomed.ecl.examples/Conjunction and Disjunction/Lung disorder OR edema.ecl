/* The next expression constraint is satisfied only by clinical findings which 
 * are either a disorder of the lung or an edema of the trunk. This gives the 
 * same result as a mathematical union of the set of |Disorder of lung| 
 * descendants and the set of |Edema of trunk| descendants. For this reason, 
 * an OR operator will usually allow more valid clinical meanings than an AND 
 * operator.
 */
 
<19829001|Disorder of lung| OR <301867009|Edema of trunk|