package realtime.wenzhen_xie.common.flink_app.dwd.realtime_dwd_trade_order_cancel_detail;

import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import realtime.wenzhen_xie.common.base.BaseSQLApp;
import realtime.wenzhen_xie.common.constant.Constant;
import realtime.wenzhen_xie.common.utils.SQLUtil;

import java.time.Duration;

/**
 * @Package com.xwz.retail.v1.realtime.flink_app.dwd.realtime_dwd_trade_order_cancel_detail.Dwd_Trade_Order_Cancel_Detail
 * @Author  Wenzhen.Xie
 * @Date  2025/4/22 13:59
 * @description: 
*/

public class Dwd_Trade_Order_Cancel_Detail extends BaseSQLApp {
    public static void main(String[] args) {
        new Dwd_Trade_Order_Cancel_Detail().start(
                10015,
                4,
                Constant.TOPIC_DWD_TRADE_ORDER_CANCEL
        );

    }
    @Override
    public void handle(StreamTableEnvironment tEnv) {
        //TODO 设置状态的保留时间
        tEnv.getConfig().setIdleStateRetention(Duration.ofSeconds(30 * 60 + 5));
        //TODO 从kafka的topic_db主题中读取数据
        readOdsDb(tEnv, Constant.TOPIC_DWD_TRADE_ORDER_CANCEL);
        //TODO 过滤出取消订单行为
        Table orderCancel = tEnv.sqlQuery("select " +
                " `data`['id'] id, " +
                " `data`['operate_time'] operate_time, " +
                " `ts` " +
                "from topic_db " +
                "where `table`='order_info' " +
                "and `type`='update' " +
                "and `old`['order_status']='1001' " +
                "and `data`['order_status']='1003' ");
        tEnv.createTemporaryView("order_cancel", orderCancel);

        //TODO 从下单事实表中获取下单数据
        tEnv.executeSql(
                "create table dwd_trade_order_detail(" +
                        "id string," +
                        "order_id string," +
                        "user_id string," +
                        "sku_id string," +
                        "sku_name string," +
                        "province_id string," +
                        "activity_id string," +
                        "activity_rule_id string," +
                        "coupon_id string," +
                        "date_id string," +
                        "create_time string," +
                        "sku_num string," +
                        "split_original_amount string," +
                        "split_activity_amount string," +
                        "split_coupon_amount string," +
                        "split_total_amount string," +
                        "ts bigint " +
                        ")" + SQLUtil.getKafkaDDL(Constant.TOPIC_DWD_TRADE_ORDER_DETAIL,Constant.TOPIC_DWD_TRADE_ORDER_CANCEL));

        //TODO 将下单事实表和取消订单表进行关联
        Table result = tEnv.sqlQuery(
                "select  " +
                        "od.id," +
                        "od.order_id," +
                        "od.user_id," +
                        "od.sku_id," +
                        "od.sku_name," +
                        "od.province_id," +
                        "od.activity_id," +
                        "od.activity_rule_id," +
                        "od.coupon_id," +
                        "date_format(oc.operate_time, 'yyyy-MM-dd') order_cancel_date_id," +
                        "oc.operate_time," +
                        "od.sku_num," +
                        "od.split_original_amount," +
                        "od.split_activity_amount," +
                        "od.split_coupon_amount," +
                        "od.split_total_amount," +
                        "oc.ts " +
                        "from dwd_trade_order_detail od " +
                        "join order_cancel oc " +
                        "on od.order_id=oc.id ");

        //TODO 将关联的结果写到kafka主题中
        tEnv.executeSql(
                "create table "+ Constant.TOPIC_DWD_TRADE_ORDER_CANCEL+"(" +
                        "id string," +
                        "order_id string," +
                        "user_id string," +
                        "sku_id string," +
                        "sku_name string," +
                        "province_id string," +
                        "activity_id string," +
                        "activity_rule_id string," +
                        "coupon_id string," +
                        "date_id string," +
                        "cancel_time string," +
                        "sku_num string," +
                        "split_original_amount string," +
                        "split_activity_amount string," +
                        "split_coupon_amount string," +
                        "split_total_amount string," +
                        "ts bigint ," +
                        "PRIMARY KEY (id) NOT ENFORCED " +
                        ")" + SQLUtil.getUpsertKafkaDDL(Constant.TOPIC_DWD_TRADE_ORDER_CANCEL));

        result.executeInsert(Constant.TOPIC_DWD_TRADE_ORDER_CANCEL);

    }
}
