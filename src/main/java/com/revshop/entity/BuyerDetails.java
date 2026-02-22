package com.revshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "buyer_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerDetails {

    @Id
    @Column(name = "buyer_id")
    private Long buyerId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "buyer_id")
    private User user;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone", unique = true, length = 15)
    private String phone;
}