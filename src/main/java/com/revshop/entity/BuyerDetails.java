package com.revshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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


    @NotBlank(message = "Username is required")
    @Column(name = "full_name", unique = true, nullable = false,length = 100)
    private String fullName;


    @NotBlank(message = "Gender is required")
    @Column(name = "gender", length = 10)
    private String gender;

    @NotNull(message = "Date of birth is required")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^[789][0-9]{9}$", message = "Phone number must be 10 digits starting with 9, 8, or 7")
    @Column(name = "phone", unique = true, length = 15)
    private String phone;

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}