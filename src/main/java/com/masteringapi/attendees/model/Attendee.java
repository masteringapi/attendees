package com.masteringapi.attendees.model;

import javax.annotation.RegEx;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.regex.Pattern;

public class Attendee {
    private Integer id;

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
