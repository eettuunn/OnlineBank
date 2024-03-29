package ru.hits.coreservice.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class JwtUserData {

    private final UUID id;

    private final String name;

    private final String email;

    private final Boolean ban;

    private final List<String> roles;

}
