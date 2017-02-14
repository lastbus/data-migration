package com.bl.db.admin.entity;

/**
 * Created by MK33 on 2016/10/9.
 */
public class TestEnity {
    private String gId;
    private int chan;

    public TestEnity(String gId, int chan) {
        this.gId = gId;
        this.chan = chan;
    }

    public void setChan(int chan) {
        this.chan = chan;
    }

    public void setgId(String gId) {
        this.gId = gId;
    }

    public String getgId() {
        return gId;
    }

    public int getChan() {
        return chan;
    }
}
