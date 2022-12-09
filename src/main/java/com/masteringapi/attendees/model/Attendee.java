package com.masteringapi.attendees.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class Attendee {
    private Integer id;

    public Attendee() {

    }

    public Attendee(com.masteringapi.attendees.grpc.server.Attendee attendee) {
        this.surname = attendee.getSurname();
        this.givenName = attendee.getGivenName();
        this.email = attendee.getEmail();
    }

    @NotNull
    @Size(max=35)
    private String givenName;

    @NotNull
    @Size(max=35)
    private String surname;

    @NotNull
    @Size(max=254)
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendee attendee = (Attendee) o;
        return Objects.equals(id, attendee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
