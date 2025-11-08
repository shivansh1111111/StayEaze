package com.business.booking.database.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "PLACES")
@Getter
@Setter
public class Place {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "PLACE_ID", updatable = false, nullable = false)
    private String placeId;

    @Column(name = "OWNER_ID")
    private String ownerId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "ADDRESS")
    private String address;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Lob
    @Column(name = "EXTRA_INFO")
    private String extraInfo;

    @Column(name = "CHECK_IN")
    private Integer checkIn;

    @Column(name = "CHECK_OUT")
    private Integer checkOut;

    @Column(name = "MAX_GUESTS")
    private Integer maxGuests;

    @Column(name = "PRICE")
    private BigDecimal price;

    // Optional: Relationships
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlacePhoto> photos;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlacePerk> perks;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
