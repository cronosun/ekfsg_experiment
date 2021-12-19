package com.github.cronosun.demo.ekfsg

import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import org.springframework.stereotype.Component

@Component
@Theme(themeClass = Lumo::class, variant = "custom-style")
@JsModule("my-lumo-theme.js")
class AppShellConfiguration : AppShellConfigurator