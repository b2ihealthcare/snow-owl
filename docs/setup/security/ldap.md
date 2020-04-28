# Configure an LDAP realm

You can configure security to communicate with a Lightweight Directory Access Protocol (LDAP) server to authenticate and authorize users. 

To integrate with LDAP, you configure an `ldap` realm in the `snowowl.yml` configuration file.

```yml
identity:
  providers:
    - ldap:
        uri: <ldap_uri>
        baseDn: dc=snowowl,dc=b2international,dc=com
        rootDn: cn=admin,dc=snowowl,dc=b2international,dc=com
        rootDnPassword: <adminpwd>
        userObjectClass: inetOrgPerson
        roleObjectClass: groupOfUniqueNames
        userIdProperty: uid
        permissionProperty: description
        memberProperty: uniqueMember
        usePool: false
```

## Configuration

The following configuration settings are supported:

| Configuration |      Description      |
|---------------|-----------------------|
| uri |  The LDAP URI that points to the LDAP/AD server to connect to |
| baseDn | The base directory where all entries in the entire subtree will be considered as potential matches for all searches |
| rootDn | The user's DN who has access to the entire `baseDn` and read content from it |
| rootDnPassword | The password of the `rootDn` user |
| userObjectClass | The user object's class to look for when searching for user entries. Defaults to `inetOrgPerson` class. |
| roleObjectClass | The role object's class to look for when searching for role entries. Defaults to `groupOfUniqueNames` class. |
| userIdProperty | The userId property to access and read for the user's unique identifier. Usually their username or email address. Defaults to `uid` property. |
| permissionProperty | A multi-valued property that is used to store permission information on a role. Defaults to the `description` property.  |
| memberProperty | A multi-valued property that is used to store and retrieve user `dn`s that belong to a given role. Defaults to the `uniqueMember` property. |

The default configuration values are selected to support both OpenLDAP and Active Directory without needing to customize the default schema that comes with their default installation.

## Configure Authentication

When users send their username and password with their request in the Authorization header, the LDAP security realm uses the provided username and password to first search for the user in the configured LDAP instance to get the user's `DN` and then user the received `DN` and the provided password to authenticate with the LDAP instance. 
If either the search for the user or the authentication fail for any reason, the user is not allowed to access the terminology server's content and the server will respond with an `HTTP 401 Unauthorized` response.

To configure authentication, you need to configure the `uri`, `baseDn`, `rootDn`, `rootDnPassword`, `userObjectClass` and `userIdProperty` configuration settings.

### Adding a user

To add a user in the LDAP realm, create an entry under the specified `baseDn` using the configured `userObjectClass` as class and the `userIdProperty` as the property where the user's username/e-mail address is configured.

Example user entry:

```
dn: cn=John Doe+sn=Doe+uid=johndoe@b2international.com,dc=snowowl,dc=b2international,dc=com
objectClass: inetOrgPerson
objectClass: organizationalPerson
objectClass: person
objectClass: top
cn: John Doe
sn: Doe
uid: johndoe@b2international.com
userPassword: <encrypted_password> 
```

## Configure Authorization

On top of the authentication part, the LDAP realm provides configuration values to support full role-based access control and authorization.

When a user's request is successfully authenticated with the LDAP realm, Snow Owl authorizes the request using the user's currently set roles and permissions in the configured LDAP instance.

### Adding a role

To add a role in the LDAP realm, create an entry under the specified `baseDn` using the configured `roleObjectClass` as class and the configured `permissionProperty` and `memberProperty` properties for permission and user mappings, respectively.

Example read-only role:

```
dn: cn=Browser,dc=snowowl,dc=b2international,dc=com
objectClass: top
objectClass: groupOfUniqueNames
cn: Browser
description: browse:*
description: export:*
uniqueMember: cn=John Doe+sn=Doe+uid=johndoe@b2international.com,dc=snowowl,dc=b2international,dc=com 
```

  