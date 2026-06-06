package com.mateo.wallet.user.dto;

public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String dni;
    private String cbu;
    private String alias;

    public UserResponse() {}

    public UserResponse(Long id, String email, String firstName,
                        String lastName, String dni, String cbu, String alias) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.cbu = cbu;
        this.alias = alias;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDni() { return dni; }
    public String getCbu() { return cbu; }
    public String getAlias() { return alias; }
}