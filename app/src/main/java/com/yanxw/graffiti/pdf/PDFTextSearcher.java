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

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import java.io.InputStream;

/**
 * Created by ohun on 2019-05-23.
 *
 * @author ohun@live.cn (夜色)
 */
public final class PDFTextSearcher {

    public static float[] searchDocFirst(InputStream in, String keyword) {
        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(in);
            PdfReaderContentParser parser = new PdfReaderContentParser(pdfReader);
            int pageNum = pdfReader.getNumberOfPages() + 1;
            KeywordRenderListener listener = new KeywordRenderListener(keyword.toCharArray());
            for (int i = 1; i < pageNum; i++) {
                Rectangle2D.Float f = parser.processContent(i, listener.clean()).getRectangle2D();
                if (f != null) {
                    return new float[]{i, f.x, f.y, f.width, f.height};
                }
            }
        } catch (Exception e) {
            System.out.println("searchDocFirst error, subKeyword={}" + keyword);
            e.printStackTrace();
        } finally {
            if (pdfReader != null) {
                pdfReader.close();
            }
        }
        return null;
    }

    public static class KeywordRenderListener implements RenderListener {
        private char[] keywordChars;
        private int keywordLength;
        private int offset;
        private Rectangle2D.Float rectangle2D;

        public KeywordRenderListener(char[] keyword) {
            keywordChars = keyword;
            keywordLength = keywordChars.length;
        }

        // 处理数据
        public void renderText(TextRenderInfo info) {
            if (rectangle2D == null) {//未找到

                //匹配字符串
                int matchIndex = match(info.getText(), offset);

                if (matchIndex == offset) {//not match
                    offset = 0;
                } else if (matchIndex == keywordLength) {//match end
                    rectangle2D = info.getBaseline().getBoundingRectange();
                } else {//match part
                    offset = matchIndex;
                }
            }
        }

        private int match(String prefix, int offset) {
            int testIndex = offset;

            for (int i = 0, L = prefix.length(); i < L; i++) {
                if (prefix.charAt(i) == keywordChars[testIndex]) {
                    if (++testIndex == keywordLength) {
                        break;
                    }
                } else {
                    testIndex = offset;
                }
            }

            return testIndex;
        }

        public void renderImage(ImageRenderInfo arg0) {

        }

        public void endTextBlock() {

        }

        public void beginTextBlock() {

        }

        public Rectangle2D.Float getRectangle2D() {
            return rectangle2D;
        }

        public KeywordRenderListener clean() {
            rectangle2D = null;
            offset = 0;
            return this;
        }
    }

}
