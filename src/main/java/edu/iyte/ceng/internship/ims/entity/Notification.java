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
@AssociatedWithEntity(entityName = Notification.entityName)
public class Notification extends BaseEntity {
    public static final String entityName = "Notification";

    @ManyToOne
    @JoinColumn(name = "source_user_id", referencedColumnName = "id")
    private User sourceUser;

    @ManyToOne
    @JoinColumn(name = "destination_user_id", referencedColumnName = "id")
    private User destinationUser;

    @NotNull
    @Column(name = "send_date")
    private Date sendDate;

    @NotNull
    @Column(name = "read")
    private Boolean read;

    @NotBlank
    @Column(name = "content")
    private String content;
}
