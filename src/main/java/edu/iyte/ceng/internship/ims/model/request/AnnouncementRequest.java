package edu.iyte.ceng.internship.ims.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnnouncementRequest {
    @NotNull(message = "title can't be blank")
    private String title;
    @NotNull(message = "context can't be blank")
    private String context;
    @NotNull(message = "url can't be blank")
    private String attachmentUrl;
}
