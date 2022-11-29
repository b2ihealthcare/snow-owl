# Configure an LDAP realm

You can configure security to communicate with a Lightweight Directory Access Protocol (LDAP) server to authenticate and authorize users. 

To integrate with LDAP, you configure an `ldap` realm in the `snowowl.yml` configuration file.

```yml
identity:
  providers:
    - ldap:
        uri: <ldap_uri>
        bindDn: cn=admin,dc=snowowl,dc=b2international,dc=com
        bindDnPassword: <adminpwd>
        baseDn: dc=snowowl,dc=b2international,dc=com
        roleBaseDn: {baseDn}
        userFilter: (objectClass={userObjectClass})
        roleFilter: (objectClass={roleObjectClass})
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
| uri |  The LDAP URI that points to the LDAP/AD server to connect to. |
| bindDn | The user's DN who has access to the entire `baseDn` and `roleBaseDn` and can read content from it. |
| bindDnPassword | The password of the `bindDn` user. |
| baseDn | The base directory where all entries in the entire subtree will be considered as potential matches for all searches. |
| roleBaseDn | Alternative base directory where all role entries in the entire subtree will be considered. Defaults to the `baseDn` value. |
| userFilter | The search filter to search for user entries under the configured `baseDn`. Defaults to `(objectClass={userObjectClass})`. |
| roleFilter | The search filter to search for role entries under the configured `roleBaseDn`. Defaults to `(objectClass={roleObjectClass})`. |
| userObjectClass | The user object's class to look for when searching for user entries. Defaults to `inetOrgPerson` class. |
| roleObjectClass | The role object's class to look for when searching for role entries. Defaults to `groupOfUniqueNames` class. |
| userIdProperty | The userId property to access and read for the user's unique identifier. Usually their username or email address. Defaults to `uid` property. |
| permissionProperty | A multi-valued property that is used to store permission information on a role. Defaults to the `description` property.  |
| memberProperty | A multi-valued property that is used to store and retrieve user `dn`s that belong to a given role. Defaults to the `uniqueMember` property. |

The default configuration values are selected to support both OpenLDAP and Active Directory without needing to customize the default schema that comes with their default installation.

## Configure Authentication

When users send their username and password with their request in the Authorization header, the LDAP security realm performs the following steps to authenticate the user:
1. Searches for a user entry in the configured `baseDn` to get the `DN`
2. Authenticates with the LDAP instance using the received `DN` and the provided password
 
If any of the above-mentioned steps fails for any reason, the user is not allowed to access the terminology server's content and the server will respond with `HTTP 401 Unauthorized`.

To configure authentication, you need to configure the `uri`, `baseDn`, `bindDn`, `bindDnPassword`, `userObjectClass` and `userIdProperty` configuration settings.

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

  
