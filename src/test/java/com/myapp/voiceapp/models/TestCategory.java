package com.myapp.voiceapp.models;

import com.myapp.voiceapp.models.Category;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class TestCategory {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateVolunteerValidatesFirstName() {
        new Category("ab", "abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateVolunteerValidatesLastName() {
        new Category("abc", "ab");
    }

//    @Test
//    public void testGetFullName() {
//        Category vol = new Category("First", "Last");
//        assertEquals("First Last", vol.getFullName());
//    }

}
