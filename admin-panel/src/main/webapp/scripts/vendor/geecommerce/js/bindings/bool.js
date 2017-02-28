define([ 'knockout', 'i18next', 'gc/gc' ], function(ko, i18n, gc) {

    return {
        init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var $element = $(element), value = valueAccessor(), allBindings = allBindingsAccessor(), switchOptions = allBindings.switchOptions || {};

            var lang = gc.app.currentLang();
            var yesText = i18n.t('app:common.yes', {
                lng : lang
            });
            var noText = i18n.t('app:common.no', {
                lng : lang
            });

            // Default settings can be overriden in binding by passing "switchOptions: {}" parameter.
            var defaultOptions = {
                width : 30,
                height : 17,
                button_width : 17,
                on_label : yesText,
                off_label : noText,
                show_labels: false
            };

            var options = _.extend({}, defaultOptions, switchOptions);
            var onchangeCallback = options.change;

            var unwrappedValue = ko.unwrap(value);

            if (!_.isUndefined(unwrappedValue) && unwrappedValue) {
                options.checked = true;
            } else {
                options.checked = false;
            }

            // Change observable value if checkbox has been clicked.
            if (ko.isObservable(value)) {
                $element.change(function(e) {
                    if (this.checked) {
                        value(true);
                    } else {
                        value(false);
                    }

                    var changeCount = $element.data('_ccount') || 0;
                    if (!_.isUndefined(onchangeCallback) && _.isFunction(onchangeCallback)) {
                        if (changeCount > 0)
                            onchangeCallback(this.checked, $element);

                        $element.data('_ccount', changeCount + 1);
                    }
                });
            }

            $element.switchButton(options);

            if(options.show_labels === true) {
                // In case the use makes a language change, we set the text again here in a computed function.
                ko.computed(function() {
                    var lang = gc.app.currentLang();

                    var noText = i18n.t('app:common.no', {
                        lng : lang
                    });
                    var yesText = i18n.t('app:common.yes', {
                        lng : lang
                    });

                    var labels = $element.parent('.switch-wrapper').children('.switch-button-label');

                    $(labels[0]).text(noText);
                    $(labels[1]).text(yesText);
                });
            }
        },
        update : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
            var value = valueAccessor();
            var valueUnwrapped = ko.unwrap(value);
            var isChecked = $(element).prop('checked');

            if (!_.isUndefined(valueUnwrapped) && !_.isNull(valueUnwrapped) && valueUnwrapped) {
                if (isChecked !== true) {
                    $(element).prop('checked', true);
                    $(element).attr('checked', 'checked');
                    $(element).change();
//                    $(element).switchButton('setChecked', true);
                }
            } else {
                if (isChecked !== false) {
                    $(element).prop('checked', false);
                    $(element).removeAttr('checked');
                    $(element).change();
//                    $(element).switchButton('setChecked', false);
                }
            }
        }
    };
});
