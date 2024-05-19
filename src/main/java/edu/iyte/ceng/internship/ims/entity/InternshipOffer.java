package edu.iyte.ceng.internship.ims.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@Table(name = "internship_offer")
@AllArgsConstructor
@NoArgsConstructor
@AssociatedWithEntity(entityName = InternshipOffer.entityName)
public class InternshipOffer extends BaseEntity {
    public static final String entityName = "InternshipOffer";

    private String firmId ;
    private String jobTitle ;
    private String title;
    private String content ;
    private Boolean accepted ;

    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    @Setter(AccessLevel.NONE)
    private ZonedDateTime publishedDate ;

    @Column(name = "updated", nullable = false)
    @LastModifiedDate
    @Setter(AccessLevel.NONE)
    private ZonedDateTime updatedDate;

}
