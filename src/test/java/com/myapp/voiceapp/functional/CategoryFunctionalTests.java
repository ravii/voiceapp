package com.myapp.voiceapp.functional;

import com.myapp.voiceapp.models.Category;
import com.myapp.voiceapp.models.Query;
import com.myapp.voiceapp.functional.config.FunctionalTestConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.myapp.voiceapp.repositories.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@FunctionalTestConfig
@ContextConfiguration
public class CategoryFunctionalTests extends AbstractEventBaseFunctionalTest {

    @Autowired
    CategoryRepository categoryRepository;

    private List<Category> createAndSaveMultipleVolunteers(int numberOfVolunteers) {
        List<Category> categories = new ArrayList<>();
        for (int i=0; i<numberOfVolunteers; i++) {
            Category v = new Category("First", "Last "+Integer.toString(i));
            categoryRepository.save(v);
            categories.add(v);
        }
        return categories;
    }

    @Test
    public void testCanViewNewVolunteerForm() throws Exception {
        mockMvc.perform(get("/categories/create")
                .with(user(TEST_USER_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create Category")))
                .andExpect(xpath("//form[@id='volunteerForm']/@method").string("post"))
                .andExpect(xpath("//form[@id='volunteerForm']/@action").string("/categories/create"))
                .andExpect(xpath("//form//input[@name='firstName']").exists())
                .andExpect(xpath("//form//input[@name='lastName']").exists());
    }

    @Test
    public void testCanCreateVolunteer() throws Exception {
        mockMvc.perform(post("/categories/create")
                .with(user(TEST_USER_EMAIL))
                .with(csrf())
                .param("firstName", "First")
                .param("lastName", "Last"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));
        mockMvc.perform(get("/categories")
                .with(user(TEST_USER_EMAIL)))
                .andExpect(content().string(containsString("First Last")));
    }

    @Test
    public void testCanViewAddVolunteerButtonOnVolunteerListing() throws Exception {
        mockMvc.perform(get("/categories")
                .with(user(TEST_USER_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(xpath("//a[@href='/categories/create']").string(containsString("Create Category")));
    }

    @Test
    public void testCanViewVolunteersWhenCreatingEvent() throws Exception {
        int numberOfVolunteers = 5;
        List<Category> vols = createAndSaveMultipleVolunteers(numberOfVolunteers);
        mockMvc.perform(get("/queries/create")
                .with(csrf())
                .with(user(TEST_USER_EMAIL)))
                .andExpect(xpath("//select[@name='categories']/option").nodeCount(numberOfVolunteers));
    }

    @Test
    public void testCanAddVolunteersWhenCreatingEvent() throws Exception {
        int numberOfVolunteers = 2;
        List<Category> vols = createAndSaveMultipleVolunteers(numberOfVolunteers);
        mockMvc.perform(post("/queries/create")
                .with(user(TEST_USER_EMAIL))
                .with(csrf())
                .param("title", "Test Query")
                .param("description", "This queries will be great!")
                .param("startDate", "11/11/11")
                .param("location", "")
                .param("categories", Integer.toString(vols.get(0).getUid()))
                .param("categories", Integer.toString(vols.get(1).getUid())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/queries/detail/*"));
        Query query = queryRepository.findAll().get(0);
        assertEquals(query.getCategories().size(), numberOfVolunteers);
    }

    @Test
    public void testCanUpdateVolunteersForEvent() throws Exception {
        int numberOfVolunteers = 3;
        List<Category> vols = createAndSaveMultipleVolunteers(numberOfVolunteers);
        mockMvc.perform(post("/queries/create")
                .with(user(TEST_USER_EMAIL))
                .with(csrf())
                .param("title", "Test Query")
                .param("description", "This queries will be great!")
                .param("startDate", "11/11/11")
                .param("location", "")
                .param("categories", Integer.toString(vols.get(0).getUid()))
                .param("categories", Integer.toString(vols.get(1).getUid())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/queries/detail/*"));
        Query query = queryRepository.findAll().get(0);
        mockMvc.perform(post("/queries/update/{uid}", query.getUid())
                .with(csrf())
                .with(user(TEST_USER_EMAIL))
                .param("uid", Integer.toString(query.getUid()))
                .param("title", query.getTitle())
                .param("option_1", query.getOption_1())
                .param("option_2", query.getOption_2())
                .param("startDate", query.getFormattedStartDate())
                .param("categories", Integer.toString(vols.get(1).getUid()))
                .param("categories", Integer.toString(vols.get(2).getUid())))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/queries/detail/"+ query.getUid()));
        Optional<Query> updatedEventRes = queryRepository.findById(query.getUid());
        assertTrue(updatedEventRes.isPresent());
        Query updatedQuery = updatedEventRes.get();
        assertEquals(updatedQuery.getCategories().size(), 2);
    }
}
