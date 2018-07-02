package com.besieged.musicpractice.model;

import java.util.List;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/7/2.
 */

public class SearchResponse {

    /**
     * result : {"songs":[{"name":"阿婆说","id":479422013,"pst":0,"t":0,"ar":[{"id":1137098,"name":"陈一发儿","tns":[],"alias":[]}],"alia":[],"pop":100,"st":0,"rt":null,"fee":0,"v":7,"crbt":null,"cf":"","al":{"id":35543057,"name":"阿婆说","picUrl":"http://p1.music.126.net/hT0qB_Wi-w5a0qhIumB59g==/18619129906686749.jpg","tns":[],"pic_str":"18619129906686749","pic":18619129906686749},"dt":246856,"h":{"br":320000,"fid":0,"size":9876419,"vd":-0.99},"m":{"br":160000,"fid":0,"size":4938232,"vd":-0.55},"l":{"br":96000,"fid":0,"size":2962957,"vd":-0.54},"a":null,"cd":"1","no":0,"rtUrl":null,"ftype":0,"rtUrls":[],"djId":0,"copyright":2,"s_id":0,"mst":9,"cp":0,"mv":0,"rtype":0,"rurl":null,"publishTime":1495209600007,"privilege":{"id":479422013,"fee":0,"payed":0,"st":0,"pl":320000,"dl":320000,"sp":7,"cp":1,"subp":1,"cs":false,"maxbr":999000,"fl":320000,"toast":false,"flag":2}}],"songCount":57}
     * code : 200
     */

