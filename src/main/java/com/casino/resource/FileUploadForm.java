package com.casino.resource;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import jakarta.ws.rs.FormParam;
import java.io.InputStream;

public class FileUploadForm {

    @FormParam("file")
    @PartType("application/octet-stream")
    public InputStream file;
}
