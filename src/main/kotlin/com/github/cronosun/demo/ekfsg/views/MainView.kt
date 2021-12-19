package com.github.cronosun.demo.ekfsg.views

import com.github.cronosun.demo.ekfsg.file.ui.UploadsPageView
import com.github.cronosun.demo.ekfsg.invoice.ui.ApproveInvoicePageView
import com.github.cronosun.demo.ekfsg.invoice.ui.ApproveRejectInvoicePageView
import com.github.cronosun.demo.ekfsg.invoice.ui.ExternalInvoicePageView
import com.github.cronosun.demo.ekfsg.mail.ui.SentMailsView
import com.github.cronosun.demo.ekfsg.start.StartView
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentUtil
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.component.tabs.TabsVariant
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.RouterLink

class MainView : AppLayout() {
    private var menu: Lazy<Tabs> = lazy { createMenu() }
    private var viewTitle: Text = Text("")

    init {
        // Use the drawer for the menu
        primarySection = Section.DRAWER

        // Make the nav bar a header
        addToNavbar(true, createHeaderContent())

        // Put the menu in the drawer
        addToDrawer(createDrawerContent(menu.value))
    }

    private fun createHeaderContent(): Component {
        val layout = HorizontalLayout()

        // Configure styling for the header
        layout.setId("header")
        layout.themeList["dark"] = true
        layout.setWidthFull()
        layout.isSpacing = false
        layout.alignItems = FlexComponent.Alignment.CENTER

        // Have the drawer toggle button on the left
        layout.add(DrawerToggle())

        // Placeholder for the title of the current view.
        // The title will be set after navigation.
        layout.add(viewTitle)

        return layout
    }

    private fun createDrawerContent(menu: Tabs): Component {
        val layout = VerticalLayout()

        // Configure styling for the drawer
        layout.setSizeFull()
        layout.isPadding = false
        layout.isSpacing = false
        layout.themeList["spacing-s"] = true
        layout.alignItems = FlexComponent.Alignment.STRETCH

        // Have a drawer header with an application logo
        val logoLayout = VerticalLayout()
        logoLayout.setId("logo")
        logoLayout.alignItems = FlexComponent.Alignment.CENTER
        val image = Image("images/logo-be.svg", "My Project logo")
        image.setSizeFull()
        logoLayout.add(image)
        logoLayout.add(H4("eKFSG"))

        // Display the logo and the menu in the drawer
        layout.add(logoLayout, menu)
        return layout
    }

    private fun createMenu(): Tabs {
        val tabs = Tabs()
        tabs.orientation = Tabs.Orientation.VERTICAL
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL)
        tabs.setId("tabs")
        tabs.add(*createMenuItems())
        return tabs
    }

    private fun createMenuItems(): Array<Component> {
        return arrayOf(
            createTab("Startseite", StartView::class.java),
            createTab("Rechnung Einreichen", ExternalInvoicePageView::class.java),
            createTab("Genehmigen & Ablehnen von Rechnungen", ApproveRejectInvoicePageView::class.java),
            createTab("Genehmigte Rechnungen", ApproveInvoicePageView::class.java),
            createTab("Mail Postausgang", SentMailsView::class.java),
            createTab("Dokmente", UploadsPageView::class.java),
        )
    }

    private fun createTab(text: String, navigationTarget: Class<out Component>): Tab {
        val tab = Tab()
        tab.add(RouterLink(text, navigationTarget))
        ComponentUtil.setData(tab, Class::class.java, navigationTarget)
        return tab
    }

    override fun afterNavigation() {
        super.afterNavigation()

        // Select the tab corresponding to currently shown view
        val maybeTab = getTabForComponent(content)
        if (maybeTab != null) {
            menu.value.selectedTab = maybeTab
        }

        // Set the view title in the header
        viewTitle.text = getCurrentPageTitle()
    }

    private fun getTabForComponent(component: Component): Tab? {
        return menu.value.children.filter { tab: Component ->
            ComponentUtil.getData(
                tab,
                Class::class.java
            ) == component.javaClass
        }
            .findFirst().map { obj: Component? ->
                Tab::class.java.cast(
                    obj
                )
            }.orElse(null)
    }

    private fun getCurrentPageTitle(): String {
        val maybeAnnotation = content.javaClass.getAnnotation(PageTitle::class.java)
        return maybeAnnotation?.value ?: "<NO_PAGE_TITLE>"
    }
}