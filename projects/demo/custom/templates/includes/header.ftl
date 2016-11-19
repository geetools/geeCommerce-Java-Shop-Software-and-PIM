<#--

	<@js type="module" fetch="api" /><br/>
	<@js type="module" fetch="main" /><br/>
	<@js type="module" path="test/test" /><br/>
	<@js path="test/test2" /><br/>
	<@js type="page" /><br/>
-->

    <header>
        <div id="brand-bar" class="container">
            <div class="row">
                <div id="brand-bar-logo" class="col-sm-4 col-xs-12">
                    <a class="navbar-brand" href="/"><img src="<@skin path="images/layout/header/logo.png" />" /><span>CommerceBoard Suite</span></a>
                </div>
                
                <div id="col-search" class="col-sm-4 col-xs-12 hidden-xs hidden-xs-down">
                    <form role="search" action="/catalog/search/query" method="get" >
                        <div class="input-group">
                            <input type="text" name="q" placeholder="Suchbegriff / Artikelnr. eingeben" class="form-control" />
                            <span class="input-group-btn">
                                <button class="btn btn-default" type="submit">Suchen</button>
                            </span>
                        </div>
                    </form>
                </div>

                <div class="col-sm-4 col-xs-12">
                    <ul class="nav navbar-nav">
                        <li id="nav-item-search" class="nav-item active hidden-tablet"><a href="#" target="_blank"><i class="glyphicon glyphicon-search fa fa-search" aria-hidden="true"></i><small>Suchen</small></a>
                        </li>
                        <#--<li><a href="#" target="_blank"><i class="glyphicon glyphicon-user" aria-hidden="true"></i><small>Benutzer</small></a>-->
                        <#--</li>-->
                        <li class="nav-item">
                            <@customer_profile/>
                        </li>

                        <li class="nav-item"><a href="#" target="_blank"><i class="glyphicon glyphicon-star fa fa-star" aria-hidden="true"></i><small>Wunschliste</small></a>
                        </li>

                        <li class="nav-item">
                            <@mini_cart/>
                        </li>
                    </ul>
                </div>
                
                <div id="col-search" class="col-sm-4 col-xs-12 hidden-sm hidden-md hidden-lg hidden-sm-up">
                    <form role="search" action="/catalog/search/query" method="get" >
                        <div class="input-group">
                            <input type="text" name="q" placeholder="Suchbegriff / Artikelnr. eingeben" class="form-control" />
                            <span class="input-group-btn">
                                <button class="btn btn-default" type="submit">Suchen</button>
                              </span>
                        </div>
                    </form>
                </div>
                
            </div>
        </div>

        <div id="top-nav-bar" class="x-whl-border">
            <div class="container">
                <div class="row">
                    <div class="col-sm-12 hidden-phone hidden-phone-h hidden-xs-down">
                        <ul class="nav navbar-nav">
                            <li class="nav-item <@nav_is_active item="/sofas/">active</@nav_is_active>"><a href="/sofas/">Sofas</a>
                            </li>
                            <li class="nav-item <@nav_is_active item="/betten/"></@nav_is_active>"><a href="/betten/">Betten</a>
                            </li>
                            <li class="nav-item <@nav_is_active item="/regale/">active</@nav_is_active>"><a href="/regale/">Regale</a>
                            </li>
                            <li class="nav-item <@nav_is_active item="/schraenke/">active</@nav_is_active>"><a href="/schraenke/">Schränke</a>
                            </li>
                            <li class="nav-item <@nav_is_active item="/tische/">active</@nav_is_active>"><a href="/tische/">Tische</a>
                            </li>
                            <li class="nav-item <@nav_is_active item="/stuehle/">active</@nav_is_active>"><a href="/stuehle/">Stühle</a>
                            </li>
                            <li class="nav-item <@nav_is_active item="/teppiche/">active</@nav_is_active>"><a href="/teppiche/">Teppiche</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

    </header>
