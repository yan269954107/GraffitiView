/*
 * (C) Copyright 2015-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     ohun@live.cn (夜色)
 */

package com.yanxw.graffiti.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ohun on 2019-05-23.
 *
 * @author ohun@live.cn (夜色)
 */
public final class PDFImageDrawer {

    public static void main(String[] args) throws FileNotFoundException {
        List<ImageConf> imageConfs = new ArrayList<>();
        imageConfs.add(new ImageConf(1, "mark.png"));
        imageConfs.add(new ImageConf(2, "mark.png"));
        FileInputStream fileInputStream = new FileInputStream("test.pdf");
        FileOutputStream fileOutputStream = new FileOutputStream("gen_test.pdf");
        drawImage(fileInputStream, fileOutputStream, imageConfs);
    }

    public static boolean drawImage(InputStream input, OutputStream output, List<ImageConf> configs) {
        PdfReader reader = null;
        try (InputStream in = input; OutputStream out = output) {
            reader = new PdfReader(in);
            int pageNum = reader.getNumberOfPages();

            PdfStamper stamper = new PdfStamper(reader, out);
            for (ImageConf conf : configs) {
                if (conf.getPageNo() > 0 && conf.getPageNo() < pageNum + 1) {
                    drawImage(stamper, conf);
                } else {
                    System.out.println("draw image to pdf fail, can`t found target page for {}, to draw." + conf);
                }
            }
            stamper.close();
            return true;
        } catch (Exception e) {
            System.out.println("draw image to pdf error, imageConfigs={}" + configs);
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return false;
    }

    public static boolean drawImageToEveryPage(InputStream input, OutputStream output, ImageConf config) {

        PdfReader reader = null;
        try (InputStream in = input; OutputStream out = output) {
            reader = new PdfReader(in);
            PdfStamper stamper = new PdfStamper(reader, out);

            for (int i = 1, pageNum = reader.getNumberOfPages() + 1; i < pageNum; i++) {
                config.setPageNo(i);
                drawImage(stamper, config);
            }
            stamper.close();
            return true;
        } catch (Exception e) {
            System.out.println("draw image to pdf error, imageConfigs={}" + config);
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return false;
    }


    private static void drawImage(PdfStamper stamper, ImageConf config) throws DocumentException, IOException {
//        PdfContentByte content = config.isUnder()
//                ? stamper.getUnderContent(config.getPageNo())
//                : stamper.getOverContent(config.getPageNo());
        PdfContentByte content = stamper.getOverContent(config.getPageNo());

        Image image = Image.getInstance(config.getImgPath());
        float w = image.getWidth(), h = image.getHeight(), x = 0, y = 0;

        Rectangle rectangle = stamper.getReader().getPageSize(config.getPageNo());
        image.scaleToFit(rectangle);
        w = image.getScaledWidth();
        h = image.getScaledHeight();

        content.addImage(image, w, 0, 0, h, x, y);
    }
}
