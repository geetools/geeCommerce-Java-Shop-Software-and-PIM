define(
		[ 'durandal/app', 'knockout', 'gc/gc', 'gc-conf', 'gc-conf/util' ],
		function(app, ko, gc, confAPI, confUtil) {

			// -----------------------------------------------------------------
			// Controller
			// -----------------------------------------------------------------
			function ConfigurationIndexController(options) {

				// Make sure that this object is being called with the 'new'
				// keyword.
				if (!(this instanceof ConfigurationIndexController)) {
					throw new TypeError(
							"ConfigurationIndexController constructor cannot be called as a function.");
				}

				this.app = gc.app;
				this.confTree = ko.observable();
				this.activeView = ko.observable();

				// Solves the 'this' problem when a DOM event-handler is fired.
				_.bindAll(this, 'activate', 'activateView',
						'compositionComplete', 'loadConfiguration','saveConf');
			}

			ConfigurationIndexController.prototype = {
				constructor : ConfigurationIndexController,
				activateView : function(child) {
					var self = this;
					console.log("A1");
					self.activeView({
						model : 'core/configuration/pages/details/index',
						transition : 'entrance',
						activationData : child
					});
					console.log("A2");
				},
				saveConf : function(view, parent, toolbar)  {
					
				},
				activate : function() {
					var self = this;

					var childrenLength = 3;
					var currentElement;

					// var confItems = {
					// "children" : []
					// };
					// console.log("A", confItems);
					// for (var i = 0; i < childrenLength; i++) {
					// confItems.children.push({
					// "idhash" : "#col-" + (i + 1),
					// "id" : "col-" + (i + 1),
					// "displayLabel" : "Item " + (i + 1),
					// "children" : []
					// });
					// for (var j = 0; j < childrenLength; j++) {
					// confItems.children[i].children.push({
					// "idhash" : "#col-" + (i + 1) + "-" + (j + 1),
					// "id" : "col-" + (i + 1) + "-" + (j + 1),
					// "displayLabel" : "Item " + (i + 1) + "."
					// + (j + 1),
					// "children" : []
					// });
					// for (var k = 0; k < childrenLength; k++) {
					// confItems.children[i].children[j]
					// .children
					// .push(
					// {
					// "idhash" : "#col-"
					// + (i + 1) + "-"
					// + (j + 1) + "-"
					// + (k + 1),
					// "id" : "col-" + (i + 1)
					// + "-" + (j + 1)
					// + "-" + (k + 1),
					// "displayLabel" : "Item "
					// + (i + 1) + "."
					// + (j + 1) + "."
					// + (k + 1),
					// "children" : [
					// {
					// "displayLabel" : "Text label",
					// "type" : "text"
					// },
					// {
					// "displayLabel" : "Boolean label",
					// "type" : "boolean"
					// } ]
					// });
					// }
					// }
					// }

					var confItems = {
						"children" : [
								{
									"idhash" : "#col-1",
									"id" : "col-1",
									"displayLabel" : "Catalog",
									"children" : [
											{
												"idhash" : "#col-1-1",
												"id" : "col-1-1",
												"displayLabel" : "Catalog",
												"children" : [
														{
															"idhash" : "#col-1-1-1",
															"id" : "col-1-1-1",
															"displayLabel" : "Frontend",
															"children" : [
																	{
																		"displayLabel" : "List Mode",
																		"type" : "select",
																		"placeholder" : "Grid (default) / List",
																		"scope" : "STORE VIEW",
																		"options" : [
																				"Grid Only",
																				"List Only",
																				"Grid (default) / List",
																				"List (default) / Grid" ]
																	},
																	{
																		"displayLabel" : "Products per Page on Grid",
																		"displayLabel2" : "Allowed Values",
																		"type" : "text",
																		"placeholder" : "9, 15, 30",
																		"comment" : "Comma-separated.",
																		"scope" : "STORE VIEW"
																	},
																	{
																		"displayLabel" : "Products per Page on Grid",
																		"displayLabel2" : "Default Value",
																		"type" : "text",
																		"placeholder" : "9",
																		"comment" : "Must be in the allowed values list.",
																		"scope" : "STORE VIEW"
																	},
																	{
																		"displayLabel" : "Products per Page on List",
																		"displayLabel2" : "Allowed Values",
																		"type" : "text",
																		"placeholder" : "5,10,15,20,25",
																		"comment" : "Comma-separated.",
																		"scope" : "STORE VIEW"
																	},
																	{
																		"displayLabel" : "Products per Page on List",
																		"displayLabel2" : "Default Value",
																		"type" : "text",
																		"placeholder" : "10",
																		"comment" : "Must be in the allowed values list.",
																		"scope" : "STORE VIEW"
																	},
																	{
																		"displayLabel" : "Allow All Products per Page",
																		"type" : "select",
																		"placeholder" : "No",
																		"comment" : "Whether to show 'All' option in the 'Show X Per Page' dropdown.",
																		"scope" : "STORE VIEW",
																		"options" : [
																				"Yes",
																				"No" ]
																	},
																	{
																		"displayLabel" : "Product Listing Sort by",
																		"type" : "select",
																		"placeholder" : "Best Value",
																		"scope" : "STORE VIEW",
																		"options" : [
																				"Best Value",
																				"Name",
																				"Price" ]
																	},
																	{
																		"displayLabel" : "Use Flat Catalog Category",
																		"type" : "select",
																		"placeholder" : "No",
																		"scope" : "GLOBAL",
																		"options" : [
																				"Yes",
																				"No" ]
																	},
																	{
																		"displayLabel" : "Use Flat Catalog Product",
																		"type" : "select",
																		"placeholder" : "No",
																		"scope" : "GLOBAL",
																		"options" : [
																				"Yes",
																				"No" ]
																	},
																	{
																		"displayLabel" : "Allow Dynamic Media URLs in",
																		"displayLabel" : "Products and Categories",
																		"type" : "select",
																		"placeholder" : "Yes",
																		"scope" : "STORE VIEW",
																		"comment" : "E.g. {{media url='path/to/image.jpg'}} {{skin url='path/to/picture.gif'}}. Dynamic directives parsing impacts catalog performance.",
																		"options" : [
																						"Yes",
																						"No" ]
																	} ]
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Sitemap",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Product Reviews",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Product Alerts",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Product Alerts Run Settings",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Product Image Placeholders",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Recently Viewed/Compared Products",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Price",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Layered Navigation",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Category Top Navigation",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Search Engine Optimizations",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Catalog Search",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Downloadable Product Options",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Date & Time Custom Options",
															"children" : []
														} ]
											},
											{
												"idhash" : "#col-2",
												"id" : "col-2",
												"displayLabel" : "Inventory",
												"children" : [
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Stock Options",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Product Stock Options",
															"children" : []
														} ]
											} ]
								},
								{
									"idhash" : "#col-2",
									"id" : "col-2",
									"displayLabel" : "Customers",
									"children" : [ {
										"idhash" : "#col-1",
										"id" : "col-1",
										"displayLabel" : "Customer Configuration",
										"children" : [
												{
													"idhash" : "#col-1",
													"id" : "col-1",
													"displayLabel" : "Account Sharing Options",
													"children" : []
												},
												{
													"idhash" : "#col-2",
													"id" : "col-2",
													"displayLabel" : "Online Customers Options",
													"children" : []
												},
												{
													"idhash" : "#col-3",
													"id" : "col-3",
													"displayLabel" : "Create New Account Options",
													"children" : []
												},
												{
													"idhash" : "#col-1",
													"id" : "col-1",
													"displayLabel" : "Password Options",
													"children" : []
												},
												{
													"idhash" : "#col-2",
													"id" : "col-2",
													"displayLabel" : "Name and Address Options",
													"children" : []
												},
												{
													"idhash" : "#col-3",
													"id" : "col-3",
													"displayLabel" : "Login Options",
													"children" : []
												},
												{
													"idhash" : "#col-1",
													"id" : "col-1",
													"displayLabel" : "Address Templates",
													"children" : []
												} ]
									} ]
								},
								{
									"idhash" : "#col-3",
									"id" : "col-3",
									"displayLabel" : "Sales",
									"children" : [
											{
												"idhash" : "#col-1",
												"id" : "col-1",
												"displayLabel" : "Sales",
												"children" : [
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Checkout Totals Sort Order",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Reorder",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Invoice and Packing Slip Design",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Minimum Order Amount",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Dashboard",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Gift Options",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Minimum Advertised Price",
															"children" : []
														} ]
											},
											{
												"idhash" : "#col-2",
												"id" : "col-2",
												"displayLabel" : "Sales Emails",
												"children" : [
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Order",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Order Comments",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Invoice",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Invoice Comments",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Shipment",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Shipment Comments",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Credit Memo",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Credit Memo Moments",
															"children" : []
														} ]
											},
											{
												"idhash" : "#col-3",
												"id" : "col-3",
												"displayLabel" : "PDF Print-outs",
												"children" : [
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Pdf Customiser by Fooman",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Pdf Totals Sort Order",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Order",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Invoice",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Shipment",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Credit Memo",
															"children" : []
														} ]
											},
											{
												"idhash" : "#col-1",
												"id" : "col-1",
												"displayLabel" : "Tax",
												"children" : [
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Tax Classes",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Calculation Settings",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Default Tax Destination Calculation",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Price Display Settings",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Shopping Cart Display Settings",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Orders, Invoices, Creditmemos Display Settings",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Fixed Product Taxes",
															"children" : []
														} ]
											},
											{
												"idhash" : "#col-2",
												"id" : "col-2",
												"displayLabel" : "Checkout",
												"children" : [
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Checkout Options",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Shopping Cart",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "My Cart Link",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Shopping Cart Sidebar",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Payment Failed Emails",
															"children" : []
														} ]
											},
											{
												"idhash" : "#col-3",
												"id" : "col-3",
												"displayLabel" : "Shipping Settings",
												"children" : [ {
													"idhash" : "#col-1",
													"id" : "col-1",
													"displayLabel" : "Origin",
													"children" : []
												}, {
													"idhash" : "#col-2",
													"id" : "col-2",
													"displayLabel" : "Options",
													"children" : []
												} ]
											},
											{
												"idhash" : "#col-1",
												"id" : "col-1",
												"displayLabel" : "Shipping Methods",
												"children" : [
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Flat Rate",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Table Rates",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Free Shipping",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "UPS",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "USPS",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "FedEx",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "DHL",
															"children" : []
														} ]
											},
											{
												"idhash" : "#col-2",
												"id" : "col-2",
												"displayLabel" : "PayPal",
												"children" : [
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Merchant Account",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "API/Integration Settings",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Express Checkout Settings",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Website Payments Standard Settings",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Website Payments Pro Settings",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Paypal Billing Agreement Settings",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Website Payments Pro (Payflow Edition) Settings",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Payflow Pro Settings",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Express Checkout (Payflow Edition) Settings",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Settlement Report Settings",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Frontend Experience Settings",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Payflow Link Settings",
															"children" : []
														} ]
											},
											{
												"idhash" : "#col-3",
												"id" : "col-3",
												"displayLabel" : "Payment Methods",
												"children" : [
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Saved CC",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Check / Money Order",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Zero Subtotal Checkout",
															"children" : []
														},
														{
															"idhash" : "#col-1",
															"id" : "col-1",
															"displayLabel" : "Purchase Order",
															"children" : []
														},
														{
															"idhash" : "#col-2",
															"id" : "col-2",
															"displayLabel" : "Authorize.net Direct Post",
															"children" : []
														},
														{
															"idhash" : "#col-3",
															"id" : "col-3",
															"displayLabel" : "Authorize.net",
															"children" : []
														} ]
											} ]
								} ]
					};

					var tree;

					tree = confItems;// confUtil.jsonToTree(confItems, self);
					self.confTree(tree);

					gc.app.pageTitle('Configuration');
					gc.app.pageDescription('Adjust system preferences');

					return;
				},
				compositionComplete : function() {
					var self = this;

					$("#col-1").addClass("in");
					$("#col-1").removeClass("collapsed");
					
					console.log("confTree:",self.confTree());
					console.log("confTree.children.children.children:",self.confTree().children[0].children[0]);
					
					self.activeView({
						model : 'core/configuration/pages/details/index',
						transition : 'entrance',
						activationData : self.confTree().children[0].children[0]
					});
					setTimeout(function() {
						$("#col-1-1-1").addClass("in");
						$("#col-1-1-1").removeClass("collapsed");
					}, 1000);
				},
				loadConfiguration : function(child) {
					var self = this;
					console.log("A1");

					self.activeView({
						model : 'core/configuration/pages/details/index',
						transition : 'entrance',
						activationData : child
					});
					console.log("A2");
				}
			}
			return ConfigurationIndexController;
		});
