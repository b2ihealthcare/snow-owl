/*******************************************************************************
 * Copyright (c) 2021 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.identity.ldap;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.console.Command;
import com.b2international.snowowl.core.console.CommandLineStream;
import com.b2international.snowowl.core.identity.IdentityProvider;
import com.b2international.snowowl.core.identity.MultiIdentityProvider;
import com.b2international.snowowl.core.plugin.Component;
import com.google.common.base.Strings;

import picocli.CommandLine;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

/**
 * @since 7.15.1
 */
@Component
@CommandLine.Command(
		name = "ldap",
		header = "Displays information about users and roles provided via LDAP",
		description = "",
		subcommands = {
				HelpCommand.class,
				LdapCommand.UserCommand.class,
				LdapCommand.UsersCommand.class,
				LdapCommand.RolesCommand.class
		}
)
public class LdapCommand extends Command {

	@Override
	public void run(final CommandLineStream out) {
		CommandLine.usage(this,  (PrintStream) out);
	}

	@CommandLine.Command(
		name = "users",
		header = "Lists Snow Owl users provided via the configured LDAP server",
		description = ""
	)
	public static class UsersCommand extends Command {

		@Option(names = { "-l", "--limit" }, description = "The maximum number of users to fetch", defaultValue = "100", required = false)
		int limit = 100;

		@Option(names = { "-e", "--extended" }, description = "Show user roles and permissions", defaultValue = "false", required = false)
		boolean extended = false;

		@Override
		public void run(final CommandLineStream out) {

			final IdentityProvider provider = getIdentityProvider();

			if (provider == IdentityProvider.NOOP) {
				out.println("LDAP based authentication is not configured for Snow Owl");
				return;
			}

			provider.searchUsers(List.of(), limit)
				.getSync(5, TimeUnit.MINUTES)
				.forEach(user -> {

					out.println(user.getUsername());

					if (extended) {

						out.println();

						out.println("\tRoles:");
						user.getRoles().forEach(role -> {
							out.println("\t\t" + role.getName());
						});

						out.println();

						out.println("\tPermissions:");
						user.getPermissions().forEach(permission -> {
							out.println("\t\t" + permission.getOperation() + " - " + permission.getResource());
						});

						out.println();

					}

				});

		}

	}

	@CommandLine.Command(
		name = "roles",
		header = "Lists Snow Owl roles provided via the configured LDAP server",
		description = ""
	)
	public static class RolesCommand extends Command {

		@Override
		public void run(final CommandLineStream out) {

			final IdentityProvider provider = getIdentityProvider();

			if (provider == IdentityProvider.NOOP) {
				out.println("LDAP based authentication is not configured for Snow Owl");
				return;
			}

			final LdapIdentityProvider ldapProvider = (LdapIdentityProvider) provider;

			try {

				final InitialLdapContext context = ldapProvider.createLdapContext();

				ldapProvider.getAllLdapRoles(context).forEach(role -> {

					out.println("Role: " + role.getName());

					out.println("\tPermissions:");
					role.getPermissions().forEach(permission -> {
						out.println("\t\t" + permission.getOperation() + " - " + permission.getResource());
					});

					out.println("\tMembers:");
					role.getUniqueMembers().forEach(member -> {
						out.println("\t\t" + member);
					});

				});


				context.close();

			} catch (final NamingException e) {
				out.println("Failed to initialize LDAP context: " + e.getMessage());
			}

		}

	}

	@CommandLine.Command(
		name = "user",
		header = "Retrieves user DN for the specified input",
		description = ""
	)
	public static class UserCommand extends Command {

		@Option(names = { "-n", "--name" }, description = "The user name to fetch", required = true)
		String username;

		@Override
		public void run(final CommandLineStream out) {

			final IdentityProvider provider = getIdentityProvider();

			if (provider == IdentityProvider.NOOP) {
				out.println("LDAP based authentication is not configured for Snow Owl");
				return;
			}

			final LdapIdentityProvider ldapProvider = (LdapIdentityProvider) provider;

			try {

				final InitialLdapContext context = ldapProvider.createLdapContext();

				final String userDN = ldapProvider.findUserDN(context, username);

				if (Strings.isNullOrEmpty(userDN)) {
					out.println("User does not exist");
				} else {
					out.println("User DN: " + userDN);
				}

				context.close();

			} catch (final NamingException e) {
				out.println("Failed to initialize LDAP context: " + e.getMessage());
			}

		}

	}

	private static IdentityProvider getIdentityProvider() {

		final IdentityProvider provider = ApplicationContext.getServiceForClass(IdentityProvider.class);

		if (provider instanceof LdapIdentityProvider) {
			return provider;
		} else if (provider instanceof MultiIdentityProvider) {

			final Optional<IdentityProvider> ldapProvider = ((MultiIdentityProvider) provider).getProviders().stream()
					.filter(LdapIdentityProvider.class::isInstance).findFirst();

			if (ldapProvider.isPresent()) {
				return ldapProvider.get();
			}

		}

		return IdentityProvider.NOOP;

	}

}
