package me.dimitri.project_manager.controller.dashboard;

import com.cloudinary.utils.ObjectUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import me.dimitri.project_manager.cloudinary.CloudinaryConfig;
import me.dimitri.project_manager.service.UserService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;
@Validated
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/pfp")
public class ImageController {

    private final UserService userService;
    private final CloudinaryConfig cloudinaryConfig;

    @Inject
    public ImageController(UserService userService, CloudinaryConfig cloudinaryConfig) {
        this.userService = userService;
        this.cloudinaryConfig = cloudinaryConfig;
    }

//    @Post(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
//    @Produces(MediaType.TEXT_PLAIN)
//    public HttpResponse<String> uploadBytes(
//            @NotNull CompletedFileUpload file,
//            @NotNull @NotBlank String fileName
//    ) {
//        try {
//            Map uploadResult = cloudinaryConfig.getCloudinary().uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
//            String url = (String) uploadResult.get("secure_url");
//            return HttpResponse.status(HttpStatus.OK).body(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return HttpResponse.badRequest("Upload Failed");
//        }
//    }

    @Post(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> uploadPfp(@NotNull CompletedFileUpload file, Principal principal) {

        if (file.getSize() == 0) {
            return HttpResponse.badRequest("{\"message\": \"Greska\"}");
        }

        // TODO: Also make sure file is jpg or png since we're only checking that on the front end -- If have time that is

        try {
            userService.updateUserImage(cloudinaryConfig.getImage(file.getBytes()), principal.getName());
            return HttpResponse.ok("{\"message\": \"Slika uspesno stavljena\"}");
        } catch (IOException e) {
            return HttpResponse.badRequest("{\"message\": \"Desio se error\"}");
        }
    }
}
