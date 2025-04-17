package com.greenwave.todolist.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.greenwave.todolist.model.User;
import com.greenwave.todolist.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class RoleController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    @PreAuthorize("hasRole('MANAGER')")
    public String showAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @PostMapping("/promote/{username}")
    public String promoteToManager(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setRoles("MANAGER");
            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/demote/{username}")
    public String demoteToUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setRoles("USER");
            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }
}
