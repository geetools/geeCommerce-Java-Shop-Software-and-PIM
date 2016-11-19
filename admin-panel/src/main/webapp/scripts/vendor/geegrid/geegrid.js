/**
 * geegrid.js 0.0.1
 * (c) 2016 Dmitriy Paramoshkin
 * geegrid.js may be freely distributed under the MIT license.
*/
(function(factory) {
     //console.log(""-----GEEGRID 2----"")
    if (typeof define === 'function' && define.amd) {
        define(['jquery', /*'lodash'*/'underscore', 'jquery-ui/core', 'jquery-ui/widget', 'jquery-ui/mouse', 'jquery-ui/draggable',
            'jquery-ui/resizable', 'focusable'], factory);
    } else if (typeof exports !== 'undefined') {
        try { jQuery = require('jquery'); } catch (e) {}
        try { _ = require('underscore'/*'lodash'*/); } catch (e) {}
        factory(jQuery, _);
    } else {
        factory(jQuery, _);
    }
})(function($, _) {

     //console.log(""-----GEEGRID 1----"")
    var scope = window;

    $.fn.equals = function(compareTo) {
        if (!compareTo || this.length != compareTo.length) {
            return false;
        }
        for (var i = 0; i < this.length; ++i) {
            if (this[i] !== compareTo[i]) {
                return false;
            }
        }
        return true;
    };

    $.fn.replaceWithPush = function(a) {
        var $a = $(a);
       // $a = $a.detach();
        this.replaceWith($a);
        return $a;
    };

    $.fn.removeClassPrefix = function(prefix) {
        this.each(function(i, el) {
            var classes = el.className.split(" ").filter(function(c) {
                return c.lastIndexOf(prefix, 0) !== 0;
            });
            el.className = $.trim(classes.join(" "));
        });
        return this;
    };

    var wrapperRow = '<div class="gee-row row"></div>';
    //Wrapper for cells inserted as new columns
    var wrapperColumn = '<div class="gee-column"></div>';
    //Wrapper for items inserted as new cells
    var wrapperCell = '<div class="gee-cell" style="z-index:9998"></div>';


    var GeeWidget = function (el, options, widgetOptions) {
        //options = options || {};
         //console.log("options")
        this.w = $(el);
        this.options = options;
        this.widgetOptions = widgetOptions;

        if(widgetOptions){
            this.key = widgetOptions.key;
            this.html = widgetOptions.html;
        } else {
            this.key = this.w.attr("id");
           // this.html =
           // this.previewSelector = this.w.attr("data-target");
        }


         //console.log(""WIDGET"")


        this.initWidget();
    }

    GeeWidget.prototype.initWidget = function() {
         //console.log("this.options")
        this.w.draggable(this.options.draggableParameters);
    }


    var GeeGridEngine = function(el, options, geeGrid) {
        var self = this;

        this.options = options;


        if(this.options.mode == 'iframe'){
            this.container = $(el).find(this.options.containerSelector);
            this.iframe = $(el);

             //console.log(""IFrame"")
             //console.log("this.iframe")
            this.iframeMode = true;
        } else{
            this.container = $(el);
            this.iframe = null;
        }



        /*        this.iframeMode = false;
                if(el.ownerDocument !== document) {
                    this.iframeMode = true;
                    this.iframeDocument = el.ownerDocument;
                    this.iframe = null;//TODO: fix
                }*/

        this.hoverSelected = $('');
        this.highlight = $('');
        this.oldSelected = $('');
        this.oldCommand = "";
        this.selected = $('');

        //this.idCounter = 0;
        //this.idPrefix = "gee-";
        this.stopEverything = false;
        this.geeGrid = geeGrid;

    }


    GeeGridEngine.prototype.initCellHandlers = function($item) {
        $item.draggable(this.options.draggableParameters);//.resizable(this.options.resizableParameters);
        $item.draggable( "option", "handle", ".w-handle-new" );
    }

    GeeGridEngine.prototype.getColSize = function(el) {
        var sizeClass = this.getPrefixClass(el, "col-md");

        if(sizeClass){
            var parts = sizeClass.split("-");
            var size = parts[parts.length - 1];
            return size;
        } else {
            sizeClass = this.getPrefixClass(el, "col-xs");
            if(sizeClass){
                var parts = sizeClass.split("-");
                var size = parts[parts.length - 1];
                return size;
            }
        }

    };

    GeeGridEngine.prototype.removeColSize = function(el) {
        $(el).removeClassPrefix("col-md-");
        $(el).removeClassPrefix("col-xs-");
    };

    GeeGridEngine.prototype.setColSize = function(el, size) {
        this.removeColSize(el);

        if(!$(el).hasClass("gee-row")){
            $(el).addClass("col-xs-12");
            $(el).addClass("col-md-" + size);
        }

    };

    GeeGridEngine.prototype.getPrefixClass = function(el, prefix) {
        var cssClasses = $(el).attr('class');
        if(cssClasses){
        var classes = cssClasses.split(' ');
            for (var i = 0; i < classes.length; i++) {
                if(classes[i].indexOf(prefix) == 0)
                    return classes[i];
            }
        }
    };

    GeeGridEngine.prototype.hasHighlight = function() {
        if(this.highlight != null) {
            return true;
        } else {
            return false
        }
    };


/*    GeeGridEngine.prototype.generateId =function(){
        this.idCounter++;
        return this.idPrefix + this.idCounter;
    }*/

    GeeGridEngine.prototype.addHighlight = function(target, command) {
        if(this.oldCommand == command/*"append"*/ && this.oldSelected.equals(target)&& this.highlight){
             //console.log(""equals"")
            // return highlight;
        } else {
            this.oldSelected = target;
            this.oldCommand = command;//"append";
          //  this.removeHighlight();
            this.highlight = $('<div id="highlight"></div>');
            target.append(this.highlight);
        }
    };

    GeeGridEngine.prototype.addHighlightAfter = function(target, command) {
        if(this.oldCommand == command/*"after"*/ && this.oldSelected.equals(target)&& this.highlight){
             //console.log(""equals"")
            //return highlight;
        } else {
            this.oldSelected = target;
            this.oldCommand = command;//"after";

            //this.removeHighlight();
            this.highlight = $('<div id="highlight"></div>');
            target.after(this.highlight);

             this.highlight.animate({
                'margin-top' : 0,
                'margin-right' : "3px",
                'margin-bottom' : 0,
                'margin-left' : "3px"
            }, "fast");
        }
    };

    GeeGridEngine.prototype.addHighlightBefore = function(target, command) {
        if(this.oldCommand == command/*"before"*/ && this.oldSelected.equals(target) && this.highlight){
             //console.log(""equals"")
        } else {
            this.oldSelected = target;
            this.oldCommand = command;//"before";

           // this.removeHighlight();
            this.highlight = $('<div id="highlight"></div>');
             //console.log(""BEFORE"")
             //console.log("target")
            target.before(this.highlight);

           this.highlight.animate({
                'margin-top' : 0,
                'margin-right' : "3px",
                'margin-bottom' : 0,
                'margin-left' : "3px"
            }, "fast");
        }
    };

    GeeGridEngine.prototype.optimizeGridNode = function($gridNode, level) {
        var self = this;
        if($gridNode.hasClass("gee-cell")){
            return false;
        }

        var optimized = false;
        if($gridNode.children().size() >= 2 || ( $gridNode.hasClass("gee-grid") && $gridNode.children().size() > 0 )){
            _.each($gridNode.children(), function ($child) {
                optimized = optimized || self.optimizeGridNode($($child), level + 1)
            })

        } else {
            if($gridNode.hasClass("gee-row")){
                self.unwrapRow($gridNode);
                optimized = true;
            } else if($gridNode.hasClass("gee-column")){
                self.unwrapColumn($gridNode);
                optimized = true;
            } else {
                optimized = false;
            }
        }
        return optimized;

    }

    GeeGridEngine.prototype.optimizeGrid = function() {
        var self = this;
        var optimized = true;

        while(optimized){
            optimized = false;
            optimized = optimized || self.optimizeGridNode($(self.getDocument()).find(".gee-grid"), 1)
        }
        $(self.getDocument()).find(".gee-stretch").removeClass("gee-stretch");
        var $columns = $(self.getDocument()).find(".gee-column");
        _.each($columns, function (column) {
            $(column).children().last().addClass("gee-stretch");
        });

        return;

         // return;



        //Constraint A
        /* $("#editor-iframe").contents()*//*$(".preview-container ")*/
        this.container.find('.gee-row, .gee-column').each(function() {
            if ($(this).children().size() < 2) {
                if($(this).hasClass("gee-row")){
                    self.unwrapRow(this);
                } else {
                    self.unwrapColumn(this);
                }
            }
        });
        return;

        //Constraint B
        /*$("#editor-iframe").contents()*//*$(".preview-container ")*/
        this.container.find('.gee-column').each(function() {
            if ($(this).parent().hasClass('gee-column')) {
                self.unwrapColumn(this);
               // $(this).children().first().unwrap();
            }
        });
        /*$("#editor-iframe").contents()*//*$(".preview-container ")*/this.container.find('.gee-row').each(function() {
            if ($(this).parent().hasClass('gee-row')) {
                self.unwrapRow(this);
                //$(this).children().first().unwrap();
            }
        });
    };



    GeeGridEngine.prototype.sameHighlight = function(target, command) {
        if(this.oldCommand && this.oldSelected && this.oldCommand == command && this.oldSelected.equals(target)&& this.highlight)
            return true;
        return false;
    }

    GeeGridEngine.prototype.removeHighlight = function(target, command) {
        if (!target || !command || !this.sameHighlight(target, command)) {
            if (this.highlight) {
                this.highlight.remove();
                this.highlight = null;
            }
            return true;
        } else
            return false;
    }


    GeeGridEngine.prototype.hideOutline = function(selected) {
        if (selected && selected.length) {
            selected.css({
                'outline': 'none'
            });
        }
        else {
            // //console.log(""NO ITEM IS SELECTED TO DEACTIVATE"")
        }
    };

    GeeGridEngine.prototype.showOutline = function(selected) {
        if (selected && selected.length) {
            // //console.log(""SELECTED", selected")
            selected.css({
                'outline': '1px grey dotted'
            });
        }
        else {
            // //console.log(""NO ITEM IS SELECTED TO ACTIVATE"")
        }
    };

    GeeGridEngine.prototype.getTargets = function(event, item) {
        var self = this;
        var eventX = event.clientX + $(window).scrollLeft();
        var eventY = event.clientY + $(window).scrollTop();

         //console.log(""--111--"")
        //if in iframe mode and gee-widget should be from config
        if(self.iframeMode &&  $(item).hasClass(self.options.widgetClass)){
            eventX -= self.iframe.offset().left;
            eventY -= self.iframe.offset().top;
        } else if(self.iframeMode){
            eventX -= $(window).scrollLeft();
            eventY -= $(window).scrollTop();
        }

        if (!$(item).hasClass(self.options.widgetClass)) {
            return $(self.allElementsFromPoint2(eventX, eventY));
        } else {
            return $(self.allElementsFromPoint2(eventX, eventY));
        }


    };

    GeeGridEngine.prototype.getTarget = function(event, item) {
        var self = this;
        var eventX = event.clientX + $(window).scrollLeft();
        var eventY = event.clientY + $(window).scrollTop();

        //if in iframe mode and gee-widget should be from config
        if(self.iframeMode && (!item || $(item).hasClass(self.options.widgetClass))){
            eventX -= self.iframe.offset().left;
            eventY -= self.iframe.offset().top;
        }

        return $(self.getDocument().elementFromPoint(eventX, eventY));
    };

    GeeGridEngine.prototype.filterDivTargets = function(targets, self){
        return targets.filter('div').not($(self)).not('.ui-draggable-dragging');
    };

    GeeGridEngine.prototype.filterGeeTarget = function(divs){
        return divs.filter('.gee-grid, .gee-row, .gee-column, .gee-cell').last();
    };

    GeeGridEngine.prototype.getSelectedGridElement = function(event, ui, self){
        var item = event.target;

        var targets = this.getTargets(event, item);

        if(!targets || !targets[0])
            return null;
        var divs = this.filterDivTargets(targets, self);
        if(!divs)
            return null;
        var selected = this.filterGeeTarget(divs);
        return selected;
    };

    GeeGridEngine.prototype.allElementsFromPoint = function(x, y) {
        var self = this;

        var element = self.getDocument().elementFromPoint(x, y);

        var elements = $(element).parents().map(function() {return this; }).get();

        elements.reverse();
        elements.push(element);

        return elements;

    };


    GeeGridEngine.prototype.allElementsFromPoint2 = function(x, y) {
        var self = this;
        var element, elements = [];
        var old_visibility = [];
        var count = 0;
        while (true) {
            element = self.getDocument().elementFromPoint(x, y);
            if (!element || element === self.getDocument().documentElement) {
                break;
            }
            elements.push(element);
            old_visibility.push(element.style.visibility);
            element.style.visibility = 'hidden'; // Temporarily hide the element (without changing the layout)
            count++;
            if(count > 100)
                break;
        }

        elements.reverse();
        old_visibility.reverse();

        for (var k = 0; k < elements.length; k++) {
            elements[k].style.visibility = old_visibility[k];
        }

        return elements;
    }

    GeeGridEngine.prototype.getDocument = function() {
        var self = this;
        //if iframe mode then return inside iframe
        if(!self.iframeMode){
            return document;
        } else {
            return self.iframe[0].contentWindow.document;// eDocument;//document.getElementById('editor-iframe').contentWindow.document;
        }
    };

    GeeGridEngine.prototype.updateHoveredElement = function(event) {
        var el = $(event.target);
        var hoverSelectedNew = el.closest(this.options.cellSelector);

        if (this.hoverSelected.get(0) === hoverSelectedNew.get(0)) {
            //Return if old and new hovered element are the same
            return;
        }

        this.hoverSelected.removeClass(this.options.hoverClass);

        if (!hoverSelectedNew.hasClass(this.options.cellClass)) {
            this.hoverSelected = $(''); //Reset highlighted element to default
            return;
        }

        //Highlight new element
        hoverSelectedNew.addClass(this.options.hoverClass);
        this.hoverSelected = hoverSelectedNew;
    };


    GeeGridEngine.prototype.updateHoveredElement2 = function(event) {
        var el = this.getTarget(event, null);
        var hoverSelectedNew = el.closest(this.options.cellSelector);

        if (this.hoverSelected.get(0) === hoverSelectedNew.get(0)) {
            //Return if old and new hovered element are the same
            return;
        }

        this.hoverSelected.removeClass(this.options.hoverClass);

        if (!hoverSelectedNew.hasClass(this.options.cellClass)) {
            this.hoverSelected = $(''); //Reset highlighted element to default
            return;
        }

        //Highlight new element
        hoverSelectedNew.addClass(this.options.hoverClass);
        this.hoverSelected = hoverSelectedNew;
    };

    GeeGridEngine.prototype.isNewWidget = function(item) {
        return !item.hasClass(this.options.cellClass);
    };

    GeeGridEngine.prototype.moveWidget = function(item) {
        return !item.hasClass(this.options.cellClass);
    };

    GeeGridEngine.prototype.addWidget = function(item) {
        return !item.hasClass(this.options.cellClass);
    };


    GeeGridEngine.prototype.leftSide = function(event, selected, elementX, elementY, elementWidth, elementHeight) {
        var width = elementWidth;
        var height = elementHeight;
        if(width >= 2 * height){
            width = height;
        } else if (height >= 2 * width) {
            width = width/2;
        } else {
            width = width/2;
        }

        var x = elementX;

        var chain = this.parentLRChain(selected, true);
        if(chain.length == 0)
            return selected;
        else if( x < width / 4 ){
            var stepSize = width/(4 * chain.length);
            var distance = width/4 - x;

            var index = Math.floor(distance / stepSize);
            return chain[index];
        } else {
            return selected;
        }
    }

    GeeGridEngine.prototype.rightSide = function(event, selected, elementX, elementY, elementWidth, elementHeight) {
        var width = elementWidth;
        var height = elementHeight;
        if(width >= 2 * height){
            width = height;
        } else if (height >= 2 * width) {
            width = width/2;
        } else {
            width = width/2;
        }

        var x = elementWidth - elementX;

        var chain = this.parentLRChain(selected, false);
        if(chain.length == 0)
            return selected;
        else if( x < width / 4 ){
            var stepSize = width/(4 * chain.length);
            var distance = width/4 - x;

            var index = Math.floor(distance / stepSize);

            return chain[index];
        } else {
            return selected;
        }
    }

    GeeGridEngine.prototype.topSide = function(event, selected, elementX, elementY, elementWidth, elementHeight) {
        var width = elementWidth;
        var height = elementHeight;
        if(width >= 2 * height){
            height = height/2;
        } else if (height >= 2 * width) {
            height = width/2;
        } else {
            height = height/2;
        }

        var y = elementY;


        var chain = this.parentTBChain(selected, true);
        if(chain.length == 0)
            return selected;
        else if( y < height / 4 ){
            var stepSize = height/(4 * chain.length);
            var distance = height/4 - y;

            var index = Math.floor(distance / stepSize);

            return chain[index];
        } else {
            return selected;
        }
    }

    GeeGridEngine.prototype.bottomSide = function(event, selected, elementX, elementY, elementWidth, elementHeight) {
        var width = elementWidth;
        var height = elementHeight;
        if(width >= 2 * height){
            height = height/2;
        } else if (height >= 2 * width) {
            height = width/2;
        } else {
            height = height/2;
        }

        var y = elementHeight - elementY;

        var chain = this.parentTBChain(selected, false);
        if(chain.length == 0)
            return selected;
        else if( y < height / 4 ){
            var stepSize = height/(4 * chain.length);
            var distance = height/4 - y;

            var index = Math.floor(distance / stepSize);
            return chain[index];
        } else {
            return selected;
        }
    }


    GeeGridEngine.prototype.parentLRChain = function(selected, left) {
        var chain = [];

        var current = selected;
        while(true){
            if(current.parent().hasClass('gee-column')){

                current = current.parent();
                chain.push(current);
            } else if (current.parent().hasClass('gee-row')) {

                if(left && current.parent().children(":not(#highlight)").first().equals(current)){
                    current = current.parent();
                } else if(!left && current.parent().children(":not(#highlight)").last().equals(current)){
                    current = current.parent();
                } else {
                    break;
                }
            } else {
                break;
            }

        }
        return chain;
    }

    GeeGridEngine.prototype.parentTBChain = function(selected, top) {
        var chain = [];
        var current = selected;
        while(true){

            if(current.parent().hasClass('gee-row')){
                current = current.parent();
                chain.push(current);
            } else if (current.parent().hasClass('gee-column')) {

                if(top && current.parent().children(":not(#highlight)").first().equals(current)){
                    current = current.parent();
                } else if(!top && current.parent().children(":not(#highlight)").last().equals(current)){
                    current = current.parent();
                } else {
                    break;
                }
            } else {
                break;
            }

        }
        return chain;
    }

    GeeGridEngine.prototype.leftSideAppender = function(selected) {
        var self = this;

        if (selected.parent().hasClass('gee-row')) {
        }
        else {
            this.wrapRow(selected)

        }

        self.addHighlightBefore(selected, "left");
    }

    GeeGridEngine.prototype.rightSideAppender = function(selected) {
        if (selected.parent().hasClass('gee-row')) {
             //console.log(""right, existing"")

            //Adding to the existing row, just recalculate size
        }
        else {
             //console.log(""right, new"")
            this.wrapRow(selected)

            //wrapping into the row and resize
        }


        this.addHighlightAfter(selected, "right");
    }

    GeeGridEngine.prototype.topSideAppender = function(selected) {
        var rowWrapped = false;
        if (selected.parent().hasClass('gee-column')) {
             //console.log(""top, existing"")
        }
        else {
             //console.log(""top, new"")
            this.wrapColumn(selected)
            rowWrapped = true;


        }

        if(rowWrapped){
            if(selected.hasClass("gee-row")){
                 //console.log(""SIZE COLUMN R TOP=" + 12")
                this.setColSize(selected.parent(), 12)
            } else {
                var size = this.getColSize(selected);
                 //console.log(""SIZE TOP=" + size")
                this.setColSize(selected.parent(), size)
                this.setColSize(selected, 12)
            }
        }

        this.addHighlightBefore(selected, "top");
        this.setColSize(this.highlight, 12)
    }

    GeeGridEngine.prototype.bottomSideAppender = function(selected) {
        var rowWrapped = false;
        if (selected.parent().hasClass('gee-column')) {
             //console.log(""bottom, existing"")
        }
        else {
             //console.log(""bottom, new"")
            this.wrapColumn(selected)
            rowWrapped = true;

        }


        if(rowWrapped){
            if(selected.hasClass("gee-row")){
                 //console.log(""SIZE COLUMN R BTM=" + 12")
                this.setColSize(selected.parent(), 12)
            } else {
                var size = this.getColSize(selected);
                 //console.log(""SIZE BOTTOM=" + size")
                this.setColSize(selected.parent(), size)
                this.setColSize(selected, 12)
            }
        }

        this.addHighlightAfter(selected, "bottom");

        this.setColSize(this.highlight, 12)
    }


    GeeGridEngine.prototype.getRelativeElementCoordinats = function(item, event, selected) {
        var result = {};
        var x;
        var y;

        if(item.hasClass("gee-widget")){
             x = (event.pageX - selected.offset().left);// + $(window).scrollLeft()/**/;
             y = (event.pageY - selected.offset().top);// + $(window).scrollTop()/**/;
         //    //console.log(""X=" + x")
            //Update position with offset, if the element is newly created by dragging into an iframe

            if(this.iframeMode){

                x -= this.iframe.offset().left ;
                y -= this.iframe.offset().top;
            }

        } else{
            if (event.clientX > selected.offset().left) {
                x = event.clientX - selected.offset().left;
            }
            else {
                x = event.clientX % selected.width();
            }
            if (event.clientY > selected.offset().top) {
                y = event.clientY - selected.offset().top;
            }
            else {
                y = event.clientY % selected.height();
            }
        }

        result.x = x;
        result.y = y;
        return result;
    };

    GeeGridEngine.prototype.resizeContainer = function (container) {
        var self=this;
        var children = $(container).children();
        var totalSize = 12;

         //console.log("children")

        var itemsCount = children.length;

        _.each(children, function (child) {
            var size = Math.round(totalSize / itemsCount);
            totalSize -= size;
            itemsCount--;

            self.setColSize(child, size);
        });

    };


    GeeGridEngine.prototype.wrapColumn = function (item) {
        item.wrap(wrapperColumn);

        if(item.hasClass("row")){
            this.setColSize(item.parent(), 12);
        } else {
            var size = this.getColSize(item)
            this.setColSize(item.parent(), size);
        }

        item.addClass("gee-stretch");
    }

    GeeGridEngine.prototype.unwrapColumn = function (container) {
        var $column = $(container);
        var $child = $(container).children().first();
        var size =  this.getColSize($column);

        this.setColSize($child, size);
        $(container).children().first().unwrap();
    }


    GeeGridEngine.prototype.wrapRow = function (item) {
        item.wrap(wrapperRow);
        if(item.hasClass("gee-stretch")){
            item.parent().addClass("gee-stretch")
        }
    }

    GeeGridEngine.prototype.unwrapRow = function (container) {
        var $child = $(container).children().first();
        // only if child isn't row itself

        this.setColSize($child, 12);

        $(container).children().first().unwrap();
    }

    GeeGridEngine.prototype.focus = function () {
        if(true){
            var data = {};
            data.message = "focus";
            data.selector = ".gee-grid";
            this.iframe[0].contentWindow.postMessage( JSON.stringify(data), '*')
        } else {
            Focusable.setFocus($(".gee-grid"), {});
        }
    }

    GeeGridEngine.prototype.unfocus = function () {
        if(true){
            var data = {};
            data.message = "unfocus";
            data.selector = ".gee-grid";
            this.iframe[0].contentWindow.postMessage( JSON.stringify(data), '*')
        } else {
           // Focusable.setFocus($(".gee-grid"), {});
        }
    }

    GeeGridEngine.prototype.onStartDragging = function(event, ui) {
        var self = this;
        item = $(event.target);
        draggable = $(ui.helper);

        this.removeResizableElements();

        //this.originalContainer = this.item.parent();

        //If the item is newly created, grab its HTML snippet
        if (item.hasClass(this.options.widgetClass)) {

            var html = "<p>Some text</p>";

            if(self.options.widgets){
                var widgetKey = item.attr("id");
                var widget = _.findWhere( self.options.widgets, { key : widgetKey } );
                if(widget && widget.html)
                    html = widget.html;

            }


            element = $('#toolbox').clone().append("<div class='col-xs-12 col-md-12 widget-content'>" + html + "</div>");//$('#toolbox').clone().append( $('#' + item.attr('id').split('-')[0]).clone() ); //$('#' + item.attr('id').split('-')[0]).clone();
            element.attr("key", item.attr("id"))
        }

        draggable.css("width", item.css("width")).css("height", item.css("height")).css("display", "block").css("opacity", "0.5");
        //we really just need to set css class from parameters
        //and raise  subscripton event
    }


    GeeGridEngine.prototype.ptInTriangle = function(p, p0, p1, p2) {
        var A = 1/2 * (-p1.y * p2.x + p0.y * (-p1.x + p2.x) + p0.x * (p1.y - p2.y) + p1.x * p2.y);
        var sign = A < 0 ? -1 : 1;
        var s = (p0.y * p2.x - p0.x * p2.y + (p2.y - p0.y) * p.x + (p0.x - p2.x) * p.y) * sign;
        var t = (p0.x * p1.y - p0.y * p1.x + (p0.y - p1.y) * p.x + (p1.x - p0.x) * p.y) * sign;

        return s > 0 && t > 0 && (s + t) < 2 * A * sign;
    }

    GeeGridEngine.prototype.isLeftSide = function(pt, width, height) {
        if(width >= 2 * height){
            var p1 = {x:0, y:0};
            var p2 = {x: height, y: height/2 };
            var p3 = {x: 0, y: height };
            return this.ptInTriangle(pt, p1, p2, p3);
        } else if (height >= 2 * width) {
            var p1 = {x:0, y:0};
            var p2 = {x: width/2, y: width};
            var p3 = {x: width/2, y: height - width};
            var p4 = {x: 0, y: height};
            return this.ptInTriangle(pt, p1, p2, p3) || this.ptInTriangle(pt, p1, p3, p4) ;
        } else {
            var p1 = {x:0, y:0};
            var p2 = {x: width/2, y: height/2 };
            var p3 = {x: 0, y: height };
            return this.ptInTriangle(pt, p1, p2, p3);
        }
        return false;
    }

    GeeGridEngine.prototype.isRightSide = function(pt, width, height) {
        if(width >= 2 * height){
            var p1 = {x:width, y:0};
            var p2 = {x: width, y: height };
            var p3 = {x: width - height, y: height / 2 };
            return this.ptInTriangle(pt, p1, p2, p3);
        } else if (height >= 2 * width) {
            var p1 = {x: width, y:0};
            var p2 = {x: width, y: height};
            var p3 = {x: width/2, y: height - width};
            var p4 = {x: width/2, y: width};
            return this.ptInTriangle(pt, p1, p2, p3) || this.ptInTriangle(pt, p1, p3, p4) ;
        } else {
            var p1 = {x:width, y:0};
            var p2 = {x: width, y: height };
            var p3 = {x: width/2, y: height/2 };
            return this.ptInTriangle(pt, p1, p2, p3);
        }
        return false;
    }

    GeeGridEngine.prototype.isTopSide = function(pt, width, height) {
        if(width >= 2 * height){
            var p1 = {x:0, y:0};
            var p2 = {x: width, y: 0 };
            var p3 = {x: width - height, y: height/2 };
            var p4 = {x: height, y: height/2};
            return this.ptInTriangle(pt, p1, p2, p3) || this.ptInTriangle(pt, p1, p3, p4) ;
        } else if (height >= 2 * width) {
            var p1 = {x:0, y:0};
            var p2 = {x: width, y: 0};
            var p3 = {x: width/2, y: width};
            return this.ptInTriangle(pt, p1, p2, p3) ;
        } else {
            var p1 = {x:0, y:0};
            var p2 = {x: width, y: 0};
            var p3 = {x: width/2, y: height/2};
            return this.ptInTriangle(pt, p1, p2, p3) ;
        }
        return false;
    }

    GeeGridEngine.prototype.isBottomSide = function(pt, width, height) {
        if(width >= 2 * height){
            var p1 = {x:0, y:height};
            var p2 = {x: height, y: height/2};
            var p3 = {x: width - height, y: height/2 };
            var p4 = {x: width, y: height};
            return this.ptInTriangle(pt, p1, p2, p3) || this.ptInTriangle(pt, p1, p3, p4) ;
        } else if (height >= 2 * width) {
            var p1 = {x:0, y:height};
            var p2 = {x: width/2, y: height/2};
            var p3 = {x: width, y: height};
            return this.ptInTriangle(pt, p1, p2, p3) ;
        } else {
            var p1 = {x:0, y:height};
            var p2 = {x: width, y: 0};
            var p3 = {x: width, y: height};
            return this.ptInTriangle(pt, p1, p2, p3) ;
        }
        return false;
    }

    GeeGridEngine.prototype.onDragging = function(event, ui) {
        var self = this;
        if(this.stopEverything)
            return;

        this.hideOutline(this.selected);
        var selected = this.getSelectedGridElement(event, ui, this);

        if(!selected)
            return;

        var item = $(event.target);

        $('#debug').text(selected.attr("class"));

        if(selected) {
            if (selected.hasClass(this.options.gridClass)) {
                //add to emptee gee-grid // should remove this option
                if (selected.children().length == 0) {

                    //Insert new cell in an empty con tainer,
                    //in bootstrap it means that we need to append col 12 to
                    this.addHighlight(selected);
                    //fix highlight size
                    this.highlight.attr("data-col-size", 12);
                    this.highlight.attr("data-insert-action", "replace")
                }
            } else if (selected.hasClass('gee-cell')) {
                // //console.log(""CELL"")

              //  this.optimizeTable();

                var position = "none";

                //Collect data to compute position of placeholder over element
                var xy = this.getRelativeElementCoordinats(item, event, selected);
                var elementX = xy.x;
                var elementY = xy.y;

                 //console.log(""RELATIVE X=" + elementX + ", Y=" + elementY")

                var elementWidth = selected.outerWidth(); //width();
                var elementHeight  = selected.outerHeight(); //height();

                var m_p0 = {x:elementWidth/2, y:elementHeight/2};
                var lt_p1 = {x:0, y:0};
                var rt_p2 = {x: elementWidth, y:0}
                var rb_p3 = {x: elementWidth, y:elementHeight}
                var lb_p4 = {x: 0, y:elementHeight}

                var pt = {x: elementX, y:elementY};

                var sideHandlers= [];
                sideHandlers.push({side: 'left',
                    handler : this.leftSide,
                    appender :  this.leftSideAppender,
                   // position: elementX / elementWidth,
                    insideTriangle: self.isLeftSide(pt, elementWidth, elementHeight)});
                    //self.ptInTriangle(pt, m_p0, lt_p1, lb_p4 )});

                sideHandlers.push({side: 'right',
                    handler : this.rightSide,
                    appender :  this.rightSideAppender,
                    //position: 1 - elementX / elementWidth,
                    insideTriangle: self.isRightSide(pt, elementWidth, elementHeight)});
                //self.ptInTriangle(pt, m_p0, rt_p2, rb_p3 )});

                sideHandlers.push({side: 'top',
                    handler : this.topSide,
                    appender : this.topSideAppender,
                   // position: elementY / elementHeight,
                    insideTriangle: self.isTopSide(pt, elementWidth, elementHeight)});
                        //self.ptInTriangle(pt, m_p0, lt_p1, rt_p2 )});

                sideHandlers.push({side: 'bottom',
                    handler : this.bottomSide,
                    appender :  this.bottomSideAppender,
                    //position: 1 - elementY / elementHeight,
                    insideTriangle: self.isBottomSide(pt, elementWidth, elementHeight)});
                //self.ptInTriangle(pt, m_p0, lb_p4, rb_p3 )});


                var handler = _.max(sideHandlers, function(o) { return o.insideTriangle; });//_.minBy(sideHandlers, function(o) { return o.position; });

                selected = handler.handler.call(this, event, selected, elementX, elementY, elementWidth, elementHeight);


                var highlightRemoved = this.removeHighlight(selected, handler.side);
                if(highlightRemoved){
                    this.optimizeGrid();
                    handler.appender.call(this, selected);
                }

            }
        }
        this.showOutline(selected);
        this.selected = selected;
       // this.optimizeTable();
    }

    GeeGridEngine.prototype.onStopDragging = function(event, ui) {
        var self = this;
        if(this.stopEverything)
            return;

        this.hideOutline(this.selected);

        var item = $(event.target);

        //debug info
        if ( this.hasHighlight()){

             //console.log(""hasHighlight - true"")
        } else {

             //console.log(""hasHighlight - false"")
        }


        //we move element inside grid
        if(this.isNewWidget(item)){

            var size = this.getColSize(this.highlight);

            item.css("visibility", "visible");

             //console.log("element")
             //console.log("this.highlight")

            item = this.highlight.replaceWithPush(element).css("opacity", "1");
             //console.log(""ITEM"")

            var key = element.attr("key");


            if (!item.hasClass('gee-grid')) {
                item = item.wrap(wrapperCell).parent();

                this.initCellHandlers(item);


                this.setColSize(item, size);
                item.attr("id", this.geeGrid.nextId());
            }

            var newParent = $(item).parent();
            if(newParent.hasClass("gee-row")) {
                this.resizeContainer(newParent);
            }

             //console.log(""DISPATCH"")
            var data = {id: item.attr("id"), key: key, item: item}
             //console.log("item")
            this.geeGrid.dispatch("widget-created", data);

            if(self.options.widgets){
                 //console.log(""WIDGETS 1"")
                var widget = _.findWhere( self.options.widgets, { key : key } );
                 //console.log(""WIDGETS 2"")
                 //console.log("widget")
                if(widget && widget.onInit) {
                     //console.log(""WIDGETS 3"")
                    widget.onInit(item);
                }
            }

            ///Get widget and onInit

//.widget-content
            item.on('click', '.w-option-outer', function() {

                var data = {};
                data.id = $(this).closest('.gee-cell').attr('id');
                self.geeGrid.dispatch("option-btn", data);

            });

            item.on('click', '.w-delete-outer', function() {
                var data = {};
                data.id = $(this).closest('.gee-cell').attr('id');
                self.geeGrid.dispatch("delete-btn", data);

                var $parent = $(this).closest('.gee-cell').parent();

                $(this).closest('.gee-cell').remove();
                if($parent.hasClass("gee-row")) {
                    self.resizeContainer($parent);
                }
                self.optimizeGrid();

            });
        } else {


            var size = this.highlight.attr("data-col-size");

            var oldParent = $(item).parent();
            item = this.highlight.replaceWithPush(item).css("opacity", "1");
            item.css("visibility", "visible");
            var newParent = $(item).parent();
           // oldParent.remove(item);

             //console.log("oldParent")
             //console.log("newParent")
             //console.log("oldParent.children()")
            $(ui.helper).remove(); //destroy clone
            $(ui.draggable).remove();
            // if(oldParent.hasClass("gee-row")){}
            if(oldParent.hasClass("gee-row")) {
                 //console.log(""resize parent"")
                this.resizeContainer(oldParent);
            }
            if(newParent.hasClass("gee-row")) {
                this.resizeContainer(newParent);
            }

        }
        //this.optimizeTable();
        this.optimizeGrid();

    }

    GeeGridEngine.prototype.addResizableElements = function ($row) {
        var self = this;
        var $children = $row.children();
        var length = $children.length;
        var i = 1;
        _.each($children, function (child) {
            if(i < length) {
                 //$el = $("<div class='gee-resize-control'></div>");
              //$el.insertAfter($(child));
                $(child).addClass("gee-resize-control");
                if(!$(child).resizable( "instance" )) {
                    $(child).resizable(self.options.resizableParameters);
                }
                i++;
            }
        });
    }

    GeeGridEngine.prototype.removeResizableElements = function () {
        var $container = $(this.getDocument()).find(".gee-grid");
        //$container.find(".gee-resize-control").remove();
        $container.find(".gee-resize-control").removeClass("gee-resize-control")
        _.each($container.find("div"), function (el) {
            if($(el).resizable( "instance" )){
                $(el).resizable( "destroy" );
            }
        })
    }

    GeeGridEngine.prototype.hideResizableElements = function () {
        var $container = $(this.getDocument()).find(".gee-grid");
        $container.find(".gee-resize-control").removeClass("gee-resize-control")
    }

    GeeGridEngine.prototype.handleResizableElements = function (event) {
        var self = this;
        if(this.geeGrid.stopUpdatingResizeHandlers){
            return;
        }
        var $el = this.getTarget(event, null);
        var $hoveredRow = $el.closest(this.options.rowSelector);

        this.hideResizableElements();
        this.addResizableElements($hoveredRow)

/*        if($hoveredRow.length == 0){
            this.$oldHoveredRow  = null;
            this.hideResizableElements();
        } else if ($hoveredRow && this.$oldHoveredRow && this.$oldHoveredRow.get(0) === $hoveredRow.get(0)){
            return;
        } else {
            this.$oldHoveredRow  = $hoveredRow;
            this.hideResizableElements();
            this.addResizableElements($hoveredRow)

        }*/
    }

    GeeGridEngine.prototype.findResizedElement = function(current, side) {
        var element = current;
        //var tryElement = true
        while(true){
             //console.log("element")
            if(element.parent().hasClass("gee-row")) {
                if(side == "left"){
                    if(element.prev().length > 0)
                        return element;
                } else {
                    if(element.next().length > 0)
                        return element;
                }
                element = element.parent();
            } else {
                element = element.parent();
                if(element.hasClass("gee-grid"))
                    return null;
            }
        }
        return element;
    }

    var GeeGrid = function(el, options) {
        var self = this;

        options = options || {};

        this.options = _.defaults(options || {}, {
            mode: "embed", // modes "embed" and "iframe"
            widgetContainer: ".gee-widgets",
            widgetSelector: ".gee-widget",
            widgetClass: "gee-widget",
            widgetFixedSize: ".gee-widget-fixed",
            gridClass: "gee-grid",
            cellClass: "gee-cell",
            rowClass: "gee-row",
            colClass: "gee-col",
            cellSelector: ".gee-cell",
            rowSelector: ".gee-row",
            colSelector: ".gee-col",
            hoverClass: "w-hover"
        });


        this.listeners = {};

        this.idPrefix = "gee-widget-node-";
        this.idCounter = 0;

         //console.log("this.options.containerSelector")
        if(this.options.mode == 'iframe'){
            this.container = $(el).find(this.options.containerSelector);
            this.iframe = $(el);

            $(el).on('load', function(){
                self.container = $(el).contents().find(self.options.containerSelector);
                 //console.log(""CONTAINER-CONTAINER1"")
                 //console.log("self.container")
            });
        } else{
            this.container = $(el);
        }


        this.stopMouseMove = false;
        this.stopUpdatingResizeHandlers = false;
        this.grid = new GeeGridEngine(el, options, this);

/*        $(document).keypress(function(event) {
            if(event.charCode == 122)
                self.grid.stopEverything = !self.grid.stopEverything;
        });
 */
         //console.log(""CONTAINER-CONTAINER"")
         //console.log("this.container")
         //console.log("$(el)")

/*        self.container.on('click', '.w-option-outer', function() {
             //console.log(""AAAAAA_A_A_A_A_A_"")
            var data = {};
            data.id = $(this).closest('.gee-cell').attr('id');
            self.dispatch("option-btn", data);
             //console.log(""BBBBBBAAAAA_A_A_A_A_A_"")
        });


        var iframeBody = $('body', $('#iframe')[0].contentWindow.document);

        self.container.on('click', '.w-option', function() {
             //console.log(""AAAAAA_A_A_A_A_A_"")
            var data = {};
            data.id = $(this).closest('.gee-cell').attr('id');
            self.dispatch("option-btn", data);
             //console.log(""BBBBBBAAAAA_A_A_A_A_A_"")
        });*/


        if(this.options.mode == 'iframe'){
            $(document).on("mousemove", function( event ) {
                event.target = null;
                if(!self.stopMouseMove) {
                    self.grid.updateHoveredElement2.call(self.grid, event);
                    self.grid.handleResizableElements.call(self.grid, event);
                }
            });


        } else {
            this.container.on("mousemove", function( event ) {
                if(!self.stopMouseMove)
                    self.grid.updateHoveredElement.call(self.grid, event);
                    self.grid.handleResizableElements.call(self.grid, event);
            });

        }


        $(self.grid.getDocument()).on('click', '.widget-content a', function(e) {
            e.preventDefault();
        });

        function bubbleIframeMouseMove(iframe){
            // Save any previous onmousemove handler
            var existingOnMouseMove = iframe.contentWindow.onmousemove;

            // Attach a new onmousemove listener
            iframe.contentWindow.onmousemove = function(e){
                // Fire any existing onmousemove listener
                if(existingOnMouseMove) existingOnMouseMove(e);

                // Create a new event for the this window
                var evt = document.createEvent("MouseEvents");

                // We'll need this to offset the mouse move appropriately
                var boundingClientRect = iframe.getBoundingClientRect();

                // Initialize the event, copying exiting event values
                // for the most part
                evt.initMouseEvent(
                    "mousemove",
                    true, // bubbles
                    false, // not cancelable
                    window,
                    e.detail,
                    e.screenX,
                    e.screenY,
                    e.clientX + boundingClientRect.left,
                    e.clientY + boundingClientRect.top,
                    e.ctrlKey,
                    e.altKey,
                    e.shiftKey,
                    e.metaKey,
                    e.button,
                    null // no related element
                );

                evt.targetOriginal = e.target;

                // Dispatch the mousemove event on the iframe element
                iframe.dispatchEvent(evt);
            };
        }

// Get the iframe element we want to track mouse movements on
        var myIframe = $(el)[0];//document.getElementById("myIframe");

// Run it through the function to setup bubbling
 bubbleIframeMouseMove(myIframe);




        ///

        var sibTotalWidth;
        var container;
        var maxCol;
        var resizeSide;
        var canResize = false;
        var resizedEl, connectedEl;
        this.options.resizableParameters = {
            handle: "e",
            start: function(event, ui) {
                self.stopUpdatingResizeHandlers = true;
                resizeSide = "right"
                resizedEl = self.grid.findResizedElement(ui.originalElement, resizeSide);
                console.log("RESIZED")

                console.log(resizedEl)

                if(resizedEl){
                    if(resizeSide == "left"){
                        connectedEl = resizedEl.prev(".gee-cell, .gee-column");
                    } else {
                        connectedEl = resizedEl.next();
                    }
                    canResize = true;
                } else {
                    canResize = false;
                }
                console.log("CONNECTED")

                console.log(connectedEl)


                if(!canResize) {
                    return;
                }

                item = $(resizedEl);
                container  = item.parent();

                item.css("opacity", "0.7");

                col_width = container.innerWidth() /12;

                sibTotalWidth = resizedEl.outerWidth() + connectedEl.outerWidth();

                console.log("sibTotalWidth=" + sibTotalWidth)


                maxCol = Math.round(sibTotalWidth/col_width);

                console.log("maxCol=" + maxCol)
                return false;

            },
            stop: function(event, ui) {

                item.css("opacity", "1");

                var resized = $(this);
                resized.queue(function() {
                    ui.originalElement.removeAttr( "style" );
                    if (connectedEl){
                        connectedEl.removeAttr( "style" );
                    }
                    $( this ).dequeue();
                });
                self.stopUpdatingResizeHandlers = false;
            },
            resize: function(event, ui){

                if(!canResize) {
                    return;
                }

                if (ui.size.width > col_width){
                    var col_currrent_element = Number((ui.size.width/col_width).toFixed(0));
                    if(col_currrent_element >= maxCol)
                        col_currrent_element = maxCol - 1;
                } else {
                    var col_currrent_element = 1;
                }

                resizedEl.removeClass (function (index, css) {
                    return (css.match (/(^|\s)col-md-\S+/g) || []).join(' ');
                });
                resizedEl.addClass("col-md-"+ col_currrent_element);


                if (connectedEl){

                    if (col_currrent_element < maxCol ){
                        connectedEl.removeClass (function (index, css) {
                            return (css.match (/(^|\s)col-md-\S+/g) || []).join(' ');
                        });
                        connectedEl.addClass("col-md-"+ (maxCol - col_currrent_element));
                    } else {
                        connectedEl.removeClass (function (index, css) {
                            return (css.match (/(^|\s)col-md-\S+/g) || []).join(' ');
                        });
                        connectedEl.addClass("col-md-" + maxCol);
                    }



                }

            },

            handles: 'e',//'e, w'
            animate: true,
           // ghost: true,
            grid: 20,
        }

        this.options.draggableParameters = {
            start: function(event, ui) {
                self.stopMouseMove = true;
                self.grid.focus();
                self.grid.onStartDragging(event, ui); // call(self.grid,
            },
            drag: function(event, ui) {
                self.grid.onDragging(event, ui);
            },
            stop: function(event, ui) {
                self.grid.onStopDragging(event, ui);
                self.grid.unfocus();
                self.stopMouseMove = false;
            },
            helper: 'clone',
            cancel: '.w-resize-l-outer, .w-resize-r-outer',
            containment: "window",
            distance: 30,
            iframeFix: true,
            zIndex: 9998
        }

        this.initWidgets();

    }

    GeeGrid.prototype.nextId = function(){
        this.idCounter++;
        return this.idPrefix + this.idCounter;
    }

    GeeGrid.prototype.getItem = function (id) {
        var self = this;
        return $(self.grid.getDocument()).find("#" + id);
    }


    GeeGrid.prototype.updateHtml = function (id, html) {
        var self = this;

        if (self.iframe) {
            var data = {};
            data.message = "set-html";
            data.selector = "#" + id + " .widget-content";
            data.html = html;

            var scripts = [];
            if(html && html.match(/<script>(.*?)<\/script>/g))
            {
                scripts = html.match(/<script>(.*?)<\/script>/g).map(function (val) {
                    return val.replace(/<\/?script>/g, '');
                });
            }
            data.scripts = scripts;
            data.cleanHtml = $.parseHTML(html)

          $(self.grid.getDocument()).find("#" + id + " .widget-content").html(data.cleanHtml).promise().done(function() {
                self.iframe[0].contentWindow.postMessage( JSON.stringify(data), '*')

            });

        } else {
            this.container.find("#" + id + " .widget-content").html(html);
        }
    }

    GeeGrid.prototype._toJson = function (el) {
        var self = this;
        var node = {};

        node.css = el.attr("class");

        if(el.attr("id"))
            node.node_id = el.attr("id");
        if(el.attr("data-col-size"))
            node.size = el.attr("data-col-size");

        var children = []
        el.children().map(function(index, elem) {
            if($(elem).hasClass("gee-grid") || $(elem).hasClass("gee-row") || $(elem).hasClass("gee-column") || $(elem).hasClass("gee-cell") ){
                children.push(self._toJson($(elem)))
            }
        });

        if(children.length > 0){
            node.nodes = children;
        }
        return node;
    }



    GeeGrid.prototype.toJson = function () {
        var self = this;
        var structure = self._toJson($(self.grid.getDocument()).find(".gee-grid"));

        return [structure];
    }

    GeeGrid.prototype._fromJson = function ($node, node) {
        var self = this;
        _.each(node.nodes, function (childNode) {
            var $childNode = $("<div>");
            $childNode.attr("class", childNode.css);
            $node.append($childNode);


            if($childNode.hasClass("gee-cell")){
                var $element = $('#toolbox').clone().append("<div class='col-xs-12 col-md-12 widget-content'></div>");
                $childNode.append($element);
                $childNode.attr("id", childNode.nodeId)

                self.grid.initCellHandlers($childNode)
                $childNode.on('click', '.w-option-outer', function() {
                    var data = {};
                    data.id = $(this).closest('.gee-cell').attr('id');
                    self.dispatch("option-btn", data);
                });

                $childNode.on('click', '.w-delete-outer', function() {
                    var data = {};
                    data.id = $(this).closest('.gee-cell').attr('id');
                    self.dispatch("delete-btn", data);

                    var $parent = $(this).closest('.gee-cell').parent();

                    $(this).closest('.gee-cell').remove();
                    if($parent.hasClass("gee-row")) {
                        self.grid.resizeContainer($parent);
                    }
                    self.grid.optimizeGrid();
                });

                var idParts = childNode.nodeId.split("-");
                var id = idParts[idParts.length - 1];

                if(id > self.idCounter){
                    self.idCounter = id++;
                }
                //TODO: init all handlers
            } else {
                self._fromJson($childNode, childNode);
            }
        });

    }

    GeeGrid.prototype.fromJson = function (structure) {
        var self = this;
        var $rootContainer = $(self.grid.getDocument()).find(".gee-grid");
        if(structure.length == 1){
            self._fromJson($rootContainer, structure[0]);
        }
      //  for
    }

    GeeGrid.prototype.dispatch = function (eventName, data) {
        if(this.listeners[eventName]) {
            for(var i=0; i<this.listeners[eventName].length; i++) {
                this.listeners[eventName][i](data);
            }
        }
    }
    GeeGrid.prototype.on = function (eventName, listener) {
        if(!this.listeners[eventName])
            this.listeners[eventName] = [];
        this.listeners[eventName].push(listener);
    }

    GeeGrid.prototype.destroy = function () {
        console.log("destroy");
        //Destroy draggable
        $(".editor-menu-widget").draggable("destroy");
        $(this.grid.getDocument()).find(".gee-cell").draggable("destroy");
        //Destroy resizable


        //on hover
        $(document).off("mousemove");
    }




    GeeGrid.prototype.initWidgets = function() {
        var self = this;

        if(self.options.widgets) {
            _.each(self.options.widgets, function (widget) {
                var widget = new GeeWidget($("#" + widget.key), self.options, widget);
                 //console.log(""WIDGET"")
                 //console.log("widget")
            })


        } else if(self.options.widgetContainer && self.options.widgetSelector) {
            $(self.options.widgetContainer)
                .find(self.options.widgetSelector)
                .each(function(index, el) {
                    var widget = new GeeWidget(el, self.options);
                     //console.log("widget")
                });
        }
    };

    $.fn.geegrid = function(options) {
        return this.each(function() {
            var o = $(this);
            if (!o.data('geegrid')) {
                o.data('geegrid', new GeeGrid(this, options));
            }
        });
    };

    scope.GeeGrid = GeeGrid;
    scope.GeeGrid.Widget = GeeWidget;

    return scope.GeeGrid;
});