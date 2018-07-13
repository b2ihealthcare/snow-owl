# Task Tracking

## Finishing the installation

Create a MySQL user for Bugzilla using the command-line client:

    mysql> GRANT SELECT, INSERT, UPDATE, DELETE, INDEX, ALTER, CREATE, LOCK TABLES,
           CREATE TEMPORARY TABLES, DROP, REFERENCES ON bugs.*
           TO bugs@localhost IDENTIFIED BY 'bugzilla_pwd'; **(1)**
    
    mysql> FLUSH PRIVILEGES;

 1. Replace `bugzilla_pwd` with a generated password

Return to folder  `/var/www/html/bugzilla`  and edit  `localconfig`  to reflect the DB user name and password changes:

/var/www/html/bugzilla/localconfig

    # Enter your database password here. It's normally advisable to specify
    # a password for your bugzilla database user.
    # If you use apostrophe (') or a backslash (\) in your password, you'll
    # need to escape it by preceding it with a '\' character. (\') or (\)
    # (Far simpler just not to use those characters.)
    $db_pass = 'bugzilla_pwd';

Apply the following patch on  `/var/www/html/bugzilla/Bugzilla/DB/Mysql.pm`  to make Bugzilla work with MySQL 5.6:

Mysql.pm.patch

    --- Mysql.pm.old        2015-07-23 22:07:27.797000043 +0200
    +++ Mysql.pm    2015-07-23 22:10:49.373999897 +0200
    @@ -309,8 +309,8 @@
         # works if InnoDB is off. (Particularly if we've already converted the
         # tables to InnoDB.)
         my ($innodb_on) = @{$self->selectcol_arrayref(
    -        q{SHOW VARIABLES LIKE '%have_innodb%'}, {Columns=>[2]})};
    -    if ($innodb_on ne 'YES') {
    +        q{SHOW ENGINES}, {Columns=>[2]})};
    +    if ($innodb_on ne 'YES' && $innodb_on ne 'DEFAULT') {
             print <<EOT;
     InnoDB is disabled in your MySQL installation.
     Bugzilla requires InnoDB to be enabled.

Finally, run `./checksetup.pl` again. Bugzilla should be reachable at [http://localhost/bugzilla](http://localhost/bugzilla) after configuration is completed. Details of the administrator user will be requested at the end of the process:

    ...
    Adding a new user setting called 'per_bug_queries'
    Adding a new user setting called 'zoom_textareas'
    Adding a new user setting called 'csv_colsepchar'
    Adding a new user setting called 'state_addselfcc'
    Adding a new user setting called 'comment_sort_order'
    Adding a new user setting called 'display_quips'
    
    Looks like we don't have an administrator set up yet. Either this is
    your first time using Bugzilla, or your administrator's privileges
    might have accidentally been deleted.
    
    Enter the e-mail address of the administrator: info@b2international.com **(1)**
    Enter the real name of the administrator: Administrator **(2)**
    Enter a password for the administrator account: **(3)**
    Please retype the password to verify:
    info@b2international.com is now set up as an administrator.
    Creating initial dummy product 'TestProduct'...
    
    Now that you have installed Bugzilla, you should visit the 'Parameters'
    page (linked in the footer of the Administrator account) to ensure it
    is set up as you wish - this includes setting the 'urlbase' option to
    the correct URL.

 1. Enter the e-mail address of the administrator user
 2. Add a display name for the Bugzilla administrator
 3. Enter the password of the administrator user

Once Bugzilla has created its table structure, you can increase the maximum table size by executing the following commands:

    mysql> USE bugzilla
    mysql> ALTER TABLE attachments
           AVG_ROW_LENGTH=1000000, MAX_ROWS=20000;

### Administration of Bugzilla

See  [http://www.bugzilla.org/docs/3.6/en/html/administration.html](http://www.bugzilla.org/docs/3.6/en/html/administration.html)  for a comprehensive list of administrative tasks and options.

After logging in with an account that has administrative privileges, click the `Administration` link on the top. The general administrative page will appear as shown below:

//Missing link/image- ( [![bugzilla administration](https://github.com/b2ihealthcare/snow-owl/raw/7.x/documentation/src/main/asciidoc/bugzilla_administration.png)](https://github.com/b2ihealthcare/snow-owl/blob/7.x/documentation/src/main/asciidoc/bugzilla_administration.png) ) 
Core parameters can be set by selecting `Parameters` on the top left. The following fields are recommended to be adjusted:

#### Required Settings

***urlbase***
* Set to the the common leading part of all URLs which are related to Bugzilla (ex.: [http://server.domain/bugzilla/](http://server.domain/bugzilla/))

***cookiepath***
* The common path segment of the URL under which Bugzilla cookies are allowed to be read; as noted in the description above the field, its value should begin with '/' (ex.: /bugzilla/)

#### General

***maintainer***
* The email address entered here is shown on various pages in Bugzilla where contacting the administrator is suggested

#### User Authentication

***requirelogin***
* Set to On if you want to limit access to registered users only (disabling anonymous browsing of bugs)

***emailregexp and emailregexpdesc***
* Depending on requirements, the administrator may limit login names to values that are not actual email addresses. In this case, set the fields as suggested in the description above, ie. `^[^@]+` and `Local usernames, no @ allowed.`

***createemailregexp***
* to disable user-initiated registration (requiring the administrator to create each user account by hand), clear the field’s contents`

Missing link/image- ( [![bugzilla emailregexp](https://github.com/b2ihealthcare/snow-owl/raw/7.x/documentation/src/main/asciidoc/bugzilla_emailregexp.png)](https://github.com/b2ihealthcare/snow-owl/blob/7.x/documentation/src/main/asciidoc/bugzilla_emailregexp.png) ) 

#### Attachments

***maxattachmentsize***
* The maximum size in kilobytes for attachments. Change it to `10240` (10 MB)

#### Dependency Graphs

***webdotbase***
* To disable relying on an external service for rendering dependency graphs of issues (as populated by default), clear the field’s contents

#### Email

***mail_delivery_method***
* If an SMTP server is available, configure its address and authentication properties below; otherwise, set this value to `None` to disable sending mail altogether

***smtpserver***
* Clear the field’s contents if no SMTP server is used

***whinedays***
* Set to 0 if mail delivery is not enabled and/or there’s no need to send users regular notifications about their assigned bugs which remained in NEW state

***use-mailer-queue***
* When set to `On`, e-mails are sent asynchronously; to use this feature, the  *jobqueue*.*pl* daemon needs to be started. For more information on this topic, please see [http://www.bugzilla.org/docs/3.6/en/html/api/jobqueue.html](http://www.bugzilla.org/docs/3.6/en/html/api/jobqueue.html).

### Product setup

Bugzilla tracks the authoring aspects of Snow Owl clients in multiple products. Per-product configuration is shown in the following parts of the guide.

Opening the preference page  `Snow Owl > Bugzilla Products`  displays the supported products in the client and their corresponding product names in Bugzilla. If you have different product names added in the issue tracker, you have to adjust the product name as shown in the image. Make sure to press Enter or click in the table to apply the change in the field before hitting  `Apply`  or  `OK`  to apply the changes. Products which are not handled by contributed task editors are displayed with an empty context view only:



Missing link/image - ( [![bugzilla products](https://github.com/b2ihealthcare/snow-owl/raw/7.x/documentation/src/main/asciidoc/bugzilla_products.png)](https://github.com/b2ihealthcare/snow-owl/blob/7.x/documentation/src/main/asciidoc/bugzilla_products.png) )
To match the default value set in client preferences, create a product called `Snow Owl Collaborative Editing` by clicking `Products` on Bugzilla’s administration page. Add a description, optionally set a version to discern individual releases, and keep `Open for bug entry` checked to allow users to file issues under this product. After creating the product, a warning will be issued by Bugzilla to create a component as well. Add the following components with the `Component`, `Component description` and `Default assignee` fields populated:

| Component name | Description |
|--|--|
| Single author with single reviewer | Single author with single reviewer |
| Dual authors with single reviewer – Dual authoring | Dual authors with single reviewer – Dual authoring |
| Dual authors with single reviewer – Dual blind authoring | Dual authors with single reviewer – Dual blind authoring |
| Dual authors with dual reviewers – Dual authoring | Dual authors with dual reviewers – Dual authoring |
| Dual authors with dual reviewers – Dual blind authoring | Dual authors with dual reviewers – Dual blind authoring |

Missing link/image - ([![bugzilla add product](https://github.com/b2ihealthcare/snow-owl/raw/7.x/documentation/src/main/asciidoc/bugzilla_add_product.png)](https://github.com/b2ihealthcare/snow-owl/blob/7.x/documentation/src/main/asciidoc/bugzilla_add_product.png))
Missing link/image - ([![bugzilla add component](https://github.com/b2ihealthcare/snow-owl/raw/7.x/documentation/src/main/asciidoc/bugzilla_add_component.png)](https://github.com/b2ihealthcare/snow-owl/blob/7.x/documentation/src/main/asciidoc/bugzilla_add_component.png))

Add custom fields through the web interface (`Administration > Custom fields`):

| Field name | Description | Sortkey | Type | Editable on Bug Creation | In Bugmail on Bug Creation |
|--|--|--|--|--|--|
| cf_artifacttype | Task artifact type | 400 | Free Text | true | false |
| cf_author_one | Author one | 410 | Free Text | true | true |
| cf_author_two | Author two | 420 | Free Text | true | true |
| cf_reviewer_one | Reviewer one | 430 | Free Text | true | true |
| cf_reviewer_two | Reviewer two | 440 | Free Text | true | true |
| cf_adjudicator | Adjudicator | 450 | Free Text | true | true |
| cf_artifact_properties_source | Properties source | 991 | Free Text | true | false |
| cf_mappingset_id | Mapping set ID | 992 | Free Text | true | false |
| cf_valueset_id | Value domain ID | 993 | Free Text | true | false |
| cf_is_promoted | Promoted | 995 | Free Text | true | false |
| cf_parent_refset_map_target_component_type | Reference set map target component type | 996 | Free Text |  true | false  |
| cf_parent_refset_referenced_component_type | Reference set referenced component type | 997 | Free Text |   true | false  |
| cf_parent_refset_identifierconcept_id | Parent reference set identifier concept id | 998 | Free Text | true | false |
| cf_refset_identifierconcept_id | Reference set identifier concept | 999 | Free Text | true | false |

### Authentication against LDAP

Bugzilla is capable to authenticate against the external LDAP server which Snow Owl Server will use. Setting it up requires the following steps to be taken:

* Go to `Administration > Parameters > LDAP` and populate the following fields:

	***LDAPserver***
	*  hostname:port pair for contacting the server, eg. `localhost:10389`

	***LDAPBaseDN***
	* set to `dc=snowowl,dc=b2international,dc=com`

	***LDAPuidattribute***
	* set to  `uid`

	***LDAPmailattribute***
	* set to  `uid`

* Click `Save Changes` to apply changes
* Go to `Administration > Parameters > User Authentication`, scroll down to `user_verify_class` and make `LDAP` the top-most item
* Click `Save Changes` to finish

To test, click the `Log Out` link at the top and try to log in with your bugzilla username and LDAP password. If it was sucessful, you should see bugzilla’s landing page. If you see an error message about not able to connect to the LDAP server, then run the following command as root:

    # setsebool -P httpd_can_network_connect on

This will allow Apache to make network connections.

If LDAP is still not working and you are being locked out from bugzilla, you can change back bugzilla to use its internal database for authentication, instead of LDAP. To do so, edit  `/var/www/html/bugzilla/data/params`, deleting LDAP from the  `user_verify_class`  entry:

/var/www/html/bugzilla/data/params

    ...
    'user_verify_class' => 'DB',
    ...

If users are already entered in the LDAP server, it is important to synchronize Bugzilla’s user database to contents of LDAP so tasks can be assigned to all users. Run the following script to perform synchronization:

    # cd /var/www/html/bugzilla
    # ./contrib/syncLDAP.pl

For general questions and documentation, please refer to chapter 3.1.10. LDAP Authentication in the documentation: [http://www.bugzilla.org/docs/3.6/en/html/parameters.html](http://www.bugzilla.org/docs/3.6/en/html/parameters.html).

### Backing up and restoring data in the issue tracker

A detailed list of steps are available at the Move Installation page of Mozilla Wiki (which describes moving the installation from one machine to another, but can also be applied for backup and restore on the same server). The important parts to take note of are the commands used for dumping the SQL database:

    $ mysqldump -u(username) -p(password) bugs > bugzilla-backup.sql

Reloading the SQL dump later requires the database to be cleared and recreated from the MySQL console:

    mysql> DROP DATABASE bugs;
    mysql> CREATE DATABASE bugs DEFAULT CHARSET utf8;

Applying the dump goes as follows:

    $ mysql -u (username) -p(password) bugs < /path/to/bugzilla-backup.sql

In addition to the contents of the database, the `data` directory and the `localconfig` file from Bugzilla’s installation directory should also be preserved.

