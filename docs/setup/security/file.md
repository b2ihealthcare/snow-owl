# Configure a file realm

You can manage and authenticate users with the built-in file realm. All the data about the users for the file realm is stored in the `users` file. The file is located in `SO_PATH_CONF` and is read on startup.

You need to explicitly select the file realm in the `snowowl.yml` configuration file in order to use it for authentication.

```yml
identity:
  providers:
    - file:
        name: users
```

In the above configuration the file realm is using the `users` file to read your users from. 
Each row in the file represents a username and password delimited by `:` character. 
The passwords are BCrypt encrypted hashes. 
The default `users` file comes with a default `snowowl` user with the default `snowowl` password.

## Users Command

To simplify file realm configuration, the Snow Owl CLI comes with a command to add a user to the file realm (`snowowl users add`). See the command help manual (`-h` option) for further details.

## Authorization

The file security realm does NOT support the Authorization formats at the moment. If you are interested in configuring role-based access control for your users, it is recommended to switch to the [LDAP security realm](./ldap.md). 
 