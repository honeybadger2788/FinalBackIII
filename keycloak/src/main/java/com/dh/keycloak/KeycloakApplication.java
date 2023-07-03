package com.dh.keycloak;

import com.dh.keycloak.service.KeycloakClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class KeycloakApplication implements CommandLineRunner {

	private static final String REALM = "ecommerce-realm";
	private static final String USERS_CLIENT = "ms-users-client";
	private static final String BILLS_CLIENT = "ms-bills-client";
	private static final String CLIENT_SECRET = "jfob1gVlftm23Yk2Pvd5D4CysiYMWltH";
	private static final String USER_ROLE = "user";
	private static final String ADMIN_ROLE = "admin";
	List<String> roles = List.of(ADMIN_ROLE, USER_ROLE);
	private static final String GROUP = "PROVIDERS";
	private static final String SCOPE = "roles";

	@Autowired
	private KeycloakClientService keycloakClientService;

	public static void main(String[] args) {
		SpringApplication.run(KeycloakApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// Se crea el reino
		keycloakClientService.createRealm(REALM);
		// Creamos Users Client
		keycloakClientService.createClient(REALM,
				USERS_CLIENT,
				CLIENT_SECRET,
				roles);
		// Creamos Bills Client
		keycloakClientService.createClient(REALM,
				BILLS_CLIENT,
				CLIENT_SECRET,
				roles);
		// Creamos Gateway Client
		keycloakClientService.createGatewayClient(REALM,
				CLIENT_SECRET,
				roles);
		// Creamos grupo PROVIDERS
		keycloakClientService.createGroup(REALM, GROUP);
		// Asignamos grupo al token
		keycloakClientService.addGroupsToToken(REALM, SCOPE);

		keycloakClientService.addUsers(REALM, GROUP);

		// Paramos aplicacion luego de crear lo necesario
		System.exit(0);
	}

}
