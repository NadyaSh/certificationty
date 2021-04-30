package org.niitp.controller;

import io.swagger.annotations.Api;
import org.niitp.model.UserEntity;
import org.niitp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adduser")
@Api(value = "adduser", description = "Добавление пользователей в базу")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @PreAuthorize("hasRole('SUPERMEN')")
    @PostMapping
    public String setUser(@ModelAttribute UserEntity user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.saveAndFlush(user);
        return "createuser";
    }

    @PreAuthorize("hasRole('SUPERMEN')")
    @GetMapping
    public String getForm(Model model) {
        model.addAttribute("createuser", new UserEntity());
        return "createuser";
    }
}
