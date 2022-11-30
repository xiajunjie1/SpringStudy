package com.maker.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.*;
/**
 * SpEL除了其基本语法外，想要发挥其真正的使用特点，必须结合
 * 已有的Spring配置环境，在Spring中所有的Bean都需要进行配置注册
 * 那么可以在注册的时候使用SpEL进行数据处理
 *
 *  使用SpEL注入不同Profile中*.properties的属性内容
 *      1.在profiles/dev和profiles/product下创建Book.properties
 *      2.在application.xml中指定Book.properties的位置
 *      3.到Book_SpEL中，对属性进行注入
 *      4.在SpringTest的测试类中，编写测试用例，实例化对象
 * */
public class SpELTest {
    private static final Logger LOGGER= LoggerFactory.getLogger(SpELTest.class);
    public static void main(String[] args) throws Exception{
        //SpELHandle();
        //parseContextTest();
        //spelTest();
        //relationalSpELTest();
        //logicSpELTest();
        //classSpELTest();
        //varSpELHandle();
        //listSpELTest();
        mapSpELTest();
    }

    /**
     * 定义一个字符串，在里面对此字符串进行连接、截取、转大写的操作
     *  此表达式中设置有两个变量#start，#end
     * */
    private static void SpELHandle(){
        String str="(\"www.\"+\"jayjxia\").substring(#start,#end).toUpperCase()";
        ExpressionParser parser=new SpelExpressionParser();//定义SpEL解析器
        Expression expression=parser.parseExpression(str);//解析表达式
        //执行表达式之前，需要考虑两个占位符的赋值
        EvaluationContext context=new StandardEvaluationContext();
        context.setVariable("start",4);
        context.setVariable("end",11);
        System.out.println("SpEL处理结果："+expression.getValue(context));//表达式计算结果

    }

    /**
     * 在SpEL中允许用户自定义分隔符，这就需要使用到ParserContext接口
     *  接口源码如下：
     *  public interface ParserContext {
     *      boolean isTemplate();//是否启用模板
     *      String getExpressionPrefix(); //模板前缀，即表达式开始
     *      String getExpressionSuffix(); //模板后缀，即表达式结束
     *      //设置默认的模板
     *      ParserContext TEMPLATE_EXPRESSION = new ParserContext() {
     *
     *        @Override
     *        public boolean isTemplate() {
     * 			return true;
     *        }
     *
     *        @Override
     *        public String getExpressionPrefix() {
     * 			return "#{";
     *        }
     *
     *        @Override
     *        public String getExpressionSuffix() {
     * 			return "}";
     *        }    * 	};
     *  }
     *
     *  一般而言，不建议修改分隔符
     * */
    private static void parseContextTest(){//实现自定义分隔符
       //String str="#{10+20}";//实现加法计算
        String str="#[10+20]";//实现加法计算，自定义分隔为#[]形式

        ExpressionParser parser=new SpelExpressionParser();
        //Expression exp=parser.parseExpression(str,ParserContext.TEMPLATE_EXPRESSION);//设置为默认分隔符，即#{...}形式
       Expression exp=parser.parseExpression(str, new ParserContext() {
           @Override
           public boolean isTemplate() {
               return true;
           }

           @Override
           public String getExpressionPrefix() {
               return "#[";
           }

           @Override
           public String getExpressionSuffix() {
               return "]";
           }
       });
        LOGGER.info("计算结果为：{}",exp.getValue());

    }


    /**
     * SpEL字面表达式
     *  即字符串定义了什么，我们就得到什么
     *  由以下实例可知，SpEL解析的时候，会根据分隔符中的字符串进行自动转型的操作
     *  并且''和" "的权限是一致的
     * */
    private static void spelTest(){
        LOGGER.info("【'jayj'+\"xia\"】，字面表达式：{},Class类型：{}",spel("#{'jayj'+\"xia\"}"),spel("#{'jayj'+\"xia\"}").getClass().getName());
        LOGGER.info("【1】，字面表达式：{}，Class类型：{}",spel("#{1}"),spel("#{1}").getClass().getName());
        LOGGER.info("【1.1】，字面表达式：{}，Class类型：{}",spel("#{1.1}"),spel("#{1.1}").getClass().getName());
        LOGGER.info("【true】，字面表达式：{}，Class类型：{}",spel("#{true}"),spel("#{true}").getClass().getName());
    }
    private static Object spel(String content){
        ExpressionParser parser=new SpelExpressionParser();
        Expression exp=parser.parseExpression(content,ParserContext.TEMPLATE_EXPRESSION);
        return exp.getValue();


    }
    /**
     * SpEL关系表达式
     *
     * */
    private static void relationalSpELTest(){
        LOGGER.info("【10 ！=20】结果为{}",spel("#{10!=20}"));
        LOGGER.info("【10+20 eq 30】结果为{}",spel("#{10+20 eq 30}"));
        LOGGER.info("【30 > 20】结果为{}",spel("#{30 > 20}"));
        LOGGER.info("【20 lt 30】结果为{}",spel("#{20 lt 30}"));
        LOGGER.info("【20 between {10,30}】结果为{}",spel("#{20 between {10,30}}"));
    }

