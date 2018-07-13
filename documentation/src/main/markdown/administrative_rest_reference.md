# Administrative REST API reference

## Introduction

This document provides a list of available REST endpoints, which can be used for taking scripted backups of a Snow Owl Server instance.

## REST API

The following URLs are exposed to assist the administrator in creating backups without stopping the server (accepted and produced content type is  `text/plain`  in all cases except errors, where an HTML message may be returned):

### Write access control via repository locks

* `GET /snowowl/admin/repositories` — Display list of available terminology repositories
	* Parameters:
    
	    * none
        
    
	* Returns: the list of registered repositories by identifier (one per line, separated by LF)
    
	* Responses:
    
	    * `200 (OK)`
	    
* `POST /snowowl/admin/repositories/lock` — Lock all terminology repositories for exclusive access

	* Parameters:
    
	    * `timeoutMilis`  - lock timeout in milliseconds (optional; default is 5 seconds)
        
    
	* Returns: empty response body
    
	* Responses:
    
	    * `204 (No Content)`  - if the operation succeeded
        
	    * `400 (Bad Request)`  - if the format or number of expected arguments is incorrect
        
	    * `409 (Conflict)`  - if another participant is already holding a lock (eg. a long-running operation has exclusive access)

* `POST /snowowl/admin/repositories/unlock` — Release previously acquired lock on all terminology repositories

	* Parameters:
	    
	    *  none
	 
	* Returns: empty response body
    
	 * Responses:
    
	    *  `204 (No Content)`  - if the lock has been successfully released
	        
	    * `400 (Bad Request)`  - if releasing the lock fails for some reason (eg. when no lock was held by the system on the specified repository)

* `POST /snowowl/admin/repositories/{id}/lock` — Lock specific repository for exclusive access

	* Parameters:
	    
	    * `id`  (path parameter) - the repository identifier (as returned by the call to the first URL)
	        
	    * `timeoutMilis`  - lock timeout in milliseconds (optional; default is 5 seconds)
        
	* Returns: empty response body
    
	* Responses:
    
	    * `204 (No Content)`  - if the operation succeeded
	        
	    * `409 (Conflict)`  - if another participant is already holding a lock (eg. a long-running operation has exclusive access)

* `POST /snowowl/admin/repositories/{id}/unlock` — Release previously acquired lock on specific repository

	* Parameters:
    
	    * `id`  (path parameter) - the repository identifier

	* Returns: empty response body
    
	* Responses:
	    
	    * `204 (No Content)`  - if the lock has been successfully released
	        
	    * `400 (Bad Request)`  - if the format or number of expected arguments is incorrect, or if releasing the lock fails for some reason (eg. when no lock was held by the system on the specified repository)

### User notification

* `POST /snowowl/admin/messages/send` — Send a message to connected users
    
    * Parameters:
        
        * `message`  - the message to send
            
        
    * Returns: empty response body
        
    * Responses:
        
       * `400 (Bad Request)`  - if the format or number of expected arguments is incorrect

### Versioning and terminology indexes

* `GET /snowowl/admin/repositories/{id}/versions` — Display list of registered versions for the specified repository

	* Parameters:
	    
	   * `id`  (path parameter) - the repository identifier
        
	* Returns: the list of versions for the specified repository (one per line, separated by LF; always including  `MAIN`  as a placeholder for the mainline of the repository, from which a version may be created later)
    
	* Responses:
	    
	    * `200 (OK)`  - if the operation succeeded
	        
	    * `404 (Not Found)`  - if a repository with the given identifier could not be found
            
        
   