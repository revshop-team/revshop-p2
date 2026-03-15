package com.revshop.repo;

import com.revshop.entity.ProductView;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductViewRepo
        extends JpaRepository<ProductView, Long> {

    List<ProductView> findByUserOrderByViewTimeDesc(User user);

}
