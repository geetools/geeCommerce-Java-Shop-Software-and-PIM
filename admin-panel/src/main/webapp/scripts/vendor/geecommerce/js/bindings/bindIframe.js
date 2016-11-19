define(
		[ 'knockout' ],
		function(ko) {

			return {
				init : function(element, valueAccessor) {
					function bindIframe() {
						try {
							var iframeInit = element.contentWindow.initChildFrame, iframedoc = element.contentDocument.body;
						} catch (e) {
							// ignored
						}
						if (iframeInit)
							iframeInit(ko, valueAccessor());
						else if (iframedoc) {
							var span = document.createElement('span');
							span
									.setAttribute('data-bind',
											'text: test');
							iframedoc.appendChild(span);
							ko.applyBindings(valueAccessor(), iframedoc);
						}
					}
					;
					bindIframe();
					ko.utils.registerEventHandler(element, 'load', bindIframe);
				}
			}
		});
