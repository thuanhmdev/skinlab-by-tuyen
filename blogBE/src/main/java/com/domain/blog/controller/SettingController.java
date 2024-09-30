package com.domain.blog.controller;

import com.domain.blog.domain.Setting;
import com.domain.blog.domain.User;
import com.domain.blog.domain.dto.ContactAdminDTO;
import com.domain.blog.exception.DataNotFoundException;
import com.domain.blog.service.SettingService;
import com.domain.blog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blog-api/settings")
public class SettingController {
    private final SettingService settingService;
    private final UserService userService;

    public SettingController(SettingService settingService, UserService userService) {
        this.settingService = settingService;
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<Setting> getSetting() {
        return ResponseEntity.ok(this.settingService.getSetting());
    }

    @GetMapping("/contact")
    public ResponseEntity<ContactAdminDTO> getInformationContactAdmin() {
        User admin = this.userService.getUserByUsername("admin");
        Setting setting = this.settingService.getSetting();
        return ResponseEntity.ok(this.settingService.getInformationContactAdmin(admin, setting));
    }


    @PutMapping()
    public ResponseEntity<Setting> updateSetting(@RequestBody Setting setting) throws DataNotFoundException {
        Setting settingUpdated = this.settingService.updateSetting(setting);
        if (settingUpdated == null) {
            throw new DataNotFoundException("Data not found");
        }
        return ResponseEntity.ok(settingUpdated);
    }

}
