package com.myapp.voiceapp.functional;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.myapp.voiceapp.forms.UserForm;
import com.myapp.voiceapp.user.UserService;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public abstract class AbstractBaseFunctionalTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    UserService userService;

    MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    static final String TEST_USER_FULL_NAME = "New User";
    static final String TEST_USER_EMAIL = "test@launchcode.org";
    static final String TEST_USER_PASSWORD = "learntocode";

    @Before
    public void setUpUser() throws Exception {
        UserForm userForm = new UserForm();
        userForm.setEmail(TEST_USER_EMAIL);
        userForm.setFullName(TEST_USER_FULL_NAME);
        userForm.setPassword(TEST_USER_PASSWORD);
        userForm.setVerifyPassword(TEST_USER_PASSWORD);
        userService.save(userForm);
    }

}
