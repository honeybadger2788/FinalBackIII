package com.dh.keycloak.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class KeycloakClientService {
    private static final String APP_USER_ROLE = "app_user";
    private static final String APP_ADMIN_ROLE = "app_admin";
    private static final String GTW_CLIENT = "api-gateway-client";
    private static final String GTW_CLIENT_URL = "http://localhost:9090";

    private final Keycloak keycloak;

    public KeycloakClientService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void createRealm(String realm){
        Optional<RealmRepresentation> representationOptional = keycloak.realms().findAll().stream().filter(r -> r.getRealm().equals(realm)).findAny();
        // Elimina el reino si existe
        if (representationOptional.isPresent()) {
            System.out.println("Reino eliminado: " + realm);
            keycloak.realm(realm).remove();
        }
        // Creamos el reino
        RealmsResource realmsResource = keycloak.realms();
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(realm);
        realmRepresentation.setEnabled(true);
        keycloak.realms().create(realmRepresentation);
        System.out.println("Reino creado exitosamente: " + realm);

        // Seteamos roles a nivel Reino
        RolesResource rolesResource = realmsResource.realm(realm).roles();
        RoleRepresentation rolAdmin = new RoleRepresentation();
        rolAdmin.setName(APP_ADMIN_ROLE);
        RoleRepresentation rolUser = new RoleRepresentation();
        rolUser.setName(APP_USER_ROLE);
        List<RoleRepresentation> realmRoles = List.of(rolAdmin, rolUser);
        for (RoleRepresentation rol : realmRoles) {
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            roleRepresentation.setName(rol.getName());
            rolesResource.create(roleRepresentation);
        }
    }

    public void createClient(String realm, String clientId, String clientSecret, List<String> roles) {
        // Verificamos que se haya creado el reino y creamos los clientes
        RealmsResource realmsResource =  keycloak.realms();
        RealmRepresentation realmRepresentation = realmsResource.realm(realm).toRepresentation();
        String realmName = realmRepresentation.getRealm();
        if(Objects.equals(realmName, realm)) {
            RealmResource realmResource = keycloak.realm(realm);
            ClientsResource clientsResource = realmResource.clients();

            // Creamos cliente
            ClientRepresentation clientRepresentation = new ClientRepresentation();
            clientRepresentation.setClientId(clientId);
            clientRepresentation.setSecret(clientSecret);
            clientRepresentation.setServiceAccountsEnabled(true);
            clientRepresentation.setDirectAccessGrantsEnabled(true);
            clientRepresentation.setEnabled(true);

            // Verificamos que se hayan creado los clientes y creamos los roles a nivel Cliente
            Response response = clientsResource.create(clientRepresentation);
            if (response.getStatus() == 201) {
                // Obtiene el ultimo segmento de la url, en este caso seria el clientId
                String createdClientId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                System.out.println("ClientId: " + createdClientId + ". Roles: " + roles);

                // Obtenemos id de clientes
                ClientResource clientResource = clientsResource.get(createdClientId);
                // Seteamos roles a nivel Cliente
                for (String rol : roles) {
                    RoleRepresentation roleRepresentation = new RoleRepresentation();
                    roleRepresentation.setName(rol);
                    roleRepresentation.setClientRole(true);
                    roleRepresentation.setContainerId(createdClientId);

                    clientResource.roles().create(roleRepresentation);
                }
                System.out.println("Cliente creado: " + clientId);
            } else {
                System.out.println("Error: " + response);
            }

        }
    }

    public void createGatewayClient(String realm, String clientSecret, List<String> roles) {
        // Verificamos que se haya creado el reino y creamos los clientes
        RealmsResource realmsResource =  keycloak.realms();
        RealmRepresentation realmRepresentation = realmsResource.realm(realm).toRepresentation();
        String realmName = realmRepresentation.getRealm();
        if(Objects.equals(realmName, realm)) {
            RealmResource realmResource = keycloak.realm(realm);
            ClientsResource gwResource = realmResource.clients();

            // Creamos gateway
            ClientRepresentation gateway = new ClientRepresentation();
            gateway.setClientId(GTW_CLIENT);
            gateway.setSecret(clientSecret);
            gateway.setRootUrl(GTW_CLIENT_URL);
            gateway.setWebOrigins(List.of("/*"));
            gateway.setRedirectUris(List.of(GTW_CLIENT_URL+"/*"));
            gateway.setAdminUrl(GTW_CLIENT_URL);
            gateway.setEnabled(true);
            gateway.setServiceAccountsEnabled(true);
            gateway.setDirectAccessGrantsEnabled(true);

            // Verificamos que se hayan creado los clientes y creamos los roles a nivel Cliente
            Response responseGTW = gwResource.create(gateway);
            if (responseGTW.getStatus() == 201) {
                // Obtiene el ultimo segmento de la url, en este caso seria el clientId
                String createdClientIdGW = responseGTW.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                System.out.println("GTW: " + createdClientIdGW + ". Roles: " + roles);

                // Obtenemos id de clientes
                ClientResource clientResourceGW = gwResource.get(createdClientIdGW);
                // Seteamos roles a nivel Cliente
                for (String rol : roles) {
                    RoleRepresentation roleRepresentation = new RoleRepresentation();
                    roleRepresentation.setName(rol);
                    roleRepresentation.setClientRole(true);
                    roleRepresentation.setContainerId(createdClientIdGW);

                    clientResourceGW.roles().create(roleRepresentation);
                }
                System.out.println("Cliente creado: " + GTW_CLIENT);
            } else {
                System.out.println("Error: " + responseGTW);
            }
        }
    }

    public void createGroup(String realm, String grupo) {
        // Verificamos que se haya creado el reino y creamos el grupo
        RealmsResource realmsResource =  keycloak.realms();
        RealmRepresentation realmRepresentation = realmsResource.realm(realm).toRepresentation();
        String realmName = realmRepresentation.getRealm();
        if(Objects.equals(realmName, realm)) {
            GroupRepresentation groupRepresentation = new GroupRepresentation();
            groupRepresentation.setName(grupo);

            Response response = realmsResource.realm(realm).groups().add(groupRepresentation);
            if (response.getStatus() == 201) {
                System.out.println("Grupo creado: " + grupo + ". Response= " + response);
            } else {
                System.out.println("Error: " + response);
            }
        }
    }

    public void addGroupsToToken(String realm, String scope) {
        RealmResource realmResource = keycloak.realm(realm);
        List<ClientScopeRepresentation> scopes = realmResource.clientScopes().findAll();
        ClientScopeRepresentation clientScope = scopes.stream()
                .filter(cs -> cs.getName().equals(scope))
                .findFirst()
                .orElse(null);

        String id = clientScope.getId();

        ProtocolMapperRepresentation groupMembership = new ProtocolMapperRepresentation();
        groupMembership.setName("group");
        groupMembership.setProtocol("openid-connect");
        groupMembership.setProtocolMapper("oidc-group-membership-mapper");
        groupMembership.getConfig().put("full.path", "false");
        groupMembership.getConfig().put("access.token.claim", "true");
        groupMembership.getConfig().put("id.token.claim", "true");
        groupMembership.getConfig().put("userinfo.token.claim", "true");
        groupMembership.getConfig().put("claim.name", "groups");

        ClientScopeResource clientScopeResource = realmResource.clientScopes().get(id);
        clientScopeResource.getProtocolMappers().createMapper(groupMembership);

        ClientScopeRepresentation updatedClientScope = clientScopeResource.toRepresentation();
        clientScopeResource.update(updatedClientScope);

        System.out.println("GRUPOS AÑADIDOS A TOKEN");
    }

    public void addUsers(String realm, String group){
        RealmResource realmResource = keycloak.realm(realm);
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        // Con permisos
        UserRepresentation userWithPermission = new UserRepresentation();
        userWithPermission.setUsername("user1");
        userWithPermission.setFirstName("Usuario 1");
        userWithPermission.setEmail("user1@mail.com");
        userWithPermission.setGroups(List.of(group));
        userWithPermission.setEnabled(true);

        // Sin permisos
        UserRepresentation userWithOutPermission = new UserRepresentation();
        userWithOutPermission.setUsername("userTest");
        userWithOutPermission.setFirstName("UsuarioTest");
        userWithOutPermission.setEmail("usertest@mail.com");
        userWithOutPermission.setEnabled(true);

        // Establecer password y dejarla como no temporal
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue("12345678");
        credentialRepresentation.setTemporary(false);

        userWithPermission.setCredentials(List.of(credentialRepresentation));
        userWithOutPermission.setCredentials(List.of(credentialRepresentation));

        realmResource.users().create(userWithPermission);
        realmResource.users().create(userWithOutPermission);

        System.out.println("GRUPOS: "+userWithPermission.getGroups());

        System.out.println("USUARIOS CREADOS");
    }

}

