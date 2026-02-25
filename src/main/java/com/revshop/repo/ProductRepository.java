package com.revshop.repo;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySeller(User seller);

    Page<Product> findByIsActive(Integer isActive, Pageable pageable);

    Optional<Product> findById(Long id);



}
