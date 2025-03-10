package com.casino.resource;

import com.casino.model.Slot;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.Optional;

@Path("/update-widget")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WidgetResource {

    private static String lastSelectedSlotName = null; // 🔥 Variável para armazenar a última slot selecionada

    // ✅ Atualiza a última slot selecionada
    @POST
    public Response updateWidgetSlot(SlotUpdateRequest request) {
        if (request.slotName == null || request.slotName.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("SlotName é obrigatório!").build();
        }

        lastSelectedSlotName = request.slotName;
        return Response.ok().build();
    }

    // ✅ Retorna a última slot selecionada
    @GET
    public Response getLastSelectedSlot() {
        if (lastSelectedSlotName == null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        // 🔄 Buscar a slot no banco de dados (MongoDB)
        Optional<Slot> slot = Slot.find("name", lastSelectedSlotName).firstResultOptional();

        if (slot.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("Slot não encontrada").build();
        }

        Slot selectedSlot = slot.get();
        SlotUpdateResponse response = new SlotUpdateResponse(
                selectedSlot.name,
                selectedSlot.provider,
                selectedSlot.imageUrl
        );

        return Response.ok(response).build();
    }

    // 📌 Classe auxiliar para a requisição
    public static class SlotUpdateRequest {
        public String slotName;
    }

    // 📌 Classe auxiliar para a resposta
    public static class SlotUpdateResponse {
        public String slotName;
        public String provider;
        public String imageUrl;

        public SlotUpdateResponse(String slotName, String provider, String imageUrl) {
            this.slotName = slotName;
            this.provider = provider;
            this.imageUrl = imageUrl;
        }
    }
}
