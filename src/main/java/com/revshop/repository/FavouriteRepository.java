package com.revshop.repository;

import com.revshop.entity.Favourite;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    List<Favourite> findByBuyer(User buyer);
}