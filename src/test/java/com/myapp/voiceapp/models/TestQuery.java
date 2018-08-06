package com.myapp.voiceapp.models;

import com.myapp.voiceapp.models.Category;
import com.myapp.voiceapp.models.Query;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class TestQuery {


    @Test(expected = IllegalArgumentException.class)
    public void testCreateEventCatchesNullTitle() {
        new Query(null, "", "", new Date(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEventCatchesEmptyTitle() {
        new Query("", "", "",new Date(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEventCatchesNullDescription() {
        new Query("Title", null,null, new Date(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEventCatchesBlankDescription() {
        new Query("Title", "", "",new Date(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEventCatchesNullDate() {
        new Query("Title", "", null, new Date());
    }

    @Test
    public void testCanAddCategories() {
        Query query = new Query("Title", "A great description", "",new Date(), "Place");
        Category vol = new Category("First", "Last");
        assertEquals(0, query.getCategories().size());
        query.addCategory(vol);
        assertEquals(1, query.getCategories().size());
    }

    @Test
    public void canCreateEventWithVolunteers() {
        Category vol1 = new Category("First", "Last");
        Category vol2 = new Category("First", "Last");
        List<Category> vols = new ArrayList<>();
        vols.add(vol1);
        vols.add(vol2);
        Query query = new Query("Title", "A great description", "",new Date(), vols);
        assertEquals(vols.size(), query.getCategories().size());
    }

}