    /**
     * SpEL逻辑运算
     *
     * */
    private static void logicSpELTest(){
        //与运算，此运算符可以使用AND代替
        LOGGER.info("【10==10 && 'a'==\"a\"】结果为：{}",spel("#{10==10 && 'a'==\"a\"}"));
        //或运算，此运算符可以使用OR代替
        LOGGER.info("【10==10 || 'a'!=\"a\"】结果为：{}",spel("#{10==10 && 'a'==\"a\"}"));
       //非运算
        LOGGER.info("【10==10 && 'a'==\"a\"】结果为：{}",spel("#{NOT(10==10 && 'a'==\"a\")}"));

        //三目运算
        LOGGER.info("【10==10 ? 'jayj':'xia'】结果为：{}",spel("#{10==10 ?'jayj':'xia'}"));
        LOGGER.info("【true?'jayj':'xia'】结果为：{}",spel("#{true ?'jayj':'xia'}"));
        //用null或者字符串作为三目运算的条件时，需要使用到Groovy语法中的Elivis表达式
        //这种表达式后面只需要跟一个值，如果前面的条件是null，那么默认为false，输出后面的值
        //如果前面的条件是字符串，那么输出该字符串
        LOGGER.info("【null ?:'xia'】结果为：{}",spel("#{null ?:'xia'}"));
        LOGGER.info("【'jayj' ?:'xia'】结果为{}",spel("#{'jayj' ?:'xia'}"));

    }

    /**
     * Class表达式
     *  通过SpEL解析和反射的操作，通过字符串就能获得Class对象、
     *  调用相应Class的静态属性或静态方法，或者实例化对象等操作
     * */
    private static void classSpELTest(){
        //此处的T(xxxx)，代表获取Class<xxx>的Class对象
        LOGGER.info("【T(java.lang.Integer,maxValue)】结果为：{}",spel("#{T(java.lang.Integer).MAX_VALUE}"));
        //创建一个对象实例
        LOGGER.info("【new com.maker.vo.Book_SpEL('spring开发',66.66)】结果为：{}",spel("#{new com.maker.vo.Book_SpEL('spring开发',66.66)}"));
    }

    /**
     * 表达式变量操作
     *  此变量可以是普通的变量类型，可以是各种Class对象
     *  甚至可以是对象当中的方法，或者是对象当中的属性，一下就是由一个方法来
     *  替换掉SpEL中的对象字符串
     * */
    public static void varSpELHandle() throws Exception{
        String content="#{ #var(2)}";//如果采用#var?(2)则表示会判断变量的内容是否为null，如果为null，那么就会返回null
        ExpressionParser parser=new SpelExpressionParser();
        Expression expr=parser.parseExpression(content,ParserContext.TEMPLATE_EXPRESSION);
        EvaluationContext context=new StandardEvaluationContext();
        Method method=Class.forName("com.maker.vo.Book_SpEL").getMethod("getCount",Integer.class);
        context.setVariable("var",method);//将变量设为com.maker.vo.Book_SpEL的方法
        LOGGER.info("结果为：{}，类型为：{}",expr.getValue(context,Double.class),expr.getValue(context,Double.class).getClass());//设置表达式计算结果的类型
    }

    /**
     * List集合表达式
     * */
    public static void listSpELTest(){
        //定义集合
        //String content="#{ {'xia','jun','jie'} }";
        List<String> data=new ArrayList<>();
        Collections.addAll(data,"xia","jun","jie");
        //获取外部集合中的元素
        //String content="#{#alls[0]}";
        //修改集合元素的内容，此修改方式会改变外部集合当中的元素值
        //String content="#{#alls[2]='jayj'}";
        //迭代修改集合元素内容，此修改方式不会改变外部集合当中的元素值，而是将修改好的集合存放到一个新的集合之中
        //固定格式为 #alls.![],其中的#this代表当前遍历到的元素值
        String content="#{#alls.!['【JayjXia】'+ #this]}";

        ExpressionParser parser=new SpelExpressionParser();
        Expression expr=parser.parseExpression(content,ParserContext.TEMPLATE_EXPRESSION);
        EvaluationContext context=new StandardEvaluationContext();
        //List<String> result=expr.getValue(context,List.class);
       // for (String str : result){
           // LOGGER.info("【集合内容】{}",str);
        //}
        context.setVariable("alls",data);
        //String result=expr.getValue(context,String.class);
        List<String> result=expr.getValue(context,List.class);
        LOGGER.info("【修改后的集合内容】{}",result);
        LOGGER.info("【外部集合所有元素】{}", data);
    }
    /**
     * Map表达式
     * */
    private static void mapSpELTest(){
        Map<String,String> data=new HashMap<>();
        data.put("xia","夏");
        data.put("jun","俊");
        data.put("jie","杰");
        //实现Get功能
        //String content="#{#maps['xia']}";
        //实现put功能，修改相应的元素值
        //String content="#{#maps['xia']='XIA'}";
        //实现遍历map的功能，将map转为List，计算出的返回值也是List对象
        //String content="#{#maps.![#this.key+'='+#this.value]}";
       //实现Map集合数据的筛选,key中包含jun的数据，如果不存在key值，则返回null
        String content="#{#maps.?[#this.key.contains('jun')]}";
        ExpressionParser parser=new SpelExpressionParser();
        Expression expr=parser.parseExpression(content,ParserContext.TEMPLATE_EXPRESSION);
        EvaluationContext context=new StandardEvaluationContext();
        context.setVariable("maps",data);
       // LOGGER.info("【map元素结果】{}",expr.getValue(context,String.class));
        LOGGER.info("【map内容】{}",expr.getValue(context,Map.class));
        LOGGER.info("【原map内容】{}",data);

    }
}
