package com.geecommerce.core.event;

/**
 * Choose whether the {@link com.geecommerce.core.event.Observer} should be run synchronously or asynchronously. This is declared in the
 * {@link com.geecommerce.core.event.annotation.Observe} annotation.
 * 
 * @see com.geecommerce.core.event.annotation.Observe
 * @see com.geecommerce.core.event.Observable
 * 
 * @author Michael Delamere
 */
public enum Run {
    SYNCHRONOUSLY, ASYNCHRONOUSLY;
}
