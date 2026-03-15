package com.revshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "seller_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerDetails {

    @Id
    @Column(name = "seller_id")
    private Long sellerId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "seller_id")
    private User user;

    @NotBlank(message = "Business name is required")
    @Column(name = "business_name",unique = true, length = 150)
    private String businessName;

    @NotBlank(message = "GST number is required")
    @Pattern(regexp = "[0-9A-Z]{15}", message = "GST number must be exactly 15 alphanumeric characters")
    @Column(name = "gst_number", unique = true, length = 20)
    private String gstNumber;

    @NotBlank(message = "Address is required")
    @Column(name = "address", length = 255)
    private String address;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[789][0-9]{9}$", message = "Phone number must be 10 digits starting with 9, 8, or 7")
    @Column(name = "phone", unique = true, length = 15)
    private String phone;

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}