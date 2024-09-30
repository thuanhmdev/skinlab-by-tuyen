package com.domain.blog.service;
import com.domain.blog.constant.ProviderEnum;
import com.domain.blog.constant.RoleEnum;
import com.domain.blog.domain.Permission;
import com.domain.blog.domain.User;
import com.domain.blog.domain.dto.LoginSocialMediaDTO;
import com.domain.blog.domain.dto.UserDTO;
import com.domain.blog.repository.UserRepository;
import com.domain.blog.util.FileUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.domain.blog.constant.FileConstant.allowedFileExtension;
import static com.domain.blog.constant.FileConstant.baseURI;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService, PermissionService permissionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;

    }

    @DependsOn("RoleService")
    @PostConstruct
    public void initializeSettings() {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("admin");
            user.setName("ADMIN");
            user.setImage("/avatar/default_user.png");
            user.setPassword(passwordEncoder.encode("121212"));
            user.setRole(this.roleService.findByName("ADMIN"));
            user.setProvider(ProviderEnum.SYSTEM);

            userRepository.save(user);
        }
    }

    public UserDTO convertUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setUsername(user.getUsername());
        userDTO.setImage(user.getImage());
        userDTO.setImageProvider(user.getImageProvider());

        UserDTO.RoleUser roleUSer = new UserDTO.RoleUser(user.getRole().getId(), user.getRole().getName());
        userDTO.setRole(roleUSer);
        userDTO.setProvider(user.getProvider());

        return userDTO;
    }

    public User createUser(User user) {
        return this.userRepository.save(user);
    }



    public User getUserById(String id) {
        Optional<User> userById = this.userRepository.findById(id);
        return userById.orElse(null);
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public User updateUser(MultipartFile file, User user) throws URISyntaxException, IOException {
        User userUpdate = this.getUserById(user.getId());
        if (userUpdate == null) {
            return null;
        }
        userUpdate.setImage(user.getImage());
        userUpdate.setName(user.getName());

        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            boolean isValid = allowedFileExtension.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
            if (isValid) {

                // Delete old file if exists
                if(userUpdate.getImage() != null && !userUpdate.getImage().isEmpty()){
                    FileUtil.deleteOldFile(fileName, "avatar");
                }

                //Create a directory if not Exist
                FileUtil.createDirectory(baseURI + "avatar");
                //Store file and save name to DB
                String fileNameFinal = FileUtil.storeFile(file, "avatar");
                String HostAndPort= ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                userUpdate.setImage( "/" +"avatar" + "/" + fileNameFinal);
            }
        } else {
            if (Objects.equals(userUpdate.getImage(), "") || userUpdate.getImage() == null) {
                // Delete old file if exists
                if(userUpdate.getImage() != null && !userUpdate.getImage().isEmpty()){
                    FileUtil.deleteOldFile(userUpdate.getImage(), "avatar");
                }
                userUpdate.setImage(null);
            } else {
                userUpdate.setImage(userUpdate.getImage());
            }
        }
        return this.userRepository.save(userUpdate);

    }

    public void updateUserRefreshToken(String refreshToken, String username) {
        User user = this.userRepository.findByUsername(username);
        user.setRefreshToken(refreshToken);
        this.userRepository.save(user);
    }

    public User getUserByRefreshTokenAndUsername(String token, String username) {
        return this.userRepository.findByRefreshTokenAndUsername(token, username);
    }

    public boolean existsById(String id) {
        return this.userRepository.existsById(id);
    }

    public User updateUserSocialMedia(User userDB, LoginSocialMediaDTO loginSocialMediaDTO) {
        userDB.setName(loginSocialMediaDTO.getName());
        userDB.setImageProvider(loginSocialMediaDTO.getImageProvider());

        return this.userRepository.save(userDB);
    }


    public List<Permission> getPermissionFromUserRole(String username){
        User user = this.userRepository.findByUsername(username);
        return user.getRole().getPermissions();

    }

}
