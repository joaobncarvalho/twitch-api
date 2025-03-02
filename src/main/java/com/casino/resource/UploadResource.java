package com.casino.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import java.io.IOException;
import java.util.Base64;
import com.casino.resource.FileUploadForm;

@Path("/upload")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class UploadResource {

    @POST
    public Response uploadImage(@MultipartForm FileUploadForm form) {
        if (form.file == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Nenhum arquivo enviado").build();
        }

        try {
            // Converter a imagem para Base64
            byte[] fileContent = form.file.readAllBytes();
            String base64Image = Base64.getEncoder().encodeToString(fileContent);

            // Retornar a imagem codificada
            return Response.ok("{\"imageBase64\": \"" + base64Image + "\"}").build();

        } catch (IOException e) {
            return Response.serverError().entity("Erro ao processar a imagem").build();
        }
    }
}
