/**
 * # The $.Promises object
 * 
 * The $.Promises object is a convenience wrapper around arrays of jQuery
 * Deferreds or promises. It helps to collect Deferreds and add new ones later
 * on, to delay their resolution and pass them to $.when even before all
 * Deferreds of the collection are set up.
 *
 * In short, $.Promises adds another layer of asynchronicity to Deferreds and
 * provides an easy-to-read API at the same time.
 *
 * See
 *
 *     http://www.zeilenwechsel.de/it/articles/6/.Promises-Handling-collections-of-jQuery-Deferreds.html
 * 
 * for more information.
 *
 * ## Dependencies
 *
 * Requires jQuery 1.6.0 or newer.
 * 
 * ## Other
 *
 * @author  Michael Heim, http://www.zeilenwechsel.de/
 * @license MIT, http://www.opensource.org/licenses/mit-license.php
 * @version 0.1.0, 6 July 2011
 */

jQuery.extend( {
    
    Promises: ( function ( $ ) {
        
        var Promises = function () {
            
            var masterDfd = $.Deferred(),
                collected = [],
                counter = 0,
                block,
                blockIndex,
                ignoreBelatedCalls = false;
            
            
            // Make 'new' optional
            if ( ! ( this instanceof Promises ) ) {
                
                var obj = new Promises();
                return Promises.apply( obj, arguments );
                // ... re-runs the constructor function, same as
                // obj.constructor.apply( obj, arguments );
                
            }
            
            
            /**
             * Takes an array of objects and removes any duplicates. The first
             * occurrence of the object is preserved. The order of elements
             * remains unchanged. 
             * 
             * @param   {Array} arr
             * @returns {Array}
             */
            var toUniqueObjects = function ( arr ) {
                
                var unique = [],
                    duplicate,
                    i, j, len, uniqueLen;
                
                for ( i = 0, len = arr.length; i < len; i++ ) {
                    
                    duplicate = false;
                    for ( j = 0, uniqueLen = unique.length; j < uniqueLen; j++ ) duplicate = ( arr[i] === unique[j] ) || duplicate;
                    if ( ! duplicate ) unique.push( arr[i] );
                    
                }
                
                return unique;        
                
            };
            
            this.add = function () {
                
                if ( collected[0] && ! this.isUnresolved() && ! ignoreBelatedCalls ) {
                    throw {
                        name: 'PromisesError',
                        message: "Can't add promise when Promises is no longer unresolved"
                    };
                }
                
                for ( var i = 0; i < arguments.length; i++ ) collected.push( arguments[i] );
                collected = toUniqueObjects( collected );
                
                if ( collected.length ) {
                    
                    counter++;
                    $.when.apply( this, collected )
                          .done( resolveIfCurrent( counter ) )
                          .fail( rejectIfCurrent( counter ) );
                    
                }
                
                return this;
                
            };
            
            this.postpone = function () {
                
                if ( ! block ) {
                    
                    if ( collected[0] && ! this.isUnresolved() && ! ignoreBelatedCalls ) {
                        throw {
                            name: 'PromisesError',
                            message: "Can't postpone resolution when Promises is no longer unresolved"
                        };
                    }
                    
                    block = $.Deferred();
                    blockIndex = collected.length;
                    this.add( block );
                    
                }
                
                return this;
                
            };
            
            this.stopPostponing = function () {
                
                if ( block ) {
                    
                    collected.splice( blockIndex, 1 );
                    this.add();     // we don't add anything, but the masterDeferred will be updated
                    block = null;
                    
                }
                
                return this;
                
            }
            
            this.ignoreBelated = function ( yesno ) {
                
                ignoreBelatedCalls = ! yesno;
                return this;
                
            };
            
            this.isUnresolved = function () {
                
                return ! ( this.isResolved() || this.isRejected() );
                
            }
            
            var resolveIfCurrent = function ( counterAtInvokation ) {
                
                return ( function() {
                    if ( counter == counterAtInvokation ) masterDfd.resolve.apply( this, arguments );
                } );
                
            };
        
            var rejectIfCurrent = function ( counterAtInvokation ) {
                
                return ( function() {
                    if ( counter == counterAtInvokation ) masterDfd.reject.apply( this, arguments );
                } );
                
            };
            
            this.add.apply( this, arguments );
            
            return masterDfd.promise( this );
            
        };
        
        return Promises;
        
    } )( jQuery )
    
} );
