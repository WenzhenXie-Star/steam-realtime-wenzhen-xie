package com.xwz.app.ods;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import realtime.wenzhen_xie.common.utils.FlinkSinkUtil;
import realtime.wenzhen_xie.common.utils.FlinkSourceUtil;


/**
 * @Package com.xwz.retail.v1.realtime.app.ods.MysqlToKafka
 * @Author Wenzhen.Xie
 * @Date 2025/5/12 16:25、.
 * @description: MysqlToKafka
 */

public class MysqlToKafka {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        MySqlSource<String> realtimeV1 = FlinkSourceUtil.getMySqlSource("realtime_v1", "*");

        DataStreamSource<String> mySQLSource = env.fromSource(realtimeV1, WatermarkStrategy.noWatermarks(), "MySQL Source");

        mySQLSource.print();

       KafkaSink<String> topic_db = FlinkSinkUtil.getKafkaSink("Damopan_topic_db");


        mySQLSource.sinkTo(topic_db);

        env.execute("Print MySQL Snapshot + Binlog");

    }
}
