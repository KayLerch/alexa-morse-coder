package io.klerch.morse.service;

import io.klerch.morse.model.MorseCode;
import io.klerch.morse.utils.ImageUtils;
import io.klerch.morse.utils.S3Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path("/encode")
public class EncodeService {
    @Autowired
    private ImageUtils imageUtils;

    @Autowired
    private S3Utils s3Utils;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public MorseCode getMorse(@QueryParam("text") String text, @QueryParam("wpm") Integer wpm, @QueryParam("fw") Integer fw) {
        return new MorseCode(s3Utils, imageUtils).load(text, wpm, fw != null ? fw : wpm);
    }
}
