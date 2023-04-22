package com.chat.application.views;


import com.chat.application.components.appnav.AppNav;
import com.chat.application.components.appnav.AppNavItem;
import com.chat.application.constant.ImageConst;
import com.chat.application.util.ImageUtil;
import com.chat.application.util.RequestUtil;
import com.chat.application.views.chat.*;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        getStyle().set("background-color","white");
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");
        viewTitle = new H2("AI Chat");
        viewTitle.addClassName("header-title");

        Span span = new Span(getQuotaPercent()
                ,new Html("<span>&nbsp;&nbsp;&nbsp;</span>")
                ,ImageUtil.getAvatar(ImageConst.LOGIN)
                ,new Html("<span>&nbsp;&nbsp;&nbsp;</span>")
                ,new Text(RequestUtil.getRequestIp()));
        span.addClassName("userLogo");

        addToNavbar(false, toggle, viewTitle,span);
    }

    private void addDrawerContent() {
        H1 appName = new H1("AI Chat");
        appName.addClassName("header-title");
        Header header = new Header(appName);
        header.getElement().setAttribute("theme",Lumo.DARK);
        Scroller scroller = new Scroller(createNavigation());
        scroller.getElement().setAttribute("theme",Lumo.DARK);
        addToDrawer(header, scroller ,createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();
        nav.addItem(new AppNavItem("Room - 3.5A", ChatRoom1.class, LineAwesomeIcon.GLOBE_SOLID.create()));
        nav.addItem(new AppNavItem("Room - 3.5B", ChatRoom2.class, LineAwesomeIcon.HISTORY_SOLID.create()));
        return nav;
    }

    private Footer createFooter() {
        Footer footer = new Footer(new Text("The information provided is for reference only."));
        footer.getElement().setAttribute("theme",Lumo.DARK);
        return footer;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());

    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private Div getQuotaPercent(){

        Div quotaLevel = new Div();
        quotaLevel.setId("quota-level");
        quotaLevel.addClassName("battery-level");

        Div quota = new Div();
        quota.addClassName("battery");
        quota.add(quotaLevel);

        return quota;
    }
}
