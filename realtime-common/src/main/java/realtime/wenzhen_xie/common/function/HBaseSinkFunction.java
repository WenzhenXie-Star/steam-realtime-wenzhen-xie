package realtime.wenzhen_xie.common.function;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.client.Connection;
import realtime.wenzhen_xie.common.bean.TableProcessDim;
import realtime.wenzhen_xie.common.constant.Constant;
import realtime.wenzhen_xie.common.utils.HBaseUtil;
import realtime.wenzhen_xie.common.utils.RedisUtil;
import redis.clients.jedis.Jedis;

/**
 * @Package realtime.wenzhen_xie.common.function.HBaseSinkFunction
 * @Author Wenzhen.Xie
 * @Date 2025/4/11 9:11
 * @description: HBaseSinkFunction
 */


public class HBaseSinkFunction extends RichSinkFunction<Tuple2<JSONObject, TableProcessDim>> {
    private Connection hbaseConn;
    private Jedis jedis;

    @Override
    public void open(Configuration parameters) throws Exception {
        hbaseConn = HBaseUtil.getHBaseConnection();
        jedis = RedisUtil.getJedis();
    }

    @Override
    public void close() throws Exception {
        HBaseUtil.closeHBaseConnection(hbaseConn);
        RedisUtil.closeJedis(jedis);
    }

    @Override
    public void invoke(Tuple2<JSONObject, TableProcessDim> tup, Context context) {
        JSONObject jsonObj = tup.f0;
        TableProcessDim tableProcessDim = tup.f1;
        String type = jsonObj.getString("op");
        jsonObj.remove("op");

        String sinkTable = tableProcessDim.getSinkTable();

        String rowKey = jsonObj.getString(tableProcessDim.getSinkRowKey());

        if("d".equals(type)){
            HBaseUtil.delRow(hbaseConn, Constant.HBASE_NAMESPACE,sinkTable,rowKey);
        }else{
            String sinkFamily = tableProcessDim.getSinkFamily();
            HBaseUtil.putRow(hbaseConn, Constant.HBASE_NAMESPACE,sinkTable,rowKey,sinkFamily,jsonObj);
        }
        if("u".equals(type)||"d".equals(type)){
            String key = RedisUtil.getKey(sinkTable, rowKey);
            jedis.del(key);
        }
    }


}