    private ResultBean result;
    private int code;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ResultBean {
        /**
         * songs : [{"name":"阿婆说","id":479422013,"pst":0,"t":0,"ar":[{"id":1137098,"name":"陈一发儿","tns":[],"alias":[]}],"alia":[],"pop":100,"st":0,"rt":null,"fee":0,"v":7,"crbt":null,"cf":"","al":{"id":35543057,"name":"阿婆说","picUrl":"http://p1.music.126.net/hT0qB_Wi-w5a0qhIumB59g==/18619129906686749.jpg","tns":[],"pic_str":"18619129906686749","pic":18619129906686749},"dt":246856,"h":{"br":320000,"fid":0,"size":9876419,"vd":-0.99},"m":{"br":160000,"fid":0,"size":4938232,"vd":-0.55},"l":{"br":96000,"fid":0,"size":2962957,"vd":-0.54},"a":null,"cd":"1","no":0,"rtUrl":null,"ftype":0,"rtUrls":[],"djId":0,"copyright":2,"s_id":0,"mst":9,"cp":0,"mv":0,"rtype":0,"rurl":null,"publishTime":1495209600007,"privilege":{"id":479422013,"fee":0,"payed":0,"st":0,"pl":320000,"dl":320000,"sp":7,"cp":1,"subp":1,"cs":false,"maxbr":999000,"fl":320000,"toast":false,"flag":2}}]
         * songCount : 57
         */

        private int songCount;
        private List<SongsBean> songs;

        public int getSongCount() {
            return songCount;
        }

        public void setSongCount(int songCount) {
            this.songCount = songCount;
        }

        public List<SongsBean> getSongs() {
            return songs;
        }

        public void setSongs(List<SongsBean> songs) {
            this.songs = songs;
        }

        public static class SongsBean {
            /**
             * name : 阿婆说
             * id : 479422013
             * pst : 0
             * t : 0
             * ar : [{"id":1137098,"name":"陈一发儿","tns":[],"alias":[]}]
             * alia : []
             * pop : 100
             * st : 0
             * rt : null
             * fee : 0
             * v : 7
             * crbt : null
             * cf :
             * al : {"id":35543057,"name":"阿婆说","picUrl":"http://p1.music.126.net/hT0qB_Wi-w5a0qhIumB59g==/18619129906686749.jpg","tns":[],"pic_str":"18619129906686749","pic":18619129906686749}
             * dt : 246856
             * h : {"br":320000,"fid":0,"size":9876419,"vd":-0.99}
             * m : {"br":160000,"fid":0,"size":4938232,"vd":-0.55}
             * l : {"br":96000,"fid":0,"size":2962957,"vd":-0.54}
             * a : null
             * cd : 1
             * no : 0
             * rtUrl : null
             * ftype : 0
             * rtUrls : []
             * djId : 0
             * copyright : 2
             * s_id : 0
             * mst : 9
             * cp : 0
             * mv : 0
             * rtype : 0
             * rurl : null
             * publishTime : 1495209600007
             * privilege : {"id":479422013,"fee":0,"payed":0,"st":0,"pl":320000,"dl":320000,"sp":7,"cp":1,"subp":1,"cs":false,"maxbr":999000,"fl":320000,"toast":false,"flag":2}
             */

            private String name;
            private int id;
            private int pst;
            private int t;
            private int pop;
            private int st;
            private Object rt;
            private int fee;
            private int v;
            private Object crbt;
            private String cf;
            private AlBean al;
            private int dt;
            private HBean h;
            private MBean m;
            private LBean l;
            private Object a;
            private String cd;
            private int no;
            private Object rtUrl;
            private int ftype;
            private int djId;
            private int copyright;
            private int s_id;
            private int mst;
            private int cp;
            private int mv;
            private int rtype;
            private Object rurl;
            private long publishTime;
            private PrivilegeBean privilege;
            private List<ArBean> ar;
            private List<?> alia;
            private List<?> rtUrls;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getPst() {
                return pst;
            }

            public void setPst(int pst) {
                this.pst = pst;
            }

            public int getT() {
                return t;
            }

            public void setT(int t) {
                this.t = t;
            }

            public int getPop() {
                return pop;
            }

            public void setPop(int pop) {
                this.pop = pop;
            }

            public int getSt() {
                return st;
            }

            public void setSt(int st) {
                this.st = st;
            }

            public Object getRt() {
                return rt;
            }

            public void setRt(Object rt) {
                this.rt = rt;
            }

            public int getFee() {
                return fee;
            }

            public void setFee(int fee) {
                this.fee = fee;
            }

            public int getV() {
                return v;
            }

            public void setV(int v) {
                this.v = v;
            }

            public Object getCrbt() {
                return crbt;
            }

            public void setCrbt(Object crbt) {
                this.crbt = crbt;
            }

            public String getCf() {
                return cf;
            }

            public void setCf(String cf) {
                this.cf = cf;
            }

            public AlBean getAl() {
                return al;
            }

            public void setAl(AlBean al) {
                this.al = al;
            }

            public int getDt() {
                return dt;
            }

            public void setDt(int dt) {
                this.dt = dt;
            }

            public HBean getH() {
                return h;
            }

            public void setH(HBean h) {
                this.h = h;
            }

            public MBean getM() {
                return m;
            }

            public void setM(MBean m) {
                this.m = m;
            }

            public LBean getL() {
                return l;
            }

            public void setL(LBean l) {
                this.l = l;
            }

            public Object getA() {
                return a;
            }

            public void setA(Object a) {
                this.a = a;
            }

            public String getCd() {
                return cd;
            }

            public void setCd(String cd) {
                this.cd = cd;
            }

            public int getNo() {
                return no;
            }

            public void setNo(int no) {
                this.no = no;
            }

            public Object getRtUrl() {
                return rtUrl;
            }

            public void setRtUrl(Object rtUrl) {
                this.rtUrl = rtUrl;
            }

            public int getFtype() {
                return ftype;
            }

            public void setFtype(int ftype) {
                this.ftype = ftype;
            }

            public int getDjId() {
                return djId;
            }

            public void setDjId(int djId) {
                this.djId = djId;
            }

            public int getCopyright() {
                return copyright;
            }

            public void setCopyright(int copyright) {
                this.copyright = copyright;
            }

            public int getS_id() {
                return s_id;
            }

            public void setS_id(int s_id) {
                this.s_id = s_id;
            }

            public int getMst() {
                return mst;
            }

            public void setMst(int mst) {
                this.mst = mst;
            }

            public int getCp() {
                return cp;
            }

            public void setCp(int cp) {
                this.cp = cp;
            }

            public int getMv() {
                return mv;
            }

            public void setMv(int mv) {
                this.mv = mv;
            }

            public int getRtype() {
                return rtype;
            }

            public void setRtype(int rtype) {
                this.rtype = rtype;
            }

            public Object getRurl() {
                return rurl;
            }

            public void setRurl(Object rurl) {
                this.rurl = rurl;
            }

            public long getPublishTime() {
                return publishTime;
            }

            public void setPublishTime(long publishTime) {
                this.publishTime = publishTime;
            }

            public PrivilegeBean getPrivilege() {
                return privilege;
            }

            public void setPrivilege(PrivilegeBean privilege) {
                this.privilege = privilege;
            }

            public List<ArBean> getAr() {
                return ar;
            }

            public void setAr(List<ArBean> ar) {
                this.ar = ar;
            }

            public List<?> getAlia() {
                return alia;
            }

            public void setAlia(List<?> alia) {
                this.alia = alia;
            }

            public List<?> getRtUrls() {
                return rtUrls;
            }

            public void setRtUrls(List<?> rtUrls) {
                this.rtUrls = rtUrls;
            }

            public static class AlBean {
                /**
                 * id : 35543057
                 * name : 阿婆说
                 * picUrl : http://p1.music.126.net/hT0qB_Wi-w5a0qhIumB59g==/18619129906686749.jpg
                 * tns : []
                 * pic_str : 18619129906686749
                 * pic : 18619129906686749
                 */

                private int id;
                private String name;
                private String picUrl;
                private String pic_str;
                private long pic;
                private List<?> tns;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getPicUrl() {
                    return picUrl;
                }

                public void setPicUrl(String picUrl) {
                    this.picUrl = picUrl;
                }

                public String getPic_str() {
                    return pic_str;
                }

                public void setPic_str(String pic_str) {
                    this.pic_str = pic_str;
                }

                public long getPic() {
                    return pic;
                }

                public void setPic(long pic) {
                    this.pic = pic;
                }

                public List<?> getTns() {
                    return tns;
                }

                public void setTns(List<?> tns) {
                    this.tns = tns;
                }
            }

            public static class HBean {
                /**
                 * br : 320000
                 * fid : 0
                 * size : 9876419
                 * vd : -0.99
                 */

                private int br;
                private int fid;
                private int size;
                private double vd;

                public int getBr() {
                    return br;
                }

                public void setBr(int br) {
                    this.br = br;
                }

                public int getFid() {
                    return fid;
                }

                public void setFid(int fid) {
                    this.fid = fid;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                public double getVd() {
                    return vd;
                }

                public void setVd(double vd) {
                    this.vd = vd;
                }
            }

            public static class MBean {
                /**
                 * br : 160000
                 * fid : 0
                 * size : 4938232
                 * vd : -0.55
                 */

                private int br;
                private int fid;
                private int size;
                private double vd;

                public int getBr() {
                    return br;
                }

                public void setBr(int br) {
                    this.br = br;
                }

                public int getFid() {
                    return fid;
                }

                public void setFid(int fid) {
                    this.fid = fid;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                public double getVd() {
                    return vd;
                }

                public void setVd(double vd) {
                    this.vd = vd;
                }
            }

            public static class LBean {
                /**
                 * br : 96000
                 * fid : 0
                 * size : 2962957
                 * vd : -0.54
                 */

                private int br;
                private int fid;
                private int size;
                private double vd;

                public int getBr() {
                    return br;
                }

                public void setBr(int br) {
                    this.br = br;
                }

                public int getFid() {
                    return fid;
                }

                public void setFid(int fid) {
                    this.fid = fid;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                public double getVd() {
                    return vd;
                }

                public void setVd(double vd) {
                    this.vd = vd;
                }
            }

            public static class PrivilegeBean {
                /**
                 * id : 479422013
                 * fee : 0
                 * payed : 0
                 * st : 0
                 * pl : 320000
                 * dl : 320000
                 * sp : 7
                 * cp : 1
                 * subp : 1
                 * cs : false
                 * maxbr : 999000
                 * fl : 320000
                 * toast : false
                 * flag : 2
                 */

                private int id;
                private int fee;
                private int payed;
                private int st;
                private int pl;
                private int dl;
                private int sp;
                private int cp;
                private int subp;
                private boolean cs;
                private int maxbr;
                private int fl;
                private boolean toast;
                private int flag;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getFee() {
                    return fee;
                }

                public void setFee(int fee) {
                    this.fee = fee;
                }

                public int getPayed() {
                    return payed;
                }

                public void setPayed(int payed) {
                    this.payed = payed;
                }

                public int getSt() {
                    return st;
                }

                public void setSt(int st) {
                    this.st = st;
                }

                public int getPl() {
                    return pl;
                }

                public void setPl(int pl) {
                    this.pl = pl;
                }

                public int getDl() {
                    return dl;
                }

                public void setDl(int dl) {
                    this.dl = dl;
                }

                public int getSp() {
                    return sp;
                }

                public void setSp(int sp) {
                    this.sp = sp;
                }

                public int getCp() {
                    return cp;
                }

                public void setCp(int cp) {
                    this.cp = cp;
                }

                public int getSubp() {
                    return subp;
                }

                public void setSubp(int subp) {
                    this.subp = subp;
                }

                public boolean isCs() {
                    return cs;
                }

                public void setCs(boolean cs) {
                    this.cs = cs;
                }

                public int getMaxbr() {
                    return maxbr;
                }

                public void setMaxbr(int maxbr) {
                    this.maxbr = maxbr;
                }

                public int getFl() {
                    return fl;
                }

                public void setFl(int fl) {
                    this.fl = fl;
                }

                public boolean isToast() {
                    return toast;
                }

                public void setToast(boolean toast) {
                    this.toast = toast;
                }

                public int getFlag() {
                    return flag;
                }

                public void setFlag(int flag) {
                    this.flag = flag;
                }
            }

            public static class ArBean {
                /**
                 * id : 1137098
                 * name : 陈一发儿
                 * tns : []
                 * alias : []
                 */

                private int id;
                private String name;
                private List<?> tns;
                private List<?> alias;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public List<?> getTns() {
                    return tns;
                }

                public void setTns(List<?> tns) {
                    this.tns = tns;
                }

                public List<?> getAlias() {
                    return alias;
                }

                public void setAlias(List<?> alias) {
                    this.alias = alias;
                }
            }
        }
    }
}
