package resource;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import io.dropwizard.auth.Auth;
import model.User;
import model.UserProfile;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.model.UserProfileDto;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/user/profile")
public class UserProfileResource {

    private final static Logger LOG = LoggerFactory.getLogger(UserProfileResource.class);

    public static final String COLLECTION_USERPROFILES = "userprofiles";
    public static final String COLLECTION_PROFILEIMAGES = "profileimages";

    private DB mongoDb;

    public UserProfileResource(DB mongoDb) {
        this.mongoDb = mongoDb;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("NORMAL")
    public Response postProfile(@Auth User user, UserProfileDto profileDto) {

        UserProfile up = profileDto.convertToUserProfile();
        up.setId(user.getId());

        JacksonDBCollection<UserProfile, String> coll = JacksonDBCollection.wrap(
                mongoDb.getCollection(COLLECTION_USERPROFILES), UserProfile.class, String.class);
        coll.removeById(user.getId());
        coll.insert(up);

        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("NORMAL")
    public Response getProfile(@Auth User user) {

        JacksonDBCollection<UserProfile, String> coll = JacksonDBCollection.wrap(
                mongoDb.getCollection(COLLECTION_USERPROFILES), UserProfile.class, String.class);

        UserProfile userProfile = coll.findOneById(user.getId());

        if (userProfile == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok().entity(UserProfileDto.dtoFrom(userProfile)).build();
        }
    }

    @PUT
    @Path("/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("NORMAL")
    public Response postProfileImage(
            @Auth User user,
            @FormDataParam("profileimage") InputStream input,
            @FormDataParam("profileimage") FormDataContentDisposition disposition) {

        GridFS fileStore = new GridFS(mongoDb, COLLECTION_PROFILEIMAGES);

        BasicDBObject query = new BasicDBObject();
        query.put("_id", user.getId());
        fileStore.remove(query);

        GridFSInputFile inputFile = fileStore.createFile(input);
        inputFile.setId(user.getId());
        inputFile.setFilename(disposition.getFileName());
        inputFile.save();

        return Response.ok().build();
    }

    @GET
    @Path("/image")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @RolesAllowed("NORMAL")
    public Response getProfileImage(@Auth User user) throws Exception {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", user.getId());

        GridFS fileStore = new GridFS(mongoDb, COLLECTION_PROFILEIMAGES);
        GridFSDBFile gridFile = fileStore.findOne(query);
        InputStream in = gridFile.getInputStream();
        String filename = gridFile.getFilename();

        Response.ResponseBuilder builder = Response.ok(in);
        builder.header("Content-Disposition", "attachment; filename=" + filename);
        return builder.build();
    }
}

