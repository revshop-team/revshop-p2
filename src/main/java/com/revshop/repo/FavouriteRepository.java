package com.revshop.repo;

import com.revshop.entity.Favourite;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    /**
     * @param buyer
     * @return List<Favourite>
     */
    List<Favourite> findByBuyer(User buyer);

    /**
     * @param buyer
     * @param product
     * @return Optional<Favourite>
     */
    Optional<Favourite> findByBuyerAndProduct(User buyer, Product product);

    /**
     * @param buyer
     * @param product
     */
    void deleteByBuyerAndProduct(User buyer, Product product);

}
