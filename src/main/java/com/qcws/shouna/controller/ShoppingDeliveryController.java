package com.qcws.shouna.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.qcws.shouna.dto.DbPageSearch;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.model.ItemInfo;
import com.qcws.shouna.model.ShoppingOrderEnum;
import com.qcws.shouna.model.ShoppingOrderItem;
import com.qcws.shouna.service.ItemInfoService;
import com.qcws.shouna.service.ShoppingOrderItemService;
import com.qcws.shouna.utils.DateUtil;
import com.qcws.shouna.utils.UploadUtil;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 配送管理
 */
@RequiresPermissions("shopping_delivery")
@RequestMapping("/admin/shopping/delivery")
@Slf4j
public class ShoppingDeliveryController extends JbootController {

    @Inject
    private ShoppingOrderItemService orderItemService;

    @Inject
    private ItemInfoService itemInfoService;

    public void index(){
        set("cd",orderItemService.findAll());
    }

    public void grid(){
        DbPageSearch dbPageSearch = DbPageSearch.instance(this);
        dbPageSearch.select("select co.*, ci.nickname, s.content color, c.content size, i.name itemName");
        dbPageSearch.from("from t_shop_order_item co LEFT JOIN customer_info ci ON co.customer_id = ci.id LEFT JOIN item_info i on co.item_id = i.id" +
                " LEFT JOIN sys_config s on i.color_id = s.id LEFT JOIN sys_config c on i.size_id = c.id");
        dbPageSearch.addExcept(" co.settle_status= ?", DbPageSearch.EXIST_VAL, ShoppingOrderEnum.PAID.getValue());
        dbPageSearch.addExcept(" and co.status= ?", DbPageSearch.EXIST_VAL, ShoppingOrderEnum.AWAIT_SHIP.getValue());
        dbPageSearch.addExcept(" and (co.telphone like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept("or co.order_no = ?");
        dbPageSearch.addExcept("or i.name like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept("or co.addtime like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept("or ci.nickname like ?)", DbPageSearch.ALLLIKE);
        dbPageSearch.orderBy("co.addtime desc");
        renderJson(dbPageSearch.toDataGrid());
    }

    public void send(){
        Integer id = getParaToInt();
        ShoppingOrderItem order = orderItemService.findById(id);
        if(null == order){
            renderHtml(new MessageBox(false).toString());
            return;
        }
        if(!order.getStatus().equals(ShoppingOrderEnum.AWAIT_SHIP.getValue())
            || !order.getSettleStatus().equals(ShoppingOrderEnum.PAID.getValue())){
            renderHtml(new MessageBox(false).toString());
            return;
        }
        order.setStatus(ShoppingOrderEnum.AWAIT_RECEIPT.getValue());
        //收货最后时间为发货后7天
        Calendar calendar = Calendar.getInstance();//日历对象
        calendar.setTime(new Date());//设置当前日期
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        order.setCountdown(calendar.getTime());
        renderHtml(new MessageBox(order.saveOrUpdate()).toString());
    }

    public void exportPanel(){
        String sql = "select *from item_info where type = '2' GROUP BY name";
        List<Record> item = Db.find(sql);
        set("i", item);
    }

    public void export(){
        ItemInfo itemInfo = getModel(ItemInfo.class,"c");
        String name = itemInfo.getName();
        String sql = "select co.order_no orderNo, i.name itemName, co.item_number itemNumber, s.content color, c.content size, ca.realname realname, " +
                "ca.telphone telephone, ca.province province, ca.city city, ca.address address " +
                "from t_shop_order_item co LEFT JOIN customer_info ci ON co.customer_id = ci.id LEFT JOIN item_info i on co.item_id = i.id " +
                "LEFT JOIN sys_config s on i.color_id = s.id LEFT JOIN sys_config c on i.size_id = c.id " +
                "LEFT JOIN customer_address ca on ca.id = co.address_id where co.`status` = '1' and co.settle_status='1' and i.`name` = '" + name + "'";
        List<Record> list = Db.find(sql);
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        String[] headers = { "订单号", "商品名称", "数量", "颜色", "大小", "收件人", "联系电话", "省份", "城市", "详细地址"};
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        //设置列宽
        sheet.setDefaultColumnWidth((short) 15);
        //创建第一行的对象，第一行一般用于填充标题内容。从第二行开始一般是数据
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            //创建单元格，每行多少数据就创建多少个单元格
            HSSFCell cell = row.createCell(i);
            HSSFCellStyle style = workbook.createCellStyle();
            //设置背景颜色
            style.setFillForegroundColor(HSSFColor.GREEN.index);
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cell.setCellStyle(style);
            //给单元格设置内容
            cell.setCellValue(new HSSFRichTextString(headers[i]));

        }
        for (int i = 0; i < list.size(); i++) {
            Record record = list.get(i);
            //从第二行开始填充数据
            row = sheet.createRow(i+1);
            int j = 0;
            HSSFCell cell = row.createCell(j++);
            cell.setCellValue(record.getStr("orderNo")); // 订单号

            cell = row.createCell(j++);
            cell.setCellValue(record.getStr("itemName")); // 商品名称

            cell = row.createCell(j++);
            cell.setCellValue(record.getStr("itemNumber")); // 数量

            cell = row.createCell(j++);
            cell.setCellValue(record.getStr("color")); // 颜色

            cell = row.createCell(j++);
            cell.setCellValue(record.getStr("size")); // 大小

            cell = row.createCell(j++);
            cell.setCellValue(record.getStr("realname")); // 收件人

            cell = row.createCell(j++);
            cell.setCellValue(record.getStr("telephone")); // 联系电话

            cell = row.createCell(j++);
            cell.setCellValue(record.getStr("province")); // 省份

            cell = row.createCell(j++);
            cell.setCellValue(record.getStr("city")); // 城市

            cell = row.createCell(j++);
            cell.setCellValue(record.getStr("address")); // 详细地址
        }

        try {
            HttpServletResponse response = getResponse();;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + getfileName(name));//Excel文件名
            response.flushBuffer();
            //将workbook中的内容写入输出流中
            workbook.write(response.getOutputStream());
        }catch (Exception e){
            log.error("导出数据失败", e);
        }
        renderNull();
    }

