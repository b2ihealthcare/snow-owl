# Configure an LDAP realm

You can configure security to communicate with a Lightweight Directory Access Protocol (LDAP) server to authenticate users. To integrate with LDAP, you configure an `ldap` realm in the `snowowl.yml` configuration file.

```yml
identity:
  providers:
    - ldap:
        uri: <ldap_uri>
        baseDn: dc=snowowl,dc=b2international,dc=com
        rootDn: cn=admin,dc=snowowl,dc=b2international,dc=com
        rootDnPassword: <adminpwd>
        userIdProperty: uid
        usePool: false
```

At a minimum, you must set the realm type to `ldap`, specify the `url` of the LDAP server and set the `rootDnPassword` in the `snowowl.yml` configuration file. 
Your users should be available under the specified `baseDn` entry, and also there should be an `cn=admin` entry to allow access for Snow Owl to read user data.
By default Snow Owl expects that the username of a user is present in the `uid` property. You can change this in the `userIdProperty` setting.