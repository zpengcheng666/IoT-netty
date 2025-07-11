/*    */ package com.sydh.common.utils.gateway.mq;

public class Topics {
    private String bb;
    private Integer bc = 0;
    private String desc;

    public Topics() {
    }

    public String getTopicName() {
        return this.bb;
    }

    public Integer getQos() {
        return this.bc;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setTopicName(String topicName) {
        this.bb = topicName;
    }

    public void setQos(Integer qos) {
        this.bc = qos;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Topics)) {
            return false;
        } else {
            Topics var2 = (Topics)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Integer var3 = this.getQos();
                    Integer var4 = var2.getQos();
                    if (var3 == null) {
                        if (var4 == null) {
                            break label47;
                        }
                    } else if (var3.equals(var4)) {
                        break label47;
                    }

                    return false;
                }

                String var5 = this.getTopicName();
                String var6 = var2.getTopicName();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                String var7 = this.getDesc();
                String var8 = var2.getDesc();
                if (var7 == null) {
                    if (var8 != null) {
                        return false;
                    }
                } else if (!var7.equals(var8)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Topics;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        Integer var3 = this.getQos();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        String var4 = this.getTopicName();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        String var5 = this.getDesc();
        var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
        return var2;
    }

    public String toString() {
        return "Topics(topicName=" + this.getTopicName() + ", qos=" + this.getQos() + ", desc=" + this.getDesc() + ")";
    }
}