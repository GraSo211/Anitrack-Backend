package com.graso.anitrack.administrator.controller;

import com.graso.anitrack.administrator.controller.dto.HeroImageResponse;
import com.graso.anitrack.administrator.controller.dto.ImagesHeroResponse;
import com.graso.anitrack.administrator.service.AdminUserService;
import com.graso.anitrack.administrator.service.AdministratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController()
@RequestMapping("/api/v1/admin")
public class AdministratorController {
    AdministratorService administratorService;
    AdminUserService adminUserService;

    public AdministratorController(AdministratorService administratorService, AdminUserService adminUserService) {
        this.administratorService = administratorService;
        this.adminUserService = adminUserService;

    }

    @PostMapping("/hero-images")
    public ResponseEntity<ImagesHeroResponse> postHeroImages(@CookieValue("access_token") String token,
                                                             @RequestParam("images") List<MultipartFile> multipartFileList) {
        adminUserService.requireAdmin(token);
        List<String> uploadedUrls = administratorService.uploadHeroImages(multipartFileList);
        ImagesHeroResponse response = new ImagesHeroResponse(uploadedUrls);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/hero-images")
    public ResponseEntity<List<HeroImageResponse>> getHeroImages() {
        List<HeroImageResponse> heroImages = administratorService.getHeroImages();

        return new ResponseEntity<>(heroImages, HttpStatus.OK);
    }

    @DeleteMapping("/hero-images")
    public ResponseEntity<Void> deleteHeroImage(@CookieValue("access_token") String token,
                                                @RequestParam("publicId") String publicId) {
        adminUserService.requireAdmin(token);
        administratorService.deleteHeroImage(publicId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    ;
}
