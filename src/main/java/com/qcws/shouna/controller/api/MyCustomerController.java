package com.qcws.shouna.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.support.swagger.ParamType;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 我的客户
 */
@RequestMapping("/api/myCustomer")
@Api(tags = "我的客户")
public class MyCustomerController extends ApiController{

    //今日新增
    private static final String NEW_TYPE = "0";

    @ApiOperation(value = "我的客户列表",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type",value = "标识是否是今日新增还是累计客户 0 今日新增  1 累计新增",paramType = ParamType.QUERY,required = true)
    })
    public void getCustomerList(){
        String type = getPara("type");
        Integer id = getLoginCustomer().getId();
        List<Record> infoList = new ArrayList<>();
        List<Record> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String ids = "";
        if(NEW_TYPE.equals(type)){
            infoList = Db.find("SELECT id,nickname,headimg FROM(SELECT t1.id, IF (find_in_set(pid, @pids) > 0, @pids := concat(@pids, ',', id), 0) AS ischild, t1.regtime,t1.nickname,t1.headimg FROM(SELECT id, pid, regtime, nickname, headimg FROM customer_info WHERE STATUS = '0' ORDER BY pid, id) t1, (SELECT @pids := ?) t2) t3 WHERE ischild != 0 and to_days(regtime) = to_days(now())", id);
            ids = getIdString(sb, infoList);
            list = Db.find("select count(*) pcount,SUM(t.price) amount,MAX(t.overtime) time,t.customer_id customerId, c.nickname name, c.headimg headImg from customer_order t LEFT JOIN customer_info c on t.customer_id = c.id where t.customer_id in ("+ids+") and t.status = 'finish' and t.settle_status = 'finish' AND to_days(t.overtime) = to_days(now()) and to_days(t.addtime) = TO_DAYS(now()) GROUP BY t.customer_id");
//            page = Db.paginate(pageNum, pageSize, "select count(*) pcount,SUM(t.price) amount,MAX(t.overtime) time,t.customer_id customerId, c.nickname name, c.headimg headImg", "from customer_order t LEFT JOIN customer_info c on t.customer_id = c.id where t.customer_id in ("+ids+") and t.status = 'finish' and t.settle_status = 'finish' AND to_days(t.overtime) = to_days(now()) and to_days(t.addtime) = TO_DAYS(now()) GROUP BY t.customer_id");
        }else {
            infoList = Db.find("SELECT id,nickname,headimg FROM(SELECT t1.id, IF (find_in_set(pid, @pids) > 0, @pids := concat(@pids, ',', id), 0) AS ischild, t1.regtime,t1.nickname,t1.headimg FROM(SELECT id, pid, regtime, nickname, headimg FROM customer_info WHERE STATUS = '0' ORDER BY pid, id) t1, (SELECT @pids := ?) t2) t3 WHERE ischild != 0", id);
            ids = getIdString(sb, infoList);
            list = Db.find("SELECT count(*) pcount, SUM(t.price) amount, MAX(t.overtime) time, t.customer_id customerId, c.nickname name, c.headimg headImg from customer_order t LEFT JOIN customer_info c on t.customer_id = c.id WHERE t.customer_id in ("+ids+") and t.status = 'finish' AND t.settle_status = 'finish' GROUP BY t.customer_id");
//            page = Db.paginate(pageNum, pageSize, "select count(*) pcount,SUM(t.price) amount,MAX(t.overtime) time,t.customer_id customerId, c.nickname name, c.headimg headImg", "from customer_order t LEFT JOIN customer_info c on t.customer_id = c.id where t.customer_id in ("+ids+") and t.status = 'finish' and t.settle_status = 'finish' GROUP BY t.customer_id");
        }
        dealwithData(infoList, list);
        renderJson(Ret.ok("data",list));
    }

    @ApiOperation(value = "新增和累计数",httpMethod = "GET")
    public void getCustomerNumber(){
        Integer id = getLoginCustomer().getId();
        List<Record> newList = Db.find("SELECT id,nickname,headimg FROM(SELECT t1.id, IF (find_in_set(pid, @pids) > 0, @pids := concat(@pids, ',', id), 0) AS ischild, t1.regtime,t1.nickname,t1.headimg FROM(SELECT id, pid, regtime, nickname, headimg FROM customer_info WHERE STATUS = '0' ORDER BY pid, id) t1, (SELECT @pids := ?) t2) t3 WHERE ischild != 0 and to_days(regtime) = to_days(now())", id);
        List<Record> allList = Db.find("SELECT id,nickname,headimg FROM(SELECT t1.id, IF (find_in_set(pid, @pids) > 0, @pids := concat(@pids, ',', id), 0) AS ischild, t1.regtime,t1.nickname,t1.headimg FROM(SELECT id, pid, regtime, nickname, headimg FROM customer_info WHERE STATUS = '0' ORDER BY pid, id) t1, (SELECT @pids := ?) t2) t3 WHERE ischild != 0", id);
        JSONObject result = new JSONObject();
        result.put("today", newList.size());
        result.put("total", allList.size());
        renderJson(Ret.ok("data",result));
    }

    private void dealwithData(List<Record> infoList, List<Record> list) {
        if(CollectionUtils.isEmpty(infoList)){
            return;
        }
        if(CollectionUtils.isEmpty(list)){
            for(Record info : infoList){
                Record record = new Record();
                record.set("name", info.getStr("nickname"));
                record.set("headImg", info.getStr("headimg"));
                record.set("customerId", info.getInt("id"));
                record.set("pcount", 0);
                record.set("amount", new BigDecimal(0));
                record.set("time", "");
                list.add(record);
            }
        }else {
            List<Integer> idList = list.stream().map(e -> e.getInt("customerId")).collect(Collectors.toList());
            for(Record info : infoList){
                if(!idList.contains(info.get("id"))){
                    Record record = new Record();
                    record.set("name", info.getStr("nickname"));
                    record.set("headImg", info.getStr("headimg"));
                    record.set("customerId", info.getInt("id"));
                    record.set("pcount", 0);
                    record.set("amount", new BigDecimal(0));
                    record.set("time", "");
                    list.add(record);
                }
            }
        }
    }

    private String getIdString(StringBuilder sb, List<Record> idList) {
        if(CollectionUtils.isEmpty(idList)){
            return null;
        }
        for(Record i : idList){
            sb.append(",").append("'").append(i.getStr("id")).append("'");
        }
        if(sb.length() > 0){
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }
}
