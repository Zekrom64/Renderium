package com.zekrom_64.renderium.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class Event<@NonNull E> {

	public final E sender;
	private CopyOnWriteArrayList<E> events = new CopyOnWriteArrayList<>();
	
	public Event(@NonNull Class<E> eclass) {
		this(eclass, null);
	}
	
	public Event(@NonNull Class<E> eclass, @Nullable String name) {
		this(eclass, name, null);
	}
	
	public Event(@NonNull Class<E> eclass, @Nullable String name, @Nullable MethodType mt) {
		try {
			// Select event method from class
			Method mevent = null;
			Method[] mclass = eclass.getMethods();
			if (name != null) {
				for(Method m : mclass) {
					if (!m.getName().equals(name)) continue;
					if (mt != null) {
						if (m.getReturnType() != mt.returnType()) continue;
						if (!Arrays.equals(m.getParameterTypes(), mt.parameterArray())) continue;
					}
					mevent = m;
					break;
				}
			} else {
				if (mclass.length < 1) throw new IllegalArgumentException("Cannot create event from empty interface");
				else if (mclass.length > 1) throw new IllegalArgumentException("Event method is ambiguous; event interface has more than one method");
				mevent = mclass[0];
			}
			if (mevent == null) throw new IllegalArgumentException("No such method found in event interface");
			if (mevent.getReturnType() != void.class) throw new IllegalArgumentException("Event interface method must return null");
			
			
			// Get the method type and handle for the event interface
			MethodType mtevent = mt != null ? mt : MethodType.methodType(mevent.getReturnType(), mevent.getParameterTypes());
			MethodHandle mhevent = MethodHandles.publicLookup().findVirtual(eclass, mevent.getName(), mtevent);
			
			// Get method handle for event list iterator
			MethodHandle mhiterator = MethodUtils.iterator(events);
			
			// Create method handle for the sender, iterating the event list and invoking with arguments for every event
			MethodHandle mhsender = MethodHandles.iteratedLoop(
					MethodHandles.permuteArguments(mhiterator, mtevent),
					null,
					mhevent
			);
			
			// Create sender instance for event
			sender = (E)MethodHandleProxies.asInterfaceInstance(eclass, mhsender);
		} catch (Exception e) {
			throw new RuntimeException("Failed to generate event sender", e);
		}
	}
	
	public void register(E e) {
		synchronized(events) {
			if (!events.contains(e)) events.add(e);
		}
	}
	
	public void unregister(E e) {
		synchronized(events) {
			events.remove(e);
		}
	}
	
}
