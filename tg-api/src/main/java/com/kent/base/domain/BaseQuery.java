package com.kent.base.domain;


import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * sorter 的用法:
 * client 端要以异动日期 (`modifiedDate`) 做 DESC 排序是, client 只需传入 `items?sorters=modifiedDate-`
 * 后台的 query 这边, 写法如下
 * <code>
 * Map<String, String> nameMap = Map.of("modifiedDate", "ITEM.MODI_DATE"); // client 是 modifiedDate, 但是 DB 是 MODI_DATE,所以需要透过 nameMap 做对应
 * IPage<Map<String, Object>> all = service.findAll(query.toPage(nameMap), wrapper); // query.toPage(nameMap) 会做出有排序跟换页功能的 page 物件, 存入 MyBatis 的 service 即可
 * </code>
 */
public class BaseQuery {

    @Schema(description = "当前页 (1-based)", example = "1")
    private Integer pageNum = 1;
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
    @Schema(description = "排序 ex: no+,name-，在属性后面加上'+'表示正向排序（ASC）; '-' 表示反向排序（DESC），多个属属性间用逗号'，'分隔, ex: no+,name- 属性的顺序即为排序的顺序")
    private String sorters;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSorters() {
        return sorters;
    }

    public void setSorters(String sorters) {
        this.sorters = sorters;
    }


    /**
     * 将 query 物件转为 Mybatis Plus 的 IPage 格式, 可以让 Mybatis Plus 对查询条件有分页或排序对效果
     *
     * @return Mybatis Plus 的 IPage
     */
    public Page toPage() {
        return toPage(Map.of());
    }

    /**
     * 将 query 物件转为 Mybatis Plus 的 IPage 格式
     *
     * @param nameMap 将 client 端传入对 field name 转成 Query Wrapper 可以使用的格式
     * @return Mybatis Plus 的 IPage
     */
    public Page toPage(Map<String, String> nameMap) {
        Page<Object> page = new Page<>(getPageNum(), getPageSize());
        String sorters = this.getSorters();
        if (StringUtils.isNoneBlank(sorters)) {
            page.setOrders(getOrderItems(nameMap, sorters));
        }
        return page;
    }

    /**
     * Sort String -> MyBatis Order Items
     *
     * @param nameMap 将 client 端传入对 field name 转成 Query Wrapper 可以使用的格式
     * @param sorters Mybatis Plus 的 OrderItem
     * @return Mybatis Plus 的 OrderItem
     */
    public List<OrderItem> getOrderItems(Map<String, String> nameMap, String sorters) {
        List<OrderItem> orderItems = Arrays.stream(sorters.split(",")).map(value -> {
            OrderItem item = new OrderItem();
            if (StringUtils.endsWith(value, "+")) {
                item.setAsc(true);
                String fieldName = StringUtils.substringBeforeLast(value, "+").trim();
                item.setColumn(nameMap.getOrDefault(fieldName, fieldName));
            } else if (StringUtils.endsWith(value, "-")) {
                item.setAsc(false);
                String fieldName = StringUtils.substringBeforeLast(value, "-").trim();
                item.setColumn(nameMap.getOrDefault(fieldName, fieldName));
            } else {
                String fieldName = value.trim();
                item.setColumn(nameMap.getOrDefault(fieldName, fieldName));
            }
            return item;
        }).collect(Collectors.toList());
        return orderItems;
    }
}
