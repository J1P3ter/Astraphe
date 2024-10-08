package com.j1p3ter.productserver.config;

import com.j1p3ter.productserver.domain.product.QProduct;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "QueryDslUtil")
public class QueryDslUtil {

    public static OrderSpecifier<?> getSortedColumn(Order order, Path<?> parent, String fieldName){
        Path<Object> fieldPath = Expressions.path(Object.class, parent, fieldName);

        return new OrderSpecifier(order, fieldPath);
    }

    public static List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable){
        List<OrderSpecifier> orders = new ArrayList<>();

        if (!pageable.getSort().isEmpty()){
            for(Sort.Order order : pageable.getSort()){
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                Path path = QProduct.product;
                OrderSpecifier<?> orderSpecifier = QueryDslUtil.getSortedColumn(direction, path, order.getProperty());
                orders.add(orderSpecifier);
            }
        }

        return orders;
    }
}