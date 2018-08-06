package com.myapp.voiceapp.models;

import org.junit.Test;

import com.myapp.voiceapp.models.User;


public class TestUser {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserCatchesBlankEmail() {
        new User(null, "a", "a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserCatchesBlankFullName() {
        new User("a@a.a", null, "a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserCatchesBlankPassword() {
        new User("a@a.a", "a", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserPerformsSimpleEmailValidation() {
        new User("a", "a", null);
    }

}
