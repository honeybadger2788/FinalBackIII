package com.dh.msusers.repository;

import com.dh.msusers.model.Bill;
import com.dh.msusers.model.User;
import com.dh.msusers.repository.feign.BillsFeignRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class KeycloakRepository implements UserRepository {

    private final Keycloak keycloakClient;
    private final BillsFeignRepository billsFeignRepository;
    private String realm = "ecommerce-realm";

    private User toUser(UserRepresentation userRepresentation) {
        return User.builder()
                .id(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .email(userRepresentation.getEmail())
                .firstName(userRepresentation.getFirstName())
                .build();
    }

    @Override
    public User findById(String id){
        UserRepresentation userRepresentation = keycloakClient.realm(realm).users().get(id).toRepresentation();
        User user = toUser(userRepresentation);
        List<Bill> bills = billsFeignRepository.getByUserId(id);
        user.setBills(bills);

        return user;
    }
}
