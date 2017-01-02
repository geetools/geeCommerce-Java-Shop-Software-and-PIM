<div class="bundle-config">

	<#list product.bundleGroups as bundleGroup>
		<#if bundleGroup.showInProductDetails >
			<div class="bundle-group">
				<div class="bundle-group-label"><@print src=bundleGroup value="bundleGroup.label.str" /></div>
				<#if bundleGroup.type?string == 'SELECT' >
					<select>
						<#if bundleGroup.optional>
							<option value="">
								none
							</option>
						</#if>

						<#list bundleGroup.bundleItems as bundleItem>
							<#if bundleItem.selected >
                                <option value="${bundleItem.product.id?string}" selected>
									<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                </option>
							<#else>
                                <option value="${bundleItem.product.id?string}">
									<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                </option>
							</#if>

						</#list>
					</select>
				</#if>

				<#if bundleGroup.type?string == 'MULTISELECT' >
					<select multiple>
						<#list bundleGroup.bundleItems as bundleItem>
							<#if bundleItem.selected >
                                <option value="${bundleItem.product.id?string}" selected>
									<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                </option>
							<#else>
                                <option value="${bundleItem.product.id?string}">
									<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                </option>
							</#if>
						</#list>
					</select>
				</#if>


				<#if bundleGroup.type?string == 'RADIOBUTTON' >
					<#if bundleGroup.optional>
						<input type="radio" name="group_${bundleGroup.id}" value=""> none </br>
					</#if>
					<#list bundleGroup.bundleItems as bundleItem>
						<#if bundleItem.selected >
                            <input type="radio"  checked name="group_${bundleGroup.id}" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
						<#else>
                            <input type="radio" name="group_${bundleGroup.id}" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
						</#if>
					</#list>
				</#if>

				<#if bundleGroup.type?string == 'CHECKBOX' >
					<#list bundleGroup.bundleItems as bundleItem>
						<#if bundleItem.selected >
                            <input type="checkbox" checked value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
						<#else>
                            <input type="checkbox" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
						</#if>
					</#list>
				</#if>
			</div>
		</#if>
	</#list>
	<!-- Filled dynamically via JavaScript -->
</div>