    private String getfileName(String name) throws UnsupportedEncodingException {
        String date = DateUtil.getDateToString(new Date(), "yyyyMMdd");
        String data = name + "_" + date + ".xls";
        return new String(data.getBytes("utf-8"), "iso8859-1");
    }

    public void importPanel(){}

    public void importData(){
        Boolean bool = true;
        UploadFile file = getFile("fileName");
        String fileName = UploadUtil.uploadFile(file);
        //缓存文件
        File newFile = new File(fileName);
        try{
            FileInputStream fs = FileUtils.openInputStream(newFile);
            HSSFWorkbook workbook=new HSSFWorkbook(fs);
            //获取第一个工作表
            HSSFSheet hs = workbook.getSheetAt(0);
            //获取Sheet的第一个行号和最后一个行号
            int last = hs.getLastRowNum();
            //从第二行开始读取数据
            int first = hs.getFirstRowNum() + 1;
            //遍历获取单元格里的信息
            List<Map<String, Object>> list = Lists.newArrayList();
            for (int i = first; i <= last; i++) {
                HSSFRow row = hs.getRow(i);
                int firstCellNum = row.getFirstCellNum();
                int lastCellNum = row.getLastCellNum();//获取所在行的最后一个行号,是从0开始
                HSSFCell orderNoCell = row.getCell(firstCellNum);
                HSSFCell exCell = row.getCell(lastCellNum - 1);
                if(null == exCell){
                    continue;
                }
                exCell.setCellType(Cell.CELL_TYPE_STRING);
                //订单号
                String orderNo = orderNoCell.getStringCellValue();
                //快递单号
                String exNo = exCell.getStringCellValue();
                Map<String, Object> map = Maps.newHashMap();
                map.put("orderNo", orderNo);
                map.put("exNo", exNo);
                list.add(map);
            }
            //快递单号入库
            insertExNo(list);

            //删除缓存文件
            if(newFile.exists()) {
                newFile.delete();
            }
        }catch (Exception e){
            log.error("上传文件失败", e);
            bool = false;
        }
        renderHtml(new MessageBox(bool).toString());
    }

    private void insertExNo(List<Map<String, Object>> list) {
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        List<String> sqlList = Lists.newArrayList();
        for(Map<String, Object> map : list){
            sqlList.add("update t_shop_order_item set exNo = " + map.get("exNo") + ", status = '2' where order_no = " + map.get("orderNo"));
        }
        Db.batch(sqlList, list.size());
    }
}
