# Configure a file realm

You can manage and authenticate users with the built-in file internal realm. All the data about the users for the file realm is stored in the `users` file. The file is located in `SO_PATH_CONF` and are read on startup.

You need to explicitly select the file realm in the `snowowl.yml` configuration file in order to use it for authentication.

```yml
identity:
  providers:
    - file:
        name: users
```

In the above configuration the file realm is using the `users` file to read your users from. Each row in the file represents a username and password delimited by `:` character. The passwords are BCrypt encrypted hashes. The default `users` file comes with a default `snowowl` user with the default `snowowl` password.