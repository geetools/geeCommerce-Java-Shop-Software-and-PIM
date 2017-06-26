define([ 'durandal/app', 'knockout', 'gc/gc', 'plugins/router', 'gc-user', 'gc-role'  ], function(app, ko, gc, router, userAPI, roleAPI) {

    function UserVM(userId) {
        var self = this;
        self.id = ko.observable(userId);
        self.forename = ko.observable();
        self.surname = ko.observable();
        self.email = ko.observableArray();
        self.username = ko.observableArray();
        self.createdAt = ko.observableArray();
        self.roles = ko.observableArray([]);
        self.queryNode = ko.observable();

        self.password = ko.observable();
        self.password2 = ko.observable();

        self.isProductManager = ko.computed(function () {
            if(self.roles()) {
                _.each(self.roles(), function (role) {
                    console.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    console.log(role.code)
                    if (role.code == 'product-manager') {
                        console.log("++++++++++++++++++++++++++++++++++")
                        return true;
                    }
                })
            }
            return false;
        })
    }

    function UserIndexController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof UserIndexController)) {
            throw new TypeError("UserIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.userId = undefined;
        this.userVM = {};

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate', 'attached', 'notifyChange');
    }

    UserIndexController.prototype = {
        constructor : UserIndexController,
        pageTitle : function() {
            var self = this;
            var title = gc.app.i18n('app:modules.user.title');
            // var userId = ko.unwrap(self.userId);
            //
            // if(!_.isUndefined(userId)) {
            //     title += ': ' + userId;
            // }

            return title;
        },
        notifyChange : function() {
            $toolbar = $("#userAllForm").closest('form').find('.toolbar-trigger').first();
            // Make sure that the save/cancel toolbar sees the change.
            $toolbar.click();
            $toolbar.trigger('change');
        },
        saveData : function(view, parent, toolbar) {
            var self = this;

            var newPassword = null;

            if(self.userVM.password() && self.userVM.password2() && self.userVM.password() == self.userVM.password2()){
                newPassword = self.userVM.password();
            }

            if(self.userVM.id() == 'new'){
                var updateModel = gc.app.newUpdateModel();
                updateModel.field('forename', self.userVM.forename());
                updateModel.field('surname', self.userVM.surname());
                updateModel.field('email', self.userVM.email());
                updateModel.field('username', self.userVM.username());
                if(newPassword)
                    updateModel.field('newPassword', newPassword);

                userAPI.createUser(updateModel).then(function (data) {
                    toolbar.hide();
                    router.navigate('//users/details/' + data.id);
                });
            } else {
                var updateModel = gc.app.newUpdateModel();
                updateModel.field('forename', self.userVM.forename());
                updateModel.field('surname', self.userVM.surname());
                updateModel.field('email', self.userVM.email());
                updateModel.field('username', self.userVM.username());

                updateModel.field('queryNode', self.userVM.queryNode())
                if(newPassword)
                    updateModel.field('newPassword', newPassword);

                userAPI.updateUser(self.userVM.id(), updateModel).then(function () {
                    toolbar.hide();
                    self.userVM.password('')
                    self.userVM.password2('')
                });
            }

        },
        activate : function(userId) {
            var self = this;

            self.userId = userId;

            if (userId == 'new') {
                gc.app.pageTitle(gc.app.i18n('app:modules.user.newUserTitle'));

                self.userVM = new UserVM(userId);
                gc.app.sessionPut('userVM', self.userVM);
            } else {

                gc.app.pageTitle(self.pageTitle());

                return userAPI.getUser(userId).then(function(user) {
                    self.userVM = new UserVM(userId);
                    self.userVM.forename(user.forename);
                    self.userVM.surname(user.surname);
                    self.userVM.email(user.email);
                    self.userVM.username(user.username);

                    self.userVM.queryNode(user.queryNode);
                    self.userVM.queryNode.subscribe(function(value) {
                        self.notifyChange()
                    })


                    gc.app.sessionPut('userVM', self.userVM);
                    gc.app.pageTitle(self.pageTitle());

                    return roleAPI.getRoles().then(function (data) {
                        var roles =  data.data.roles;

                        _.each(user.roleIds, function (id) {
                            var role = _.findWhere( roles, { id : id } );
                            self.userVM.roles.push({id: role.id, name: role.name, code: role.code});
                        });
                    });

                });
            }
        },
        attached : function() {
            var self = this;
        }
    };

    return UserIndexController;
});
