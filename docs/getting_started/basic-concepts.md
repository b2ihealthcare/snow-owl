# Basic Concepts

There are a few concepts that are core to Snow Owl. Understanding these concepts from the outset will tremendously help ease the learning process.

## Terminology / Code System

A terminology (also known as code system, classification and/or ontology) defines and encapsulates a set of terminology components (eg. set of codes with their meanings) and versions. A terminology is identified by a unique name and stored in a repository. Multiple code systems can exist in a single repository besides each other as long as their name is unique.

## Terminology Component

A terminology component is a basic element in a code system with actual clinical meaning or use. For example in SNOMED CT, the Concept, Description, Relationship and Reference Set Member are terminology components.

## Version

A version that refers to an important snapshot in time, consistent across many terminology components, also known as tag or label. It is often created when the state of the terminology is deemed to be ready to be published and distributed to downstream customers or for internal use. A version is identified by its version ID (or version tag) within a given code system.

## Repository

A repository manages changes to a set of data over time in the form of revisions. Conceptually very similar to a source code repository (like a Git repository), but information stored in the repository must conform to a predefined schema (eg. SNOMED CT Concepts RF2 schema) as opposed to storing it in pure binary or textual format. This way a repository can support various full-text search functionalities, semantical queries and evaluations on the stored, revision-controlled terminology data. 

A repository is identified by a name and this name is used to refer to the repository when performing create, read, update, delete and other operations against the revisions in it. Repositories organize revisions into branches and commits.

## Revision

A revision is the basic unit of information stored in a repository about a terminology component or artifact. It contains two types of information:

* one is the actual data that you care about, for example a single code from a code system with its meaining and properties.
* the other is revision control information (aka revision metadata). Each revision is identified by a random Universally Unique IDentifier (UUID) that is assigned when performing a commit in the repository. Also, during a commit each revision is associated with a branch and timestamp. Revisions can be compared, restored, and merged.

## Branch

A set of components under version control may be branched or forked at a point in time so that, from that time forward, two copies of those components may develop at different speeds or in different ways independently of each other. At later point in time the changes made on one of these branches can be merged into the other. 

Branches are organized into hierarchies like directories in file systems. A child branch has access to all of the information that is stored on its parent branch up until its baseTimestamp, which is the time the branch was created. Each repository has a predefined root branch, called `MAIN`.

## Commit

A commit represents a set of changes made against a branch in a repository. After a successful commit, the changes made by the commit are immediately available and searchable on the given branch.

## Merge / Rebase

A merge/rebase is an operation in which two sets of changes are applied to set of components. A merge/rebase always happens between two branches, denoting one as the source and the other as the target of the operation. 
