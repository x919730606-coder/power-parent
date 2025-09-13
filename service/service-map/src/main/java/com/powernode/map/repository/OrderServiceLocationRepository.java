package com.powernode.map.repository;


import com.powernode.model.entity.map.OrderServiceLocation;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Repository
public interface OrderServiceLocationRepository extends MongoRepository<OrderServiceLocation, String> {

    List<OrderServiceLocation> findByOrderIdOrderByCreateTimeAsc(Long orderId);
}

