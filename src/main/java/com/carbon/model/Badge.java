package com.carbon.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Entity class representing a badge earned by a user.
 * Maps to the "badges" table in the database.
 * Each row records one badge completion for one user.
 */

@Entity
@Table(name = "badges", uniqueConstraints = {
    @UniqueConstraint(name = "uq_badge_user_name", columnNames = {"user_id", "name"})
})
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who earned this badge (references users.id)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_badges_user"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private String name;

    private String imageFilename = "";

    private String contentType = "application/octet-stream";

    private long sizeBytes;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] image = new byte[0];

    @PrePersist
    private void applyInsertDefaults() {
        if (imageFilename == null) {
            imageFilename = "";
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        if (image == null) {
            image = new byte[0];
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public byte[] getImage() {
        return image;
    }

    // Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


}
