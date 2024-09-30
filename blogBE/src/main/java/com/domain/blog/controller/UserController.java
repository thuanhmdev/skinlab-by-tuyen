package com.domain.blog.controller;


import com.domain.blog.annotation.ApiMessage;
import com.domain.blog.domain.User;
import com.domain.blog.domain.dto.UserDTO;
import com.domain.blog.exception.DataNotFoundException;
import com.domain.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/blog-api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiMessage("Call api Successfully")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") String id) throws DataNotFoundException {
        User user = this.userService.getUserById(id);
        if (user == null) {
            throw new DataNotFoundException("Data not found");
        }
        return ResponseEntity.ok(this.userService.convertUserToUserDTO(user));
    }

    @PutMapping()
    public ResponseEntity<UserDTO> updateUser(@RequestParam(name = "file", required = false) MultipartFile file,
                                              @RequestParam(name = "id") String id,
                                              @RequestParam(name = "name") String name,
                                              @RequestParam(name = "image", required = false) String image
    ) throws DataNotFoundException, URISyntaxException, IOException {

        User newInforUser = new User();
        newInforUser.setId(id);
        newInforUser.setName(name);
        newInforUser.setImage(image);


        User userUpdated = this.userService.updateUser(file, newInforUser);
        if (userUpdated == null) {
            throw new DataNotFoundException("Data not found");
        }


        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertUserToUserDTO(userUpdated));
    }
}
