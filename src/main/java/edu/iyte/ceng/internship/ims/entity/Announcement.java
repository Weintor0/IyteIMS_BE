package edu.iyte.ceng.internship.ims.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@Table(name = "announcement")
@AllArgsConstructor
@NoArgsConstructor
public class Announcement extends BaseEntity{
    @Column(name = "published", nullable = false, updatable = false)
    @CreatedDate
    @Setter(AccessLevel.NONE)
    private ZonedDateTime publishDate ;
    private String title ;
    private String context ;
    @Column(name = "attachment_URL")
    private String attachmentUrl;
}
