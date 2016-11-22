/* Expression constraints can be built up from smaller parts using conjunction
 * (i.e. AND) and disjunction (i.e. OR). The simplest example of this is where 
 * the conjunction or disjunction is used between two simple expressions. For 
 * example, the following expression constraint is satisfied only by clinical 
 * findings which are both a disorder of the lung and an edema of the trunk. 
 * This gives the same result as a mathematical intersection between the set 
 * of |Disorder of lung| descendants and the set of |Edema of trunk| descendants.
 */
 
<19829001|Disorder of lung| AND <301867009|Edema of trunk|