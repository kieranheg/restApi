package com.kieranheg.restapi.getorder.controller;

import com.kieranheg.restapi.auxiliary.exception.RestExceptionHandler;
import com.kieranheg.restapi.getorder.model.Order;
import com.kieranheg.restapi.getorder.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderController_UT {
    private static final String CAN_FIND_ID = "1234567890";
    private static final String NOT_FOUND_ID = "1737737737";
    private static final String BAD_PARAM_ID = "26";
    
    @Mock
    private OrderService orderService;
    
    @InjectMocks
    private OrderController orderController;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    public void BeforeEach() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).setControllerAdvice(new RestExceptionHandler()).build();
    }
    
    @Test
    @DisplayName("GET for valid order id - Ok")
    void givenValidOrderIdReturnsOrder() throws Exception {
        Order mockOrder = Order.builder().id(CAN_FIND_ID).name("Sample Order").quantity(99).build();
        given(orderService.findById(CAN_FIND_ID)).willReturn(Optional.of(mockOrder));
    
        mockMvc.perform(get("/order/{id}", CAN_FIND_ID)
                .contentType(APPLICATION_JSON))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/order/1234567890"))
                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(CAN_FIND_ID)))
                .andExpect(jsonPath("$.name", is("Sample Order")))
                .andExpect(jsonPath("$.quantity", is(99)));
    }
    
    @Test
    @DisplayName("GET for non existent order id - Not Found")
    void givenNonExistentOrderIdReturnsOrderNotFound() throws Exception {
        given(orderService.findById(NOT_FOUND_ID)).willReturn(Optional.empty());
        
        mockMvc.perform(get("/order/{id}", NOT_FOUND_ID)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("GET for invalid order id - Bad request")
    void givenInvalidOrderIdReturnsOrderNotFound() throws Exception {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));
        ConstraintViolationException constraintViolationException = mock(ConstraintViolationException.class);
        
        given(orderService.findById(BAD_PARAM_ID)).willThrow(constraintViolationException);
        
        mockMvc.perform(get("/order/{id}", BAD_PARAM_ID)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}