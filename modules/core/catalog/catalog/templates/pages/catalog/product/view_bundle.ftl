<div class="bundle-config">

	<#list product.bundleGroups as bundleGroup>
		<#if bundleGroup.showInProductDetails >
			<div class="bundle-group row" group-type="${bundleGroup.type}">
				<div class="bundle-group-label"><@print src=bundleGroup value="bundleGroup.label.str" /></div>

		<#--		cms_product_variants-->

			<#if bundleGroup.type?string == 'LIST' >
				<#list bundleGroup.bundleItems as bundleItem>
					<div class="row">
						<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
						<#if bundleItem.product.variantMaster>
							<div class="col-xs-12 col-sm-7">
								<@cms_product_carousel product_id="${bundleItem.product.id}" />
                            </div>
                        	<div class="col-xs-12 col-sm-5">
								<@cms_product_variants product_id="${bundleItem.product.id}" />
                            </div>
						<#else>
						</#if>

					</div>
				</#list>
			</#if>
				<#if bundleGroup.type?string == 'SELECT' >
					<select class="bundle-item">
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
                                            <option bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" value="${variantProduct.id?string}" selected qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" />
                                            </option>
										<#else>
                                            <option bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" value="${variantProduct.id?string}" qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" />
                                            </option>
										</#if>
									</#if>
								</#list>
							<#else>
								<#if bundleItem.selected >
                                    <option bundle_option="${bundleItem.product.id?string}" value="${bundleItem.product.id?string}" selected qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                    </option>
								<#else>
                                    <option bundle_option="${bundleItem.product.id?string}" value="${bundleItem.product.id?string}" qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                    </option>
								</#if>
							</#if>

						</#list>
					</select>
				</#if>

				<#if bundleGroup.type?string == 'MULTISELECT' >
					<select multiple class="bundle-item">
						<#list bundleGroup.bundleItems as bundleItem>
							<#if bundleItem.product.variantMaster >
								<#list  bundleItem.product.variants as variantProduct>
									<#if variantProduct.validForSelling >
										<#if bundleItem.selected &&  bundleItem.defaultProductId?string == variantProduct.id?string>
                                            <option bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" value="${variantProduct.id?string}" selected qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" />
                                            </option>
										<#else>
                                            <option bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" value="${variantProduct.id?string}" qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" />
                                            </option>
										</#if>
									</#if>
								</#list>
							<#else>
								<#if bundleItem.selected >
                                    <option bundle_option="${bundleItem.product.id?string}" value="${bundleItem.product.id?string}" selected qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                    </option>
								<#else>
                                    <option bundle_option="${bundleItem.product.id?string}" value="${bundleItem.product.id?string}" qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                    </option>
								</#if>
							</#if>
						</#list>
					</select>
				</#if>


				<#if bundleGroup.type?string == 'RADIOBUTTON' >
					<#if bundleGroup.optional>
						<input type="radio" class="bundle-item" name="group_${bundleGroup.id}" value=""> none </br>
					</#if>
					<#list bundleGroup.bundleItems as bundleItem>
						<#if bundleItem.product.variantMaster >
							<#list  bundleItem.product.variants as variantProduct>
								<#if variantProduct.validForSelling >
									<#if bundleItem.selected &&  bundleItem.defaultProductId?string == variantProduct.id?string>
                                        <input type="radio" class="bundle-item" bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" qty="${bundleItem.quantity}" checked name="group_${bundleGroup.id}" value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									<#else>
                                        <input type="radio" class="bundle-item" bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" qty="${bundleItem.quantity}" name="group_${bundleGroup.id}" value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									</#if>
								</#if>
							</#list>
						<#else>
							<#if bundleItem.selected >
                                <input type="radio" class="bundle-item" bundle_option="${bundleItem.product.id?string}" qty="${bundleItem.quantity}" checked name="group_${bundleGroup.id}" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
							<#else>
                                <input type="radio" class="bundle-item" bundle_option="${bundleItem.product.id?string}" qty="${bundleItem.quantity}" name="group_${bundleGroup.id}" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
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
                                        <input type="checkbox" class="bundle-item" bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" qty="${bundleItem.quantity}" checked value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									<#else>
                                        <input type="checkbox" class="bundle-item" bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" qty="${bundleItem.quantity}" value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									</#if>
								</#if>
							</#list>
						<#else>
							<#if bundleItem.selected >
                                <input type="checkbox" class="bundle-item" bundle_option="${bundleItem.product.id?string}" qty="${bundleItem.quantity}" checked value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
							<#else>
                                <input type="checkbox" class="bundle-item" bundle_option="${bundleItem.product.id?string}" qty="${bundleItem.quantity}" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
							</#if>
						</#if>

					</#list>
				</#if>
			</div>
		</#if>
	</#list>
	<#list product.bundleGroups as bundleGroup>
		<#if bundleGroup.type?string != 'LIST' >
			<#list bundleGroup.bundleItems as bundleItem>
				<#if (bundleItem.withProductIds?? && bundleItem.withProductIds?size > 0) >
					<#list bundleItem.withProductIds as withProductId>
                    	<input type="hidden" name="condition" condition="${bundleItem.conditionType?string}" product="${bundleItem.product.id?string}" with="${withProductId?string}" />
					</#list>
				</#if>
			</#list>
		</#if>
	</#list>

	<!-- Filled dynamically via JavaScript -->
</div>
