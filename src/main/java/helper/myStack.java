package helper;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Stack;
import org.apache.log4j.Logger;

/**
 * Created by n-soorani on 2017/03/14.
 */
public class myStack<T> {

    private Stack<T> freeStack;
    private final  Object monitor = new Object();
    private Logger logger;

    public myStack(Logger logger) {
        freeStack = new Stack<T>();
        this.logger = logger;
    }

    public  void push(T item ){
        logger.info("push : Index=>" + item);
        synchronized (monitor ) {
            freeStack.push(item);
        }
    }

    public  T pop(){
        T item = null;
        synchronized (monitor ){
            item = freeStack.pop();
        }
        logger.info("pop : Index=>" + item);
        return item;
    }

    public String toString() {
        return Arrays.toString(this.freeStack.toArray());
    }

}
