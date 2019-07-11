package com.yanxw.graffiti.pdf;

public class ImageConf {
    /**
     * 目标页数
     */
    private int pageNo;

    /**
     * 图片内容
     */
    private String imgPath;

    /**
     * 图片宽度
     */
    private float width;

    /**
     * 图片高度
     */
    private float height;

    public ImageConf() {
    }

    public ImageConf(int pageNo, String imgPath) {
        this.pageNo = pageNo;
        this.imgPath = imgPath;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getPageNo() {
        return pageNo;
    }

    public ImageConf setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public float getWidth() {
        return width;
    }

    public ImageConf setWidth(float width) {
        this.width = width;
        return this;
    }

    public float getHeight() {
        return height;
    }

    public ImageConf setHeight(float height) {
        this.height = height;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImageConf{");
        sb.append(", pageNo=").append(pageNo);
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append('}');
        return sb.toString();
    }
}