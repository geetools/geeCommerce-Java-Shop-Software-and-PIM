{{#variants}}

	<!-- Variant container -->
	<div class="variant-outer variant-attr-{{{attribute_code}}}" data-attr="{{{attribute_code}}}">
		<div class="variant-inner">

			<input id="selected-prd-variant" type="hidden" name="selectedVariant" value="" />

			<!-- Input fields for selected options -->
			<input id="varAttrVal_{{{attribute_code}}}" class="selected-option" type="hidden" name="variantOptionIds[]" value="" />
			<input id="varAttrCode_{{{attribute_code}}}" class="selected-attribute" type="hidden" name="variantAttributeCodes[]" value="{{{attribute_code}}}" />

			<!-- Attribute label -->
			<div class="wd-variant-attribute">
				{{{attribute_label}}}:&nbsp;
				<span class="wd-variant-selected-value"></span>
			</div>
			<div class="wd-variant-options">
				<!-- Selectable options -->
				<ul>
					{{#options}}
						<li><a href="javascript: void(0)" id="wd_option_{{{attribute_code}}}_{{{id}}}" data-attr="{{{attribute_code}}}" data-option="{{{id}}}" data-label="{{{value}}}">{{{value}}}</a></li>
					{{/options}}
				</ul>
			</div>
		</div>

{{/variants}}