package webdav.server.virtual.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class PropertyBuilder
{
    @Target({ ElementType.FIELD, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    protected static @interface Property
    {
        public String name();
    }
    
    public PropertyBuilder(String globalName)
    {
        this.globalName = globalName.trim();
    }
    
    protected final String globalName;

    public Map<String, Object> build()
    {
        Class c = getClass();
        
        return Stream.concat(Stream.of(c.getDeclaredFields()), Stream.of(c.getMethods()))
                .filter(f -> f.isAnnotationPresent(Property.class))
                .collect(HashMap::new
                        , (m,f)->
                        {
                            Object value;

                            try
                            {
                                f.setAccessible(true);

                                if(f instanceof Field)
                                    value = (Object)((Field)f).get(this);
                                else if(f instanceof Method)
                                    value = (Object)((Method)f).invoke(this);
                                else
                                    value = null;
                            }
                            catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
                            {
                                ex.printStackTrace();
                                value = null;
                            }

                            m.put(globalName + "::" + f.getAnnotation(Property.class).name(), value);
                        }
                        , HashMap::putAll);
    }
}
