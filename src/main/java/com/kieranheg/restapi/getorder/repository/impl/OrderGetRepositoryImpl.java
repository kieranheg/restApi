package com.kieranheg.restapi.getorder.repository.impl;

import com.kieranheg.restapi.getorder.model.Order;
import com.kieranheg.restapi.getorder.repository.OrderGetRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
class OrderGetRepositoryImpl implements OrderGetRepository {
    
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    
    public OrderGetRepositoryImpl(final JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        
        // Build a SimpleJdbcInsert object from the specified data source
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("orders")
                .usingGeneratedKeyColumns("id");
    }
    
    @Override
    public Optional<Order> findById(final Integer id) {
        try {
            Order order = jdbcTemplate.queryForObject("SELECT * FROM orders WHERE id = ?",
                    new Object[]{id},
                    (rs, rowNum) -> {
                        return Order.builder()
                                .id(rs.getInt("id"))
                                .name(rs.getString("name"))
                                .quantity(rs.getInt("quantity"))
                                .build();
                    });
            return Optional.of(order);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
