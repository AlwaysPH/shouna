package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.SysArticle;
import com.qcws.shouna.service.SysArticleService;
import com.qcws.shouna.utils.Constant;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RequiresPermissions("sys_article")
@RequestMapping("/admin/sys/article")
public class SysArticleController extends JbootController {

    @Inject
    SysArticleService sysArticleService;

    public void index(){

    }

    public void grid(){
        PageSearch pageSearch = PageSearch.instance(this);
        Page<SysArticle> page = sysArticleService.paginateByColumns(
          pageSearch.getPageNumber(),
          pageSearch.getPageSize(),
          pageSearch.getColumns("title","author","content"),
          pageSearch.getOrderBy()
        );
        renderJson(new SimpleDatagrid(page));
    }

    public void content() {
        Integer id = getParaToInt();
        set("c", sysArticleService.findById(id));
    }

    public void saveContent() {
        Integer id = getParaToInt("id");
        String content = getPara("content");
        int row = Db.update("update sys_article set content = ? where id = ?", content, id);
        renderJson(row > 0 ? Ret.ok() : Ret.fail());
    }

    public void add(){

    }

    public void edit(){
        int id = getParaToInt();
        set("s",sysArticleService.findById(id));
    }

    public void save() throws IOException {
        List<UploadFile> file = getFiles("img");
        SysArticle sysArticle = getModel(SysArticle.class,"s");
        if (!file.isEmpty()) {
            try {
                String fileName = file.get(0).getOriginalFileName();
                String type=fileName.indexOf(".")!=-1 ? fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()):null;
                if (null!= type) {// 判断文件类型是否为空
                    if ("GIF".equals(type.toUpperCase())||"PNG".equals(type.toUpperCase())||"JPG".equals(type.toUpperCase())) {
                        // 自定义的文件名称
                        String trueFileName=String.valueOf(System.currentTimeMillis())+fileName;  //15312196403485.jpg
                        // 设置存放图片文件的路径
                        String path="/data/wwwroot/default/imgfile/"+trueFileName+".jpg";
                        BufferedImage image = ImageIO.read(file.get(0).getFile());
                        //写入文件
                        ImageIO.write(image, "jpg", new File(path));

                        renderJson(Ret.ok("imgurl", "/imgfile/"+trueFileName+".jpg"));
                    }
                }
            } catch (Exception e) {
                renderJson(Ret.fail(Constant.RETMSG,"上传失败,请稍后再试"));
            }
        } else {
            renderJson(Ret.fail(Constant.RETMSG,"文件为空"));
        }
        if(sysArticle.getId() != null){
            sysArticle.setLasttime(new Date());
        }else {
            sysArticle.setAddtime(new Date());
        }
        boolean bool = sysArticle.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());
    }

    public void delete(){
        int id = getParaToInt();
        renderJson(sysArticleService.deleteById(id) ? Ret.ok() : Ret.fail());
    }

}
