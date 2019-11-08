import org.flowable.common.engine.impl.de.odysseus.el.ExpressionFactoryImpl;
import org.flowable.common.engine.impl.de.odysseus.el.util.SimpleContext;
import org.flowable.common.engine.impl.javax.el.ExpressionFactory;
import org.flowable.common.engine.impl.javax.el.ValueExpression;
import org.junit.Test;

public class JuelTest {
  
    public static final int NUM = 1000000;
  
    public int getA() {  
        return 10;  
    }  
  
    public int getB() {  
        return 10;  
    }  
  
    String exp = "${Math:min(Math:floor(test.a*test.b),Math:floor(c+d*e))}";  
  
    ExpressionFactory factory = new ExpressionFactoryImpl();
  
    SimpleContext context = new SimpleContext();
  
    @Test
    public void compileTest() throws Exception {  
        context.setFunction("Math", "min", Math.class.getMethod("min", int.class, int.class));  
        context.setFunction("Math", "floor", Math.class.getMethod("floor", double.class));  
        context.setVariable("test", factory.createValueExpression(new JuelTest(), JuelTest.class));  
        context.setVariable("c", factory.createValueExpression(10, Integer.class));  
        context.setVariable("d", factory.createValueExpression(10, Integer.class));  
        context.setVariable("e", factory.createValueExpression(2, Integer.class));  
        ValueExpression e = factory.createValueExpression(context, exp, String.class);
  
        for (int i = 0; i < NUM; i++) {  
            Object result = e.getValue(context);  
            // System.err.println(result);  
        }  
    }  
  
}  