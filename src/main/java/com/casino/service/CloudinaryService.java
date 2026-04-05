package com.casino.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.io.IOException;
import java.util.Map;

@ApplicationScoped
public class CloudinaryService {

    @ConfigProperty(name = "cloudinary.cloud_name")
    String cloudName;

    @ConfigProperty(name = "cloudinary.api_key")
    String apiKey;

    @ConfigProperty(name = "cloudinary.api_secret")
    String apiSecret;

    @ConfigProperty(name = "cloudinary.folder", defaultValue = "slots_v2")
    String folder;

    private Cloudinary cloudinary;

    @PostConstruct
    void init() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    public String uploadImage(byte[] imageBytes, String fileName) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.asMap(
                "public_id", fileName,
                "folder", folder,
                "overwrite", true,
                "resource_type", "image"
        ));
        return (String) uploadResult.get("secure_url");
    }
}