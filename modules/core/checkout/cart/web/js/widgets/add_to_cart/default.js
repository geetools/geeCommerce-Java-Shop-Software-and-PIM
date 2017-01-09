define(['jquery', 'bootstrap', 'gc/gc'], function ($, Bootstrap, gc) {
    return {
        init: function (widgetParams) {

            var formSelector = "[name=addToCartForm_" + widgetParams.productId + "]";
            $(formSelector).submit(function(e) {
                var self = this;
                e.preventDefault();

                //gather parameters

                if($(".bundle-config").length){
                    var products = [];
                    var qtys = [];

                    _.each($(".bundle-group"), function (bundleGroup) {

                        if($(bundleGroup).attr("group-type") == "LIST"){

                            $(bundleGroup).find('input[name=selectedVariant], input[name=bundleProduct]').each(function () {
                                var $productItem = $(this);

                                if($productItem.val()){
                                    products.push($productItem.val());
                                    qtys.push(1);//qtys.push($productItem.attr("qty"));
                                }
                            })
                        }


                        if($(bundleGroup).attr("group-type") == "RADIOBUTTON"){
                            var $productItem = $(bundleGroup).find("input:checked");

                            if($productItem.val()){
                                products.push($productItem.val());
                                qtys.push($productItem.attr("qty"));
                            }
                        }

                        if($(bundleGroup).attr("group-type") == "CHECKBOX"){
                            var $productItems = $(bundleGroup).find("input:checked");

                            _.each($productItems, function (productItem) {
                                var $productItem = $(productItem);
                                if($productItem.val()){
                                    products.push($productItem.val());
                                    qtys.push($productItem.attr("qty"));
                                }
                            })
                        }

                        if($(bundleGroup).attr("group-type") == "SELECT"){
                            $(bundleGroup).find('option:selected').each(function(){
                                var $productItem = $(this);
                                if($productItem.val()){
                                    products.push($productItem.val());
                                    qtys.push($productItem.attr("qty"));
                                }
                            });

                        }

                        if($(bundleGroup).attr("group-type") == "MULTISELECT"){

                            $(bundleGroup).find('option:selected').each(function(){
                                var $productItem = $(this);
                                if($productItem.val()){
                                    products.push($productItem.val());
                                    qtys.push($productItem.attr("qty"));
                                }
                            });

                        }

                    })

                    var productsValue = products.join();
                    var quantitiesValue = qtys.join();

                    $('input[name=productIds]').remove();
                    $('input[name=quantities]').remove();


                    $('<input>').attr({
                        type: 'hidden',
                        id: 'bundleProductIds',
                        name: 'productIds',
                        value: productsValue
                    }).appendTo(formSelector);

                    $('<input>').attr({
                        type: 'hidden',
                        id: 'bundleQuantities',
                        name: 'quantities',
                        value: quantitiesValue
                    }).appendTo(formSelector);



                    $('input[name=productId]').val( widgetParams.productId);

                }

                self.submit();

                return true;
            });
        }
    }

});