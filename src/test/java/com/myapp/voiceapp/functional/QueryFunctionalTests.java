package com.myapp.voiceapp.functional;

import com.myapp.voiceapp.models.Query;
import com.myapp.voiceapp.functional.config.FunctionalTestConfig;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.text.StringContainsInOrder.stringContainsInOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@FunctionalTestConfig
@ContextConfiguration
public class QueryFunctionalTests extends AbstractEventBaseFunctionalTest {

    @Test
    public void testIndexShowsRecentEvents() throws Exception {
        Query query = createAndSaveSingleEvent();
        mockMvc.perform(get("/")
                .with(user(TEST_USER_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(query.getTitle())))
                .andExpect(content().string(containsString(query.getFormattedStartDate())));
    }

    @Test
    public void testCanViewNewEventForm() throws Exception {
        mockMvc.perform(get("/queries/create")
                .with(user(TEST_USER_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create Query")))
                .andExpect(xpath("//form[@id='eventForm']/@method").string("post"))
                .andExpect(xpath("//form[@id='eventForm']/@action").string("/queries/create"))
                .andExpect(xpath("//form//input[@name='title']").exists())
                .andExpect(xpath("//form//input[@name='startDate']").exists())
                .andExpect(xpath("//form//textarea[@name='description']").exists())
                .andExpect(xpath("//form//input[@name='location']").exists())
                .andExpect(xpath("//form//select[@name='categories']").exists());
    }

    @Test
    public void testCanCreateEvent() throws Exception {
        mockMvc.perform(post("/queries/create")
                .with(user(TEST_USER_EMAIL))
                .with(csrf())
                .param("title", "Test Query")
                .param("description", "This queries will be great!")
                .param("startDate", "11/11/11")
                .param("location", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/queries/detail/*"));
        Query query = queryRepository.findAll().get(0);
        assertNotNull(query);
    }

    @Test
    public void testCanViewEventDetails() throws Exception {
        Query query = createAndSaveSingleEvent();
        List<String> eventFields = Arrays.asList(
                query.getTitle(),
                query.getFormattedStartDate(),
                query.getOption_1(),query.getOption_2(),
                query.getCategoriesFormatted()
        );
        mockMvc.perform(get("/queries/detail/{uid}", query.getUid())
                .with(user(TEST_USER_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(content().string(stringContainsInOrder(eventFields)));
    }

    @Test
    public void testDisplayErrorMessageOnInvalidEventId() throws Exception {
        mockMvc.perform(get("/queries/detail/{uid}", -1)
                .with(csrf())
                .with(user(TEST_USER_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No event found with id: -1")));
    }

    @Test
    public void testCreateEventValidatesTitle() throws Exception {
        mockMvc.perform(post("/queries/create")
                .with(csrf())
                .with(user(TEST_USER_EMAIL))
                .param("title", "")
                .param("description", "Test description")
                .param("startDate", "11/11/11"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("event", "title"));
    }

    @Test
    public void testCreateEventValidatesDescription() throws Exception {
        mockMvc.perform(post("/queries/create")
                .with(csrf())
                .with(user(TEST_USER_EMAIL))
                .param("title", "Test title")
                .param("description", "")
                .param("startDate", "11/11/11"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("event", "description"));
    }

    @Test
    public void testCreateEventValidatesStartDate() throws Exception {
        mockMvc.perform(post("/queries/create")
                .with(csrf())
                .with(user(TEST_USER_EMAIL))
                .param("title", "Test Title")
                .param("description", "Test description")
                .param("startDate", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("event", "startDate"));
    }

    @Test
    public void testEventDetailsPageDisplaysEditButton() throws Exception {
        Query query = createAndSaveSingleEvent();
        mockMvc.perform(get("/queries/detail/{uid}", query.getUid())
                .with(user(TEST_USER_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(xpath("//a[contains(@class,'btn') and @href='/queries/update/%s']", query.getUid())
                        .string(Matchers.containsString("Edit")));
    }

    @Test
    public void testCanViewUpdateEventForm() throws Exception {
        Query query = createAndSaveSingleEvent();
        mockMvc.perform(get("/queries/update/{uid}", query.getUid())
                .with(user(TEST_USER_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Update Query")))
                .andExpect(xpath("//form[@method='post' and @action='/queries/update/%s']",
                        query.getUid()).exists())
                .andExpect(xpath("//form//input[@name='uid' and @value='%s']",
                        query.getUid()).exists())
                .andExpect(xpath("//form//input[@name='title' and @value='%s']",
                        query.getTitle()).exists())
                .andExpect(xpath("//form//input[@name='startDate' and @value='%s']",
                        query.getFormattedStartDate()).exists())

                .andExpect(xpath("//form//textarea[@name='option_1']")
                        .string(query.getOption_1()))
                .andExpect(xpath("//form//textarea[@name='option_2']")
                        .string(query.getOption_2()));
    }

    @Test
    public void testCanUpdateEvent() throws Exception {
        Query query = createAndSaveSingleEvent();
        String newTitle = query.getTitle() + "UPDATED";
        mockMvc.perform(post("/queries/update/{uid}", query.getUid())
                .with(csrf())
                .with(user(TEST_USER_EMAIL))
                .param("uid", Integer.toString(query.getUid()))
                .param("title", newTitle)
                .param("option_1", query.getOption_1())
                .param("option_2", query.getOption_2())
                .param("startDate", query.getFormattedStartDate())
                .param("categories", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/queries/detail/"+ query.getUid()));
        Optional<Query> updatedEventRes = queryRepository.findById(query.getUid());
        assertTrue(updatedEventRes.isPresent());
        Query updatedQuery = updatedEventRes.get();
        assertEquals(newTitle, updatedQuery.getTitle());
    }

    @Test
    public void testEventDetailPageContainsDeleteButton() throws Exception {
        Query query = createAndSaveSingleEvent();
        mockMvc.perform(get("/queries/detail/{uid}", query.getUid())
                .with(user(TEST_USER_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(xpath("//form[@method='post' and @action='/queries/delete/%s']", query.getUid())
                    .exists())
                .andExpect(xpath("//form//button")
                        .string(Matchers.containsString("Delete")));
    }

    @Test
    public void testCanDeleteEvent() throws Exception {
        Query query = createAndSaveSingleEvent();
        mockMvc.perform(post("/queries/delete/{uid}", query.getUid())
                .with(user(TEST_USER_EMAIL))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/queries"));
        Optional<Query> updatedEventRes = queryRepository.findById(query.getUid());
        assertFalse(updatedEventRes.isPresent());
    }


}
