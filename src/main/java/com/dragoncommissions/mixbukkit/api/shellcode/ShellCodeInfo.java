package com.dragoncommissions.mixbukkit.api.shellcode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShellCodeInfo {

    /**
     * @return Official name of the shellcode
     */
    String name();

    /**
     * @return Description of the shellcode, like what it will do etc.
     */
    String description();

    /**
     * @return Returns true if it's going to modify the MethodNode. For example: Add a try & catch block
     */
    boolean requireMethodNodeModification() default false;

    /**
     * @return Returns true if it will need to allocate new variables
     */
    boolean requireVarManager() default false;

    /**
     * @return Content of stack, first one pushed into stack should be in the bottom of return value
     */
    String[] stacksContent() default {};

    /**
     * @return What's required on the stack
     */
    String[] requiredStacksContent() default {};

    /**
     * @return Is it safe to call it directly inside a method
     */
    boolean calledDirectly() default false;

    /**
     * @return Is it required to use -noverify in order to load the shellcode
     */
    boolean failsClassVerification() default true;

}
