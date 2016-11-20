package com.geecommerce.core.event;

/**
 * Choose which event is to be observed when using the
 * {@link com.geecommerce.core.event.annotation.Observe} annotation.
 * 
 * @see com.geecommerce.core.event.annotation.Observe
 * @see com.geecommerce.core.event.Observable
 * 
 * @author Michael Delamere
 */
public enum Event {
    BEFORE_NEW, AFTER_NEW, BEFORE_UPDATE, AFTER_UPDATE, BEFORE_REMOVE, AFTER_REMOVE, BEFORE_POPULATE, AFTER_POPULATE;
}
