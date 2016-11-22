define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-content', 'gc-content-layout', 'knockout-validation', 'gc-widget' ,'gc-media-asset', 'geegrid', 'ckeditor-jquery'], function(app, ko, router, gc, contentAPI, contentLayoutAPI, validation, widgetAPI, mediaAssetAPI, geegrid, ckeditorJquery) {

    function ToolboxGroup(key, label, items){
        var self = this;
        self.key = key;
        self.label = label;
        self.items = items;
    }

    function ToolboxItem(key, label, html, type, icon){
        var self = this;
        self.key = key;
        self.icon = icon;
        self.label = label;
        self.html = html;
        self.type = type;
    }

    function ContentVM(geegrid){
        var self = this;
        // self.id = id;
        self.nodes = [];
        self.geegrid = geegrid;
        self.ckeditorConfigs;
        self.widgets = {};

        self.type = ko.observable('PARTIAL');
        
        self.isNew = function () {
            return false;
        }

        self.contentId = ko.observable();

        self.selectedNode = ko.observable();
        self.isShowOptions = ko.observable(false);

        self.selectedTab = ko.observable();
        self.selectedParameterValues = ko.observableArray([])

        self.switchTab = function(tab) {
            self.selectedTab(tab);
        }

        self.switchTabBack = function() {
            self.selectedTab(self.selectedTab().parentTab);
        }

        self.showOptions = function(nodeId) {
            var node = _.findWhere( self.nodes, { id : nodeId } );
            self.selectedNode(node);

            // node.showOptions();
            self.cloneParameters();
            self.selectedTab(node.tab())
            self.isShowOptions(true);

// $("#cmsPanel.cms-full-mode").css("position", "static");
        }


        self.removeNode = function(nodeId) {
            // remove from nodes
            self.nodes = _.without(self.nodes, _.findWhere( self.nodes, { id : nodeId } ));
        }

        self.saveOptions = function () {
            $("#cmsPanel.cms-full-mode").css("position", "");

            self.isShowOptions(false);
            self.saveParameterValues()
            self.selectedNode().generatePreviewHtml(function (html) {
               // console.log("SET PREVIEW HTML TO IFRAME")
              // console.log(self.geegrid)
                self.geegrid.updateHtml(self.selectedNode().id, html)
            });

            self._onChange();
        }

        self.cancelOptions = function () {
            $("#cmsPanel.cms-full-mode").css("position", "");
        }

        self.updateContent = function(nodeId, content){
            var node = _.findWhere( self.nodes, { id : nodeId } );

            node.content(content);

            self._onChange();
        }

        self._onChange = function (event) {
            gc.app.triggerSaveAlert(event);
        }

        self.cloneParameters = function () {
            var parameterValues = [];

            self.selectedNode().cloneParameters();

/*
 * _.each(self.selectedNode().parameterValues(), function (param) { parameterValues.push(param.clone()); })
 * 
 * self.selectedParameterValues(parameterValues);
 */
        }

        self.saveParameterValues = function () {
            self.selectedNode().saveParameters();// .parameterValues(self.selectedParameterValues())
        }

        self.addNode = function(id, widgetKey){
            var contentNode = new ContentNodeVM(id, self);
            var widget = self.widgets[widgetKey];

            if(widget != null){
                contentNode.setWidget(widget);

                if(self.geegrid) {
                    contentNode._initWidget();
                    self._onChange();
                }
            }

            contentNode.key = widgetKey;
            self.nodes.push(contentNode);

            return contentNode;
        }



        self.previewURL = ko.computed(function() {
            if(self.isNew()){
                return "#"
            }
            var reqCtx = gc.app.activeRequestCtx();
            if(self.type() == 'PARTIAL') {
                return 'https://' + reqCtx.urlPrefix + "/content/preview/" + self.contentId() + '?xt=' + new Date().getTime();
            } else {
                return 'https://' + reqCtx.urlPrefix + "/content/page/" + self.contentId() + '?xt=' + new Date().getTime();
            }
        });

    }

    function ContentNodeVM(id, contentVM) {
        var self = this;
        self.id = id;
        self.contentVM = contentVM;

        self.tab = ko.observable();

        self.widget = null;
        self.key = null;

        self.parameterValues = ko.observableArray([]);
        self.content = ko.observable("");

        self.preview = ko.observable("");
        self.editor = null;

        self.previewHtml = ko.computed(function() {
            if(self.preview())
                return self.preview();

            return '<div class="empty-node">No preview for such widget parameters</div>';
        });

        self.showOptions = function () {
            self.selectedTab(self.tab);
        }

        
        self.maSystemPath = function () {
            return "system/cms/widgets/" + self.widget.code();
        }


        self._initCKEditorWidget = function(){
            var $itemCell =  self.contentVM.geegrid.getItem(self.id);
            $itemCell.find(".w-option-outer").hide();
            var ckeditorConfig = self.contentVM.ckeditorConfigs[self.widget.config.ckeditor];
            var content = self.content() ? self.content() : self.widget.content;
            content = "<div data-bind='ckeditor' contenteditable='true'>" + content + "</div>";

            self.contentVM.geegrid.updateHtml(self.id, content);

            if(!self.content()){
                self.content(self.widget.content);
            }

            self.editor = $itemCell.find("[data-bind=ckeditor]").ckeditor(ckeditorConfig).editor;
            self.editor.on('instanceReady', function () {
                $('body > .cke_float').appendTo('.editor-content');
            });

            self.editor.on("change", function (data) {
                self.contentVM.updateContent(self.id,  self.editor.getData());
            })

        }

        self._initWidget = function(){
            var self = this;
            if(self.widget.type == "BACKEND") {
                self.generatePreviewHtml(function (html) {
                    if (html) {
                        self.contentVM.geegrid.updateHtml(self.id, html)
                    } else {
                        self.contentVM.geegrid.updateHtml(self.id, self.previewHtml())
                    }
                });
            } else if(self.widget.type == "CKEDITOR_CLIENT") {
                self._initCKEditorWidget();
            } else if(self.widget.type == "HTML_CLIENT") {
                self.contentVM.geegrid.updateHtml(self.id, self.widget.content);
                var $itemCell =  self.contentVM.geegrid.getItem(self.id);
                $itemCell.find(".w-option-outer").hide();
            }
        }

        self.generatePreviewHtml = function (callback) {

            var nodeMap = {}
            nodeMap.css = "";
            nodeMap.type = "WIDGET";
            nodeMap.widget = self.widget.code();
            nodeMap.param_values = {};

            _.each(self.parameterValues(), function (param) {
                nodeMap.param_values[param.parameter.code()] = param.value();

            })

            var json = JSON.stringify(nodeMap);
            //

            var url =   "/content/preview-node/" + self.contentVM.contentId() + "?node=" + encodeURIComponent(json) + '&xt=' + new Date().getTime();

            $.ajax({
                type: "GET",
                url: url,
                // data: params,
                success: function (data) {

                    self.preview($(data).find('#preview-node').html())

                    callback(self.preview());
                },
                error: function (data) {
                    callback(self.previewHtml());
                },
            });
        }

        self.saveParameters = function () {
            var parameterValues = [];

            if(self.tab()){
                saveTabParameters(self.tab());
            }

            function saveTabParameters(tab) {
                _.each(tab.items(), function (item) {
                    if(item.TYPE == "PARAMETER"){
                        parameterValues.push(item);
                    } else if (item.TYPE == "TAB"){
                        saveTabParameters(item);
                    }

                });
            }
            self.parameterValues(parameterValues);
        }

        self.cloneParameters = function () {

            if(self.tab()){
                cloneTabParameters(self.tab());
            }

            function cloneTabParameters(tab) {
                var newItems = [];
                _.each(tab.items(), function (item) {

                    if(item.TYPE == "PARAMETER"){
                        var parameterValue = _.findWhere( self.parameterValues(), { id : item.id } );
                        console.log(parameterValue.value())
                        newItems.push(parameterValue.clone());
                    } else if (item.TYPE == "TAB"){
                        cloneTabParameters(item);
                        newItems.push(item);
                    }

                });
                tab.items(newItems);
            }
        }

        self.setWidget = function (widget) {
            self.widget = widget;

            var parameterValues = [];
            _.each(self.widget.parameters(), function (parameter) {
                var parameterValue = new WidgetParameterValueVM(parameter)
                if(parameter.defaultValue() && parameter.type() == "SLIDER"){
                    parameterValue.value(parseInt(parameter.defaultValue(), 10));
                } else if(parameter.defaultValue()){
                    parameterValue.value(parameter.defaultValue());
                }
                parameterValues.push(parameterValue)
            })

            self.parameterValues(parameterValues);

            console.log(self.widget)
            if(self.widget.tab()){
                console.log(self.widget.tab())
                self.tab(populateTabs(self.widget.tab(), null));

            }

            function populateTabs(tab, parent) {
                var tabVM = new WidgetParameterTabVM(self, parent);
                tabVM.label(tab.label);

                _.each(tab.items, function (item) {
                    console.log(item);
                    if(item.type == "PARAMETER"){
                        var parameterValue = _.findWhere( self.parameterValues(), { id : item.id } );
                        tabVM.items.push(parameterValue.clone());
                        console.log(parameterValue.value())
                    } else if (item.type == "TAB"){
                        tabVM.items.push(populateTabs(item.item, tabVM));
                    }

                })

                return tabVM;
            }
        }
    }

    function WidgetGroupVM(code, label, widgetIds) {
        var self = this;
        self.code = code;
        self.label = label;
        self.widgetIds = widgetIds;
    }

    function WidgetVM(controller) {
        var self = this;

        self.controller = controller;
        self.code = ko.observable();
        self.icon = ko.observable();
        self.group = null;
        self.label = ko.observableArray();
        self.parameters = ko.observableArray([]);
        self.tab = ko.observable();
        self.type = null;
        self.content = "";
        self.config = null;
    }

    function WidgetParameterTabVM(contentNode, parentTab) {
        var self = this;
        self.TYPE = "TAB";
        self.parentTab = parentTab;
        self.contentNode = contentNode;
        self.label = ko.observableArray();
        self.items = ko.observableArray([]);
    }

    function WidgetParameterVM(id) {
        var self = this;

        self.id = id;
        self.label = ko.observableArray();
        self.code = ko.observable();
        self.options = ko.observableArray([]);
        self.option = ko.observable();
        self.type = ko.observable();

        self.defaultValue = ko.observable();
        self.maxValue = ko.observable();
        self.minValue = ko.observable();
        self.step = ko.observable();
    }

    function WidgetParameterValueVM(parameterVM) {
        var self = this;
        self.id = parameterVM.id;
        self.TYPE = "PARAMETER";
        self.value = ko.observable();
        self.parameter = parameterVM;

        self.url = ko.observable('');
        self.previewUrl = ko.observable('');
        self.mimeType = ko.observable('');
        self.fileName = ko.observable('');

        self.isImage = ko.computed(function() {
            return self.mimeType() && self.mimeType().startsWith('image/');
        });

        self.isFileLoaded = ko.computed(function() {
            return self.fileName() && true;
        });


        self.clone = function(){
            var vm = new WidgetParameterValueVM(self.parameter);
            vm.value(self.value());
            vm.url(self.url());
            vm.previewUrl(self.previewUrl());
            vm.mimeType(self.mimeType());
            vm.fileName(self.fileName());

            return vm;
        }
    }


    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function ContentDesignController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ContentDesignController)) {
            throw new TypeError("ContentDesignController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.toolboxGroups = ko.observable();
        this.contentVM = {};
        this.widgets = {};
        this.geegrid = null;

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate','attached','detached', 'getJson');
    }

    ContentDesignController.prototype = {
        constructor : ContentDesignController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.content.detailsTitle';
            return title;
        },
        setMobileMode: function () {
            $("#editor-iframe").css("width", "540px");
        },
        setTabletMode: function () {
            $("#editor-iframe").css("width", "768px");
        },
        setDesktopMode: function () {
            $("#editor-iframe").css("width", "100%");
        },
        resizeCmsPanel: function () {
            if($("#cmsPanel").hasClass("cms-full-mode")){
                $("#cmsPanel").removeClass("cms-full-mode");
                $("body").removeClass("cms-full-mode");
                $(".cms-full-mode-switch").addClass("fa-expand");
                $(".cms-full-mode-switch").removeClass("fa-compress");
               // jQuery('#cmsPanel').appendTo("#design");
            } else {
                $("body").addClass("cms-full-mode");
                $("#cmsPanel").addClass("cms-full-mode");
                $(".cms-full-mode-switch").removeClass("fa-expand");
                $(".cms-full-mode-switch").addClass("fa-compress");
               // jQuery('#cmsPanel').appendTo("body");
            }

        },
        pageDescription : 'app:modules.content.detailsSubtitle',
        gc : gc,
        getJson : function () {
            this.geegrid.toJson();
        },
        saveData : function(context) {
            var self = this;

            console.log(self)
            
            var contentUpdateModel = gc.app.newUpdateModel();

            var contentNodes = [];

            _.each(self.contentVM.nodes, function (node) {
                var contentNode = {}
                contentNode.node_id = node.id;
                contentNode.key = node.key;
                if(node.widget.type == "BACKEND"){
                    contentNode.widget = node.widget.code();
                    contentNode.type = "WIDGET";

                    contentNode.param_values = {};
                    contentNode.preview = node.preview();

                    _.each(node.parameterValues(), function (paramValue) {
                        contentNode.param_values[paramValue.parameter.code()] = paramValue.value();
                    });

                } else {
                    contentNode.type = "TEXT";
                    contentNode.content = node.content();
                }

                contentNodes.push(contentNode)
            })


            contentUpdateModel.field('content_nodes', JSON.stringify(contentNodes));

            contentUpdateModel.field('structure_nodes', JSON.stringify(self.contentVM.geegrid.toJson()));

                contentAPI.updateContent(self.contentVM.contentId(), contentUpdateModel).then(function(data){

                    context.saved();
                });

        },
        activate : function(data) {
            var self = this;
            self.allToolboxItems = []
            self.contentVM = new ContentVM();
            self.contentVM.contentId(data);

            console.log("ACTIVATEEEEEEEEEEEEEEEEEEEEEEED");

            self.contentVM.ckeditorConfigs = {}
            var hCkeditorOptions = {
                // Define the toolbar groups as it is a more accessible solution.
                toolbarGroups: [
                    {name: 'clipboard', groups: ['clipboard', 'undo']},
                    {name: 'links'},
                    { name: 'align' },
                    '/',
                    {name: 'basicstyles', groups: ['basicstyles', 'cleanup', 'alignment']},
                    {name: 'styles'},
                    {name: 'colors'}
                ],

                // Remove the redundant buttons from toolbar groups defined above.
                extraPlugins: 'justify',
                removeDialogTabs: 'link:upload;image:Upload'
            }

            var ckeditorOptions = {
                // Define the toolbar groups as it is a more accessible solution.
                toolbarGroups: [
                    { name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
                    { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
                    { name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
                    { name: 'insert', groups: [ 'insert' ] },
              /* { name: 'forms', groups: [ 'forms' ] }, */
                    { name: 'links', groups: [ 'links' ] },
                    '/',
                    { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
                    { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },

                    '/',
                    { name: 'styles', groups: [ 'styles' ] },
                    { name: 'colors', groups: [ 'colors' ] },
                    { name: 'tools', groups: [ 'tools' ] },
                    { name: 'others', groups: [ 'others' ] },
                    { name: 'about', groups: [ 'about' ] }
                ],

                removeButtons: 'Form,HiddenField,Smiley,Radio,TextField,Checkbox,Textarea,Select,Button,ImageButton,Flash,About,ShowBlocks,Maximize,Source,Templates,Save,NewPage,Preview,Print',


                // Remove the redundant buttons from toolbar groups defined above.
                removeDialogTabs: 'link:upload;image:Upload'
            }

            self.contentVM.ckeditorConfigs["headers"] = hCkeditorOptions;
            self.contentVM.ckeditorConfigs["text"] = ckeditorOptions;
            

            self.widgetGroups = [];
          // self.toolboxGroups();

            return widgetAPI.getWidgetGroups().then(function (data) {


                _.each(data.data.widgetGroups, function (widgetGroup) {
                    var widgetGroupVM = new WidgetGroupVM(widgetGroup.code, widgetGroup.label, widgetGroup.widgetIds);
                    self.widgetGroups.push(widgetGroupVM);

                })


                return widgetAPI.getWidgets().then(function (data) {
                    // For now set all widgets to Widget group

                    _.each(data.data.widgets, function(widget){

                        var widgetVM = new WidgetVM(self);
                        widgetVM.code(widget.code);
                        widgetVM.label(widget.label);
                        widgetVM.group = widget.group;
                        widgetVM.icon(widget.icon);

                        widgetVM.type = widget.type;
                        widgetVM.content = widget.content;
                        widgetVM.config = widget.configuration;

                        if(widget.tabs && widget.tabs.length > 0){
                            widgetVM.tab(widget.tabs[0]);
                        }

                        _.each(widget.parameters, function(parameter){

                            var parameterVM = new WidgetParameterVM(parameter.id);
                            parameterVM.code(parameter.code);
                            parameterVM.option(parameter.option);
                            parameterVM.type(parameter.type);
                            parameterVM.label(parameter.label);
                            parameterVM.defaultValue(parameter.defaultValue);
                            parameterVM.minValue(parameter.minValue);
                            parameterVM.maxValue(parameter.maxValue);
                            parameterVM.step(parameter.step);


                            parameterVM.options.push({id:"", text: "Select"})
                            widgetAPI.getParameterOptions(widget.code, parameter.id).then(function(data){
                                if(data.data.widgetParameterOptions){
                                    _.each(data.data.widgetParameterOptions, function(option){
                                        gc.ctxobj.enhance(option, [ 'label' ], 'any');
                                        parameterVM.options.push({id:option.value, text:  option.label ? option.label.i18n : option.value})
                                    })
                                }
                            });

                            // console.log( widgetVM.parameters());
                            widgetVM.parameters.push(parameterVM);
                        });
                        self.widgets[widget.id] = widgetVM;
                        self.contentVM.widgets[widget.code] = widgetVM;
                    })


                    var groups = []
                    _.each(self.widgetGroups, function(widgetGroup){

                        var items = [];
                        _.each(widgetGroup.widgetIds, function (id) {
                            var widget = self.widgets[id];
                            var item = new ToolboxItem(widget.code(), widget.label(), "<p>Click to edit widget</p>", "html", widget.icon());
                            items.push(item);
                            self.allToolboxItems.push(item);
                            delete self.widgets[id];
                        });
                        _.each(self.widgets, function (widget) {
                            if(widget.group == widgetGroup.code){
                                var item = new ToolboxItem(widget.code(), widget.label(), "<p>Click to edit widget</p>", "html", widget.icon());
                                items.push(item);
                                self.allToolboxItems.push(item);
                            }
                        });
                        var toolboxGroup = new ToolboxGroup(widgetGroup.code, widgetGroup.label, items);
                        groups.push(toolboxGroup);
                    });
                    self.toolboxGroups(groups);

                    return contentAPI.getContent( self.contentVM.contentId()).then(function(data) {
                        self.contentVM.type(data.type);
                        self.contentVM.structure = data.structureNodes;
                        _.each(data.contentNodes, function (node) {
                            var contentNode = self.contentVM.addNode(node.nodeId, node.key);
                            contentNode.content(node.content)

                            if(node.parameterValues){
                                _.each(contentNode.parameterValues(), function (parameterValue) {
                                    // console.log(parameterValue)
                                    parameterValue.value(node.parameterValues[parameterValue.parameter.code()])
                                })
                            }
                            // restore params
                        })

                        // console.log(data);
                    });
                })
            });



        },

        attached : function() {
            var self = this;

            console.log('________________ @@@WIDEGTS :::::::::::::::::::: ', self.widgets);
            
            
            
            if(!window.autoResizeIframe){
                window.autoResizeIframe = function(){
                    $('#editor-iframe').height($('#editor-iframe').contents().height());
                }
            }

            self.iframe = $('<iframe id="editor-iframe" name="editor-iframe" onLoad="autoResizeIframe();" src="/content/storefront" sandbox="allow-same-origin allow-scripts" style="width:100%;height:900px;" scrolling="no" seamless="seamless" ></iframe> ');
            self.iframe.appendTo('.editor-content');

            self.iframe.load(function () {
                var options = {
                    containerSelector : ".preview-container",
                    mode: "iframe",
                    widgets: self.allToolboxItems
                };
                // setTimeout(function () {
                self.geegrid = $('#editor-iframe').geegrid(options).data('geegrid');;

                self.contentVM.geegrid = self.geegrid;

                self.geegrid.on("widget-created", function (data) {
                    self.contentVM.addNode(data.id, data.key);
                })

                self.geegrid.on("option-btn", function (data) {
                    self.contentVM.showOptions(data.id);
                })

                self.geegrid.on("delete-btn", function (data) {
                    self.contentVM.removeNode(data.id);
                })


                self.geegrid.fromJson(self.contentVM.structure)

                _.each(self.contentVM.nodes, function (node) {
                    node._initWidget();
                })

                gc.app.setNavToolbar({title: 'CMS', items: [{label: 'Mobile', icon: 'fa fa-mobile-phone', click: self.setMobileMode},
                  {label: 'Tablet', icon: 'fa fa-tablet', click: self.setTabletMode},
                  {label: 'Desktop', icon: 'fa fa-desktop', click: self.setDesktopMode},
                  {label: 'Preview', icon: 'fa fa-external-link ', href: self.contentVM.previewURL}]});

                gc.app.onSaveEvent(function(context) {
                    var id = $('#view-content-details>.tab-content>.active').attr('id');
                    if(id == 'design') {
                        self.saveData(context);
                    }
                });

                $("#content-spinner").hide();

            });


            $('.parameters-modal').draggable({
                handle: ".parameters-modal-header"
            });


         // }, 2000)


            // TODO: subscribe
            // createdWidget
            // show parameters call
            // geegrid - updateHtml

            // $('#editor-menu').addClass('hidden').addClass('absolute');
            // $('.editor-content').addClass('col-md-12').removeClass(
            // 'col-md-10');
            //
            // $('#search-elements').addClass('hidden');

           // self.pinMenu();

/*
 * $('.form-control').css({ "width" : "auto" }); $( '.main-content, .menu-component, .row, .col-md-12, .col-md-10, .col-md-2') .css({ "padding" : "0px", "margin" : "0px" });
 */


            /*
             * $().ready(function() { var $scrollingDiv = $("#editor-menu");
             * 
             * 
             * 
             * $(window).scroll(function(){ var scrollTop = $(window).scrollTop(); var elementOffset = $scrollingDiv.offset().top; var originalTop = (elementOffset - scrollTop);
             * console.log(originalTop)
             * 
             * console.log($(window).scrollTop())
             * 
             * if(originalTop < $(window).scrollTop()){ $scrollingDiv .stop() .animate({"marginTop": ($(window).scrollTop() - originalTop)}, "slow" ); } }); });
             */
        },
        detached : function () {
            gc.app.unsetNavToolbar();
            this.geegrid.destroy();
        }
    }

    return ContentDesignController;
});