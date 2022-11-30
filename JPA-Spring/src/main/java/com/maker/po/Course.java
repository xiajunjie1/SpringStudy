package com.maker.po;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity//表示当前类是一个实体类
@Table(name="course")//如果类名称与表名称相同，该注解可以不定义
@Cacheable //该类对象可以实现二级缓存
public class Course implements Serializable {
    @Id//标明主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//生成策略交由数据库
    private Long cid;
    @Column(name="cname")//如果属性名称和表字段名称不对应，需要加上此注解来指定映射关系
    private String cname;
    @Temporal(TemporalType.DATE)//表示是日期的状态
    private Date start;
    @Temporal(TemporalType.DATE)//表示是日期的状态
    private Date end;

    private Integer credit;
    private Integer num;
    @Version
    private Integer vseq;

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Long getCid() {
        return cid;
    }

    public String getCname() {
        return cname;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public Integer getCredit() {
        return credit;
    }

    public Integer getNum() {
        return num;
    }
    /**
     * 一定要有无参构造，因为JPA的操作需要使用到反射
     * */
    public Course() {
    }

    public Course(Long cid, String cname, Date start, Date end, Integer credit, Integer num) {
        this.cid = cid;
        this.cname = cname;
        this.start = start;
        this.end = end;
        this.credit = credit;
        this.num = num;
    }

    public void setVseq(Integer vseq) {
        this.vseq = vseq;
    }

    public Integer getVseq() {
        return vseq;
    }

    @Override
    public String toString() {
        return "【课程信息】ID="+this.cid+"、名称="+this.cname+"、学分="+this.credit+"、人数="+this.num
                +"、开始日期="+this.start+"、结束日期="+this.end;
    }
}
