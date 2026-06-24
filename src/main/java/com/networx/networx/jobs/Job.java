package com.networx.networx.jobs;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.networx.networx.enums.Level;
import com.networx.networx.enums.Payment;
import com.networx.networx.enums.Type;
import com.networx.networx.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Level level;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Payment payment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date listingDate = new Date();

    @ToString.Exclude
    @OneToMany(mappedBy = "jobs", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Image> images;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    @JsonBackReference
    private User user;

}
