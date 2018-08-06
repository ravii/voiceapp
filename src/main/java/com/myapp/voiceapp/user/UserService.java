package com.myapp.voiceapp.user;

import com.myapp.voiceapp.forms.UserForm;
import com.myapp.voiceapp.models.User;

public interface UserService {

    public User save(UserForm userForm) throws EmailExistsException;
    public User findByEmail(String email);

}
