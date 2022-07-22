package org.darklol9.bots.events;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

@AllArgsConstructor
public class EventCaller implements EventListener {

    private List<Listener> listeners;

    @SneakyThrows
    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        for (Listener listener : listeners) {
            Class<?> klass = listener.getClass();
            for (Method declaredMethod : klass.getDeclaredMethods()) {
                IListener ann = null;
                for (Annotation annotation : declaredMethod.getAnnotations()) {
                    if (annotation instanceof IListener) {
                        ann = (IListener) annotation;
                        break;
                    }
                }
                if (ann != null) {
                    Class<?> type = declaredMethod.getParameters()[0].getType();
                    if (type.isAssignableFrom(genericEvent.getClass())) {
                        try {
                            declaredMethod.setAccessible(true);
                            declaredMethod.invoke(listener, genericEvent);
                        } catch (Exception ex) {
                            throw ex.getCause();
                        } finally {
                            declaredMethod.setAccessible(false);
                        }
                    }
                }
            }
        }
    }
}
