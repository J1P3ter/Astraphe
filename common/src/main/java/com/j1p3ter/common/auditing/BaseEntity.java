package com.j1p3ter.common.auditing;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column
    private Long createdBy;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column
    private Long updatedBy;

    private LocalDateTime deletedAt;

    @Column
    private Long deletedBy;

    @Column(name = "is_delete")
    private boolean isDelete = false;

    public void softDelete(Long deletedBy){
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
        this.isDelete = true;
    }

}