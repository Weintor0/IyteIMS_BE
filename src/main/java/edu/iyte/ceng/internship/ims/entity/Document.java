package edu.iyte.ceng.internship.ims.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@AssociatedWithEntity(entityName = Document.entityName)
public class Document extends BaseEntity {
    public static final String entityName = "Document";

    @ManyToOne
    @JoinColumn(name = "source_user_id", referencedColumnName = "id")
    private User sourceUser;

    @ManyToOne
    @JoinColumn(name = "destination_user_id", referencedColumnName = "id")
    private User destinationUser;

    @NotNull
    @Column(name = "upload_date")
    private Date uploadDate;

    @NotBlank
    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "content", length = 1024 * 1024 * 16)
    private byte[] content;
}
