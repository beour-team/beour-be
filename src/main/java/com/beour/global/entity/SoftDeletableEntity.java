package com.beour.global.entity;

import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class SoftDeletableEntity {

    protected LocalDateTime deletedAt;

    public void softDelete(){
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted(){
        return this.deletedAt != null;
    }

    public LocalDateTime getDeletedAt(){
        return deletedAt;
    }
}
