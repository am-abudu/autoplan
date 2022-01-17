package com.oldwu.security.validate;

import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@RestController
@RequestMapping("/code")
public class ValidateCodeController {

    public final static String SESSION_KEY_IMAGE_CODE = "SESSION_KEY_IMAGE_CODE";

    //使用sessionStrategy将生成的验证码对象存储到Session中，并通过IO流将生成的图片输出到登录页面上。
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @RequestMapping("/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //生成验证码对象
        ImageCode imageCode = createImageCode();
        //生成的验证码对象存储到Session中
        sessionStrategy.setAttribute(new ServletWebRequest(request),SESSION_KEY_IMAGE_CODE,imageCode);
        //通过IO流将生成的图片输出到登录页面上
        ImageIO.write(imageCode.getImage(), "jpeg", response.getOutputStream());
    }

    /**
     * 用于生成验证码对象
     * @return
     */
    private ImageCode createImageCode() {

        int width = 100;    // 验证码图片宽度
        int height = 36;    // 验证码图片长度
        int length = 4;     // 验证码位数
        int expireIn = 60;  // 验证码有效时间 60s

        //创建一个带缓冲区图像对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //获得在图像上绘图的Graphics对象
        Graphics g = image.getGraphics();

        Random random = new Random();

        Font font = new Font("Times New Roman", Font.ITALIC, 30);
        //设置颜色、并随机绘制直线
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(font);
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        //生成随机数 并绘制
        StringBuilder sRand = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand.append(rand);
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));

            FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
            int strHeight = metrics.getHeight();
            //顶边位置+上升距离（原本字体基线位置对准画布的y坐标导致字体偏上ascent距离，加上ascent后下移刚好顶边吻合）
            int top = (height - strHeight) / 2 + metrics.getAscent();

            if (i % 2 == 0) {
                g.drawString(rand, 18 * i + 14, top + i%3);
            } else {
                g.drawString(rand, 18 * i + 14, top - i%3);
            }

        }
        g.dispose();
        return new ImageCode(image, sRand.toString(), expireIn);
    }

    /**
     * 获取随机演示
     * @param fc
     * @param bc
     * @return
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

}