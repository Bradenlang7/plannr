package com.planify.planify.controller;

import com.planify.planify.dto.BaseUserDTO;
import com.planify.planify.dto.CreateUserDTO;
import com.planify.planify.dto.UpdateUserDTO;
import com.planify.planify.dto.UserMapper;
import com.planify.planify.entity.User;
import com.planify.planify.jwt.JwtUtil;
import com.planify.planify.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userMapper = new UserMapper();
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(@RequestBody CreateUserDTO dto) {
        System.out.println(dto);
        BaseUserDTO user = userService.createUser(dto);
        //If a user account is successfully created, create and sent JWT token to the front end.
        String token = jwtUtil.generateToken(user.email(), user.id().toString());
        User userEntity = userService.getUserByEmail(user.email());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", userEntity.getUsername());
        System.out.println("Sign Up Responses" + response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseUserDTO> updateUser(@PathVariable long id, @RequestBody UpdateUserDTO updateUserDTO) {
        BaseUserDTO baseUserDTO = userService.updateUser(id, updateUserDTO);
        return ResponseEntity.ok(baseUserDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseUserDTO> getBaseUserDTOById(@PathVariable long id) {
        BaseUserDTO baseUserDTO = userService.getBaseUserDTOById(id);

        return ResponseEntity.ok(baseUserDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable long id) {
        long deletedId = userService.deleteUser(id);

        return ResponseEntity.ok("User with ID " + deletedId + " has been deleted");
    }
}
