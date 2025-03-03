package com.casino.resource;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.util.Map;

@ApplicationScoped
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Inject
    public CloudinaryService(
            @ConfigProperty(name = "cloudinary.cloud_name") String cloudName,
            @ConfigProperty(name = "cloudinary.api_key") String apiKey,
            @ConfigProperty(name = "cloudinary.api_secret") String apiSecret
    ) {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadImage(InputStream inputStream, String fileName) {
        try {
            Map uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.asMap(
                    "folder", "bonus-hunt",
                    "public_id", fileName
            ));
            return (String) uploadResult.get("secure_url"); // Retorna a URL da imagem
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da imagem para Cloudinary", e);
        }
    }
}

