package org.gora.server.model.entity;


import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_privilege")
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RolePrivilegeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;
    @ManyToOne
    @JoinColumn(name = "role_seq")
    private RoleEntity role;
    @ManyToOne
    @JoinColumn(name = "privilege_seq")
    private PrivilegeEntity privilege;
    @CreationTimestamp
    @Column
    private Date createdAt;
    @UpdateTimestamp
    @Column
    private Date updatedAt;
    @Column
    private Long createdBy;
    @Column
    private Long updatedBy;
}
