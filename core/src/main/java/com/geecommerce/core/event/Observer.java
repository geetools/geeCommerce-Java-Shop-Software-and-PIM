package com.geecommerce.core.event;

/**
 * Use this interface along with the
 * {@link com.geecommerce.core.event.annotation.Observe} annotation in order to
 * observe objects extending the {@link com.geecommerce.core.event.Observable}
 * class.
 * 
 * @see com.geecommerce.core.event.Observable
 * @see com.geecommerce.core.event.annotation.Observe
 * @see com.geecommerce.core.event.Event
 * @see com.geecommerce.core.event.Run
 * 
 * @author Michael Delamere
 */
public interface Observer {
    public void onEvent(Event evt, Observable o);
}
