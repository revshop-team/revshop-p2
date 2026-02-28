package com.revshop.entity;

import jakarta.persistence.*;
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

    @Column(name = "business_name", length = 150)
    private String businessName;

    @Column(name = "gst_number", unique = true, length = 20)
    private String gstNumber;

    @Column(name = "address", length = 255)
    private String address;

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