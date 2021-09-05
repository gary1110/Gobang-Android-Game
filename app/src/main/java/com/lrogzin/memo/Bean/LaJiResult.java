package com.lrogzin.memo.Bean;

import java.util.List;

public class LaJiResult {
    /**
     * ret : 200
     * data : [{"score":0.55761,"keyword":"模糊图片"},{"score":0.402902,"keyword":"亚光砖"},{"score":0.266364,"keyword":"釉面砖"}]
     * qt : 1.318
     */

    private int ret;
    private double qt;
    private List<DataBean> data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public double getQt() {
        return qt;
    }

    public void setQt(double qt) {
        this.qt = qt;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * score : 0.55761
         * keyword : 模糊图片
         */

        private double score;
        private String keyword;

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }
    }
}
