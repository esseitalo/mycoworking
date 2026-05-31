package com.mycoworking.app.model;

import java.sql.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

  @Column(updatable = false)
  @CreatedDate
  protected Date createdDate;

  @Column(updatable = false)
  @LastModifiedDate
  protected Date lastModifiedDate;
}
