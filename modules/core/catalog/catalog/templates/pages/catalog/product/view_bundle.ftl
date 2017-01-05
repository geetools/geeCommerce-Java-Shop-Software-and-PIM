<div class="bundle-config">

	<#list product.bundleGroups as bundleGroup>
		<#if bundleGroup.showInProductDetails >
			<div class="bundle-group" group-type="${bundleGroup.type}">
				<div class="bundle-group-label"><@print src=bundleGroup value="bundleGroup.label.str" /></div>

		<#--		cms_product_variants-->

			<#if bundleGroup.type?string == 'LIST' >
				<#list bundleGroup.bundleItems as bundleItem>
					<div>
						<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
						<#if bundleItem.product.variantMaster>
							<@cms_product_carousel product_id="${bundleItem.product.id}" />
							<@cms_product_variants product_id="${bundleItem.product.id}" />
						<#else>
						</#if>

					</div>
				</#list>
			</#if>
				<#if bundleGroup.type?string == 'SELECT' >
					<select>
						<#if bundleGroup.optional>
							<option value="">
								none
							</option>
						</#if>

						<#list bundleGroup.bundleItems as bundleItem>

							<#if bundleItem.product.variantMaster >
								<#list  bundleItem.product.variants as variantProduct>
									<#if variantProduct.validForSelling >
										<#if bundleItem.selected &&  bundleItem.defaultProductId?string == variantProduct.id?string>
                                            <option value="${variantProduct.id?string}" selected qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" />
                                            </option>
										<#else>
                                            <option value="${variantProduct.id?string}" qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" />
                                            </option>
										</#if>
									</#if>
								</#list>
							<#else>
								<#if bundleItem.selected >
                                    <option value="${bundleItem.product.id?string}" selected qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                    </option>
								<#else>
                                    <option value="${bundleItem.product.id?string}" qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                    </option>
								</#if>
							</#if>

						</#list>
					</select>
				</#if>

				<#if bundleGroup.type?string == 'MULTISELECT' >
					<select multiple>
						<#list bundleGroup.bundleItems as bundleItem>
							<#if bundleItem.product.variantMaster >
								<#list  bundleItem.product.variants as variantProduct>
									<#if variantProduct.validForSelling >
										<#if bundleItem.selected &&  bundleItem.defaultProductId?string == variantProduct.id?string>
                                            <option value="${variantProduct.id?string}" selected qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" />
                                            </option>
										<#else>
                                            <option value="${variantProduct.id?string}" qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" />
                                            </option>
										</#if>
									</#if>
								</#list>
							<#else>
								<#if bundleItem.selected >
                                    <option value="${bundleItem.product.id?string}" selected qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                    </option>
								<#else>
                                    <option value="${bundleItem.product.id?string}" qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                    </option>
								</#if>
							</#if>
						</#list>
					</select>
				</#if>


				<#if bundleGroup.type?string == 'RADIOBUTTON' >
					<#if bundleGroup.optional>
						<input type="radio" name="group_${bundleGroup.id}" value=""> none </br>
					</#if>
					<#list bundleGroup.bundleItems as bundleItem>
						<#if bundleItem.product.variantMaster >
							<#list  bundleItem.product.variants as variantProduct>
								<#if variantProduct.validForSelling >
									<#if bundleItem.selected &&  bundleItem.defaultProductId?string == variantProduct.id?string>
                                        <input type="radio"  qty="${bundleItem.quantity}" checked name="group_${bundleGroup.id}" value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									<#else>
                                        <input type="radio" qty="${bundleItem.quantity}" name="group_${bundleGroup.id}" value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									</#if>
								</#if>
							</#list>
						<#else>
							<#if bundleItem.selected >
                                <input type="radio" qty="${bundleItem.quantity}" checked name="group_${bundleGroup.id}" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
							<#else>
                                <input type="radio" qty="${bundleItem.quantity}" name="group_${bundleGroup.id}" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
							</#if>
						</#if>

					</#list>
				</#if>

				<#if bundleGroup.type?string == 'CHECKBOX' >
					<#list bundleGroup.bundleItems as bundleItem>

						<#if bundleItem.product.variantMaster >
							<#list  bundleItem.product.variants as variantProduct>
								<#if variantProduct.validForSelling >
									<#if bundleItem.selected &&  bundleItem.defaultProductId?string == variantProduct.id?string>
                                        <input type="checkbox" qty="${bundleItem.quantity}" checked value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									<#else>
                                        <input type="checkbox" qty="${bundleItem.quantity}" value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									</#if>
								</#if>
							</#list>
						<#else>
							<#if bundleItem.selected >
                                <input type="checkbox" qty="${bundleItem.quantity}" checked value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
							<#else>
                                <input type="checkbox" qty="${bundleItem.quantity}" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
							</#if>
						</#if>

					</#list>
				</#if>
			</div>
		</#if>
	</#list>
	<!-- Filled dynamically via JavaScript -->
</div>
