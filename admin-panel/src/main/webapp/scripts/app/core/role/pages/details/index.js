define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-role', 'gc-permission'  ], function(app, ko, gc, roleAPI, permissionAPI) {

    function RoleVM(roleId) {
        var self = this;
        self.id = ko.observable(roleId);
        self.name = ko.observableArray();
        self.code = ko.observable();
        self.permissions = ko.observableArray();
    }

    function RoleIndexController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof RoleIndexController)) {
            throw new TypeError("RoleIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.roleId = undefined;
        this.roleVM = {};

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate', 'attached');
    }

    RoleIndexController.prototype = {
        constructor : RoleIndexController,
        pageTitle : function() {
            var self = this;
            var title = gc.app.i18n('app:modules.role.title');
            // var userId = ko.unwrap(self.userId);
            //
            // if(!_.isUndefined(userId)) {
            //     title += ': ' + userId;
            // }

            return title;
        },
        saveData : function() {
            var self = this;

            var updateModel = gc.app.newUpdateModel();
        },
        activate : function(roleId) {
            var self = this;

            self.roleId = roleId;

            if (roleId == 'new') {
                gc.app.pageTitle(gc.app.i18n('app:modules.role.newRoleTitle'));

                self.roleVM = new RoleVM(roleId);
                gc.app.sessionPut('roleVM', self.roleVM);
            } else {

                gc.app.pageTitle(self.pageTitle());

                return roleAPI.getRole(roleId).then(function(role) {
                    self.roleVM = new RoleVM(roleId);
                    self.roleVM.code(role.code);
                    self.roleVM.name(role.name);

                    gc.app.sessionPut('roleVM', self.roleVM);
                    gc.app.pageTitle(self.pageTitle());

                    return permissionAPI.getPermissions().then(function (data) {
                        var permissions =  data.data.permissions;

                        _.each(role.permissionIds, function (id) {
                            var permission = _.findWhere( permissions, { id : id } );
                            self.roleVM.permissions.push({id: permission.id, name: permission.name, code: permission.code});
                        });
                    });

                });
            }
        },
        attached : function() {
            var self = this;
        }
    };

    return RoleIndexController;
});
