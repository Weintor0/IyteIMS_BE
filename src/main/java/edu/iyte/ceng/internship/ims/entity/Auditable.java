package edu.iyte.ceng.internship.ims.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.ZonedDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
public class Auditable implements Serializable {

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private ZonedDateTime createdDate;

    @Column(name = "modified_date", nullable = false)
    @LastModifiedDate
    private ZonedDateTime modifiedDate;
}
