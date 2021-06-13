package com.alibaba.ttl.threadpool.agent.transformlet.helper;

import com.alibaba.ttl.spi.TtlEnhanced;
import com.alibaba.ttl.threadpool.agent.logging.Logger;
import com.alibaba.ttl.threadpool.agent.transformlet.TtlTransformlet;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javassist.*;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import static com.alibaba.ttl.TransmittableThreadLocal.Transmitter.capture;

/**
 * Helper methods for {@link TtlTransformlet} implementation.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @since 2.13.0
 */
public final class TtlTransformletHelper {
    private static final Logger logger = Logger.getLogger(TtlTransformletHelper.class);

    // ======== Javassist/Class Helper ========

    /**
     * Output string like {@code public ScheduledFuture scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     * for {@link  java.util.concurrent.ScheduledThreadPoolExecutor#scheduleAtFixedRate}.
     *
     * @param method method object
     * @return method signature string
     */
    @NonNull
    public static String signatureOfMethod(@NonNull final CtBehavior method) throws NotFoundException {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(Modifier.toString(method.getModifiers()));
        if (method instanceof CtMethod) {
            final String returnType = ((CtMethod) method).getReturnType().getSimpleName();
            stringBuilder.append(" ").append(returnType);
        }
        stringBuilder.append(" ").append(method.getName()).append("(");

        final CtClass[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            CtClass parameterType = parameterTypes[i];
            if (i != 0){
                stringBuilder.append(", ");
            }
            stringBuilder.append(parameterType.getSimpleName());
        }

        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public static URL getLocationUrlOfClass(CtClass clazz) {
        try {
            // proxy classes is dynamic, no class file
            if (clazz.getName().startsWith("com.sun.proxy.")) return null;

            return clazz.getURL();
        } catch (Exception e) {
            logger.warn("Fail to getLocationUrlOfClass " + clazz.getName() + ", cause: " + e.toString());
            return null;
        }
    }

    public static String getLocationFileOfClass(CtClass clazz) {
        final URL location = getLocationUrlOfClass(clazz);
        if (location == null) return null;

        return location.getFile();
    }

    public static URL getLocationUrlOfClass(Class<?> clazz) {
        try {
            // proxy classes is dynamic, no class file
            if (clazz.getName().startsWith("com.sun.proxy.")) return null;

            final ProtectionDomain protectionDomain = clazz.getProtectionDomain();
            if (protectionDomain == null) return null;

            final CodeSource codeSource = protectionDomain.getCodeSource();
            if (codeSource == null) return null;

            return codeSource.getLocation();
        } catch (Exception e) {
            logger.warn("Fail to getLocationUrlOfClass " + clazz.getName() + ", cause: " + e.toString());
            return null;
        }
    }

    public static String getLocationFileOfClass(Class<?> clazz) {
        final URL location = getLocationUrlOfClass(clazz);
        if (location == null) return null;

        return location.getFile();
    }

    // ======== Method Transform Helper ========

    @NonNull
    public static String renamedMethodNameByTtl(@NonNull CtMethod method) {
        return "original$" + method.getName() + "$method$renamed$by$ttl";
    }

    public static String addTryFinallyToMethod(@NonNull CtMethod method, @NonNull String beforeCode, @NonNull String finallyCode) throws CannotCompileException, NotFoundException {
        return addTryFinallyToMethod(method, renamedMethodNameByTtl(method), beforeCode, finallyCode);
    }

    /**
     * Add {@code try-finally} logic to method.
     *
     * @return the body code of method rewritten
     */
    public static String addTryFinallyToMethod(@NonNull CtMethod method, @NonNull String nameForOriginalMethod, @NonNull String beforeCode, @NonNull String finallyCode) throws CannotCompileException, NotFoundException {
        final CtClass clazz = method.getDeclaringClass();

        final CtMethod newMethod = CtNewMethod.copy(method, clazz, null);
        // rename original method, and set to private method(avoid reflect out renamed method unexpectedly)
        newMethod.setName(nameForOriginalMethod);
        newMethod.setModifiers(newMethod.getModifiers()
            & ~Modifier.PUBLIC /* remove public */
            & ~Modifier.PROTECTED /* remove protected */
            | Modifier.PRIVATE /* add private */);
        clazz.addMethod(newMethod);

        final String returnOp;
        if (method.getReturnType() == CtClass.voidType) {
            returnOp = "";
        } else {
            returnOp = "return ";
        }
        // set new method implementation
        final String code = "{\n" +
            beforeCode + "\n" +
            "try {\n" +
            "    " + returnOp + nameForOriginalMethod + "($$);\n" +
            "} finally {\n" +
            "    " + finallyCode + "\n" +
            "} }";
        method.setBody(code);

        return code;
    }

    // ======== CRR Helper ========

    @Nullable
    public static Object doCaptureIfNotTtlEnhanced(@Nullable Object obj) {
        if (obj instanceof TtlEnhanced) return null;
        else return capture();
    }

    private TtlTransformletHelper() {
        throw new InstantiationError("Must not instantiate this class");
    }
}
