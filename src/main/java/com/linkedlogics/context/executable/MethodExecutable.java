package com.linkedlogics.context.executable;

import com.linkedlogics.LogicContext;
import com.linkedlogics.annotation.*;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class MethodExecutable implements LogicExecutable {
    private Object object ;
    private Method method ;
    private String returnAs ;

    private MethodParameter[] values ;
    private DefaultValue[] defaultValues ;

    public MethodExecutable(Method method) {
        this(null, method) ;
    }

    public MethodExecutable(Object object, Method method) {
        this.object = object;
        this.method = method;
        init();
    }

    private void init() {
        values = new MethodParameter[this.method.getParameterCount()] ;

        for (int i = 0; i < this.method.getParameterCount(); i++) {
            if (this.method.getParameterTypes()[i] == LogicContext.class
                || LogicContext.class.isAssignableFrom(this.method.getParameterTypes()[i])) {
                values[i] = MethodParameter.getContextAsParam() ;
                continue;
            }

            Annotation[] annotations = this.method.getParameterAnnotations()[i] ;
            if (annotations.length == 0) {
                values[i] = MethodParameter.getContextParam(this.method.getParameterTypes()[i].getSimpleName().toLowerCase(), this.method.getParameterTypes()[i], true, null, false) ;
            } else {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof ContextParam) {
                        ContextParam param = (ContextParam) annotation ;
                        values[i] = MethodParameter.getContextParam(param.value(), this.method.getParameterTypes()[i], param.required(), param.defaultValue(), param.useInputIfNull()) ;
                    } else if (annotation instanceof InputParam) {
                        InputParam param = (InputParam) annotation ;
                        values[i] = MethodParameter.getInputParam(param.value(), this.method.getParameterTypes()[i], param.required(), param.defaultValue(), param.useContextIfNull()) ;
                    }
                }
            }
        }

        Logic logic = this.method.getAnnotation(Logic.class) ;
        if (logic.returnAs().length() > 0) {
            this.returnAs = logic.returnAs() ;
        }
    }

    @Override
    public Optional<Map<String, Object>> execute(LogicContext context) {
        Object[] params = new Object[values.length] ;
        for (int i = 0; i < values.length; i++) {
            params[i] = values[i].getValue(context) ;
        }
        final Object result ;
        if (Modifier.isStatic(method.getModifiers())) {
            result = ReflectionUtils.invokeMethod(method, null, params);
        } else {
            result = ReflectionUtils.invokeMethod(method, object, params);
        }

        if (result != null) {
            if (method.isAnnotationPresent(AsyncLogic.class)) {
                return Optional.of(new HashMap<>() {{
                    put("externalId", result.toString()) ;
                }});
            }

            if (result instanceof Map) {
                return Optional.of((Map<String, Object>) result) ;
            } else {
                String key = returnAs == null ? result.getClass().getSimpleName().toLowerCase() : returnAs ;
                return Optional.of(new HashMap<>() {{
                   put(key, result) ;
                }});
            }
        }

        return Optional.empty() ;
    }
}
