package io.github.shiryu.commands.bukkit.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;

@UtilityClass
public class ReflectionUtil {

    @NotNull
    public Class<?> getClazz(@NotNull final String name){
        try{
            return Class.forName(name);
        }catch (Exception e) {
            return null;
        }
    }

    @NotNull
    public Constructor findConstructor(@NotNull final Class<?> clazz, @NotNull final Class<?>... parameterTypes){
        try{
            final Constructor constructor = clazz.getConstructor(parameterTypes);

            constructor.setAccessible(true);

            return constructor;
        }catch (NoSuchMethodException e){
            return null;
        }
    }

    @NotNull
    public Constructor findConstructor(@NotNull final Class<?> clazz){
        return findConstructor(clazz, null);
    }

    @NotNull
    public <T> T newInstance(@NotNull final Constructor constructor, @NotNull final Object... parameters){
        try{
            return (T) constructor.newInstance(parameters);
        }catch (InstantiationException | IllegalAccessException | InvocationTargetException e){
            return null;
        }
    }

    @NotNull
    public <T> T newInstance(@NotNull final Constructor constructor){
        return newInstance(constructor, null);
    }


    @NotNull
    public Field findField(@NotNull final Class<?> clazz, @NotNull final String name){
        try{
            final Field field = clazz.getDeclaredField(name);
            
            field.setAccessible(true);
            
            return field;
        }catch(NoSuchFieldException e){
            return null;
        }
    }


    @NotNull
    public <T> T getField(@NotNull final Field field, @NotNull final Object base){
        try{
            return (T) field.get(base);
        }catch (IllegalAccessException e){
            return null;
        }
    }

    @NotNull
    public <T> T getField(@NotNull final Field field){
        return getField(field, null);
    }

    public void setField(@NotNull final Field field, @NotNull final Object base, @NotNull final Object set){
        try{
            field.set(base, set);
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public void setField(@NotNull final Field field, @NotNull final Object set){
       setField(field, null, set);
    }

    @NotNull
    public Method findMethod(@NotNull final Class<?> clazz, @NotNull final String name, @NotNull final Class<?>... parameterTypes){
        try{
            final Method method = clazz.getMethod(name, parameterTypes);

            method.setAccessible(true);

            return method;
        }catch (NoSuchMethodException e){
            return null;
        }
    }

    @NotNull
    public Method findMethod(@NotNull final Class<?> clazz, @NotNull final String name){
        return findMethod(clazz, name, null);
    }

    public <T> T invokeMethod(@NotNull final Method method, @NotNull final Object base, @NotNull final Object... parameters){
        try{
            return (T) method.invoke(base, parameters);
        }catch (IllegalAccessException | InvocationTargetException e){
            return null;
        }
    }

    public <T> T invokeMethod(@NotNull final Method method, @NotNull final Object base){
        return invokeMethod(method, base, null);
    }

    public <T> T invokeMethodStatic(@NotNull final Method method, @NotNull final Object... parameters){
        return invokeMethod(method, null, parameters);
    }

    public <T> T invokeMethodStatic(@NotNull final Method method){
        return invokeMethodStatic(method, null);
    }
}
