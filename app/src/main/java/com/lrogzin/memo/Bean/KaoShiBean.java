package com.lrogzin.memo.Bean;

public class KaoShiBean {
    private String title;
    private String desc;
    private String daan;
    private  String xuanxiang1;
    private  String xuanxiang2;
    private  String xuanxiang3;
    private  String xuanxiang4;
    private String type;//1单选 2多选 3填空

    public KaoShiBean(String title, String desc, String daan, String xuanxiang1, String xuanxiang2, String xuanxiang3, String xuanxiang4, String type) {
        this.title = title;
        this.desc = desc;
        this.daan = daan;
        this.xuanxiang1 = xuanxiang1;
        this.xuanxiang2 = xuanxiang2;
        this.xuanxiang3 = xuanxiang3;
        this.xuanxiang4 = xuanxiang4;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDaan() {
        return daan;
    }

    public void setDaan(String daan) {
        this.daan = daan;
    }

    public String getXuanxiang1() {
        return xuanxiang1;
    }

    public void setXuanxiang1(String xuanxiang1) {
        this.xuanxiang1 = xuanxiang1;
    }

    public String getXuanxiang2() {
        return xuanxiang2;
    }

    public void setXuanxiang2(String xuanxiang2) {
        this.xuanxiang2 = xuanxiang2;
    }

    public String getXuanxiang3() {
        return xuanxiang3;
    }

    public void setXuanxiang3(String xuanxiang3) {
        this.xuanxiang3 = xuanxiang3;
    }

    public String getXuanxiang4() {
        return xuanxiang4;
    }

    public void setXuanxiang4(String xuanxiang4) {
        this.xuanxiang4 = xuanxiang4;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
