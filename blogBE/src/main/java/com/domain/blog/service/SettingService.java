package com.domain.blog.service;

import com.domain.blog.domain.Setting;
import com.domain.blog.domain.User;
import com.domain.blog.domain.dto.ContactAdminDTO;
import com.domain.blog.repository.SettingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class SettingService {
    private final SettingRepository settingsRepository;

    public SettingService(SettingRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @PostConstruct
    public void initializeSettings() {
        if (settingsRepository.count() == 0) {
            Setting setting = new Setting();
            setting.setId("setting");
            setting.setTitle("Trang chủ");
            setting.setSiteName("Skinlab by Tuyen");
            setting.setDescription("Bạn muốn tìm hiểu về các bí quyết làm đẹp tự nhiên? Hãy ghé thăm blog của tôi!");
            setting.setEmail("hakimtuyen997@gmail.com");
            setting.setFacebookLink("https://www.facebook.com/100006900795338");
            setting.setMessengerLink("https://m.me/61551133326547/");
            setting.setXLink("");
            setting.setInstagramLink("");
            setting.setFanpageFacebookLink("https://www.facebook.com/61551133326547");
            settingsRepository.save(setting);
        }
    }


    public Setting getSetting() {
        Optional<Setting> setting = this.settingsRepository.findById("setting");
       return setting.orElse(null);
    }


    public Setting updateSetting(Setting setting)  {
        Setting settingUpdate = this.getSetting();
        if (setting == null) {
            return null;
        }

        settingUpdate.setTitle(setting.getTitle());
        settingUpdate.setDescription(setting.getTitle());
        settingUpdate.setSiteName(setting.getSiteName());
        settingUpdate.setEmail(setting.getEmail());
        settingUpdate.setFacebookLink(setting.getFacebookLink());
        settingUpdate.setMessengerLink(setting.getMessengerLink());
        settingUpdate.setXLink(setting.getXLink());
        settingUpdate.setInstagramLink(setting.getInstagramLink());
        settingUpdate.setFanpageFacebookLink(setting.getFanpageFacebookLink());

        return this.settingsRepository.save(settingUpdate);
    }

    public ContactAdminDTO getInformationContactAdmin(User user, Setting setting) {
        ContactAdminDTO contactAdminDTO = new ContactAdminDTO();
        contactAdminDTO.setName(user.getName());
        contactAdminDTO.setFacebookLink(setting.getFacebookLink());
        contactAdminDTO.setMessengerLink(setting.getMessengerLink());
        contactAdminDTO.setXLink(setting.getXLink());
        contactAdminDTO.setInstagramLink(setting.getInstagramLink());
        contactAdminDTO.setFanpageFacebookLink(setting.getFanpageFacebookLink());
        return contactAdminDTO;
    }


    public String getSiteNameSetting() {
        Optional<Setting> setting = this.settingsRepository.findById("setting");
        if(setting.isPresent()) {
            return setting.get().getSiteName();
        }
        return "";
    }

    public String getEmailSetting() {
        Optional<Setting> setting = this.settingsRepository.findById("setting");
        if(setting.isPresent()) {
            return setting.get().getEmail();
        }
        return "";
    }
}
