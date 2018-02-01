package skadic.commands;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Help {
    String syntax() default "";

    String description() default "";
}
