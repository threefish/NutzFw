package com.nutzfw.core.common.util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/3/8
 * 描述此类：
 */
public class ImgUtil {

    public static BufferedImage createImgByStr(String msg) {
        int width = msg.length() * 20 + 20;
        int height = 50;
        // 图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        // 将图像填充为白色
        g.setColor(Color.pink);
        g.fillRect(0, 0, width, height);
        // 创建字体
        Font font = getFont(14);
        g.setFont(font);
        char[] s = msg.toCharArray();
        for (int i = 0; i < s.length; i++) {
            // 设置字体位置
            g.setColor(new Color(0, 0, 0));
            g.drawString(String.valueOf(s), 20, 15 + 20);
        }
        return buffImg;
    }


    public static Font getFont(int fontHeight) {
        Font baseFont = new Font(Font.DIALOG, Font.LAYOUT_LEFT_TO_RIGHT, 18);
        return baseFont.deriveFont(Font.PLAIN, fontHeight);
    }
}
