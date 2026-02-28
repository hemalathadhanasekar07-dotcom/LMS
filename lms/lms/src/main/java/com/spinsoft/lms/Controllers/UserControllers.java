package com.spinsoft.lms.Controllers;

import com.spinsoft.lms.DTO.UserDto;
import com.spinsoft.lms.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserControllers {

@Autowired
private UserServices userServices;
@PostMapping("/auth/register")
    public UserDto createUser(@RequestBody UserDto userDto) throws Exception {
        return userServices.createUser(userDto);
    }


    @PutMapping("/auth/register/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) throws Exception {
        return userServices.updateUser(id,userDto);
    }

    @DeleteMapping("/auth/register/{id}")
    public String deleteUser(){
        return "user deleted";
    }

    @GetMapping("/users/{id}")
    public String getUser(){
    return "user";
    }

}

