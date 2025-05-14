package com.xwz.retail.v2.reaktime.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package com.xwz.retail.v2.reaktime.bean.DimCategoryCompare
 * @Author Wenzhen.Xie
 * @Date 2025/5/14 21:10
 * @description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DimCategoryCompare {
    private Integer id;
    private String categoryName;
    private String searchCategory;
}
