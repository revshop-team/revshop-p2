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

    /**
     * @param seller
     * @return List<Product>
     */
    List<Product> findBySeller(User seller);

    /**
     * @param isActive
     * @param pageable
     * @return Page<Product>
     */
    Page<Product> findByIsActive(Integer isActive, Pageable pageable);

    /**
     * @param id
     * @return Optional<Product>
     */
    Optional<Product> findById(Long id);



}
