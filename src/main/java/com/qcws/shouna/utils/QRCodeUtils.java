package com.qcws.shouna.utils;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Random;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import io.jboot.Jboot;

public class QRCodeUtils {
	 
	private static final String CHARSET = "utf-8";
	
	    private static final String FORMAT_NAME = "JPG";
	    // 二维码尺寸    
	    private static final int QRCODE_SIZE = 300;
	    // LOGO宽度    
	    private static final int WIDTH = 60;
	    // LOGO高度    
	    private static final int HEIGHT = 60;

	    private static BufferedImage createImage(String content, String imgPath,
	                                             boolean needCompress) throws Exception {
	        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
	        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
	        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
	        hints.put(EncodeHintType.MARGIN, 1);
	        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
	                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
	        int width = bitMatrix.getWidth();
	        int height = bitMatrix.getHeight();
	        BufferedImage image = new BufferedImage(width, height,
	                BufferedImage.TYPE_INT_RGB);
	        for (int x = 0; x < width; x++) {
	            for (int y = 0; y < height; y++) {
	                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
	                        : 0xFFFFFFFF);
	            }
	        }
	        if (imgPath == null || "".equals(imgPath)) {
	            return image;
	        }
	        // 插入图片    
	        QRCodeUtils.insertImage(image, imgPath, needCompress);
	        return image;
	    }

	    /**
	     * 插入LOGO  
	     *
	     * @param source
	     *            二维码图片  
	     * @param imgPath
	     *            LOGO图片地址  
	     * @param needCompress
	     *            是否压缩  
	     * @throws Exception
	     */
	    private static void insertImage(BufferedImage source, String imgPath,
	                                    boolean needCompress) throws Exception {
	    	// 请求素材
	    	HttpURLConnection connection = (HttpURLConnection) new URL(imgPath).openConnection();
	    	connection.connect();
	    	InputStream inputStream = connection.getInputStream(); 
	        Image src = ImageIO.read(inputStream);
	        int width = src.getWidth(null);
	        int height = src.getHeight(null);
	        if (needCompress) { // 压缩LOGO    
	            if (width > WIDTH) {
	                width = WIDTH;
	            }
	            if (height > HEIGHT) {
	                height = HEIGHT;
	            }
	            Image image = src.getScaledInstance(width, height,
	                    Image.SCALE_SMOOTH);
	            BufferedImage tag = new BufferedImage(width, height,
	                    BufferedImage.TYPE_INT_RGB);
	            Graphics g = tag.getGraphics();
	            g.drawImage(image, 0, 0, null); // 绘制缩小后的图    
	            g.dispose();
	            src = image;
	        }
	        // 插入LOGO    
	        Graphics2D graph = source.createGraphics();
	        int x = (QRCODE_SIZE - width) / 2;
	        int y = (QRCODE_SIZE - height) / 2;
	        graph.drawImage(src, x, y, width, height, null);
	        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
	        graph.setStroke(new BasicStroke(3f));
	        graph.draw(shape);
	        graph.dispose();
	    }

	    /**
	     * 生成二维码(内嵌LOGO)  
	     *
	     * @param content
	     *            内容  
	     * @param imgPath
	     *            LOGO地址  
	     * @param destPath
	     *            存放目录  
	     * @param needCompress
	     *            是否压缩LOGO  
	     * @throws Exception
	     */
	    public static String encode(String content, String imgPath,boolean needCompress) throws Exception {
	        BufferedImage image = QRCodeUtils.createImage(content, imgPath,needCompress);
	        String file = new Random().nextInt(99999999)+".jpg";
			String host = Jboot.configValue("jboot.web.upload");
			ImageIO.write(image, FORMAT_NAME, new File(host+"/"+file));
			return Jboot.configValue("jboot.web.imghost") + file;
		}
	        
	   

	    /**
	     * 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)  
	     * @date 2013-12-11 上午10:16:36  
	     * @param destPath 存放目录  
	     */
	    public static void mkdirs(String destPath) {
	        File file =new File(destPath);
	        //当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)    
	        if (!file.exists() && !file.isDirectory()) {
	            file.mkdirs();
	        }
	    }

	    /**
	     * 生成二维码(内嵌LOGO)  
	     *
	     * @param content
	     *            内容  
	     * @param imgPath
	     *            LOGO地址  
	     * @param destPath
	     *            存储地址  
	     * @throws Exception
	     */
	    public static void encode(String content, String imgPath)
	            throws Exception {
	        QRCodeUtils.encode(content, imgPath, false);
	    }

	    /**
	     * 生成二维码  
	     *
	     * @param content
	     *            内容  
	     * @param destPath
	     *            存储地址  
	     * @param needCompress
	     *            是否压缩LOGO  
	     * @throws Exception
	     */
	    public static void encode(String content,
	                              boolean needCompress) throws Exception {
	        QRCodeUtils.encode(content, null, needCompress);
	    }

	    /**
	     * 生成二维码  
	     *
	     * @param content
	     *            内容  
	     * @param destPath
	     *            存储地址  
	     * @throws Exception
	     */
	    public static String encode(String content) throws Exception {
	        return QRCodeUtils.encode(content, null, false);
	    }

	    /**
	     * 生成二维码(内嵌LOGO)  
	     *
	     * @param content
	     *            内容  
	     * @param imgPath
	     *            LOGO地址  
	     * @param output
	     *            输出流  
	     * @param needCompress
	     *            是否压缩LOGO  
	     * @throws Exception
	     */
	    public static void encode(String content, String imgPath,
	                              OutputStream output, boolean needCompress) throws Exception {
	        BufferedImage image = QRCodeUtils.createImage(content, imgPath,
	                needCompress);
	        ImageIO.write(image, FORMAT_NAME, output);
	    }

	    /**
	     * 生成二维码  
	     *
	     * @param content
	     *            内容  
	     * @param output
	     *            输出流  
	     * @throws Exception
	     */
	    public static void encode(String content, OutputStream output)
	            throws Exception {
	        QRCodeUtils.encode(content, null, output, false);
	    }

	 

	    public static void main(String[] args) throws Exception {
	        String text = "http://www.baidu.com";  //这里设置自定义网站url
	        String logoPath = "https://thirdwx.qlogo.cn/mmopen/vi_32/xvqrLQqrnnsMzibhm56ub1oTRpfZVP0m2YlkYmibCXRd3V38J9t2Fic9fiaf8paCI1xCiaCaI834ofGFpp8BHYibcfibw/132";
	        
	        System.out.println(QRCodeUtils.encode(text, logoPath,  true));
	    }
}
