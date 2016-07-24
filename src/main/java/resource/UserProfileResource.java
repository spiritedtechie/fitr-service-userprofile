package resource;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import model.UserProfile;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.model.UserProfileDto;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/user/{id}/profile")
public class UserProfileResource {

    private final static Logger LOG = LoggerFactory.getLogger(UserProfileResource.class);

    private DB mongoDb;

    public UserProfileResource(DB mongoDb) {
        this.mongoDb = mongoDb;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postProfile(
            @PathParam("id") String userId, UserProfileDto profileDto) {

        UserProfile up = profileDto.userProfileFrom();
        up.setId(userId);

        JacksonDBCollection<UserProfile, String> coll = JacksonDBCollection.wrap(
                mongoDb.getCollection("userprofiles"), UserProfile.class, String.class);
        coll.insert(up);

        return Response.ok().build();
    }

    @POST
    @Path("/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postProfileImage(
            @PathParam("id") String userId,
            @FormDataParam("profileimage") InputStream input,
            @FormDataParam("profileimage") FormDataContentDisposition disposition) {

        GridFS fileStore = new GridFS(mongoDb, "profileimages");
        GridFSInputFile inputFile = fileStore.createFile(input);
        inputFile.setId(userId);
        inputFile.setFilename(disposition.getFileName());
        inputFile.save();

        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@PathParam("id") String userId) {

        JacksonDBCollection<UserProfile, String> coll = JacksonDBCollection.wrap(
                mongoDb.getCollection("userprofiles"), UserProfile.class, String.class);

        UserProfile userProfile = coll.findOneById(userId);

        if (userProfile == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok().entity(UserProfileDto.dtoFrom(userProfile)).build();
        }
    }

    @GET
    @Path("/image")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProfileImage(@PathParam("id") String userId) throws Exception {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", userId);

        GridFS fileStore = new GridFS(mongoDb, "profileimages");
        GridFSDBFile gridFile = fileStore.findOne(query);

        InputStream in = gridFile.getInputStream();

        Response.ResponseBuilder builder = Response.ok(in);
        builder.header("Content-Disposition", "attachment; filename=" + gridFile.getFilename());
        return builder.build();
    }
}

