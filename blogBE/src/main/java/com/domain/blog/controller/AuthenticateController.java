package com.domain.blog.controller;

import com.domain.blog.annotation.ApiMessage;
import com.domain.blog.constant.RoleEnum;
import com.domain.blog.domain.User;
import com.domain.blog.domain.dto.*;
import com.domain.blog.exception.DataNotFoundException;
import com.domain.blog.service.RoleService;
import com.domain.blog.service.UserService;
import com.domain.blog.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping("/blog-api")
public class AuthenticateController {

    @Value("${app.jwt.jwt-validity-in-seconds}")
    private long jwtExpiration;
    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @Value("${app.jwt.jwt-secret-key}")
    private String jwtSecretKey;

    private final JwtEncoder jwtEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;


    public AuthenticateController(AuthenticationManagerBuilder authenticationManagerBuilder, JwtEncoder jwtEncoder, UserService userService, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }


    @PostMapping("/admin/auth/login")
    public ResponseEntity<ResponseLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        // Authenticate in loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();

        User userDB = this.userService.getUserByUsername(loginDTO.getUsername());
        UserDTO.RoleUser roleUSer = new UserDTO.RoleUser(userDB.getRole().getId(), userDB.getRole().getName());
        UserDTO userDTO = new UserDTO(userDB.getId(), userDB.getUsername(), userDB.getName(), userDB.getImage(), userDB.getImageProvider(), roleUSer, userDB.getProvider());
        responseLoginDTO.setUser(userDTO);

        // create access_token
        String access_token = SecurityUtil.createToken(userDTO, jwtEncoder, jwtExpiration);
        responseLoginDTO.setAccessToken(access_token);

        //Create refresh token, update refresh token for user and setCookie
        String refreshToken = SecurityUtil.createRefreshToken(loginDTO.getUsername(), userDTO, jwtEncoder, refreshTokenExpiration);
        responseLoginDTO.setRefreshToken(refreshToken);
        this.userService.updateUserRefreshToken(refreshToken, loginDTO.getUsername());


        return ResponseEntity.ok().body(responseLoginDTO);
    }

    @ApiMessage("fetch account")
    @GetMapping("/admin/auth/account")
    public ResponseEntity<UserDTO> getAccount() {
        String username = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        User user = this.userService.getUserByUsername(username);

        return ResponseEntity.ok(this.userService.convertUserToUserDTO(user));
    }

    @ApiMessage("Get user by refresh token")
    @PostMapping("/auth/refresh")
    public ResponseEntity<ResponseLoginDTO> getRefreshToken(@RequestBody TokenDTO refreshToken) {
        try{
            if(Objects.equals(refreshToken.getRefreshToken(), "")){
                throw new BadCredentialsException("Invalid Refresh token");
            }
            //check valid token
            Jwt decodedToken = SecurityUtil.checkValidRefreshToken(refreshToken.getRefreshToken(), this.jwtSecretKey);
            String username = decodedToken.getSubject();

            // check user by token + email
            User currentUserDB = this.userService.getUserByRefreshTokenAndUsername(refreshToken.getRefreshToken(), username);
            if (currentUserDB == null) {
                throw new BadCredentialsException("Refresh Token is invalid");
            }

            UserDTO.RoleUser roleUSer = new UserDTO.RoleUser(currentUserDB.getRole().getId(), currentUserDB.getRole().getName());
            UserDTO userDTO = new UserDTO(currentUserDB.getId(), currentUserDB.getUsername(), currentUserDB.getName(), currentUserDB.getImage(), currentUserDB.getImageProvider(), roleUSer, currentUserDB.getProvider());
            String createNewAccessToken = SecurityUtil.createToken(userDTO, jwtEncoder, jwtExpiration);

            ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
            responseLoginDTO.setAccessToken(createNewAccessToken);
            responseLoginDTO.setRefreshToken(refreshToken.getRefreshToken());
            responseLoginDTO.setUser(userDTO);

            return ResponseEntity.ok().body(responseLoginDTO);
        }catch (Exception e){
            throw new BadCredentialsException("Invalid refresh token");
        }

    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws DataNotFoundException {
        String username = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        if(username.equals("")){
            throw new DataNotFoundException("Invalid username");
        }
        this.userService.updateUserRefreshToken("", username);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/auth/social-media")
    public ResponseEntity<ResponseLoginDTO> loginWidthSocialMedia(@RequestBody LoginSocialMediaDTO loginSocialMediaDTO) {
        User  userWithSocialMedia;
        Optional<User> userOptional = Optional.ofNullable(this.userService.getUserByUsername(loginSocialMediaDTO.getUsername()));
        if(userOptional.isPresent()){
            userWithSocialMedia = this.userService.updateUserSocialMedia(userOptional.get(), loginSocialMediaDTO);
        }else{
            User newUser = new User();
            newUser.setName(loginSocialMediaDTO.getName());
            newUser.setUsername(loginSocialMediaDTO.getUsername());
            newUser.setProvider(loginSocialMediaDTO.getProvider());
            newUser.setRole(this.roleService.findByName("USER"));
            newUser.setPassword(String.valueOf(System.currentTimeMillis()));
            newUser.setImageProvider(loginSocialMediaDTO.getImageProvider());
            userWithSocialMedia = this.userService.createUser(newUser);
        }
        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
        UserDTO userDTO = this.userService.convertUserToUserDTO(userWithSocialMedia);
        responseLoginDTO.setUser(userDTO);

        // create access_token
        String access_token = SecurityUtil.createToken(userDTO, jwtEncoder, jwtExpiration);
        responseLoginDTO.setAccessToken(access_token);

        //Create refresh token, update refresh token for user
        String refreshToken = SecurityUtil.createRefreshToken(userWithSocialMedia.getUsername(), userDTO, jwtEncoder, refreshTokenExpiration);
        responseLoginDTO.setRefreshToken(refreshToken);
        this.userService.updateUserRefreshToken(refreshToken, userWithSocialMedia.getUsername());

        return ResponseEntity.ok().body(responseLoginDTO);
    }

}
