package realtime.wenzhen_xie.common.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Package com.xwz.retail.v1.realtime.bean.CartAddUuBean
 * @Author  Wenzhen.Xie
 * @Date  2025/4/8 13:51
 * @description:
 */
@Data
@AllArgsConstructor
public class TrafficHomeDetailPageViewBean {
    // 窗口起始时间
    String stt;
    // 窗口结束时间
    String edt;
    // 当天日期
    String curDate;
    // 首页独立访客数
    Long homeUvCt;
    // 商品详情页独立访客数
    Long goodDetailUvCt;
    // 时间戳
    @JSONField(serialize = false)
    Long ts;
}
