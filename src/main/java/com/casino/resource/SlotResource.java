package com.casino.resource;
import com.casino.model.BonusHunt;
import com.casino.model.Slot;
import com.casino.model.SlotEntry;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.List;

@Path("/slots")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SlotResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSlot(Slot slot) {
        try {
            System.out.println("📥 Dados recebidos no backend: " + slot);

            if (slot.name == null || slot.provider == null || slot.rtp <= 0 || slot.maxWin <= 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Campos obrigatórios faltando").build();
            }

            if (slot.imageUrl == null || slot.imageUrl.isEmpty()) { // 🟢 Usamos imageUrl em vez de imageBase64
                return Response.status(Response.Status.BAD_REQUEST).entity("Imagem obrigatória").build();
            }

            slot.persist(); // Salva no MongoDB com a URL da imagem

            System.out.println("✅ Slot salva no MongoDB: " + slot.id);
            return Response.ok(slot).build();

        } catch (Exception e) {
            e.printStackTrace(); // Exibe erro no console do backend
            return Response.serverError().entity("Erro ao salvar a slot").build();
        }
    }



    @GET
    @Path("/providers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProviders() {
        List<String> providers = Slot.<Slot>listAll() // 🔥 Casting explícito para Slot
                .stream()
                .map(slot -> slot.provider) // 🔥 Agora pode acessar diretamente
                .filter(provider -> provider != null && !provider.isBlank()) // 🔥 Usa `isBlank()` para evitar problemas com espaços
                .distinct()
                .sorted()
                .toList();

        return Response.ok(providers).build();
    }





    @GET
    @Path("/slots")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Slot> getSlots(@QueryParam("query") String query) {
        return Slot.list("name like ?1", "%" + query + "%");
    }

    @PUT
    @Path("/bonus-hunts/{id}/slots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSlotToBonusHunt(@PathParam("id") String id, SlotEntry slot) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(id));
        if (hunt == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        hunt.slots.add(slot);
        hunt.persist();
        return Response.ok(hunt).build();
    }


    @GET
    public List<Slot> list() {
        return Slot.listAll();
    }

    @GET
    @Path("/{id}")
    public Slot get(@PathParam("id") String id) {
        return Slot.findById(new org.bson.types.ObjectId(id));
    }
}
